package com.company.dental.modules.appointment.service;

import com.company.dental.modules.appointment.dto.DoctorScheduleCreateRequest;
import com.company.dental.modules.appointment.query.DoctorScheduleQuery;
import com.company.dental.modules.appointment.vo.DoctorScheduleVO;

import java.util.List;

public interface DoctorScheduleService {

    List<DoctorScheduleVO> listSchedules(DoctorScheduleQuery query);

    DoctorScheduleVO createSchedule(DoctorScheduleCreateRequest request);
}
