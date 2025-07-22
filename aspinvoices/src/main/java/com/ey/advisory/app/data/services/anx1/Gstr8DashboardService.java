package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;

/**
 * @author Shashikant.Shukla
 *
 */
public interface Gstr8DashboardService {

	List<GstinDto> getAllSupplierGstins(List<Long> entityId);

}
