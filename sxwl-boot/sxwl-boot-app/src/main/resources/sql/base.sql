-- =====================================================================
-- 底座系统数据库设计约定（全表通用，修改任何表前请先阅读）
-- =====================================================================
-- 1. 软删除与唯一约束复用
--    - 所有 unique index 均带 WHERE delete_flag = 0 条件
--    - 软删后可重建同名/同编码记录，旧记录仍保留在表中（delete_flag=1）
--    - 引用关系跟 id 走，不跟 code 走：旧 role_id 与新 role_id 不同，即使 role_code 相同
--    - 应用层如需"恢复已删除记录"而非"新建同名记录"，需自行判断，DDL 层不区分
--    - 日志表（sys_log_info、pla_log_info）保留 delete_flag，正常情况下禁止删除，
--      字段存在的目的是留痕——若发生删除动作，delete_flag=1 配合审计字段可追溯
--
-- 2. 审计字段
--    - create_by/create_org/create_time：创建时写入，不再变更
--    - update_by/update_time：每次更新写入
--    - create_org 指向创建人所属组织，组织删除后历史记录可能指向已删除组织，
--      应用层查询组织名时需 LEFT JOIN 兼容，显示"已删除组织"
--
-- 3. ancestors 祖先路径
--    - 逗号分隔的 ID 链，如 0,1,2，查询子树用 LIKE '0,1,%'
--    - 前提：ID 不会产生前缀冲突（当前用 int8 长整型，风险极低）
--    - 若改用雪花 ID（19位），ancestors 字段会变长，LIKE 性能需重新评估
--
-- 4. 主键 ID 生成策略
--    - 所有表主键 id 为 int8，由后端雪花算法生成，DDL 不设默认值
--    - 雪花 ID 为 19 位长整型，ancestors 字段需评估 LIKE 性能（见第 3 条）
--
-- 5. 敏感数据加密（全面国密化）
--    - id_card（身份证号）属于敏感个人信息，应用层必须加密存储（国密 SM4 CBC/CTR），禁止明文落库
--    - password（密码）使用 SM3 + 随机盐值 + 多轮迭代哈希存储，禁止明文/可逆加密
--    - phone（手机号）视合规要求评估是否加密，加密后需调整登录查询逻辑
--    - 接口签名/防篡改使用 SM2 非对称签名或 SM3 HMAC
--    - 传输层加密使用国密 TLS（SM2 协商密钥 + SM4 加密报文）
--
-- 6. 时间类型与引用完整性
--    - 所有时间字段使用 timestamp（无时区），应用层统一用 UTC 处理，数据库不存时区信息
--    - 全表不使用外键约束（FOREIGN KEY），引用完整性由应用层维护
--      · 原因：外键影响写入性能、增加分库分表难度、限制数据迁移灵活性
--      · 应用层需保证关联 ID 的有效性，删除关联记录时应用层处理级联
--
-- 说明：本版本不含多租户（tenant_id）和乐观锁（version）字段
--       - 无多租户：所有数据全局共享，唯一约束为全局唯一，无 pla_tenant_info 表
--       - 无乐观锁：并发更新由应用层加锁或 CAS 处理
-- =====================================================================


-- =====================================================================
-- 系统用户信息表（B端用户：企业员工，登录后台管理系统）
-- =====================================================================

CREATE TABLE "sys_user_info" (
  "id" int8 NOT NULL,
  "username" varchar(64) NOT NULL,
  "password" varchar(256) NOT NULL,
  "real_name" varchar(64) NOT NULL,
  "nickname" varchar(64),
  "phone" varchar(20) NOT NULL,
  "email" varchar(128),
  "avatar" text,
  "gender" int2 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "error_count" int2 NOT NULL DEFAULT 0,
  "lock_time" timestamp,
  "last_login_time" timestamp,
  "last_login_ip" varchar(64),
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_user_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_info"."username" IS '登录账号，全局唯一';
COMMENT ON COLUMN "sys_user_info"."password" IS '密码哈希（SM3 + 随机盐值 + 多轮迭代，禁止明文/可逆加密）';
COMMENT ON COLUMN "sys_user_info"."real_name" IS '真实姓名';
COMMENT ON COLUMN "sys_user_info"."nickname" IS '昵称';
COMMENT ON COLUMN "sys_user_info"."phone" IS '手机号（用于手机验证码登录、找回密码），全局唯一';
COMMENT ON COLUMN "sys_user_info"."email" IS '邮箱';
COMMENT ON COLUMN "sys_user_info"."avatar" IS '头像URL（RustFS访问地址）';
COMMENT ON COLUMN "sys_user_info"."gender" IS '性别：0=未知 1=男 2=女';
COMMENT ON COLUMN "sys_user_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_user_info"."error_count" IS '密码错误次数（连续错误5次锁定）';
COMMENT ON COLUMN "sys_user_info"."lock_time" IS '锁定时间（锁定30分钟后自动解锁）';
COMMENT ON COLUMN "sys_user_info"."last_login_time" IS '最后登录时间';
COMMENT ON COLUMN "sys_user_info"."last_login_ip" IS '最后登录IP';
COMMENT ON COLUMN "sys_user_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_user_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_user_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_user_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_user_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_info" IS '系统用户信息表（B端用户，企业员工）';

CREATE UNIQUE INDEX "uk_sys_user_username" ON "sys_user_info" ("username") WHERE "delete_flag" = 0;
CREATE UNIQUE INDEX "uk_sys_user_phone" ON "sys_user_info" ("phone") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_user_status" ON "sys_user_info" ("status");
CREATE INDEX "idx_sys_user_create_time" ON "sys_user_info" ("create_time");

-- =====================================================================
-- 组织信息表（业务字段已补全）
-- =====================================================================

CREATE TABLE "sys_organization_info" (
  "id" int8 NOT NULL,
  "org_code" varchar(4) NOT NULL,
  "org_name" varchar(64) NOT NULL,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "ancestors" varchar(256) NOT NULL DEFAULT '0',
  "org_level" int2 NOT NULL,
  "org_type" varchar(4),
  "leader_id" int8,
  "phone" varchar(20),
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_organization_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_organization_info"."org_code" IS '组织编码，4位数字/字母，全局唯一';
COMMENT ON COLUMN "sys_organization_info"."org_name" IS '组织名称，如：总公司、技术部、后端组';
COMMENT ON COLUMN "sys_organization_info"."parent_id" IS '父组织ID，根组织为0';
COMMENT ON COLUMN "sys_organization_info"."ancestors" IS '祖先路径，逗号分隔的ID链，如 0,1,2，查询子树用 LIKE ''0,1,%''；前提：ID无前缀冲突（int8长整型，风险极低），改用雪花ID需重新评估LIKE性能';
COMMENT ON COLUMN "sys_organization_info"."org_level" IS '层级：1=公司 2=部门 3=小组';
COMMENT ON COLUMN "sys_organization_info"."org_type" IS '组织类型（关联字典detail_value），如 01=公司 02=部门';
COMMENT ON COLUMN "sys_organization_info"."leader_id" IS '负责人ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_organization_info"."phone" IS '组织联系电话';
COMMENT ON COLUMN "sys_organization_info"."sort" IS '排序号，控制组织树展示顺序';
COMMENT ON COLUMN "sys_organization_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_organization_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_organization_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_organization_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_organization_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_organization_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_organization_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_organization_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_organization_info" IS '组织信息表';

CREATE UNIQUE INDEX "uk_sys_org_code" ON "sys_organization_info" ("org_code") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_org_parent_id" ON "sys_organization_info" ("parent_id");
CREATE INDEX "idx_sys_org_ancestors" ON "sys_organization_info" ("ancestors");
CREATE INDEX "idx_sys_org_leader_id" ON "sys_organization_info" ("leader_id");
CREATE INDEX "idx_sys_org_status" ON "sys_organization_info" ("status");

CREATE TABLE "sys_role_info" (
  "id" int8 NOT NULL,
  "role_code" varchar(32) NOT NULL,
  "role_name" varchar(64) NOT NULL,
  "data_scope" int2 NOT NULL DEFAULT 4,
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_role_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_role_info"."role_code" IS '角色编码，英文，如 admin、user，全局唯一';
COMMENT ON COLUMN "sys_role_info"."role_name" IS '角色名称，如：管理员、普通员工';
COMMENT ON COLUMN "sys_role_info"."data_scope" IS '数据权限范围：1=全部 2=本组织 3=本组织及下级 4=仅本人 5=自定义';
COMMENT ON COLUMN "sys_role_info"."sort" IS '排序号，控制角色展示顺序';
COMMENT ON COLUMN "sys_role_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_role_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_role_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_role_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_role_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_role_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_role_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_role_info" IS '角色信息表';

CREATE UNIQUE INDEX "uk_sys_role_code" ON "sys_role_info" ("role_code") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_role_status" ON "sys_role_info" ("status");
CREATE INDEX "idx_sys_role_data_scope" ON "sys_role_info" ("data_scope");

CREATE TABLE "sys_menu_info" (
  "id" int8 NOT NULL,
  "menu_name" varchar(64) NOT NULL,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "ancestors" varchar(256) NOT NULL DEFAULT '0',
  "menu_type" int2 NOT NULL,
  "path" varchar(128),
  "component" varchar(128),
  "perms" varchar(64),
  "icon" varchar(64),
  "is_frame" int2 NOT NULL DEFAULT 0,
  "is_cache" int2 NOT NULL DEFAULT 0,
  "sort" int4 NOT NULL DEFAULT 0,
  "visible" int2 NOT NULL DEFAULT 1,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_menu_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_menu_info"."menu_name" IS '菜单名称，如：用户管理、新增用户';
COMMENT ON COLUMN "sys_menu_info"."parent_id" IS '父菜单ID，根菜单为0';
COMMENT ON COLUMN "sys_menu_info"."ancestors" IS '祖先路径，逗号分隔的ID链，如 0,1,2，查询子树用 LIKE ''0,1,%''；前提：ID无前缀冲突（int8长整型，风险极低），改用雪花ID需重新评估LIKE性能';
COMMENT ON COLUMN "sys_menu_info"."menu_type" IS '类型：1=目录 2=菜单 3=按钮';
COMMENT ON COLUMN "sys_menu_info"."path" IS '路由路径，如 user（目录/菜单用，按钮为空）';
COMMENT ON COLUMN "sys_menu_info"."component" IS '前端组件路径（PascalCase），如 System/User（菜单用，目录/按钮为空）';
COMMENT ON COLUMN "sys_menu_info"."perms" IS '权限标识，格式 模块:资源:操作，如 system:user:list（按钮用，目录/菜单可空）';
COMMENT ON COLUMN "sys_menu_info"."icon" IS '菜单图标，如 user';
COMMENT ON COLUMN "sys_menu_info"."is_frame" IS '是否外链：0=内嵌 1=外链（外链时path存完整URL）';
COMMENT ON COLUMN "sys_menu_info"."is_cache" IS '是否缓存：0=不缓存 1=缓存（前端keep-alive，列表页建议缓存）';
COMMENT ON COLUMN "sys_menu_info"."sort" IS '排序号，控制菜单展示顺序';
COMMENT ON COLUMN "sys_menu_info"."visible" IS '是否可见：0=隐藏 1=显示';
COMMENT ON COLUMN "sys_menu_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_menu_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_menu_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_menu_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_menu_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_menu_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_menu_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_menu_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_menu_info" IS '菜单信息表';

CREATE INDEX "idx_sys_menu_parent_id" ON "sys_menu_info" ("parent_id");
CREATE INDEX "idx_sys_menu_ancestors" ON "sys_menu_info" ("ancestors");
CREATE INDEX "idx_sys_menu_status" ON "sys_menu_info" ("status");
CREATE UNIQUE INDEX "uk_sys_menu_perms" ON "sys_menu_info" ("perms") WHERE "delete_flag" = 0 AND "perms" IS NOT NULL;

CREATE TABLE "sys_position_info" (
  "id" int8 NOT NULL,
  "position_code" varchar(32) NOT NULL,
  "position_name" varchar(64) NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_position_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_position_info"."position_code" IS '岗位编码，英文，如 cto、backend_engineer，全局唯一';
COMMENT ON COLUMN "sys_position_info"."position_name" IS '岗位名称，如：技术总监、后端工程师';
COMMENT ON COLUMN "sys_position_info"."sort" IS '排序号，控制岗位展示顺序';
COMMENT ON COLUMN "sys_position_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_position_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_position_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_position_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_position_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_position_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_position_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_position_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_position_info" IS '岗位信息表';

CREATE UNIQUE INDEX "uk_sys_position_code" ON "sys_position_info" ("position_code") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_position_status" ON "sys_position_info" ("status");

-- =====================================================================
-- 字典主表 + 明细表（业务字段已补全）
-- =====================================================================

CREATE TABLE "sys_dict_info" (
  "id" int8 NOT NULL,
  "dict_code" varchar(2) NOT NULL,
  "dict_name" varchar(64) NOT NULL,
  "description" varchar(200),
  "status" int2 NOT NULL DEFAULT 1,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_dict_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_dict_info"."dict_code" IS '字典编码，两位数字 01-99，全局唯一';
COMMENT ON COLUMN "sys_dict_info"."dict_name" IS '字典名称，如：性别、用户状态';
COMMENT ON COLUMN "sys_dict_info"."description" IS '描述说明，字典用途';
COMMENT ON COLUMN "sys_dict_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_dict_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_dict_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_dict_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dict_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_dict_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dict_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_dict_info" IS '字典信息表';

CREATE UNIQUE INDEX "uk_sys_dict_info_code" ON "sys_dict_info" ("dict_code") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_dict_info_status" ON "sys_dict_info" ("status");


CREATE TABLE "sys_dict_detail_info" (
  "id" int8 NOT NULL,
  "dict_id" int8 NOT NULL,
  "detail_value" varchar(4) NOT NULL,
  "detail_label" varchar(128) NOT NULL,
  "description" varchar(200),
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "is_default" int2 NOT NULL DEFAULT 0,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_dict_detail_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_dict_detail_info"."dict_id" IS '所属字典ID（关联 sys_dict_info.id）';
COMMENT ON COLUMN "sys_dict_detail_info"."detail_value" IS '字典项值，4位数字，前2位为所属字典code，后2位为序号，如 0101，全局唯一';
COMMENT ON COLUMN "sys_dict_detail_info"."detail_label" IS '字典项标签，前端显示文本，如：男、女';
COMMENT ON COLUMN "sys_dict_detail_info"."description" IS '描述说明，补充解释明细项含义';
COMMENT ON COLUMN "sys_dict_detail_info"."sort" IS '排序号，控制下拉框顺序';
COMMENT ON COLUMN "sys_dict_detail_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_dict_detail_info"."is_default" IS '是否默认选中：0=否 1=是';
COMMENT ON COLUMN "sys_dict_detail_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_dict_detail_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_dict_detail_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dict_detail_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_dict_detail_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dict_detail_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_dict_detail_info" IS '字典明细信息表';

CREATE UNIQUE INDEX "uk_sys_dict_detail_value" ON "sys_dict_detail_info" ("detail_value") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_dict_detail_dict_id" ON "sys_dict_detail_info" ("dict_id");
CREATE UNIQUE INDEX "uk_sys_dict_detail_default" ON "sys_dict_detail_info" ("dict_id") WHERE "is_default" = 1 AND "delete_flag" = 0;

CREATE TABLE "sys_log_info" (
  "id" int8 NOT NULL,
  "log_type" int2 NOT NULL,
  "title" varchar(64) NOT NULL,
  "description" varchar(500),
  "method" varchar(128),
  "request_url" varchar(256),
  "request_method" varchar(10),
  "request_param" text,
  "response_result" text,
  "operate_ip" varchar(64),
  "operate_location" varchar(128),
  "user_id" int8,
  "user_name" varchar(64),
  "execute_time" int8,
  "error_msg" text,
  "status" int2 NOT NULL DEFAULT 1,
  "trace_id" varchar(64),
  "user_agent" text,
  "browser" varchar(32),
  "os" varchar(32),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_log_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_log_info"."log_type" IS '日志类型：1=登录 2=操作 3=异常 4=安全';
COMMENT ON COLUMN "sys_log_info"."title" IS '模块标题，如：用户管理';
COMMENT ON COLUMN "sys_log_info"."description" IS '操作描述，如：删除用户[zhangsan]';
COMMENT ON COLUMN "sys_log_info"."method" IS '调用方法，如 SysUserController.delete()';
COMMENT ON COLUMN "sys_log_info"."request_url" IS '请求URL，如 /sxwl-api/sys/user/1';
COMMENT ON COLUMN "sys_log_info"."request_method" IS 'HTTP方法：GET/POST/PUT/DELETE';
COMMENT ON COLUMN "sys_log_info"."request_param" IS '请求参数（JSON，应用层截断至2000字符）';
COMMENT ON COLUMN "sys_log_info"."response_result" IS '响应结果（JSON，应用层截断至2000字符）';
COMMENT ON COLUMN "sys_log_info"."operate_ip" IS '操作人IP';
COMMENT ON COLUMN "sys_log_info"."operate_location" IS '操作地点，如：北京市（IP反查）';
COMMENT ON COLUMN "sys_log_info"."user_id" IS '操作人ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_log_info"."user_name" IS '操作人账号（冗余，便于查询）';
COMMENT ON COLUMN "sys_log_info"."execute_time" IS '执行耗时（毫秒）';
COMMENT ON COLUMN "sys_log_info"."error_msg" IS '错误信息（异常日志用）';
COMMENT ON COLUMN "sys_log_info"."status" IS '操作状态：0=失败 1=成功';
COMMENT ON COLUMN "sys_log_info"."trace_id" IS '链路追踪ID（分布式场景串联一次请求的多条日志）';
COMMENT ON COLUMN "sys_log_info"."user_agent" IS '原始User-Agent字符串';
COMMENT ON COLUMN "sys_log_info"."browser" IS '浏览器，如 Chrome/Edge/Firefox';
COMMENT ON COLUMN "sys_log_info"."os" IS '操作系统，如 Windows/macOS/Android/iOS';
COMMENT ON COLUMN "sys_log_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_log_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_log_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_log_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_log_info" IS '系统日志信息表';

CREATE INDEX "idx_sys_log_type" ON "sys_log_info" ("log_type");
CREATE INDEX "idx_sys_log_user_id" ON "sys_log_info" ("user_id");
CREATE INDEX "idx_sys_log_status" ON "sys_log_info" ("status");
CREATE INDEX "idx_sys_log_create_time" ON "sys_log_info" ("create_time");
CREATE INDEX "idx_sys_log_trace_id" ON "sys_log_info" ("trace_id");

CREATE TABLE "sys_file_info" (
  "id" int8 NOT NULL,
  "file_name" varchar(128) NOT NULL,
  "object_key" varchar(256) NOT NULL,
  "file_url" text,
  "file_size" int8 NOT NULL,
  "file_type" varchar(64) NOT NULL,
  "file_suffix" varchar(16),
  "bucket_name" varchar(64) NOT NULL,
  "md5" varchar(64),
  "business_type" varchar(32),
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_file_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_file_info"."file_name" IS '原始文件名，如：头像.png';
COMMENT ON COLUMN "sys_file_info"."object_key" IS 'RustFS对象键，如 2026/07/02/uuid.png';
COMMENT ON COLUMN "sys_file_info"."file_url" IS '访问URL（冗余，便于前端直接使用）';
COMMENT ON COLUMN "sys_file_info"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "sys_file_info"."file_type" IS '文件MIME类型，如 image/png';
COMMENT ON COLUMN "sys_file_info"."file_suffix" IS '文件后缀，如 png';
COMMENT ON COLUMN "sys_file_info"."bucket_name" IS 'RustFS bucket名，如 sys-file';
COMMENT ON COLUMN "sys_file_info"."md5" IS '文件MD5（秒传/去重用）';
COMMENT ON COLUMN "sys_file_info"."business_type" IS '业务类型，如 avatar、attachment';
COMMENT ON COLUMN "sys_file_info"."status" IS '状态：0=临时 1=正常 2=已删除';
COMMENT ON COLUMN "sys_file_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_file_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_file_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_file_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_file_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_file_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_file_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_file_info" IS '系统文件信息表';

CREATE INDEX "idx_sys_file_md5" ON "sys_file_info" ("file_size", "md5");
CREATE INDEX "idx_sys_file_business_type" ON "sys_file_info" ("business_type");
CREATE INDEX "idx_sys_file_status" ON "sys_file_info" ("status");
CREATE INDEX "idx_sys_file_create_by" ON "sys_file_info" ("create_by");
CREATE INDEX "idx_sys_file_create_time" ON "sys_file_info" ("create_time");


-- =====================================================================
-- 关系表（用户角色 / 用户组织 / 用户岗位 / 角色菜单）
-- 关系表特性：无 update_by/update_time（删除重建，无修改语义）
-- =====================================================================

CREATE TABLE "sys_user_role_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_user_role_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_role_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_user_role_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "sys_user_role_info"."create_by" IS '创建人标识（谁分配的角色）';
COMMENT ON COLUMN "sys_user_role_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_role_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_role_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_role_info" IS '用户角色信息表';

CREATE UNIQUE INDEX "uk_sys_user_role" ON "sys_user_role_info" ("user_id", "role_id") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_user_role_role" ON "sys_user_role_info" ("role_id");


CREATE TABLE "sys_user_organization_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "org_id" int8 NOT NULL,
  "is_main" int2 NOT NULL DEFAULT 0,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_user_organization_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_organization_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_user_organization_info"."org_id" IS '组织ID（关联 sys_organization_info.id）';
COMMENT ON COLUMN "sys_user_organization_info"."is_main" IS '是否主组织：0=否 1=是（每个用户仅一个主组织）';
COMMENT ON COLUMN "sys_user_organization_info"."create_by" IS '创建人标识（谁分配的组织）';
COMMENT ON COLUMN "sys_user_organization_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_organization_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_organization_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_organization_info" IS '用户组织信息表';

CREATE UNIQUE INDEX "uk_sys_user_org" ON "sys_user_organization_info" ("user_id", "org_id") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_user_org_org" ON "sys_user_organization_info" ("org_id");
CREATE UNIQUE INDEX "uk_sys_user_org_main" ON "sys_user_organization_info" ("user_id") WHERE "is_main" = 1 AND "delete_flag" = 0;


CREATE TABLE "sys_user_position_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "position_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_user_position_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_position_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_user_position_info"."position_id" IS '岗位ID（关联 sys_position_info.id）';
COMMENT ON COLUMN "sys_user_position_info"."create_by" IS '创建人标识（谁分配的岗位）';
COMMENT ON COLUMN "sys_user_position_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_position_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_position_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_position_info" IS '用户岗位信息表';

CREATE UNIQUE INDEX "uk_sys_user_position" ON "sys_user_position_info" ("user_id", "position_id") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_user_position_position" ON "sys_user_position_info" ("position_id");


CREATE TABLE "sys_role_menu_info" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "menu_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_role_menu_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_role_menu_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "sys_role_menu_info"."menu_id" IS '菜单ID（关联 sys_menu_info.id）';
COMMENT ON COLUMN "sys_role_menu_info"."create_by" IS '创建人标识（谁分配的菜单）';
COMMENT ON COLUMN "sys_role_menu_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_role_menu_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role_menu_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_role_menu_info" IS '角色菜单信息表';

CREATE UNIQUE INDEX "uk_sys_role_menu" ON "sys_role_menu_info" ("role_id", "menu_id") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_role_menu_menu" ON "sys_role_menu_info" ("menu_id");


-- =====================================================================
-- 角色数据权限表（配合 sys_role_info.data_scope=5 自定义数据权限使用）
-- 关系表特性：无 update_by/update_time
-- =====================================================================

CREATE TABLE "sys_role_data_scope_info" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "org_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_role_data_scope_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_role_data_scope_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "sys_role_data_scope_info"."org_id" IS '授权可见的组织ID（关联 sys_organization_info.id）';
COMMENT ON COLUMN "sys_role_data_scope_info"."create_by" IS '创建人标识（谁配置的数据权限）';
COMMENT ON COLUMN "sys_role_data_scope_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_role_data_scope_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role_data_scope_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_role_data_scope_info" IS '角色数据权限信息表（仅 sys_role_info.data_scope=5 时生效）';

CREATE UNIQUE INDEX "uk_sys_role_data_scope" ON "sys_role_data_scope_info" ("role_id", "org_id") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_role_data_scope_org" ON "sys_role_data_scope_info" ("org_id");


-- =====================================================================
-- 平台用户信息表（C端用户：App/H5/小程序的注册用户，不登录后台）
-- =====================================================================

CREATE TABLE "pla_user_info" (
  "id" int8 NOT NULL,
  "phone" varchar(20) NOT NULL,
  "password" varchar(256),
  "nickname" varchar(64) NOT NULL,
  "avatar" text,
  "gender" int2 NOT NULL DEFAULT 0,
  "email" varchar(128),
  "real_name" varchar(64),
  "id_card" varchar(128),
  "birthday" date,
  "wx_open_id" varchar(64),
  "register_source" varchar(32) NOT NULL,
  "login_type" varchar(32),
  "status" int2 NOT NULL DEFAULT 1,
  "error_count" int2 NOT NULL DEFAULT 0,
  "lock_time" timestamp,
  "last_login_time" timestamp,
  "last_login_ip" varchar(64),
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "pla_user_info"."id" IS '唯一标识';
COMMENT ON COLUMN "pla_user_info"."phone" IS '手机号（主登录方式，全局唯一，见 uk_pla_user_phone）';
COMMENT ON COLUMN "pla_user_info"."password" IS '密码哈希（SM3 + 随机盐值 + 多轮迭代，禁止明文/可逆加密；微信登录可空）';
COMMENT ON COLUMN "pla_user_info"."nickname" IS '昵称（C端用户核心展示名）';
COMMENT ON COLUMN "pla_user_info"."avatar" IS '头像URL（RustFS访问地址）';
COMMENT ON COLUMN "pla_user_info"."gender" IS '性别：0=未知 1=男 2=女';
COMMENT ON COLUMN "pla_user_info"."email" IS '邮箱';
COMMENT ON COLUMN "pla_user_info"."real_name" IS '真实姓名（实名认证后填写）';
COMMENT ON COLUMN "pla_user_info"."id_card" IS '身份证号（实名认证后填写，应用层必须加密存储 国密SM4，禁止明文落库；密文+IV Base64后约64字符，varchar(128)预留空间）';
COMMENT ON COLUMN "pla_user_info"."birthday" IS '生日（用于生日营销）';
COMMENT ON COLUMN "pla_user_info"."wx_open_id" IS '微信OpenID（微信登录用），全局唯一';
COMMENT ON COLUMN "pla_user_info"."register_source" IS '注册来源：wechat=微信注册 phone=手机号注册';
COMMENT ON COLUMN "pla_user_info"."login_type" IS '最近登录方式：phone_password=手机密码 phone_sms=手机验证码 wechat=微信';
COMMENT ON COLUMN "pla_user_info"."status" IS '状态：0=禁用 1=正常';
COMMENT ON COLUMN "pla_user_info"."error_count" IS '密码错误次数（连续错误5次锁定）';
COMMENT ON COLUMN "pla_user_info"."lock_time" IS '锁定时间（锁定30分钟后自动解锁）';
COMMENT ON COLUMN "pla_user_info"."last_login_time" IS '最后登录时间';
COMMENT ON COLUMN "pla_user_info"."last_login_ip" IS '最后登录IP';
COMMENT ON COLUMN "pla_user_info"."description" IS '描述说明';
COMMENT ON COLUMN "pla_user_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "pla_user_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "pla_user_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "pla_user_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "pla_user_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "pla_user_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "pla_user_info" IS '平台用户信息表（C端用户）';

CREATE UNIQUE INDEX "uk_pla_user_phone" ON "pla_user_info" ("phone") WHERE "delete_flag" = 0;
CREATE UNIQUE INDEX "uk_pla_user_wx_open_id" ON "pla_user_info" ("wx_open_id") WHERE "delete_flag" = 0 AND "wx_open_id" IS NOT NULL;
CREATE INDEX "idx_pla_user_status" ON "pla_user_info" ("status");
CREATE INDEX "idx_pla_user_register_source" ON "pla_user_info" ("register_source");

CREATE TABLE "pla_log_info" (
  "id" int8 NOT NULL,
  "log_type" int2 NOT NULL,
  "title" varchar(64) NOT NULL,
  "description" varchar(500),
  "method" varchar(128),
  "request_url" varchar(256),
  "request_method" varchar(10),
  "request_param" text,
  "response_result" text,
  "operate_ip" varchar(64),
  "operate_location" varchar(128),
  "user_id" int8,
  "user_name" varchar(64),
  "execute_time" int8,
  "error_msg" text,
  "status" int2 NOT NULL DEFAULT 1,
  "trace_id" varchar(64),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "pla_log_info"."id" IS '唯一标识';
COMMENT ON COLUMN "pla_log_info"."log_type" IS '日志类型：1=登录 2=操作 3=异常 4=安全';
COMMENT ON COLUMN "pla_log_info"."title" IS '模块标题，如：平台管理';
COMMENT ON COLUMN "pla_log_info"."description" IS '操作描述，如：修改系统配置';
COMMENT ON COLUMN "pla_log_info"."method" IS '调用方法，如 PlaConfigController.update()';
COMMENT ON COLUMN "pla_log_info"."request_url" IS '请求URL，如 /sxwl-api/pla/config';
COMMENT ON COLUMN "pla_log_info"."request_method" IS 'HTTP方法：GET/POST/PUT/DELETE';
COMMENT ON COLUMN "pla_log_info"."request_param" IS '请求参数（JSON，应用层截断至2000字符）';
COMMENT ON COLUMN "pla_log_info"."response_result" IS '响应结果（JSON，应用层截断至2000字符）';
COMMENT ON COLUMN "pla_log_info"."operate_ip" IS '操作人IP';
COMMENT ON COLUMN "pla_log_info"."operate_location" IS '操作地点，如：北京市（IP反查）';
COMMENT ON COLUMN "pla_log_info"."user_id" IS '操作人ID（关联 pla_user_info.id）';
COMMENT ON COLUMN "pla_log_info"."user_name" IS '操作人账号（冗余，便于查询）';
COMMENT ON COLUMN "pla_log_info"."execute_time" IS '执行耗时（毫秒）';
COMMENT ON COLUMN "pla_log_info"."error_msg" IS '错误信息（异常日志用）';
COMMENT ON COLUMN "pla_log_info"."status" IS '操作状态：0=失败 1=成功';
COMMENT ON COLUMN "pla_log_info"."trace_id" IS '链路追踪ID（分布式场景串联一次请求的多条日志）';
COMMENT ON COLUMN "pla_log_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "pla_log_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "pla_log_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "pla_log_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "pla_log_info" IS '平台日志信息表';

CREATE INDEX "idx_pla_log_type" ON "pla_log_info" ("log_type");
CREATE INDEX "idx_pla_log_user_id" ON "pla_log_info" ("user_id");
CREATE INDEX "idx_pla_log_status" ON "pla_log_info" ("status");
CREATE INDEX "idx_pla_log_create_time" ON "pla_log_info" ("create_time");
CREATE INDEX "idx_pla_log_trace_id" ON "pla_log_info" ("trace_id");

CREATE TABLE "pla_file_info" (
  "id" int8 NOT NULL,
  "file_name" varchar(128) NOT NULL,
  "object_key" varchar(256) NOT NULL,
  "file_url" text,
  "file_size" int8 NOT NULL,
  "file_type" varchar(64) NOT NULL,
  "file_suffix" varchar(16),
  "bucket_name" varchar(64) NOT NULL,
  "md5" varchar(64),
  "business_type" varchar(32),
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp NOT NULL,
  "update_by" int8,
  "update_time" timestamp,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "pla_file_info"."id" IS '唯一标识';
COMMENT ON COLUMN "pla_file_info"."file_name" IS '原始文件名，如：头像.png';
COMMENT ON COLUMN "pla_file_info"."object_key" IS 'RustFS对象键，如 2026/07/02/uuid.png';
COMMENT ON COLUMN "pla_file_info"."file_url" IS '访问URL（冗余，便于前端直接使用）';
COMMENT ON COLUMN "pla_file_info"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "pla_file_info"."file_type" IS '文件MIME类型，如 image/png';
COMMENT ON COLUMN "pla_file_info"."file_suffix" IS '文件后缀，如 png';
COMMENT ON COLUMN "pla_file_info"."bucket_name" IS 'RustFS bucket名，如 pla-file';
COMMENT ON COLUMN "pla_file_info"."md5" IS '文件MD5（秒传/去重用）';
COMMENT ON COLUMN "pla_file_info"."business_type" IS '业务类型，如 avatar、attachment';
COMMENT ON COLUMN "pla_file_info"."status" IS '状态：0=临时 1=正常 2=已删除';
COMMENT ON COLUMN "pla_file_info"."description" IS '描述说明';
COMMENT ON COLUMN "pla_file_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "pla_file_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "pla_file_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "pla_file_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "pla_file_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "pla_file_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "pla_file_info" IS '平台文件信息表';

CREATE INDEX "idx_pla_file_md5" ON "pla_file_info" ("file_size", "md5");
CREATE INDEX "idx_pla_file_business_type" ON "pla_file_info" ("business_type");
CREATE INDEX "idx_pla_file_status" ON "pla_file_info" ("status");
CREATE INDEX "idx_pla_file_create_by" ON "pla_file_info" ("create_by");
CREATE INDEX "idx_pla_file_create_time" ON "pla_file_info" ("create_time");


-- =====================================================================
-- 系统文件上传会话表（RustFS 分片上传过程追踪）
-- =====================================================================

CREATE TABLE "sys_file_session_info" (
  "id"             int8 NOT NULL,
  "file_md5"       varchar(64)   NOT NULL,
  "original_name"  varchar(128)  NOT NULL,
  "file_size"      int8          NOT NULL,
  "content_type"   varchar(100),
  "total_chunks"   int4          NOT NULL,
  "chunk_size"     int4          NOT NULL,
  "status"         int2          NOT NULL DEFAULT 0,
  "create_by"      int8          NOT NULL,
  "create_org"     int8          NOT NULL,
  "create_time"    timestamp     NOT NULL,
  "update_by"      int8,
  "update_time"    timestamp,
  "delete_flag"    int2          NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_file_session_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_file_session_info"."file_md5" IS '文件 MD5，用于秒传判断和续传查询';
COMMENT ON COLUMN "sys_file_session_info"."original_name" IS '原始文件名，合并后写入 sys_file_info.file_name';
COMMENT ON COLUMN "sys_file_session_info"."file_size" IS '总文件大小（字节）';
COMMENT ON COLUMN "sys_file_session_info"."content_type" IS 'MIME 类型，如 application/zip';
COMMENT ON COLUMN "sys_file_session_info"."total_chunks" IS '总分片数，合并时循环读取 0..total_chunks-1';
COMMENT ON COLUMN "sys_file_session_info"."chunk_size" IS '每个分片的大小（字节），除最后一片外所有分片等大';
COMMENT ON COLUMN "sys_file_session_info"."status" IS '状态：0=上传中 1=已完成 2=已取消';
COMMENT ON COLUMN "sys_file_session_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_file_session_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_file_session_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_file_session_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_file_session_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_file_session_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_file_session_info" IS '系统文件上传会话表';

CREATE INDEX "idx_sys_file_session_md5" ON "sys_file_session_info" ("file_md5");
CREATE INDEX "idx_sys_file_session_status" ON "sys_file_session_info" ("status");


-- =====================================================================
-- 系统文件分片明细表（RustFS 分片上传追踪）
-- =====================================================================

CREATE TABLE "sys_file_chunk_info" (
  "id"           int8 NOT NULL,
  "upload_id"    int8          NOT NULL,
  "chunk_index"  int4          NOT NULL,
  "chunk_md5"    varchar(64),
  "object_key"   varchar(256)  NOT NULL,
  "chunk_size"   int8          NOT NULL,
  "status"       int2          NOT NULL DEFAULT 0,
  "create_time"  timestamp     NOT NULL,
  "delete_flag"  int2          NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_file_chunk_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_file_chunk_info"."upload_id" IS '上传会话 ID（关联 sys_file_session_info.id）';
COMMENT ON COLUMN "sys_file_chunk_info"."chunk_index" IS '分片序号，从 0 开始，合并时按此排序';
COMMENT ON COLUMN "sys_file_chunk_info"."chunk_md5" IS '分片 MD5，上传时校验，防止传输损坏';
COMMENT ON COLUMN "sys_file_chunk_info"."object_key" IS 'S3 临时对象键，合并后清理';
COMMENT ON COLUMN "sys_file_chunk_info"."chunk_size" IS '本分片实际大小（最后一片可能小于 chunk_size）';
COMMENT ON COLUMN "sys_file_chunk_info"."status" IS '状态：0=待上传 1=已上传';
COMMENT ON COLUMN "sys_file_chunk_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_file_chunk_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_file_chunk_info" IS '系统文件分片明细表';

CREATE INDEX "idx_sys_file_chunk_upload" ON "sys_file_chunk_info" ("upload_id");
CREATE UNIQUE INDEX "uk_sys_file_chunk" ON "sys_file_chunk_info" ("upload_id", "chunk_index") WHERE "delete_flag" = 0;

-- =====================================================================
-- 代码生成表信息配置表（代码生成器手动配置，不与现有业务表交互）
-- =====================================================================

CREATE TABLE "sys_codegen_table_info" (
    "id"              int8          NOT NULL,
    "table_name"      varchar(128)  NOT NULL,
    "module_prefix"   varchar(64)   NOT NULL,
    "biz_name"        varchar(64)   NOT NULL,
    "biz_name_cn"     varchar(64)   NOT NULL,
    "biz_name_plural" varchar(64)   NOT NULL,
    "table_comment"   varchar(200),
    "package_name"    varchar(128)  NOT NULL,
    "author"          varchar(64),
    "gen_type"        varchar(16)   NOT NULL DEFAULT 'crud',
    "status"          int2          NOT NULL DEFAULT 1,
    "create_by"       int8          NOT NULL,
    "create_org"      int8          NOT NULL,
    "create_time"     timestamp     NOT NULL,
    "update_by"       int8,
    "update_time"     timestamp,
    "delete_flag"     int2          NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_codegen_table_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_codegen_table_info"."table_name" IS '待生成的数据库表名，如 sys_role_info';
COMMENT ON COLUMN "sys_codegen_table_info"."module_prefix" IS '模块前缀，如 system';
COMMENT ON COLUMN "sys_codegen_table_info"."biz_name" IS '业务名（英文单数），如 Role';
COMMENT ON COLUMN "sys_codegen_table_info"."biz_name_cn" IS '业务中文名，如 角色';
COMMENT ON COLUMN "sys_codegen_table_info"."biz_name_plural" IS '业务名（英文复数），如 Roles';
COMMENT ON COLUMN "sys_codegen_table_info"."table_comment" IS '表注释（支持 Markdown 格式）';
COMMENT ON COLUMN "sys_codegen_table_info"."package_name" IS '包名，如 com.sxwl.system';
COMMENT ON COLUMN "sys_codegen_table_info"."author" IS '作者';
COMMENT ON COLUMN "sys_codegen_table_info"."gen_type" IS '生成类型：crud / tree';
COMMENT ON COLUMN "sys_codegen_table_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_codegen_table_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_codegen_table_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_codegen_table_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_codegen_table_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_codegen_table_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_codegen_table_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_codegen_table_info" IS '代码生成表信息配置表';

CREATE UNIQUE INDEX "uk_sys_codegen_table_name" ON "sys_codegen_table_info" ("table_name") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_codegen_table_status" ON "sys_codegen_table_info" ("status");
CREATE INDEX "idx_sys_codegen_table_create_time" ON "sys_codegen_table_info" ("create_time");

-- =====================================================================
-- 代码生成字段信息配置表（核心，手动配置每列的映射规则）
-- =====================================================================

CREATE TABLE "sys_codegen_field_info" (
    "id"              int8          NOT NULL,
    "table_id"        int8          NOT NULL,
    "column_name"     varchar(128)  NOT NULL,
    "column_type"     varchar(32)   NOT NULL,
    "column_comment"  varchar(200),
    "java_type"       varchar(32)   NOT NULL,
    "java_field_name" varchar(128)  NOT NULL,
    "is_pk"           int2          NOT NULL DEFAULT 0,
    "is_insert"       int2          NOT NULL DEFAULT 1,
    "is_edit"         int2          NOT NULL DEFAULT 1,
    "is_list"         int2          NOT NULL DEFAULT 1,
    "is_query"        int2          NOT NULL DEFAULT 0,
    "query_type"      varchar(16),
    "query_form_type" varchar(32),
    "form_type"       varchar(32),
    "form_dict_code"  varchar(64),
    "is_required"     int2          NOT NULL DEFAULT 0,
    "is_unique"       int2          NOT NULL DEFAULT 0,
    "max_length"      int4,
    "sort"            int4          NOT NULL DEFAULT 0,
    "create_time"     timestamp     NOT NULL,
    "delete_flag"     int2          NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_codegen_field_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_codegen_field_info"."table_id" IS '关联表 ID（关联 sys_codegen_table_info.id）';
COMMENT ON COLUMN "sys_codegen_field_info"."column_name" IS 'DB 列名，如 role_code';
COMMENT ON COLUMN "sys_codegen_field_info"."column_type" IS 'DB 类型，如 varchar / int8 / decimal / datetime / text';
COMMENT ON COLUMN "sys_codegen_field_info"."column_comment" IS 'DB 列注释（支持 Markdown 格式）';
COMMENT ON COLUMN "sys_codegen_field_info"."java_type" IS 'Java 类型：String / Long / Integer / LocalDateTime / BigDecimal / Boolean';
COMMENT ON COLUMN "sys_codegen_field_info"."java_field_name" IS 'Java 字段名，如 roleCode';
COMMENT ON COLUMN "sys_codegen_field_info"."is_pk" IS '是否主键：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."is_insert" IS '是否在新增时显示：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."is_edit" IS '是否在编辑时显示：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."is_list" IS '是否在列表展示：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."is_query" IS '是否作为查询条件：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."query_type" IS '查询方式：eq / like / between';
COMMENT ON COLUMN "sys_codegen_field_info"."query_form_type" IS '查询表单组件：Input / Select / DateRange';
COMMENT ON COLUMN "sys_codegen_field_info"."form_type" IS '表单组件：Input / Select / TextArea / DatePicker / NumberInput / ImageUpload / Radio / Checkbox';
COMMENT ON COLUMN "sys_codegen_field_info"."form_dict_code" IS 'Select/Radio 关联的字典编码';
COMMENT ON COLUMN "sys_codegen_field_info"."is_required" IS '是否必填：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."is_unique" IS '是否唯一性校验：0=否 1=是';
COMMENT ON COLUMN "sys_codegen_field_info"."max_length" IS '最大长度';
COMMENT ON COLUMN "sys_codegen_field_info"."sort" IS '排序号';
COMMENT ON COLUMN "sys_codegen_field_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_codegen_field_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_codegen_field_info" IS '代码生成字段信息配置表';

CREATE INDEX "idx_sys_codegen_field_table_id" ON "sys_codegen_field_info" ("table_id");
CREATE INDEX "idx_sys_codegen_field_sort" ON "sys_codegen_field_info" ("table_id", "sort");


-- =====================================================================
-- 系统参数配置表（系统配置参数表，存放程序运行参数）
-- =====================================================================

CREATE TABLE "sys_config_info" (
  "id"             int8          NOT NULL,
  "config_key"     varchar(128)  NOT NULL,
  "config_name"    varchar(128)  NOT NULL,
  "config_value"   text          NOT NULL,
  "config_type"    varchar(32)   NOT NULL DEFAULT 'system',
  "description"    varchar(200),
  "status"         int2          NOT NULL DEFAULT 1,
  "create_by"      int8          NOT NULL,
  "create_org"     int8          NOT NULL,
  "create_time"    timestamp     NOT NULL,
  "update_by"      int8,
  "update_time"    timestamp,
  "delete_flag"    int2          NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_config_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_config_info"."config_key" IS '参数键名，全局唯一，如 sys.siteName、job.backupCron';
COMMENT ON COLUMN "sys_config_info"."config_name" IS '参数名称，如：站点名称、备份定时表达式';
COMMENT ON COLUMN "sys_config_info"."config_value" IS '参数值';
COMMENT ON COLUMN "sys_config_info"."config_type" IS '参数类型：system=系统参数 notice=通知参数 job=任务参数';
COMMENT ON COLUMN "sys_config_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_config_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "sys_config_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_config_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_config_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_config_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_config_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_config_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_config_info" IS '系统参数配置表';

CREATE UNIQUE INDEX "uk_sys_config_key" ON "sys_config_info" ("config_key") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_config_type" ON "sys_config_info" ("config_type");
CREATE INDEX "idx_sys_config_status" ON "sys_config_info" ("status");

-- ==================== 种子数据：系统参数 ====================
INSERT INTO "sys_config_info" ("id", "config_key", "config_name", "config_value", "config_type", "description", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(1, 'sys.siteName', '站点名称', 'SXWL 管理系统', 'system', '系统站点名称', 1, 0, 0, NOW(), 0),
(2, 'sys.copyright', '版权信息', '© 2026 SXWL. All Rights Reserved.', 'system', '页脚版权信息', 1, 0, 0, NOW(), 0),
(3, 'sys.logo', '系统Logo', '/logo.png', 'system', '系统 Logo 图片路径', 1, 0, 0, NOW(), 0),
(4, 'notice.pushChannel', '通知推送通道', 'sse', 'notice', '通知推送通道：sse=SSE推送 ws=WebSocket推送', 1, 0, 0, NOW(), 0),
(5, 'job.backupCron', '数据备份定时', '0 0 2 * * ?', 'job', '数据备份定时表达式（每天凌晨2点）', 1, 0, 0, NOW(), 0);


-- ==================== 种子数据：组织 / 角色 / 用户 / 菜单 ====================
-- 注意：这些是引导种子数据，id 固定为 1 的和业务数据区分开。

-- 1. 根组织
INSERT INTO "sys_organization_info" ("id", "org_code", "org_name", "parent_id", "ancestors", "org_level", "status", "description", "create_by", "create_org", "create_time", "delete_flag") VALUES
(1, '0001', '总公司', 0, '0', 1, 1, '根组织', 0, 0, NOW(), 0);

-- 2. 超级管理员角色（data_scope=1 触发超级管理员 *:*:*）
INSERT INTO "sys_role_info" ("id", "role_code", "role_name", "data_scope", "sort", "status", "description", "create_by", "create_org", "create_time", "delete_flag") VALUES
(1, 'super_admin', '超级管理员', 1, 1, 1, '超级管理员（所有权限）', 0, 0, NOW(), 0);

-- 3. 引导管理员用户（用于首次登录后创建正式账号，密码由 admin 首次登录时重置）
INSERT INTO "sys_user_info" ("id", "username", "password", "real_name", "nickname", "phone", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(1, 'admin', '', '系统管理员', 'Admin', '13800000000', 1, 0, 0, NOW(), 0);

-- 4. 用户-角色关联
INSERT INTO "sys_user_role_info" ("id", "user_id", "role_id", "create_by", "create_org", "create_time", "delete_flag") VALUES
(1, 1, 1, 0, 0, NOW(), 0);

-- 5. 菜单树（覆盖所有扩展功能模块）
-- 目录：系统管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(100, '系统管理', 0, '0', 1, 'setting', 2, 1, 1, 0, 0, NOW(), 0);
-- 菜单：仪表盘
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(101, '仪表盘', 0, '0', 2, 'dashboard', 'Dashboard', 'system:dashboard:query', 'dashboard', 1, 1, 1, 0, 0, NOW(), 0);
-- 菜单：用户管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(102, '用户管理', 100, '0,100', 2, 'system/user', 'System/User', 'system:user:list', 'user', 2, 1, 1, 0, 0, NOW(), 0);
-- 菜单：角色管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(103, '角色管理', 100, '0,100', 2, 'system/role', 'System/Role', 'system:role:list', 'role', 3, 1, 1, 0, 0, NOW(), 0);
-- 菜单：菜单管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(104, '菜单管理', 100, '0,100', 2, 'system/menu', 'System/Menu', 'system:menu:list', 'menu', 4, 1, 1, 0, 0, NOW(), 0);
-- 菜单：组织管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(105, '组织管理', 100, '0,100', 2, 'system/organization', 'System/Organization', 'system:org:list', 'organization', 5, 1, 1, 0, 0, NOW(), 0);
-- 菜单：岗位管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(106, '岗位管理', 100, '0,100', 2, 'system/position', 'System/Position', 'system:position:list', 'position', 6, 1, 1, 0, 0, NOW(), 0);
-- 菜单：字典管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(107, '字典管理', 100, '0,100', 2, 'system/dict', 'System/Dict', 'system:dict:list', 'dict', 7, 1, 1, 0, 0, NOW(), 0);
-- 菜单：参数管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(108, '参数管理', 100, '0,100', 2, 'system/config', 'System/Config', 'system:config:list', 'config', 8, 1, 1, 0, 0, NOW(), 0);
-- 菜单：通知公告
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(109, '通知公告', 100, '0,100', 2, 'system/notice', 'System/Notice', 'system:notice:list', 'notice', 9, 1, 1, 0, 0, NOW(), 0);

-- 目录：监控运维
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(200, '监控运维', 0, '0', 1, 'monitor', 2, 1, 1, 0, 0, NOW(), 0);

-- 菜单：系统监控
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(201, '系统监控', 200, '0,200', 2, 'monitor/server', 'Monitor/ServerMonitor', 'monitor:server:list', 'server', 1, 1, 1, 0, 0, NOW(), 0);
-- 菜单：在线用户
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(202, '在线用户', 200, '0,200', 2, 'monitor/online-user', 'Monitor/OnlineUser', 'monitor:onlineuser:list', 'online-user', 2, 1, 1, 0, 0, NOW(), 0);
-- 菜单：缓存管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(203, '缓存管理', 200, '0,200', 2, 'monitor/cache', 'Monitor/Cache', 'monitor:cache:list', 'cache', 3, 1, 1, 0, 0, NOW(), 0);
-- 菜单：定时任务
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(204, '定时任务', 200, '0,200', 2, 'monitor/job', 'Monitor/Job', 'monitor:job:list', 'job', 4, 1, 1, 0, 0, NOW(), 0);
-- 菜单：任务日志
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(205, '任务日志', 200, '0,200', 2, 'monitor/job-log', 'Monitor/JobLog', 'monitor:job:list', 'job-log', 5, 1, 1, 0, 0, NOW(), 0);
-- 菜单：数据备份
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(206, '数据备份', 200, '0,200', 2, 'system/backup', 'System/Backup', 'monitor:backup:list', 'backup', 6, 1, 1, 0, 0, NOW(), 0);
-- 目录：日志管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(300, '日志管理', 0, '0', 1, 'log', 3, 1, 1, 0, 0, NOW(), 0);
-- 菜单：操作日志
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(301, '操作日志', 300, '0,300', 2, 'log', 'Log/OperationLog', 'system:log:list', 'log', 1, 1, 1, 0, 0, NOW(), 0);
-- 菜单：登录日志
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(302, '登录日志', 300, '0,300', 2, 'log/login', 'Log/LoginLog', 'system:loginlog:list', 'login-log', 2, 1, 1, 0, 0, NOW(), 0);
-- 目录：系统工具
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(400, '系统工具', 0, '0', 1, 'tool', 4, 1, 1, 0, 0, NOW(), 0);
-- 菜单：代码生成
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(401, '代码生成', 400, '0,400', 2, 'system/codegen', 'System/Codegen', 'codegen:list', 'codegen', 1, 1, 1, 0, 0, NOW(), 0);
-- 菜单：文件管理
INSERT INTO "sys_menu_info" ("id", "menu_name", "parent_id", "ancestors", "menu_type", "path", "component", "perms", "icon", "sort", "visible", "status", "create_by", "create_org", "create_time", "delete_flag") VALUES
(402, '文件管理', 400, '0,400', 2, 'file', 'File', 'system:file:list', 'file', 2, 1, 1, 0, 0, NOW(), 0);

-- 6. 角色-菜单关联（super_admin 拥有全部菜单权限）
INSERT INTO "sys_role_menu_info" ("id", "role_id", "menu_id", "create_by", "create_org", "create_time", "delete_flag") VALUES
(1, 1, 100, 0, 0, NOW(), 0),
(2, 1, 101, 0, 0, NOW(), 0),
(3, 1, 102, 0, 0, NOW(), 0),
(4, 1, 103, 0, 0, NOW(), 0),
(5, 1, 104, 0, 0, NOW(), 0),
(6, 1, 105, 0, 0, NOW(), 0),
(7, 1, 106, 0, 0, NOW(), 0),
(8, 1, 107, 0, 0, NOW(), 0),
(9, 1, 108, 0, 0, NOW(), 0),
(10, 1, 109, 0, 0, NOW(), 0),
(11, 1, 200, 0, 0, NOW(), 0),
(12, 1, 201, 0, 0, NOW(), 0),
(13, 1, 202, 0, 0, NOW(), 0),
(14, 1, 203, 0, 0, NOW(), 0),
(15, 1, 204, 0, 0, NOW(), 0),
(16, 1, 205, 0, 0, NOW(), 0),
(17, 1, 206, 0, 0, NOW(), 0),
(18, 1, 300, 0, 0, NOW(), 0),
(19, 1, 301, 0, 0, NOW(), 0),
(20, 1, 302, 0, 0, NOW(), 0),
(21, 1, 400, 0, 0, NOW(), 0),
(22, 1, 401, 0, 0, NOW(), 0),
(23, 1, 402, 0, 0, NOW(), 0);

-- =====================================================================


-- =====================================================================
-- 通知公告信息表
-- =====================================================================

CREATE TABLE "sys_notice_info" (
  "id"             int8          NOT NULL,
  "title"          varchar(256)  NOT NULL,
  "content"        text          NOT NULL,
  "notice_type"    varchar(32)   NOT NULL DEFAULT 'notice',
  "level"          varchar(32)   NOT NULL DEFAULT 'info',
  "status"         int2          NOT NULL DEFAULT 0,
  "publish_time"   timestamp,
  "expire_time"    timestamp,
  "create_by"      int8          NOT NULL,
  "create_org"     int8          NOT NULL,
  "create_time"    timestamp     NOT NULL,
  "update_by"      int8,
  "update_time"    timestamp,
  "delete_flag"    int2          NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_notice_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_notice_info"."title" IS '公告标题';
COMMENT ON COLUMN "sys_notice_info"."content" IS '公告内容（富文本 HTML）';
COMMENT ON COLUMN "sys_notice_info"."notice_type" IS '公告类型：notice=通知 announcement=公告';
COMMENT ON COLUMN "sys_notice_info"."level" IS '级别：info=普通 important=重要 urgent=紧急';
COMMENT ON COLUMN "sys_notice_info"."status" IS '状态：0=草稿 1=已发布 2=已撤回';
COMMENT ON COLUMN "sys_notice_info"."publish_time" IS '发布时间';
COMMENT ON COLUMN "sys_notice_info"."expire_time" IS '过期时间';
COMMENT ON COLUMN "sys_notice_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_notice_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_notice_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_notice_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_notice_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_notice_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_notice_info" IS '通知公告信息表';

CREATE INDEX "idx_sys_notice_type" ON "sys_notice_info" ("notice_type");
CREATE INDEX "idx_sys_notice_level" ON "sys_notice_info" ("level");
CREATE INDEX "idx_sys_notice_status" ON "sys_notice_info" ("status");
CREATE INDEX "idx_sys_notice_create_time" ON "sys_notice_info" ("create_time");


-- =====================================================================
-- 定时任务定义表
-- =====================================================================

CREATE TABLE "sys_job_info" (
  "id"              int8          NOT NULL,
  "job_name"        varchar(64)   NOT NULL,
  "job_group"       varchar(64)   NOT NULL DEFAULT 'DEFAULT',
  "class_name"      varchar(256)  NOT NULL,
  "method_name"     varchar(64)   NOT NULL,
  "method_params"   varchar(500),
  "cron_expression" varchar(64)   NOT NULL,
  "description"     varchar(200),
  "status"          int2          NOT NULL DEFAULT 1,
  "create_by"       int8          NOT NULL,
  "create_org"      int8          NOT NULL,
  "create_time"     timestamp     NOT NULL,
  "update_by"       int8,
  "update_time"     timestamp,
  "delete_flag"     int2          NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_job_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_job_info"."job_name" IS '任务名称';
COMMENT ON COLUMN "sys_job_info"."job_group" IS '任务分组';
COMMENT ON COLUMN "sys_job_info"."class_name" IS '调用目标类全限定名';
COMMENT ON COLUMN "sys_job_info"."method_name" IS '调用目标方法名';
COMMENT ON COLUMN "sys_job_info"."method_params" IS '方法参数（JSON）';
COMMENT ON COLUMN "sys_job_info"."cron_expression" IS 'Cron 表达式';
COMMENT ON COLUMN "sys_job_info"."description" IS '描述说明';
COMMENT ON COLUMN "sys_job_info"."status" IS '状态：0=暂停 1=正常';
COMMENT ON COLUMN "sys_job_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_job_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_job_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_job_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_job_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_job_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_job_info" IS '定时任务定义表';

CREATE UNIQUE INDEX "uk_sys_job_name_group" ON "sys_job_info" ("job_name", "job_group") WHERE "delete_flag" = 0;
CREATE INDEX "idx_sys_job_status" ON "sys_job_info" ("status");


-- =====================================================================
-- 定时任务执行日志表
-- =====================================================================

CREATE TABLE "sys_job_log_info" (
  "id"              int8          NOT NULL,
  "job_id"          int8          NOT NULL,
  "job_name"        varchar(64)   NOT NULL,
  "job_group"       varchar(64),
  "class_name"      varchar(256),
  "method_name"     varchar(64),
  "method_params"   varchar(500),
  "cron_expression" varchar(64),
  "status"          int2          NOT NULL DEFAULT 1,
  "execute_time"    int8,
  "error_msg"       text,
  "fire_time"       timestamp,
  "create_by"       int8          NOT NULL,
  "create_org"      int8          NOT NULL,
  "create_time"     timestamp     NOT NULL,
  "update_by"       int8,
  "update_time"     timestamp,
  "delete_flag"     int2          NOT NULL DEFAULT 0,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_job_log_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_job_log_info"."job_id" IS '任务 ID（关联 sys_job_info.id）';
COMMENT ON COLUMN "sys_job_log_info"."job_name" IS '任务名称';
COMMENT ON COLUMN "sys_job_log_info"."job_group" IS '任务分组';
COMMENT ON COLUMN "sys_job_log_info"."class_name" IS '调用目标类全限定名';
COMMENT ON COLUMN "sys_job_log_info"."method_name" IS '调用目标方法名';
COMMENT ON COLUMN "sys_job_log_info"."method_params" IS '方法参数（JSON）';
COMMENT ON COLUMN "sys_job_log_info"."cron_expression" IS 'Cron 表达式';
COMMENT ON COLUMN "sys_job_log_info"."status" IS '执行状态：0=失败 1=成功';
COMMENT ON COLUMN "sys_job_log_info"."execute_time" IS '执行耗时（毫秒）';
COMMENT ON COLUMN "sys_job_log_info"."error_msg" IS '错误信息';
COMMENT ON COLUMN "sys_job_log_info"."fire_time" IS '实际执行时间';
COMMENT ON COLUMN "sys_job_log_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "sys_job_log_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_job_log_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_job_log_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "sys_job_log_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_job_log_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_job_log_info" IS '定时任务执行日志表';

CREATE INDEX "idx_sys_job_log_job_id" ON "sys_job_log_info" ("job_id");
CREATE INDEX "idx_sys_job_log_status" ON "sys_job_log_info" ("status");
CREATE INDEX "idx_sys_job_log_create_time" ON "sys_job_log_info" ("create_time");


-- =====================================================================
-- Quartz 调度器系统表（PostgreSQL）
-- =====================================================================

CREATE TABLE "qrtz_job_details" (
  "sched_name"        varchar(120) NOT NULL,
  "job_name"          varchar(200) NOT NULL,
  "job_group"         varchar(200) NOT NULL,
  "description"       varchar(250),
  "job_class_name"    varchar(250) NOT NULL,
  "is_durable"        boolean      NOT NULL,
  "is_nonconcurrent"  boolean      NOT NULL,
  "is_update_data"    boolean      NOT NULL,
  "requests_recovery" boolean      NOT NULL,
  "job_data"          bytea,
  PRIMARY KEY ("sched_name", "job_name", "job_group")
);

COMMENT ON TABLE "qrtz_job_details" IS 'Quartz 任务详情表';

COMMENT ON COLUMN "qrtz_job_details"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_job_details"."job_name" IS '任务名称';
COMMENT ON COLUMN "qrtz_job_details"."job_group" IS '任务分组';
COMMENT ON COLUMN "qrtz_job_details"."description" IS '任务描述';
COMMENT ON COLUMN "qrtz_job_details"."job_class_name" IS '任务实现类全限定名';
COMMENT ON COLUMN "qrtz_job_details"."is_durable" IS '是否持久化（任务完成后是否保留）';
COMMENT ON COLUMN "qrtz_job_details"."is_nonconcurrent" IS '是否禁止并发执行';
COMMENT ON COLUMN "qrtz_job_details"."is_update_data" IS '每次执行是否更新JobData';
COMMENT ON COLUMN "qrtz_job_details"."requests_recovery" IS '调度器异常重启后是否请求恢复';
COMMENT ON COLUMN "qrtz_job_details"."job_data" IS '任务数据（二进制）';


CREATE TABLE "qrtz_triggers" (
  "sched_name"     varchar(120) NOT NULL,
  "trigger_name"   varchar(200) NOT NULL,
  "trigger_group"  varchar(200) NOT NULL,
  "job_name"       varchar(200) NOT NULL,
  "job_group"      varchar(200) NOT NULL,
  "description"    varchar(250),
  "next_fire_time" bigint,
  "prev_fire_time" bigint,
  "priority"       integer,
  "trigger_state"  varchar(16)  NOT NULL,
  "trigger_type"   varchar(8)   NOT NULL,
  "start_time"     bigint       NOT NULL,
  "end_time"       bigint,
  "calendar_name"  varchar(200),
  "misfire_instr"  smallint,
  "job_data"       bytea,
  PRIMARY KEY ("sched_name", "trigger_name", "trigger_group"),
  FOREIGN KEY ("sched_name", "job_name", "job_group") REFERENCES "qrtz_job_details" ("sched_name", "job_name", "job_group")
);

COMMENT ON TABLE "qrtz_triggers" IS 'Quartz 触发器表';

COMMENT ON COLUMN "qrtz_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "qrtz_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "qrtz_triggers"."job_name" IS '关联任务名称';
COMMENT ON COLUMN "qrtz_triggers"."job_group" IS '关联任务分组';
COMMENT ON COLUMN "qrtz_triggers"."description" IS '触发器描述';
COMMENT ON COLUMN "qrtz_triggers"."next_fire_time" IS '下次触发时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_triggers"."prev_fire_time" IS '上次触发时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_triggers"."priority" IS '优先级';
COMMENT ON COLUMN "qrtz_triggers"."trigger_state" IS '触发器状态（WAITING/PAUSED/ACQUIRED/BLOCKED/ERROR/COMPLETE）';
COMMENT ON COLUMN "qrtz_triggers"."trigger_type" IS '触发器类型（SIMPLE/CRON/BLOB/CALENDAR/DAILY_TIME_INDICATOR）';
COMMENT ON COLUMN "qrtz_triggers"."start_time" IS '开始时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_triggers"."end_time" IS '结束时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_triggers"."calendar_name" IS '日历名称';
COMMENT ON COLUMN "qrtz_triggers"."misfire_instr" IS '失火指令';
COMMENT ON COLUMN "qrtz_triggers"."job_data" IS '任务数据（二进制）';


CREATE TABLE "qrtz_simple_triggers" (
  "sched_name"      varchar(120) NOT NULL,
  "trigger_name"    varchar(200) NOT NULL,
  "trigger_group"   varchar(200) NOT NULL,
  "repeat_count"    bigint       NOT NULL,
  "repeat_interval" bigint       NOT NULL,
  "times_triggered" bigint       NOT NULL,
  PRIMARY KEY ("sched_name", "trigger_name", "trigger_group"),
  FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "qrtz_triggers" ("sched_name", "trigger_name", "trigger_group")
);

COMMENT ON TABLE "qrtz_simple_triggers" IS 'Quartz 简单触发器表';

COMMENT ON COLUMN "qrtz_simple_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_simple_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "qrtz_simple_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "qrtz_simple_triggers"."repeat_count" IS '重复次数（0=不重复，-1=无限重复）';
COMMENT ON COLUMN "qrtz_simple_triggers"."repeat_interval" IS '重复间隔（毫秒）';
COMMENT ON COLUMN "qrtz_simple_triggers"."times_triggered" IS '已触发次数';


CREATE TABLE "qrtz_cron_triggers" (
  "sched_name"      varchar(120) NOT NULL,
  "trigger_name"    varchar(200) NOT NULL,
  "trigger_group"   varchar(200) NOT NULL,
  "cron_expression" varchar(120) NOT NULL,
  "time_zone_id"    varchar(80),
  PRIMARY KEY ("sched_name", "trigger_name", "trigger_group"),
  FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "qrtz_triggers" ("sched_name", "trigger_name", "trigger_group")
);

COMMENT ON TABLE "qrtz_cron_triggers" IS 'Quartz Cron 触发器表';

COMMENT ON COLUMN "qrtz_cron_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_cron_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "qrtz_cron_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "qrtz_cron_triggers"."cron_expression" IS 'Cron 表达式';
COMMENT ON COLUMN "qrtz_cron_triggers"."time_zone_id" IS '时区ID';


CREATE TABLE "qrtz_simprop_triggers" (
  "sched_name"    varchar(120) NOT NULL,
  "trigger_name"  varchar(200) NOT NULL,
  "trigger_group" varchar(200) NOT NULL,
  "str_prop_1"    varchar(512),
  "str_prop_2"    varchar(512),
  "str_prop_3"    varchar(512),
  "int_prop_1"    integer,
  "int_prop_2"    integer,
  "long_prop_1"   bigint,
  "long_prop_2"   bigint,
  "dec_prop_1"    numeric(13, 4),
  "dec_prop_2"    numeric(13, 4),
  "bool_prop_1"   boolean,
  "bool_prop_2"   boolean,
  "time_zone_id"  varchar(80),
  PRIMARY KEY ("sched_name", "trigger_name", "trigger_group"),
  FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "qrtz_triggers" ("sched_name", "trigger_name", "trigger_group")
);

COMMENT ON TABLE "qrtz_simprop_triggers" IS 'Quartz 简化属性触发器表';

COMMENT ON COLUMN "qrtz_simprop_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_simprop_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "qrtz_simprop_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "qrtz_simprop_triggers"."str_prop_1" IS '字符串属性1';
COMMENT ON COLUMN "qrtz_simprop_triggers"."str_prop_2" IS '字符串属性2';
COMMENT ON COLUMN "qrtz_simprop_triggers"."str_prop_3" IS '字符串属性3';
COMMENT ON COLUMN "qrtz_simprop_triggers"."int_prop_1" IS '整型属性1';
COMMENT ON COLUMN "qrtz_simprop_triggers"."int_prop_2" IS '整型属性2';
COMMENT ON COLUMN "qrtz_simprop_triggers"."long_prop_1" IS '长整型属性1';
COMMENT ON COLUMN "qrtz_simprop_triggers"."long_prop_2" IS '长整型属性2';
COMMENT ON COLUMN "qrtz_simprop_triggers"."dec_prop_1" IS '十进制属性1';
COMMENT ON COLUMN "qrtz_simprop_triggers"."dec_prop_2" IS '十进制属性2';
COMMENT ON COLUMN "qrtz_simprop_triggers"."bool_prop_1" IS '布尔属性1';
COMMENT ON COLUMN "qrtz_simprop_triggers"."bool_prop_2" IS '布尔属性2';
COMMENT ON COLUMN "qrtz_simprop_triggers"."time_zone_id" IS '时区ID';


CREATE TABLE "qrtz_blob_triggers" (
  "sched_name"    varchar(120) NOT NULL,
  "trigger_name"  varchar(200) NOT NULL,
  "trigger_group" varchar(200) NOT NULL,
  "blob_data"     bytea,
  PRIMARY KEY ("sched_name", "trigger_name", "trigger_group"),
  FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "qrtz_triggers" ("sched_name", "trigger_name", "trigger_group")
);

COMMENT ON TABLE "qrtz_blob_triggers" IS 'Quartz BLOB 触发器表';

COMMENT ON COLUMN "qrtz_blob_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_blob_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "qrtz_blob_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "qrtz_blob_triggers"."blob_data" IS 'BLOB 触发数据（二进制）';


CREATE TABLE "qrtz_calendars" (
  "sched_name"    varchar(120) NOT NULL,
  "calendar_name" varchar(200) NOT NULL,
  "calendar"      bytea        NOT NULL,
  PRIMARY KEY ("sched_name", "calendar_name")
);

COMMENT ON TABLE "qrtz_calendars" IS 'Quartz 日历表';

COMMENT ON COLUMN "qrtz_calendars"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_calendars"."calendar_name" IS '日历名称';
COMMENT ON COLUMN "qrtz_calendars"."calendar" IS '日历数据（二进制）';


CREATE TABLE "qrtz_paused_trigger_grps" (
  "sched_name"    varchar(120) NOT NULL,
  "trigger_group" varchar(200) NOT NULL,
  PRIMARY KEY ("sched_name", "trigger_group")
);

COMMENT ON TABLE "qrtz_paused_trigger_grps" IS 'Quartz 暂停触发器组表';

COMMENT ON COLUMN "qrtz_paused_trigger_grps"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_paused_trigger_grps"."trigger_group" IS '已暂停的触发器分组';


CREATE TABLE "qrtz_fired_triggers" (
  "sched_name"        varchar(120) NOT NULL,
  "entry_id"          varchar(95)  NOT NULL,
  "trigger_name"      varchar(200) NOT NULL,
  "trigger_group"     varchar(200) NOT NULL,
  "instance_name"     varchar(200) NOT NULL,
  "fired_time"        bigint       NOT NULL,
  "sched_time"        bigint       NOT NULL,
  "priority"          integer      NOT NULL,
  "state"             varchar(16)  NOT NULL,
  "job_name"          varchar(200),
  "job_group"         varchar(200),
  "is_nonconcurrent"  boolean,
  "requests_recovery" boolean,
  PRIMARY KEY ("sched_name", "entry_id")
);

COMMENT ON TABLE "qrtz_fired_triggers" IS 'Quartz 已触发触发器表';

COMMENT ON COLUMN "qrtz_fired_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_fired_triggers"."entry_id" IS '调度条目ID';
COMMENT ON COLUMN "qrtz_fired_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "qrtz_fired_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "qrtz_fired_triggers"."instance_name" IS '调度器实例名称';
COMMENT ON COLUMN "qrtz_fired_triggers"."fired_time" IS '触发时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_fired_triggers"."sched_time" IS '预定调度时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_fired_triggers"."priority" IS '优先级';
COMMENT ON COLUMN "qrtz_fired_triggers"."state" IS '触发器状态';
COMMENT ON COLUMN "qrtz_fired_triggers"."job_name" IS '任务名称';
COMMENT ON COLUMN "qrtz_fired_triggers"."job_group" IS '任务分组';
COMMENT ON COLUMN "qrtz_fired_triggers"."is_nonconcurrent" IS '是否禁止并发执行';
COMMENT ON COLUMN "qrtz_fired_triggers"."requests_recovery" IS '是否请求恢复';


CREATE TABLE "qrtz_scheduler_state" (
  "sched_name"        varchar(120) NOT NULL,
  "instance_name"     varchar(200) NOT NULL,
  "last_checkin_time" bigint       NOT NULL,
  "checkin_interval"  bigint       NOT NULL,
  PRIMARY KEY ("sched_name", "instance_name")
);

COMMENT ON TABLE "qrtz_scheduler_state" IS 'Quartz 调度器状态表';

COMMENT ON COLUMN "qrtz_scheduler_state"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_scheduler_state"."instance_name" IS '调度器实例名称';
COMMENT ON COLUMN "qrtz_scheduler_state"."last_checkin_time" IS '最后检入时间（毫秒时间戳）';
COMMENT ON COLUMN "qrtz_scheduler_state"."checkin_interval" IS '检入间隔（毫秒）';


CREATE TABLE "qrtz_locks" (
  "sched_name" varchar(120) NOT NULL,
  "lock_name"  varchar(40)  NOT NULL,
  PRIMARY KEY ("sched_name", "lock_name")
);

COMMENT ON TABLE "qrtz_locks" IS 'Quartz 锁表';

COMMENT ON COLUMN "qrtz_locks"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "qrtz_locks"."lock_name" IS '锁名称';

-- Quartz 性能索引（官方标准）
CREATE INDEX IF NOT EXISTS "idx_qrtz_j_req_recovery" ON "qrtz_job_details" ("sched_name", "requests_recovery");
CREATE INDEX IF NOT EXISTS "idx_qrtz_j_grp" ON "qrtz_job_details" ("sched_name", "job_group");

CREATE INDEX IF NOT EXISTS "idx_qrtz_t_j" ON "qrtz_triggers" ("sched_name", "job_name", "job_group");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_jg" ON "qrtz_triggers" ("sched_name", "job_group");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_c" ON "qrtz_triggers" ("sched_name", "calendar_name");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_g" ON "qrtz_triggers" ("sched_name", "trigger_group");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_state" ON "qrtz_triggers" ("sched_name", "trigger_state");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_n_state" ON "qrtz_triggers" ("sched_name", "trigger_name", "trigger_group", "trigger_state");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_n_g_state" ON "qrtz_triggers" ("sched_name", "trigger_group", "trigger_state");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_next_fire_time" ON "qrtz_triggers" ("sched_name", "next_fire_time");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_nft_st" ON "qrtz_triggers" ("sched_name", "trigger_state", "next_fire_time");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_nft_misfire" ON "qrtz_triggers" ("sched_name", "misfire_instr", "next_fire_time");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_nft_st_misfire" ON "qrtz_triggers" ("sched_name", "misfire_instr", "next_fire_time", "trigger_state");
CREATE INDEX IF NOT EXISTS "idx_qrtz_t_nft_st_misfire_grp" ON "qrtz_triggers" ("sched_name", "misfire_instr", "next_fire_time", "trigger_group", "trigger_state");

CREATE INDEX IF NOT EXISTS "idx_qrtz_ft_trig_inst_name" ON "qrtz_fired_triggers" ("sched_name", "instance_name");
CREATE INDEX IF NOT EXISTS "idx_qrtz_ft_inst_job_req_rcvry" ON "qrtz_fired_triggers" ("sched_name", "instance_name", "requests_recovery");
CREATE INDEX IF NOT EXISTS "idx_qrtz_ft_j_g" ON "qrtz_fired_triggers" ("sched_name", "job_name", "job_group");
CREATE INDEX IF NOT EXISTS "idx_qrtz_ft_jg" ON "qrtz_fired_triggers" ("sched_name", "job_group");
CREATE INDEX IF NOT EXISTS "idx_qrtz_ft_t_g" ON "qrtz_fired_triggers" ("sched_name", "trigger_name", "trigger_group");
CREATE INDEX IF NOT EXISTS "idx_qrtz_ft_tg" ON "qrtz_fired_triggers" ("sched_name", "trigger_group");


-- =====================================================================
-- 系统监控指标日志表（时序记录，无审计字段）
-- =====================================================================

CREATE TABLE "sys_monitor_server_log" (
  "id" int8 NOT NULL,
  "cpu_load" float8,
  "mem_used" int8,
  "mem_total" int8,
  "disk_used" int8,
  "disk_total" int8,
  "create_time" timestamp NOT NULL,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_monitor_server_log"."id" IS '雪花ID';
COMMENT ON COLUMN "sys_monitor_server_log"."cpu_load" IS 'CPU 负载百分比（0~100）';
COMMENT ON COLUMN "sys_monitor_server_log"."mem_used" IS '已用内存（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."mem_total" IS '总内存（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."disk_used" IS '已用磁盘（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."disk_total" IS '总磁盘（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_server_log" IS '系统监控-服务器指标日志';

CREATE INDEX "idx_smsl_create_time" ON "sys_monitor_server_log" ("create_time");


CREATE TABLE "sys_monitor_jvm_log" (
  "id" int8 NOT NULL,
  "heap_used" int8,
  "heap_max" int8,
  "heap_committed" int8,
  "thread_count" int4,
  "peak_thread_count" int4,
  "class_loaded_count" int4,
  "create_time" timestamp NOT NULL,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_monitor_jvm_log"."id" IS '雪花ID';
COMMENT ON COLUMN "sys_monitor_jvm_log"."heap_used" IS '堆内存已用（字节）';
COMMENT ON COLUMN "sys_monitor_jvm_log"."heap_max" IS '堆内存最大值（字节）';
COMMENT ON COLUMN "sys_monitor_jvm_log"."heap_committed" IS '堆内存提交值（字节）';
COMMENT ON COLUMN "sys_monitor_jvm_log"."thread_count" IS '当前线程数';
COMMENT ON COLUMN "sys_monitor_jvm_log"."peak_thread_count" IS '峰值线程数';
COMMENT ON COLUMN "sys_monitor_jvm_log"."class_loaded_count" IS '已加载类数';
COMMENT ON COLUMN "sys_monitor_jvm_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_jvm_log" IS '系统监控-JVM 指标日志';

CREATE INDEX "idx_smjvl_create_time" ON "sys_monitor_jvm_log" ("create_time");


CREATE TABLE "sys_monitor_redis_log" (
  "id" int8 NOT NULL,
  "connected_clients" int4,
  "used_memory" int8,
  "hit_rate" float8,
  "total_keys" int4,
  "create_time" timestamp NOT NULL,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_monitor_redis_log"."id" IS '雪花ID';
COMMENT ON COLUMN "sys_monitor_redis_log"."connected_clients" IS '已连接客户端数';
COMMENT ON COLUMN "sys_monitor_redis_log"."used_memory" IS 'Redis 内存使用（字节）';
COMMENT ON COLUMN "sys_monitor_redis_log"."hit_rate" IS '缓存命中率（0~100）';
COMMENT ON COLUMN "sys_monitor_redis_log"."total_keys" IS 'Key 总数';
COMMENT ON COLUMN "sys_monitor_redis_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_redis_log" IS '系统监控-Redis 指标日志';

CREATE INDEX "idx_smrl_create_time" ON "sys_monitor_redis_log" ("create_time");


CREATE TABLE "sys_monitor_db_log" (
  "id" int8 NOT NULL,
  "active_connections" int4,
  "create_time" timestamp NOT NULL,
  PRIMARY KEY ("id")
);

COMMENT ON COLUMN "sys_monitor_db_log"."id" IS '雪花ID';
COMMENT ON COLUMN "sys_monitor_db_log"."active_connections" IS '活跃连接数';
COMMENT ON COLUMN "sys_monitor_db_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_db_log" IS '系统监控-数据库指标日志';

CREATE INDEX "idx_smdbl_create_time" ON "sys_monitor_db_log" ("create_time");
