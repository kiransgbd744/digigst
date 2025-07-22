package com.ey.advisory.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

/**
 * @author vishal.verma
 *
 */
public interface EWB3WayRequesIdWiseDownloadTabService {

	public List<Gstr2RequesIdWiseDownloadTabDto> getDownloadData(Long configId);
}
