package com.ey.advisory.app.util;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.app.util.AspDocumentConstants.FormReturnTypes;
import com.ey.advisory.app.util.AspDocumentConstants.TransDocTypes;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_GENERAL_QUE_KEY_ID;


/**
 * 
 * @author Mohana.Dasari
 *
 */
public class AspDocumentUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AspDocumentUtil.class);

	private static DateTimeFormatter MONTH_YEAR = DateTimeFormatter
			.ofPattern(AspDocumentConstants.MONTH_YEAR_DATE_FORMATTER);

	/**
	 * 
	 * @param entityId
	 * @param reqReturnPeriod
	 * @param gstinAndEntityMap
	 * @param entityAndReturnPeriodMap
	 * @return
	 */
	public static String getFormReturnType(Long entityId,
			String reqReturnPeriod, Map<String, String> questionAnsMap,
			String transDocType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"getFormReturnType method " + " Entity Id " + entityId);
		}
		String formReturnType = null;
		String entityTaxPeriod = questionAnsMap
				.get(CONFIG_PARAM_GENERAL_QUE_KEY_ID.G9.name());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getFormReturnType method entityTaxPeriod "
					+ entityTaxPeriod + " reqReturnPeriod " + reqReturnPeriod);
		}
		YearMonth parsedEntityTaxPeriod = null;// Cut Off Tax Period

		if (entityTaxPeriod != null && !entityTaxPeriod.trim().isEmpty()) {
			parsedEntityTaxPeriod = YearMonth.parse(entityTaxPeriod,
					MONTH_YEAR);
		}
		YearMonth parsedDocTaxPeriod = YearMonth.parse(reqReturnPeriod,
				MONTH_YEAR);
		if (parsedEntityTaxPeriod != null
				&& parsedDocTaxPeriod.isBefore(parsedEntityTaxPeriod)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getFormReturnType method Form Return Type is GSTR1 or GSTR2");
			}
			if (TransDocTypes.OUTWARD.getType().equalsIgnoreCase(transDocType))
				formReturnType = FormReturnTypes.GSTR1.getType();
			else
				formReturnType = FormReturnTypes.GSTR2.getType();
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"getFormReturnType method Form Return Type is ANX1 or ANX2");
			}
			if (TransDocTypes.OUTWARD.getType().equalsIgnoreCase(transDocType))
				formReturnType = FormReturnTypes.GSTR1.getType();
			else
				formReturnType = FormReturnTypes.GSTR2.getType();
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"getFormReturnType Form Return Type " + formReturnType);
		}
		return formReturnType;
	}

}
