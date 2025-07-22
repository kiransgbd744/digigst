package com.ey.advisory.app.dashboard.mergefiles;

import org.springframework.stereotype.Component;

@Component("DefaultAPICallDashboardFileNameCreator")
public class DefaultAPICallDashboardFileNameCreator
		implements APICallDashboardFileNameCreator {

	@Override
	public String createFileName(String returnType, String gstin,
			String taxPeriod, String existingFileName) {

		if ("ITC04".equalsIgnoreCase(returnType)) {
			String month = taxPeriod.substring(0, 2);
			switch (month) {
			case "13":
				return String.format("%s_%s_%s%s%s", returnType, gstin, "Q1",
						taxPeriod.substring(2), ".zip");
			case "14":
				return String.format("%s_%s_%s%s%s", returnType, gstin, "Q2",
						taxPeriod.substring(2), ".zip");
			case "15":
				return String.format("%s_%s_%s%s%s", returnType, gstin, "Q3",
						taxPeriod.substring(2), ".zip");
			case "16":
				return String.format("%s_%s_%s%s%s", returnType, gstin, "Q4",
						taxPeriod.substring(2), ".zip");
			default:
				return existingFileName;
			}
		} else {
			return existingFileName;
		}
	}

}
