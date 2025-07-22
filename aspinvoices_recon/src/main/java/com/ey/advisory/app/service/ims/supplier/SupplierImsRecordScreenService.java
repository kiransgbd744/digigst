package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import org.springframework.stereotype.Component;
/**
 * 
 *  @author ashutosh.kar
 *
 */

@Component("SupplierImsRecordScreenService")
public interface SupplierImsRecordScreenService {

	List<SupplierImsGstnTrailPopUpResponseDto> getSupplierImsGstnTrailPopupData
		(SupplierImsEntityReqDto criteria);

}
