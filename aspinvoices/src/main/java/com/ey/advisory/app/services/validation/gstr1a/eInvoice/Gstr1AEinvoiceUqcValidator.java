package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.util.OnboardingQuestionValidationsUtil;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_ANS_KEY_ID;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.ProcessingResultType;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AEinvoiceUqcValidator")
public class Gstr1AEinvoiceUqcValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {
	/**
	 * This class is responsible to validate UQC should be as per the defined
	 * masters attached. In case UQC is other than defined values, then
	 * information message will be diplayed. In case UQCis not as per defined
	 * masters then at the time of JSON creation for Saving GSTR 1, OTH-others
	 * will be reported by default.
	 * 
	 * 
	 * siva krishna
	 * 
	 * BR_OUTWARD_73
	 */

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;
	@Qualifier("OnboardingQuestionValidationsUtil")
	private OnboardingQuestionValidationsUtil util;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		String dataOriginTypeCode = document.getDataOriginTypeCode();
		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			String uom = item.getUom();

			if (uom != null && !uom.isEmpty()) {

				uomCache = StaticContextHolder.getBean("DefaultUomCache",
						UomCache.class);
				if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()
						&& item.getHsnSac().length() > 1
						&& !GSTConstants.SERVICES_CODE.equalsIgnoreCase(
								item.getHsnSac().substring(0, 2))) {

					int m = uomCache.finduom(trimAndConvToUpperCase(uom));
					int n = uomCache.finduomDesc(trimAndConvToUpperCase(uom));
					int o = uomCache
							.finduomMergeDesc(trimAndConvToUpperCase(uom));

					if (m == 1 || n == 1 || o == 1) {
						if (n == 1) {
							item.setUom(uomCache.uQcDescAndCodemap()
									.get(trimAndConvToUpperCase(uom)));
						}
						if (o == 1) {

							item.setUom(uomCache.uQcDesc()
									.get(trimAndConvToUpperCase(uom)));
						}
					} else {

						String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O19
								.name();
						util = StaticContextHolder.getBean(
								"OnboardingQuestionValidationsUtil",
								OnboardingQuestionValidationsUtil.class);
						Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
								.getEntityConfigParamMap();
						String paramtrvalue = util.valid(entityConfigParamMap,
								paramkryId, document.getEntityId());
						if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
								.equalsIgnoreCase(paramtrvalue)) {
							item.setUom(GSTConstants.OTH);

							if (invoicemanagementUqc(dataOriginTypeCode)) {
								if (GSTConstants.NA.equalsIgnoreCase(
										item.getItemUqcUser())) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(GSTConstants.UQC);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION,
											ProcessingResultType.INFO, "IN0507",
											"Invalid Unit Of Measurement",
											location));
								}
							} else {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.UQC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.INFO, "IN0507",
										"Invalid Unit Of Measurement",
										location));
							}
						} else {
							if (invoicemanagementUqc(dataOriginTypeCode)) {
								if (GSTConstants.NA.equalsIgnoreCase(
										item.getItemUqcUser())) {
									Set<String> errorLocations = new HashSet<>();
									errorLocations.add(GSTConstants.UQC);
									TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
											idx, errorLocations.toArray());
									errors.add(new ProcessingResult(
											APP_VALIDATION,
											ProcessingResultType.INFO, "IN0507",
											"Invalid Unit Of Measurement",
											location));
								}
							} else {
								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.UQC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										ProcessingResultType.ERROR, "ER10052",
										"Invalid Unit Of Measurement",
										location));
							}
						}
					}
				}
			} else {

				String paramkryId = CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O19.name();
				util = StaticContextHolder.getBean(
						"OnboardingQuestionValidationsUtil",
						OnboardingQuestionValidationsUtil.class);
				Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap = document
						.getEntityConfigParamMap();
				String paramtrvalue = util.valid(entityConfigParamMap,
						paramkryId, document.getEntityId());
				if (CONFIG_PARAM_OUTWARD_ANS_KEY_ID.B.name()
						.equalsIgnoreCase(paramtrvalue)) {
					item.setUom(GSTConstants.OTH);

				} else {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.UQC);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(
							new ProcessingResult(APP_VALIDATION,
									ProcessingResultType.ERROR, "ER15211",
									"Unit Of Measurement is mandatory as per "
											+ "On-Boarding  parameter.",
									location));
				}

			}
		});
		return errors;
	}

	private boolean invoicemanagementUqc(String origionType) {
		if (Strings.isNullOrEmpty(origionType))
			return false;
		if (origionType.toUpperCase().contains(GSTConstants.IM))
			return true;
		return false;

	}
}
