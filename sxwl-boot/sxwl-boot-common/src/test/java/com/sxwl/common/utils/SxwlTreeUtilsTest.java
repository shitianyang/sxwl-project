package com.sxwl.common.utils;

import com.sxwl.common.entity.SxwlTreeNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通用树结构构建工具单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlTreeUtils 工具类测试")
class SxwlTreeUtilsTest {

    /** 测试用树节点实现 */
    private static class TestTreeNode implements SxwlTreeNode<TestTreeNode> {
        private final Long id;
        private final Long parentId;
        private final String name;
        private final int sortValue;
        private List<TestTreeNode> children;

        TestTreeNode(Long id, Long parentId, String name, int sortValue) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
            this.sortValue = sortValue;
        }

        @Override
        public Long getId() { return id; }

        @Override
        public Long getParentId() { return parentId; }

        @Override
        public List<TestTreeNode> getChildren() { return children; }

        @Override
        public void setChildren(List<TestTreeNode> children) { this.children = children; }

        @Override
        public Integer getSortValue() { return sortValue; }

        public String getName() { return name; }
    }

    @Test
    @DisplayName("平铺列表构建树：根节点正确，子节点挂载正确")
    void testBuildTree() {
        List<TestTreeNode> nodes = Arrays.asList(
                new TestTreeNode(1L, 0L, "根节点1", 1),
                new TestTreeNode(2L, 0L, "根节点2", 2),
                new TestTreeNode(3L, 1L, "子节点1-1", 1),
                new TestTreeNode(4L, 1L, "子节点1-2", 2),
                new TestTreeNode(5L, 3L, "子节点3-1", 1)
        );

        List<TestTreeNode> tree = SxwlTreeUtils.buildTree(nodes);
        assertEquals(2, tree.size(), "应有 2 个根节点");
        assertEquals("根节点1", tree.get(0).getName());

        // 根节点1 应有 2 个子节点
        TestTreeNode root1 = tree.get(0);
        assertNotNull(root1.getChildren());
        assertEquals(2, root1.getChildren().size());

        // 子节点 3 应有 1 个子节点
        TestTreeNode child3 = root1.getChildren().get(0);
        assertEquals("子节点1-1", child3.getName());
        assertNotNull(child3.getChildren());
        assertEquals(1, child3.getChildren().size());
        assertEquals("子节点3-1", child3.getChildren().get(0).getName());
    }

    @Test
    @DisplayName("空列表返回空树")
    void testBuildTreeEmpty() {
        List<TestTreeNode> tree = SxwlTreeUtils.<TestTreeNode>buildTree(Collections.emptyList());
        assertTrue(tree.isEmpty());
    }

    @Test
    @DisplayName("null 输入返回空树")
    void testBuildTreeNull() {
        List<TestTreeNode> tree = SxwlTreeUtils.buildTree(null);
        assertTrue(tree.isEmpty());
    }

    @Test
    @DisplayName("仅根节点：无子节点时树构建正确")
    void testBuildTreeOnlyRoots() {
        List<TestTreeNode> nodes = Arrays.asList(
                new TestTreeNode(1L, 0L, "根1", 1),
                new TestTreeNode(2L, 0L, "根2", 2)
        );
        List<TestTreeNode> tree = SxwlTreeUtils.buildTree(nodes);
        assertEquals(2, tree.size());
        tree.forEach(node -> assertTrue(node.getChildren().isEmpty()));
    }

    @Test
    @DisplayName("孤儿节点自动提升为根节点")
    void testOrphanNodePromoted() {
        List<TestTreeNode> nodes = new ArrayList<>();
        nodes.add(new TestTreeNode(1L, 0L, "根节点", 1));
        nodes.add(new TestTreeNode(2L, 99L, "孤儿节点", 2)); // parentId=99 不存在

        List<TestTreeNode> tree = SxwlTreeUtils.buildTree(nodes);
        assertEquals(2, tree.size(), "孤儿节点应提升为根节点");
    }

    @Test
    @DisplayName("同层节点按 sortValue 升序排列")
    void testSortOrder() {
        List<TestTreeNode> nodes = Arrays.asList(
                new TestTreeNode(1L, 0L, "根-后", 2),
                new TestTreeNode(2L, 0L, "根-前", 1),
                new TestTreeNode(3L, 1L, "子-后", 2),
                new TestTreeNode(4L, 1L, "子-前", 1)
        );

        List<TestTreeNode> tree = SxwlTreeUtils.buildTree(nodes);
        assertEquals("根-前", tree.get(0).getName(), "根节点按 sort 升序");
        assertEquals("根-后", tree.get(1).getName());

        // 子节点挂载在 id=1（根-后）下
        TestTreeNode root2 = tree.get(1);
        assertEquals("根-后", root2.getName());
        assertEquals("子-前", root2.getChildren().get(0).getName(), "子节点按 sort 升序");
        assertEquals("子-后", root2.getChildren().get(1).getName());
    }

    @Test
    @DisplayName("深层嵌套节点树构建正确")
    void testDeepNesting() {
        List<TestTreeNode> nodes = Arrays.asList(
                new TestTreeNode(1L, 0L, "L1", 1),
                new TestTreeNode(2L, 1L, "L2", 1),
                new TestTreeNode(3L, 2L, "L3", 1),
                new TestTreeNode(4L, 3L, "L4", 1)
        );

        List<TestTreeNode> tree = SxwlTreeUtils.buildTree(nodes);
        assertEquals(1, tree.size());
        assertEquals("L1", tree.get(0).getName());
        assertEquals("L2", tree.get(0).getChildren().get(0).getName());
        assertEquals("L3", tree.get(0).getChildren().get(0).getChildren().get(0).getName());
        assertEquals("L4", tree.get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getName());
    }
}
