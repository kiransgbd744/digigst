package com.ey.advisory.app.data.services.anx1;

/**
 * @author V.Mule
 *
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.daos.client.DataStatusFileSummaryFetchDao;
import com.ey.advisory.app.docs.dto.DataStatusFileSummaryRespDto;
import com.ey.advisory.app.docs.dto.anx1.DataStatusFilesummaryReqDto;
import com.ey.advisory.app.util.OnboardingConfigParamsCheck;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

@Service("DataStatusFileSummaryFetchService")
public class DataStatusFileSummaryFetchService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusFileSummaryFetchService.class);
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	private static final String OUTWARD = "outward";
	private static final String INWARD = "inward";
	private static final String RET = "RET";

	// Outward file types
	private static final String OUTWARD_RAW269 = "comprehensive_raw";
	private static final String OUTWARD_RAW109 = "raw";
	private static final String INWARD_RAW = "raw";
	private static final String B2C = "b2c";
	private static final String TABLE4 = "table4";
	private static final String TABLE3H3I = "table3h3i";

	private static final String RET1AND1A = "ret1and1a";
	private static final String INTEREST = "interest";
	private static final String SETOFFANDUTIL = "setoffandutil";
	private static final String REFUNDS = "refunds";

	@Autowired
	@Qualifier("DataStatusFileSummaryFetchDaoImpl")
	private DataStatusFileSummaryFetchDao dataStatusFileSummaryFetchDao;

	@Autowired
	@Qualifier("OnboardingConfigParamsCheck")
	private OnboardingConfigParamsCheck onboardingConfigParamsCheck;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	public List<DataStatusFileSummaryRespDto> find(
			DataStatusFilesummaryReqDto summaryRequest) {
		List<DataStatusFileSummaryRespDto> finalDataResps = new ArrayList<>();

		List<DataStatusFileSummaryRespDto> respDtos = fetchDataByCriteria(
				summaryRequest);
		Map<String, List<DataStatusFileSummaryRespDto>> returnSectionMap = createMapByFileNameGstinReturnPeriodAndSection(
				respDtos);
		calculateDataByPeriodAndDocType(returnSectionMap, finalDataResps);
		return finalDataResps;
	}

	public List<DataStatusFileSummaryRespDto> fetchDataByCriteria(
			DataStatusFilesummaryReqDto summaryRequest) {

		String dataType = summaryRequest.getDataType().toLowerCase();
		String fileType = summaryRequest.getFileType().toLowerCase();
		List<Object> answers = entityConfigPrmtRepository.findByQtnCode();
		if (answers != null && !answers.isEmpty()) {
			Integer answer = Integer.parseInt(String.valueOf(answers.get(0)));
			summaryRequest.setAnswer(answer);
		}
		List<Object[]> dataArray = new ArrayList<>();
		if (dataType.equalsIgnoreCase(OUTWARD)) {
			switch (fileType) {
			case OUTWARD_RAW269:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthOutwardRawFileData(summaryRequest));
				break;
			case OUTWARD_RAW109:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthOutwardRaw109FileData(summaryRequest));
				break;
			case B2C:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fetchOutwardB2cData(summaryRequest));
				break;
			case TABLE4:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fetchOutwardTable4Data(summaryRequest));
				break;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"The selected RawFile data for datastatus file summary is :",
						dataArray);
			}

		} else if (dataType.equalsIgnoreCase(INWARD)) {
			switch (fileType) {
			case INWARD_RAW:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthInwardRawFileData(summaryRequest));
				break;
			case TABLE3H3I:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthInwardTable3h3iFileData(summaryRequest));
				break;

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"The selected RawFile data for datastatus file summary is:"
								, dataArray);
			}
		} else if (dataType.equalsIgnoreCase(RET)) {
			switch (fileType) {
			case RET1AND1A:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthOthersRet1And1aFileData(summaryRequest));
				break;
			case INTEREST:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthOthersInterestFileData(summaryRequest));
				break;
			case SETOFFANDUTIL:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthOthersSetOffAndUtilFileData(summaryRequest));
				break;
			case REFUNDS:
				dataArray.addAll(dataStatusFileSummaryFetchDao
						.fecthOthersRefundsFileData(summaryRequest));
				break;
			}
		}
		return convertDataArrayToListItems(dataArray);
	}

	public void calculateDataByPeriodAndDocType(
			Map<String, List<DataStatusFileSummaryRespDto>> returnSectionMap,
			List<DataStatusFileSummaryRespDto> finalDataResps) {

		returnSectionMap.keySet().forEach(key -> {
			List<DataStatusFileSummaryRespDto> dataList = returnSectionMap
					.get(key);
			if (!dataList.isEmpty()) {
				String date = "";
				String fileName = "";
				String gstins = "";
				String retperiod = "";
				String retType = "";
				String docType = "";
				String returnSection = "";
				String authToken = "";
				BigInteger count = BigInteger.ZERO;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;
				BigDecimal totalTaxes = BigDecimal.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;

				DataStatusFileSummaryRespDto respDto = new DataStatusFileSummaryRespDto();
				for (DataStatusFileSummaryRespDto dto : dataList) {
					date = dto.getDate();
					gstins = dto.getGstin();
					authToken = dto.getAuthToken();
					docType = dto.getDocType();
					fileName = dto.getFileName();
					returnSection = dto.getReturnSection();
					retperiod = dto.getReturnPeriod();
					retType = dto.getReturnType();

					count = count.add((dto.getCount() != null
							&& dto.getCount().intValue() > 0) ? dto.getCount()
									: BigInteger.ZERO);
					if (docType != null && (docType.equals("RCR")
							|| docType.equals("CR") || docType.equals("RFV")
							|| docType.equals("RRFV"))) {
						totalTaxes = totalTaxes
								.subtract((dto.getTotalTaxes() != null
										&& dto.getTotalTaxes().intValue() > 0)
												? dto.getTotalTaxes()
												: BigDecimal.ZERO);
						taxableValue = taxableValue
								.subtract((dto.getTaxableValue() != null
										&& dto.getTaxableValue().intValue() > 0)
												? dto.getTaxableValue()
												: BigDecimal.ZERO);
						cess = cess.subtract((dto.getCess() != null
								&& dto.getCess().intValue() > 0) ? dto.getCess()
										: BigDecimal.ZERO);
						igst = igst.subtract((dto.getIgst() != null
								&& dto.getIgst().intValue() > 0) ? dto.getIgst()
										: BigDecimal.ZERO);
						cgst = cgst.subtract((dto.getCgst() != null
								&& dto.getCgst().intValue() > 0) ? dto.getCgst()
										: BigDecimal.ZERO);
						sgst = sgst.subtract((dto.getSgst() != null
								&& dto.getSgst().intValue() > 0) ? dto.getSgst()
										: BigDecimal.ZERO);
					} else {
						totalTaxes = totalTaxes.add((dto.getTotalTaxes() != null
								&& dto.getTotalTaxes().intValue() > 0)
										? dto.getTotalTaxes()
										: BigDecimal.ZERO);
						taxableValue = taxableValue
								.add((dto.getTaxableValue() != null
										&& dto.getTaxableValue().intValue() > 0)
												? dto.getTaxableValue()
												: BigDecimal.ZERO);
						cess = cess.add((dto.getCess() != null
								&& dto.getCess().intValue() > 0) ? dto.getCess()
										: BigDecimal.ZERO);
						igst = igst.add((dto.getIgst() != null
								&& dto.getIgst().intValue() > 0) ? dto.getIgst()
										: BigDecimal.ZERO);
						cgst = cgst.add((dto.getCgst() != null
								&& dto.getCgst().intValue() > 0) ? dto.getCgst()
										: BigDecimal.ZERO);
						sgst = sgst.add((dto.getSgst() != null
								&& dto.getSgst().intValue() > 0) ? dto.getSgst()
										: BigDecimal.ZERO);
					}
				}
				respDto.setDate(date);
				respDto.setFileName(fileName);
				respDto.setGstin(gstins);
				respDto.setReturnPeriod(retperiod);
				respDto.setReturnType(retType);
				respDto.setReturnSection(returnSection);
				respDto.setCount(count);
				respDto.setTaxableValue(taxableValue);
				respDto.setTotalTaxes(totalTaxes);
				respDto.setIgst(igst);
				respDto.setCgst(cgst);
				respDto.setSgst(sgst);
				respDto.setCess(cess);
				respDto.setAuthToken(authToken);
				finalDataResps.add(respDto);
			}
		});
	}

	public Map<String, List<DataStatusFileSummaryRespDto>> createMapByFileNameGstinReturnPeriodAndSection(
			List<DataStatusFileSummaryRespDto> respDtos) {
		Map<String, List<DataStatusFileSummaryRespDto>> returnSectionMap = new LinkedHashMap<>();

		respDtos.forEach(dto -> {
			StringBuilder key = new StringBuilder();
			key.append(dto.getFileName()).append("_").append(dto.getGstin())
					.append("_").append(dto.getReturnPeriod()).append("_")
					.append(dto.getReturnSection());
			String dataKey = key.toString();
			if (returnSectionMap.containsKey(dataKey)) {
				List<DataStatusFileSummaryRespDto> dtos = returnSectionMap
						.get(dataKey);
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			} else {
				List<DataStatusFileSummaryRespDto> dtos = new LinkedList<>();
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			}
		});
		return returnSectionMap;
	}

	private List<DataStatusFileSummaryRespDto> convertDataArrayToListItems(
			List<Object[]> dataArray) {
		List<DataStatusFileSummaryRespDto> apiSummaryResDtos = new ArrayList<>();
		for (Object status[] : dataArray) {
			try {
				DataStatusFileSummaryRespDto apiSummaryResDto = new DataStatusFileSummaryRespDto();

				String date = String.valueOf(String.valueOf(status[0]));
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd");
				if (date != null && !date.isEmpty()) {
					apiSummaryResDto.setDate(dateFormat.format(status[0]));
				}
				apiSummaryResDto.setFileName(String.valueOf(status[1]));
				String gstin = String.valueOf(status[2]);
				apiSummaryResDto.setGstin(gstin);
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						apiSummaryResDto.setAuthToken("Active");
					} else {
						apiSummaryResDto.setAuthToken("Inactive");
					}
				} else {
					apiSummaryResDto.setAuthToken("Inactive");
				}
				apiSummaryResDto.setReturnPeriod(String.valueOf(status[3]));
				if (status[4] == null) {
					apiSummaryResDto.setReturnType("GSTR1");
				} else {
					apiSummaryResDto.setReturnType(String.valueOf(status[4]));
				}
				if (status[5] == null) {
					apiSummaryResDto.setReturnSection("Cancellation");
				} else {
					apiSummaryResDto
							.setReturnSection(String.valueOf(status[5]));
				}
				apiSummaryResDto.setCount(GenUtil.getBigInteger(status[6]));
				apiSummaryResDto.setTaxableValue((BigDecimal) status[7]);
				apiSummaryResDto.setTotalTaxes((BigDecimal) status[8]);
				apiSummaryResDto.setIgst((BigDecimal) status[9]);
				apiSummaryResDto.setCgst((BigDecimal) status[10]);
				apiSummaryResDto.setSgst((BigDecimal) status[11]);
				apiSummaryResDto.setCess((BigDecimal) status[12]);
				apiSummaryResDto.setDocType(String.valueOf(status[13]));
				apiSummaryResDtos.add(apiSummaryResDto);
			} catch (NullPointerException e) {
				throw new NullPointerException(
						"Please provide cutoff date for selected entity -> ");
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("The response data for datastatus file summary is:",
					apiSummaryResDtos);
		}
		return apiSummaryResDtos;
	}
}
