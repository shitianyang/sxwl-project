/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysOrganizationDTO 测试")
class SysOrganizationDTOTest {
    @Test
    void testGettersAndSetters() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L); dto.setOrgCode("HQ"); dto.setOrgName("总部"); dto.setParentId(0L);
        dto.setAncestors("0"); dto.setOrgLevel(1); dto.setOrgType("company"); dto.setLeaderId(1L);
        dto.setPhone("010-8888"); dto.setSort(1); dto.setStatus(1); dto.setDescription("描述");
        SysOrganizationDTO child = new SysOrganizationDTO();
        dto.setChildren(List.of(child));
        assertEquals(1L, dto.getId()); assertEquals("HQ", dto.getOrgCode());
        assertEquals("总部", dto.getOrgName()); assertEquals(0L, dto.getParentId());
        assertEquals("0", dto.getAncestors()); assertEquals(1, dto.getOrgLevel());
        assertEquals("company", dto.getOrgType()); assertEquals(1L, dto.getLeaderId());
        assertEquals("010-8888", dto.getPhone()); assertEquals(1, dto.getSort());
        assertEquals(1, dto.getStatus()); assertEquals("描述", dto.getDescription());
        assertEquals(1, dto.getChildren().size());
    }
    @Test
    void testSortValue() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        assertEquals(0, dto.getSortValue());
        dto.setSort(5);
        assertEquals(5, dto.getSortValue());
    }
}
