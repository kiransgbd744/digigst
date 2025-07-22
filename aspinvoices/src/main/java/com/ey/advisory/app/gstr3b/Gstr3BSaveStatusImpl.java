package com.ey.advisory.app.gstr3b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Arun.KA
 *
 */

@Component("Gstr3BSaveStatusImpl")
public class Gstr3BSaveStatusImpl implements Gstr3BSaveStatus {
	
	@Autowired
	@Qualifier("Gstr3BSaveStatusDaoImpl")
	Gstr3BSaveStatusDao gstr3BSaveStatusDao;
	
	public List<Gst3BSaveStatusDto> getSaveStatus(String gstin,
			String taxPeriod){
		
		return gstr3BSaveStatusDao.getSaveStatus(gstin, taxPeriod);
		
	}
}
