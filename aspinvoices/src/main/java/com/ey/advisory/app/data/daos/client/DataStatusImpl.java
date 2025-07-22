package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.app.docs.dto.DataStatusSearchDto;
/**
 * 
 * @author Balakrishna.S
 *
 */
public interface DataStatusImpl {
	
	public List<DataStatusSearchDto> fecth(String SectionType,String gstins,
			LocalDate dataRecvFrom, LocalDate dataRecvTo);
}
