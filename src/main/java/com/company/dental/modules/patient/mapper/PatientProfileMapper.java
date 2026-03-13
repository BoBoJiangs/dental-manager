package com.company.dental.modules.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.dental.modules.patient.entity.PatientProfileEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientProfileMapper extends BaseMapper<PatientProfileEntity> {
}
