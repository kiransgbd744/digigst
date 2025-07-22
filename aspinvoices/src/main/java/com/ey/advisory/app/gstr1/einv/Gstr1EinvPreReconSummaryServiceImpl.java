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
@Component("Gstr1EinvPreReconSummaryServiceImpl")
public class Gstr1EinvPreReconSummaryServiceImpl
		implements Gstr1EinvPreReconSummaryService {

	@Autowired
	@Qualifier("Gstr1EinvPreReconSummaryDaoImpl")
	private Gstr1EinvPreReconSummaryDao preReconSummaryDao;

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
	public List<PreReconSummaryDto> getPreReconSummData(List<String> gstins,
			String taxPeriod) {
		try {
			Integer returnPeriod = GenUtil
					.getReturnPeriodFromTaxPeriod(taxPeriod);
			List<Object[]> objList = preReconSummaryDao
					.getPreReconSummary(gstins, returnPeriod);

			LOGGER.debug("Converting of list<obj[]> to List<dto> BEGIN");

			List<PreReconSummaryDto> retList = objList.stream()
					.map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			List<String> sections = new ArrayList<>();
			sections.add("Invoice");
			sections.add("Credit Notes");
			sections.add("Debit Notes");
			sections.add("Cancellations");
			sections.add("Deletion");

			Map<String, PreReconSummaryDto> reqMap = retList.stream()
					.collect(Collectors.groupingBy(o -> o.getSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			List<PreReconSummaryDto> valueList = reqMap.values().stream()
					.collect(Collectors.toList());

			List<PreReconSummaryDto> respList = convertScreenLevelData(
					valueList, sections);
			Collections.sort(respList, (a, b) -> a.getOrderPosition()
					.compareToIgnoreCase(b.getOrderPosition()));

			return respList;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing getPreReconSummData  "
							+ "  for gstins :%s and taxPeriod :%s ",
					gstins, taxPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private PreReconSummaryDto convertToDto(Object[] arr) {
		PreReconSummaryDto dto = new PreReconSummaryDto();
		try {
			dto.setGstin(arr[0] != null ? (String) arr[0] : "");
			dto.setTaxPeriod(arr[1] != null ? (String) arr[1] : "");
			dto.setSection(arr[2] != null ? (String) arr[2] : "");
			dto.setEinvCount(
					arr[3] != null ? GenUtil.getBigInteger(arr[3]) : BigInteger.ZERO);
			dto.setEinvTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[4]));
			dto.setEinvIgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[5]));
			dto.setEinvCgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[6]));
			dto.setEinvSgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[7]));
			dto.setEinvCess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[8]));
			dto.setSrCount(
					arr[9] != null ? GenUtil.getBigInteger(arr[9]) : BigInteger.ZERO);
			dto.setSrTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[10]));
			dto.setSrIgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[11]));
			dto.setSrCgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[12]));
			dto.setSrSgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[13]));
			dto.setSrCess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[14]));
			dto.setLevel("L2");
			return dto;
		} catch (Exception ee) {
			String msg = String.format("Error while converting to dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}

	}

	private List<PreReconSummaryDto> convertScreenLevelData(
			List<PreReconSummaryDto> retList, List<String> sections) {
		// Invoice
		BigInteger einvCountInvoice = BigInteger.ZERO;
		BigDecimal einvTaxableValInvoice = BigDecimal.ZERO;
		BigDecimal einvIgstInvoice = BigDecimal.ZERO;
		BigDecimal einvCgstInvoice = BigDecimal.ZERO;
		BigDecimal einvSgstInvoice = BigDecimal.ZERO;
		BigDecimal einvCessInvoice = BigDecimal.ZERO;
		BigInteger srCountInvoice = BigInteger.ZERO;
		BigDecimal srTaxableValInvoice = BigDecimal.ZERO;
		BigDecimal srIgstInvoice = BigDecimal.ZERO;
		BigDecimal srCgstInvoice = BigDecimal.ZERO;
		BigDecimal srSgstInvoice = BigDecimal.ZERO;
		BigDecimal srCessInvoice = BigDecimal.ZERO;

		// Credit Notes
		BigInteger einvCountCredNote = BigInteger.ZERO;
		BigDecimal einvTaxableValCredNote = BigDecimal.ZERO;
		BigDecimal einvIgstCredNote = BigDecimal.ZERO;
		BigDecimal einvCgstCredNote = BigDecimal.ZERO;
		BigDecimal einvSgstCredNote = BigDecimal.ZERO;
		BigDecimal einvCessCredNote = BigDecimal.ZERO;
		BigInteger srCountCredNote = BigInteger.ZERO;
		BigDecimal srTaxableValCredNote = BigDecimal.ZERO;
		BigDecimal srIgstCredNote = BigDecimal.ZERO;
		BigDecimal srCgstCredNote = BigDecimal.ZERO;
		BigDecimal srSgstCredNote = BigDecimal.ZERO;
		BigDecimal srCessCredNote = BigDecimal.ZERO;

		// Debit Nootes
		BigInteger einvCountDebNote = BigInteger.ZERO;
		BigDecimal einvTaxableValDebNote = BigDecimal.ZERO;
		BigDecimal einvIgstDebNote = BigDecimal.ZERO;
		BigDecimal einvCgstDebNote = BigDecimal.ZERO;
		BigDecimal einvSgstDebNote = BigDecimal.ZERO;
		BigDecimal einvCessDebNote = BigDecimal.ZERO;
		BigInteger srCountDebNote = BigInteger.ZERO;
		BigDecimal srTaxableValDebNote = BigDecimal.ZERO;
		BigDecimal srIgstDebNote = BigDecimal.ZERO;
		BigDecimal srCgstDebNote = BigDecimal.ZERO;
		BigDecimal srSgstDebNote = BigDecimal.ZERO;
		BigDecimal srCessDebNote = BigDecimal.ZERO;

		// Cancellations
		BigInteger einvCountCancel = BigInteger.ZERO;
		BigDecimal einvTaxableValCancel = BigDecimal.ZERO;
		BigDecimal einvIgstCancel = BigDecimal.ZERO;
		BigDecimal einvCgstCancel = BigDecimal.ZERO;
		BigDecimal einvSgstCancel = BigDecimal.ZERO;
		BigDecimal einvCessCancel = BigDecimal.ZERO;
		BigInteger srCountCancel = BigInteger.ZERO;
		BigDecimal srTaxableValCancel = BigDecimal.ZERO;
		BigDecimal srIgstCancel = BigDecimal.ZERO;
		BigDecimal srCgstCancel = BigDecimal.ZERO;
		BigDecimal srSgstCancel = BigDecimal.ZERO;
		BigDecimal srCessCancel = BigDecimal.ZERO;

		// Deletions
		BigInteger einvCountDelete = BigInteger.ZERO;
		BigDecimal einvTaxableValDelete = BigDecimal.ZERO;
		BigDecimal einvIgstDelete = BigDecimal.ZERO;
		BigDecimal einvCgstDelete = BigDecimal.ZERO;
		BigDecimal einvSgstDelete = BigDecimal.ZERO;
		BigDecimal einvCessDelete = BigDecimal.ZERO;
		BigInteger srCountDelete = BigInteger.ZERO;
		BigDecimal srTaxableValDelete = BigDecimal.ZERO;
		BigDecimal srIgstDelete = BigDecimal.ZERO;
		BigDecimal srCgstDelete = BigDecimal.ZERO;
		BigDecimal srSgstDelete = BigDecimal.ZERO;
		BigDecimal srCessDelete = BigDecimal.ZERO;

		List<PreReconSummaryDto> obj = new ArrayList<>();
		final String level = "L1";

		LOGGER.debug("Calculate value for level 1 and level 2 started");

		for (PreReconSummaryDto resp : retList) {

			PreReconSummaryDto gstr2Recon = new PreReconSummaryDto();

			if (resp.getSection().contains("INV_")) {
				einvCountInvoice = einvCountInvoice.add(resp.getEinvCount());
				einvTaxableValInvoice = einvTaxableValInvoice
						.add(resp.getEinvTaxableVal());
				einvIgstInvoice = einvIgstInvoice.add(resp.getEinvIgst());
				einvCgstInvoice = einvCgstInvoice.add(resp.getEinvCgst());
				einvSgstInvoice = einvSgstInvoice.add(resp.getEinvSgst());
				einvCessInvoice = einvCessInvoice.add(resp.getEinvCess());
				srCountInvoice = srCountInvoice.add(resp.getSrCount());
				srTaxableValInvoice = srTaxableValInvoice
						.add(resp.getSrTaxableVal());
				srIgstInvoice = srIgstInvoice.add(resp.getSrIgst());
				srCgstInvoice = srCgstInvoice.add(resp.getSrCgst());
				srSgstInvoice = srSgstInvoice.add(resp.getSrSgst());
				srCessInvoice = srCessInvoice.add(resp.getSrCess());

			} else if (resp.getSection().contains("C_")) {
				einvCountCredNote = einvCountCredNote.add(resp.getEinvCount());
				einvTaxableValCredNote = einvTaxableValCredNote
						.add(resp.getEinvTaxableVal());
				einvIgstCredNote = einvIgstCredNote.add(resp.getEinvIgst());
				einvCgstCredNote = einvCgstCredNote.add(resp.getEinvCgst());
				einvSgstCredNote = einvSgstCredNote.add(resp.getEinvSgst());
				einvCessCredNote = einvCessCredNote.add(resp.getEinvCess());
				srCountCredNote = srCountCredNote.add(resp.getSrCount());
				srTaxableValCredNote = srTaxableValCredNote
						.add(resp.getSrTaxableVal());
				srIgstCredNote = srIgstCredNote.add(resp.getSrIgst());
				srCgstCredNote = srCgstCredNote.add(resp.getSrCgst());
				srSgstCredNote = srSgstCredNote.add(resp.getSrSgst());
				srCessCredNote = srCessCredNote.add(resp.getSrCess());

			} else if (resp.getSection().contains("D_")) {
				einvCountDebNote = einvCountDebNote.add(resp.getEinvCount());
				einvTaxableValDebNote = einvTaxableValDebNote
						.add(resp.getEinvTaxableVal());
				einvIgstDebNote = einvIgstDebNote.add(resp.getEinvIgst());
				einvCgstDebNote = einvCgstDebNote.add(resp.getEinvCgst());
				einvSgstDebNote = einvSgstDebNote.add(resp.getEinvSgst());
				einvCessDebNote = einvCessDebNote.add(resp.getEinvCess());
				srCountDebNote = srCountDebNote.add(resp.getSrCount());
				srTaxableValDebNote = srTaxableValDebNote
						.add(resp.getSrTaxableVal());
				srIgstDebNote = srIgstDebNote.add(resp.getSrIgst());
				srCgstDebNote = srCgstDebNote.add(resp.getSrCgst());
				srSgstDebNote = srSgstDebNote.add(resp.getSrSgst());
				srCessDebNote = srCessDebNote.add(resp.getSrCess());
			} else if (resp.getSection().contains("CAN_")) {
				einvCountCancel = einvCountCancel.add(resp.getEinvCount());
				einvTaxableValCancel = einvTaxableValCancel
						.add(resp.getEinvTaxableVal());
				einvIgstCancel = einvIgstCancel.add(resp.getEinvIgst());
				einvCgstCancel = einvCgstCancel.add(resp.getEinvCgst());
				einvSgstCancel = einvSgstCancel.add(resp.getEinvSgst());
				einvCessCancel = einvCessCancel.add(resp.getEinvCess());
				srCountCancel = srCountCancel.add(resp.getSrCount());
				srTaxableValCancel = srTaxableValCancel
						.add(resp.getSrTaxableVal());
				srIgstCancel = srIgstCancel.add(resp.getSrIgst());
				srCgstCancel = srCgstCancel.add(resp.getSrCgst());
				srSgstCancel = srSgstCancel.add(resp.getSrSgst());
				srCessCancel = srCessCancel.add(resp.getSrCess());
			} else if (resp.getSection().contains("DEL_")) {
				einvCountDelete = einvCountDelete.add(resp.getEinvCount());
				einvTaxableValDelete = einvTaxableValDelete
						.add(resp.getEinvTaxableVal());
				einvIgstDelete = einvIgstDelete.add(resp.getEinvIgst());
				einvCgstDelete = einvCgstDelete.add(resp.getEinvCgst());
				einvSgstDelete = einvSgstDelete.add(resp.getEinvSgst());
				einvCessDelete = einvCessDelete.add(resp.getEinvCess());
				srCountDelete = srCountDelete.add(resp.getSrCount());
				srTaxableValDelete = srTaxableValDelete
						.add(resp.getSrTaxableVal());
				srIgstDelete = srIgstDelete.add(resp.getSrIgst());
				srCgstDelete = srCgstDelete.add(resp.getSrCgst());
				srSgstDelete = srSgstDelete.add(resp.getSrSgst());
				srCessDelete = srCessDelete.add(resp.getSrCess());
			}
			String section = resp.getSection() != null
					? (String) resp.getSection() : "";
			gstr2Recon.setSection(section);
			gstr2Recon.setEinvCount(resp.getEinvCount());
			gstr2Recon.setEinvTaxableVal((BigDecimal) resp.getEinvTaxableVal());
			gstr2Recon.setEinvIgst((BigDecimal) resp.getEinvIgst());
			gstr2Recon.setEinvCgst((BigDecimal) resp.getEinvCgst());
			gstr2Recon.setEinvSgst((BigDecimal) resp.getEinvSgst());
			gstr2Recon.setEinvCess((BigDecimal) resp.getEinvCess());

			gstr2Recon.setSrCount(resp.getSrCount());
			gstr2Recon.setSrTaxableVal((BigDecimal) resp.getSrTaxableVal());
			gstr2Recon.setSrIgst((BigDecimal) resp.getSrIgst());
			gstr2Recon.setSrCgst((BigDecimal) resp.getSrCgst());
			gstr2Recon.setSrSgst((BigDecimal) resp.getSrSgst());
			gstr2Recon.setSrCess((BigDecimal) resp.getSrCess());

			if (section.equalsIgnoreCase("INV_B2B")) {
				gstr2Recon.setOrderPosition("B");
			} else if (section.equalsIgnoreCase("INV_SEZ")) {
				gstr2Recon.setOrderPosition("C");
			} else if (section.equalsIgnoreCase("INV_DE")) {
				gstr2Recon.setOrderPosition("D");
			} else if (section.equalsIgnoreCase("INV_EXP")) {
				gstr2Recon.setOrderPosition("E");
			} else if (section.equalsIgnoreCase("C_B2B")) {
				gstr2Recon.setOrderPosition("G");
			} else if (section.equalsIgnoreCase("C_SEZ")) {
				gstr2Recon.setOrderPosition("H");
			} else if (section.equalsIgnoreCase("C_DE")) {
				gstr2Recon.setOrderPosition("I");
			} else if (section.equalsIgnoreCase("C_EXP")) {
				gstr2Recon.setOrderPosition("J");
			} else if (section.equalsIgnoreCase("D_B2B")) {
				gstr2Recon.setOrderPosition("L");
			} else if (section.equalsIgnoreCase("D_SEZ")) {
				gstr2Recon.setOrderPosition("M");
			} else if (section.equalsIgnoreCase("D_DE")) {
				gstr2Recon.setOrderPosition("N");
			} else if (section.equalsIgnoreCase("D_EXP")) {
				gstr2Recon.setOrderPosition("O");
			} else if (section.equalsIgnoreCase("CAN_I")) {
				gstr2Recon.setOrderPosition("Q");
			} else if (section.equalsIgnoreCase("CAN_C")) {
				gstr2Recon.setOrderPosition("R");
			} else if (section.equalsIgnoreCase("CAN_D")) {
				gstr2Recon.setOrderPosition("S");
			} else if (section.equalsIgnoreCase("DEL_I")) {
				gstr2Recon.setOrderPosition("U");
			} else if (section.equalsIgnoreCase("DEL_C")) {
				gstr2Recon.setOrderPosition("V");
			} else if (section.equalsIgnoreCase("DEL_D")) {
				gstr2Recon.setOrderPosition("W");
			}
			obj.add(gstr2Recon);
		}

		LOGGER.debug("Calculate value for level 1 and level 2 ended");
		LOGGER.debug("Setting values for level 1 started");

		for (int i = 0; i < sections.size(); i++) {
			PreReconSummaryDto gstr2Recon = new PreReconSummaryDto();
			if ("Invoice".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setSection(sections.get(i));
				gstr2Recon.setEinvCount(einvCountInvoice);
				gstr2Recon.setEinvTaxableVal(einvTaxableValInvoice);
				gstr2Recon.setEinvIgst(einvIgstInvoice);
				gstr2Recon.setEinvCgst(einvCgstInvoice);
				gstr2Recon.setEinvSgst(einvSgstInvoice);
				gstr2Recon.setEinvCess(einvCessInvoice);
				gstr2Recon.setSrCount(srCountInvoice);
				gstr2Recon.setSrTaxableVal(srTaxableValInvoice);
				gstr2Recon.setSrIgst(srIgstInvoice);
				gstr2Recon.setSrCgst(srCgstInvoice);
				gstr2Recon.setSrSgst(srSgstInvoice);
				gstr2Recon.setSrCess(srCessInvoice);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("A");
			} else if ("Credit Notes".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setSection(sections.get(i));
				gstr2Recon.setEinvCount(einvCountCredNote);
				gstr2Recon.setEinvTaxableVal(einvTaxableValCredNote);
				gstr2Recon.setEinvIgst(einvIgstCredNote);
				gstr2Recon.setEinvCgst(einvCgstCredNote);
				gstr2Recon.setEinvSgst(einvSgstCredNote);
				gstr2Recon.setEinvCess(einvCessCredNote);
				gstr2Recon.setSrCount(srCountCredNote);
				gstr2Recon.setSrTaxableVal(srTaxableValCredNote);
				gstr2Recon.setSrIgst(srIgstCredNote);
				gstr2Recon.setSrCgst(srCgstCredNote);
				gstr2Recon.setSrSgst(srSgstCredNote);
				gstr2Recon.setSrCess(srCessCredNote);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("F");
			} else if ("Debit Notes".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setSection(sections.get(i));
				gstr2Recon.setEinvCount(einvCountDebNote);
				gstr2Recon.setEinvTaxableVal(einvTaxableValDebNote);
				gstr2Recon.setEinvIgst(einvIgstDebNote);
				gstr2Recon.setEinvCgst(einvCgstDebNote);
				gstr2Recon.setEinvSgst(einvSgstDebNote);
				gstr2Recon.setEinvCess(einvCessDebNote);
				gstr2Recon.setSrCount(srCountDebNote);
				gstr2Recon.setSrTaxableVal(srTaxableValDebNote);
				gstr2Recon.setSrIgst(srIgstDebNote);
				gstr2Recon.setSrCgst(srCgstDebNote);
				gstr2Recon.setSrSgst(srSgstDebNote);
				gstr2Recon.setSrCess(srCessDebNote);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("K");
			} else if ("Cancellations".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setSection(sections.get(i));
				gstr2Recon.setEinvCount(einvCountCancel);
				gstr2Recon.setEinvTaxableVal(einvTaxableValCancel);
				gstr2Recon.setEinvIgst(einvIgstCancel);
				gstr2Recon.setEinvCgst(einvCgstCancel);
				gstr2Recon.setEinvSgst(einvSgstCancel);
				gstr2Recon.setEinvCess(einvCessCancel);
				gstr2Recon.setSrCount(srCountCancel);
				gstr2Recon.setSrTaxableVal(srTaxableValCancel);
				gstr2Recon.setSrIgst(srIgstCancel);
				gstr2Recon.setSrCgst(srCgstCancel);
				gstr2Recon.setSrSgst(srSgstCancel);
				gstr2Recon.setSrCess(srCessCancel);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("P");
			} else if ("Deletion".equalsIgnoreCase(sections.get(i))) {
				gstr2Recon.setSection(sections.get(i));
				gstr2Recon.setEinvCount(einvCountDelete);
				gstr2Recon.setEinvTaxableVal(einvTaxableValDelete);
				gstr2Recon.setEinvIgst(einvIgstDelete);
				gstr2Recon.setEinvCgst(einvCgstDelete);
				gstr2Recon.setEinvSgst(einvSgstDelete);
				gstr2Recon.setEinvCess(einvCessDelete);
				gstr2Recon.setSrCount(srCountDelete);
				gstr2Recon.setSrTaxableVal(srTaxableValDelete);
				gstr2Recon.setSrIgst(srIgstDelete);
				gstr2Recon.setSrCgst(srCgstDelete);
				gstr2Recon.setSrSgst(srSgstDelete);
				gstr2Recon.setSrCess(srCessDelete);
				gstr2Recon.setLevel(level);
				gstr2Recon.setOrderPosition("T");
			}
			obj.add(gstr2Recon);
		}
		LOGGER.debug("Setting values for level 1 ended");

		return obj;
	}

	@Override
	public Workbook getPreReconSummReport(List<String> gstins, String taxPeriod,
			Long entityId) {
		try {
			Workbook workbook = null;
			Integer returnPeriod = GenUtil
					.getReturnPeriodFromTaxPeriod(taxPeriod);
			List<Object[]> objList = preReconSummaryDao
					.getPreReconSummary(gstins, returnPeriod);

			List<PreReconSummaryReportDto> retList = objList.stream()
					.map(o -> convertToPreReconReportDto(o))
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

	private PreReconSummaryDto addDto(PreReconSummaryDto a,
			PreReconSummaryDto b) {
		PreReconSummaryDto dto = new PreReconSummaryDto();
		dto.setSection(a.getSection());
		dto.setEinvCount(a.getEinvCount() != null && b.getEinvCount() != null
				? (a.getEinvCount().add(b.getEinvCount())) : BigInteger.ZERO);
		dto.setEinvTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getEinvTaxableVal().add(b.getEinvTaxableVal())));
		dto.setEinvIgst(GenUtil
				.defaultToZeroIfNull(a.getEinvIgst().add(b.getEinvIgst())));
		dto.setEinvCgst(GenUtil
				.defaultToZeroIfNull(a.getEinvCgst().add(b.getEinvCgst())));
		dto.setEinvSgst(GenUtil
				.defaultToZeroIfNull(a.getEinvSgst().add(b.getEinvSgst())));
		dto.setEinvCess(GenUtil
				.defaultToZeroIfNull(a.getEinvCess().add(b.getEinvCess())));
		dto.setSrCount(a.getSrCount() != null && b.getSrCount() != null
				? (a.getSrCount().add(b.getSrCount())) : BigInteger.ZERO);
		dto.setSrTaxableVal(GenUtil.defaultToZeroIfNull(
				a.getSrTaxableVal().add(b.getSrTaxableVal())));
		dto.setSrIgst(
				GenUtil.defaultToZeroIfNull(a.getSrIgst().add(b.getSrIgst())));
		dto.setSrCgst(
				GenUtil.defaultToZeroIfNull(a.getSrCgst().add(b.getSrCgst())));
		dto.setSrSgst(
				GenUtil.defaultToZeroIfNull(a.getSrSgst().add(b.getSrSgst())));
		dto.setSrCess(
				GenUtil.defaultToZeroIfNull(a.getSrCess().add(b.getSrCess())));
		return dto;
	}

	private Workbook writeToExcel(List<PreReconSummaryReportDto> retList,
			Long entityId, String taxPeriod) {

		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		try {

			if (retList != null && !retList.isEmpty()) {

				String[] invoiceHeaders = commonUtility
						.getProp("pre.recon.summary.report.header").split(",");

				String fileName = "Pre_Recon_Summary_Report_Template.xlsx";
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileName);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Gstr1EinvPreReconSummaryServiceImpl.writeToExcel "
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
					String msg = "Gstr1EinvPreReconSummaryServiceImpl.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.PRE_RECON_SUMMARY,
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

	private PreReconSummaryReportDto convertToPreReconReportDto(Object[] arr) {
		PreReconSummaryReportDto dto = new PreReconSummaryReportDto();
		try {
			String gstin = (arr[0] != null ? (String) arr[0] : "").trim();
			dto.setGstin(gstin);
			// dto.setTaxPeriod(arr[1] != null ? (String) arr[1] : "");
			String section = (arr[2] != null ? (String) arr[2] : "").trim();
			if ("INV_B2B".equalsIgnoreCase(section)) {
				dto.setSection(
						"Invoice - B2B Supplies (Other Than SEZ & Deemed Exports)");
			} else if ("INV_SEZ".equalsIgnoreCase(section)) {
				dto.setSection("Invoice - Supplies to SEZ");
			} else if ("INV_DE".equalsIgnoreCase(section)) {
				dto.setSection("Invoice - Deemed Exports");
			} else if ("INV_EXP".equalsIgnoreCase(section)) {
				dto.setSection("Invoice - Exports");
			} else if ("C_B2B".equalsIgnoreCase(section)) {
				dto.setSection(
						"Credit Notes - B2B Supplies (Other Than SEZ & Deemed Exports)");
			} else if ("C_SEZ".equalsIgnoreCase(section)) {
				dto.setSection("Credit Notes - Supplies to SEZ");
			} else if ("C_DE".equalsIgnoreCase(section)) {
				dto.setSection("Credit Notes - Deemed Exports");
			} else if ("C_EXP".equalsIgnoreCase(section)) {
				dto.setSection("Credit Notes - Exports");
			} else if ("D_B2B".equalsIgnoreCase(section)) {
				dto.setSection(
						"Debit Notes - B2B Supplies (Other Than SEZ & Deemed Exports)");
			} else if ("D_SEZ".equalsIgnoreCase(section)) {
				dto.setSection("Debit Notes - Supplies to SEZ");
			} else if ("D_DE".equalsIgnoreCase(section)) {
				dto.setSection("Debit Notes - Deemed Exports");
			} else if ("D_EXP".equalsIgnoreCase(section)) {
				dto.setSection("Debit Notes - Exports");
			} else if ("CAN_I".equalsIgnoreCase(section)) {
				dto.setSection("Invoice - Cancellation at DigiGST");
			} else if ("CAN_C".equalsIgnoreCase(section)) {
				dto.setSection("Credit Notes - Cancellation at DigiGST");
			} else if ("CAN_D".equalsIgnoreCase(section)) {
				dto.setSection("Debit Notes - Cancellation at DigiGST");
			} else if ("DEL_I".equalsIgnoreCase(section)) {
				dto.setSection(
						"Invoice - Deletion success / Failed in A/P GSTR-1");
			} else if ("DEL_C".equalsIgnoreCase(section)) {
				dto.setSection(
						"Credit Notes - Deletion success / Failed in A/P GSTR-1");
			} else if ("DEL_D".equalsIgnoreCase(section)) {
				dto.setSection(
						"Debit Notes - Deletion success / Failed in A/P GSTR-1 ");
			}
			dto.setEinvCount(
					arr[3] != null ? GenUtil.getBigInteger(arr[3]) : BigInteger.ZERO);
			dto.setEinvTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[4]));
			dto.setEinvIgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[5]));
			dto.setEinvCgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[6]));
			dto.setEinvSgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[7]));
			dto.setEinvCess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[8]));
			dto.setSrCount(
					arr[9] != null ? GenUtil.getBigInteger(arr[9]) : BigInteger.ZERO);
			dto.setSrTaxableVal(
					GenUtil.defaultToZeroIfNull((BigDecimal) arr[10]));
			dto.setSrIgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[11]));
			dto.setSrCgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[12]));
			dto.setSrSgst(GenUtil.defaultToZeroIfNull((BigDecimal) arr[13]));
			dto.setSrCess(GenUtil.defaultToZeroIfNull((BigDecimal) arr[14]));

			return dto;
		} catch (Exception ee) {
			String msg = String
					.format("Error while converting to preRecon report dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}

	}
}
