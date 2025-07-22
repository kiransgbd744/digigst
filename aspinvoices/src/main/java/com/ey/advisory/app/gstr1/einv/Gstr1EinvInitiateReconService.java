package com.ey.advisory.app.gstr1.einv;

import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;

public interface Gstr1EinvInitiateReconService {

	public List<Gstr1EinvInitiateReconGstinDetailsDto> getGstins(
			List<GSTNDetailEntity> gstins, String taxPeriod);

	public String initiatRecon(ArrayList<String> gstinlist, String taxPeriod,
			Long entityId);

	public List<Gstr1EinvRequesIdWiseDownloadTabDto> getDownloadData(
			Long configId);

	public String initiatPRvsSubmRecon(List<String> gstinlist,
			String fromTaxPeriod, String toTaxPeriod, Long entityId);

	public List<Gstr1EinvRequesIdWiseDownloadTabDto> getPrVsSubmDownloadData(
			Long configId);



	

}
