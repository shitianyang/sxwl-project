package com.sxwl.system.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色菜单分配请求 DTO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysRoleMenuGrantDTO {

    @NotNull
    private List<Long> menuIds;

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }
}
