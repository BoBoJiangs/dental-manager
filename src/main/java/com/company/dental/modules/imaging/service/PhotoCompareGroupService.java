package com.company.dental.modules.imaging.service;

import com.company.dental.modules.imaging.dto.PhotoCompareGroupCreateRequest;
import com.company.dental.modules.imaging.query.PhotoCompareGroupQuery;
import com.company.dental.modules.imaging.vo.PhotoCompareGroupVO;

import java.util.List;

public interface PhotoCompareGroupService {

    List<PhotoCompareGroupVO> listCompareGroups(PhotoCompareGroupQuery query);

    PhotoCompareGroupVO createCompareGroup(PhotoCompareGroupCreateRequest request);
}
