/**
 * 
 */
package com.ey.advisory.einv.app.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.gstnapi.domain.master.NICError;
import com.ey.advisory.gstnapi.repositories.master.NICErrorRepository;

/**
 * @author Sachindra.S
 *
 */
@Service("EINVAPIErrorDetailServiceImpl")
public class APIErrorDetailServiceImpl implements APIErrorDetailService {

	@Autowired
	private NICErrorRepository nicErrorRepository;

	@Override
	public NICError getError(String category, Integer code) {
		return nicErrorRepository.findByErrCategoryAndErrCode(category, code);
	}

	@Override
	public NICError saveNicError(String category, Integer code, String desc) {
		NICError nicError = new NICError();
		nicError.setErrCategory(category);
		nicError.setErrCode(code);
		nicError.setErrDesc(desc);
		return nicErrorRepository.saveAndFlush(nicError);
	}

}
