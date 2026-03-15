package com.company.dental.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("queue_record")
public class QueueRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String queueNo;
    private String queueStatus;
    private LocalDateTime checkinAt;
    private LocalDateTime callAt;
    private LocalDateTime startTreatmentAt;
    private LocalDateTime endTreatmentAt;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
