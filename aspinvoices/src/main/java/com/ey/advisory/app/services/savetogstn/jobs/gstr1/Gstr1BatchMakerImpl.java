package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("gstr1BatchMakerImpl")
public class Gstr1BatchMakerImpl implements Gstr1BatchMaker {

	@Autowired
	@Qualifier("rateDataToB2bB2baConverter")
	private RateDataToGstr1Converter b2bGstr1Converter;

	@Autowired
	@Qualifier("saveB2bB2baBatchProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveB2bBatchProcess;

	@Autowired
	@Qualifier("rateDataToB2clB2claConverter")
	private RateDataToGstr1Converter b2clGstr1Converter;

	@Autowired
	@Qualifier("saveB2clB2claBatchProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveB2clBatchProcess;

	@Autowired
	@Qualifier("rateDataToExpExpaConverter")
	private RateDataToGstr1Converter expGstr1Converter;

	@Autowired
	@Qualifier("saveExpExpaBatchProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveExpBatchProcess;

	@Autowired
	@Qualifier("rateDataToCdnrCdnraConverter")
	private RateDataToGstr1Converter cdnrGstr1Converter;

	@Autowired
	@Qualifier("saveCdnrCdnraBatchProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveCdnrBatchProcess;

	@Autowired
	@Qualifier("rateDataToCdnurCdnuraConverter")
	private RateDataToGstr1Converter cdnurGstr1Converter;

	@Autowired
	@Qualifier("saveCdnurCdnuraProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveCdnurBatchProcess;

	@Autowired
	@Qualifier("rateDataToAtAtaConverter")
	private RateDataToGstr1Converter atGstr1Converter;

	@Autowired
	@Qualifier("RateDataToSupEcomConverter")
	private RateDataToGstr1Converter supEcomGstr1Converter;

	@Autowired
	@Qualifier("rateDataToB2csB2csaConverter")
	private RateDataToGstr1Converter b2csGstr1Converter;

	@Autowired
	@Qualifier("rateDataToTxpTxpaConverter")
	private RateDataToGstr1Converter txpGstr1Converter;

	@Autowired
	@Qualifier("rateDataToNilConverter")
	private RateDataToGstr1Converter nilGstr1Converter;

	@Autowired
	@Qualifier("rateDataToHsnSumConverter")
	private RateDataToGstr1Converter hsnGstr1Converter;

	@Autowired
	@Qualifier("rateDataToDocIssuedConverter")
	private RateDataToGstr1Converter docIssGstr1Converter;

	@Autowired
	@Qualifier("saveSummaryBatchProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveSummaryBatchProcess;

	@Autowired
	@Qualifier("RateDataToB2bConverter")
	private RateDataToGstr1Converter b2bDeleteGstr1Converter;

	@Autowired
	@Qualifier("RateDataToExpConverter")
	private RateDataToGstr1Converter expDeleteGstr1Converter;

	@Autowired
	@Qualifier("RateDataToCdnrConverter")
	private RateDataToGstr1Converter cdnrDeleteGstr1Converter;

	@Autowired
	@Qualifier("RateDataToCdnurConverter")
	private RateDataToGstr1Converter cdnurDeleteGstr1Converter;

	@Autowired
	@Qualifier("RateDataToEcomSupConverter")
	private RateDataToGstr1Converter ecomSupGstr1Converter;

	@Autowired
	@Qualifier("RateDataToEcomSupSummConverter")
	private RateDataToGstr1Converter ecomSupSummGstr1Converter;

	@Autowired
	@Qualifier("Gstr1SaveEcomBatchProcessImpl")
	private Gstr1SectionWiseSaveBatchProcess saveEcomBatchProcess;
	
	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	@Override
	public List<SaveToGstnBatchRefIds> saveGstr1Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Map<Long, Long> orgCanIdsMap,
			Long retryCount, Long userRequestId, String origin, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAV_GST1_DATA_START,
				PerfamanceEventConstants.Gstr1BatchMakerImpl,
				PerfamanceEventConstants.saveGstr1Data,
				PerfamanceEventConstants.Section_Docs_Size.concat(":{")
						.concat(section).concat(String.valueOf(docs.size()))
						.concat("}"));

		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		if (Strings.isNullOrEmpty(origin)) {
			origin = GenUtil.getReturnType(context);
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			int chunkSizeFetcherSize = chunkSizeFetcher.getSize();

			if (docs != null && !docs.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("{} Docs found to do {} Gstr1 SaveToGstn",
							docs.size(), section);
				}
				List<SaveToGstnBatchRefIds> list = new ArrayList<>();
				if (APIConstants.B2B.equalsIgnoreCase(section)
						|| APIConstants.B2BA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = b2bGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveB2bBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				} else if (APIConstants.B2CL.equalsIgnoreCase(section)
						|| APIConstants.B2CLA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = b2clGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveB2clBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				} else if (APIConstants.EXP.equalsIgnoreCase(section)
						|| APIConstants.EXPA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = expGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveExpBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				} else if (APIConstants.CDNR.equalsIgnoreCase(section)
						|| APIConstants.CDNRA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = cdnrGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveCdnrBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				} else if (APIConstants.CDNUR.equalsIgnoreCase(section)
						|| APIConstants.CDNURA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = cdnurGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveCdnurBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.B2CS.equalsIgnoreCase(section)
						|| APIConstants.B2CSA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = b2csGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.AT.equalsIgnoreCase(section)
						|| APIConstants.ATA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = atGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.TXP.equalsIgnoreCase(section)
						|| APIConstants.TXPA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = txpGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.NIL.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = nilGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.HSNSUM.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = hsnGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.DOCISS.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = docIssGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				} else if (APIConstants.SUPECOM.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = supEcomGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);;
					}
				} else if (APIConstants.ECOMSUPSUM.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = ecomSupSummGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveSummaryBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				}

				else if (APIConstants.ECOMSUP.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = ecomSupGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						batchDto.setOrigin(origin);
						list = saveEcomBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				}

				if (!list.isEmpty()) {
					respList.addAll(list);
				}

			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
			throw new AppException(msg, ex);
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAV_GST1_DATA_END,
				PerfamanceEventConstants.Gstr1BatchMakerImpl,
				PerfamanceEventConstants.saveGstr1Data,
				PerfamanceEventConstants.Section_Docs_Size.concat(":{")
						.concat(section).concat(String.valueOf(docs.size()))
						.concat("}"));
		return respList;
	}

	@Override
	public List<SaveToGstnBatchRefIds> deleteGstr1Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Map<Long, Long> orgCanIdsMap,
			Long retryCount, Long userRequestId, ProcessingContext context) {

		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
			// DELETE Action is changed to DELETE (FILE UPLOAD)
			if (APIConstants.DELETE.equalsIgnoreCase(taxDocType)) {
				taxDocType = APIConstants.DELETE_FILE_UPLOAD;
			}
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			int chunkSizeFetcherSize = chunkSizeFetcher.getSize();
			if (docs != null && !docs.isEmpty()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"{} Docs found to do {} Gstr1 Delete SaveToGstn",
							docs.size(), section);
				}
				List<SaveToGstnBatchRefIds> list = new ArrayList<>();
				if (APIConstants.B2B.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = b2bDeleteGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						list = saveB2bBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}

				} else if (APIConstants.EXP.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = expDeleteGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						list = saveExpBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				} else if (APIConstants.CDNR.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = cdnrDeleteGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						list = saveCdnrBatchProcess.execute(batchDto, groupCode,
								section, taxDocType, orgCanIdsMap, retryCount,
								userRequestId, context);
					}
				} else if (APIConstants.CDNUR.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = cdnurDeleteGstr1Converter
							.convertToGstr1Object(docs, section, groupCode,
									taxDocType, chunkSizeFetcherSize);
					if (batchDto != null) {
						list = saveCdnurBatchProcess.execute(batchDto,
								groupCode, section, taxDocType, orgCanIdsMap,
								retryCount, userRequestId, context);
					}
				}

				if (!list.isEmpty()) {
					respList.addAll(list);
				}
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
			throw new AppException(msg, ex);
		}

		return respList;
	}
}
