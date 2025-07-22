package com.ey.advisory.app.gstr2jsonupload;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdInvoicesHeaderEntity;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2JsonParser {
	
	public List<GetGstr2aB2bInvoicesHeaderEntity> parseB2bData(
			Header dto, String apiResp);

	public List<GetGstr2aB2baInvoicesHeaderEntity> parseB2baData(
			Header dto, String apiResp);
	
	public List<GetGstr2aCdnInvoicesHeaderEntity> parseCdnData(
			Header dto, String apiResp);
	
	public List<GetGstr2aCdnaInvoicesHeaderEntity> parseCdnAData(
			Header dto, String apiResp);
	
	public List<GetGstr2aIsdInvoicesHeaderEntity> parseIsdData(
			Header dto, String apiResp);


}
