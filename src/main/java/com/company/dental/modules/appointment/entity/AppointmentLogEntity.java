package com.company.dental.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("appointment_log")
public class AppointmentLogEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private String actionType;
    private String beforeData;
    private String afterData;
    private Long actionBy;
    private LocalDateTime actionAt;
    private String remark;
}
