package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2RequesIdWiseDownloadTabService {

	public List<Gstr2RequesIdWiseDownloadTabDto> getDownloadData(Long configId, 
			String reconType);
}
