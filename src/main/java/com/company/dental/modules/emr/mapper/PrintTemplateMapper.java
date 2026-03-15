package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.emr.entity.PrintTemplateEntity;
import com.company.dental.modules.emr.query.PrintTemplateQuery;
import com.company.dental.modules.emr.vo.PrintTemplateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PrintTemplateMapper extends BaseMapper<PrintTemplateEntity> {

    List<PrintTemplateVO> selectTemplateList(@Param("query") PrintTemplateQuery query,
                                             @Param("orgId") Long orgId,
                                             @Param("accessScope") String accessScope,
                                             @Param("clinicIds") List<Long> clinicIds,
                                             @Param("staffId") Long staffId);
}
