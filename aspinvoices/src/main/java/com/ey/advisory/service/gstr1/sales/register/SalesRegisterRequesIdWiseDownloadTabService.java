package com.ey.advisory.service.gstr1.sales.register;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface SalesRegisterRequesIdWiseDownloadTabService {

	public List<Gstr2RequesIdWiseDownloadTabDto> getDownloadData(Long configId);
}
