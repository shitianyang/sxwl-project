-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_blob_triggers";
CREATE TABLE "public"."qrtz_blob_triggers" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "blob_data" bytea
)
;
COMMENT ON COLUMN "public"."qrtz_blob_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_blob_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "public"."qrtz_blob_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "public"."qrtz_blob_triggers"."blob_data" IS 'BLOB 触发数据（二进制）';
COMMENT ON TABLE "public"."qrtz_blob_triggers" IS 'Quartz BLOB 触发器表';

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_calendars";
CREATE TABLE "public"."qrtz_calendars" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "calendar_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "calendar" bytea NOT NULL
)
;
COMMENT ON COLUMN "public"."qrtz_calendars"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_calendars"."calendar_name" IS '日历名称';
COMMENT ON COLUMN "public"."qrtz_calendars"."calendar" IS '日历数据（二进制）';
COMMENT ON TABLE "public"."qrtz_calendars" IS 'Quartz 日历表';

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_cron_triggers";
CREATE TABLE "public"."qrtz_cron_triggers" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "cron_expression" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "time_zone_id" varchar(80) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."qrtz_cron_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_cron_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "public"."qrtz_cron_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "public"."qrtz_cron_triggers"."cron_expression" IS 'Cron 表达式';
COMMENT ON COLUMN "public"."qrtz_cron_triggers"."time_zone_id" IS '时区ID';
COMMENT ON TABLE "public"."qrtz_cron_triggers" IS 'Quartz Cron 触发器表';

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_fired_triggers";
CREATE TABLE "public"."qrtz_fired_triggers" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "entry_id" varchar(95) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "instance_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "fired_time" int8 NOT NULL,
  "sched_time" int8 NOT NULL,
  "priority" int4 NOT NULL,
  "state" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "job_name" varchar(200) COLLATE "pg_catalog"."default",
  "job_group" varchar(200) COLLATE "pg_catalog"."default",
  "is_nonconcurrent" bool,
  "requests_recovery" bool
)
;
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."entry_id" IS '调度条目ID';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."instance_name" IS '调度器实例名称';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."fired_time" IS '触发时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."sched_time" IS '预定调度时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."priority" IS '优先级';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."state" IS '触发器状态';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."job_name" IS '任务名称';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."job_group" IS '任务分组';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."is_nonconcurrent" IS '是否禁止并发执行';
COMMENT ON COLUMN "public"."qrtz_fired_triggers"."requests_recovery" IS '是否请求恢复';
COMMENT ON TABLE "public"."qrtz_fired_triggers" IS 'Quartz 已触发触发器表';

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_job_details";
CREATE TABLE "public"."qrtz_job_details" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "job_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "job_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(250) COLLATE "pg_catalog"."default",
  "job_class_name" varchar(250) COLLATE "pg_catalog"."default" NOT NULL,
  "is_durable" bool NOT NULL,
  "is_nonconcurrent" bool NOT NULL,
  "is_update_data" bool NOT NULL,
  "requests_recovery" bool NOT NULL,
  "job_data" bytea
)
;
COMMENT ON COLUMN "public"."qrtz_job_details"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_job_details"."job_name" IS '任务名称';
COMMENT ON COLUMN "public"."qrtz_job_details"."job_group" IS '任务分组';
COMMENT ON COLUMN "public"."qrtz_job_details"."description" IS '任务描述';
COMMENT ON COLUMN "public"."qrtz_job_details"."job_class_name" IS '任务实现类全限定名';
COMMENT ON COLUMN "public"."qrtz_job_details"."is_durable" IS '是否持久化（任务完成后是否保留）';
COMMENT ON COLUMN "public"."qrtz_job_details"."is_nonconcurrent" IS '是否禁止并发执行';
COMMENT ON COLUMN "public"."qrtz_job_details"."is_update_data" IS '每次执行是否更新JobData';
COMMENT ON COLUMN "public"."qrtz_job_details"."requests_recovery" IS '调度器异常重启后是否请求恢复';
COMMENT ON COLUMN "public"."qrtz_job_details"."job_data" IS '任务数据（二进制）';
COMMENT ON TABLE "public"."qrtz_job_details" IS 'Quartz 任务详情表';

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_locks";
CREATE TABLE "public"."qrtz_locks" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "lock_name" varchar(40) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."qrtz_locks"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_locks"."lock_name" IS '锁名称';
COMMENT ON TABLE "public"."qrtz_locks" IS 'Quartz 锁表';

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_paused_trigger_grps";
CREATE TABLE "public"."qrtz_paused_trigger_grps" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."qrtz_paused_trigger_grps"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_paused_trigger_grps"."trigger_group" IS '已暂停的触发器分组';
COMMENT ON TABLE "public"."qrtz_paused_trigger_grps" IS 'Quartz 暂停触发器组表';

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_scheduler_state";
CREATE TABLE "public"."qrtz_scheduler_state" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "instance_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "last_checkin_time" int8 NOT NULL,
  "checkin_interval" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."qrtz_scheduler_state"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_scheduler_state"."instance_name" IS '调度器实例名称';
COMMENT ON COLUMN "public"."qrtz_scheduler_state"."last_checkin_time" IS '最后检入时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_scheduler_state"."checkin_interval" IS '检入间隔（毫秒）';
COMMENT ON TABLE "public"."qrtz_scheduler_state" IS 'Quartz 调度器状态表';

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_simple_triggers";
CREATE TABLE "public"."qrtz_simple_triggers" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "repeat_count" int8 NOT NULL,
  "repeat_interval" int8 NOT NULL,
  "times_triggered" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."qrtz_simple_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_simple_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "public"."qrtz_simple_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "public"."qrtz_simple_triggers"."repeat_count" IS '重复次数（0=不重复，-1=无限重复）';
COMMENT ON COLUMN "public"."qrtz_simple_triggers"."repeat_interval" IS '重复间隔（毫秒）';
COMMENT ON COLUMN "public"."qrtz_simple_triggers"."times_triggered" IS '已触发次数';
COMMENT ON TABLE "public"."qrtz_simple_triggers" IS 'Quartz 简单触发器表';

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_simprop_triggers";
CREATE TABLE "public"."qrtz_simprop_triggers" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "str_prop_1" varchar(512) COLLATE "pg_catalog"."default",
  "str_prop_2" varchar(512) COLLATE "pg_catalog"."default",
  "str_prop_3" varchar(512) COLLATE "pg_catalog"."default",
  "int_prop_1" int4,
  "int_prop_2" int4,
  "long_prop_1" int8,
  "long_prop_2" int8,
  "dec_prop_1" numeric(13,4),
  "dec_prop_2" numeric(13,4),
  "bool_prop_1" bool,
  "bool_prop_2" bool,
  "time_zone_id" varchar(80) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."str_prop_1" IS '字符串属性1';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."str_prop_2" IS '字符串属性2';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."str_prop_3" IS '字符串属性3';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."int_prop_1" IS '整型属性1';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."int_prop_2" IS '整型属性2';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."long_prop_1" IS '长整型属性1';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."long_prop_2" IS '长整型属性2';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."dec_prop_1" IS '十进制属性1';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."dec_prop_2" IS '十进制属性2';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."bool_prop_1" IS '布尔属性1';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."bool_prop_2" IS '布尔属性2';
COMMENT ON COLUMN "public"."qrtz_simprop_triggers"."time_zone_id" IS '时区ID';
COMMENT ON TABLE "public"."qrtz_simprop_triggers" IS 'Quartz 简化属性触发器表';

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS "public"."qrtz_triggers";
CREATE TABLE "public"."qrtz_triggers" (
  "sched_name" varchar(120) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "job_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "job_group" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(250) COLLATE "pg_catalog"."default",
  "next_fire_time" int8,
  "prev_fire_time" int8,
  "priority" int4,
  "trigger_state" varchar(16) COLLATE "pg_catalog"."default" NOT NULL,
  "trigger_type" varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
  "start_time" int8 NOT NULL,
  "end_time" int8,
  "calendar_name" varchar(200) COLLATE "pg_catalog"."default",
  "misfire_instr" int2,
  "job_data" bytea
)
;
COMMENT ON COLUMN "public"."qrtz_triggers"."sched_name" IS '调度器名称';
COMMENT ON COLUMN "public"."qrtz_triggers"."trigger_name" IS '触发器名称';
COMMENT ON COLUMN "public"."qrtz_triggers"."trigger_group" IS '触发器分组';
COMMENT ON COLUMN "public"."qrtz_triggers"."job_name" IS '关联任务名称';
COMMENT ON COLUMN "public"."qrtz_triggers"."job_group" IS '关联任务分组';
COMMENT ON COLUMN "public"."qrtz_triggers"."description" IS '触发器描述';
COMMENT ON COLUMN "public"."qrtz_triggers"."next_fire_time" IS '下次触发时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_triggers"."prev_fire_time" IS '上次触发时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_triggers"."priority" IS '优先级';
COMMENT ON COLUMN "public"."qrtz_triggers"."trigger_state" IS '触发器状态（WAITING/PAUSED/ACQUIRED/BLOCKED/ERROR/COMPLETE）';
COMMENT ON COLUMN "public"."qrtz_triggers"."trigger_type" IS '触发器类型（SIMPLE/CRON/BLOB/CALENDAR/DAILY_TIME_INDICATOR）';
COMMENT ON COLUMN "public"."qrtz_triggers"."start_time" IS '开始时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_triggers"."end_time" IS '结束时间（毫秒时间戳）';
COMMENT ON COLUMN "public"."qrtz_triggers"."calendar_name" IS '日历名称';
COMMENT ON COLUMN "public"."qrtz_triggers"."misfire_instr" IS '失火指令';
COMMENT ON COLUMN "public"."qrtz_triggers"."job_data" IS '任务数据（二进制）';
COMMENT ON TABLE "public"."qrtz_triggers" IS 'Quartz 触发器表';

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for sys_codegen_field_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_codegen_field_info";
CREATE TABLE "public"."sys_codegen_field_info" (
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
COMMENT ON COLUMN "public"."sys_codegen_field_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."table_id" IS '关联表 ID（关联 sys_codegen_table_info.id）';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."column_name" IS 'DB 列名，如 role_code';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."column_type" IS 'DB 类型，如 varchar / int8 / decimal / datetime / text';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."column_comment" IS 'DB 列注释（支持 Markdown 格式）';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."java_type" IS 'Java 类型：String / Long / Integer / LocalDateTime / BigDecimal / Boolean';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."java_field_name" IS 'Java 字段名，如 roleCode';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_pk" IS '是否主键：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_insert" IS '是否在新增时显示：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_edit" IS '是否在编辑时显示：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_list" IS '是否在列表展示：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_query" IS '是否作为查询条件：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."query_type" IS '查询方式：eq / like / between';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."query_form_type" IS '查询表单组件：Input / Select / DateRange';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."form_type" IS '表单组件：Input / Select / TextArea / DatePicker / NumberInput / ImageUpload / Radio / Checkbox';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."form_dict_code" IS 'Select/Radio 关联的字典编码';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_required" IS '是否必填：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."is_unique" IS '是否唯一性校验：0=否 1=是';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."max_length" IS '最大长度';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."sort" IS '排序号';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_codegen_field_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_codegen_field_info" IS '代码生成字段信息配置表';

-- ----------------------------
-- Records of sys_codegen_field_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_codegen_table_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_codegen_table_info";
CREATE TABLE "public"."sys_codegen_table_info" (
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
COMMENT ON COLUMN "public"."sys_codegen_table_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."table_name" IS '待生成的数据库表名，如 sys_role_info';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."module_prefix" IS '模块前缀，如 system';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."biz_name" IS '业务名（英文单数），如 Role';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."biz_name_cn" IS '业务中文名，如 角色';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."biz_name_plural" IS '业务名（英文复数），如 Roles';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."table_comment" IS '表注释（支持 Markdown 格式）';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."package_name" IS '包名，如 com.sxwl.system';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."author" IS '作者';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."gen_type" IS '生成类型：crud / tree';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_codegen_table_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_codegen_table_info" IS '代码生成表信息配置表';

-- ----------------------------
-- Records of sys_codegen_table_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_config_info";
CREATE TABLE "public"."sys_config_info" (
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
COMMENT ON COLUMN "public"."sys_config_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_config_info"."config_key" IS '参数键名，全局唯一，如 sys.siteName、job.backupCron';
COMMENT ON COLUMN "public"."sys_config_info"."config_name" IS '参数名称，如：站点名称、备份定时表达式';
COMMENT ON COLUMN "public"."sys_config_info"."config_value" IS '参数值';
COMMENT ON COLUMN "public"."sys_config_info"."config_type" IS '参数类型：system=系统参数 notice=通知参数 job=任务参数';
COMMENT ON COLUMN "public"."sys_config_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_config_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_config_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_config_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_config_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_config_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_config_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_config_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_config_info" IS '系统参数配置表';

-- ----------------------------
-- Records of sys_config_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_detail_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_detail_info";
CREATE TABLE "public"."sys_dict_detail_info" (
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
COMMENT ON COLUMN "public"."sys_dict_detail_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."dict_id" IS '所属字典ID（关联 sys_dict_info.id）';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."detail_value" IS '字典项值，4位数字，前2位为所属字典code，后2位为序号，如 0101，全局唯一';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."detail_label" IS '字典项标签，前端显示文本，如：男、女';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."description" IS '描述说明，补充解释明细项含义';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."sort" IS '排序号，控制下拉框顺序';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."is_default" IS '是否默认选中：0=否 1=是';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_dict_detail_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_dict_detail_info" IS '字典明细信息表';

-- ----------------------------
-- Records of sys_dict_detail_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_dict_info";
CREATE TABLE "public"."sys_dict_info" (
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
COMMENT ON COLUMN "public"."sys_dict_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_dict_info"."dict_code" IS '字典编码，两位数字 01-99，全局唯一';
COMMENT ON COLUMN "public"."sys_dict_info"."dict_name" IS '字典名称，如：性别、用户状态';
COMMENT ON COLUMN "public"."sys_dict_info"."description" IS '描述说明，字典用途';
COMMENT ON COLUMN "public"."sys_dict_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_dict_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_dict_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_dict_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_dict_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_dict_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_dict_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_dict_info" IS '字典信息表';

-- ----------------------------
-- Records of sys_dict_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file_chunk_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_file_chunk_info";
CREATE TABLE "public"."sys_file_chunk_info" (
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
COMMENT ON COLUMN "public"."sys_file_chunk_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."upload_id" IS '上传会话 ID（关联 sys_file_session_info.id）';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."chunk_index" IS '分片序号，从 0 开始，合并时按此排序';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."chunk_md5" IS '分片 MD5，上传时校验，防止传输损坏';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."object_key" IS 'S3 临时对象键，合并后清理';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."chunk_size" IS '本分片实际大小（最后一片可能小于 chunk_size）';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."status" IS '状态：0=待上传 1=已上传';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_file_chunk_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_file_chunk_info" IS '系统文件分片明细表';

-- ----------------------------
-- Records of sys_file_chunk_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_file_info";
CREATE TABLE "public"."sys_file_info" (
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
COMMENT ON COLUMN "public"."sys_file_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_file_info"."file_name" IS '原始文件名，如：头像.png';
COMMENT ON COLUMN "public"."sys_file_info"."object_key" IS 'RustFS对象键，如 2026/07/02/uuid.png';
COMMENT ON COLUMN "public"."sys_file_info"."file_url" IS '访问URL（冗余，便于前端直接使用）';
COMMENT ON COLUMN "public"."sys_file_info"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "public"."sys_file_info"."file_type" IS '文件MIME类型，如 image/png';
COMMENT ON COLUMN "public"."sys_file_info"."file_suffix" IS '文件后缀，如 png';
COMMENT ON COLUMN "public"."sys_file_info"."bucket_name" IS 'RustFS bucket名，如 sys-file';
COMMENT ON COLUMN "public"."sys_file_info"."md5" IS '文件MD5（秒传/去重用）';
COMMENT ON COLUMN "public"."sys_file_info"."business_type" IS '业务类型，如 avatar、attachment';
COMMENT ON COLUMN "public"."sys_file_info"."status" IS '状态：0=临时 1=正常 2=已删除';
COMMENT ON COLUMN "public"."sys_file_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_file_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_file_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_file_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_file_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_file_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_file_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_file_info" IS '系统文件信息表';

-- ----------------------------
-- Records of sys_file_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_file_session_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_file_session_info";
CREATE TABLE "public"."sys_file_session_info" (
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
COMMENT ON COLUMN "public"."sys_file_session_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_file_session_info"."file_md5" IS '文件 MD5，用于秒传判断和续传查询';
COMMENT ON COLUMN "public"."sys_file_session_info"."original_name" IS '原始文件名，合并后写入 sys_file_info.file_name';
COMMENT ON COLUMN "public"."sys_file_session_info"."file_size" IS '总文件大小（字节）';
COMMENT ON COLUMN "public"."sys_file_session_info"."content_type" IS 'MIME 类型，如 application/zip';
COMMENT ON COLUMN "public"."sys_file_session_info"."total_chunks" IS '总分片数，合并时循环读取 0..total_chunks-1';
COMMENT ON COLUMN "public"."sys_file_session_info"."chunk_size" IS '每个分片的大小（字节），除最后一片外所有分片等大';
COMMENT ON COLUMN "public"."sys_file_session_info"."status" IS '状态：0=上传中 1=已完成 2=已取消';
COMMENT ON COLUMN "public"."sys_file_session_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_file_session_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_file_session_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_file_session_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_file_session_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_file_session_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_file_session_info" IS '系统文件上传会话表';

-- ----------------------------
-- Records of sys_file_session_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_job_info";
CREATE TABLE "public"."sys_job_info" (
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
COMMENT ON COLUMN "public"."sys_job_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_job_info"."job_name" IS '任务名称';
COMMENT ON COLUMN "public"."sys_job_info"."job_group" IS '任务分组';
COMMENT ON COLUMN "public"."sys_job_info"."class_name" IS '调用目标类全限定名';
COMMENT ON COLUMN "public"."sys_job_info"."method_name" IS '调用目标方法名';
COMMENT ON COLUMN "public"."sys_job_info"."method_params" IS '方法参数（JSON）';
COMMENT ON COLUMN "public"."sys_job_info"."cron_expression" IS 'Cron 表达式';
COMMENT ON COLUMN "public"."sys_job_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_job_info"."status" IS '状态：0=暂停 1=正常';
COMMENT ON COLUMN "public"."sys_job_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_job_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_job_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_job_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_job_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_job_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_job_info" IS '定时任务定义表';

-- ----------------------------
-- Records of sys_job_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job_log_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_job_log_info";
CREATE TABLE "public"."sys_job_log_info" (
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
COMMENT ON COLUMN "public"."sys_job_log_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_job_log_info"."job_id" IS '任务 ID（关联 sys_job_info.id）';
COMMENT ON COLUMN "public"."sys_job_log_info"."job_name" IS '任务名称';
COMMENT ON COLUMN "public"."sys_job_log_info"."job_group" IS '任务分组';
COMMENT ON COLUMN "public"."sys_job_log_info"."class_name" IS '调用目标类全限定名';
COMMENT ON COLUMN "public"."sys_job_log_info"."method_name" IS '调用目标方法名';
COMMENT ON COLUMN "public"."sys_job_log_info"."method_params" IS '方法参数（JSON）';
COMMENT ON COLUMN "public"."sys_job_log_info"."cron_expression" IS 'Cron 表达式';
COMMENT ON COLUMN "public"."sys_job_log_info"."status" IS '执行状态：0=失败 1=成功';
COMMENT ON COLUMN "public"."sys_job_log_info"."execute_time" IS '执行耗时（毫秒）';
COMMENT ON COLUMN "public"."sys_job_log_info"."error_msg" IS '错误信息';
COMMENT ON COLUMN "public"."sys_job_log_info"."fire_time" IS '实际执行时间';
COMMENT ON COLUMN "public"."sys_job_log_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_job_log_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_job_log_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_job_log_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_job_log_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_job_log_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_job_log_info" IS '定时任务执行日志表';

-- ----------------------------
-- Records of sys_job_log_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_log_info";
CREATE TABLE "public"."sys_log_info" (
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
COMMENT ON COLUMN "public"."sys_log_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_log_info"."log_type" IS '日志类型：1=登录 2=操作 3=异常 4=安全';
COMMENT ON COLUMN "public"."sys_log_info"."title" IS '模块标题，如：用户管理';
COMMENT ON COLUMN "public"."sys_log_info"."description" IS '操作描述，如：删除用户[zhangsan]';
COMMENT ON COLUMN "public"."sys_log_info"."method" IS '调用方法，如 SysUserController.delete()';
COMMENT ON COLUMN "public"."sys_log_info"."request_url" IS '请求URL，如 /sxwl-api/sys/user/1';
COMMENT ON COLUMN "public"."sys_log_info"."request_method" IS 'HTTP方法：GET/POST/PUT/DELETE';
COMMENT ON COLUMN "public"."sys_log_info"."request_param" IS '请求参数（JSON，应用层截断至2000字符）';
COMMENT ON COLUMN "public"."sys_log_info"."response_result" IS '响应结果（JSON，应用层截断至2000字符）';
COMMENT ON COLUMN "public"."sys_log_info"."operate_ip" IS '操作人IP';
COMMENT ON COLUMN "public"."sys_log_info"."operate_location" IS '操作地点，如：北京市（IP反查）';
COMMENT ON COLUMN "public"."sys_log_info"."user_id" IS '操作人ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "public"."sys_log_info"."user_name" IS '操作人账号（冗余，便于查询）';
COMMENT ON COLUMN "public"."sys_log_info"."execute_time" IS '执行耗时（毫秒）';
COMMENT ON COLUMN "public"."sys_log_info"."error_msg" IS '错误信息（异常日志用）';
COMMENT ON COLUMN "public"."sys_log_info"."status" IS '操作状态：0=失败 1=成功';
COMMENT ON COLUMN "public"."sys_log_info"."trace_id" IS '链路追踪ID（分布式场景串联一次请求的多条日志）';
COMMENT ON COLUMN "public"."sys_log_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_log_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_log_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_log_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON COLUMN "public"."sys_log_info"."user_agent" IS '原始User-Agent字符串';
COMMENT ON COLUMN "public"."sys_log_info"."browser" IS '浏览器，如 Chrome/Edge/Firefox';
COMMENT ON COLUMN "public"."sys_log_info"."os" IS '操作系统，如 Windows/macOS/Android/iOS';
COMMENT ON COLUMN "public"."sys_log_info"."diff" IS '字段级变更差异 JSON（如：[{"field":"角色","oldValue":"admin","newValue":"user"}]）';
COMMENT ON TABLE "public"."sys_log_info" IS '系统日志信息表';

-- ----------------------------
-- Records of sys_log_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_menu_info";
CREATE TABLE "public"."sys_menu_info" (
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
COMMENT ON COLUMN "public"."sys_menu_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_menu_info"."menu_name" IS '菜单名称，如：用户管理、新增用户';
COMMENT ON COLUMN "public"."sys_menu_info"."parent_id" IS '父菜单ID，根菜单为0';
COMMENT ON COLUMN "public"."sys_menu_info"."ancestors" IS '祖先路径，逗号分隔的ID链，如 0,1,2，查询子树用 LIKE ''0,1,%''；前提：ID无前缀冲突（int8长整型，风险极低），改用雪花ID需重新评估LIKE性能';
COMMENT ON COLUMN "public"."sys_menu_info"."menu_type" IS '类型：1=目录 2=菜单 3=按钮';
COMMENT ON COLUMN "public"."sys_menu_info"."path" IS '路由路径，如 user（目录/菜单用，按钮为空）';
COMMENT ON COLUMN "public"."sys_menu_info"."component" IS '前端组件路径（PascalCase），如 System/User（菜单用，目录/按钮为空）';
COMMENT ON COLUMN "public"."sys_menu_info"."perms" IS '权限标识，格式 模块:资源:操作，如 system:user:list（按钮用，目录/菜单可空）';
COMMENT ON COLUMN "public"."sys_menu_info"."icon" IS '菜单图标，如 user';
COMMENT ON COLUMN "public"."sys_menu_info"."is_frame" IS '是否外链：0=内嵌 1=外链（外链时path存完整URL）';
COMMENT ON COLUMN "public"."sys_menu_info"."is_cache" IS '是否缓存：0=不缓存 1=缓存（前端keep-alive，列表页建议缓存）';
COMMENT ON COLUMN "public"."sys_menu_info"."sort" IS '排序号，控制菜单展示顺序';
COMMENT ON COLUMN "public"."sys_menu_info"."visible" IS '是否可见：0=隐藏 1=显示';
COMMENT ON COLUMN "public"."sys_menu_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_menu_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_menu_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_menu_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_menu_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_menu_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_menu_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_menu_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_menu_info" IS '菜单信息表';

-- ----------------------------
-- Records of sys_menu_info
-- ----------------------------
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735172, '用户管理', 192125966745735168, '0,192125966745735168', 2, 'system/user', 'System/User', 'system:user:list', 'user', 0, 1, 1, 1, 1, '用户管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735173, '角色管理', 192125966745735168, '0,192125966745735168', 2, 'system/role', 'System/Role', 'system:role:list', 'role', 0, 1, 2, 1, 1, '角色管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735174, '菜单管理', 192125966745735168, '0,192125966745735168', 2, 'system/menu', 'System/Menu', 'system:menu:list', 'menu', 0, 1, 3, 1, 1, '菜单管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735175, '组织管理', 192125966745735168, '0,192125966745735168', 2, 'system/organization', 'System/Organization', 'system:organization:list', 'organization', 0, 1, 4, 1, 1, '组织管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735176, '岗位管理', 192125966745735168, '0,192125966745735168', 2, 'system/position', 'System/Position', 'system:position:list', 'position', 0, 1, 5, 1, 1, '岗位管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735177, '字典管理', 192125966745735168, '0,192125966745735168', 2, 'system/dict', 'System/Dict', 'system:dict:list', 'dict', 0, 1, 6, 1, 1, '字典管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735178, '参数配置', 192125966745735168, '0,192125966745735168', 2, 'system/config', 'System/Config', 'system:config:list', 'config', 0, 1, 7, 1, 1, '参数配置菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735179, '公告管理', 192125966745735168, '0,192125966745735168', 2, 'system/notice', 'System/Notice', 'system:notice:list', 'notice', 0, 1, 8, 1, 1, '公告管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735180, '文件管理', 192125966745735168, '0,192125966745735168', 2, 'system/file', 'File', 'system:file:list', 'file', 0, 1, 9, 1, 1, '文件管理菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735182, '登录日志', 192125966745735181, '0,192125966745735168,192125966745735181', 2, 'system/log/login', 'Log/LoginLog', 'system:log:list', 'login-log', 0, 1, 1, 1, 1, '登录日志菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735183, '操作日志', 192125966745735181, '0,192125966745735168,192125966745735181', 2, 'system/log/operation', 'Log/OperationLog', 'system:log:list', 'log', 0, 1, 2, 1, 1, '操作日志菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735250, '查看', 192125966745735182, '0,192125966745735168,192125966745735181,192125966745735182', 3, NULL, NULL, 'system:log:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735251, '查看', 192125966745735183, '0,192125966745735168,192125966745735181,192125966745735183', 3, NULL, NULL, 'system:log:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735184, '服务监控', 192125966745735169, '0,192125966745735169', 2, 'monitor/server', 'Monitor/ServerMonitor', 'monitor:server:list', 'server', 0, 1, 1, 1, 1, '服务监控菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735185, '在线用户', 192125966745735169, '0,192125966745735169', 2, 'monitor/onlineuser', 'Monitor/OnlineUser', 'monitor:onlineuser:list', 'online-user', 0, 1, 2, 1, 1, '在线用户菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735186, '缓存监控', 192125966745735169, '0,192125966745735169', 2, 'monitor/cache', 'Monitor/Cache', 'monitor:cache:list', 'cache', 0, 1, 3, 1, 1, '缓存监控菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735187, '定时任务', 192125966745735169, '0,192125966745735169', 2, 'monitor/job', 'Monitor/Job', 'monitor:job:list', 'job', 0, 1, 4, 1, 1, '定时任务菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735188, '任务日志', 192125966745735169, '0,192125966745735169', 2, 'monitor/joblog', 'Monitor/JobLog', 'monitor:job:list', 'job-log', 0, 1, 5, 1, 1, '任务日志菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735189, '数据备份', 192125966745735169, '0,192125966745735169', 2, 'monitor/backup', 'System/Backup', 'monitor:backup:list', 'backup', 0, 1, 6, 1, 1, '数据备份菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735168, '系统管理', 0, '0', 1, NULL, NULL, NULL, 'setting', 0, 0, 90, 1, 1, '系统管理目录', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-06 19:21:27.417975', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735170, '系统工具', 0, '0', 1, NULL, NULL, NULL, 'tool', 0, 0, 80, 1, 1, '系统工具目录', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-06 19:21:39.286557', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735169, '监控管理', 0, '0', 1, NULL, NULL, NULL, 'monitor', 0, 0, 70, 1, 1, '监控管理目录', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-06 19:21:44.149574', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735171, '工作台', 0, '0', 2, 'dashboard', 'Dashboard', 'system:dashboard:query', 'dashboard', 0, 1, 10, 1, 1, '工作台菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-06 19:21:49.439191', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735181, '日志管理', 0, '0', 1, NULL, NULL, NULL, 'log', 0, 0, 60, 1, 1, '日志管理目录', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-06 19:22:16.554387', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735190, '代码生成', 192125966745735170, '0,192125966745735170', 2, 'codegen/table', 'System/Codegen', 'codegen:table:list', 'codegen', 0, 1, 1, 1, 1, '代码生成菜单', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735191, '查询', 192125966745735172, '0,192125966745735168,192125966745735172,192125966745735172', 3, NULL, NULL, 'system:user:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735192, '新增', 192125966745735172, '0,192125966745735168,192125966745735172,192125966745735172', 3, NULL, NULL, 'system:user:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735193, '修改', 192125966745735172, '0,192125966745735168,192125966745735172,192125966745735172', 3, NULL, NULL, 'system:user:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735194, '删除', 192125966745735172, '0,192125966745735168,192125966745735172,192125966745735172', 3, NULL, NULL, 'system:user:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735195, '查询', 192125966745735173, '0,192125966745735168,192125966745735173,192125966745735173', 3, NULL, NULL, 'system:role:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735196, '新增', 192125966745735173, '0,192125966745735168,192125966745735173,192125966745735173', 3, NULL, NULL, 'system:role:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735197, '修改', 192125966745735173, '0,192125966745735168,192125966745735173,192125966745735173', 3, NULL, NULL, 'system:role:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735198, '删除', 192125966745735173, '0,192125966745735168,192125966745735173,192125966745735173', 3, NULL, NULL, 'system:role:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735199, '分配权限', 192125966745735173, '0,192125966745735168,192125966745735173,192125966745735173', 3, NULL, NULL, 'system:role:grant', NULL, 0, 0, 5, 1, 1, '分配权限按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735200, '查询', 192125966745735174, '0,192125966745735168,192125966745735174,192125966745735174', 3, NULL, NULL, 'system:menu:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735201, '新增', 192125966745735174, '0,192125966745735168,192125966745735174,192125966745735174', 3, NULL, NULL, 'system:menu:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735202, '修改', 192125966745735174, '0,192125966745735168,192125966745735174,192125966745735174', 3, NULL, NULL, 'system:menu:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735203, '删除', 192125966745735174, '0,192125966745735168,192125966745735174,192125966745735174', 3, NULL, NULL, 'system:menu:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735204, '查询', 192125966745735175, '0,192125966745735168,192125966745735175,192125966745735175', 3, NULL, NULL, 'system:organization:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735205, '新增', 192125966745735175, '0,192125966745735168,192125966745735175,192125966745735175', 3, NULL, NULL, 'system:organization:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735206, '修改', 192125966745735175, '0,192125966745735168,192125966745735175,192125966745735175', 3, NULL, NULL, 'system:organization:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735207, '删除', 192125966745735175, '0,192125966745735168,192125966745735175,192125966745735175', 3, NULL, NULL, 'system:organization:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735208, '查询', 192125966745735176, '0,192125966745735168,192125966745735176,192125966745735176', 3, NULL, NULL, 'system:position:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735209, '新增', 192125966745735176, '0,192125966745735168,192125966745735176,192125966745735176', 3, NULL, NULL, 'system:position:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735210, '修改', 192125966745735176, '0,192125966745735168,192125966745735176,192125966745735176', 3, NULL, NULL, 'system:position:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735211, '删除', 192125966745735176, '0,192125966745735168,192125966745735176,192125966745735176', 3, NULL, NULL, 'system:position:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735212, '查询', 192125966745735177, '0,192125966745735168,192125966745735177,192125966745735177', 3, NULL, NULL, 'system:dict:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735213, '新增', 192125966745735177, '0,192125966745735168,192125966745735177,192125966745735177', 3, NULL, NULL, 'system:dict:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735214, '修改', 192125966745735177, '0,192125966745735168,192125966745735177,192125966745735177', 3, NULL, NULL, 'system:dict:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735215, '删除', 192125966745735177, '0,192125966745735168,192125966745735177,192125966745735177', 3, NULL, NULL, 'system:dict:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735216, '查询', 192125966745735178, '0,192125966745735168,192125966745735178,192125966745735178', 3, NULL, NULL, 'system:config:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735217, '新增', 192125966745735178, '0,192125966745735168,192125966745735178,192125966745735178', 3, NULL, NULL, 'system:config:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735218, '修改', 192125966745735178, '0,192125966745735168,192125966745735178,192125966745735178', 3, NULL, NULL, 'system:config:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735219, '删除', 192125966745735178, '0,192125966745735168,192125966745735178,192125966745735178', 3, NULL, NULL, 'system:config:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735220, '查询', 192125966745735179, '0,192125966745735168,192125966745735179,192125966745735179', 3, NULL, NULL, 'system:notice:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735221, '新增', 192125966745735179, '0,192125966745735168,192125966745735179,192125966745735179', 3, NULL, NULL, 'system:notice:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735222, '修改', 192125966745735179, '0,192125966745735168,192125966745735179,192125966745735179', 3, NULL, NULL, 'system:notice:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735223, '删除', 192125966745735179, '0,192125966745735168,192125966745735179,192125966745735179', 3, NULL, NULL, 'system:notice:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735224, '发布', 192125966745735179, '0,192125966745735168,192125966745735179,192125966745735179', 3, NULL, NULL, 'system:notice:publish', NULL, 0, 0, 5, 1, 1, '发布按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735225, '撤回', 192125966745735179, '0,192125966745735168,192125966745735179,192125966745735179', 3, NULL, NULL, 'system:notice:revoke', NULL, 0, 0, 6, 1, 1, '撤回按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735226, '上传', 192125966745735180, '0,192125966745735168,192125966745735180,192125966745735180', 3, NULL, NULL, 'system:file:upload', NULL, 0, 0, 1, 1, 1, '上传按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735227, '下载', 192125966745735180, '0,192125966745735168,192125966745735180,192125966745735180', 3, NULL, NULL, 'system:file:download', NULL, 0, 0, 2, 1, 1, '下载按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735228, '删除', 192125966745735180, '0,192125966745735168,192125966745735180,192125966745735180', 3, NULL, NULL, 'system:file:delete', NULL, 0, 0, 3, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735229, '强退', 192125966745735185, '0,192125966745735169,192125966745735185,192125966745735185', 3, NULL, NULL, 'monitor:onlineuser:forceLogout', NULL, 0, 0, 1, 1, 1, '强退按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735230, '清理', 192125966745735186, '0,192125966745735169,192125966745735186,192125966745735186', 3, NULL, NULL, 'monitor:cache:clear', NULL, 0, 0, 1, 1, 1, '清理按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735231, '查询', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735232, '新增', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735233, '修改', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735234, '删除', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735235, '暂停', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:pause', NULL, 0, 0, 5, 1, 1, '暂停按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735236, '恢复', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:resume', NULL, 0, 0, 6, 1, 1, '恢复按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735237, '执行', 192125966745735187, '0,192125966745735169,192125966745735187,192125966745735187', 3, NULL, NULL, 'monitor:job:run', NULL, 0, 0, 7, 1, 1, '执行按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735238, '查询', 192125966745735188, '0,192125966745735169,192125966745735188,192125966745735188', 3, NULL, NULL, 'monitor:job:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735239, '删除', 192125966745735188, '0,192125966745735169,192125966745735188,192125966745735188', 3, NULL, NULL, 'monitor:joblog:delete', NULL, 0, 0, 2, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735240, '清空', 192125966745735188, '0,192125966745735169,192125966745735188,192125966745735188', 3, NULL, NULL, 'monitor:joblog:clean', NULL, 0, 0, 3, 1, 1, '清空按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735241, '备份', 192125966745735189, '0,192125966745735169,192125966745735189,192125966745735189', 3, NULL, NULL, 'monitor:backup:backup', NULL, 0, 0, 1, 1, 1, '备份按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735242, '还原', 192125966745735189, '0,192125966745735169,192125966745735189,192125966745735189', 3, NULL, NULL, 'monitor:backup:restore', NULL, 0, 0, 2, 1, 1, '还原按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735243, '删除', 192125966745735189, '0,192125966745735169,192125966745735189,192125966745735189', 3, NULL, NULL, 'monitor:backup:delete', NULL, 0, 0, 3, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735244, '查询', 192125966745735190, '0,192125966745735170,192125966745735190,192125966745735190', 3, NULL, NULL, 'codegen:table:query', NULL, 0, 0, 1, 1, 1, '查询按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735245, '新增', 192125966745735190, '0,192125966745735170,192125966745735190,192125966745735190', 3, NULL, NULL, 'codegen:table:add', NULL, 0, 0, 2, 1, 1, '新增按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735246, '修改', 192125966745735190, '0,192125966745735170,192125966745735190,192125966745735190', 3, NULL, NULL, 'codegen:table:edit', NULL, 0, 0, 3, 1, 1, '修改按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735247, '删除', 192125966745735190, '0,192125966745735170,192125966745735190,192125966745735190', 3, NULL, NULL, 'codegen:table:delete', NULL, 0, 0, 4, 1, 1, '删除按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735248, '生成代码', 192125966745735190, '0,192125966745735170,192125966745735190,192125966745735190', 3, NULL, NULL, 'codegen:codegen:generate', NULL, 0, 0, 5, 1, 1, '生成代码按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735249, '预览', 192125966745735190, '0,192125966745735170,192125966745735190,192125966745735190', 3, NULL, NULL, 'codegen:codegen:preview', NULL, 0, 0, 6, 1, 1, '预览按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
-- 接口鉴权实际使用的 view 类权限码按钮（与 SxwlPermConstant 对齐，修复非超管 403）
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735252, '查看', 192125966745735180, '0,192125966745735168,192125966745735180,192125966745735180', 3, NULL, NULL, 'system:file:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735253, '查看', 192125966745735184, '0,192125966745735169,192125966745735184,192125966745735184', 3, NULL, NULL, 'monitor:server:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735254, '查看', 192125966745735185, '0,192125966745735169,192125966745735185,192125966745735185', 3, NULL, NULL, 'monitor:onlineuser:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735255, '查看', 192125966745735186, '0,192125966745735169,192125966745735186,192125966745735186', 3, NULL, NULL, 'monitor:cache:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);
INSERT INTO "public"."sys_menu_info" VALUES (192125966745735256, '查看', 192125966745735189, '0,192125966745735169,192125966745735189,192125966745735189', 3, NULL, NULL, 'monitor:backup:view', NULL, 0, 0, 1, 1, 1, '查看按钮', 191727527736459266, 191727527736459264, '2026-09-05 12:00:00', 191727527736459266, '2026-09-05 12:00:00', 0);

-- ----------------------------
-- Table structure for sys_monitor_db_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_monitor_db_log";
CREATE TABLE "public"."sys_monitor_db_log" (
  "id" int8 NOT NULL,
  "active_connections" int4,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."sys_monitor_db_log"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_monitor_db_log"."active_connections" IS '活跃连接数';
COMMENT ON COLUMN "public"."sys_monitor_db_log"."create_time" IS '记录时间';
COMMENT ON TABLE "public"."sys_monitor_db_log" IS '系统监控-数据库指标日志';

-- ----------------------------
-- Records of sys_monitor_db_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_monitor_jvm_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_monitor_jvm_log";
CREATE TABLE "public"."sys_monitor_jvm_log" (
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
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."heap_used" IS '堆内存已用（字节）';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."heap_max" IS '堆内存最大值（字节）';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."heap_committed" IS '堆内存提交值（字节）';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."thread_count" IS '当前线程数';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."peak_thread_count" IS '峰值线程数';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."class_loaded_count" IS '已加载类数';
COMMENT ON COLUMN "public"."sys_monitor_jvm_log"."create_time" IS '记录时间';
COMMENT ON TABLE "public"."sys_monitor_jvm_log" IS '系统监控-JVM 指标日志';

-- ----------------------------
-- Records of sys_monitor_jvm_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_monitor_redis_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_monitor_redis_log";
CREATE TABLE "public"."sys_monitor_redis_log" (
  "id" int8 NOT NULL,
  "connected_clients" int4,
  "used_memory" int8,
  "hit_rate" float8,
  "total_keys" int4,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."sys_monitor_redis_log"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_monitor_redis_log"."connected_clients" IS '已连接客户端数';
COMMENT ON COLUMN "public"."sys_monitor_redis_log"."used_memory" IS 'Redis 内存使用（字节）';
COMMENT ON COLUMN "public"."sys_monitor_redis_log"."hit_rate" IS '缓存命中率（0~100）';
COMMENT ON COLUMN "public"."sys_monitor_redis_log"."total_keys" IS 'Key 总数';
COMMENT ON COLUMN "public"."sys_monitor_redis_log"."create_time" IS '记录时间';
COMMENT ON TABLE "public"."sys_monitor_redis_log" IS '系统监控-Redis 指标日志';

-- ----------------------------
-- Records of sys_monitor_redis_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_monitor_server_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_monitor_server_log";
CREATE TABLE "public"."sys_monitor_server_log" (
  "id" int8 NOT NULL,
  "cpu_load" float8,
  "mem_used" int8,
  "mem_total" int8,
  "disk_used" int8,
  "disk_total" int8,
  "create_time" timestamp(6) NOT NULL
)
;
COMMENT ON COLUMN "public"."sys_monitor_server_log"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_monitor_server_log"."cpu_load" IS 'CPU 负载百分比（0~100）';
COMMENT ON COLUMN "public"."sys_monitor_server_log"."mem_used" IS '已用内存（字节）';
COMMENT ON COLUMN "public"."sys_monitor_server_log"."mem_total" IS '总内存（字节）';
COMMENT ON COLUMN "public"."sys_monitor_server_log"."disk_used" IS '已用磁盘（字节）';
COMMENT ON COLUMN "public"."sys_monitor_server_log"."disk_total" IS '总磁盘（字节）';
COMMENT ON COLUMN "public"."sys_monitor_server_log"."create_time" IS '记录时间';
COMMENT ON TABLE "public"."sys_monitor_server_log" IS '系统监控-服务器指标日志';

-- ----------------------------
-- Records of sys_monitor_server_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_notice_info";
CREATE TABLE "public"."sys_notice_info" (
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
COMMENT ON COLUMN "public"."sys_notice_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_notice_info"."title" IS '公告标题';
COMMENT ON COLUMN "public"."sys_notice_info"."content" IS '公告内容（富文本 HTML）';
COMMENT ON COLUMN "public"."sys_notice_info"."notice_type" IS '公告类型：notice=通知 announcement=公告';
COMMENT ON COLUMN "public"."sys_notice_info"."level" IS '级别：info=普通 important=重要 urgent=紧急';
COMMENT ON COLUMN "public"."sys_notice_info"."status" IS '状态：0=草稿 1=已发布 2=已撤回';
COMMENT ON COLUMN "public"."sys_notice_info"."publish_time" IS '发布时间';
COMMENT ON COLUMN "public"."sys_notice_info"."expire_time" IS '过期时间';
COMMENT ON COLUMN "public"."sys_notice_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_notice_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_notice_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_notice_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_notice_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_notice_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_notice_info" IS '通知公告信息表';

-- ----------------------------
-- Records of sys_notice_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_read
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_notice_read";
CREATE TABLE "public"."sys_notice_read" (
  "id" int8 NOT NULL,
  "notice_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "read_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."sys_notice_read"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_notice_read"."notice_id" IS '公告ID，关联 sys_notice_info.id';
COMMENT ON COLUMN "public"."sys_notice_read"."user_id" IS '用户ID，关联 sys_user_info.id';
COMMENT ON COLUMN "public"."sys_notice_read"."read_time" IS '阅读时间';
COMMENT ON TABLE "public"."sys_notice_read" IS '通知公告已读状态表';

-- ----------------------------
-- Records of sys_notice_read
-- ----------------------------

-- ----------------------------
-- Table structure for sys_organization_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_organization_info";
CREATE TABLE "public"."sys_organization_info" (
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
COMMENT ON COLUMN "public"."sys_organization_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_organization_info"."org_code" IS '组织编码，4位数字/字母，全局唯一';
COMMENT ON COLUMN "public"."sys_organization_info"."org_name" IS '组织名称，如：总公司、技术部、后端组';
COMMENT ON COLUMN "public"."sys_organization_info"."parent_id" IS '父组织ID，根组织为0';
COMMENT ON COLUMN "public"."sys_organization_info"."ancestors" IS '祖先路径，逗号分隔的ID链，如 0,1,2，查询子树用 LIKE ''0,1,%''；前提：ID无前缀冲突（int8长整型，风险极低），改用雪花ID需重新评估LIKE性能';
COMMENT ON COLUMN "public"."sys_organization_info"."org_level" IS '层级：1=公司 2=部门 3=小组';
COMMENT ON COLUMN "public"."sys_organization_info"."org_type" IS '组织类型（关联字典detail_value），如 01=公司 02=部门';
COMMENT ON COLUMN "public"."sys_organization_info"."leader_id" IS '负责人ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "public"."sys_organization_info"."phone" IS '组织联系电话';
COMMENT ON COLUMN "public"."sys_organization_info"."sort" IS '排序号，控制组织树展示顺序';
COMMENT ON COLUMN "public"."sys_organization_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_organization_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_organization_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_organization_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_organization_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_organization_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_organization_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_organization_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_organization_info" IS '组织信息表';

-- ----------------------------
-- Records of sys_organization_info
-- ----------------------------
INSERT INTO "public"."sys_organization_info" VALUES (191727527736459264, '1000', '总公司', 0, '0', 1, NULL, NULL, NULL, 0, 1, '根组织', 191727527736459266, 191727527736459264, '2026-09-04 09:36:59.734686', NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_position_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_position_info";
CREATE TABLE "public"."sys_position_info" (
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
COMMENT ON COLUMN "public"."sys_position_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_position_info"."position_code" IS '岗位编码，英文，如 cto、backend_engineer，全局唯一';
COMMENT ON COLUMN "public"."sys_position_info"."position_name" IS '岗位名称，如：技术总监、后端工程师';
COMMENT ON COLUMN "public"."sys_position_info"."sort" IS '排序号，控制岗位展示顺序';
COMMENT ON COLUMN "public"."sys_position_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_position_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_position_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_position_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_position_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_position_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_position_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_position_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_position_info" IS '岗位信息表';

-- ----------------------------
-- Records of sys_position_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_data_scope_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_data_scope_info";
CREATE TABLE "public"."sys_role_data_scope_info" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "org_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."org_id" IS '授权可见的组织ID（关联 sys_organization_info.id）';
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."create_by" IS '创建人标识（谁配置的数据权限）';
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_role_data_scope_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_role_data_scope_info" IS '角色数据权限信息表（仅 sys_role_info.data_scope=5 时生效）';

-- ----------------------------
-- Records of sys_role_data_scope_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_info";
CREATE TABLE "public"."sys_role_info" (
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
COMMENT ON COLUMN "public"."sys_role_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_role_info"."role_code" IS '角色编码，英文，如 admin、user，全局唯一';
COMMENT ON COLUMN "public"."sys_role_info"."role_name" IS '角色名称，如：管理员、普通员工';
COMMENT ON COLUMN "public"."sys_role_info"."data_scope" IS '数据权限范围：1=全部 2=本组织 3=本组织及下级 4=仅本人 5=自定义';
COMMENT ON COLUMN "public"."sys_role_info"."sort" IS '排序号，控制角色展示顺序';
COMMENT ON COLUMN "public"."sys_role_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_role_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_role_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_role_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_role_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_role_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_role_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_role_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_role_info" IS '角色信息表';

-- ----------------------------
-- Records of sys_role_info
-- ----------------------------
INSERT INTO "public"."sys_role_info" VALUES (191727527736459265, 'super_admin', '超级管理员', 1, 1, 1, '超级管理员（所有权限）', 191727527736459266, 191727527736459264, '2026-09-04 09:36:59.74917', NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_role_menu_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role_menu_info";
CREATE TABLE "public"."sys_role_menu_info" (
  "id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "menu_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "public"."sys_role_menu_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_role_menu_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "public"."sys_role_menu_info"."menu_id" IS '菜单ID（关联 sys_menu_info.id）';
COMMENT ON COLUMN "public"."sys_role_menu_info"."create_by" IS '创建人标识（谁分配的菜单）';
COMMENT ON COLUMN "public"."sys_role_menu_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_role_menu_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_role_menu_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_role_menu_info" IS '角色菜单信息表';

-- ----------------------------
-- Records of sys_role_menu_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_info";
CREATE TABLE "public"."sys_user_info" (
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
COMMENT ON COLUMN "public"."sys_user_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_user_info"."username" IS '登录账号，全局唯一';
COMMENT ON COLUMN "public"."sys_user_info"."password" IS '密码哈希（SM3 + 随机盐值 + 多轮迭代，禁止明文/可逆加密）';
COMMENT ON COLUMN "public"."sys_user_info"."real_name" IS '真实姓名';
COMMENT ON COLUMN "public"."sys_user_info"."nickname" IS '昵称';
COMMENT ON COLUMN "public"."sys_user_info"."phone" IS '手机号（用于手机验证码登录、找回密码），全局唯一';
COMMENT ON COLUMN "public"."sys_user_info"."email" IS '邮箱';
COMMENT ON COLUMN "public"."sys_user_info"."avatar" IS '头像URL（RustFS访问地址）';
COMMENT ON COLUMN "public"."sys_user_info"."gender" IS '性别：0=未知 1=男 2=女';
COMMENT ON COLUMN "public"."sys_user_info"."status" IS '状态：0=禁用 1=启用';
COMMENT ON COLUMN "public"."sys_user_info"."error_count" IS '密码错误次数（连续错误5次锁定）';
COMMENT ON COLUMN "public"."sys_user_info"."lock_time" IS '锁定时间（锁定30分钟后自动解锁）';
COMMENT ON COLUMN "public"."sys_user_info"."last_login_time" IS '最后登录时间';
COMMENT ON COLUMN "public"."sys_user_info"."last_login_ip" IS '最后登录IP';
COMMENT ON COLUMN "public"."sys_user_info"."description" IS '描述说明';
COMMENT ON COLUMN "public"."sys_user_info"."create_by" IS '创建人标识';
COMMENT ON COLUMN "public"."sys_user_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_user_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user_info"."update_by" IS '更新人标识';
COMMENT ON COLUMN "public"."sys_user_info"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_user_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_user_info" IS '系统用户信息表（B端用户，企业员工）';

-- ----------------------------
-- Records of sys_user_info
-- ----------------------------
INSERT INTO "public"."sys_user_info" VALUES (191727527736459266, 'SuperAdmin', '{sm3}oyW5JBKg3bkknEI8sgi6hg==$46f8d62a74b34ea85748c3e27ccff2b186716916296c721c55ef0f17ed34b193', '超级管理员', 'SuperAdmin', '16601153615', NULL, NULL, 0, 1, 0, NULL, NULL, NULL, NULL, 191727527736459266, 191727527736459264, '2026-09-04 09:36:59.758113', NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_user_organization_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_organization_info";
CREATE TABLE "public"."sys_user_organization_info" (
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
COMMENT ON COLUMN "public"."sys_user_organization_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_user_organization_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "public"."sys_user_organization_info"."org_id" IS '组织ID（关联 sys_organization_info.id）';
COMMENT ON COLUMN "public"."sys_user_organization_info"."is_main" IS '是否主组织：0=否 1=是（每个用户仅一个主组织）';
COMMENT ON COLUMN "public"."sys_user_organization_info"."create_by" IS '创建人标识（谁分配的组织）';
COMMENT ON COLUMN "public"."sys_user_organization_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_user_organization_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user_organization_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_user_organization_info" IS '用户组织信息表';

-- ----------------------------
-- Records of sys_user_organization_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_position_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_position_info";
CREATE TABLE "public"."sys_user_position_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "position_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "public"."sys_user_position_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_user_position_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "public"."sys_user_position_info"."position_id" IS '岗位ID（关联 sys_position_info.id）';
COMMENT ON COLUMN "public"."sys_user_position_info"."create_by" IS '创建人标识（谁分配的岗位）';
COMMENT ON COLUMN "public"."sys_user_position_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_user_position_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user_position_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_user_position_info" IS '用户岗位信息表';

-- ----------------------------
-- Records of sys_user_position_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user_role_info";
CREATE TABLE "public"."sys_user_role_info" (
  "id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL,
  "create_by" int8 NOT NULL,
  "create_org" int8 NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "delete_flag" int2 NOT NULL DEFAULT 0
)
;
COMMENT ON COLUMN "public"."sys_user_role_info"."id" IS '唯一标识';
COMMENT ON COLUMN "public"."sys_user_role_info"."user_id" IS '用户ID（关联 sys_user_info.id）';
COMMENT ON COLUMN "public"."sys_user_role_info"."role_id" IS '角色ID（关联 sys_role_info.id）';
COMMENT ON COLUMN "public"."sys_user_role_info"."create_by" IS '创建人标识（谁分配的角色）';
COMMENT ON COLUMN "public"."sys_user_role_info"."create_org" IS '创建人所属组织标识';
COMMENT ON COLUMN "public"."sys_user_role_info"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user_role_info"."delete_flag" IS '删除标志：0=正常 1=已删除';
COMMENT ON TABLE "public"."sys_user_role_info" IS '用户角色信息表';

-- ----------------------------
-- Records of sys_user_role_info
-- ----------------------------
INSERT INTO "public"."sys_user_role_info" VALUES (191727527736459267, 191727527736459266, 191727527736459265, 191727527736459266, 191727527736459264, '2026-09-04 09:36:59.76738', 0);

-- ----------------------------
-- Primary Key structure for table qrtz_blob_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_blob_triggers" ADD CONSTRAINT "qrtz_blob_triggers_pkey" PRIMARY KEY ("sched_name", "trigger_name", "trigger_group");

-- ----------------------------
-- Primary Key structure for table qrtz_calendars
-- ----------------------------
ALTER TABLE "public"."qrtz_calendars" ADD CONSTRAINT "qrtz_calendars_pkey" PRIMARY KEY ("sched_name", "calendar_name");

-- ----------------------------
-- Primary Key structure for table qrtz_cron_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_cron_triggers" ADD CONSTRAINT "qrtz_cron_triggers_pkey" PRIMARY KEY ("sched_name", "trigger_name", "trigger_group");

-- ----------------------------
-- Indexes structure for table qrtz_fired_triggers
-- ----------------------------
CREATE INDEX "idx_qrtz_ft_inst_job_req_rcvry" ON "public"."qrtz_fired_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "instance_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "requests_recovery" "pg_catalog"."bool_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_ft_j_g" ON "public"."qrtz_fired_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_ft_jg" ON "public"."qrtz_fired_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_ft_t_g" ON "public"."qrtz_fired_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_ft_tg" ON "public"."qrtz_fired_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_ft_trig_inst_name" ON "public"."qrtz_fired_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "instance_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table qrtz_fired_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_fired_triggers" ADD CONSTRAINT "qrtz_fired_triggers_pkey" PRIMARY KEY ("sched_name", "entry_id");

-- ----------------------------
-- Indexes structure for table qrtz_job_details
-- ----------------------------
CREATE INDEX "idx_qrtz_j_grp" ON "public"."qrtz_job_details" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_j_req_recovery" ON "public"."qrtz_job_details" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "requests_recovery" "pg_catalog"."bool_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table qrtz_job_details
-- ----------------------------
ALTER TABLE "public"."qrtz_job_details" ADD CONSTRAINT "qrtz_job_details_pkey" PRIMARY KEY ("sched_name", "job_name", "job_group");

-- ----------------------------
-- Primary Key structure for table qrtz_locks
-- ----------------------------
ALTER TABLE "public"."qrtz_locks" ADD CONSTRAINT "qrtz_locks_pkey" PRIMARY KEY ("sched_name", "lock_name");

-- ----------------------------
-- Primary Key structure for table qrtz_paused_trigger_grps
-- ----------------------------
ALTER TABLE "public"."qrtz_paused_trigger_grps" ADD CONSTRAINT "qrtz_paused_trigger_grps_pkey" PRIMARY KEY ("sched_name", "trigger_group");

-- ----------------------------
-- Primary Key structure for table qrtz_scheduler_state
-- ----------------------------
ALTER TABLE "public"."qrtz_scheduler_state" ADD CONSTRAINT "qrtz_scheduler_state_pkey" PRIMARY KEY ("sched_name", "instance_name");

-- ----------------------------
-- Primary Key structure for table qrtz_simple_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_simple_triggers" ADD CONSTRAINT "qrtz_simple_triggers_pkey" PRIMARY KEY ("sched_name", "trigger_name", "trigger_group");

-- ----------------------------
-- Primary Key structure for table qrtz_simprop_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_simprop_triggers" ADD CONSTRAINT "qrtz_simprop_triggers_pkey" PRIMARY KEY ("sched_name", "trigger_name", "trigger_group");

-- ----------------------------
-- Indexes structure for table qrtz_triggers
-- ----------------------------
CREATE INDEX "idx_qrtz_t_c" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "calendar_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_g" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_j" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_jg" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_n_g_state" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_n_state" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_next_fire_time" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "next_fire_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_nft_misfire" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "misfire_instr" "pg_catalog"."int2_ops" ASC NULLS LAST,
  "next_fire_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_nft_st" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "next_fire_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_nft_st_misfire" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "misfire_instr" "pg_catalog"."int2_ops" ASC NULLS LAST,
  "next_fire_time" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trigger_state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_nft_st_misfire_grp" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "misfire_instr" "pg_catalog"."int2_ops" ASC NULLS LAST,
  "next_fire_time" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trigger_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_qrtz_t_state" ON "public"."qrtz_triggers" USING btree (
  "sched_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "trigger_state" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table qrtz_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_triggers" ADD CONSTRAINT "qrtz_triggers_pkey" PRIMARY KEY ("sched_name", "trigger_name", "trigger_group");

-- ----------------------------
-- Indexes structure for table sys_codegen_field_info
-- ----------------------------
CREATE INDEX "idx_sys_codegen_field_sort" ON "public"."sys_codegen_field_info" USING btree (
  "table_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "sort" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_codegen_field_table_id" ON "public"."sys_codegen_field_info" USING btree (
  "table_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_codegen_field_info
-- ----------------------------
ALTER TABLE "public"."sys_codegen_field_info" ADD CONSTRAINT "sys_codegen_field_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_codegen_table_info
-- ----------------------------
CREATE INDEX "idx_sys_codegen_table_create_time" ON "public"."sys_codegen_table_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_codegen_table_status" ON "public"."sys_codegen_table_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_codegen_table_name" ON "public"."sys_codegen_table_info" USING btree (
  "table_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_codegen_table_info
-- ----------------------------
ALTER TABLE "public"."sys_codegen_table_info" ADD CONSTRAINT "sys_codegen_table_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_config_info
-- ----------------------------
CREATE INDEX "idx_sys_config_status" ON "public"."sys_config_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_config_type" ON "public"."sys_config_info" USING btree (
  "config_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_config_key" ON "public"."sys_config_info" USING btree (
  "config_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_config_info
-- ----------------------------
ALTER TABLE "public"."sys_config_info" ADD CONSTRAINT "sys_config_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_dict_detail_info
-- ----------------------------
CREATE INDEX "idx_sys_dict_detail_dict_id" ON "public"."sys_dict_detail_info" USING btree (
  "dict_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_dict_detail_default" ON "public"."sys_dict_detail_info" USING btree (
  "dict_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE is_default = 1 AND delete_flag = 0;
CREATE UNIQUE INDEX "uk_sys_dict_detail_value" ON "public"."sys_dict_detail_info" USING btree (
  "detail_value" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_dict_detail_info
-- ----------------------------
ALTER TABLE "public"."sys_dict_detail_info" ADD CONSTRAINT "sys_dict_detail_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_dict_info
-- ----------------------------
CREATE INDEX "idx_sys_dict_info_status" ON "public"."sys_dict_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_dict_info_code" ON "public"."sys_dict_info" USING btree (
  "dict_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_dict_info
-- ----------------------------
ALTER TABLE "public"."sys_dict_info" ADD CONSTRAINT "sys_dict_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_file_chunk_info
-- ----------------------------
CREATE INDEX "idx_sys_file_chunk_upload" ON "public"."sys_file_chunk_info" USING btree (
  "upload_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_file_chunk" ON "public"."sys_file_chunk_info" USING btree (
  "upload_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "chunk_index" "pg_catalog"."int4_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_file_chunk_info
-- ----------------------------
ALTER TABLE "public"."sys_file_chunk_info" ADD CONSTRAINT "sys_file_chunk_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_file_info
-- ----------------------------
CREATE INDEX "idx_sys_file_business_type" ON "public"."sys_file_info" USING btree (
  "business_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_create_by" ON "public"."sys_file_info" USING btree (
  "create_by" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_create_time" ON "public"."sys_file_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_md5" ON "public"."sys_file_info" USING btree (
  "file_size" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "md5" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_status" ON "public"."sys_file_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_file_info
-- ----------------------------
ALTER TABLE "public"."sys_file_info" ADD CONSTRAINT "sys_file_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_file_session_info
-- ----------------------------
CREATE INDEX "idx_sys_file_session_md5" ON "public"."sys_file_session_info" USING btree (
  "file_md5" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_file_session_status" ON "public"."sys_file_session_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_file_session_info
-- ----------------------------
ALTER TABLE "public"."sys_file_session_info" ADD CONSTRAINT "sys_file_session_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_job_info
-- ----------------------------
CREATE INDEX "idx_sys_job_status" ON "public"."sys_job_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_job_name_group" ON "public"."sys_job_info" USING btree (
  "job_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "job_group" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_job_info
-- ----------------------------
ALTER TABLE "public"."sys_job_info" ADD CONSTRAINT "sys_job_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_job_log_info
-- ----------------------------
CREATE INDEX "idx_sys_job_log_create_time" ON "public"."sys_job_log_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_job_log_job_id" ON "public"."sys_job_log_info" USING btree (
  "job_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_job_log_status" ON "public"."sys_job_log_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_job_log_info
-- ----------------------------
ALTER TABLE "public"."sys_job_log_info" ADD CONSTRAINT "sys_job_log_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_log_info
-- ----------------------------
CREATE INDEX "idx_sys_log_create_time" ON "public"."sys_log_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_status" ON "public"."sys_log_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_trace_id" ON "public"."sys_log_info" USING btree (
  "trace_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_type" ON "public"."sys_log_info" USING btree (
  "log_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_log_user_id" ON "public"."sys_log_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_log_info
-- ----------------------------
ALTER TABLE "public"."sys_log_info" ADD CONSTRAINT "sys_log_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_menu_info
-- ----------------------------
CREATE INDEX "idx_sys_menu_ancestors" ON "public"."sys_menu_info" USING btree (
  "ancestors" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_menu_parent_id" ON "public"."sys_menu_info" USING btree (
  "parent_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_menu_status" ON "public"."sys_menu_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_menu_perms" ON "public"."sys_menu_info" USING btree (
  "id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "perms" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0 AND perms IS NOT NULL;

-- ----------------------------
-- Primary Key structure for table sys_menu_info
-- ----------------------------
ALTER TABLE "public"."sys_menu_info" ADD CONSTRAINT "sys_menu_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_db_log
-- ----------------------------
CREATE INDEX "idx_smdbl_create_time" ON "public"."sys_monitor_db_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_db_log
-- ----------------------------
ALTER TABLE "public"."sys_monitor_db_log" ADD CONSTRAINT "sys_monitor_db_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_jvm_log
-- ----------------------------
CREATE INDEX "idx_smjvl_create_time" ON "public"."sys_monitor_jvm_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_jvm_log
-- ----------------------------
ALTER TABLE "public"."sys_monitor_jvm_log" ADD CONSTRAINT "sys_monitor_jvm_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_redis_log
-- ----------------------------
CREATE INDEX "idx_smrl_create_time" ON "public"."sys_monitor_redis_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_redis_log
-- ----------------------------
ALTER TABLE "public"."sys_monitor_redis_log" ADD CONSTRAINT "sys_monitor_redis_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_monitor_server_log
-- ----------------------------
CREATE INDEX "idx_smsl_create_time" ON "public"."sys_monitor_server_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_monitor_server_log
-- ----------------------------
ALTER TABLE "public"."sys_monitor_server_log" ADD CONSTRAINT "sys_monitor_server_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_notice_info
-- ----------------------------
CREATE INDEX "idx_sys_notice_create_time" ON "public"."sys_notice_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_level" ON "public"."sys_notice_info" USING btree (
  "level" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_status" ON "public"."sys_notice_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_type" ON "public"."sys_notice_info" USING btree (
  "notice_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_notice_info
-- ----------------------------
ALTER TABLE "public"."sys_notice_info" ADD CONSTRAINT "sys_notice_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_notice_read
-- ----------------------------
CREATE UNIQUE INDEX "idx_sys_notice_read_uid_nid" ON "public"."sys_notice_read" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "notice_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_notice_read_user_id" ON "public"."sys_notice_read" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_notice_read
-- ----------------------------
ALTER TABLE "public"."sys_notice_read" ADD CONSTRAINT "sys_notice_read_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_organization_info
-- ----------------------------
CREATE INDEX "idx_sys_org_ancestors" ON "public"."sys_organization_info" USING btree (
  "ancestors" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_org_leader_id" ON "public"."sys_organization_info" USING btree (
  "leader_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_org_parent_id" ON "public"."sys_organization_info" USING btree (
  "parent_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_org_status" ON "public"."sys_organization_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_org_code" ON "public"."sys_organization_info" USING btree (
  "org_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_organization_info
-- ----------------------------
ALTER TABLE "public"."sys_organization_info" ADD CONSTRAINT "sys_organization_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_position_info
-- ----------------------------
CREATE INDEX "idx_sys_position_status" ON "public"."sys_position_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_position_code" ON "public"."sys_position_info" USING btree (
  "position_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_position_info
-- ----------------------------
ALTER TABLE "public"."sys_position_info" ADD CONSTRAINT "sys_position_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_role_data_scope_info
-- ----------------------------
CREATE INDEX "idx_sys_role_data_scope_org" ON "public"."sys_role_data_scope_info" USING btree (
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_role_data_scope" ON "public"."sys_role_data_scope_info" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_role_data_scope_info
-- ----------------------------
ALTER TABLE "public"."sys_role_data_scope_info" ADD CONSTRAINT "sys_role_data_scope_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_role_info
-- ----------------------------
CREATE INDEX "idx_sys_role_data_scope" ON "public"."sys_role_info" USING btree (
  "data_scope" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_role_status" ON "public"."sys_role_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_role_code" ON "public"."sys_role_info" USING btree (
  "role_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_role_info
-- ----------------------------
ALTER TABLE "public"."sys_role_info" ADD CONSTRAINT "sys_role_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_role_menu_info
-- ----------------------------
CREATE INDEX "idx_sys_role_menu_menu" ON "public"."sys_role_menu_info" USING btree (
  "menu_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_role_menu" ON "public"."sys_role_menu_info" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "menu_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_role_menu_info
-- ----------------------------
ALTER TABLE "public"."sys_role_menu_info" ADD CONSTRAINT "sys_role_menu_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_info
-- ----------------------------
CREATE INDEX "idx_sys_user_create_time" ON "public"."sys_user_info" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sys_user_status" ON "public"."sys_user_info" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_phone" ON "public"."sys_user_info" USING btree (
  "phone" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;
CREATE UNIQUE INDEX "uk_sys_user_username" ON "public"."sys_user_info" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_info
-- ----------------------------
ALTER TABLE "public"."sys_user_info" ADD CONSTRAINT "sys_user_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_organization_info
-- ----------------------------
CREATE INDEX "idx_sys_user_org_org" ON "public"."sys_user_organization_info" USING btree (
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_org" ON "public"."sys_user_organization_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "org_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;
CREATE UNIQUE INDEX "uk_sys_user_org_main" ON "public"."sys_user_organization_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE is_main = 1 AND delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_organization_info
-- ----------------------------
ALTER TABLE "public"."sys_user_organization_info" ADD CONSTRAINT "sys_user_organization_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_position_info
-- ----------------------------
CREATE INDEX "idx_sys_user_position_position" ON "public"."sys_user_position_info" USING btree (
  "position_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_position" ON "public"."sys_user_position_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "position_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_position_info
-- ----------------------------
ALTER TABLE "public"."sys_user_position_info" ADD CONSTRAINT "sys_user_position_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sys_user_role_info
-- ----------------------------
CREATE INDEX "idx_sys_user_role_role" ON "public"."sys_user_role_info" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "uk_sys_user_role" ON "public"."sys_user_role_info" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST
) WHERE delete_flag = 0;

-- ----------------------------
-- Primary Key structure for table sys_user_role_info
-- ----------------------------
ALTER TABLE "public"."sys_user_role_info" ADD CONSTRAINT "sys_user_role_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table qrtz_blob_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_blob_triggers" ADD CONSTRAINT "fk_qrtz_blob_triggers_triggers" FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "public"."qrtz_triggers" ("sched_name", "trigger_name", "trigger_group") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table qrtz_cron_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_cron_triggers" ADD CONSTRAINT "fk_qrtz_cron_triggers_triggers" FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "public"."qrtz_triggers" ("sched_name", "trigger_name", "trigger_group") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table qrtz_simple_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_simple_triggers" ADD CONSTRAINT "fk_qrtz_simple_triggers_triggers" FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "public"."qrtz_triggers" ("sched_name", "trigger_name", "trigger_group") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table qrtz_simprop_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_simprop_triggers" ADD CONSTRAINT "fk_qrtz_simprop_triggers_triggers" FOREIGN KEY ("sched_name", "trigger_name", "trigger_group") REFERENCES "public"."qrtz_triggers" ("sched_name", "trigger_name", "trigger_group") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table qrtz_triggers
-- ----------------------------
ALTER TABLE "public"."qrtz_triggers" ADD CONSTRAINT "fk_qrtz_triggers_job_details" FOREIGN KEY ("sched_name", "job_name", "job_group") REFERENCES "public"."qrtz_job_details" ("sched_name", "job_name", "job_group") ON DELETE NO ACTION ON UPDATE NO ACTION;
