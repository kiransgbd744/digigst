package com.ey.advisory.app.reconewbvsitc04;

import java.util.List;

import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

/**
 * @author Ravindra V S
 *
 */
public interface EwbVsItc04RequesIdWiseDownloadTabService {

	public List<Gstr2RequesIdWiseDownloadTabDto> getDownloadData(Long configId);
}
