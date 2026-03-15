package com.company.dental.modules.patient.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PatientTagUpdateRequest {

    @NotNull(message = "标签列表不能为空")
    private List<Long> tagIds;
}
