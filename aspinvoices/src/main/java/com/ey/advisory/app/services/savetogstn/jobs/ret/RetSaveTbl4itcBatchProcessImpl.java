/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("RetSaveTbl4itcBatchProcessImpl")
public class RetSaveTbl4itcBatchProcessImpl implements RetSaveBatchProcess {

	/* (non-Javadoc)
	 * @see com.ey.advisory.app.services.savetogstn.jobs.ret.RetSaveBatchProcess#execute(com.ey.advisory.app.docs.dto.SaveBatchProcessDto, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SaveToGstnBatchRefIds> execute(SaveBatchProcessDto batchDto,
			String groupCode, String section) {
		// TODO Auto-generated method stub
		return null;
	}

}
