package com.company.dental.modules.imaging.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.imaging.entity.PhotoCompareGroupEntity;
import com.company.dental.modules.imaging.query.PhotoCompareGroupQuery;
import com.company.dental.modules.imaging.vo.PhotoCompareGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PhotoCompareGroupMapper extends BaseMapper<PhotoCompareGroupEntity> {

    List<PhotoCompareGroupVO> selectCompareGroupList(@Param("query") PhotoCompareGroupQuery query,
                                                     @Param("orgId") Long orgId,
                                                     @Param("accessScope") String accessScope,
                                                     @Param("clinicIds") List<Long> clinicIds,
                                                     @Param("staffId") Long staffId);

    PhotoCompareGroupVO selectCompareGroupDetailById(@Param("id") Long id,
                                                     @Param("orgId") Long orgId,
                                                     @Param("accessScope") String accessScope,
                                                     @Param("clinicIds") List<Long> clinicIds,
                                                     @Param("staffId") Long staffId);
}
