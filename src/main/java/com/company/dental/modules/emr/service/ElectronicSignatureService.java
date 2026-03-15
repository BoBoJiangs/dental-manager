package com.company.dental.modules.emr.service;

import com.company.dental.modules.emr.dto.ElectronicSignatureCreateRequest;
import com.company.dental.modules.emr.query.ElectronicSignatureQuery;
import com.company.dental.modules.emr.vo.ElectronicSignatureVO;

import java.util.List;

public interface ElectronicSignatureService {

    List<ElectronicSignatureVO> listSignatures(ElectronicSignatureQuery query);

    ElectronicSignatureVO createSignature(ElectronicSignatureCreateRequest request);
}
