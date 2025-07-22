package com.ey.advisory.app.ims.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("DefaultGetIrnResponseProcessor")
public class DefaultGetIrnResponseProcessor implements GetIrnJsonProcessor {

	@Autowired
	@Qualifier("GetIrnSingleResponseProcessor")
	GetIrnJsonResponseProcessor singleProcessor;

	@Autowired
	@Qualifier("GetIrnMultiResponseProcessor")
	GetIrnJsonResponseProcessor multiProcessor;

	@Override
	public GetIrnJsonResponseProcessor getProcessorType(List<Long> reqIds) {

		if (reqIds.size() <= 5) {
			return singleProcessor;
		} else {
			return multiProcessor;
		}
	}
}

