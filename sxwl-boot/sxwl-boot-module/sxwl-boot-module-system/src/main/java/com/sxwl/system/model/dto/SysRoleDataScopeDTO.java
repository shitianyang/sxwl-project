package com.sxwl.system.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色数据权限分配请求 DTO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysRoleDataScopeDTO {

    @NotNull
    private List<Long> orgIds;

    public List<Long> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<Long> orgIds) {
        this.orgIds = orgIds;
    }
}
