package com.company.dental.modules.appointment.controller;

import com.company.dental.common.api.ApiResponse;
import com.company.dental.modules.appointment.dto.QueueStatusUpdateRequest;
import com.company.dental.modules.appointment.query.QueueRecordQuery;
import com.company.dental.modules.appointment.service.QueueRecordService;
import com.company.dental.modules.appointment.vo.QueueRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "候诊管理")
@RestController
@RequestMapping("/api/appointments/queues")
public class QueueRecordController {

    private final QueueRecordService queueRecordService;

    public QueueRecordController(QueueRecordService queueRecordService) {
        this.queueRecordService = queueRecordService;
    }

    @Operation(summary = "查询候诊列表")
    @GetMapping
    public ApiResponse<List<QueueRecordVO>> list(@ModelAttribute QueueRecordQuery query) {
        return ApiResponse.success(queueRecordService.listQueues(query));
    }

    @Operation(summary = "更新候诊状态")
    @PreAuthorize("hasAuthority('APPOINTMENT_CHECKIN')")
    @PutMapping("/{id}/status")
    public ApiResponse<QueueRecordVO> updateStatus(@PathVariable Long id,
                                                   @Valid @RequestBody QueueStatusUpdateRequest request) {
        return ApiResponse.success(queueRecordService.updateQueueStatus(id, request));
    }
}
