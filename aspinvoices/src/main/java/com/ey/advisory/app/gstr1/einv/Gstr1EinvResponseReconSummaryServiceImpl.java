package com.ey.advisory.app.gstr1.einv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("Gstr1EinvResponseReconSummaryServiceImpl")
public class Gstr1EinvResponseReconSummaryServiceImpl
		implements Gstr1EinvResponseReconSummaryService {

	@Autowired
	@Qualifier("Gstr1EinvResponseReconSummaryDaoImpl")
	private Gstr1EinvResponseReconSummaryDao respReconSummaryDao;

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	private static final DateTimeFormatter timeFormat = DateTimeFormatter
			.ofPattern("HH:mm:ss");

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	@Override
	public List<ResponseSummaryDto> getResponseSummData(List<String> gstins,
			String taxPeriod) {
		try {
			Integer returnPeriod = GenUtil
					.getReturnPeriodFromTaxPeriod(taxPeriod);
			List<Object[]> objList = respReconSummaryDao
					.getResponseSummary(gstins, returnPeriod);

			LOGGER.debug("Converting of list<obj[]> to List<dto> BEGIN");

			List<ResponseSummaryDto> retList = objList.stream()
					.map(o -> convertToResponseDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<String> sections = new ArrayList<>();
			sections.add("CDNR");
			sections.add("CDNUR");

			Map<String, ResponseSummaryDto> reqMap = retList.stream()
					.collect(Collectors.groupingBy(o -> o.getParticular(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			List<ResponseSummaryDto> valueList = reqMap.values().stream()
					.collect(Collectors.toList());

			List<ResponseSummaryDto> respList = convertResponseSummScreenLevelData(
					valueList, sections);
			Collections.sort(respList, (a, b) -> a.getOrderPosition()
					.compareToIgnoreCase(b.getOrderPosition()));

			return respList;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing getResponseSummData  "
							+ "  for gstins :%s and taxPeriod :%s ",
					gstins, taxPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private ResponseSummaryDto addDto(ResponseSummaryDto a,
			ResponseSummaryDto b) {
		ResponseSummaryDto dto = new ResponseSummaryDto();
		dto.setParticular(a.getParticular());
		dto.setDigiCount(a.getDigiCount() != null && b.getDigiCount() != null
				? a.getDigiCount().add(b.getDigiCount()) : BigInteger.ZERO);
		dto.setDigiTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getDigiTaxableVal().add(b.getDigiTaxableVal())));
		dto.setDigiTotalTax(GenUtil.defaultToZeroIfNull(
				a.getDigiTotalTax().add(b.getDigiTotalTax())));
		dto.setSaveCount(a.getSaveCount() != null && b.getSaveCount() != null
				? a.getSaveCount().add(b.getSaveCount()) : BigInteger.ZERO);
		dto.setSaveTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getSaveTaxableVal().add(b.getSaveTaxableVal())));
		dto.setSaveTotalTax(GenUtil.defaultToZeroIfNull(
				a.getSaveTotalTax().add(b.getSaveTotalTax())));
		dto.setNotSaveCount(
				a.getNotSaveCount() != null && b.getNotSaveCount() != null
						? a.getNotSaveCount().add(b.getNotSaveCount())
						: BigInteger.ZERO);
		dto.setNotSaveTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getNotSaveTaxableVal().add(b.getNotSaveTaxableVal())));
		dto.setNotSaveTotalTax(GenUtil.defaultToZeroIfNull(
				a.getNotSaveTotalTax().add(b.getNotSaveTotalTax())));
		dto.setDelCount(a.getDelCount() != null && b.getDelCount() != null
				? a.getDelCount().add(b.getDelCount()) : BigInteger.ZERO);
		dto.setDelTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getDelTaxableVal().add(b.getDelTaxableVal())));
		dto.setDelTotalTax(GenUtil.defaultToZeroIfNull(
				a.getDelTotalTax().add(b.getDelTotalTax())));
		dto.setSavedCount(a.getSavedCount() != null && b.getSavedCount() != null
				? a.getSavedCount().add(b.getSavedCount()) : BigInteger.ZERO);
		dto.setSavedTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getSavedTaxableVal().add(b.getSavedTaxableVal())));
		dto.setSavedTotalTax(GenUtil.defaultToZeroIfNull(
				a.getSavedTotalTax().add(b.getSavedTotalTax())));
		dto.setPendingCount(
				a.getPendingCount() != null && b.getPendingCount() != null
						? a.getPendingCount().add(b.getPendingCount())
						: BigInteger.ZERO);
		dto.setPendingTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getPendingTaxableVal().add(b.getPendingTaxableVal())));
		dto.setPendingTotalTax(GenUtil.defaultToZeroIfNull(
				a.getPendingTotalTax().add(b.getPendingTotalTax())));
		return dto;
	}

	private ResponseSummaryDto convertToResponseDto(Object[] arr) {
		ResponseSummaryDto dto = new ResponseSummaryDto();
		try {
			dto.setGstin(arr[0] != null ? (String) arr[0] : "");
			dto.setTaxPeriod(arr[1] != null ? (String) arr[1] : "");
			dto.setParticular(arr[2] != null ? (String) arr[2] : "");
			dto.setDigiCount(
					arr[3] != null ? GenUtil.getBigInteger(arr[3]) : BigInteger.ZERO);
			dto.setDigiTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[4]));
			dto.setDigiTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[5]));
			dto.setSaveCount(
					arr[6] != null ? GenUtil.getBigInteger(arr[6]) : BigInteger.ZERO);
			dto.setSaveTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[7]));
			dto.setSaveTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[8]));
			dto.setNotSaveCount(
					arr[9] != null ? GenUtil.getBigInteger(arr[9]) : BigInteger.ZERO);
			dto.setNotSaveTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[10]));
			dto.setNotSaveTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[11]));
			dto.setDelCount(
					arr[12] != null ? GenUtil.getBigInteger(arr[12]) : BigInteger.ZERO);
			dto.setDelTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[13]));
			dto.setDelTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[14]));
			dto.setSavedCount(
					arr[15] != null ? GenUtil.getBigInteger(arr[15]) : BigInteger.ZERO);
			dto.setSavedTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[16]));
			dto.setSavedTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[17]));
			dto.setPendingCount(
					arr[18] != null ? GenUtil.getBigInteger(arr[18]) : BigInteger.ZERO);
			dto.setPendingTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[19]));
			dto.setPendingTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[20]));
			dto.setLevel("L2");
			return dto;
		} catch (Exception ee) {
			String msg = String.format("Error while converting to dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	private List<ResponseSummaryDto> convertResponseSummScreenLevelData(
			List<ResponseSummaryDto> retList, List<String> sections) {
		// CDNR
		BigInteger digiCountCdnr = BigInteger.ZERO;
		BigDecimal digiTaxableValCdnr = BigDecimal.ZERO;
		BigDecimal digiTotalTaxCdnr = BigDecimal.ZERO;
		BigInteger saveCountCdnr = BigInteger.ZERO;
		BigDecimal saveTaxableValCdnr = BigDecimal.ZERO;
		BigDecimal saveTotalTaxCdnr = BigDecimal.ZERO;
		BigInteger notSaveCountCdnr = BigInteger.ZERO;
		BigDecimal notSaveTaxableValCdnr = BigDecimal.ZERO;
		BigDecimal notSaveTotalTaxCdnr = BigDecimal.ZERO;
		BigInteger delCountCdnr = BigInteger.ZERO;
		BigDecimal delTaxableValCdnr = BigDecimal.ZERO;
		BigDecimal delTotalTaxCdnr = BigDecimal.ZERO;
		BigInteger savedCountCdnr = BigInteger.ZERO;
		BigDecimal savedTaxableValCdnr = BigDecimal.ZERO;
		BigDecimal savedTotalTaxCdnr = BigDecimal.ZERO;
		BigInteger pendingCountCdnr = BigInteger.ZERO;
		BigDecimal pendingTaxableValCdnr = BigDecimal.ZERO;
		BigDecimal pendingTotalTaxCdnr = BigDecimal.ZERO;

		// CDNUR
		BigInteger digiCountCdnur = BigInteger.ZERO;
		BigDecimal digiTaxableValCdnur = BigDecimal.ZERO;
		BigDecimal digiTotalTaxCdnur = BigDecimal.ZERO;
		BigInteger saveCountCdnur = BigInteger.ZERO;
		BigDecimal saveTaxableValCdnur = BigDecimal.ZERO;
		BigDecimal saveTotalTaxCdnur = BigDecimal.ZERO;
		BigInteger notSaveCountCdnur = BigInteger.ZERO;
		BigDecimal notSaveTaxableValCdnur = BigDecimal.ZERO;
		BigDecimal notSaveTotalTaxCdnur = BigDecimal.ZERO;
		BigInteger delCountCdnur = BigInteger.ZERO;
		BigDecimal delTaxableValCdnur = BigDecimal.ZERO;
		BigDecimal delTotalTaxCdnur = BigDecimal.ZERO;
		BigInteger savedCountCdnur = BigInteger.ZERO;
		BigDecimal savedTaxableValCdnur = BigDecimal.ZERO;
		BigDecimal savedTotalTaxCdnur = BigDecimal.ZERO;
		BigInteger pendingCountCdnur = BigInteger.ZERO;
		BigDecimal pendingTaxableValCdnur = BigDecimal.ZERO;
		BigDecimal pendingTotalTaxCdnur = BigDecimal.ZERO;

		List<ResponseSummaryDto> obj = new ArrayList<>();
		final String level = "L1";

		LOGGER.debug("Calculate value for level 1 and level 2 started");

		for (ResponseSummaryDto resp : retList) {

			ResponseSummaryDto reconSumm = new ResponseSummaryDto();

			if (resp.getParticular().contains("CDNR")) {
				digiCountCdnr = digiCountCdnr.add(resp.getDigiCount());
				saveCountCdnr = saveCountCdnr.add(resp.getSaveCount());
				notSaveCountCdnr = notSaveCountCdnr.add(resp.getNotSaveCount());
				delCountCdnr = delCountCdnr.add(resp.getDelCount());
				savedCountCdnr = savedCountCdnr.add(resp.getSavedCount());
				pendingCountCdnr = pendingCountCdnr.add(resp.getPendingCount());

				if (resp.getParticular().contains("_DR")) {
					digiTaxableValCdnr = digiTaxableValCdnr
							.add(resp.getDigiTaxableVal());
					digiTotalTaxCdnr = digiTotalTaxCdnr
							.add(resp.getDigiTotalTax());
					saveTaxableValCdnr = saveTaxableValCdnr
							.add(resp.getSaveTaxableVal());
					saveTotalTaxCdnr = saveTotalTaxCdnr
							.add(resp.getSaveTotalTax());
					notSaveTaxableValCdnr = notSaveTaxableValCdnr
							.add(resp.getNotSaveTaxableVal());
					notSaveTotalTaxCdnr = notSaveTotalTaxCdnr
							.add(resp.getNotSaveTotalTax());
					delTaxableValCdnr = delTaxableValCdnr
							.add(resp.getDelTaxableVal());
					delTotalTaxCdnr = delTotalTaxCdnr
							.add(resp.getDelTotalTax());
					savedTaxableValCdnr = savedTaxableValCdnr
							.add(resp.getSavedTaxableVal());
					savedTotalTaxCdnr = savedTotalTaxCdnr
							.add(resp.getSavedTotalTax());
					pendingTaxableValCdnr = pendingTaxableValCdnr
							.add(resp.getPendingTaxableVal());
					pendingTotalTaxCdnr = pendingTotalTaxCdnr
							.add(resp.getPendingTotalTax());
				} else if (resp.getParticular().contains("_CR")) {
					digiTaxableValCdnr = digiTaxableValCdnr
							.subtract(resp.getDigiTaxableVal());
					digiTotalTaxCdnr = digiTotalTaxCdnr
							.subtract(resp.getDigiTotalTax());
					saveTaxableValCdnr = saveTaxableValCdnr
							.subtract(resp.getSaveTaxableVal());
					saveTotalTaxCdnr = saveTotalTaxCdnr
							.subtract(resp.getSaveTotalTax());
					notSaveTaxableValCdnr = notSaveTaxableValCdnr
							.subtract(resp.getNotSaveTaxableVal());
					notSaveTotalTaxCdnr = notSaveTotalTaxCdnr
							.subtract(resp.getNotSaveTotalTax());
					delTaxableValCdnr = delTaxableValCdnr
							.subtract(resp.getDelTaxableVal());
					delTotalTaxCdnr = delTotalTaxCdnr
							.subtract(resp.getDelTotalTax());
					savedTaxableValCdnr = savedTaxableValCdnr
							.subtract(resp.getSavedTaxableVal());
					savedTotalTaxCdnr = savedTotalTaxCdnr
							.subtract(resp.getSavedTotalTax());
					pendingTaxableValCdnr = pendingTaxableValCdnr
							.subtract(resp.getPendingTaxableVal());
					pendingTotalTaxCdnr = pendingTotalTaxCdnr
							.subtract(resp.getPendingTotalTax());
				}

			} else if (resp.getParticular().contains("CDNUR")) {

				digiCountCdnur = digiCountCdnur.add(resp.getDigiCount());
				saveCountCdnur = saveCountCdnur.add(resp.getSaveCount());
				notSaveCountCdnur = notSaveCountCdnur
						.add(resp.getNotSaveCount());
				delCountCdnur = delCountCdnur.add(resp.getDelCount());
				savedCountCdnur = savedCountCdnur.add(resp.getSavedCount());
				pendingCountCdnur = pendingCountCdnur
						.add(resp.getPendingCount());

				if (resp.getParticular().contains("_DR")) {
					digiTaxableValCdnur = digiTaxableValCdnur
							.add(resp.getDigiTaxableVal());
					digiTotalTaxCdnur = digiTotalTaxCdnur
							.add(resp.getDigiTotalTax());
					saveTaxableValCdnur = saveTaxableValCdnur
							.add(resp.getSaveTaxableVal());
					saveTotalTaxCdnur = saveTotalTaxCdnur
							.add(resp.getSaveTotalTax());
					notSaveTaxableValCdnur = notSaveTaxableValCdnur
							.add(resp.getNotSaveTaxableVal());
					notSaveTotalTaxCdnur = notSaveTotalTaxCdnur
							.add(resp.getNotSaveTotalTax());
					delTaxableValCdnur = delTaxableValCdnur
							.add(resp.getDelTaxableVal());
					delTotalTaxCdnur = delTotalTaxCdnur
							.add(resp.getDelTotalTax());
					savedTaxableValCdnur = savedTaxableValCdnur
							.add(resp.getSavedTaxableVal());
					savedTotalTaxCdnur = savedTotalTaxCdnur
							.add(resp.getSavedTotalTax());
					pendingTaxableValCdnur = pendingTaxableValCdnur
							.add(resp.getPendingTaxableVal());
					pendingTotalTaxCdnur = pendingTotalTaxCdnur
							.add(resp.getPendingTotalTax());
				} else if (resp.getParticular().contains("_CR")) {
					digiTaxableValCdnur = digiTaxableValCdnur
							.subtract(resp.getDigiTaxableVal());
					digiTotalTaxCdnur = digiTotalTaxCdnur
							.subtract(resp.getDigiTotalTax());
					saveTaxableValCdnur = saveTaxableValCdnur
							.subtract(resp.getSaveTaxableVal());
					saveTotalTaxCdnur = saveTotalTaxCdnur
							.subtract(resp.getSaveTotalTax());
					notSaveTaxableValCdnur = notSaveTaxableValCdnur
							.subtract(resp.getNotSaveTaxableVal());
					notSaveTotalTaxCdnur = notSaveTotalTaxCdnur
							.subtract(resp.getNotSaveTotalTax());
					delTaxableValCdnur = delTaxableValCdnur
							.subtract(resp.getDelTaxableVal());
					delTotalTaxCdnur = delTotalTaxCdnur
							.subtract(resp.getDelTotalTax());
					savedTaxableValCdnur = savedTaxableValCdnur
							.subtract(resp.getSavedTaxableVal());
					savedTotalTaxCdnur = savedTotalTaxCdnur
							.subtract(resp.getSavedTotalTax());
					pendingTaxableValCdnur = pendingTaxableValCdnur
							.subtract(resp.getPendingTaxableVal());
					pendingTotalTaxCdnur = pendingTotalTaxCdnur
							.subtract(resp.getPendingTotalTax());
				}

			}
			String particular = resp.getParticular() != null
					? (String) resp.getParticular() : "";
			reconSumm.setParticular(particular);
			reconSumm.setDigiCount(resp.getDigiCount());
			reconSumm.setDigiTaxableVal(resp.getDigiTaxableVal());
			reconSumm.setDigiTotalTax(resp.getDigiTotalTax());
			reconSumm.setSaveCount(resp.getSaveCount());
			reconSumm.setSaveTaxableVal(resp.getSaveTaxableVal());
			reconSumm.setSaveTotalTax(resp.getSaveTotalTax());
			reconSumm.setNotSaveCount(resp.getNotSaveCount());
			reconSumm.setNotSaveTaxableVal(resp.getNotSaveTaxableVal());
			reconSumm.setNotSaveTotalTax(resp.getNotSaveTotalTax());
			reconSumm.setDelCount(resp.getDelCount());
			reconSumm.setDelTaxableVal(resp.getDelTaxableVal());
			reconSumm.setDelTotalTax(resp.getDelTotalTax());
			reconSumm.setSavedCount(resp.getSavedCount());
			reconSumm.setSavedTaxableVal(resp.getSavedTaxableVal());
			reconSumm.setSavedTotalTax(resp.getSavedTotalTax());
			reconSumm.setPendingCount(resp.getPendingCount());
			reconSumm.setPendingTaxableVal(resp.getPendingTaxableVal());
			reconSumm.setPendingTotalTax(resp.getPendingTotalTax());

			if (particular.equalsIgnoreCase("B2B")) {
				reconSumm.setOrderPosition("A");
				reconSumm.setLevel("L1");
			} else if (particular.equalsIgnoreCase("EXPORTS")) {
				reconSumm.setOrderPosition("B");
				reconSumm.setLevel("L1");
			} else if (particular.equalsIgnoreCase("CDNR_CR")) {
				reconSumm.setParticular("-CR");
				reconSumm.setOrderPosition("D");
				reconSumm.setLevel("L2");
			} else if (particular.equalsIgnoreCase("CDNR_DR")) {
				reconSumm.setParticular("-DR");
				reconSumm.setOrderPosition("E");
				reconSumm.setLevel("L2");
			} else if (particular.equalsIgnoreCase("CDNUR_EXP_CR")) {
				reconSumm.setParticular("-CR");
				reconSumm.setOrderPosition("G");
				reconSumm.setLevel("L2");
			} else if (particular.equalsIgnoreCase("CDNUR_EXP_DR")) {
				reconSumm.setParticular("-DR");
				reconSumm.setOrderPosition("H");
				reconSumm.setLevel("L2");
			}
			obj.add(reconSumm);
		}

		LOGGER.debug("Calculate value for level 1 and level 2 ended");
		LOGGER.debug("Setting values for level 1 started");

		for (int i = 0; i < sections.size(); i++) {
			ResponseSummaryDto gstr2Recon = new ResponseSummaryDto();
			if ("CDNR".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setParticular(sections.get(i));
				gstr2Recon.setDigiCount(digiCountCdnr);
				gstr2Recon.setDigiTaxableVal(digiTaxableValCdnr);
				gstr2Recon.setDigiTotalTax(digiTotalTaxCdnr);
				gstr2Recon.setSaveCount(saveCountCdnr);
				gstr2Recon.setSaveTaxableVal(saveTaxableValCdnr);
				gstr2Recon.setSaveTotalTax(saveTotalTaxCdnr);
				gstr2Recon.setNotSaveCount(notSaveCountCdnr);
				gstr2Recon.setNotSaveTaxableVal(notSaveTaxableValCdnr);
				gstr2Recon.setNotSaveTotalTax(notSaveTotalTaxCdnr);
				gstr2Recon.setDelCount(delCountCdnr);
				gstr2Recon.setDelTaxableVal(delTaxableValCdnr);
				gstr2Recon.setDelTotalTax(delTotalTaxCdnr);
				gstr2Recon.setSavedCount(savedCountCdnr);
				gstr2Recon.setSavedTaxableVal(savedTaxableValCdnr);
				gstr2Recon.setSavedTotalTax(savedTotalTaxCdnr);
				gstr2Recon.setPendingCount(pendingCountCdnr);
				gstr2Recon.setPendingTaxableVal(pendingTaxableValCdnr);
				gstr2Recon.setPendingTotalTax(pendingTotalTaxCdnr);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("C");
			} else if ("CDNUR".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setParticular(sections.get(i));
				gstr2Recon.setDigiCount(digiCountCdnur);
				gstr2Recon.setDigiTaxableVal(digiTaxableValCdnur);
				gstr2Recon.setDigiTotalTax(digiTotalTaxCdnur);
				gstr2Recon.setSaveCount(saveCountCdnur);
				gstr2Recon.setSaveTaxableVal(saveTaxableValCdnur);
				gstr2Recon.setSaveTotalTax(saveTotalTaxCdnur);
				gstr2Recon.setNotSaveCount(notSaveCountCdnur);
				gstr2Recon.setNotSaveTaxableVal(notSaveTaxableValCdnur);
				gstr2Recon.setNotSaveTotalTax(notSaveTotalTaxCdnur);
				gstr2Recon.setDelCount(delCountCdnur);
				gstr2Recon.setDelTaxableVal(delTaxableValCdnur);
				gstr2Recon.setDelTotalTax(delTotalTaxCdnur);
				gstr2Recon.setSavedCount(savedCountCdnur);
				gstr2Recon.setSavedTaxableVal(savedTaxableValCdnur);
				gstr2Recon.setSavedTotalTax(savedTotalTaxCdnur);
				gstr2Recon.setPendingCount(pendingCountCdnur);
				gstr2Recon.setPendingTaxableVal(pendingTaxableValCdnur);
				gstr2Recon.setPendingTotalTax(pendingTotalTaxCdnur);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("F");
			}
			obj.add(gstr2Recon);
		}
		LOGGER.debug("Setting values for level 1 ended");

		return obj;
	}

	@Override
	public Workbook getResponseSummReport(List<String> gstins, String taxPeriod,
			Long entityId) {
		try {
			Workbook workbook = null;
			Integer returnPeriod = GenUtil
					.getReturnPeriodFromTaxPeriod(taxPeriod);
			List<Object[]> objList = respReconSummaryDao
					.getResponseSummary(gstins, returnPeriod);

			List<ResponseSummaryReportDto> retList = objList.stream()
					.map(o -> convertToResponseReportDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			Collections.sort(retList,
					(a, b) -> a.getGstin().compareToIgnoreCase(b.getGstin()));
			workbook = writeToExcel(retList, entityId, taxPeriod);

			return workbook;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing getPreReconSummData  "
							+ "  for gstins :%s and taxPeriod :%s ",
					gstins, taxPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private Workbook writeToExcel(List<ResponseSummaryReportDto> retList,
			Long entityId, String taxPeriod) {

		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		try {

			if (retList != null && !retList.isEmpty()) {

				String[] invoiceHeaders = commonUtility
						.getProp("response.recon.summary.report.header")
						.split(",");

				String fileName = "Response_Summary_Report_Template.xlsx";
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileName);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Gstr1EinvResponseReconSummaryServiceImpl.writeToExcel "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				Worksheet sheet = workbook.getWorksheets().get(0);
				Cells reportCells = sheet.getCells();

				// Hiding the gridlines of the first worksheet of the Excel file

				sheet.setGridlinesVisible(false);

				populateEntityDetails(sheet, taxPeriod, entityId);

				reportCells.importCustomObjects(retList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn, retList.size(),
						true, "yyyy-mm-dd", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "Gstr1EinvResponseReconSummaryServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.RESPONSE_RECON_SUMMARY,
						SaveFormat.XLSX);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Workbook has been generated successfully with the"
									+ " error response list in the directory : %s",
							workbook.getAbsolutePath());
				}

			} else {
				throw new AppException(
						"No records found, cannot generate report");
			}
			return workbook;
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while "
							+ "saving excel sheet into folder, %s ",
					e.getMessage());
			LOGGER.error(msg);
			throw new AppException(e.getMessage(), e);
		}
	}

	private void populateEntityDetails(Worksheet sheet, String taxPeriod,
			Long entityId) {

		Optional<EntityInfoEntity> optional = entityInfoRepo.findById(entityId);
		EntityInfoEntity entity = optional.get();
		String entityName = entity.getEntityName();

		Cell cellB2 = sheet.getCells().get("B3");

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime istCreatedDate = EYDateUtil.toISTDateTimeFromUTC(now);

		String formatDate = istCreatedDate.toLocalDate().format(format);

		String formatTime = istCreatedDate.toLocalTime().format(timeFormat);

		String month = Month.of(Integer.valueOf(taxPeriod.substring(0, 2)))
				.name();
		String reqMonth = Month.valueOf(month).getDisplayName(TextStyle.FULL,
				Locale.ENGLISH);
		cellB2.setValue("ENTITY NAME - " + entityName + " | " + "Date -"
				+ formatDate + " | " + "Time -" + formatTime + "|"
				+ "Tax Period - " + reqMonth + " " + taxPeriod.substring(2, 6));
	}

	private ResponseSummaryReportDto convertToResponseReportDto(Object[] arr) {
		ResponseSummaryReportDto dto = new ResponseSummaryReportDto();
		try {
			String gstin = arr[0] != null ? (String) arr[0] : "";
			dto.setGstin(gstin);
			// dto.setTaxPeriod(arr[1] != null ? (String) arr[1] : "");
			String particular = arr[2] != null ? (String) arr[2] : "";
			if ("B2B".equalsIgnoreCase(particular)) {
				dto.setParticular(particular);
			} else if ("EXPORTS".equalsIgnoreCase(particular)) {
				dto.setParticular(particular);
			} else if ("CDNR_CR".equalsIgnoreCase(particular)) {
				dto.setParticular("CDNR - Credit Notes");
			} else if ("CDNR_DR".equalsIgnoreCase(particular)) {
				dto.setParticular("CDNR - Debit Notes");
			} else if ("CDNUR_EXP_CR".equalsIgnoreCase(particular)) {
				dto.setParticular("CDNUR (Exports) - Credit Notes");
			} else if ("CDNUR_EXP_DR".equalsIgnoreCase(particular)) {
				dto.setParticular("CDNUR (Exports) - Debit Notes");
			}

			dto.setDigiCount(
					arr[3] != null ? GenUtil.getBigInteger(arr[3]) : BigInteger.ZERO);
			dto.setDigiTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[4]));
			dto.setDigiTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[5]));
			dto.setSaveCount(
					arr[6] != null ? GenUtil.getBigInteger(arr[6]) : BigInteger.ZERO);
			dto.setSaveTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[7]));
			dto.setSaveTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[8]));
			dto.setNotSaveCount(
					arr[9] != null ? GenUtil.getBigInteger(arr[9]) : BigInteger.ZERO);
			dto.setNotSaveTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[10]));
			dto.setNotSaveTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[11]));
			dto.setDelCount(
					arr[12] != null ? GenUtil.getBigInteger(arr[12]) : BigInteger.ZERO);
			dto.setDelTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[13]));
			dto.setDelTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[14]));
			dto.setSavedCount(
					arr[15] != null ? GenUtil.getBigInteger(arr[15]) : BigInteger.ZERO);
			dto.setSavedTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[16]));
			dto.setSavedTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[17]));
			dto.setPendingCount(
					arr[18] != null ? GenUtil.getBigInteger(arr[18]) : BigInteger.ZERO);
			dto.setPendingTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[19]));
			dto.setPendingTotalTax(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[20]));
			return dto;
		} catch (Exception ee) {
			String msg = String.format("Error while converting to report dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}
}
