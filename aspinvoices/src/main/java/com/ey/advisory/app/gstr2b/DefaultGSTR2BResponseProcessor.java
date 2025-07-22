package com.ey.advisory.app.gstr2b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("DefaultGSTR2BResponseProcessor")
public class DefaultGSTR2BResponseProcessor implements Gstr2BGetJsonProcessor {

	@Autowired
	@Qualifier("GSTR2BSingleResponseProcessor")
	Gstr2BGetJsonResponseProcessor singleProcessor;

	@Autowired
	@Qualifier("GSTR2BMultiResponseProcessor")
	Gstr2BGetJsonResponseProcessor multiProcessor;

	@Override
	public Gstr2BGetJsonResponseProcessor getProcessorType(List<Long> reqIds) {

		if (reqIds.size() <= 5) {
			return singleProcessor;
		} else {
			return multiProcessor;
		}
	}
}
