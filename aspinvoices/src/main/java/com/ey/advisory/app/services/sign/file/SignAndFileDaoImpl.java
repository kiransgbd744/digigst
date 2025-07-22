/**
 * 
 */
package com.ey.advisory.app.services.sign.file;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.multitenancy.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("SignAndFileDaoImpl")
public class SignAndFileDaoImpl implements SignAndFileDao {

	
	@Autowired
	private DocRepository docRepo; 
	
	@Override
	public void updateGstr1Tables(String taxperiod, String gstin, String groupCode) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"update isFiled flags as True for the input combination {} {} ",
					taxperiod, gstin);
		}
		//needs to update isFiled and FiledDate Flags in all the tables for vertical 
		//processed tables and proc inserted tables as well.
		
		TenantContext.setTenantId(groupCode);
		LocalDate modifiedDate = EYDateUtil.toISTDateTimeFromUTC(LocalDate.now());
		LocalDateTime now = EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now());
		docRepo.markFilededAsTrue(taxperiod, gstin, modifiedDate, now);

	}

}
