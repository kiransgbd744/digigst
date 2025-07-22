package com.ey.advisory.app.data.services.drc01c;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.gstr3b.Gst3BSaveStatusDto;

/**
 * @author vishal.verma
 *
 */

@Component("DRC01CSaveStatusImpl")
public class DRC01CSaveStatusImpl implements DRC01CSaveStatus {
	
	@Autowired
	@Qualifier("DRC01CSaveStatusDaoImpl")
	DRC01CSaveStatusDao saveStatusDao;
	
	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod){
		
		return saveStatusDao.getSaveStatus(gstin, taxPeriod);
		
	}
}
