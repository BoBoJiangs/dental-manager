package com.company.dental.modules.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.patient.entity.PatientDoctorRelEntity;
import com.company.dental.modules.patient.vo.PatientDoctorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PatientDoctorRelMapper extends BaseMapper<PatientDoctorRelEntity> {

    @Select("""
            SELECT s.id AS doctorId,
                   s.staff_name AS doctorName,
                   r.clinic_id AS clinicId,
                   c.clinic_name AS clinicName,
                   s.job_title AS jobTitle,
                   s.specialty AS specialty,
                   r.relation_type AS relationType,
                   r.first_related_at AS firstRelatedAt,
                   r.latest_related_at AS latestRelatedAt
            FROM patient_doctor_rel r
            JOIN staff s ON s.id = r.doctor_id
            LEFT JOIN clinic c ON c.id = r.clinic_id
            WHERE r.patient_id = #{patientId}
              AND r.relation_type = 'PRIMARY'
              AND r.status = 1
              AND s.status = 1
              AND s.is_deleted = 0
            ORDER BY r.id
            """)
    List<PatientDoctorVO> selectPrimaryDoctorsByPatientId(@Param("patientId") Long patientId);

    @Select("""
            SELECT s.id AS doctorId,
                   s.staff_name AS doctorName,
                   s.main_clinic_id AS clinicId,
                   c.clinic_name AS clinicName,
                   s.job_title AS jobTitle,
                   s.specialty AS specialty
            FROM staff s
            LEFT JOIN clinic c ON c.id = s.main_clinic_id
            WHERE s.org_id = #{orgId}
              AND s.status = 1
              AND s.is_deleted = 0
              AND s.is_doctor = 1
            ORDER BY s.id
            """)
    List<PatientDoctorVO> selectDoctorOptionsByOrgId(@Param("orgId") Long orgId);
}
