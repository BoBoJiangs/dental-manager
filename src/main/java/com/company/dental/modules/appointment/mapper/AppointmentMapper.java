package com.company.dental.modules.appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.appointment.entity.AppointmentEntity;
import com.company.dental.modules.appointment.query.AppointmentPageQuery;
import com.company.dental.modules.appointment.vo.AppointmentDetailVO;
import com.company.dental.modules.appointment.vo.AppointmentPageItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppointmentMapper extends BaseMapper<AppointmentEntity> {

    Page<AppointmentPageItemVO> selectAppointmentPage(Page<AppointmentPageItemVO> page,
                                                      @Param("query") AppointmentPageQuery query,
                                                      @Param("orgId") Long orgId,
                                                      @Param("accessScope") String accessScope,
                                                      @Param("clinicIds") List<Long> clinicIds,
                                                      @Param("staffId") Long staffId);

    AppointmentDetailVO selectAppointmentDetailById(@Param("id") Long id,
                                                    @Param("orgId") Long orgId,
                                                    @Param("accessScope") String accessScope,
                                                    @Param("clinicIds") List<Long> clinicIds,
                                                    @Param("staffId") Long staffId);

    @Select("""
            SELECT COUNT(1)
            FROM patient
            WHERE id = #{patientId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidPatient(@Param("orgId") Long orgId, @Param("patientId") Long patientId);

    @Select("""
            SELECT COUNT(1)
            FROM clinic
            WHERE id = #{clinicId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
            """)
    Long countValidClinic(@Param("orgId") Long orgId, @Param("clinicId") Long clinicId);

    @Select("""
            SELECT COUNT(1)
            FROM staff
            WHERE id = #{doctorId}
              AND org_id = #{orgId}
              AND status = 1
              AND is_deleted = 0
              AND is_doctor = 1
            """)
    Long countValidDoctor(@Param("orgId") Long orgId, @Param("doctorId") Long doctorId);
}
