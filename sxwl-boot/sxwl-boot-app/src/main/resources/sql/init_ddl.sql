/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1-postgres
 Source Server Type    : PostgreSQL
 Source Server Version : 180004 (180004)
 Source Host           : 127.0.0.1:30013
 Source Catalog        : sxwl_project
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 180004 (180004)
 File Encoding         : 65001

 Date: 24/08/2026 17:16:17
*/


-- ----------------------------
-- Table structure for sys_codegen_field_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_codegen_field_info";
CREATE TABLE "sys_codegen_field_info" (
  "id" int8 NOT NULL,
  "table_id" int8 NOT NULL,
  "column_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "column_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "column_comment" varchar(200) COLLATE "pg_catalog"."default",
  "java_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "java_field_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "is_pk" int2 NOT NULL DEFAULT 0,
  "is_insert" int2 NOT NULL DEFAULT 1,
  "is_edit" int2 NOT NULL DEFAULT 1,
  "is_list" int2 NOT NULL DEFAULT 1,
  "is_query" int2 NOT NULL DEFAULT 0,
  "query_type" varchar(16) COLLATE "pg_catalog"."default",
  "query_form_type" varchar(32) COLLATE "pg_catalog"."default",
  "form_type" varchar(32) COLLATE "pg_catalog"."default",
  "form_dict_code" varchar(64) COLLATE "pg_catalog"."default",
  "is_required" int2 NOT NULL DEFAULT 0,
  "is_unique" int2 NOT NULL DEFAULT 0,
  "max_length" int4,
  "sort" int4 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_codegen_table_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_codegen_table_info";
CREATE TABLE "sys_codegen_table_info" (
  "id" int8 NOT NULL,
  "table_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "module_prefix" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "biz_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "biz_name_cn" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "biz_name_plural" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "table_comment" varchar(200) COLLATE "pg_catalog"."default",
  "package_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "author" varchar(64) COLLATE "pg_catalog"."default",
  "gen_type" varchar(16) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'crud'::character varying,
  "status" int2 NOT NULL DEFAULT 1,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_config_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_config_info";
CREATE TABLE "sys_config_info" (
  "id" int8 NOT NULL,
  "config_key" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "config_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "config_value" text COLLATE "pg_catalog"."default" NOT NULL,
  "config_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'system'::character varying,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_dict_detail_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_dict_detail_info";
CREATE TABLE "sys_dict_detail_info" (
  "id" int8 NOT NULL,
  "dict_id" int8 NOT NULL,
  "detail_value" varchar(4) COLLATE "pg_catalog"."default" NOT NULL,
  "detail_label" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "is_default" int2 NOT NULL DEFAULT 0,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_dict_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_dict_info";
CREATE TABLE "sys_dict_info" (
  "id" int8 NOT NULL,
  "dict_code" varchar(2) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_file_chunk_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_file_chunk_info";
CREATE TABLE "sys_file_chunk_info" (
  "id" int8 NOT NULL,
  "upload_id" int8 NOT NULL,
  "chunk_index" int4 NOT NULL,
  "chunk_md5" varchar(64) COLLATE "pg_catalog"."default",
  "object_key" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "chunk_size" int8 NOT NULL,
  "status" int2 NOT NULL DEFAULT 0,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_file_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_file_info";
CREATE TABLE "sys_file_info" (
  "id" int8 NOT NULL,
  "file_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "object_key" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "file_url" text COLLATE "pg_catalog"."default",
  "file_size" int8 NOT NULL,
  "file_type" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "file_suffix" varchar(16) COLLATE "pg_catalog"."default",
  "bucket_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "md5" varchar(64) COLLATE "pg_catalog"."default",
  "business_type" varchar(32) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_file_session_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_file_session_info";
CREATE TABLE "sys_file_session_info" (
  "id" int8 NOT NULL,
  "file_md5" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "original_name" varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
  "file_size" int8 NOT NULL,
  "content_type" varchar(100) COLLATE "pg_catalog"."default",
  "total_chunks" int4 NOT NULL,
  "chunk_size" int4 NOT NULL,
  "status" int2 NOT NULL DEFAULT 0,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_job_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_job_info";
CREATE TABLE "sys_job_info" (
  "id" int8 NOT NULL,
  "job_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "job_group" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'DEFAULT'::character varying,
  "class_name" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "method_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "method_params" varchar(500) COLLATE "pg_catalog"."default",
  "cron_expression" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_job_log_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_job_log_info";
CREATE TABLE "sys_job_log_info" (
  "id" int8 NOT NULL,
  "job_id" int8 NOT NULL,
  "job_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "job_group" varchar(64) COLLATE "pg_catalog"."default",
  "class_name" varchar(256) COLLATE "pg_catalog"."default",
  "method_name" varchar(64) COLLATE "pg_catalog"."default",
  "method_params" varchar(500) COLLATE "pg_catalog"."default",
  "cron_expression" varchar(64) COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "execute_time" int8,
  "error_msg" text COLLATE "pg_catalog"."default",
  "fire_time" timestamp(6),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_log_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_log_info";
CREATE TABLE "sys_log_info" (
  "id" int8 NOT NULL,
  "log_type" int2 NOT NULL,
  "title" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(500) COLLATE "pg_catalog"."default",
  "method" varchar(128) COLLATE "pg_catalog"."default",
  "request_url" varchar(256) COLLATE "pg_catalog"."default",
  "request_method" varchar(10) COLLATE "pg_catalog"."default",
  "request_param" text COLLATE "pg_catalog"."default",
  "response_result" text COLLATE "pg_catalog"."default",
  "operate_ip" varchar(64) COLLATE "pg_catalog"."default",
  "operate_location" varchar(128) COLLATE "pg_catalog"."default",
  "user_id" int8,
  "user_name" varchar(64) COLLATE "pg_catalog"."default",
  "execute_time" int8,
  "error_msg" text COLLATE "pg_catalog"."default",
  "status" int2 NOT NULL DEFAULT 1,
  "trace_id" varchar(64) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0,
  "user_agent" text COLLATE "pg_catalog"."default",
  "browser" varchar(32) COLLATE "pg_catalog"."default",
  "os" varchar(32) COLLATE "pg_catalog"."default",
  "diff" text COLLATE "pg_catalog"."default"
)
;
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
COMMENT ON COLUMN "sys_log_info"."user_agent" IS '原始User-Agent字符串';
COMMENT ON COLUMN "sys_log_info"."browser" IS '浏览器，如 Chrome/Edge/Firefox';
COMMENT ON COLUMN "sys_log_info"."os" IS '操作系统，如 Windows/macOS/Android/iOS';
COMMENT ON COLUMN "sys_log_info"."diff" IS '字段级变更差异 JSON（如：[{"field":"角色","oldValue":"admin","newValue":"user"}]）';
COMMENT ON TABLE "sys_log_info" IS '系统日志信息表';

-- ----------------------------
-- Table structure for sys_menu_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_menu_info";
CREATE TABLE "sys_menu_info" (
  "id" int8 NOT NULL,
  "menu_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "ancestors" varchar(256) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0'::character varying,
  "menu_type" int2 NOT NULL,
  "path" varchar(128) COLLATE "pg_catalog"."default",
  "component" varchar(128) COLLATE "pg_catalog"."default",
  "perms" varchar(64) COLLATE "pg_catalog"."default",
  "icon" varchar(64) COLLATE "pg_catalog"."default",
  "is_frame" int2 NOT NULL DEFAULT 0,
  "is_cache" int2 NOT NULL DEFAULT 0,
  "sort" int4 NOT NULL DEFAULT 0,
  "visible" int2 NOT NULL DEFAULT 1,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_monitor_db_log
-- ----------------------------
DROP TABLE IF EXISTS "sys_monitor_db_log";
CREATE TABLE "sys_monitor_db_log" (
  "id" int8 NOT NULL,
  "active_connections" int4,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "sys_monitor_db_log"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_monitor_db_log"."active_connections" IS '活跃连接数';
COMMENT ON COLUMN "sys_monitor_db_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_db_log" IS '系统监控-数据库指标日志';

-- ----------------------------
-- Table structure for sys_monitor_jvm_log
-- ----------------------------
DROP TABLE IF EXISTS "sys_monitor_jvm_log";
CREATE TABLE "sys_monitor_jvm_log" (
  "id" int8 NOT NULL,
  "heap_used" int8,
  "heap_max" int8,
  "heap_committed" int8,
  "thread_count" int4,
  "peak_thread_count" int4,
  "class_loaded_count" int4,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "sys_monitor_jvm_log"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_monitor_jvm_log"."heap_used" IS '堆内存已用（字节）';
COMMENT ON COLUMN "sys_monitor_jvm_log"."heap_max" IS '堆内存最大值（字节）';
COMMENT ON COLUMN "sys_monitor_jvm_log"."heap_committed" IS '堆内存提交值（字节）';
COMMENT ON COLUMN "sys_monitor_jvm_log"."thread_count" IS '当前线程数';
COMMENT ON COLUMN "sys_monitor_jvm_log"."peak_thread_count" IS '峰值线程数';
COMMENT ON COLUMN "sys_monitor_jvm_log"."class_loaded_count" IS '已加载类数';
COMMENT ON COLUMN "sys_monitor_jvm_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_jvm_log" IS '系统监控-JVM 指标日志';

-- ----------------------------
-- Table structure for sys_monitor_redis_log
-- ----------------------------
DROP TABLE IF EXISTS "sys_monitor_redis_log";
CREATE TABLE "sys_monitor_redis_log" (
  "id" int8 NOT NULL,
  "connected_clients" int4,
  "used_memory" int8,
  "hit_rate" float8,
  "total_keys" int4,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "sys_monitor_redis_log"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_monitor_redis_log"."connected_clients" IS '已连接客户端数';
COMMENT ON COLUMN "sys_monitor_redis_log"."used_memory" IS 'Redis 内存使用（字节）';
COMMENT ON COLUMN "sys_monitor_redis_log"."hit_rate" IS '缓存命中率（0~100）';
COMMENT ON COLUMN "sys_monitor_redis_log"."total_keys" IS 'Key 总数';
COMMENT ON COLUMN "sys_monitor_redis_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_redis_log" IS '系统监控-Redis 指标日志';

-- ----------------------------
-- Table structure for sys_monitor_server_log
-- ----------------------------
DROP TABLE IF EXISTS "sys_monitor_server_log";
CREATE TABLE "sys_monitor_server_log" (
  "id" int8 NOT NULL,
  "cpu_load" float8,
  "mem_used" int8,
  "mem_total" int8,
  "disk_used" int8,
  "disk_total" int8,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "sys_monitor_server_log"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_monitor_server_log"."cpu_load" IS 'CPU 负载百分比（0~100）';
COMMENT ON COLUMN "sys_monitor_server_log"."mem_used" IS '已用内存（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."mem_total" IS '总内存（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."disk_used" IS '已用磁盘（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."disk_total" IS '总磁盘（字节）';
COMMENT ON COLUMN "sys_monitor_server_log"."create_time" IS '记录时间';
COMMENT ON TABLE "sys_monitor_server_log" IS '系统监控-服务器指标日志';

-- ----------------------------
-- Table structure for sys_notice_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_notice_info";
CREATE TABLE "sys_notice_info" (
  "id" int8 NOT NULL,
  "title" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "notice_type" varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'notice'::character varying,
  "level" varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'info'::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "publish_time" timestamp(6),
  "expire_time" timestamp(6),
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_notice_read
-- ----------------------------
DROP TABLE IF EXISTS "sys_notice_read";
CREATE TABLE "sys_notice_read" (
  "id" int8 NOT NULL,
  "notice_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "read_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "sys_notice_read"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_notice_read"."notice_id" IS '公告ID，关联 sys_notice_info.id';
COMMENT ON COLUMN "sys_notice_read"."user_id" IS '用户ID，关联 sys_user_info.id';
COMMENT ON COLUMN "sys_notice_read"."read_time" IS '阅读时间';
COMMENT ON TABLE "sys_notice_read" IS '通知公告已读状态表';

-- ----------------------------
-- Table structure for sys_organization_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_organization_info";
CREATE TABLE "sys_organization_info" (
  "id" int8 NOT NULL,
  "org_code" varchar(4) COLLATE "pg_catalog"."default" NOT NULL,
  "org_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_id" int8 NOT NULL DEFAULT 0,
  "ancestors" varchar(256) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0'::character varying,
  "org_level" int2 NOT NULL,
  "org_type" varchar(4) COLLATE "pg_catalog"."default",
  "leader_id" int8,
  "phone" varchar(20) COLLATE "pg_catalog"."default",
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_position_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_position_info";
CREATE TABLE "sys_position_info" (
  "id" int8 NOT NULL,
  "position_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "position_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_role_data_scope_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_role_data_scope_info";
CREATE TABLE "sys_role_data_scope_info" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "org_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "sys_role_data_scope_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_role_data_scope_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "sys_role_data_scope_info"."org_id" IS '授权可见的组织ID（关联 sys_organization_info.id）';
COMMENT ON COLUMN "sys_role_data_scope_info"."create_by" IS '创建人标识（谁配置的数据权限）';
COMMENT ON COLUMN "sys_role_data_scope_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_role_data_scope_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role_data_scope_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_role_data_scope_info" IS '角色数据权限信息表（仅 sys_role_info.data_scope=5 时生效）';

-- ----------------------------
-- Table structure for sys_role_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_role_info";
CREATE TABLE "sys_role_info" (
  "id" int8 NOT NULL,
  "role_code" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "data_scope" int2 NOT NULL DEFAULT 4,
  "sort" int4 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_role_menu_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_role_menu_info";
CREATE TABLE "sys_role_menu_info" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "menu_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "sys_role_menu_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_role_menu_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "sys_role_menu_info"."menu_id" IS '菜单ID（关联 sys_menu_info.id）';
COMMENT ON COLUMN "sys_role_menu_info"."create_by" IS '创建人标识（谁分配的菜单）';
COMMENT ON COLUMN "sys_role_menu_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_role_menu_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_role_menu_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_role_menu_info" IS '角色菜单信息表';

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_user_info";
CREATE TABLE "sys_user_info" (
  "id" int8 NOT NULL,
  "username" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
  "real_name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "nickname" varchar(64) COLLATE "pg_catalog"."default",
  "phone" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "email" varchar(128) COLLATE "pg_catalog"."default",
  "avatar" text COLLATE "pg_catalog"."default",
  "gender" int2 NOT NULL DEFAULT 0,
  "status" int2 NOT NULL DEFAULT 1,
  "error_count" int2 NOT NULL DEFAULT 0,
  "lock_time" timestamp(6),
  "last_login_time" timestamp(6),
  "last_login_ip" varchar(64) COLLATE "pg_catalog"."default",
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "update_by" int8,
  "update_time" timestamp(6),
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
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

-- ----------------------------
-- Table structure for sys_user_organization_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_user_organization_info";
CREATE TABLE "sys_user_organization_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "org_id" int8 NOT NULL,
  "is_main" int2 NOT NULL DEFAULT 0,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "sys_user_organization_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_organization_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_user_organization_info"."org_id" IS '组织ID（关联 sys_organization_info.id）';
COMMENT ON COLUMN "sys_user_organization_info"."is_main" IS '是否主组织：0=否 1=是（每个用户仅一个主组织）';
COMMENT ON COLUMN "sys_user_organization_info"."create_by" IS '创建人标识（谁分配的组织）';
COMMENT ON COLUMN "sys_user_organization_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_organization_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_organization_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_organization_info" IS '用户组织信息表';

-- ----------------------------
-- Table structure for sys_user_position_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_user_position_info";
CREATE TABLE "sys_user_position_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "position_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "sys_user_position_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_position_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_user_position_info"."position_id" IS '岗位ID（关联 sys_position_info.id）';
COMMENT ON COLUMN "sys_user_position_info"."create_by" IS '创建人标识（谁分配的岗位）';
COMMENT ON COLUMN "sys_user_position_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_position_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_position_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_position_info" IS '用户岗位信息表';

-- ----------------------------
-- Table structure for sys_user_role_info
-- ----------------------------
DROP TABLE IF EXISTS "sys_user_role_info";
CREATE TABLE "sys_user_role_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "sys_user_role_info"."id" IS '唯一标识';
COMMENT ON COLUMN "sys_user_role_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "sys_user_role_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "sys_user_role_info"."create_by" IS '创建人标识（谁分配的角色）';
COMMENT ON COLUMN "sys_user_role_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "sys_user_role_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_user_role_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "sys_user_role_info" IS '用户角色信息表';

-- ----------------------------
-- Indexes structure for table sys_codegen_field_info
-- ----------------------------
CREATE INDEX "idx_sys_codegen_field_sort" ON "sys_codegen_field_info" USING btree (
  "table_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "sort" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_codegen_field_table_id" ON "sys_codegen_field_info" USING btree (
  "table_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_codegen_field_info
-- ----------------------------
ALTER TABLE "sys_codegen_field_info" ADD CONSTRAINT "sys_codegen_field_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_codegen_table_info
-- ----------------------------
CREATE INDEX "idx_sys_codegen_table_create_time" ON "sys_codegen_table_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_codegen_table_status" ON "sys_codegen_table_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_codegen_table_name" ON "sys_codegen_table_info" USING btree (
  "table_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_codegen_table_info
-- ----------------------------
ALTER TABLE "sys_codegen_table_info" ADD CONSTRAINT "sys_codegen_table_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_config_info
-- ----------------------------
CREATE INDEX "idx_sys_config_status" ON "sys_config_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_config_type" ON "sys_config_info" USING btree (
  "config_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_config_key" ON "sys_config_info" USING btree (
  "config_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_config_info
-- ----------------------------
ALTER TABLE "sys_config_info" ADD CONSTRAINT "sys_config_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_dict_detail_info
-- ----------------------------
CREATE INDEX "idx_sys_dict_detail_dict_id" ON "sys_dict_detail_info" USING btree (
  "dict_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_dict_detail_default" ON "sys_dict_detail_info" USING btree (
  "dict_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE is_default = 1 AND delete_flag = 0;
CREATE UNIQUE INDEX "uk_sys_dict_detail_value" ON "sys_dict_detail_info" USING btree (
  "detail_value" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_dict_detail_info
-- ----------------------------
ALTER TABLE "sys_dict_detail_info" ADD CONSTRAINT "sys_dict_detail_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_dict_info
-- ----------------------------
CREATE INDEX "idx_sys_dict_info_status" ON "sys_dict_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_dict_info_code" ON "sys_dict_info" USING btree (
  "dict_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_dict_info
-- ----------------------------
ALTER TABLE "sys_dict_info" ADD CONSTRAINT "sys_dict_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_file_chunk_info
-- ----------------------------
CREATE INDEX "idx_sys_file_chunk_upload" ON "sys_file_chunk_info" USING btree (
  "upload_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_file_chunk" ON "sys_file_chunk_info" USING btree (
  "upload_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "chunk_index" "pg_catalog"."int4_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_file_chunk_info
-- ----------------------------
ALTER TABLE "sys_file_chunk_info" ADD CONSTRAINT "sys_file_chunk_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_file_info
-- ----------------------------
CREATE INDEX "idx_sys_file_business_type" ON "sys_file_info" USING btree (
  "business_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_create_by" ON "sys_file_info" USING btree (
  "create_by" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_create_time" ON "sys_file_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_md5" ON "sys_file_info" USING btree (
  "file_size" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "md5" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_status" ON "sys_file_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_file_info
-- ----------------------------
ALTER TABLE "sys_file_info" ADD CONSTRAINT "sys_file_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_file_session_info
-- ----------------------------
CREATE INDEX "idx_sys_file_session_md5" ON "sys_file_session_info" USING btree (
  "file_md5" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_session_status" ON "sys_file_session_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_file_session_info
-- ----------------------------
ALTER TABLE "sys_file_session_info" ADD CONSTRAINT "sys_file_session_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_job_info
-- ----------------------------
CREATE INDEX "idx_sys_job_status" ON "sys_job_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_job_name_group" ON "sys_job_info" USING btree (
  "job_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_job_info
-- ----------------------------
ALTER TABLE "sys_job_info" ADD CONSTRAINT "sys_job_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_job_log_info
-- ----------------------------
CREATE INDEX "idx_sys_job_log_create_time" ON "sys_job_log_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_job_log_job_id" ON "sys_job_log_info" USING btree (
  "job_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_job_log_status" ON "sys_job_log_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_job_log_info
-- ----------------------------
ALTER TABLE "sys_job_log_info" ADD CONSTRAINT "sys_job_log_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_log_info
-- ----------------------------
CREATE INDEX "idx_sys_log_create_time" ON "sys_log_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_status" ON "sys_log_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_trace_id" ON "sys_log_info" USING btree (
  "trace_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_type" ON "sys_log_info" USING btree (
  "log_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_user_id" ON "sys_log_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_log_info
-- ----------------------------
ALTER TABLE "sys_log_info" ADD CONSTRAINT "sys_log_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_menu_info
-- ----------------------------
CREATE INDEX "idx_sys_menu_ancestors" ON "sys_menu_info" USING btree (
  "ancestors" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_menu_parent_id" ON "sys_menu_info" USING btree (
  "parent_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_menu_status" ON "sys_menu_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_menu_perms" ON "sys_menu_info" USING btree (
  "perms" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0 AND perms IS NOT NULL;

-- ----------------------------
-- Primary Key structure for table sys_menu_info
-- ----------------------------
ALTER TABLE "sys_menu_info" ADD CONSTRAINT "sys_menu_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_db_log
-- ----------------------------
CREATE INDEX "idx_smdbl_create_time" ON "sys_monitor_db_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_db_log
-- ----------------------------
ALTER TABLE "sys_monitor_db_log" ADD CONSTRAINT "sys_monitor_db_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_jvm_log
-- ----------------------------
CREATE INDEX "idx_smjvl_create_time" ON "sys_monitor_jvm_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_jvm_log
-- ----------------------------
ALTER TABLE "sys_monitor_jvm_log" ADD CONSTRAINT "sys_monitor_jvm_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_redis_log
-- ----------------------------
CREATE INDEX "idx_smrl_create_time" ON "sys_monitor_redis_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_redis_log
-- ----------------------------
ALTER TABLE "sys_monitor_redis_log" ADD CONSTRAINT "sys_monitor_redis_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_server_log
-- ----------------------------
CREATE INDEX "idx_smsl_create_time" ON "sys_monitor_server_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_server_log
-- ----------------------------
ALTER TABLE "sys_monitor_server_log" ADD CONSTRAINT "sys_monitor_server_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_notice_info
-- ----------------------------
CREATE INDEX "idx_sys_notice_create_time" ON "sys_notice_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_level" ON "sys_notice_info" USING btree (
  "level" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_status" ON "sys_notice_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_type" ON "sys_notice_info" USING btree (
  "notice_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_notice_info
-- ----------------------------
ALTER TABLE "sys_notice_info" ADD CONSTRAINT "sys_notice_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_notice_read
-- ----------------------------
CREATE UNIQUE INDEX "idx_sys_notice_read_uid_nid" ON "sys_notice_read" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "notice_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_read_user_id" ON "sys_notice_read" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_notice_read
-- ----------------------------
ALTER TABLE "sys_notice_read" ADD CONSTRAINT "sys_notice_read_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_organization_info
-- ----------------------------
CREATE INDEX "idx_sys_org_ancestors" ON "sys_organization_info" USING btree (
  "ancestors" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_org_leader_id" ON "sys_organization_info" USING btree (
  "leader_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_org_parent_id" ON "sys_organization_info" USING btree (
  "parent_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_org_status" ON "sys_organization_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_org_code" ON "sys_organization_info" USING btree (
  "org_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_organization_info
-- ----------------------------
ALTER TABLE "sys_organization_info" ADD CONSTRAINT "sys_organization_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_position_info
-- ----------------------------
CREATE INDEX "idx_sys_position_status" ON "sys_position_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_position_code" ON "sys_position_info" USING btree (
  "position_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_position_info
-- ----------------------------
ALTER TABLE "sys_position_info" ADD CONSTRAINT "sys_position_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_role_data_scope_info
-- ----------------------------
CREATE INDEX "idx_sys_role_data_scope_org" ON "sys_role_data_scope_info" USING btree (
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_role_data_scope" ON "sys_role_data_scope_info" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_role_data_scope_info
-- ----------------------------
ALTER TABLE "sys_role_data_scope_info" ADD CONSTRAINT "sys_role_data_scope_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_role_info
-- ----------------------------
CREATE INDEX "idx_sys_role_data_scope" ON "sys_role_info" USING btree (
  "data_scope" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_role_status" ON "sys_role_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_role_code" ON "sys_role_info" USING btree (
  "role_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_role_info
-- ----------------------------
ALTER TABLE "sys_role_info" ADD CONSTRAINT "sys_role_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_role_menu_info
-- ----------------------------
CREATE INDEX "idx_sys_role_menu_menu" ON "sys_role_menu_info" USING btree (
  "menu_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_role_menu" ON "sys_role_menu_info" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "menu_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_role_menu_info
-- ----------------------------
ALTER TABLE "sys_role_menu_info" ADD CONSTRAINT "sys_role_menu_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_info
-- ----------------------------
CREATE INDEX "idx_sys_user_create_time" ON "sys_user_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_user_status" ON "sys_user_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_phone" ON "sys_user_info" USING btree (
  "phone" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;
CREATE UNIQUE INDEX "uk_sys_user_username" ON "sys_user_info" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_info
-- ----------------------------
ALTER TABLE "sys_user_info" ADD CONSTRAINT "sys_user_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_organization_info
-- ----------------------------
CREATE INDEX "idx_sys_user_org_org" ON "sys_user_organization_info" USING btree (
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_org" ON "sys_user_organization_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;
CREATE UNIQUE INDEX "uk_sys_user_org_main" ON "sys_user_organization_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE is_main = 1 AND delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_organization_info
-- ----------------------------
ALTER TABLE "sys_user_organization_info" ADD CONSTRAINT "sys_user_organization_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_position_info
-- ----------------------------
CREATE INDEX "idx_sys_user_position_position" ON "sys_user_position_info" USING btree (
  "position_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_position" ON "sys_user_position_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "position_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_position_info
-- ----------------------------
ALTER TABLE "sys_user_position_info" ADD CONSTRAINT "sys_user_position_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_role_info
-- ----------------------------
CREATE INDEX "idx_sys_user_role_role" ON "sys_user_role_info" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_role" ON "sys_user_role_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_role_info
-- ----------------------------
ALTER TABLE "sys_user_role_info" ADD CONSTRAINT "sys_user_role_info_pkey" PRIMARY KEY ("id");
