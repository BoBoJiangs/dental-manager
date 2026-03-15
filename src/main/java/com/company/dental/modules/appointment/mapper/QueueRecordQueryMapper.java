package com.company.dental.modules.appointment.mapper;

import com.company.dental.modules.appointment.query.QueueRecordQuery;
import com.company.dental.modules.appointment.vo.QueueRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QueueRecordQueryMapper {

    List<QueueRecordVO> selectQueueList(@Param("query") QueueRecordQuery query,
                                        @Param("orgId") Long orgId,
                                        @Param("accessScope") String accessScope,
                                        @Param("clinicIds") List<Long> clinicIds,
                                        @Param("staffId") Long staffId);

    QueueRecordVO selectQueueDetailById(@Param("id") Long id,
                                        @Param("orgId") Long orgId,
                                        @Param("accessScope") String accessScope,
                                        @Param("clinicIds") List<Long> clinicIds,
                                        @Param("staffId") Long staffId);
}
