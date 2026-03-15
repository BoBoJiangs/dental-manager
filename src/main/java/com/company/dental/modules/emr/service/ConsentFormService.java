package com.company.dental.modules.emr.service;

import com.company.dental.modules.emr.dto.ConsentFormCreateRequest;
import com.company.dental.modules.emr.query.ConsentFormQuery;
import com.company.dental.modules.emr.query.PrintTemplateQuery;
import com.company.dental.modules.emr.vo.ConsentFormVO;
import com.company.dental.modules.emr.vo.PrintTemplateVO;

import java.util.List;

public interface ConsentFormService {

    List<PrintTemplateVO> listPrintTemplates(PrintTemplateQuery query);

    List<ConsentFormVO> listConsentForms(ConsentFormQuery query);

    ConsentFormVO getConsentFormDetail(Long consentFormId);

    ConsentFormVO createConsentForm(ConsentFormCreateRequest request);

    ConsentFormVO updateConsentForm(Long consentFormId, ConsentFormCreateRequest request);
}
