/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("RateDataToTbl3dConverter")
public class RateDataToTbl3dConverter implements RateDataToRetConverter {

	/* (non-Javadoc)
	 * @see com.ey.advisory.app.services.savetogstn.jobs.ret.RateDataToRetConverter#convertToAnx1Object(java.util.List, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SaveBatchProcessDto convertToRetObject(List<Object[]> objects,
			String section, String groupCode, String taxDocType) {
		// TODO Auto-generated method stub
		return null;
	}

}
