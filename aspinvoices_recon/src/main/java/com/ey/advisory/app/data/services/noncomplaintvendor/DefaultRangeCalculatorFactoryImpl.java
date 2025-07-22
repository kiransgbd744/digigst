package com.ey.advisory.app.data.services.noncomplaintvendor;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("DefaultRangeCalculatorFactoryImpl")
public class DefaultRangeCalculatorFactoryImpl
		implements ConfirmantRangeCalculatorFactory {

	@Override
	public ConfirmantRangeCalculator getCalculator(String returnType) {

		ConfirmantRangeCalculator calculator = null;

		try {
			switch (returnType) {
			case NCVReturnTypeConstants.GSTR1:
			case NCVReturnTypeConstants.GSTR1A:
			case NCVReturnTypeConstants.GSTR5:
			case NCVReturnTypeConstants.GSTR6:
			case NCVReturnTypeConstants.GSTR7:
			case NCVReturnTypeConstants.GSTR8:
			case NCVReturnTypeConstants.GSTR3B:
				calculator = (ConfirmantRangeCalculator) StaticContextHolder
						.getBean("MonthlyConfirmantRangeCalculator",
								ConfirmantRangeCalculator.class);
				break;
			case NCVReturnTypeConstants.ITC04:
			case NCVReturnTypeConstants.CMP08:
				calculator = (ConfirmantRangeCalculator) StaticContextHolder
						.getBean("QuaterlyConfirmantRangeCalculator",
								ConfirmantRangeCalculator.class);
				break;
			case NCVReturnTypeConstants.GSTR4:
			case NCVReturnTypeConstants.GSTR9:
			case NCVReturnTypeConstants.GSTR9A:
				calculator = (ConfirmantRangeCalculator) StaticContextHolder
						.getBean("AnnuallyConfirmantRangeCalculator",
								ConfirmantRangeCalculator.class);
				break;
			default:
				String errMsg = String.format(
						"No Calculator available for the return type: '%s' ",
						returnType);
				LOGGER.info(errMsg);
				throw new AppException(errMsg);

			}

		} catch (Exception ex) {
			String errMsg = "Error while determining the calculator";
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex);
		}
		return calculator;
	}

}
