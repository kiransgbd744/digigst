package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.List;

import com.aspose.cells.Workbook;
import com.ey.advisory.gstr2.userdetails.GstinDto;

public interface VendorMasterReportService {
	Workbook getVendorMasterErrorReport(Long batchId, String refId,String typeOfFlag);

	List<String> getListOfvendorGstinList(List<String> recipientGstinList,
			boolean flag);

	List<VendorGstinDto> getListOfvendorPan(List<String> recipientGstinList,
			boolean flag);

	List<String> getListOfvendorName(List<String> recipientGstinList,
			List<String> vendorPanList, List<String> vendorGstInList);

	List<VendorGstinDto> getvendorNameForGstin(List<String> vendorGstInList,
			Long entityId);

	List<String> getListOfvendorCode(List<String> vendorNameList,
			List<String> vendorGstInList, Long entityId);

	List<VendorGstinDto> getListOfvendorGstinList(Long entityId);

	List<GstinDto> getListOfRecipientPan(List<String> recipientPanList);

	List<GstinDto> getListOfRecipientGstin(Long entityId);

	List<GstinDto> getListOfVendorPans(Long entityId);

	List<GstinDto> getListOfVendorGstin(List<String> vendorPans, Long entityId);

	List<GstinDto> getListOfNonComplaintVendorPans(Long entityId);

	List<GstinDto> getListOfNonComplaintVendorGstin(List<String> vendorPans,
			Long entityId);
}
