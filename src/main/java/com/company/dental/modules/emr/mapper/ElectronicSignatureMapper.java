package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.emr.entity.ElectronicSignatureEntity;
import com.company.dental.modules.emr.query.ElectronicSignatureQuery;
import com.company.dental.modules.emr.vo.ElectronicSignatureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ElectronicSignatureMapper extends BaseMapper<ElectronicSignatureEntity> {

    List<ElectronicSignatureVO> selectSignatureList(@Param("query") ElectronicSignatureQuery query,
                                                    @Param("orgId") Long orgId,
                                                    @Param("accessScope") String accessScope,
                                                    @Param("clinicIds") List<Long> clinicIds,
                                                    @Param("staffId") Long staffId);

    ElectronicSignatureVO selectSignatureDetailById(@Param("id") Long id,
                                                    @Param("orgId") Long orgId,
                                                    @Param("accessScope") String accessScope,
                                                    @Param("clinicIds") List<Long> clinicIds,
                                                    @Param("staffId") Long staffId);
}
