package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.List;

import org.javatuples.Pair;

import com.aspose.cells.Workbook;

public interface VendorMasterUploadDao {

	public Pair<List<VendorMasterUploadEntityDto>, Integer> getReconResult(
			List<String> reciepientPANList, List<String> vendorGSTList,
			int pageSize, int pageNumber);
	
	public Workbook downloadReconResult(List<String> reciepientPANList, List<String> vendorGSTList);
}
