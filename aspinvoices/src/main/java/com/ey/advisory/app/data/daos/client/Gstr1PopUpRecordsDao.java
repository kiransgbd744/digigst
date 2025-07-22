package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsFinalResponseDto;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsRequestDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1PopUpRecordsDao {
	Gstr1PopScreenRecordsFinalResponseDto fetchProcessStatusData(
			Gstr1PopScreenRecordsRequestDto reqDto, List<String> gstins);

}
