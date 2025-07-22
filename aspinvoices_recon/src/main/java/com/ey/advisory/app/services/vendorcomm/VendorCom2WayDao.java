package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import org.javatuples.Pair;

import com.ey.advisory.app.vendorcomm.dto.VendorReportDownloadDto;
import com.ey.advisory.app.vendorcomm.dto.VendorResponseDataDto;

public interface VendorCom2WayDao {

	public Pair <List<VendorResponseDataDto>, Integer> getVendrData(
			VendorReportDownloadDto jsonDto, int pageNum, int pageLimit);

}
