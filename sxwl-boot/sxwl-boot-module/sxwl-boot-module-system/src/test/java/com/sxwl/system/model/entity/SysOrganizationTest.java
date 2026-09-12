/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysOrganization 实体测试")
class SysOrganizationTest {
    @Test
    void testGettersAndSetters() {
        SysOrganization entity = new SysOrganization();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setOrgCode("DEPT"); entity.setOrgName("技术部"); entity.setParentId(0L);
        entity.setAncestors("0"); entity.setOrgLevel(2); entity.setOrgType("DEPT"); entity.setLeaderId(100L);
        entity.setPhone("123456"); entity.setSort(1); entity.setStatus(1); entity.setDescription("描述");
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals("DEPT", entity.getOrgCode());
        assertEquals("技术部", entity.getOrgName()); assertEquals(0L, entity.getParentId());
        assertEquals("0", entity.getAncestors()); assertEquals(2, entity.getOrgLevel());
        assertEquals("DEPT", entity.getOrgType()); assertEquals(100L, entity.getLeaderId());
        assertEquals("123456", entity.getPhone()); assertEquals(1, entity.getSort());
        assertEquals(1, entity.getStatus()); assertEquals("描述", entity.getDescription());
    }
    @Test void testNoArgsConstructor() { SysOrganization e = new SysOrganization(); assertNull(e.getId()); assertNull(e.getOrgCode()); }
}
