package com.company.dental.modules.appointment.service;

import com.company.dental.modules.appointment.dto.QueueStatusUpdateRequest;
import com.company.dental.modules.appointment.query.QueueRecordQuery;
import com.company.dental.modules.appointment.vo.QueueRecordVO;

import java.util.List;

public interface QueueRecordService {

    List<QueueRecordVO> listQueues(QueueRecordQuery query);

    QueueRecordVO updateQueueStatus(Long queueRecordId, QueueStatusUpdateRequest request);
}
