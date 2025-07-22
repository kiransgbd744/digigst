/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("RetBatchMakerImpl")
@Slf4j
public class RetBatchMakerImpl implements RetBatchMaker {

	@Autowired
	@Qualifier("RateDataToTbl3aConverter")
	private RateDataToRetConverter tbl3aRetConverter;

	@Autowired
	@Qualifier("RateDataToTbl3cConverter")
	private RateDataToRetConverter tbl3cRetConverter;

	@Autowired
	@Qualifier("RateDataToTbl3dConverter")
	private RateDataToRetConverter tbl3dRetConverter;

	@Autowired
	@Qualifier("RateDataToTbl4aConverter")
	private RateDataToRetConverter tbl4aRetConverter;

	@Autowired
	@Qualifier("RateDataToTbl4bConverter")
	private RateDataToRetConverter tbl4bRetConverter;

	@Autowired
	@Qualifier("RateDataToTbl4itcConverter")
	private RateDataToRetConverter tbl4itcRetConverter;

	@Autowired
	@Qualifier("RateDataToTbl6Converter")
	private RateDataToRetConverter tbl6RetConverter;

	

	@Autowired
	@Qualifier("RetSaveTbl3aBatchProcessImpl")
	private RetSaveBatchProcess tbl3aBatchProcess;

	@Autowired
	@Qualifier("RetSaveTbl3cBatchProcessImpl")
	private RetSaveBatchProcess tbl3cBatchProcess;

	@Autowired
	@Qualifier("RetSaveTbl3dBatchProcessImpl")
	private RetSaveBatchProcess tbl3dBatchProcess;

	@Autowired
	@Qualifier("RetSaveTbl4aBatchProcessImpl")
	private RetSaveBatchProcess tbl4aBatchProcess;

	@Autowired
	@Qualifier("RetSaveTbl4bBatchProcessImpl")
	private RetSaveBatchProcess tbl4bBatchProcess;

	@Autowired
	@Qualifier("RetSaveTbl4itcBatchProcessImpl")
	private RetSaveBatchProcess tbl4itcBatchProcess;

	@Autowired
	@Qualifier("RetSaveTbl6BatchProcessImpl")
	private RetSaveBatchProcess tbl6BatchProcess;

	@Override
	public List<SaveToGstnBatchRefIds> saveRetData(String groupCode, String section,
			List<Object[]> docs, SaveToGstnOprtnType operationType) {
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
				/*for (List<Object[]> doc : docs) {
					if (doc != null && !doc.isEmpty()) {*/
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"{} invoices found to do {} Anx1SaveToGstn",
									docs.size(), section);
						}
						List<SaveToGstnBatchRefIds> list = new ArrayList<>();
/*
						if (APIConstants.B2C.equals(section)) {
							SaveBatchProcessDto batchDto = tbl3aRetConverter
									.convertToRetObject(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else*/ if (APIConstants.B2B.equals(section)) {
							SaveBatchProcessDto batchDto = tbl3cRetConverter
									.convertToRetObject(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = tbl3aBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.EXPWP.equals(section)
								|| APIConstants.EXPWOP.equals(section)) {
							SaveBatchProcessDto batchDto = tbl3dRetConverter
									.convertToRetObject(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = tbl3cBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.SEZWP.equals(section)
								|| APIConstants.SEZWOP.equals(section)) {
							SaveBatchProcessDto batchDto = tbl4aRetConverter
									.convertToRetObject(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = tbl3dBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.DE.equals(section)) {
							SaveBatchProcessDto batchDto = tbl4bRetConverter
									.convertToRetObject(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = tbl4aBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} /*else if (APIConstants.REV.equals(section)) {
							SaveBatchProcessDto batchDto = tbl4itcRetConverter
									.convertToRetObject(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.IMPS.equals(section)) {
							SaveBatchProcessDto batchDto = tbl6RetConverter
									.convertToRetObject(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.IMPG.equals(section)
								|| APIConstants.IMPGSEZ.equals(section)) {
							SaveBatchProcessDto batchDto = impgAnx1Converter
									.convertToRetObject(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = tbl4bBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.MIS.equals(section)) {
							SaveBatchProcessDto batchDto = misAnx1Converter
									.convertToRetObject(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveMisBatchProcess.execute(batchDto,
									groupCode, section);
							}
						} else if (APIConstants.ECOM.equals(section)) {
							SaveBatchProcessDto batchDto = ecomAnx1Converter
									.convertToRetObject(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
							list = saveSummaryBatchProcess.execute(batchDto,
									groupCode, section);
							}
						}*/

						if (!list.isEmpty()) {
							LOGGER.debug("Sent BatchRefIds are {} ", list);
							respList.addAll(list);
						}
					/*}
				}*/
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
		}
		return respList;

	}

}
