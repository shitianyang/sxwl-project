package com.sxwl.common.utils;

import com.sxwl.common.entity.SxwlTreeNode;

import java.util.*;

/**
 * 通用树结构构建工具
 *
 * <p>将平铺的节点列表递归构建为树形结构，适用于菜单、组织等具有 parentId 自引用的场景。</p>
 *
 * <p><b>使用前提：</b>节点类型必须实现 {@link SxwlTreeNode} 接口。</p>
 *
 * <p><b>树构建规则：</b></p>
 * <ul>
 *   <li>parentId == 0 或 parentId == null 的节点作为根节点</li>
 *   <li>非根节点根据 parentId 挂载到对应父节点的 children 列表中</li>
 *   <li>每个层级按 {@link SxwlTreeNode#getSortValue()} 升序排序</li>
 *   <li>找不到父节点的孤儿节点自动提升为根节点</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public final class SxwlTreeUtils {

    private SxwlTreeUtils() {
        throw new UnsupportedOperationException("SxwlTreeUtils 工具类，不允许实例化");
    }

    /**
     * 将平铺节点列表构建为树结构
     *
     * @param nodes 平铺节点列表
     * @param <T>   实现了 SxwlTreeNode 的节点类型
     * @return 根节点列表（含完整子树）
     */
    public static <T extends SxwlTreeNode<T>> List<T> buildTree(List<T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 建立 id → node 索引
        Map<Long, T> nodeMap = new LinkedHashMap<>(nodes.size());
        for (T node : nodes) {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getId(), node);
        }

        // 2. 构建父子关系
        List<T> roots = new ArrayList<>();
        for (T node : nodes) {
            Long parentId = node.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(node);
            } else {
                T parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    // 父节点不存在，提升为根
                    roots.add(node);
                }
            }
        }

        // 3. 递归排序
        sortChildren(roots);

        return roots;
    }

    /**
     * 递归对每层子节点按 sortValue 升序排序
     */
    @SuppressWarnings("unchecked")
    private static <T extends SxwlTreeNode<T>> void sortChildren(List<T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.sort(Comparator.comparingInt(T::getSortValue));
        for (T node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortChildren(node.getChildren());
            }
        }
    }
}
