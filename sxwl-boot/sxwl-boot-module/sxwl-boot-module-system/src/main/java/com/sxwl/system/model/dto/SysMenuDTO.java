package com.sxwl.system.model.dto;

import com.sxwl.common.entity.SxwlTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统菜单 DTO（统一请求/响应 + 树形结构）
 *
 * <p>实现 {@link SxwlTreeNode} 接口，可使用 {@link com.sxwl.common.utils.SxwlTreeUtils#buildTree(List)}
 * 构建树结构。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysMenuDTO implements SxwlTreeNode<SysMenuDTO> {

    /** 菜单 ID */
    private Long id;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单 ID */
    private Long parentId;

    /** 祖先路径 */
    private String ancestors;

    /** 菜单类型：1=目录 2=菜单 3=按钮 */
    private Integer menuType;

    /** 路由路径 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 权限标识 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 是否外链：0=内嵌 1=外链 */
    private Integer isFrame;

    /** 是否缓存：0=不缓存 1=缓存 */
    private Integer isCache;

    /** 排序号 */
    private Integer sort;

    /** 是否可见：0=隐藏 1=显示 */
    private Integer visible;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 描述说明 */
    private String description;

    /** 子菜单列表（树结构） */
    private List<SysMenuDTO> children;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getIsFrame() {
        return isFrame;
    }

    public void setIsFrame(Integer isFrame) {
        this.isFrame = isFrame;
    }

    public Integer getIsCache() {
        return isCache;
    }

    public void setIsCache(Integer isCache) {
        this.isCache = isCache;
    }

    @Override
    public Integer getSortValue() {
        return sort != null ? sort : 0;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<SysMenuDTO> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<SysMenuDTO> children) {
        this.children = children;
    }
}
