package com.company.dental.modules.appointment.service;

import com.company.dental.common.api.PageResult;
import com.company.dental.modules.appointment.dto.AppointmentCancelRequest;
import com.company.dental.modules.appointment.dto.AppointmentCheckInRequest;
import com.company.dental.modules.appointment.dto.AppointmentCreateRequest;
import com.company.dental.modules.appointment.dto.AppointmentRescheduleRequest;
import com.company.dental.modules.appointment.query.AppointmentPageQuery;
import com.company.dental.modules.appointment.vo.AppointmentDetailVO;
import com.company.dental.modules.appointment.vo.AppointmentPageItemVO;

public interface AppointmentService {

    PageResult<AppointmentPageItemVO> pageAppointments(AppointmentPageQuery query);

    AppointmentDetailVO getAppointmentDetail(Long appointmentId);

    AppointmentDetailVO createAppointment(AppointmentCreateRequest request);

    AppointmentDetailVO rescheduleAppointment(Long appointmentId, AppointmentRescheduleRequest request);

    AppointmentDetailVO cancelAppointment(Long appointmentId, AppointmentCancelRequest request);

    AppointmentDetailVO checkInAppointment(Long appointmentId, AppointmentCheckInRequest request);
}
