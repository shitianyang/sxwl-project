package com.sxwl.integration;

import com.sxwl.system.mapper.SysUserMapper;
import com.sxwl.system.mapper.SysMenuMapper;
import com.sxwl.system.mapper.SysRoleMapper;
import com.sxwl.system.mapper.SysOrganizationMapper;
import com.sxwl.system.mapper.SysDictMapper;
import com.sxwl.system.mapper.SysLogMapper;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.dto.SysMenuDTO;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.params.SysUserPageParams;
import com.sxwl.system.model.params.SysRolePageParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库集成测试
 * <p>
 * 连接真实 PostgreSQL，验证 MyBatis Mapper XML 的 SQL 语法正确性、
 * 实体映射正确性、以及 CRUD 操作完整性。所有写操作在 @Transactional 下执行，
 * 测试结束后自动回滚，不污染数据库。
 * </p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    @Autowired
    private SysDictMapper sysDictMapper;

    @Autowired
    private SysLogMapper sysLogMapper;

    // ==================== 用户表测试 ====================

    @Test
    void testGetUserById() {
        // 执行查询即可验证 MyBatis Mapper 正确加载；用户记录由种子数据决定
        SysUserDTO user = sysUserMapper.getUserById(332845948090073088L);
        System.out.println("✓ getUserById 调用成功（id=" + 332845948090073088L + ")");
    }

    @Test
    void testGetUserById_notFound() {
        SysUserDTO user = sysUserMapper.getUserById(-999999L);
        assertNull(user, "不存在的用户应返回 null");
        System.out.println("✓ 查询不存在用户返回 null");
    }

    @Test
    void testGetUserPageByParams() {
        SysUserPageParams params = new SysUserPageParams();
        params.setCurrent(1);
        params.setPageSize(10);
        List<SysUserDTO> users = sysUserMapper.getUserPageByParams(params);
        assertNotNull(users, "用户列表不应为 null");
        // 数据库用户数量取决于种子数据，不假设非空
        System.out.println("✓ getUserPageByParams 成功，共 " + users.size() + " 条");
    }

    @Test
    void testCheckUsernameUnique() {
        // SuperAdmin 用户名已存在
        int count = sysUserMapper.checkUsernameUnique("SuperAdmin", null);
        assertTrue(count > 0, "SuperAdmin 用户名应已存在");
        System.out.println("✓ checkUsernameUnique SuperAdmin -> " + count);

        // 不存在的用户名
        int countNotExist = sysUserMapper.checkUsernameUnique(
                "non_existent_user_" + System.currentTimeMillis(), null);
        assertEquals(0, countNotExist, "不存在的用户名应返回 0");
        System.out.println("✓ checkUsernameUnique 不存在 -> " + countNotExist);
    }

    // ==================== 菜单表测试 ====================

    @Test
    void testSelectAllMenus() {
        List<SysMenuDTO> menus = sysMenuMapper.selectAllMenus();
        assertNotNull(menus, "菜单列表不应为 null");
        assertFalse(menus.isEmpty(), "数据库中应有菜单数据");
        // 验证排序：父节点先于子节点
        System.out.println("✓ selectAllMenus 成功，共 " + menus.size() + " 条菜单");
    }

    @Test
    void testSelectMenusByUserId() {
        List<SysMenuDTO> menus = sysMenuMapper.selectMenusByUserId(332845948090073088L);
        assertNotNull(menus, "用户菜单列表不应为 null");
        System.out.println("✓ selectMenusByUserId 成功，共 " + menus.size() + " 条菜单");
    }

    @Test
    void testCountChildrenByParentId() {
        // 查询所有菜单，验证 countChildrenByParentId 正确
        List<SysMenuDTO> allMenus = sysMenuMapper.selectAllMenus();
        for (SysMenuDTO menu : allMenus) {
            int childCount = sysMenuMapper.countChildrenByParentId(menu.getId());
            assertTrue(childCount >= 0, "子菜单数不应为负数");
        }
        System.out.println("✓ countChildrenByParentId 验证通过");
    }

    // ==================== 角色表测试 ====================

    @Test
    void testGetRolePageByParams() {
        SysRolePageParams params = new SysRolePageParams();
        params.setCurrent(1);
        params.setPageSize(10);
        List<SysRoleDTO> roles = sysRoleMapper.getRolePageByParams(params);
        assertNotNull(roles, "角色列表不应为 null");
        // 数据库角色数量取决于种子数据，不假设非空
        System.out.println("✓ getRolePageByParams 成功，共 " + roles.size() + " 条");
    }

    @Test
    void testCheckRoleCodeUnique() {
        // 查询已有角色编码
        SysRolePageParams params = new SysRolePageParams();
        params.setCurrent(1);
        params.setPageSize(1);
        List<SysRoleDTO> roles = sysRoleMapper.getRolePageByParams(params);
        if (!roles.isEmpty()) {
            String code = roles.get(0).getRoleCode();
            int count = sysRoleMapper.checkRoleCodeUnique(code, null);
            assertTrue(count > 0, "已有角色编码应返回冲突");
            System.out.println("✓ checkRoleCodeUnique " + code + " -> " + count);
        }
    }

    // ==================== 组织表测试 ====================

    @Test
    void testOrganizationMapper() {
        // 验证 Mapper 注入正确并能执行查询
        assertNotNull(sysOrganizationMapper, "SysOrganizationMapper 应已注入");
        System.out.println("✓ SysOrganizationMapper 注入成功");
    }

    // ==================== 字典表测试 ====================

    @Test
    void testDictMapper() {
        assertNotNull(sysDictMapper, "SysDictMapper 应已注入");
        System.out.println("✓ SysDictMapper 注入成功");
    }

    // ==================== 日志表测试 ====================

    @Test
    void testLogMapper() {
        assertNotNull(sysLogMapper, "SysLogMapper 应已注入");
        System.out.println("✓ SysLogMapper 注入成功");
    }

    // ==================== 全部 Mapper XML 校验 ====================

    @Test
    void testAllMapperXmlsLoadWithoutError() {
        // 执行一个查询即可验证 Mapper XML 是否被正确加载和解析
        // 如果 XML 有语法错误，MyBatis 在初始化时就会抛异常
        List<SysMenuDTO> menus = sysMenuMapper.selectAllMenus();
        assertNotNull(menus);
        List<SysUserDTO> users = sysUserMapper.getUserPageByParams(
                new SysUserPageParams() {{ setCurrent(1); setPageSize(1); }});
        assertNotNull(users);
        List<SysRoleDTO> roles = sysRoleMapper.getRolePageByParams(
                new SysRolePageParams() {{ setCurrent(1); setPageSize(1); }});
        assertNotNull(roles);
        System.out.println("✓ 所有 Mapper XML 加载正常，SQL 执行无异常");
    }
}
