package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("anx1BatchMakerImpl")
@Slf4j
public class Anx1BatchMakerImpl implements Anx1BatchMaker {

	@Autowired
	@Qualifier("anx1RateDataToB2cConverter")
	private RateDataToAnx1Converter b2cAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToB2bConverter")
	private RateDataToAnx1Converter b2bAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToExpwpAndExpwopConverter")
	private RateDataToAnx1Converter expAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToSezwpAndSezwopConverter")
	private RateDataToAnx1Converter sezAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToDeemedExportsConverter")
	private RateDataToAnx1Converter deAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToRevConverter")
	private RateDataToAnx1Converter revAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToImpsConverter")
	private RateDataToAnx1Converter impsAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToImpgAndImpgSezConverter")
	private RateDataToAnx1Converter impgAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToMisConverter")
	private RateDataToAnx1Converter misAnx1Converter;

	@Autowired
	@Qualifier("anx1RateDataToEcomConverter")
	private RateDataToAnx1Converter ecomAnx1Converter;

	@Autowired
	@Qualifier("anx1SaveB2bBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveB2bBatchProcess;

	@Autowired
	@Qualifier("anx1SaveExpwpAndExpwopBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveExpBatchProcess;

	@Autowired
	@Qualifier("anx1SaveSezwpAndSezwopBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveSezBatchProcess;

	@Autowired
	@Qualifier("anx1SaveDeemedExportsBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveDeBatchProcess;

	@Autowired
	@Qualifier("anx1SaveImpgAndImpgSezBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveImpgBatchProcess;

	@Autowired
	@Qualifier("anx1SaveMisBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveMisBatchProcess;

	/**
	 * B2c, Rev, Imps, Ecom are vertical sections
	 */
	@Autowired
	@Qualifier("anx1SaveSummaryBatchProcessImpl")
	private Anx1SectionWiseSaveBatchProcess saveSummaryBatchProcess;

	@Override
	public List<SaveToGstnBatchRefIds> saveAnx1Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("saveAnx1Data {} OperationType with groupcode {} and"
					+ " section", operationType, groupCode, section);
		}
		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
			//	for (List<Object[]> doc : docs) {
				//	if (doc != null && !doc.isEmpty()) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"{} invoices found to do {} Anx1SaveToGstn",
									docs.size(), section);
						}
						List<SaveToGstnBatchRefIds> list = new ArrayList<>();

						if (APIConstants.B2C.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = b2cAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.B2B.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = b2bAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveB2bBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.EXPWP.equalsIgnoreCase(section)
								|| APIConstants.EXPWOP.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = expAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveExpBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.SEZWP.equalsIgnoreCase(section)
								|| APIConstants.SEZWOP.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = sezAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSezBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.DE.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = deAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveDeBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.REV.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = revAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.IMPS.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = impsAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.IMPG.equalsIgnoreCase(section)
								|| APIConstants.IMPGSEZ.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = impgAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveImpgBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.MIS.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = misAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveMisBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.ECOM.equalsIgnoreCase(section)) {
							SaveBatchProcessDto batchDto = ecomAnx1Converter
									.convertToAnx1Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						}

						if (!list.isEmpty()) {
							LOGGER.debug("Sent BatchRefIds are {} ", list);
							respList.addAll(list);
						}
					//}
				//}
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
			throw new APIException(msg, ex);
		}
		return respList;

	}

}
