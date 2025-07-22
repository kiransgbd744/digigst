package com.ey.advisory.app.gstr1a.einv;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;

public interface Gstr1AEinvInitiateReconService {

	public List<Gstr1AEinvInitiateReconGstinDetailsDto> getGstins(
			List<GSTNDetailEntity> gstins, String taxPeriod);

	public String initiatRecon(ArrayList<String> gstinlist, String taxPeriod,
			Long entityId);

	public List<Gstr1AEinvRequesIdWiseDownloadTabDto> getDownloadData(
			Long configId);

	public String initiatPRvsSubmRecon(List<String> gstinlist,
			String fromTaxPeriod, String toTaxPeriod, Long entityId);

	public List<Gstr1AEinvRequesIdWiseDownloadTabDto> getPrVsSubmDownloadData(
			Long configId);

}
