package com.company.dental.modules.emr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.dental.modules.emr.entity.MedicalRecordEntity;
import com.company.dental.modules.emr.query.MedicalRecordPageQuery;
import com.company.dental.modules.emr.vo.MedicalRecordDetailVO;
import com.company.dental.modules.emr.vo.MedicalRecordPageItemVO;
import com.company.dental.modules.patient.vo.PatientVisitRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecordEntity> {

    Page<MedicalRecordPageItemVO> selectMedicalRecordPage(Page<MedicalRecordPageItemVO> page,
                                                          @Param("query") MedicalRecordPageQuery query,
                                                          @Param("orgId") Long orgId,
                                                          @Param("accessScope") String accessScope,
                                                          @Param("clinicIds") List<Long> clinicIds,
                                                          @Param("staffId") Long staffId);

    MedicalRecordDetailVO selectMedicalRecordDetailById(@Param("id") Long id,
                                                        @Param("orgId") Long orgId,
                                                        @Param("accessScope") String accessScope,
                                                        @Param("clinicIds") List<Long> clinicIds,
                                                        @Param("staffId") Long staffId);

    java.util.List<PatientVisitRecordVO> selectPatientVisitRecords(@Param("patientId") Long patientId,
                                                                   @Param("orgId") Long orgId);

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

    @Select("""
            SELECT COUNT(1)
            FROM appointment
            WHERE id = #{appointmentId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidAppointment(@Param("orgId") Long orgId, @Param("appointmentId") Long appointmentId);
}
