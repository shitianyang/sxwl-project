package com.sxwl.common.entity;

import java.util.List;

/**
 * 树形节点通用接口
 *
 * <p>实现此接口的 DTO / VO 可使用 {@link com.sxwl.common.utils.SxwlTreeUtils#buildTree(List)}
 * 将平铺列表递归构建为树结构。</p>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * public class SysMenuDTO implements SxwlTreeNode<SysMenuDTO> {
 *     private Long id;
 *     private Long parentId;
 *     private List<SysMenuDTO> children;
 *     // getter/setter...
 * }
 * }</pre>
 *
 * @param <T> 自身类型，实现 Comparable 接口的变体
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public interface SxwlTreeNode<T extends SxwlTreeNode<T>> {

    /**
     * 获取节点 ID
     *
     * @return 节点 ID
     */
    Long getId();

    /**
     * 获取父节点 ID（根节点返回 0）
     *
     * @return 父节点 ID
     */
    Long getParentId();

    /**
     * 获取子节点列表
     *
     * @return 子节点列表
     */
    List<T> getChildren();

    /**
     * 设置子节点列表
     *
     * @param children 子节点列表
     */
    void setChildren(List<T> children);

    /**
     * 获取排序值（可选覆写，用于同层节点排序）
     *
     * @return 排序值，默认返回 0
     */
    default Integer getSortValue() {
        return 0;
    }
}
