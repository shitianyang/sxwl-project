package com.sxwl.system.model.dto;

/**
 * 系统用户 DTO（统一请求/响应）
 *
 * <p>同时用于新增、修改和列表返回，password 仅在新增和编辑回显时使用。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysUserDTO {

    /** 用户 ID（新增时为 null，修改和响应时不为空） */
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（新增必填，修改可选，列表返回时不包含） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 创建时间（仅列表返回时填充） */
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
