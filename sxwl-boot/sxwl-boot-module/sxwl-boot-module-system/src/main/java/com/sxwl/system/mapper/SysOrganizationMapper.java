package com.sxwl.system.mapper;

import com.sxwl.common.annotation.SxwlDataScope;
import com.sxwl.system.model.dto.SysOrganizationDTO;
import com.sxwl.system.model.entity.SysOrganization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统组织 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysOrganizationMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Mapper
public interface SysOrganizationMapper {

    /**
     * 根据 ID 查询组织
     *
     * @param id 组织 ID
     * @return 组织 DTO
     */
    SysOrganizationDTO getOrganizationById(@Param("id") Long id);

    /**
     * 查询所有组织（按 sort 升序）
     *
     * @return 组织 DTO 列表（平铺）
     */
    @SxwlDataScope
    List<SysOrganizationDTO> selectAllOrganizations();

    /**
     * 统计指定父组织下的子组织数
     *
     * @param parentId 父组织 ID
     * @return 子组织数
     */
    int countChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 校验组织编码是否唯一（排除指定 ID）
     *
     * @param orgCode   组织编码
     * @param excludeId 需要排除的组织 ID（修改时传自身 ID，新增传 null）
     * @return >0 表示已存在
     */
    int checkOrgCodeUnique(@Param("orgCode") String orgCode,
                           @Param("excludeId") Long excludeId);

    /**
     * 新增组织
     *
     * @param entity 组织实体
     * @return 影响行数
     */
    int insertOrganization(SysOrganization entity);

    /**
     * 修改组织
     *
     * @param entity 组织实体
     * @return 影响行数
     */
    int updateOrganization(SysOrganization entity);

    /**
     * 逻辑删除组织
     *
     * @param id 组织 ID
     * @return 影响行数
     */
    int deleteOrganizationById(@Param("id") Long id);
}
