package com.sxwl.codegen.model.entity;

/**
 * 代码生成-字段信息配置
 *
 * <p>记录用户手动配置的每个字段的映射规则，关联 {@link SysCodegenTable}。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysCodegenField {

    /** 唯一标识 */
    private Long id;

    /** 关联表 ID */
    private Long tableId;

    /** DB 列名，如 role_code */
    private String columnName;

    /** DB 类型，如 varchar / int8 */
    private String columnType;

    /** DB 列注释 */
    private String columnComment;

    /** Java 类型：String / Long / Integer / LocalDateTime / BigDecimal / Boolean */
    private String javaType;

    /** Java 字段名，如 roleCode */
    private String javaFieldName;

    /** 是否主键：0=否 1=是 */
    private Integer isPk;

    /** 是否在新增时显示：0=否 1=是 */
    private Integer isInsert;

    /** 是否在编辑时显示：0=否 1=是 */
    private Integer isEdit;

    /** 是否在列表展示：0=否 1=是 */
    private Integer isList;

    /** 是否作为查询条件：0=否 1=是 */
    private Integer isQuery;

    /** 查询方式：eq / like / between */
    private String queryType;

    /** 查询表单组件：Input / Select / DateRange */
    private String queryFormType;

    /** 表单组件：Input / Select / TextArea / DatePicker / NumberInput / ImageUpload / Radio / Checkbox */
    private String formType;

    /** Select/Radio 关联的字典编码 */
    private String formDictCode;

    /** 是否必填：0=否 1=是 */
    private Integer isRequired;

    /** 是否唯一性校验：0=否 1=是 */
    private Integer isUnique;

    /** 最大长度 */
    private Integer maxLength;

    /** 排序号 */
    private Integer sort;

    /** 创建时间 */
    private String createTime;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getTableId() { return tableId; }

    public void setTableId(Long tableId) { this.tableId = tableId; }

    public String getColumnName() { return columnName; }

    public void setColumnName(String columnName) { this.columnName = columnName; }

    public String getColumnType() { return columnType; }

    public void setColumnType(String columnType) { this.columnType = columnType; }

    public String getColumnComment() { return columnComment; }

    public void setColumnComment(String columnComment) { this.columnComment = columnComment; }

    public String getJavaType() { return javaType; }

    public void setJavaType(String javaType) { this.javaType = javaType; }

    public String getJavaFieldName() { return javaFieldName; }

    public void setJavaFieldName(String javaFieldName) { this.javaFieldName = javaFieldName; }

    public Integer getIsPk() { return isPk; }

    public void setIsPk(Integer isPk) { this.isPk = isPk; }

    public Integer getIsInsert() { return isInsert; }

    public void setIsInsert(Integer isInsert) { this.isInsert = isInsert; }

    public Integer getIsEdit() { return isEdit; }

    public void setIsEdit(Integer isEdit) { this.isEdit = isEdit; }

    public Integer getIsList() { return isList; }

    public void setIsList(Integer isList) { this.isList = isList; }

    public Integer getIsQuery() { return isQuery; }

    public void setIsQuery(Integer isQuery) { this.isQuery = isQuery; }

    public String getQueryType() { return queryType; }

    public void setQueryType(String queryType) { this.queryType = queryType; }

    public String getQueryFormType() { return queryFormType; }

    public void setQueryFormType(String queryFormType) { this.queryFormType = queryFormType; }

    public String getFormType() { return formType; }

    public void setFormType(String formType) { this.formType = formType; }

    public String getFormDictCode() { return formDictCode; }

    public void setFormDictCode(String formDictCode) { this.formDictCode = formDictCode; }

    public Integer getIsRequired() { return isRequired; }

    public void setIsRequired(Integer isRequired) { this.isRequired = isRequired; }

    public Integer getIsUnique() { return isUnique; }

    public void setIsUnique(Integer isUnique) { this.isUnique = isUnique; }

    public Integer getMaxLength() { return maxLength; }

    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public Integer getSort() { return sort; }

    public void setSort(Integer sort) { this.sort = sort; }

    public String getCreateTime() { return createTime; }

    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
