package com.ey.advisory.app.data.services.noncomplaintvendor;

public interface ConfirmantRangeCalculator {

	ConfirmantRangeDto calculate(String returnType, String filingType, String fy);
}
