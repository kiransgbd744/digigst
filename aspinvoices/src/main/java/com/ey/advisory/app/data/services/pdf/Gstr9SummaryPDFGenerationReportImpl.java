package com.ey.advisory.app.data.services.pdf;

import static java.util.Comparator.comparing;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.javatuples.Quintet;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.services.gstr9.Gstr9InwardUtil;
import com.ey.advisory.app.data.services.gstr9.Gstr9PDFLabelUtil;
import com.ey.advisory.app.data.services.savetogstn.gstr9.Gstr9SaveToGstnDataUtil;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9PdfDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table10ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table14ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table15ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table16ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table17ItemsReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table18ItemsReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table4ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table5ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6IogReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6SuppNonRchrgReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6SuppRchrgRegReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6SuppRchrgUnRegReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table7OtherReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table7ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table8ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table9ReqDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("Gstr9SummaryPDFGenerationReportImpl")
public class Gstr9SummaryPDFGenerationReportImpl
		implements Gstr9SummaryPDFGenerationReport {

	@Autowired
	private Gstr9SaveToGstnDataUtil gstr9SaveToGstnDataUtil;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");

	private static ImmutableList<String> inwardAutoCalcList = ImmutableList
			.of("6A", "8A");

	@Autowired
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	private GstnReturnFilingStatus gstnReturnFilingStatus;

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	private Gstr9AutoCalculateRepository gstr9AutoCalculateRepository;

	@Override
	public JasperPrint generateGstr9SummaryPdfReport(String gstin, String fy, String isDigigst) {

		JasperPrint jasperPrint = null;
		String source = "jasperReports/GSTR9SummaryTemplate.jrxml";
		GetDetailsForGstr9ReqDto dto = null;
		String filingStatus = Gstr9PDFLabelUtil.DRAFT;
		String arn = null;
		String lglName = null;
		String image = null;
		String timeOnPage = getStandardTime(LocalDateTime.now());
		String filingDate = null;

		try {

			LOGGER.debug("Gstr9 pdf process intialised {}", gstin);
			String taxPeriod = Gstr9InwardUtil.getTaxperiodFromFY(fy);
			String fyGstn = fy;
			/*
			 * Get gstr9 user input data as DTO
			 */
			dto = gstr9SaveToGstnDataUtil.populateGstr9SavePayload(gstin, fy, isDigigst);

			Pair<Map<String, BigDecimal>, Map<String, BigDecimal>> autoCalcPair = getAutoCalcData(
					gstin, taxPeriod);

			if (dto == null) {
				return null;
			}

			/*
			 * For Legal name
			 */
			GSTNDetailEntity gstnDetailEntity = gSTNDetailRepository
					.findByGstinAndIsDeleteFalse(gstin);
			EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
					.findEntityByEntityId(gstnDetailEntity.getEntityId());

			/*
			 * For Filing details
			 */
			Triplet<String, String, String> filingData = getFilingDateAndStatus(
					gstin, taxPeriod, fyGstn);

			if (!Strings.isNullOrEmpty(filingData.getValue0()))
				filingStatus = filingData.getValue0();

			if (!Strings.isNullOrEmpty(filingData.getValue1())) {
				filingDate = filingData.getValue1();
				timeOnPage = filingData.getValue1();
			}
			if (!Strings.isNullOrEmpty(filingData.getValue2()))
				arn = filingData.getValue2();

			Map<String, Object> parameters = new HashMap<>();

			parameters.put("statusTimeStamp", timeOnPage);
			parameters.put("note", Gstr9PDFLabelUtil.NOTE);

			if (entityInfoEntity != null) {
				lglName = entityInfoEntity.getEntityName();
			}

			List<Gstr9PdfDTO> tableDetailsList = tableDetails(dto.getGstin(),
					fyGstn, lglName, arn, filingDate);

			Triplet<List<Gstr9PdfDTO>, Map<String, BigDecimal>, Map<String, BigDecimal>> table4Data = getTable4List(
					dto);

			Pair<List<Gstr9PdfDTO>, Map<String, BigDecimal>> table5Data = getTable5List(
					dto, table4Data.getValue1(), table4Data.getValue2());

			Quintet<Map<String, BigDecimal>, Map<String, BigDecimal>, Map<String, BigDecimal>, Map<String, BigDecimal>, Map<String, Object>> table6Data = getTable6List(
					dto, parameters, autoCalcPair.getValue0());

			List<Gstr9PdfDTO> table6BList = getTable6BList(dto);
			List<Gstr9PdfDTO> table6CList = getTable6CList(dto);
			List<Gstr9PdfDTO> table6DList = getTable6DList(dto);
			List<Gstr9PdfDTO> table6EList = getTable6EList(dto);

			List<Gstr9PdfDTO> table7List = getTable7List(dto,
					table6Data.getValue0());

			List<Gstr9PdfDTO> table8List = getTable8List(dto,
					table6Data.getValue1(), table6Data.getValue2(),
					table6Data.getValue3(), autoCalcPair.getValue1());

			Collections.sort(table8List, comparing(Gstr9PdfDTO::getSubSection));

			List<Gstr9PdfDTO> table9List = getTable9List(dto);

			List<Gstr9PdfDTO> table10List = getTable10List(dto,
					table5Data.getValue1());

			List<Gstr9PdfDTO> table14List = getTable14List(dto);

			List<Gstr9PdfDTO> table15List = getTable15List(dto);

			List<Gstr9PdfDTO> table16List = getTable16List(dto);

			List<Gstr9PdfDTO> tableHSNList = getTableHSNList(dto);
			
			parameters.put("tableDetails",
					new JRBeanCollectionDataSource(tableDetailsList));

			parameters.put("table4",
					new JRBeanCollectionDataSource(table4Data.getValue0()));

			parameters.put("table5",
					new JRBeanCollectionDataSource(table5Data.getValue0()));

			parameters.put("table6B",
					new JRBeanCollectionDataSource(table6BList));

			parameters.put("table6C",
					new JRBeanCollectionDataSource(table6CList));

			parameters.put("table6D",
					new JRBeanCollectionDataSource(table6DList));

			parameters.put("table6E",
					new JRBeanCollectionDataSource(table6EList));

			parameters.put("table7",
					new JRBeanCollectionDataSource(table7List));

			parameters.put("table8",
					new JRBeanCollectionDataSource(table8List));

			parameters.put("table9",
					new JRBeanCollectionDataSource(table9List));

			parameters.put("table10",
					new JRBeanCollectionDataSource(table10List));

			parameters.put("table14",
					new JRBeanCollectionDataSource(table14List));

			parameters.put("table15",
					new JRBeanCollectionDataSource(table15List));

			parameters.put("table16",
					new JRBeanCollectionDataSource(table16List));

			parameters.put("tableHSN",
					new JRBeanCollectionDataSource(tableHSNList));

			if (filingStatus.equalsIgnoreCase(Gstr9PDFLabelUtil.FILED)) {
				image = "FILED-WM.jpg";
			} else if (filingStatus
					.equalsIgnoreCase(Gstr9PDFLabelUtil.SUBMITTED)) {
				image = "SUBMITTED-WM.jpg";
			} else {
				image = "DRAFT-WM.jpg";
			}

			File imgFile = ResourceUtils
					.getFile("classpath:jasperReports/" + image);
			byte[] blob = Files.readAllBytes(Paths.get(imgFile.getPath()));
			ByteArrayInputStream bis = new ByteArrayInputStream(blob);
			BufferedImage bImage2 = ImageIO.read(bis);
			parameters.put("bgStatusImage", bImage2);

			File file = ResourceUtils.getFile("classpath:" + source);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());

		} catch (Exception e) {
			String msg = "Exception occured while genearting Gstr9 Summery pdf";
			e.printStackTrace();
			LOGGER.error(msg, e);
			throw new AppException(msg);

		}

		return jasperPrint;
	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		return formatter1.format(ist);

	}

	private Pair<Map<String, BigDecimal>, Map<String, BigDecimal>> getAutoCalcData(
			String gstin, String retPeriod) {
		Map<String, BigDecimal> table6AMap = new HashMap<>();
		Map<String, BigDecimal> table8AMap = new HashMap<>();
		try {
			List<Gstr9AutoCalculateEntity> entityList = gstr9AutoCalculateRepository
					.findByGstinAndRetPeriodAndSubSectionInAndIsActiveTrue(
							gstin, retPeriod, inwardAutoCalcList);

			for (Gstr9AutoCalculateEntity e : entityList) {
				if ("6A".equalsIgnoreCase(e.getSubSection())) {

					table6AMap.put(Gstr9PDFLabelUtil.IGST, e.getIamt() != null
							? e.getIamt() : BigDecimal.ZERO);
					table6AMap.put(Gstr9PDFLabelUtil.CGST, e.getCamt() != null
							? e.getCamt() : BigDecimal.ZERO);
					table6AMap.put(Gstr9PDFLabelUtil.SGST, e.getSamt() != null
							? e.getSamt() : BigDecimal.ZERO);
					table6AMap.put(Gstr9PDFLabelUtil.CESS, e.getCsamt() != null
							? e.getCsamt() : BigDecimal.ZERO);

				}
				if ("8A".equalsIgnoreCase(e.getSubSection())) {

					table8AMap.put(Gstr9PDFLabelUtil.IGST, e.getIamt() != null
							? e.getIamt() : BigDecimal.ZERO);
					table8AMap.put(Gstr9PDFLabelUtil.CGST, e.getCamt() != null
							? e.getCamt() : BigDecimal.ZERO);
					table8AMap.put(Gstr9PDFLabelUtil.SGST, e.getSamt() != null
							? e.getSamt() : BigDecimal.ZERO);
					table8AMap.put(Gstr9PDFLabelUtil.CESS, e.getCsamt() != null
							? e.getCsamt() : BigDecimal.ZERO);

				}

			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return new Pair<>(table6AMap, table8AMap);
	}

	private List<Gstr9PdfDTO> tableDetails(String gstin, String fy,
			String lglName, String arn, String dof) {

		List<Gstr9PdfDTO> list = new ArrayList<>();
		Gstr9PdfDTO dto1 = new Gstr9PdfDTO(fy, Gstr9PDFLabelUtil.FY);

		Gstr9PdfDTO dto2 = new Gstr9PdfDTO(gstin, Gstr9PDFLabelUtil.GSTIN);

		Gstr9PdfDTO dto3 = new Gstr9PdfDTO(lglName,
				Gstr9PDFLabelUtil.LEGAL_NAME);

		Gstr9PdfDTO dto4 = new Gstr9PdfDTO(arn, Gstr9PDFLabelUtil.ARN);

		Gstr9PdfDTO dto5 = new Gstr9PdfDTO(dof, Gstr9PDFLabelUtil.DOF);
		Collections.addAll(list, dto1, dto2, dto3, dto4, dto5);
		return list;
	}

	private Triplet<List<Gstr9PdfDTO>, Map<String, BigDecimal>, Map<String, BigDecimal>> getTable4List(
			GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table4List = new ArrayList<>();
		Gstr9PdfDTO table4ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_4A);
		Gstr9PdfDTO table4BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_4B);
		Gstr9PdfDTO table4CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_4C);
		Gstr9PdfDTO table4DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_4D);
		Gstr9PdfDTO table4EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_4E);
		Gstr9PdfDTO table4FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_4F);
		Gstr9PdfDTO table4GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_4G);
		Gstr9PdfDTO table4G1Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G1,
				Gstr9PDFLabelUtil.TABLE_4G1);
		Gstr9PdfDTO table4HDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.H,
				Gstr9PDFLabelUtil.TABLE_4H);
		Gstr9PdfDTO table4IDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.I,
				Gstr9PDFLabelUtil.TABLE_4I);
		Gstr9PdfDTO table4JDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.J,
				Gstr9PDFLabelUtil.TABLE_4J);
		Gstr9PdfDTO table4KDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.K,
				Gstr9PDFLabelUtil.TABLE_4K);
		Gstr9PdfDTO table4LDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.L,
				Gstr9PDFLabelUtil.TABLE_4L);
		Gstr9PdfDTO table4MDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.M,
				Gstr9PDFLabelUtil.TABLE_4M);
		Gstr9PdfDTO table4NDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.N,
				Gstr9PDFLabelUtil.TABLE_4N);

		table4CDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table4CDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table4DDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table4DDto.setSgst(Gstr9PDFLabelUtil.BLANK);

		Map<String, BigDecimal> table4GMap = new HashMap<>();
		Map<String, BigDecimal> table4NMap = new HashMap<>();

		BigDecimal subtotalHtaxableValue = BigDecimal.ZERO;
		BigDecimal subtotalHCgst = BigDecimal.ZERO;
		BigDecimal subtotalHSgst = BigDecimal.ZERO;
		BigDecimal subtotalHIgst = BigDecimal.ZERO;
		BigDecimal subtotalHCess = BigDecimal.ZERO;

		BigDecimal subtotalMtaxableValue = BigDecimal.ZERO;
		BigDecimal subtotalMCgst = BigDecimal.ZERO;
		BigDecimal subtotalMSgst = BigDecimal.ZERO;
		BigDecimal subtotalMIgst = BigDecimal.ZERO;
		BigDecimal subtotalMCess = BigDecimal.ZERO;

		BigDecimal table4GtaxableValue = BigDecimal.ZERO;
		BigDecimal table4Gcgst = BigDecimal.ZERO;
		BigDecimal table4Gsgst = BigDecimal.ZERO;
		BigDecimal table4Gigst = BigDecimal.ZERO;
		BigDecimal table4Gcess = BigDecimal.ZERO;
		
		BigDecimal table4G1taxableValue = BigDecimal.ZERO;
		BigDecimal table4G1cgst = BigDecimal.ZERO;
		BigDecimal table4G1sgst = BigDecimal.ZERO;
		BigDecimal table4G1igst = BigDecimal.ZERO;
		BigDecimal table4G1cess = BigDecimal.ZERO;

		BigDecimal subtotalHMtaxableValue = BigDecimal.ZERO;
		BigDecimal subtotalHMcgst = BigDecimal.ZERO;
		BigDecimal subtotalHMsgst = BigDecimal.ZERO;
		BigDecimal subtotalHMigst = BigDecimal.ZERO;
		BigDecimal subtotalHMcess = BigDecimal.ZERO;

		if (dto.getTable4ReqDto() != null) {

			Gstr9Table4ReqDto table4Dto = dto.getTable4ReqDto();

			if (table4Dto.getTable4B2CReqDto() != null) {
				table4ADto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4B2CReqDto().getTxval()));
				table4ADto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4B2CReqDto().getCamt()));
				table4ADto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4B2CReqDto().getSamt()));
				table4ADto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4B2CReqDto().getIamt()));
				table4ADto.setCess(convertBigDecimalToString(
						table4Dto.getTable4B2CReqDto().getCsamt()));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4B2CReqDto().getTxval());
				subtotalHCgst = addBigTwoDecimals(subtotalHCgst,
						table4Dto.getTable4B2CReqDto().getCamt());
				subtotalHSgst = addBigTwoDecimals(subtotalHSgst,
						table4Dto.getTable4B2CReqDto().getSamt());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4B2CReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4B2CReqDto().getCsamt());

			}
			table4List.add(table4ADto);

			if (table4Dto.getTable4B2BReqDto() != null) {
				table4BDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4B2BReqDto().getTxval()));
				table4BDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4B2BReqDto().getCamt()));
				table4BDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4B2BReqDto().getSamt()));
				table4BDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4B2BReqDto().getIamt()));
				table4BDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4B2BReqDto().getCsamt()));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4B2BReqDto().getTxval());
				subtotalHCgst = addBigTwoDecimals(subtotalHCgst,
						table4Dto.getTable4B2BReqDto().getCamt());
				subtotalHSgst = addBigTwoDecimals(subtotalHSgst,
						table4Dto.getTable4B2BReqDto().getSamt());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4B2BReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4B2BReqDto().getCsamt());

			}
			table4List.add(table4BDto);

			if (table4Dto.getTable4ExpReqDto() != null) {
				table4CDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4ExpReqDto().getTxval()));
				table4CDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4ExpReqDto().getIamt()));
				table4CDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4ExpReqDto().getCsamt()));
				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4ExpReqDto().getTxval());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4ExpReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4ExpReqDto().getCsamt());

			}
			table4List.add(table4CDto);

			if (table4Dto.getTable4SezReqDto() != null) {
				table4DDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4SezReqDto().getTxval()));
				table4DDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4SezReqDto().getIamt()));
				table4DDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4SezReqDto().getCsamt()));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4SezReqDto().getTxval());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4SezReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4SezReqDto().getCsamt());

			}

			table4List.add(table4DDto);
			if (table4Dto.getTable4DeemedReqDto() != null) {
				table4EDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4DeemedReqDto().getTxval()));
				table4EDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4DeemedReqDto().getCamt()));
				table4EDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4DeemedReqDto().getSamt()));
				table4EDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4DeemedReqDto().getIamt()));
				table4EDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4DeemedReqDto().getCsamt()));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4DeemedReqDto().getTxval());
				subtotalHCgst = addBigTwoDecimals(subtotalHCgst,
						table4Dto.getTable4DeemedReqDto().getCamt());
				subtotalHSgst = addBigTwoDecimals(subtotalHSgst,
						table4Dto.getTable4DeemedReqDto().getSamt());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4DeemedReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4DeemedReqDto().getCsamt());

			}

			table4List.add(table4EDto);
			if (table4Dto.getTable4AtReqDto() != null) {
				table4FDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4AtReqDto().getTxval()));
				table4FDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4AtReqDto().getCamt()));
				table4FDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4AtReqDto().getSamt()));
				table4FDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4AtReqDto().getIamt()));
				table4FDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4AtReqDto().getCsamt()));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4AtReqDto().getTxval());
				subtotalHCgst = addBigTwoDecimals(subtotalHCgst,
						table4Dto.getTable4AtReqDto().getCamt());
				subtotalHSgst = addBigTwoDecimals(subtotalHSgst,
						table4Dto.getTable4AtReqDto().getSamt());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4AtReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4AtReqDto().getCsamt());

			}
			table4List.add(table4FDto);
			if (table4Dto.getTable4RchrgReqDto() != null) {

				// required in table5N
				table4GtaxableValue = table4Dto.getTable4RchrgReqDto()
						.getTxval();
				table4Gcgst = table4Dto.getTable4RchrgReqDto().getCamt();
				table4Gsgst = table4Dto.getTable4RchrgReqDto().getSamt();
				table4Gigst = table4Dto.getTable4RchrgReqDto().getIamt();
				table4Gcess = table4Dto.getTable4RchrgReqDto().getCsamt();

				table4GDto.setTaxableValue(
						convertBigDecimalToString(table4GtaxableValue));
				table4GDto.setCgst(convertBigDecimalToString(table4Gcgst));
				table4GDto.setSgst(convertBigDecimalToString(table4Gsgst));
				table4GDto.setIgst(convertBigDecimalToString(table4Gigst));
				table4GDto.setCess(convertBigDecimalToString(table4Gcess));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4RchrgReqDto().getTxval());
				subtotalHCgst = addBigTwoDecimals(subtotalHCgst,
						table4Dto.getTable4RchrgReqDto().getCamt());
				subtotalHSgst = addBigTwoDecimals(subtotalHSgst,
						table4Dto.getTable4RchrgReqDto().getSamt());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4RchrgReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4RchrgReqDto().getCsamt());

			}
			table4List.add(table4GDto);
			
			if (table4Dto.getTable4RchrgG1ReqDto() != null) {

				// required in table5N
				table4G1taxableValue = table4Dto.getTable4RchrgG1ReqDto()
						.getTxval();
				table4G1cgst = table4Dto.getTable4RchrgG1ReqDto().getCamt();
				table4G1sgst = table4Dto.getTable4RchrgG1ReqDto().getSamt();
				table4G1igst = table4Dto.getTable4RchrgG1ReqDto().getIamt();
				table4G1cess = table4Dto.getTable4RchrgG1ReqDto().getCsamt();

				table4G1Dto.setTaxableValue(
						convertBigDecimalToString(table4G1taxableValue));
				table4G1Dto.setCgst(convertBigDecimalToString(table4G1cgst));
				table4G1Dto.setSgst(convertBigDecimalToString(table4G1sgst));
				table4G1Dto.setIgst(convertBigDecimalToString(table4G1igst));
				table4G1Dto.setCess(convertBigDecimalToString(table4G1cess));

				subtotalHtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
						table4Dto.getTable4RchrgG1ReqDto().getTxval());
				subtotalHCgst = addBigTwoDecimals(subtotalHCgst,
						table4Dto.getTable4RchrgG1ReqDto().getCamt());
				subtotalHSgst = addBigTwoDecimals(subtotalHSgst,
						table4Dto.getTable4RchrgG1ReqDto().getSamt());
				subtotalHIgst = addBigTwoDecimals(subtotalHIgst,
						table4Dto.getTable4RchrgG1ReqDto().getIamt());
				subtotalHCess = addBigTwoDecimals(subtotalHCess,
						table4Dto.getTable4RchrgG1ReqDto().getCsamt());

			}
			table4List.add(table4G1Dto);
			
			table4HDto.setTaxableValue(
					convertBigDecimalToString(subtotalHtaxableValue));
			table4HDto.setCgst(convertBigDecimalToString(subtotalHCgst));
			table4HDto.setSgst(convertBigDecimalToString(subtotalHSgst));
			table4HDto.setIgst(convertBigDecimalToString(subtotalHIgst));
			table4HDto.setCess(convertBigDecimalToString(subtotalHCess));
			table4List.add(table4HDto);

			if (table4Dto.getTable4CrntReqDto() != null) {
				table4IDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4CrntReqDto().getTxval()));
				table4IDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4CrntReqDto().getCamt()));
				table4IDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4CrntReqDto().getSamt()));
				table4IDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4CrntReqDto().getIamt()));
				table4IDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4CrntReqDto().getCsamt()));

				subtotalMtaxableValue = addBigTwoDecimals(subtotalMtaxableValue,
						table4Dto.getTable4CrntReqDto().getTxval() != null
								? table4Dto.getTable4CrntReqDto().getTxval()
										.negate()
								: table4Dto.getTable4CrntReqDto().getTxval());

				subtotalMCgst = addBigTwoDecimals(subtotalMCgst,
						table4Dto.getTable4CrntReqDto().getCamt() != null
								? table4Dto.getTable4CrntReqDto().getCamt()
										.negate()
								: table4Dto.getTable4CrntReqDto().getCamt());

				subtotalMSgst = addBigTwoDecimals(subtotalMSgst,
						table4Dto.getTable4CrntReqDto().getSamt() != null
								? table4Dto.getTable4CrntReqDto().getSamt()
										.negate()
								: table4Dto.getTable4CrntReqDto().getSamt());

				subtotalMIgst = addBigTwoDecimals(subtotalMIgst,
						table4Dto.getTable4CrntReqDto().getIamt() != null
								? table4Dto.getTable4CrntReqDto().getIamt()
										.negate()
								: table4Dto.getTable4CrntReqDto().getIamt());

				subtotalMCess = addBigTwoDecimals(subtotalMCess,
						table4Dto.getTable4CrntReqDto().getCsamt() != null
								? table4Dto.getTable4CrntReqDto().getCsamt()
										.negate()
								: table4Dto.getTable4CrntReqDto().getCsamt());

			}
			table4List.add(table4IDto);
			if (table4Dto.getTable4drntReqDto() != null) {
				table4JDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4drntReqDto().getTxval()));
				table4JDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4drntReqDto().getCamt()));
				table4JDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4drntReqDto().getSamt()));
				table4JDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4drntReqDto().getIamt()));
				table4JDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4drntReqDto().getCsamt()));

				subtotalMtaxableValue = addBigTwoDecimals(subtotalMtaxableValue,
						table4Dto.getTable4drntReqDto().getTxval());
				subtotalMCgst = addBigTwoDecimals(subtotalMCgst,
						table4Dto.getTable4drntReqDto().getCamt());
				subtotalMSgst = addBigTwoDecimals(subtotalMSgst,
						table4Dto.getTable4drntReqDto().getSamt());
				subtotalMIgst = addBigTwoDecimals(subtotalMIgst,
						table4Dto.getTable4drntReqDto().getIamt());
				subtotalMCess = addBigTwoDecimals(subtotalMCess,
						table4Dto.getTable4drntReqDto().getCsamt());

			}
			table4List.add(table4JDto);
			if (table4Dto.getTable4AmdPosReqDto() != null) {
				table4KDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4AmdPosReqDto().getTxval()));
				table4KDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4AmdPosReqDto().getCamt()));
				table4KDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4AmdPosReqDto().getSamt()));
				table4KDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4AmdPosReqDto().getIamt()));
				table4KDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4AmdPosReqDto().getCsamt()));

				subtotalMtaxableValue = addBigTwoDecimals(subtotalMtaxableValue,
						table4Dto.getTable4AmdPosReqDto().getTxval());
				subtotalMCgst = addBigTwoDecimals(subtotalMCgst,
						table4Dto.getTable4AmdPosReqDto().getCamt());
				subtotalMSgst = addBigTwoDecimals(subtotalMSgst,
						table4Dto.getTable4AmdPosReqDto().getSamt());
				subtotalMIgst = addBigTwoDecimals(subtotalMIgst,
						table4Dto.getTable4AmdPosReqDto().getIamt());
				subtotalMCess = addBigTwoDecimals(subtotalMCess,
						table4Dto.getTable4AmdPosReqDto().getCsamt());

			}
			table4List.add(table4KDto);
			if (table4Dto.getTable4AmdNegReqDto() != null) {
				table4LDto.setTaxableValue(convertBigDecimalToString(
						table4Dto.getTable4AmdNegReqDto().getTxval()));
				table4LDto.setCgst(convertBigDecimalToString(
						table4Dto.getTable4AmdNegReqDto().getCamt()));
				table4LDto.setSgst(convertBigDecimalToString(
						table4Dto.getTable4AmdNegReqDto().getSamt()));
				table4LDto.setIgst(convertBigDecimalToString(
						table4Dto.getTable4AmdNegReqDto().getIamt()));
				table4LDto.setCess(convertBigDecimalToString(
						table4Dto.getTable4AmdNegReqDto().getCsamt()));

				subtotalMtaxableValue = addBigTwoDecimals(subtotalMtaxableValue,
						table4Dto.getTable4AmdNegReqDto().getTxval() != null
								? table4Dto.getTable4AmdNegReqDto().getTxval()
										.negate()
								: table4Dto.getTable4AmdNegReqDto().getTxval());

				subtotalMCgst = addBigTwoDecimals(subtotalMCgst,
						table4Dto.getTable4AmdNegReqDto().getCamt() != null
								? table4Dto.getTable4AmdNegReqDto().getCamt()
										.negate()
								: table4Dto.getTable4AmdNegReqDto().getCamt());

				subtotalMSgst = addBigTwoDecimals(subtotalMSgst,

						table4Dto.getTable4AmdNegReqDto().getSamt() != null
								? table4Dto.getTable4AmdNegReqDto().getSamt()
										.negate()
								: table4Dto.getTable4AmdNegReqDto().getSamt());

				subtotalMIgst = addBigTwoDecimals(subtotalMIgst,
						table4Dto.getTable4AmdNegReqDto().getIamt() != null
								? table4Dto.getTable4AmdNegReqDto().getIamt()
										.negate()
								: table4Dto.getTable4AmdNegReqDto().getIamt());

				subtotalMCess = addBigTwoDecimals(subtotalMCess,
						table4Dto.getTable4AmdNegReqDto().getCsamt() != null
								? table4Dto.getTable4AmdNegReqDto().getCsamt()
										.negate()
								: table4Dto.getTable4AmdNegReqDto().getCsamt());

			}
			table4List.add(table4LDto);
			table4MDto.setTaxableValue(
					convertBigDecimalToString(subtotalMtaxableValue));
			table4MDto.setCgst(convertBigDecimalToString(subtotalMCgst));
			table4MDto.setSgst(convertBigDecimalToString(subtotalMSgst));
			table4MDto.setIgst(convertBigDecimalToString(subtotalMIgst));
			table4MDto.setCess(convertBigDecimalToString(subtotalMCess));
			table4List.add(table4MDto);

			// required in table5N
			subtotalHMtaxableValue = addBigTwoDecimals(subtotalHtaxableValue,
					subtotalMtaxableValue);

			subtotalHMcgst = addBigTwoDecimals(subtotalHCgst, subtotalMCgst);
			subtotalHMsgst = addBigTwoDecimals(subtotalHSgst, subtotalMSgst);
			subtotalHMigst = addBigTwoDecimals(subtotalHIgst, subtotalMIgst);
			subtotalHMcess = addBigTwoDecimals(subtotalHCess, subtotalMCess);

			table4NDto.setTaxableValue(
					convertBigDecimalToString(subtotalHMtaxableValue));
			table4NDto.setCgst(convertBigDecimalToString(subtotalHMcgst));
			table4NDto.setSgst(convertBigDecimalToString(subtotalHMsgst));
			table4NDto.setIgst(convertBigDecimalToString(subtotalHMigst));
			table4NDto.setCess(convertBigDecimalToString(subtotalHMcess));
			table4List.add(table4NDto);
		} else {
			Collections.addAll(table4List, table4ADto, table4BDto, table4CDto,
					table4DDto, table4EDto, table4FDto, table4GDto, table4G1Dto, 
					table4HDto, table4IDto, table4JDto, table4KDto, table4LDto, 
					table4MDto, table4NDto);

		}
		
		BigDecimal gMapTaxablevlaue = addBigTwoDecimals(table4GtaxableValue,table4G1taxableValue);
		BigDecimal gMapCgstValue = addBigTwoDecimals(table4Gcgst, table4G1cgst);
		BigDecimal gMapSgstValue = addBigTwoDecimals(table4Gsgst, table4G1sgst);
		BigDecimal gMapigstValue = addBigTwoDecimals(table4Gigst, table4G1igst);
		BigDecimal gMapCessValue = addBigTwoDecimals(table4Gcess, table4G1cess);
		table4GMap.put(Gstr9PDFLabelUtil.TAXABLE_VALUE, gMapTaxablevlaue);
		table4GMap.put(Gstr9PDFLabelUtil.CGST, gMapCgstValue);
		table4GMap.put(Gstr9PDFLabelUtil.SGST, gMapSgstValue);
		table4GMap.put(Gstr9PDFLabelUtil.IGST, gMapigstValue);
		table4GMap.put(Gstr9PDFLabelUtil.CESS, gMapCessValue);

		table4NMap.put(Gstr9PDFLabelUtil.TAXABLE_VALUE, subtotalHMtaxableValue);
		table4NMap.put(Gstr9PDFLabelUtil.CGST, subtotalHMcgst);
		table4NMap.put(Gstr9PDFLabelUtil.SGST, subtotalHMsgst);
		table4NMap.put(Gstr9PDFLabelUtil.IGST, subtotalHMigst);
		table4NMap.put(Gstr9PDFLabelUtil.CESS, subtotalHMcess);

		return new Triplet<>(table4List, table4GMap, table4NMap);
	}

	private Pair<List<Gstr9PdfDTO>, Map<String, BigDecimal>> getTable5List(
			GetDetailsForGstr9ReqDto dto, Map<String, BigDecimal> table4GMap,
			Map<String, BigDecimal> table4NMap) {

		List<Gstr9PdfDTO> table5List = new ArrayList<>();
		Gstr9PdfDTO table5ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_5A);
		Gstr9PdfDTO table5BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_5B);
		Gstr9PdfDTO table5CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_5C);
		Gstr9PdfDTO table5C1Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C51,
				Gstr9PDFLabelUtil.TABLE_5C1);
		Gstr9PdfDTO table5DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_5D);
		Gstr9PdfDTO table5EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_5E);
		Gstr9PdfDTO table5FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_5F);
		Gstr9PdfDTO table5GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_5G);
		Gstr9PdfDTO table5HDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.H,
				Gstr9PDFLabelUtil.TABLE_5H);
		Gstr9PdfDTO table5IDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.I,
				Gstr9PDFLabelUtil.TABLE_5I);
		Gstr9PdfDTO table5JDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.J,
				Gstr9PDFLabelUtil.TABLE_5J);
		Gstr9PdfDTO table5KDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.K,
				Gstr9PDFLabelUtil.TABLE_5K);
		Gstr9PdfDTO table5LDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.L,
				Gstr9PDFLabelUtil.TABLE_5L);
		Gstr9PdfDTO table5MDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.M,
				Gstr9PDFLabelUtil.TABLE_5M);
		Gstr9PdfDTO table5NDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.N,
				Gstr9PDFLabelUtil.TABLE_5N);

		table5ADto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5ADto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5ADto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5ADto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5BDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5BDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5BDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5BDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5CDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5CDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5CDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5CDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5C1Dto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5C1Dto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5C1Dto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5C1Dto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5DDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5DDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5DDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5DDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5EDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5EDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5EDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5EDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5FDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5FDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5FDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5FDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5GDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5GDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5GDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5GDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5HDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5HDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5HDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5HDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5IDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5IDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5IDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5IDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5JDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5JDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5JDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5JDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5KDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5KDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5KDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5KDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5LDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5LDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5LDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5LDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table5MDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table5MDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table5MDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table5MDto.setCess(Gstr9PDFLabelUtil.BLANK);
		Map<String, BigDecimal> table5NMap = new HashMap<>();

		BigDecimal subtotalGtaxableValue = BigDecimal.ZERO;

		BigDecimal subtotalLtaxableValue = BigDecimal.ZERO;

		BigDecimal totalTurnOverNTaxable = BigDecimal.ZERO;
		BigDecimal totalTurnOverNCgst = BigDecimal.ZERO;
		BigDecimal totalTurnOverNSgst = BigDecimal.ZERO;
		BigDecimal totalTurnOverNIgst = BigDecimal.ZERO;
		BigDecimal totalTurnOverNCess = BigDecimal.ZERO;

		if (dto.getTable5ReqDto() != null) {

			Gstr9Table5ReqDto table5Dto = dto.getTable5ReqDto();

			if (table5Dto.getTable5ZeroRtdReqDto() != null) {
				table5ADto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5ZeroRtdReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5ZeroRtdReqDto().getTxval());

			}
			table5List.add(table5ADto);

			if (table5Dto.getTable5SezReqDto() != null) {
				table5BDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5SezReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5SezReqDto().getTxval());

			}
			table5List.add(table5BDto);

			if (table5Dto.getTable5RchrgReqDto() != null) {
				table5CDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5RchrgReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5RchrgReqDto().getTxval());

			}
			table5List.add(table5CDto);
			
			if (table5Dto.getTable5C1RchrgReqDto() != null) {
				table5C1Dto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5C1RchrgReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5C1RchrgReqDto().getTxval());

			}
			table5List.add(table5C1Dto);

			if (table5Dto.getTable5ExmtReqDto() != null) {
				table5DDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5ExmtReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5ExmtReqDto().getTxval());

			}
			table5List.add(table5DDto);

			if (table5Dto.getTable5NilReqDto() != null) {
				table5EDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5NilReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5NilReqDto().getTxval());

			}
			table5List.add(table5EDto);

			if (table5Dto.getTable5NonGstReqDto() != null) {
				table5FDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5NonGstReqDto().getTxval()));

				subtotalGtaxableValue = addBigTwoDecimals(subtotalGtaxableValue,
						table5Dto.getTable5NonGstReqDto().getTxval());

			}
			table5List.add(table5FDto);

			table5GDto.setTaxableValue(
					convertBigDecimalToString(subtotalGtaxableValue));

			table5List.add(table5GDto);

			if (table5Dto.getTable5CrNtReqDto() != null) {
				table5HDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5CrNtReqDto().getTxval()));

				subtotalLtaxableValue = addBigTwoDecimals(subtotalLtaxableValue,

						table5Dto.getTable5CrNtReqDto().getTxval() != null
								? table5Dto.getTable5CrNtReqDto().getTxval()
										.negate()
								: table5Dto.getTable5CrNtReqDto().getTxval());

			}
			table5List.add(table5HDto);

			if (table5Dto.getTable5DbNtReqDto() != null) {
				table5IDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5DbNtReqDto().getTxval()));

				subtotalLtaxableValue = addBigTwoDecimals(subtotalLtaxableValue,
						table5Dto.getTable5DbNtReqDto().getTxval());

			}
			table5List.add(table5IDto);

			if (table5Dto.getTable5AmdPosReqDto() != null) {
				table5JDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5AmdPosReqDto().getTxval()));

				subtotalLtaxableValue = addBigTwoDecimals(subtotalLtaxableValue,
						table5Dto.getTable5AmdPosReqDto().getTxval());

			}
			table5List.add(table5JDto);

			if (table5Dto.getTable5AmdNegReqDto() != null) {
				table5KDto.setTaxableValue(convertBigDecimalToString(
						table5Dto.getTable5AmdNegReqDto().getTxval()));

				subtotalLtaxableValue = addBigTwoDecimals(subtotalLtaxableValue,
						table5Dto.getTable5AmdNegReqDto().getTxval() != null
								? table5Dto.getTable5AmdNegReqDto().getTxval()
										.negate()
								: table5Dto.getTable5AmdNegReqDto().getTxval());

			}
			table5List.add(table5KDto);

			table5LDto.setTaxableValue(
					convertBigDecimalToString(subtotalLtaxableValue));

			table5List.add(table5LDto);

			BigDecimal turnOverM = addBigTwoDecimals(subtotalGtaxableValue,
					subtotalLtaxableValue);

			table5MDto.setTaxableValue(convertBigDecimalToString(turnOverM));
			table5List.add(table5MDto);
			/*
			 * 4N + 5M - 4G - 4G1
			 */
			totalTurnOverNTaxable = addAndsubtractBigDecimals(
					table4NMap.get(Gstr9PDFLabelUtil.TAXABLE_VALUE), turnOverM,
					table4GMap.get(Gstr9PDFLabelUtil.TAXABLE_VALUE));

			totalTurnOverNCgst = addAndsubtractBigDecimals(
					table4NMap.get(Gstr9PDFLabelUtil.CGST), BigDecimal.ZERO,
					table4GMap.get(Gstr9PDFLabelUtil.CGST));

			totalTurnOverNSgst = addAndsubtractBigDecimals(
					table4NMap.get(Gstr9PDFLabelUtil.SGST), BigDecimal.ZERO,
					table4GMap.get(Gstr9PDFLabelUtil.SGST));

			totalTurnOverNIgst = addAndsubtractBigDecimals(
					table4NMap.get(Gstr9PDFLabelUtil.IGST), BigDecimal.ZERO,
					table4GMap.get(Gstr9PDFLabelUtil.IGST));
			totalTurnOverNCess = addAndsubtractBigDecimals(
					table4NMap.get(Gstr9PDFLabelUtil.CESS), BigDecimal.ZERO,
					table4GMap.get(Gstr9PDFLabelUtil.CESS));

		} else {
			Collections.addAll(table5List, table5ADto, table5BDto, table5CDto,
					table5C1Dto, table5DDto, table5EDto, table5FDto, table5GDto, 
					table5HDto, table5IDto, table5JDto, table5KDto, table5LDto, 
					table5MDto);
		}
		table5NMap.put(Gstr9PDFLabelUtil.TAXABLE_VALUE, totalTurnOverNTaxable);
		table5NMap.put(Gstr9PDFLabelUtil.CGST, totalTurnOverNCgst);
		table5NMap.put(Gstr9PDFLabelUtil.SGST, totalTurnOverNSgst);
		table5NMap.put(Gstr9PDFLabelUtil.IGST, totalTurnOverNIgst);
		table5NMap.put(Gstr9PDFLabelUtil.CESS, totalTurnOverNCess);

		table5NDto.setTaxableValue(
				convertBigDecimalToString(totalTurnOverNTaxable));
		table5NDto.setIgst(convertBigDecimalToString(totalTurnOverNIgst));
		table5NDto.setCgst(convertBigDecimalToString(totalTurnOverNCgst));
		table5NDto.setSgst(convertBigDecimalToString(totalTurnOverNSgst));
		table5NDto.setCess(convertBigDecimalToString(totalTurnOverNCess));
		table5List.add(table5NDto);

		return new Pair<>(table5List, table5NMap);
	}

	private List<Gstr9PdfDTO> getTable6BList(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table6BList = new ArrayList<>();
		Gstr9PdfDTO table6B1Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B1,
				Gstr9PDFLabelUtil.IP);
		Gstr9PdfDTO table6B2Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B2,
				Gstr9PDFLabelUtil.CG);
		Gstr9PdfDTO table6B3Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B3,
				Gstr9PDFLabelUtil.IS);

		Collections.addAll(table6BList, table6B1Dto, table6B2Dto, table6B3Dto);
		if (dto.getTable6ReqDto() != null) {
			if (dto.getTable6ReqDto().getTable6SuppNonRchrgReqDto() != null) {

				Gstr9Table6ReqDto table6Dto = dto.getTable6ReqDto();
				for (Gstr9Table6SuppNonRchrgReqDto e : table6Dto
						.getTable6SuppNonRchrgReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {

						table6B1Dto = table6BList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IP))
								.findAny().orElse(null);

						table6B1Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6B1Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6B1Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6B1Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						table6B2Dto = table6BList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.CG))
								.findAny().orElse(null);

						table6B2Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6B2Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6B2Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6B2Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}

					if (Gstr9PDFLabelUtil.INPUT_SERVICES
							.equalsIgnoreCase(e.getItctyp())) {

						table6B3Dto = table6BList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IS))
								.findAny().orElse(null);

						table6B3Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6B3Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6B3Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6B3Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}
				}

			}
		}
		return table6BList;

	}

	private List<Gstr9PdfDTO> getTable6CList(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table6CList = new ArrayList<>();

		Gstr9PdfDTO table6C1Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C1,
				Gstr9PDFLabelUtil.IP);
		Gstr9PdfDTO table6C2Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C2,
				Gstr9PDFLabelUtil.CG);
		Gstr9PdfDTO table6C3Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C3,
				Gstr9PDFLabelUtil.IS);
		Collections.addAll(table6CList, table6C1Dto, table6C2Dto, table6C3Dto);

		if (dto.getTable6ReqDto() != null) {
			Gstr9Table6ReqDto table6Dto = dto.getTable6ReqDto();

			if (table6Dto.getTable6SuppRchrgUnRegReqDto() != null) {

				for (Gstr9Table6SuppRchrgUnRegReqDto e : table6Dto
						.getTable6SuppRchrgUnRegReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {
						table6C1Dto = table6CList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IP))
								.findAny().orElse(null);

						table6C1Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6C1Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6C1Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6C1Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {
						table6C2Dto = table6CList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.CG))
								.findAny().orElse(null);

						table6C2Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6C2Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6C2Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6C2Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}

					if (Gstr9PDFLabelUtil.INPUT_SERVICES
							.equalsIgnoreCase(e.getItctyp())) {
						table6C3Dto = table6CList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IS))
								.findAny().orElse(null);

						table6C3Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6C3Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6C3Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6C3Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}
				}

			}

		}
		return table6CList;

	}

	private List<Gstr9PdfDTO> getTable6DList(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table6DList = new ArrayList<>();

		Gstr9PdfDTO table6D1Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D1,
				Gstr9PDFLabelUtil.IP);
		Gstr9PdfDTO table6D2Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D2,
				Gstr9PDFLabelUtil.CG);
		Gstr9PdfDTO table6D3Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D3,
				Gstr9PDFLabelUtil.IS);
		Collections.addAll(table6DList, table6D1Dto, table6D2Dto, table6D3Dto);

		if (dto.getTable6ReqDto() != null) {
			Gstr9Table6ReqDto table6Dto = dto.getTable6ReqDto();

			if (table6Dto.getTable6SuppRchrgRegReqDto() != null) {

				for (Gstr9Table6SuppRchrgRegReqDto e : table6Dto
						.getTable6SuppRchrgRegReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {
						table6D1Dto = table6DList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IP))
								.findAny().orElse(null);

						table6D1Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6D1Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6D1Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6D1Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						table6D2Dto = table6DList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.CG))
								.findAny().orElse(null);

						table6D2Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6D2Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6D2Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6D2Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}

					if (Gstr9PDFLabelUtil.INPUT_SERVICES
							.equalsIgnoreCase(e.getItctyp())) {

						table6D3Dto = table6DList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IS))
								.findAny().orElse(null);

						table6D3Dto.setCgst(
								convertBigDecimalToString(e.getCamt()));
						table6D3Dto.setSgst(
								convertBigDecimalToString(e.getSamt()));
						table6D3Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6D3Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));
					}
				}

			}
		}
		return table6DList;

	}

	private List<Gstr9PdfDTO> getTable6EList(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table6EList = new ArrayList<>();

		Gstr9PdfDTO table6E1Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E1,
				Gstr9PDFLabelUtil.IP);
		Gstr9PdfDTO table6E2Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E2,
				Gstr9PDFLabelUtil.CG);
		table6E1Dto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table6E1Dto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table6E2Dto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table6E2Dto.setSgst(Gstr9PDFLabelUtil.BLANK);

		Collections.addAll(table6EList, table6E1Dto, table6E2Dto);

		if (dto.getTable6ReqDto() != null) {
			Gstr9Table6ReqDto table6Dto = dto.getTable6ReqDto();

			if (table6Dto.getTable6IogReqDto() != null) {

				for (Gstr9Table6IogReqDto e : table6Dto.getTable6IogReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {

						table6E1Dto = table6EList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.IP))
								.findAny().orElse(null);

						table6E1Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6E1Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));

					}
					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						table6E2Dto = table6EList.stream().filter(
								p -> p.getLabel().equals(Gstr9PDFLabelUtil.CG))
								.findAny().orElse(null);

						table6E2Dto.setIgst(
								convertBigDecimalToString(e.getIamt()));
						table6E2Dto.setCess(
								convertBigDecimalToString(e.getCsamt()));

					}
				}

			}
		}
		return table6EList;

	}

	private Quintet<Map<String, BigDecimal>, Map<String, BigDecimal>, Map<String, BigDecimal>, Map<String, BigDecimal>, Map<String, Object>> getTable6List(
			GetDetailsForGstr9ReqDto dto, Map<String, Object> parameters,
			Map<String, BigDecimal> table6AMap) {

		List<Gstr9PdfDTO> table6List = new ArrayList<>();

		Gstr9PdfDTO table6FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_6F);
		Gstr9PdfDTO table6GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_6G);
		Gstr9PdfDTO table6HDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.H,
				Gstr9PDFLabelUtil.TABLE_6H);
		Gstr9PdfDTO table6IDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.I,
				Gstr9PDFLabelUtil.TABLE_6I);
		Gstr9PdfDTO table6JDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.J,
				Gstr9PDFLabelUtil.TABLE_6J);
		Gstr9PdfDTO table6KDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.K,
				Gstr9PDFLabelUtil.TABLE_6K);
		Gstr9PdfDTO table6LDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.L,
				Gstr9PDFLabelUtil.TABLE_6L);
		Gstr9PdfDTO table6MDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.M,
				Gstr9PDFLabelUtil.TABLE_6M);
		Gstr9PdfDTO table6NDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.N,
				Gstr9PDFLabelUtil.TABLE_6N);
		Gstr9PdfDTO table6ODto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.O,
				Gstr9PDFLabelUtil.TABLE_6O);
		table6FDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table6FDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table6KDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table6KDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table6LDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table6LDto.setCess(Gstr9PDFLabelUtil.BLANK);

		Map<String, BigDecimal> table6OMap = new HashMap<>();
		Map<String, BigDecimal> table6BMap = new HashMap<>();
		Map<String, BigDecimal> table6EMap = new HashMap<>();
		Map<String, BigDecimal> table6HMap = new HashMap<>();

		parameters.put("lable6A", Gstr9PDFLabelUtil.TABLE_6A);

		BigDecimal cgst6A = table6AMap.get(Gstr9PDFLabelUtil.CGST) != null
				? table6AMap.get(Gstr9PDFLabelUtil.CGST) : BigDecimal.ZERO;
		BigDecimal sgst6A = table6AMap.get(Gstr9PDFLabelUtil.SGST) != null
				? table6AMap.get(Gstr9PDFLabelUtil.SGST) : BigDecimal.ZERO;
		BigDecimal igst6A = table6AMap.get(Gstr9PDFLabelUtil.IGST) != null
				? table6AMap.get(Gstr9PDFLabelUtil.IGST) : BigDecimal.ZERO;
		BigDecimal cess6A = table6AMap.get(Gstr9PDFLabelUtil.CESS) != null
				? table6AMap.get(Gstr9PDFLabelUtil.CESS) : BigDecimal.ZERO;

		BigDecimal cgst6I = BigDecimal.ZERO;
		BigDecimal sgst6I = BigDecimal.ZERO;
		BigDecimal igst6I = BigDecimal.ZERO;
		BigDecimal cess6I = BigDecimal.ZERO;

		BigDecimal cgst6N = BigDecimal.ZERO;
		BigDecimal sgst6N = BigDecimal.ZERO;
		BigDecimal igst6N = BigDecimal.ZERO;
		BigDecimal cess6N = BigDecimal.ZERO;

		BigDecimal cgst6O = BigDecimal.ZERO;
		BigDecimal sgst6O = BigDecimal.ZERO;
		BigDecimal igst6O = BigDecimal.ZERO;
		BigDecimal cess6O = BigDecimal.ZERO;

		BigDecimal cgst6B = BigDecimal.ZERO;
		BigDecimal sgst6B = BigDecimal.ZERO;
		BigDecimal igst6B = BigDecimal.ZERO;
		BigDecimal cess6B = BigDecimal.ZERO;

		BigDecimal cgst6H = BigDecimal.ZERO;
		BigDecimal sgst6H = BigDecimal.ZERO;
		BigDecimal igst6H = BigDecimal.ZERO;
		BigDecimal cess6H = BigDecimal.ZERO;

		BigDecimal cgst6E = BigDecimal.ZERO;
		BigDecimal sgst6E = BigDecimal.ZERO;
		BigDecimal igst6E = BigDecimal.ZERO;
		BigDecimal cess6E = BigDecimal.ZERO;

		parameters.put("cgst6A", convertBigDecimalToString(cgst6A));

		parameters.put("sgst6A", convertBigDecimalToString(sgst6A));

		parameters.put("igst6A", convertBigDecimalToString(igst6A));

		parameters.put("cess6A", convertBigDecimalToString(cess6A));

		if (dto.getTable6ReqDto() != null) {

			Gstr9Table6ReqDto table6Dto = dto.getTable6ReqDto();

			if (table6Dto.getTable6SuppNonRchrgReqDto() != null) {

				for (Gstr9Table6SuppNonRchrgReqDto e : table6Dto
						.getTable6SuppNonRchrgReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6B = addBigTwoDecimals(cgst6B, e.getCamt());
						sgst6B = addBigTwoDecimals(sgst6B, e.getSamt());
						igst6B = addBigTwoDecimals(igst6B, e.getIamt());
						cess6B = addBigTwoDecimals(cess6B, e.getCsamt());

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6B = addBigTwoDecimals(cgst6B, e.getCamt());
						sgst6B = addBigTwoDecimals(sgst6B, e.getSamt());
						igst6B = addBigTwoDecimals(igst6B, e.getIamt());
						cess6B = addBigTwoDecimals(cess6B, e.getCsamt());

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.INPUT_SERVICES
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6B = addBigTwoDecimals(cgst6B, e.getCamt());
						sgst6B = addBigTwoDecimals(sgst6B, e.getSamt());
						igst6B = addBigTwoDecimals(igst6B, e.getIamt());
						cess6B = addBigTwoDecimals(cess6B, e.getCsamt());

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

				}

			}

			if (table6Dto.getTable6SuppRchrgUnRegReqDto() != null) {

				for (Gstr9Table6SuppRchrgUnRegReqDto e : table6Dto
						.getTable6SuppRchrgUnRegReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.INPUT_SERVICES
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

				}

			}

			if (table6Dto.getTable6SuppRchrgRegReqDto() != null) {

				for (Gstr9Table6SuppRchrgRegReqDto e : table6Dto
						.getTable6SuppRchrgRegReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.INPUT_SERVICES
							.equalsIgnoreCase(e.getItctyp())) {

						cgst6I = addBigTwoDecimals(cgst6I, e.getCamt());
						sgst6I = addBigTwoDecimals(sgst6I, e.getSamt());
						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

				}

			}

			if (table6Dto.getTable6IogReqDto() != null) {

				for (Gstr9Table6IogReqDto e : table6Dto.getTable6IogReqDto()) {

					if (Gstr9PDFLabelUtil.INPUTS
							.equalsIgnoreCase(e.getItctyp())) {

						igst6E = addBigTwoDecimals(igst6E, e.getIamt());
						cess6E = addBigTwoDecimals(cess6E, e.getIamt());

						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

					if (Gstr9PDFLabelUtil.CAPITAL_GOODS
							.equalsIgnoreCase(e.getItctyp())) {

						igst6E = addBigTwoDecimals(igst6E, e.getIamt());
						cess6E = addBigTwoDecimals(cess6E, e.getIamt());

						igst6I = addBigTwoDecimals(igst6I, e.getIamt());
						cess6I = addBigTwoDecimals(cess6I, e.getCsamt());

					}

				}

			}

			if (table6Dto.getTable6IosReqDto() != null) {

				table6FDto.setIgst(convertBigDecimalToString(
						table6Dto.getTable6IosReqDto().getIamt()));
				table6FDto.setCess(convertBigDecimalToString(
						table6Dto.getTable6IosReqDto().getCsamt()));

				igst6I = addBigTwoDecimals(igst6I,
						table6Dto.getTable6IosReqDto().getIamt());
				cess6I = addBigTwoDecimals(cess6I,
						table6Dto.getTable6IosReqDto().getCsamt());

			}
			table6List.add(table6FDto);
			if (table6Dto.getTable6IsdReqDto() != null) {

				table6GDto.setCgst(convertBigDecimalToString(
						table6Dto.getTable6IsdReqDto().getCamt()));
				table6GDto.setSgst(convertBigDecimalToString(
						table6Dto.getTable6IsdReqDto().getSamt()));
				table6GDto.setIgst(convertBigDecimalToString(
						table6Dto.getTable6IsdReqDto().getIamt()));
				table6GDto.setCess(convertBigDecimalToString(
						table6Dto.getTable6IsdReqDto().getCsamt()));

				cgst6I = addBigTwoDecimals(cgst6I,
						table6Dto.getTable6IsdReqDto().getCamt());
				sgst6I = addBigTwoDecimals(sgst6I,
						table6Dto.getTable6IsdReqDto().getSamt());

				igst6I = addBigTwoDecimals(igst6I,
						table6Dto.getTable6IsdReqDto().getIamt());
				cess6I = addBigTwoDecimals(cess6I,
						table6Dto.getTable6IsdReqDto().getCsamt());

			}
			table6List.add(table6GDto);

			if (table6Dto.getTable6ItcClmdReqDto() != null) {

				cgst6H = table6Dto.getTable6ItcClmdReqDto().getCamt();
				sgst6H = table6Dto.getTable6ItcClmdReqDto().getSamt();
				igst6H = table6Dto.getTable6ItcClmdReqDto().getIamt();
				cess6H = table6Dto.getTable6ItcClmdReqDto().getCsamt();

				table6HDto.setCgst(convertBigDecimalToString(cgst6H));
				table6HDto.setSgst(convertBigDecimalToString(sgst6H));
				table6HDto.setIgst(convertBigDecimalToString(igst6H));
				table6HDto.setCess(convertBigDecimalToString(cess6H));

				cgst6I = addBigTwoDecimals(cgst6I,
						table6Dto.getTable6ItcClmdReqDto().getCamt());
				sgst6I = addBigTwoDecimals(sgst6I,
						table6Dto.getTable6ItcClmdReqDto().getSamt());

				igst6I = addBigTwoDecimals(igst6I,
						table6Dto.getTable6ItcClmdReqDto().getIamt());
				cess6I = addBigTwoDecimals(cess6I,
						table6Dto.getTable6ItcClmdReqDto().getCsamt());

			}
			table6List.add(table6HDto);

			/*
			 * Sub-total (B to H)
			 */
			table6IDto.setCgst(convertBigDecimalToString(cgst6I));
			table6IDto.setSgst(convertBigDecimalToString(sgst6I));
			table6IDto.setIgst(convertBigDecimalToString(igst6I));
			table6IDto.setCess(convertBigDecimalToString(cess6I));
			table6List.add(table6IDto);

			/*
			 * Difference (I - A)
			 */
			table6JDto.setCgst(convertBigDecimalToString(
					subtractTwoBigDecimals(cgst6I, cgst6A)));
			table6JDto.setSgst(convertBigDecimalToString(
					subtractTwoBigDecimals(sgst6I, sgst6A)));
			table6JDto.setIgst(convertBigDecimalToString(
					subtractTwoBigDecimals(igst6I, igst6A)));
			table6JDto.setCess(convertBigDecimalToString(
					subtractTwoBigDecimals(cess6I, cess6A)));
			table6List.add(table6JDto);

			if (table6Dto.getTable6Trans1ReqDto() != null) {

				table6KDto.setCgst(convertBigDecimalToString(
						table6Dto.getTable6Trans1ReqDto().getCamt()));
				table6KDto.setSgst(convertBigDecimalToString(
						table6Dto.getTable6Trans1ReqDto().getSamt()));

				cgst6N = addBigTwoDecimals(cgst6N,
						table6Dto.getTable6Trans1ReqDto().getCamt());
				sgst6N = addBigTwoDecimals(sgst6N,
						table6Dto.getTable6Trans1ReqDto().getSamt());

			}
			table6List.add(table6KDto);

			if (table6Dto.getTable6Trans2ReqDto() != null) {

				table6LDto.setCgst(convertBigDecimalToString(
						table6Dto.getTable6Trans2ReqDto().getCamt()));
				table6LDto.setSgst(convertBigDecimalToString(
						table6Dto.getTable6Trans2ReqDto().getSamt()));

				cgst6N = addBigTwoDecimals(cgst6N,
						table6Dto.getTable6Trans2ReqDto().getCamt());
				sgst6N = addBigTwoDecimals(sgst6N,
						table6Dto.getTable6Trans2ReqDto().getSamt());

			}
			table6List.add(table6LDto);

			if (table6Dto.getTable6OtherReqDto() != null) {

				table6MDto.setCgst(convertBigDecimalToString(
						table6Dto.getTable6OtherReqDto().getCamt()));
				table6MDto.setSgst(convertBigDecimalToString(
						table6Dto.getTable6OtherReqDto().getSamt()));
				table6MDto.setIgst(convertBigDecimalToString(
						table6Dto.getTable6OtherReqDto().getIamt()));
				table6MDto.setCess(convertBigDecimalToString(
						table6Dto.getTable6OtherReqDto().getCsamt()));

				cgst6N = addBigTwoDecimals(cgst6N,
						table6Dto.getTable6OtherReqDto().getCamt());
				sgst6N = addBigTwoDecimals(sgst6N,
						table6Dto.getTable6OtherReqDto().getSamt());

				igst6N = addBigTwoDecimals(igst6N,
						table6Dto.getTable6OtherReqDto().getIamt());
				cess6N = addBigTwoDecimals(cess6N,
						table6Dto.getTable6OtherReqDto().getCsamt());

			}
			table6List.add(table6MDto);

			/*
			 * Sub-total (K to Mabove)
			 */
			table6NDto.setCgst(convertBigDecimalToString(cgst6N));
			table6NDto.setSgst(convertBigDecimalToString(sgst6N));
			table6NDto.setIgst(convertBigDecimalToString(igst6N));
			table6NDto.setCess(convertBigDecimalToString(cess6N));
			table6List.add(table6NDto);

			cgst6O = addBigTwoDecimals(cgst6I, cgst6N);
			sgst6O = addBigTwoDecimals(sgst6I, sgst6N);
			igst6O = addBigTwoDecimals(igst6I, igst6N);
			cess6O = addBigTwoDecimals(cess6I, cess6N);

			table6ODto.setCgst(convertBigDecimalToString(
					addBigTwoDecimals(cgst6I, cgst6N)));
			table6ODto.setSgst(convertBigDecimalToString(
					addBigTwoDecimals(sgst6I, sgst6N)));
			table6ODto.setIgst(convertBigDecimalToString(
					addBigTwoDecimals(igst6I, igst6N)));
			table6ODto.setCess(convertBigDecimalToString(
					addBigTwoDecimals(cess6I, cess6N)));
			table6List.add(table6ODto);

		} else {
			Collections.addAll(table6List, table6FDto, table6GDto, table6HDto,
					table6IDto, table6JDto, table6KDto, table6LDto, table6MDto,
					table6NDto, table6ODto);
		}
		/*
		 * 
		 * Required for 7J
		 */
		table6OMap.put(Gstr9PDFLabelUtil.CGST, cgst6O);
		table6OMap.put(Gstr9PDFLabelUtil.SGST, sgst6O);
		table6OMap.put(Gstr9PDFLabelUtil.IGST, igst6O);
		table6OMap.put(Gstr9PDFLabelUtil.CESS, cess6O);

		/*
		 * Required for 8B
		 */
		table6BMap.put(Gstr9PDFLabelUtil.CGST, cgst6B);
		table6BMap.put(Gstr9PDFLabelUtil.SGST, sgst6B);
		table6BMap.put(Gstr9PDFLabelUtil.IGST, igst6B);
		table6BMap.put(Gstr9PDFLabelUtil.CESS, cess6B);

		table6HMap.put(Gstr9PDFLabelUtil.CGST, cgst6H);
		table6HMap.put(Gstr9PDFLabelUtil.SGST, sgst6H);
		table6HMap.put(Gstr9PDFLabelUtil.IGST, igst6H);
		table6HMap.put(Gstr9PDFLabelUtil.CESS, cess6H);
		/*
		 * Required for 8H
		 */

		table6EMap.put(Gstr9PDFLabelUtil.CGST, cgst6E);
		table6EMap.put(Gstr9PDFLabelUtil.SGST, sgst6E);
		table6EMap.put(Gstr9PDFLabelUtil.IGST, igst6E);
		table6EMap.put(Gstr9PDFLabelUtil.CESS, cess6E);

		parameters.put("table6", new JRBeanCollectionDataSource(table6List));

		return new Quintet<>(table6OMap, table6BMap, table6EMap, table6HMap,
				parameters);

	}

	private List<Gstr9PdfDTO> getTable7List(GetDetailsForGstr9ReqDto dto,
			Map<String, BigDecimal> table6OMap) {

		List<Gstr9PdfDTO> table7List = new ArrayList<>();
		Gstr9PdfDTO table7ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_7A);
		Gstr9PdfDTO table7BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_7B);
		Gstr9PdfDTO table7CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_7C);
		Gstr9PdfDTO table7DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_7D);
		Gstr9PdfDTO table7EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_7E);
		Gstr9PdfDTO table7FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_7F);
		Gstr9PdfDTO table7GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_7G);
		Gstr9PdfDTO table7HDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.H,
				Gstr9PDFLabelUtil.TABLE_7H);
		Gstr9PdfDTO table7IDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.I,
				Gstr9PDFLabelUtil.TABLE_7I);
		Gstr9PdfDTO table7JDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.J,
				Gstr9PDFLabelUtil.TABLE_7J);

		table7FDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table7FDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table7GDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table7GDto.setCess(Gstr9PDFLabelUtil.BLANK);

		BigDecimal cgstI = BigDecimal.ZERO;
		BigDecimal sgstI = BigDecimal.ZERO;
		BigDecimal igstI = BigDecimal.ZERO;
		BigDecimal cessI = BigDecimal.ZERO;

		if (dto.getTable7ReqDto() != null) {
			Gstr9Table7ReqDto table7Dto = dto.getTable7ReqDto();

			if (table7Dto.getGstr9Table7Rule37ReqDto() != null) {

				table7ADto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule37ReqDto().getCamt()));

				table7ADto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule37ReqDto().getSamt()));

				table7ADto.setIgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule37ReqDto().getIamt()));

				table7ADto.setCess(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule37ReqDto().getCsamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7Rule37ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7Rule37ReqDto().getSamt());
				igstI = addBigTwoDecimals(igstI,
						table7Dto.getGstr9Table7Rule37ReqDto().getIamt());
				cessI = addBigTwoDecimals(cessI,
						table7Dto.getGstr9Table7Rule37ReqDto().getCsamt());

			}
			table7List.add(table7ADto);

			if (table7Dto.getGstr9Table7Rule39ReqDto() != null) {

				table7BDto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule39ReqDto().getCamt()));

				table7BDto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule39ReqDto().getSamt()));

				table7BDto.setIgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule39ReqDto().getIamt()));

				table7BDto.setCess(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule39ReqDto().getCsamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7Rule39ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7Rule39ReqDto().getSamt());
				igstI = addBigTwoDecimals(igstI,
						table7Dto.getGstr9Table7Rule39ReqDto().getIamt());
				cessI = addBigTwoDecimals(cessI,
						table7Dto.getGstr9Table7Rule39ReqDto().getCsamt());

			}
			table7List.add(table7BDto);

			if (table7Dto.getGstr9Table7Rule42ReqDto() != null) {

				table7CDto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule42ReqDto().getCamt()));

				table7CDto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule42ReqDto().getSamt()));

				table7CDto.setIgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule42ReqDto().getIamt()));

				table7CDto.setCess(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule42ReqDto().getCsamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7Rule42ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7Rule42ReqDto().getSamt());
				igstI = addBigTwoDecimals(igstI,
						table7Dto.getGstr9Table7Rule42ReqDto().getIamt());
				cessI = addBigTwoDecimals(cessI,
						table7Dto.getGstr9Table7Rule42ReqDto().getCsamt());

			}
			table7List.add(table7CDto);

			if (table7Dto.getGstr9Table7Rule43ReqDto() != null) {

				table7DDto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule43ReqDto().getCamt()));

				table7DDto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule43ReqDto().getSamt()));

				table7DDto.setIgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule43ReqDto().getIamt()));

				table7DDto.setCess(convertBigDecimalToString(
						table7Dto.getGstr9Table7Rule43ReqDto().getCsamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7Rule43ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7Rule43ReqDto().getSamt());
				igstI = addBigTwoDecimals(igstI,
						table7Dto.getGstr9Table7Rule43ReqDto().getIamt());
				cessI = addBigTwoDecimals(cessI,
						table7Dto.getGstr9Table7Rule43ReqDto().getCsamt());

			}
			table7List.add(table7DDto);

			if (table7Dto.getGstr9Table7Sec17ReqDto() != null) {

				table7EDto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Sec17ReqDto().getCamt()));

				table7EDto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Sec17ReqDto().getSamt()));

				table7EDto.setIgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7Sec17ReqDto().getIamt()));

				table7EDto.setCess(convertBigDecimalToString(
						table7Dto.getGstr9Table7Sec17ReqDto().getCsamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7Sec17ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7Sec17ReqDto().getSamt());
				igstI = addBigTwoDecimals(igstI,
						table7Dto.getGstr9Table7Sec17ReqDto().getIamt());
				cessI = addBigTwoDecimals(cessI,
						table7Dto.getGstr9Table7Sec17ReqDto().getCsamt());

			}
			table7List.add(table7EDto);

			if (table7Dto.getGstr9Table7RevsTrans1ReqDto() != null) {

				table7FDto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7RevsTrans1ReqDto().getCamt()));

				table7FDto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7RevsTrans1ReqDto().getSamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7RevsTrans1ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7RevsTrans1ReqDto().getSamt());

			}
			table7List.add(table7FDto);

			if (table7Dto.getGstr9Table7RevsTrans2ReqDto() != null) {

				table7GDto.setCgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7RevsTrans2ReqDto().getCamt()));

				table7GDto.setSgst(convertBigDecimalToString(
						table7Dto.getGstr9Table7RevsTrans2ReqDto().getSamt()));

				cgstI = addBigTwoDecimals(cgstI,
						table7Dto.getGstr9Table7RevsTrans2ReqDto().getCamt());
				sgstI = addBigTwoDecimals(sgstI,
						table7Dto.getGstr9Table7RevsTrans2ReqDto().getSamt());

			}
			table7List.add(table7GDto);

			if (table7Dto.getGstr9Table7OtherReqDto() != null) {
				BigDecimal cgst7H = BigDecimal.ZERO;
				BigDecimal sgst7H = BigDecimal.ZERO;
				BigDecimal igst7H = BigDecimal.ZERO;
				BigDecimal cess7H = BigDecimal.ZERO;

				for (Gstr9Table7OtherReqDto e : table7Dto
						.getGstr9Table7OtherReqDto()) {

					cgst7H = addBigTwoDecimals(cgst7H, e.getCamt());
					sgst7H = addBigTwoDecimals(sgst7H, e.getSamt());
					igst7H = addBigTwoDecimals(igst7H, e.getIamt());
					cess7H = addBigTwoDecimals(cess7H, e.getCsamt());
				}

				table7HDto.setCgst(convertBigDecimalToString(cgst7H));
				table7HDto.setSgst(convertBigDecimalToString(sgst7H));
				table7HDto.setIgst(convertBigDecimalToString(igst7H));
				table7HDto.setCess(convertBigDecimalToString(cess7H));

				cgstI = addBigTwoDecimals(cgstI, cgst7H);
				sgstI = addBigTwoDecimals(sgstI, sgst7H);
				igstI = addBigTwoDecimals(igstI, igst7H);
				cessI = addBigTwoDecimals(cessI, cess7H);

			}
			table7List.add(table7HDto);

			/*
			 * Sum of A to H
			 */
			table7IDto.setCgst(convertBigDecimalToString(cgstI));
			table7IDto.setSgst(convertBigDecimalToString(sgstI));
			table7IDto.setIgst(convertBigDecimalToString(igstI));
			table7IDto.setCess(convertBigDecimalToString(cessI));
			table7List.add(table7IDto);

		} else {
			Collections.addAll(table7List, table7ADto, table7BDto, table7CDto,
					table7DDto, table7EDto, table7FDto, table7GDto, table7HDto,
					table7IDto);
		}
		/*
		 * 6O - 7I
		 */
		table7JDto.setCgst(convertBigDecimalToString(subtractTwoBigDecimals(
				table6OMap.get(Gstr9PDFLabelUtil.CGST), cgstI)));
		table7JDto.setSgst(convertBigDecimalToString(subtractTwoBigDecimals(
				table6OMap.get(Gstr9PDFLabelUtil.SGST), sgstI)));
		table7JDto.setIgst(convertBigDecimalToString(subtractTwoBigDecimals(
				table6OMap.get(Gstr9PDFLabelUtil.IGST), igstI)));
		table7JDto.setCess(convertBigDecimalToString(subtractTwoBigDecimals(
				table6OMap.get(Gstr9PDFLabelUtil.CESS), cessI)));
		table7List.add(table7JDto);

		return table7List;
	}

	private List<Gstr9PdfDTO> getTable8List(GetDetailsForGstr9ReqDto dto,
			Map<String, BigDecimal> table6BMap,
			Map<String, BigDecimal> table6EMap,
			Map<String, BigDecimal> table6HMap,
			Map<String, BigDecimal> table8AMap) {

		List<Gstr9PdfDTO> table8List = new ArrayList<>();

		Gstr9PdfDTO table8ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_8A);
		Gstr9PdfDTO table8BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_8B);
		Gstr9PdfDTO table8CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_8C);
		Gstr9PdfDTO table8DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_8D);
		Gstr9PdfDTO table8EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_8E);
		Gstr9PdfDTO table8FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_8F);
		Gstr9PdfDTO table8GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_8G);
		Gstr9PdfDTO table8HDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.H,
				Gstr9PDFLabelUtil.TABLE_8H);
		Gstr9PdfDTO table8IDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.I,
				Gstr9PDFLabelUtil.TABLE_8I);
		Gstr9PdfDTO table8JDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.J,
				Gstr9PDFLabelUtil.TABLE_8J);
		Gstr9PdfDTO table8KDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.K,
				Gstr9PDFLabelUtil.TABLE_8K);

		BigDecimal cgstA = table8AMap.get(Gstr9PDFLabelUtil.CGST) != null
				? table8AMap.get(Gstr9PDFLabelUtil.CGST) : BigDecimal.ZERO;
		BigDecimal sgstA = table8AMap.get(Gstr9PDFLabelUtil.SGST) != null
				? table8AMap.get(Gstr9PDFLabelUtil.SGST) : BigDecimal.ZERO;
		BigDecimal igstA = table8AMap.get(Gstr9PDFLabelUtil.IGST) != null
				? table8AMap.get(Gstr9PDFLabelUtil.IGST) : BigDecimal.ZERO;
		BigDecimal cessA = table8AMap.get(Gstr9PDFLabelUtil.CESS) != null
				? table8AMap.get(Gstr9PDFLabelUtil.CESS) : BigDecimal.ZERO;

		BigDecimal cgstB = BigDecimal.ZERO;
		BigDecimal sgstB = BigDecimal.ZERO;
		BigDecimal igstB = BigDecimal.ZERO;
		BigDecimal cessB = BigDecimal.ZERO;

		BigDecimal cgstC = BigDecimal.ZERO;
		BigDecimal sgstC = BigDecimal.ZERO;
		BigDecimal igstC = BigDecimal.ZERO;
		BigDecimal cessC = BigDecimal.ZERO;

		BigDecimal cgstD = BigDecimal.ZERO;
		BigDecimal sgstD = BigDecimal.ZERO;
		BigDecimal igstD = BigDecimal.ZERO;
		BigDecimal cessD = BigDecimal.ZERO;

		BigDecimal cgstG = BigDecimal.ZERO;
		BigDecimal sgstG = BigDecimal.ZERO;
		BigDecimal igstG = BigDecimal.ZERO;
		BigDecimal cessG = BigDecimal.ZERO;

		BigDecimal cgstH = BigDecimal.ZERO;
		BigDecimal sgstH = BigDecimal.ZERO;
		BigDecimal igstH = BigDecimal.ZERO;
		BigDecimal cessH = BigDecimal.ZERO;

		BigDecimal cgstI = BigDecimal.ZERO;
		BigDecimal sgstI = BigDecimal.ZERO;
		BigDecimal igstI = BigDecimal.ZERO;
		BigDecimal cessI = BigDecimal.ZERO;

		BigDecimal cgstK = BigDecimal.ZERO;
		BigDecimal sgstK = BigDecimal.ZERO;
		BigDecimal igstK = BigDecimal.ZERO;
		BigDecimal cessK = BigDecimal.ZERO;

		table8ADto.setCgst(convertBigDecimalToString(cgstA));
		table8ADto.setSgst(convertBigDecimalToString(sgstA));
		table8ADto.setIgst(convertBigDecimalToString(igstA));
		table8ADto.setCess(convertBigDecimalToString(cessA));
		table8List.add(table8ADto);

		cgstB = addBigTwoDecimals(table6BMap.get(Gstr9PDFLabelUtil.CGST),
				table6HMap.get(Gstr9PDFLabelUtil.CGST));
		sgstB = addBigTwoDecimals(table6BMap.get(Gstr9PDFLabelUtil.SGST),
				table6HMap.get(Gstr9PDFLabelUtil.SGST));
		igstB = addBigTwoDecimals(table6BMap.get(Gstr9PDFLabelUtil.IGST),
				table6HMap.get(Gstr9PDFLabelUtil.IGST));
		cessB = addBigTwoDecimals(table6BMap.get(Gstr9PDFLabelUtil.CESS),
				table6HMap.get(Gstr9PDFLabelUtil.CESS));

		table8BDto.setCgst(convertBigDecimalToString(cgstB));
		table8BDto.setSgst(convertBigDecimalToString(sgstB));
		table8BDto.setIgst(convertBigDecimalToString(igstB));
		table8BDto.setCess(convertBigDecimalToString(cessB));
		table8List.add(table8BDto);

		cgstH = table6EMap.get(Gstr9PDFLabelUtil.CGST);
		sgstH = table6EMap.get(Gstr9PDFLabelUtil.SGST);
		igstH = table6EMap.get(Gstr9PDFLabelUtil.IGST);
		cessH = table6EMap.get(Gstr9PDFLabelUtil.CESS);

		table8HDto.setCgst(convertBigDecimalToString(cgstH));
		table8HDto.setSgst(convertBigDecimalToString(sgstH));
		table8HDto.setIgst(convertBigDecimalToString(igstH));

		table8HDto.setCess(convertBigDecimalToString(cessH));
		table8List.add(table8HDto);

		if (dto.getTable8ReqDto() != null) {
			Gstr9Table8ReqDto table8Dto = dto.getTable8ReqDto();

			if (table8Dto.getGstr9Table8ItcInwdSuppReqDto() != null) {

				cgstC = table8Dto.getGstr9Table8ItcInwdSuppReqDto().getCamt();
				sgstC = table8Dto.getGstr9Table8ItcInwdSuppReqDto().getSamt();
				igstC = table8Dto.getGstr9Table8ItcInwdSuppReqDto().getIamt();
				cessC = table8Dto.getGstr9Table8ItcInwdSuppReqDto().getCsamt();

				table8CDto.setCgst(convertBigDecimalToString(cgstC));
				table8CDto.setSgst(convertBigDecimalToString(sgstC));
				table8CDto.setIgst(convertBigDecimalToString(igstC));
				table8CDto.setCess(convertBigDecimalToString(cessC));

			}
			table8List.add(table8CDto);

			if (table8Dto.getGstr9Table8ItcNtAvaildReqDto() != null) {

				table8EDto.setCgst(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getCamt()));
				table8EDto.setSgst(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getSamt()));
				table8EDto.setIgst(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getIamt()));
				table8EDto.setCess(convertBigDecimalToString(table8Dto
						.getGstr9Table8ItcNtAvaildReqDto().getCsamt()));

				cgstK = addBigTwoDecimals(cgstK,
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getCamt());
				sgstK = addBigTwoDecimals(sgstK,
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getSamt());
				igstK = addBigTwoDecimals(igstK,
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getIamt());
				cessK = addBigTwoDecimals(cessK,
						table8Dto.getGstr9Table8ItcNtAvaildReqDto().getCsamt());

			}
			table8List.add(table8EDto);

			if (table8Dto.getGstr9Table8ItcNtElegReqDto() != null) {

				table8FDto.setCgst(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtElegReqDto().getCamt()));
				table8FDto.setSgst(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtElegReqDto().getSamt()));
				table8FDto.setIgst(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtElegReqDto().getIamt()));
				table8FDto.setCess(convertBigDecimalToString(
						table8Dto.getGstr9Table8ItcNtElegReqDto().getCsamt()));

				cgstK = addBigTwoDecimals(cgstK,
						table8Dto.getGstr9Table8ItcNtElegReqDto().getCamt());
				sgstK = addBigTwoDecimals(sgstK,
						table8Dto.getGstr9Table8ItcNtElegReqDto().getSamt());
				igstK = addBigTwoDecimals(igstK,
						table8Dto.getGstr9Table8ItcNtElegReqDto().getIamt());
				cessK = addBigTwoDecimals(cessK,
						table8Dto.getGstr9Table8ItcNtElegReqDto().getCsamt());

			}
			table8List.add(table8FDto);

			if (table8Dto.getGstr9Table8IogTaxPaidReqDto() != null) {

				cgstG = table8Dto.getGstr9Table8IogTaxPaidReqDto().getCamt();
				sgstG = table8Dto.getGstr9Table8IogTaxPaidReqDto().getSamt();
				igstG = table8Dto.getGstr9Table8IogTaxPaidReqDto().getIamt();
				cessG = table8Dto.getGstr9Table8IogTaxPaidReqDto().getCsamt();

				table8GDto.setCgst(convertBigDecimalToString(cgstG));
				table8GDto.setSgst(convertBigDecimalToString(sgstG));
				table8GDto.setIgst(convertBigDecimalToString(igstG));
				table8GDto.setCess(convertBigDecimalToString(cessG));

			}
			table8List.add(table8GDto);

			cgstI = subtractTwoBigDecimals(cgstG, cgstH);
			sgstI = subtractTwoBigDecimals(sgstG, sgstH);
			igstI = subtractTwoBigDecimals(igstG, igstH);
			cessI = subtractTwoBigDecimals(cessG, cessH);

			table8IDto.setCgst(convertBigDecimalToString(cgstI));
			table8IDto.setSgst(convertBigDecimalToString(sgstI));
			table8IDto.setIgst(convertBigDecimalToString(igstI));
			table8IDto.setCess(convertBigDecimalToString(cessI));
			table8List.add(table8IDto);

			table8JDto.setCgst(convertBigDecimalToString(cgstI));
			table8JDto.setSgst(convertBigDecimalToString(sgstI));
			table8JDto.setIgst(convertBigDecimalToString(igstI));
			table8JDto.setCess(convertBigDecimalToString(cessI));
			table8List.add(table8JDto);

			cgstK = addBigTwoDecimals(cgstK, cgstI);
			sgstK = addBigTwoDecimals(sgstK, sgstI);
			igstK = addBigTwoDecimals(igstK, igstI);
			cessK = addBigTwoDecimals(cessK, cessI);

			table8KDto.setCgst(convertBigDecimalToString(cgstK));
			table8KDto.setSgst(convertBigDecimalToString(sgstK));
			table8KDto.setIgst(convertBigDecimalToString(igstK));
			table8KDto.setCess(convertBigDecimalToString(cessK));
			table8List.add(table8KDto);

		} else {
			Collections.addAll(table8List, table8CDto, table8EDto, table8FDto,
					table8GDto, table8IDto, table8JDto, table8KDto);

		}

		cgstD = subtractTwoBigDecimals(cgstA, addBigTwoDecimals(cgstB, cgstC));
		sgstD = subtractTwoBigDecimals(sgstA, addBigTwoDecimals(sgstB, sgstC));
		igstD = subtractTwoBigDecimals(igstA, addBigTwoDecimals(igstB, igstC));
		cessD = subtractTwoBigDecimals(cessA, addBigTwoDecimals(cessB, cessC));

		table8DDto.setCgst(convertBigDecimalToString(cgstD));
		table8DDto.setSgst(convertBigDecimalToString(sgstD));
		table8DDto.setIgst(convertBigDecimalToString(igstD));
		table8DDto.setCess(convertBigDecimalToString(cessD));
		table8List.add(table8DDto);

		return table8List;
	}

	private List<Gstr9PdfDTO> getTable9List(GetDetailsForGstr9ReqDto dto) {

		List<Gstr9PdfDTO> table9List = new ArrayList<>();

		Gstr9PdfDTO table9ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_9A);
		Gstr9PdfDTO table9BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_9B);
		Gstr9PdfDTO table9CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_9C);
		Gstr9PdfDTO table9DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_9D);
		Gstr9PdfDTO table9EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_9E);
		Gstr9PdfDTO table9FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_9F);
		Gstr9PdfDTO table9GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_9G);
		Gstr9PdfDTO table9HDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.H,
				Gstr9PDFLabelUtil.TABLE_9H);

		table9ADto.setCess(Gstr9PDFLabelUtil.BLANK);
		table9BDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table9BDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table9CDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table9CDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table9DDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table9DDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table9DDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table9EDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table9EDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table9EDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table9EDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table9FDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table9FDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table9FDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table9FDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table9GDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table9GDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table9GDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table9GDto.setCess(Gstr9PDFLabelUtil.BLANK);
		table9HDto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table9HDto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table9HDto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table9HDto.setCess(Gstr9PDFLabelUtil.BLANK);

		if (dto.getTable9ReqDto() != null) {
			Gstr9Table9ReqDto table9Dto = dto.getTable9ReqDto();

			if (table9Dto.getGstr9Table9IamtReqDto() != null) {
				table9ADto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9IamtReqDto().getTxpyble()));
				table9ADto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9IamtReqDto().getTxpaidCash()));
				table9ADto.setCgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9IamtReqDto().getTaxPaidItcCamt()));
				table9ADto.setSgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9IamtReqDto().getTaxPaidItcSamt()));
				table9ADto.setIgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9IamtReqDto().getTaxPaidItcIamt()));

			}
			table9List.add(table9ADto);

			if (table9Dto.getGstr9Table9CamtReqDto() != null) {
				table9BDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9CamtReqDto().getTxpyble()));
				table9BDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9CamtReqDto().getTxpaidCash()));
				table9BDto.setCgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9CamtReqDto().getTaxPaidItcCamt()));
				table9BDto.setIgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9CamtReqDto().getTaxPaidItcIamt()));

			}

			table9List.add(table9BDto);

			if (table9Dto.getGstr9Table9SamtReqDto() != null) {
				table9CDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9SamtReqDto().getTxpyble()));
				table9CDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9SamtReqDto().getTxpaidCash()));
				table9CDto.setIgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9SamtReqDto().getTaxPaidItcIamt()));
				table9CDto.setSgst(convertBigDecimalToString(table9Dto
						.getGstr9Table9SamtReqDto().getTaxPaidItcSamt()));

			}

			table9List.add(table9CDto);

			if (table9Dto.getGstr9Table9CsamtReqDto() != null) {

				table9DDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9CsamtReqDto().getTxpyble()));
				table9DDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9CsamtReqDto().getTxpaidCash()));
				table9DDto.setCess(convertBigDecimalToString(table9Dto
						.getGstr9Table9CsamtReqDto().getTaxPaidItcCsamt()));

			}

			table9List.add(table9DDto);

			if (table9Dto.getGstr9Table9IntrReqDto() != null) {

				table9EDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9IntrReqDto().getTxpyble()));
				table9EDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9IntrReqDto().getTxpaidCash()));

			}

			table9List.add(table9EDto);

			if (table9Dto.getGstr9Table9TeeReqDto() != null) {

				table9FDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9TeeReqDto().getTxpyble()));
				table9FDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9TeeReqDto().getTxpaidCash()));

			}

			table9List.add(table9FDto);

			if (table9Dto.getGstr9Table9PenReqDto() != null) {

				table9GDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9PenReqDto().getTxpyble()));
				table9GDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9PenReqDto().getTxpaidCash()));

			}

			table9List.add(table9GDto);

			if (table9Dto.getGstr9Table9OtherReqDto() != null) {

				table9HDto.setTaxPayable(convertBigDecimalToString(
						table9Dto.getGstr9Table9OtherReqDto().getTxpyble()));
				table9HDto.setPaidCash(convertBigDecimalToString(
						table9Dto.getGstr9Table9OtherReqDto().getTxpaidCash()));
			}

			table9List.add(table9HDto);

		} else {
			Collections.addAll(table9List, table9ADto, table9BDto, table9CDto,
					table9DDto, table9EDto, table9FDto, table9GDto, table9HDto);
		}

		return table9List;

	}

	private List<Gstr9PdfDTO> getTable10List(GetDetailsForGstr9ReqDto dto,
			Map<String, BigDecimal> table5NMap) {

		List<Gstr9PdfDTO> table10List = new ArrayList<>();
		Gstr9PdfDTO table10ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.TEN,
				Gstr9PDFLabelUtil.TABLE_10);
		Gstr9PdfDTO table10BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.ELEVEN,
				Gstr9PDFLabelUtil.TABLE_11);
		Gstr9PdfDTO table10CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.TWELEVE,
				Gstr9PDFLabelUtil.TABLE_12);
		Gstr9PdfDTO table10DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.THIRTEEN,
				Gstr9PDFLabelUtil.TABLE_13);
		Gstr9PdfDTO table10EDto = new Gstr9PdfDTO(" ",
				Gstr9PDFLabelUtil.TABLE_10A);

		table10CDto.setTaxableValue(Gstr9PDFLabelUtil.BLANK);
		table10DDto.setTaxableValue(Gstr9PDFLabelUtil.BLANK);

		BigDecimal txbleVal10 = BigDecimal.ZERO;
		BigDecimal cgst10 = BigDecimal.ZERO;
		BigDecimal sgst10 = BigDecimal.ZERO;
		BigDecimal igst10 = BigDecimal.ZERO;
		BigDecimal cess10 = BigDecimal.ZERO;

		BigDecimal txbleVal11 = BigDecimal.ZERO;
		BigDecimal cgst11 = BigDecimal.ZERO;
		BigDecimal sgst11 = BigDecimal.ZERO;
		BigDecimal igst11 = BigDecimal.ZERO;
		BigDecimal cess11 = BigDecimal.ZERO;

		if (dto.getTable10ReqDto() != null) {

			Gstr9Table10ReqDto table10Dto = dto.getTable10ReqDto();

			if (table10Dto.getDbnAmdReqDto() != null) {

				txbleVal10 = table10Dto.getDbnAmdReqDto().getTxval();
				cgst10 = table10Dto.getDbnAmdReqDto().getCamt();
				sgst10 = table10Dto.getDbnAmdReqDto().getSamt();
				igst10 = table10Dto.getDbnAmdReqDto().getIamt();
				cess10 = table10Dto.getDbnAmdReqDto().getCsamt();

				table10ADto
						.setTaxableValue(convertBigDecimalToString(txbleVal10));
				table10ADto.setCgst(convertBigDecimalToString(cgst10));
				table10ADto.setSgst(convertBigDecimalToString(sgst10));
				table10ADto.setIgst(convertBigDecimalToString(igst10));
				table10ADto.setCess(convertBigDecimalToString(cess10));

			}
			table10List.add(table10ADto);

			if (table10Dto.getCdnAmdReqDto() != null) {

				txbleVal11 = table10Dto.getCdnAmdReqDto().getTxval();
				cgst11 = table10Dto.getCdnAmdReqDto().getCamt();
				sgst11 = table10Dto.getCdnAmdReqDto().getSamt();
				igst11 = table10Dto.getCdnAmdReqDto().getIamt();
				cess11 = table10Dto.getCdnAmdReqDto().getCsamt();

				table10BDto
						.setTaxableValue(convertBigDecimalToString(txbleVal11));
				table10BDto.setCgst(convertBigDecimalToString(cgst11));
				table10BDto.setSgst(convertBigDecimalToString(sgst11));
				table10BDto.setIgst(convertBigDecimalToString(igst11));
				table10BDto.setCess(convertBigDecimalToString(cess11));

			}
			table10List.add(table10BDto);

			if (table10Dto.getGstr9Table10ItcRvslReqDto() != null) {

				table10CDto.setCgst(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcRvslReqDto().getCamt()));
				table10CDto.setSgst(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcRvslReqDto().getSamt()));
				table10CDto.setIgst(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcRvslReqDto().getIamt()));
				table10CDto.setCess(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcRvslReqDto().getCsamt()));

			}
			table10List.add(table10CDto);

			if (table10Dto.getGstr9Table10ItcAvaildReqDto() != null) {

				table10DDto.setCgst(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcAvaildReqDto().getCamt()));
				table10DDto.setSgst(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcAvaildReqDto().getSamt()));
				table10DDto.setIgst(convertBigDecimalToString(
						table10Dto.getGstr9Table10ItcAvaildReqDto().getIamt()));
				table10DDto.setCess(convertBigDecimalToString(table10Dto
						.getGstr9Table10ItcAvaildReqDto().getCsamt()));

			}
			table10List.add(table10DDto);

		} else {
			Collections.addAll(table10List, table10ADto, table10BDto,
					table10CDto, table10DDto);

		}

		table10EDto.setTaxableValue(
				convertBigDecimalToString(addAndsubtractBigDecimals(
						table5NMap.get(Gstr9PDFLabelUtil.TAXABLE_VALUE),
						txbleVal10, txbleVal11)));
		table10EDto.setCgst(convertBigDecimalToString(addAndsubtractBigDecimals(
				table5NMap.get(Gstr9PDFLabelUtil.CGST), cgst10, cgst11)));

		table10EDto.setSgst(convertBigDecimalToString(addAndsubtractBigDecimals(
				table5NMap.get(Gstr9PDFLabelUtil.SGST), sgst10, sgst11)));

		table10EDto.setIgst(convertBigDecimalToString(addAndsubtractBigDecimals(
				table5NMap.get(Gstr9PDFLabelUtil.IGST), igst10, igst11)));

		table10EDto.setCess(convertBigDecimalToString(addAndsubtractBigDecimals(
				table5NMap.get(Gstr9PDFLabelUtil.CESS), cess10, cess11)));

		table10List.add(table10EDto);
		return table10List;
	}

	private List<Gstr9PdfDTO> getTable14List(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table14List = new ArrayList<>();

		Gstr9PdfDTO table14ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_14A);
		Gstr9PdfDTO table14BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_14B);
		Gstr9PdfDTO table14CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_14C);
		Gstr9PdfDTO table14DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_14D);
		Gstr9PdfDTO table14EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_14E);

		if (dto.getTable14ReqDto() != null) {

			Gstr9Table14ReqDto table10Dto = dto.getTable14ReqDto();

			if (table10Dto.getGstr9Table14IamtReqDto() != null) {
				table14ADto.setTaxPayable(convertBigDecimalToString(
						table10Dto.getGstr9Table14IamtReqDto().getTxpyble()));
				table14ADto.setPaid(convertBigDecimalToString(
						table10Dto.getGstr9Table14IamtReqDto().getTxpaid()));
			}
			table14List.add(table14ADto);
			if (table10Dto.getGstr9Table14CamtReqDto() != null) {
				table14BDto.setTaxPayable(convertBigDecimalToString(
						table10Dto.getGstr9Table14CamtReqDto().getTxpyble()));
				table14BDto.setPaid(convertBigDecimalToString(
						table10Dto.getGstr9Table14CamtReqDto().getTxpaid()));
			}
			table14List.add(table14BDto);

			if (table10Dto.getGstr9Table14SamtReqDto() != null) {
				table14CDto.setTaxPayable(convertBigDecimalToString(
						table10Dto.getGstr9Table14SamtReqDto().getTxpyble()));
				table14CDto.setPaid(convertBigDecimalToString(
						table10Dto.getGstr9Table14SamtReqDto().getTxpaid()));
			}
			table14List.add(table14CDto);

			if (table10Dto.getGstr9Table14CSamtReqDto() != null) {
				table14DDto.setTaxPayable(convertBigDecimalToString(
						table10Dto.getGstr9Table14CSamtReqDto().getTxpyble()));
				table14DDto.setPaid(convertBigDecimalToString(
						table10Dto.getGstr9Table14CSamtReqDto().getTxpaid()));
			}
			table14List.add(table14DDto);

			if (table10Dto.getGstr9Table14IntrReqDto() != null) {
				table14EDto.setTaxPayable(convertBigDecimalToString(
						table10Dto.getGstr9Table14IntrReqDto().getTxpyble()));
				table14EDto.setPaid(convertBigDecimalToString(
						table10Dto.getGstr9Table14IntrReqDto().getTxpaid()));
			}
			table14List.add(table14EDto);

		} else {
			Collections.addAll(table14List, table14ADto, table14BDto,
					table14CDto, table14DDto, table14EDto);
		}
		return table14List;
	}

	private List<Gstr9PdfDTO> getTable15List(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table15List = new ArrayList<>();

		Gstr9PdfDTO table15ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_15A);
		Gstr9PdfDTO table15BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_15B);
		Gstr9PdfDTO table15CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_15C);
		Gstr9PdfDTO table15DDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.D,
				Gstr9PDFLabelUtil.TABLE_15D);
		Gstr9PdfDTO table15EDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.E,
				Gstr9PDFLabelUtil.TABLE_15E);
		Gstr9PdfDTO table15FDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.F,
				Gstr9PDFLabelUtil.TABLE_15F);
		Gstr9PdfDTO table15GDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.G,
				Gstr9PDFLabelUtil.TABLE_15G);

		table15ADto.setInterest(Gstr9PDFLabelUtil.BLANK);
		table15ADto.setPenalty(Gstr9PDFLabelUtil.BLANK);
		table15ADto.setLateFee(Gstr9PDFLabelUtil.BLANK);
		table15BDto.setInterest(Gstr9PDFLabelUtil.BLANK);
		table15BDto.setPenalty(Gstr9PDFLabelUtil.BLANK);
		table15BDto.setLateFee(Gstr9PDFLabelUtil.BLANK);
		table15CDto.setInterest(Gstr9PDFLabelUtil.BLANK);
		table15CDto.setPenalty(Gstr9PDFLabelUtil.BLANK);
		table15CDto.setLateFee(Gstr9PDFLabelUtil.BLANK);
		table15DDto.setInterest(Gstr9PDFLabelUtil.BLANK);
		table15DDto.setPenalty(Gstr9PDFLabelUtil.BLANK);
		table15DDto.setLateFee(Gstr9PDFLabelUtil.BLANK);

		if (dto.getTable15ReqDto() != null) {

			Gstr9Table15ReqDto table15Dto = dto.getTable15ReqDto();

			if (table15Dto.getGstr9Table15RfdClmdReqDto() != null) {
				table15ADto.setCgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdClmdReqDto().getCamt()));
				table15ADto.setSgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdClmdReqDto().getSamt()));
				table15ADto.setIgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdClmdReqDto().getIamt()));
				table15ADto.setCess(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdClmdReqDto().getCsamt()));

			}
			table15List.add(table15ADto);

			if (table15Dto.getGstr9Table15RfdSancReqDto() != null) {
				table15BDto.setCgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdSancReqDto().getCamt()));
				table15BDto.setSgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdSancReqDto().getSamt()));
				table15BDto.setIgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdSancReqDto().getIamt()));
				table15BDto.setCess(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdSancReqDto().getCsamt()));

			}
			table15List.add(table15BDto);

			if (table15Dto.getGstr9Table15RfdRejtReqDto() != null) {
				table15CDto.setCgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdRejtReqDto().getCamt()));
				table15CDto.setSgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdRejtReqDto().getSamt()));
				table15CDto.setIgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdRejtReqDto().getIamt()));
				table15CDto.setCess(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdRejtReqDto().getCsamt()));

			}
			table15List.add(table15CDto);

			if (table15Dto.getGstr9Table15RfdPendReqDto() != null) {
				table15DDto.setCgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdPendReqDto().getCamt()));
				table15DDto.setSgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdPendReqDto().getSamt()));
				table15DDto.setIgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdPendReqDto().getIamt()));
				table15DDto.setCess(convertBigDecimalToString(
						table15Dto.getGstr9Table15RfdPendReqDto().getCsamt()));

			}
			table15List.add(table15DDto);

			if (table15Dto.getGstr9Table15TaxDmndtReqDto() != null) {
				table15EDto.setCgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getCamt()));
				table15EDto.setSgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getSamt()));
				table15EDto.setIgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getIamt()));
				table15EDto.setCess(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getCsamt()));
				table15EDto.setInterest(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getIntr()));
				table15EDto.setPenalty(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getPen()));
				table15EDto.setLateFee(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxDmndtReqDto().getFee()));

			}
			table15List.add(table15EDto);

			if (table15Dto.getGstr9Table15TaxPaidReqDto() != null) {
				table15FDto.setCgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getCamt()));
				table15FDto.setSgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getSamt()));
				table15FDto.setIgst(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getIamt()));
				table15FDto.setCess(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getCsamt()));
				table15FDto.setInterest(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getIntr()));
				table15FDto.setPenalty(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getPen()));
				table15FDto.setLateFee(convertBigDecimalToString(
						table15Dto.getGstr9Table15TaxPaidReqDto().getFee()));

			}
			table15List.add(table15FDto);

			if (table15Dto.getGstr9Table15TotalDmndPendReqDto() != null) {
				table15GDto.setCgst(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getCamt()));
				table15GDto.setSgst(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getSamt()));
				table15GDto.setIgst(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getIamt()));
				table15GDto.setCess(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getCsamt()));
				table15GDto.setInterest(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getIntr()));
				table15GDto.setPenalty(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getPen()));
				table15GDto.setLateFee(convertBigDecimalToString(table15Dto
						.getGstr9Table15TotalDmndPendReqDto().getFee()));

			}
			table15List.add(table15GDto);

		} else {
			Collections.addAll(table15List, table15ADto, table15BDto,
					table15CDto, table15DDto, table15EDto, table15FDto,
					table15GDto);
		}

		return table15List;
	}

	private List<Gstr9PdfDTO> getTable16List(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> table16List = new ArrayList<>();
		Gstr9PdfDTO table16ADto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.A,
				Gstr9PDFLabelUtil.TABLE_16A);
		Gstr9PdfDTO table16BDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.B,
				Gstr9PDFLabelUtil.TABLE_16B);
		Gstr9PdfDTO table16CDto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.C,
				Gstr9PDFLabelUtil.TABLE_16C);

		table16ADto.setCgst(Gstr9PDFLabelUtil.BLANK);
		table16ADto.setSgst(Gstr9PDFLabelUtil.BLANK);
		table16ADto.setIgst(Gstr9PDFLabelUtil.BLANK);
		table16ADto.setCess(Gstr9PDFLabelUtil.BLANK);

		if (dto.getTable16ReqDto() != null) {

			Gstr9Table16ReqDto table16Dto = dto.getTable16ReqDto();

			if (table16Dto.getGstr9Table16CompSuppReqDto() != null) {

				table16ADto.setTaxableValue(convertBigDecimalToString(
						table16Dto.getGstr9Table16CompSuppReqDto().getTxval()));
			}
			table16List.add(table16ADto);

			if (table16Dto.getGstr9Table16DeemedSuppReqDto() != null) {

				table16BDto.setTaxableValue(convertBigDecimalToString(table16Dto
						.getGstr9Table16DeemedSuppReqDto().getTxval()));

				table16BDto.setCgst(convertBigDecimalToString(table16Dto
						.getGstr9Table16DeemedSuppReqDto().getCamt()));

				table16BDto.setSgst(convertBigDecimalToString(table16Dto
						.getGstr9Table16DeemedSuppReqDto().getSamt()));
				table16BDto.setIgst(convertBigDecimalToString(table16Dto
						.getGstr9Table16DeemedSuppReqDto().getIamt()));
				table16BDto.setCess(convertBigDecimalToString(table16Dto
						.getGstr9Table16DeemedSuppReqDto().getCsamt()));
			}
			table16List.add(table16BDto);

			if (table16Dto.getGstr9Table16NotReturnedReqDto() != null) {

				table16CDto.setTaxableValue(convertBigDecimalToString(table16Dto
						.getGstr9Table16NotReturnedReqDto().getTxval()));

				table16CDto.setCgst(convertBigDecimalToString(table16Dto
						.getGstr9Table16NotReturnedReqDto().getCamt()));

				table16CDto.setSgst(convertBigDecimalToString(table16Dto
						.getGstr9Table16NotReturnedReqDto().getSamt()));
				table16CDto.setIgst(convertBigDecimalToString(table16Dto
						.getGstr9Table16NotReturnedReqDto().getIamt()));
				table16CDto.setCess(convertBigDecimalToString(table16Dto
						.getGstr9Table16NotReturnedReqDto().getCsamt()));
			}
			table16List.add(table16CDto);

		} else {
			Collections.addAll(table16List, table16ADto, table16BDto,
					table16CDto);
		}

		return table16List;
	}

	private List<Gstr9PdfDTO> getTableHSNList(GetDetailsForGstr9ReqDto dto) {
		List<Gstr9PdfDTO> tableHSNList = new ArrayList<>();
		Gstr9PdfDTO table17Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.SEVENTEEN,
				Gstr9PDFLabelUtil.TABLE_17);
		Gstr9PdfDTO table18Dto = new Gstr9PdfDTO(Gstr9PDFLabelUtil.EIGHTEEN,
				Gstr9PDFLabelUtil.TABLE_18);

		Integer count17 = null;
		BigDecimal totalQty17 = BigDecimal.ZERO;
		BigDecimal taxableValue17 = BigDecimal.ZERO;
		BigDecimal igst17 = BigDecimal.ZERO;
		BigDecimal cgst17 = BigDecimal.ZERO;
		BigDecimal sgst17 = BigDecimal.ZERO;
		BigDecimal cess17 = BigDecimal.ZERO;

		Integer count18 = null;
		BigDecimal totalQty18 = BigDecimal.ZERO;
		BigDecimal taxableValue18 = BigDecimal.ZERO;
		BigDecimal igst18 = BigDecimal.ZERO;
		BigDecimal cgst18 = BigDecimal.ZERO;
		BigDecimal sgst18 = BigDecimal.ZERO;
		BigDecimal cess18 = BigDecimal.ZERO;

		if (dto.getTable17ReqDto() != null) {

			List<Gstr9Table17ItemsReqDto> gstr9Table17List = dto
					.getTable17ReqDto().getGstr9Table17ItemsReqDtos();

			count17 = gstr9Table17List.size();

			if (!CollectionUtils.isEmpty(gstr9Table17List)) {

				for (Gstr9Table17ItemsReqDto e : gstr9Table17List) {

					totalQty17 = addBigTwoDecimals(totalQty17, e.getQty());
					taxableValue17 = addBigTwoDecimals(taxableValue17,
							e.getTxval());
					igst17 = addBigTwoDecimals(igst17, e.getIamt());
					cgst17 = addBigTwoDecimals(cgst17, e.getCamt());
					sgst17 = addBigTwoDecimals(sgst17, e.getSamt());
					cess17 = addBigTwoDecimals(cess17, e.getCsamt());
				}
				table17Dto.setCount(count17.toString());
				table17Dto.setQty(convertBigDecimalToString(totalQty17));
				table17Dto.setTaxableValue(
						convertBigDecimalToString(taxableValue17));
				table17Dto.setCgst(convertBigDecimalToString(cgst17));
				table17Dto.setSgst(convertBigDecimalToString(sgst17));

				table17Dto.setIgst(convertBigDecimalToString(igst17));
				table17Dto.setCess(convertBigDecimalToString(cess17));

			}

		}
		tableHSNList.add(table17Dto);

		if (dto.getTable18ReqDto() != null) {

			List<Gstr9Table18ItemsReqDto> gstr9Table18List = dto
					.getTable18ReqDto().getGstr9Table18ItemsReqDtos();

			count18 = gstr9Table18List.size();

			if (!CollectionUtils.isEmpty(gstr9Table18List)) {

				for (Gstr9Table18ItemsReqDto e : gstr9Table18List) {

					totalQty18 = addBigTwoDecimals(totalQty18, e.getQty());
					taxableValue18 = addBigTwoDecimals(taxableValue18,
							e.getTxval());
					igst18 = addBigTwoDecimals(igst18, e.getIamt());
					cgst18 = addBigTwoDecimals(cgst18, e.getCamt());
					sgst18 = addBigTwoDecimals(sgst18, e.getSamt());
					cess18 = addBigTwoDecimals(cess18, e.getCsamt());
				}
				table18Dto.setCount(count18.toString());
				table18Dto.setQty(convertBigDecimalToString(totalQty18));
				table18Dto.setTaxableValue(
						convertBigDecimalToString(taxableValue18));
				table18Dto.setCgst(convertBigDecimalToString(cgst18));
				table18Dto.setSgst(convertBigDecimalToString(sgst18));

				table18Dto.setIgst(convertBigDecimalToString(igst18));
				table18Dto.setCess(convertBigDecimalToString(cess18));

			}

		}
		tableHSNList.add(table18Dto);

		return tableHSNList;
	}

	private String convertBigDecimalToString(BigDecimal amt) {
		Format format = com.ibm.icu.text.NumberFormat
				.getCurrencyInstance(new Locale("en", "in"));

		if (amt != null) {
			return format.format(amt);
		}
		return null;
	}

	private BigDecimal addBigTwoDecimals(BigDecimal amt1, BigDecimal amt2) {
		BigDecimal bd1 = amt1 != null ? amt1 : BigDecimal.ZERO;
		BigDecimal bd2 = amt2 != null ? amt2 : BigDecimal.ZERO;

		return bd1.add(bd2);
	}

	private BigDecimal subtractTwoBigDecimals(BigDecimal amt1,
			BigDecimal amt2) {
		BigDecimal bd1 = amt1 != null ? amt1 : BigDecimal.ZERO;
		BigDecimal bd2 = amt2 != null ? amt2 : BigDecimal.ZERO;

		return bd1.subtract(bd2);
	}

	private BigDecimal addAndsubtractBigDecimals(BigDecimal amt1,
			BigDecimal amt2, BigDecimal amt3) {
		BigDecimal bd3 = amt3 != null ? amt3 : BigDecimal.ZERO;
		BigDecimal sum = addBigTwoDecimals(amt1, amt2);
		return sum.subtract(bd3);
	}

	private Triplet<String, String, String> getFilingDateAndStatus(String gstin,
			String taxPeriod, String fy) {

		String filingStatus = null;
		String filingDate = null;
		String arn = null;

		try {
			GstrReturnStatusEntity gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							gstin, taxPeriod, GSTR9Constants.GSTR9);

			GstrReturnStatusEntity gstrReturnStatusEntitySubm = gstrReturnStatusRepository
					.findSubmittedRecords(gstin, taxPeriod,
							GSTR9Constants.GSTR9);

			if (gstrReturnStatusEntity == null || !(gstrReturnStatusEntity
					.getStatus().equalsIgnoreCase(Gstr9PDFLabelUtil.FILED)
					|| gstrReturnStatusEntity.getStatus()
							.equalsIgnoreCase(Gstr9PDFLabelUtil.SUBMITTED))) {

				List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

				try {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTR9_PDF_RET);
					returnFilingGstnResponseDtoList = gstnReturnFilingStatus
							.callGstnApi(Arrays.asList(gstin), fy, false);

					for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(GSTR9Constants.GSTR9)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(taxPeriod)) {

							filingStatus = returnFilingGstnResponseDto
									.getStatus();
							filingDate = returnFilingGstnResponseDto
									.getFilingDate() + " 00:00:00";
							arn = returnFilingGstnResponseDto.getArnNo();

						}

					}

				} catch (Exception e) {
					LOGGER.error(
							"Not able to generate Public Auth Token while Fetching Filling Status");
				}

			} else {

				filingStatus = gstrReturnStatusEntity.getStatus();
				if (filingStatus.equalsIgnoreCase(Gstr9PDFLabelUtil.FILED)) {

					filingDate = gstrReturnStatusEntity.getFilingDate()
							.format(formatter2) + " 00:00:00";
					arn = gstrReturnStatusEntity.getArnNo();

				} else if (filingStatus
						.equalsIgnoreCase(Gstr9PDFLabelUtil.SUBMITTED)
						&& gstrReturnStatusEntitySubm != null) {

					filingDate = getStandardTime(
							gstrReturnStatusEntitySubm.getUpdatedOn());
					arn = gstrReturnStatusEntity.getArnNo();

				}

			}
		} catch (Exception e) {
			String msg = "Exception occured getting return status and date";
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}
		return new Triplet<>(filingStatus, filingDate, arn);
	}
}
