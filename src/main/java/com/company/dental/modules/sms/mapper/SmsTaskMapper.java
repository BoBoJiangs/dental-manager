package com.company.dental.modules.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.sms.entity.SmsTaskEntity;
import com.company.dental.modules.sms.query.SmsTaskPageQuery;
import com.company.dental.modules.sms.vo.SmsTaskDetailVO;
import com.company.dental.modules.sms.vo.SmsTaskPageItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmsTaskMapper extends BaseMapper<SmsTaskEntity> {

    Page<SmsTaskPageItemVO> selectSmsTaskPage(Page<SmsTaskPageItemVO> page,
                                              @Param("query") SmsTaskPageQuery query,
                                              @Param("orgId") Long orgId,
                                              @Param("accessScope") String accessScope,
                                              @Param("clinicIds") List<Long> clinicIds,
                                              @Param("staffId") Long staffId);

    SmsTaskDetailVO selectSmsTaskDetailById(@Param("id") Long id,
                                            @Param("orgId") Long orgId,
                                            @Param("accessScope") String accessScope,
                                            @Param("clinicIds") List<Long> clinicIds,
                                            @Param("staffId") Long staffId);
}
