package com.ey.advisory.service.days.revarsal180;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface ITCReversal180IdWiseDownloadTabService {

	public List<ITCReversal180DownloadTabDto> getDownloadData(Long computeId);
}
