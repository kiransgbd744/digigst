package com.ey.advisory.app.services.itc04;

import org.springframework.stereotype.Component;

/**
 * @author Saif.S
 *
 */
@Component("Itc04TaxPeriodUtil")
public class Itc04TaxPeriodUtil {

	public String getQReturnPeriod(String returnPeriod, String finYear) {
		int fy = Integer.parseInt(finYear);
		if (fy <= 2021) {
			if ("APR-JUN".equalsIgnoreCase(returnPeriod))
				return "13";
			else if ("JUL-SEP".equalsIgnoreCase(returnPeriod))
				return "14";
			else {
				if (fy == 2021) {
					if ("OCT-MAR".equalsIgnoreCase(returnPeriod))
						return "18";
				} else {
					if ("OCT-DEC".equalsIgnoreCase(returnPeriod))
						return "15";
					else if ("JAN-MAR".equalsIgnoreCase(returnPeriod))
						return "16";
				}
			}
		} else {
			if ("APR-SEP".equalsIgnoreCase(returnPeriod))
				return "17";
			else if ("OCT-MAR".equalsIgnoreCase(returnPeriod))
				return "18";
		}
		return null;
	}

	public String getValidReturnPeriod(String returnPeriod, String finYear) {
		int fy = Integer.parseInt(finYear);
		if (fy < 2021) {
			return returnPeriod;
		} else if (fy == 2021) {
			if ("Apr-Jun".equalsIgnoreCase(returnPeriod))
				return returnPeriod;
			else if ("Jul-Sep".equalsIgnoreCase(returnPeriod))
				return returnPeriod;
			else if ("Oct-Mar".equalsIgnoreCase(returnPeriod)
					|| "Oct-Dec".equalsIgnoreCase(returnPeriod)
					|| "Jan-Mar".equalsIgnoreCase(returnPeriod))
				return "Oct-Mar";
		} else {
			if ("Apr-Jun".equalsIgnoreCase(returnPeriod)
					|| "Jul-Sep".equalsIgnoreCase(returnPeriod)
					|| "Apr-Sep".equalsIgnoreCase(returnPeriod))
				return "Apr-Sep";
			else if ("Oct-Mar".equalsIgnoreCase(returnPeriod)
					|| "Oct-Dec".equalsIgnoreCase(returnPeriod)
					|| "Jan-Mar".equalsIgnoreCase(returnPeriod))
				return "Oct-Mar";
		}
		return returnPeriod;
	}

}
