package com.company.dental.modules.imaging.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.imaging.entity.PatientImageEntity;
import com.company.dental.modules.imaging.query.PatientImageQuery;
import com.company.dental.modules.imaging.vo.PatientImageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PatientImageMapper extends BaseMapper<PatientImageEntity> {

    List<PatientImageVO> selectPatientImages(@Param("query") PatientImageQuery query,
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
            FROM medical_record
            WHERE id = #{medicalRecordId}
              AND org_id = #{orgId}
              AND is_deleted = 0
            """)
    Long countValidMedicalRecord(@Param("orgId") Long orgId, @Param("medicalRecordId") Long medicalRecordId);

    @Select("""
            SELECT COUNT(1)
            FROM file_attachment
            WHERE id = #{fileId}
              AND org_id = #{orgId}
              AND is_deleted = 0
              AND file_status = 1
            """)
    Long countValidFile(@Param("orgId") Long orgId, @Param("fileId") Long fileId);
}
