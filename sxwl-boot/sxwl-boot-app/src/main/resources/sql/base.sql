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
COMMENT ON COLUMN "sys_menu_info"."component" IS '前端组件路径，如 system/user/index（菜单用，目录/按钮为空）';
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
