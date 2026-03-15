package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.emr.entity.ConsentFormRecordEntity;
import com.company.dental.modules.emr.query.ConsentFormQuery;
import com.company.dental.modules.emr.vo.ConsentFormVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsentFormRecordMapper extends BaseMapper<ConsentFormRecordEntity> {

    List<ConsentFormVO> selectConsentForms(@Param("query") ConsentFormQuery query,
                                           @Param("orgId") Long orgId,
                                           @Param("accessScope") String accessScope,
                                           @Param("clinicIds") List<Long> clinicIds,
                                           @Param("staffId") Long staffId);

    ConsentFormVO selectConsentFormDetailById(@Param("id") Long id,
                                              @Param("orgId") Long orgId,
                                              @Param("accessScope") String accessScope,
                                              @Param("clinicIds") List<Long> clinicIds,
                                              @Param("staffId") Long staffId);
}
