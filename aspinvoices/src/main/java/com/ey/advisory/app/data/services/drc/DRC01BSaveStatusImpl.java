package com.ey.advisory.app.data.services.drc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr3b.Gst3BSaveStatusDto;

/**
 * @author vishal.verma
 *
 */

@Component("DRC01BSaveStatusImpl")
public class DRC01BSaveStatusImpl implements DRC01BSaveStatus {
	
	@Autowired
	@Qualifier("DRC01BSaveStatusDaoImpl")
	DRC01BSaveStatusDao saveStatusDao;
	
	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod){
		
		return saveStatusDao.getSaveStatus(gstin, taxPeriod);
		
	}
}
