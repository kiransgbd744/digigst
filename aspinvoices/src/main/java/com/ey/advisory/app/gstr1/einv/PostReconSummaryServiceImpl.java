package com.ey.advisory.app.gstr1.einv;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Font;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("PostReconSummaryServiceImpl")
public class PostReconSummaryServiceImpl implements PostReconSummaryService {
	
	final static String available = "Available";

	final static String notAvailable = "Not Available";

	final static String cancelled = "Cancelled";

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	private static final DateTimeFormatter timeFormat = DateTimeFormatter
			.ofPattern("HH:mm:ss");

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	final static Map<Integer, String> sections = ImmutableMap
			.<Integer, String>builder().put(1, "Additional in A/P GSTR1")
			.put(2, "Errors in A/P GSTR1 (Not Available in DigiGST)")
			.put(3, "Errors in A/P GSTR1 (Available in DigiGST)")
			.put(4, "Additional in DigiGST (Not available in A/P GSTR1)")
			.put(5, "Available in DigiGST & Available in A/P GSTR1")
			.put(6, "Deletion / Deletion Failed status in A/P GSTR1").build();

	final static Map<String, String> subSectionMap = ImmutableMap
			.<String, String>builder()
			.put(sections.get(1) + "-" + notAvailable,
					"Records not available at DigiGST & A/P success at GSTR-1")
			.put(sections.get(1) + "-" + cancelled,
					"Records cancelled at DigiGST & A/P success at GSTR-1")
			.put(sections.get(2) + "-" + notAvailable,
					"Records not available at DigiGST & A/P fail at GSTR-1")
			.put(sections.get(2) + "-" + cancelled,
					"Records cancelled at DigiGST & A/P fail at GSTR-1")
			.put(sections.get(3) + "-" + available,
					"Records available at DigiGST & A/P fail at GSTR-1")
			.put(sections.get(4) + "-" + available,
					"Records available at DigiGST & not available in A/P GSTR-1")
			.put(sections.get(5) + "-" + available,
					"Records available at DigiGST & A/P success at GSTR-1")
			.put(sections.get(6) + "-" + available,
					"Records available at DigiGST & Deletion success/fail at GSTR-1")
			.put(sections.get(6) + "-" + cancelled,
					"Records cancelled at DigiGST & Deletion success/fail at GSTR-1")
			.put(sections.get(6) + "-" + notAvailable,
					"Records not available at DigiGST & Deletion success/fail at GSTR-1")
			.build();

	@Autowired
	@Qualifier("PostReconSummaryDaoImpl")
	private PostReconSummaryDao postReconSummaryDao;

	@Autowired
	CommonUtility commonUtility;

	@Override
	public PostReconSummaryTabDto getReconSummaryDetails(
			List<String> recipientGstins, String taxPeriod) {
		Long totalEinvCount = 0L;

		Long salesEinvCount = 0L;
		try {

			List<Object[]> objList = postReconSummaryDao
					.getPostReconSummaryData(recipientGstins,
							Integer.valueOf(taxPeriod));

			if (objList.isEmpty()) {
				String msg = String.format(
						"No Data Available" + "  for recipientGstins :%s"
								+ " TaxPeriod :%s ",
						recipientGstins, taxPeriod);
				LOGGER.error(msg);
				String appMsg = "No Data Available for Selected Gstins and TaxPeriod";
				throw new AppException(appMsg);
			}

			List<PostReconSummaryDto> retList = objList.stream()
					.map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("Converted List from ResultSet {}", retList);
			Set<String> uniqueParticular = new HashSet<>();

			for (PostReconSummaryDto resp : retList) {

				totalEinvCount = resp.getEinvcount() + totalEinvCount;
				salesEinvCount = resp.getSalesRegcount() + salesEinvCount;
				uniqueParticular.add(resp.getSection());

			}

			List<PostReconSummaryDto> extractPercentageList = calculatePercentage(
					retList, totalEinvCount, salesEinvCount);
			LOGGER.debug("Converted List after Percentage Calculation {}",
					extractPercentageList);

			List<String> sections = Lists.newArrayList(uniqueParticular);

			Map<String, PostReconSummaryDto> reqMap = extractPercentageList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getParticulars(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			List<PostReconSummaryDto> valueList = reqMap.values().stream()
					.collect(Collectors.toList());

			List<PostReconSummaryDto> respList = convertScreenLevelData(
					sections, valueList);

			Collections.sort(respList, (a, b) -> a.getOrderPosition()
					.compareToIgnoreCase(b.getOrderPosition()));

			return new PostReconSummaryTabDto(respList);
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing getReconSummaryDetails  "
							+ "  for recipientGstins :%s" + " TaxPeriod :%s ",
					recipientGstins, taxPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}

	private PostReconSummaryDto addDto(PostReconSummaryDto a,
			PostReconSummaryDto b) {
		PostReconSummaryDto dto = new PostReconSummaryDto();

		dto.setSection(a.getSection());
		dto.setGstin(a.getGstin());
		dto.setDigigstStatus(a.getDigigstStatus());
		dto.setGstnStatus(a.getGstnStatus());
		dto.setParticulars(a.getParticulars());
		dto.setEinvcount(a.getEinvcount() != null && b.getEinvcount() != null
				? (a.getEinvcount() + b.getEinvcount()) : 0);
		dto.setEinvtaxablevalue(GenUtil.defaultToZeroIfNull(
				a.getEinvtaxablevalue().add(b.getEinvtaxablevalue())));
		dto.setEinvpercentage(GenUtil.defaultToZeroIfNull(
				a.getEinvpercentage().add(b.getEinvpercentage())));

		dto.setEinvtotaltax(GenUtil.defaultToZeroIfNull(
				a.getEinvtotaltax().add(b.getEinvtotaltax())));
		dto.setSalesRegcount(
				a.getSalesRegcount() != null && b.getSalesRegcount() != null
						? (a.getSalesRegcount() + b.getSalesRegcount()) : 0);
		dto.setSalesRegpercentage(GenUtil.defaultToZeroIfNull(
				a.getSalesRegpercentage().add(b.getSalesRegpercentage())));

		dto.setSalesRegtaxablevalue(GenUtil.defaultToZeroIfNull(
				a.getSalesRegtaxablevalue().add(b.getSalesRegtaxablevalue())));
		dto.setSalesRegtotaltax(GenUtil.defaultToZeroIfNull(
				a.getSalesRegtotaltax().add(b.getSalesRegtotaltax())));

		return dto;

	}

	private List<PostReconSummaryDto> calculatePercentage(
			List<PostReconSummaryDto> reqDto, Long totalEinvCount,
			Long totalSalesRegCount) {
		List<PostReconSummaryDto> listDto = new ArrayList<>();
		try {

			for (PostReconSummaryDto prSummDto : reqDto) {

				DecimalFormat numberFormat = new DecimalFormat("#.##");
				numberFormat.setRoundingMode(RoundingMode.DOWN);

				PostReconSummaryDto dto = new PostReconSummaryDto();

				double einvPercent = 0;
				if (totalEinvCount == 0) {
					einvPercent = 0;
				} else {
					einvPercent = (prSummDto.getEinvcount().doubleValue() * 100)
							/ totalEinvCount.doubleValue();
				}
				double salesPercent = 0;

				if (totalSalesRegCount == 0) {
					salesPercent = 0;
				} else {
					salesPercent = (prSummDto.getSalesRegcount().doubleValue()
							* 100) / totalSalesRegCount.doubleValue();
				}
				BigDecimal einvPercentageValue = new BigDecimal(
						numberFormat.format(einvPercent));

				BigDecimal salesPercentageValue = new BigDecimal(
						numberFormat.format(salesPercent));

				String section = prSummDto.getSection();
				dto.setSection(section.trim());

				String gstin = prSummDto.getGstin();
				dto.setGstin(gstin.trim());

				String digiStatus = prSummDto.getDigigstStatus();
				dto.setDigigstStatus(digiStatus.trim());
				String gstnStatus = prSummDto.getDigigstStatus();
				dto.setGstnStatus(gstnStatus.trim());
				dto.setParticulars(prSummDto.getParticulars());
				dto.setEinvcount(prSummDto.getEinvcount());
				dto.setEinvpercentage((einvPercentageValue));
				dto.setEinvtaxablevalue(prSummDto.getEinvtaxablevalue());
				dto.setEinvtotaltax(prSummDto.getEinvtotaltax());
				dto.setSalesRegcount(prSummDto.getSalesRegcount());
				dto.setSalesRegpercentage(salesPercentageValue);
				dto.setSalesRegtaxablevalue(
						prSummDto.getSalesRegtaxablevalue());
				dto.setSalesRegtotaltax(prSummDto.getSalesRegtotaltax());
				listDto.add(dto);
			}

			return listDto;
		} catch (Exception ee) {
			String msg = String
					.format("Error while converting to dto in Perc Calc");
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}

	}

	private PostReconSummaryDto convertToDto(Object[] arr) {
		PostReconSummaryDto dto = new PostReconSummaryDto();
		try {
			String section = arr[0] != null ? (String) arr[0] : "";
			dto.setSection(section.trim());

			String gstin = arr[1] != null ? (String) arr[1] : "";
			dto.setGstin(gstin.trim());

			String digiStatus = arr[3] != null ? (String) arr[3] : "";
			dto.setDigigstStatus(digiStatus.trim());
			String gstnStatus = arr[4] != null ? (String) arr[4] : "";
			dto.setGstnStatus(gstnStatus.trim());
			dto.setParticulars(
					getSubSectionBasedCateg(digiStatus.trim(), section.trim())
							.trim());
			dto.setEinvcount((GenUtil.getBigInteger(arr[5])).longValue());
//			dto.setEinvpercentage(
//					(GenUtil.defaultToZeroIfNull((BigDecimal) arr[6])));
			dto.setEinvtaxablevalue(
					(GenUtil.defaultToZeroIfNull((BigDecimal) arr[7])));
			dto.setEinvtotaltax(
					(GenUtil.defaultToZeroIfNull((BigDecimal) arr[8])));
			dto.setSalesRegcount((GenUtil.getBigInteger(arr[9])).longValue());
//			dto.setSalesRegpercentage(
//					(GenUtil.defaultToZeroIfNull((BigDecimal) arr[10])));
			dto.setSalesRegtaxablevalue(
					(GenUtil.defaultToZeroIfNull((BigDecimal) arr[11])));
			dto.setSalesRegtotaltax(
					(GenUtil.defaultToZeroIfNull((BigDecimal) arr[12])));
			return dto;
		} catch (Exception ee) {
			String msg = String.format("Error while converting to dto");
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}

	}

	private List<PostReconSummaryDto> convertScreenLevelData(
			List<String> particular, List<PostReconSummaryDto> retList) {
		// Additional in A/P GSTR1
		Long additionalapeinvcount = 0L;
		BigDecimal additionalapeinvpercentage = BigDecimal.ZERO;
		BigDecimal additionalapeinvtaxablevalue = BigDecimal.ZERO;
		BigDecimal additionalapeinvtotaltax = BigDecimal.ZERO;
		Long additionalapsalesRegcount = 0L;
		BigDecimal additionalapsalesRegpercentage = BigDecimal.ZERO;
		BigDecimal additionalapsalesRegtaxablevalue = BigDecimal.ZERO;
		BigDecimal additionalapsalesRegtotaltax = BigDecimal.ZERO;
		String additionaldigiStatus = null;
		String additionalgstnStatus = null;

		// Errors in A/P GSTR1 (Not Available in DigiGST)
		Long errNotAvaeinvcount = 0L;
		BigDecimal errNotAvaeinvpercentage = BigDecimal.ZERO;
		BigDecimal errNotAvaeinvtaxablevalue = BigDecimal.ZERO;
		BigDecimal errNotAvaeinvtotaltax = BigDecimal.ZERO;
		Long errNotAvasalesRegcount = 0L;
		BigDecimal errNotAvasalesRegpercentage = BigDecimal.ZERO;
		BigDecimal errNotAvasalesRegtaxablevalue = BigDecimal.ZERO;
		BigDecimal errNotAvasalesRegtotaltax = BigDecimal.ZERO;
		String errNotAvadigiStatus = null;
		String errNotAvagstnStatus = null;

		// Errors in A/P GSTR1 (Available in DigiGST)
		Long errAvaeinvcount = 0L;
		BigDecimal errAvaeinvpercentage = BigDecimal.ZERO;
		BigDecimal errAvaeinvtaxablevalue = BigDecimal.ZERO;
		BigDecimal errAvaeinvtotaltax = BigDecimal.ZERO;
		Long errAvasalesRegcount = 0L;
		BigDecimal errAvasalesRegpercentage = BigDecimal.ZERO;
		BigDecimal errAvasalesRegtaxablevalue = BigDecimal.ZERO;
		BigDecimal errAvasalesRegtotaltax = BigDecimal.ZERO;

		String errAvadigiStatus = null;
		String errAvagstnStatus = null;

		// Additional in DigiGST (Not available in A/P GSTR1)
		Long addNotAvaeinvcount = 0L;
		BigDecimal addNotAvaeinvpercentage = BigDecimal.ZERO;
		BigDecimal addNotAvaeinvtaxablevalue = BigDecimal.ZERO;
		BigDecimal addNotAvaeinvtotaltax = BigDecimal.ZERO;
		Long addNotAvasalesRegcount = 0L;
		BigDecimal addNotAvasalesRegpercentage = BigDecimal.ZERO;
		BigDecimal addNotAvasalesRegtaxablevalue = BigDecimal.ZERO;
		BigDecimal addNotAvasalesRegtotaltax = BigDecimal.ZERO;

		String addNotAvadigiStatus = null;
		String addNotAvagstnStatus = null;

		// Available in DigiGST & Available in A/P GSTR1
		Long addAvaeinvcount = 0L;
		BigDecimal addAvaeinvpercentage = BigDecimal.ZERO;
		BigDecimal addAvaeinvtaxablevalue = BigDecimal.ZERO;
		BigDecimal addAvaeinvtotaltax = BigDecimal.ZERO;
		Long addAvasalesRegcount = 0L;
		BigDecimal addAvasalesRegpercentage = BigDecimal.ZERO;
		BigDecimal addAvasalesRegtaxablevalue = BigDecimal.ZERO;
		BigDecimal addAvasalesRegtotaltax = BigDecimal.ZERO;

		String addAvadigiStatus = null;
		String addAvagstnStatus = null;

		// Deletion / Deletion Failed status in A/P GSTR1
		Long deletioneinvcount = 0L;
		BigDecimal deletioneinvpercentage = BigDecimal.ZERO;
		BigDecimal deletioneinvtaxablevalue = BigDecimal.ZERO;
		BigDecimal deletioneinvtotaltax = BigDecimal.ZERO;
		Long deletionsalesRegcount = 0L;
		BigDecimal deletionsalesRegpercentage = BigDecimal.ZERO;
		BigDecimal deletionsalesRegtaxablevalue = BigDecimal.ZERO;
		BigDecimal deletionsalesRegtotaltax = BigDecimal.ZERO;

		String deletiondigiStatus = null;
		String deletiongstnStatus = null;

		List<PostReconSummaryDto> obj = new ArrayList<>();
		final String level = "L1";

		LOGGER.debug("Calculate value for level 1 and level 2 started");

		try {
			for (PostReconSummaryDto resp : retList) {

				PostReconSummaryDto postReconDto = new PostReconSummaryDto();

				if (sections.get(1).equalsIgnoreCase(resp.getSection())) {
					additionalapeinvcount = additionalapeinvcount
							+ resp.getEinvcount();
					additionalapeinvpercentage = additionalapeinvpercentage
							.add(resp.getEinvpercentage());
					additionalapeinvtaxablevalue = additionalapeinvtaxablevalue
							.add(resp.getEinvtaxablevalue());
					additionalapeinvtotaltax = additionalapeinvtotaltax
							.add(resp.getEinvtotaltax());
					additionalapsalesRegcount = additionalapsalesRegcount
							+ resp.getSalesRegcount();
					additionalapsalesRegpercentage = additionalapsalesRegpercentage
							.add(resp.getSalesRegpercentage());
					additionalapsalesRegtaxablevalue = additionalapsalesRegtaxablevalue
							.add(resp.getSalesRegtaxablevalue());
					additionalapsalesRegtotaltax = additionalapsalesRegtotaltax
							.add(resp.getSalesRegtotaltax());
					additionaldigiStatus = resp.getDigigstStatus();
					additionalgstnStatus = resp.getGstnStatus();

				} else if (sections.get(2)
						.equalsIgnoreCase(resp.getSection())) {
					errNotAvaeinvcount = errNotAvaeinvcount
							+ resp.getEinvcount();
					errNotAvaeinvpercentage = errNotAvaeinvpercentage
							.add(resp.getEinvpercentage());
					errNotAvaeinvtaxablevalue = errNotAvaeinvtaxablevalue
							.add(resp.getEinvtaxablevalue());
					errNotAvaeinvtotaltax = errNotAvaeinvtotaltax
							.add(resp.getEinvtotaltax());
					errNotAvasalesRegcount = errNotAvasalesRegcount
							+ resp.getSalesRegcount();
					errNotAvasalesRegpercentage = errNotAvasalesRegpercentage
							.add(resp.getSalesRegpercentage());
					errNotAvasalesRegtaxablevalue = errNotAvasalesRegtaxablevalue
							.add(resp.getSalesRegtaxablevalue());
					errNotAvasalesRegtotaltax = errNotAvaeinvtotaltax
							.add(resp.getSalesRegtotaltax());
					errNotAvadigiStatus = resp.getDigigstStatus();
					errNotAvagstnStatus = resp.getGstnStatus();
				} else if (sections.get(3)
						.equalsIgnoreCase(resp.getSection())) {
					errAvaeinvcount = errAvaeinvcount + resp.getEinvcount();
					errAvaeinvpercentage = errAvaeinvpercentage
							.add(resp.getEinvpercentage());
					errAvaeinvtaxablevalue = errAvaeinvtaxablevalue
							.add(resp.getEinvtaxablevalue());
					errAvaeinvtotaltax = errAvaeinvtotaltax
							.add(resp.getEinvtotaltax());
					errAvasalesRegcount = errAvasalesRegcount
							+ resp.getSalesRegcount();
					errAvasalesRegpercentage = errAvasalesRegpercentage
							.add(resp.getSalesRegpercentage());
					errAvasalesRegtaxablevalue = errAvasalesRegtaxablevalue
							.add(resp.getSalesRegtaxablevalue());
					errAvasalesRegtotaltax = errAvaeinvtotaltax
							.add(resp.getSalesRegtotaltax());
					errAvadigiStatus = resp.getDigigstStatus();
					errAvagstnStatus = resp.getGstnStatus();
				} else if (sections.get(4)
						.equalsIgnoreCase(resp.getSection())) {
					addNotAvaeinvcount = addNotAvaeinvcount
							+ resp.getEinvcount();
					addNotAvaeinvpercentage = addNotAvaeinvpercentage
							.add(resp.getEinvpercentage());
					addNotAvaeinvtaxablevalue = addNotAvaeinvtaxablevalue
							.add(resp.getEinvtaxablevalue());
					addNotAvaeinvtotaltax = addNotAvaeinvtotaltax
							.add(resp.getEinvtotaltax());
					addNotAvasalesRegcount = addNotAvasalesRegcount
							+ resp.getSalesRegcount();
					addNotAvasalesRegpercentage = addNotAvasalesRegpercentage
							.add(resp.getSalesRegpercentage());
					addNotAvasalesRegtaxablevalue = addNotAvasalesRegtaxablevalue
							.add(resp.getSalesRegtaxablevalue());
					addNotAvasalesRegtotaltax = addNotAvaeinvtotaltax
							.add(resp.getSalesRegtotaltax());
					addNotAvadigiStatus = resp.getDigigstStatus();
					addNotAvagstnStatus = resp.getGstnStatus();

				} else if (sections.get(5)
						.equalsIgnoreCase(resp.getSection())) {
					addAvaeinvcount = addAvaeinvcount + resp.getEinvcount();
					addAvaeinvpercentage = addAvaeinvpercentage
							.add(resp.getEinvpercentage());
					addAvaeinvtaxablevalue = addAvaeinvtaxablevalue
							.add(resp.getEinvtaxablevalue());
					addAvaeinvtotaltax = addAvaeinvtotaltax
							.add(resp.getEinvtotaltax());
					addAvasalesRegcount = addAvasalesRegcount
							+ resp.getSalesRegcount();
					addAvasalesRegpercentage = addAvasalesRegpercentage
							.add(resp.getSalesRegpercentage());
					addAvasalesRegtaxablevalue = addAvasalesRegtaxablevalue
							.add(resp.getSalesRegtaxablevalue());
					addAvasalesRegtotaltax = addAvaeinvtotaltax
							.add(resp.getSalesRegtotaltax());
					addAvadigiStatus = resp.getDigigstStatus();
					addAvagstnStatus = resp.getGstnStatus();

				} else if (sections.get(6)
						.equalsIgnoreCase(resp.getSection())) {
					deletioneinvcount = deletioneinvcount + resp.getEinvcount();
					deletioneinvpercentage = deletioneinvpercentage
							.add(resp.getEinvpercentage());
					deletioneinvtaxablevalue = deletioneinvtaxablevalue
							.add(resp.getEinvtaxablevalue());
					deletioneinvtotaltax = deletioneinvtotaltax
							.add(resp.getEinvtotaltax());
					deletionsalesRegcount = deletionsalesRegcount
							+ resp.getSalesRegcount();
					deletionsalesRegpercentage = deletionsalesRegpercentage
							.add(resp.getSalesRegpercentage());
					deletionsalesRegtaxablevalue = deletionsalesRegtaxablevalue
							.add(resp.getSalesRegtaxablevalue());
					deletionsalesRegtotaltax = deletioneinvtotaltax
							.add(resp.getSalesRegtotaltax());
					deletiondigiStatus = resp.getDigigstStatus();
					deletiongstnStatus = resp.getGstnStatus();

				}

				String perticularName = (resp.getParticulars() != null)
						? (String) resp.getParticulars() : "";

				postReconDto.setEinvcount(resp.getEinvcount());
				postReconDto.setEinvpercentage(
						(BigDecimal) resp.getEinvpercentage());
				postReconDto.setEinvtaxablevalue(
						(BigDecimal) resp.getEinvtaxablevalue());
				postReconDto
						.setEinvtotaltax((BigDecimal) resp.getEinvtotaltax());
				postReconDto.setSalesRegcount(resp.getSalesRegcount());
				postReconDto.setSalesRegpercentage(
						(BigDecimal) resp.getSalesRegpercentage());
				postReconDto.setSalesRegtaxablevalue(
						(BigDecimal) resp.getSalesRegtaxablevalue());
				postReconDto.setSalesRegtotaltax(
						(BigDecimal) resp.getSalesRegtotaltax());
				postReconDto.setSection(resp.getParticulars());
				postReconDto.setGstnStatus(resp.getGstnStatus());
				postReconDto.setDigigstStatus(resp.getDigigstStatus());
				if (perticularName.equalsIgnoreCase(subSectionMap
						.get(sections.get(1) + "-" + notAvailable))) {
					postReconDto.setOrderPosition("B");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(1) + "-" + cancelled))) {
					postReconDto.setOrderPosition("C");
				} else if (perticularName.equalsIgnoreCase(subSectionMap
						.get(sections.get(2) + "-" + notAvailable))) {
					postReconDto.setOrderPosition("E");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(2) + "-" + cancelled))) {
					postReconDto.setOrderPosition("F");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(3) + "-" + available))) {
					postReconDto.setOrderPosition("H");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(4) + "-" + available))) {
					postReconDto.setOrderPosition("J");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(5) + "-" + available))) {
					postReconDto.setOrderPosition("L");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(6) + "-" + available))) {
					postReconDto.setOrderPosition("N");
				} else if (perticularName.equalsIgnoreCase(
						subSectionMap.get(sections.get(6) + "-" + cancelled))) {
					postReconDto.setOrderPosition("O");
				} else if (perticularName.equalsIgnoreCase(subSectionMap
						.get(sections.get(6) + "-" + notAvailable))) {
					postReconDto.setOrderPosition("P");
				}
				obj.add(postReconDto);
			}

			LOGGER.debug("Calculate value for level 1 and level 2 ended");
			LOGGER.debug("Setting values for level 1 started");

			for (int i = 0; i < particular.size(); i++) {
				PostReconSummaryDto postReconDto = new PostReconSummaryDto();
				if (sections.get(1).equalsIgnoreCase(particular.get(i))) {
					postReconDto.setSection(particular.get(i));
					postReconDto.setEinvcount(additionalapeinvcount);
					postReconDto.setEinvpercentage(additionalapeinvpercentage);
					postReconDto
							.setEinvtaxablevalue(additionalapeinvtaxablevalue);
					postReconDto.setEinvtotaltax(additionalapeinvtotaltax);
					postReconDto.setSalesRegcount(additionalapsalesRegcount);
					postReconDto.setSalesRegpercentage(
							additionalapsalesRegpercentage);
					postReconDto.setSalesRegtaxablevalue(
							additionalapsalesRegtaxablevalue);
					postReconDto
							.setSalesRegtotaltax(additionalapsalesRegtotaltax);
					postReconDto.setLevel(level);
					postReconDto.setDigigstStatus(additionaldigiStatus);
					postReconDto.setGstnStatus(additionalgstnStatus);
					postReconDto.setOrderPosition("A");
				} else if (sections.get(2)
						.equalsIgnoreCase(particular.get(i))) {
					postReconDto.setSection(particular.get(i));
					postReconDto.setEinvcount(errNotAvaeinvcount);
					postReconDto.setEinvpercentage(errNotAvaeinvpercentage);
					postReconDto.setEinvtaxablevalue(errNotAvaeinvtaxablevalue);
					postReconDto.setEinvtotaltax(errNotAvaeinvtotaltax);
					postReconDto.setSalesRegcount(errNotAvasalesRegcount);
					postReconDto
							.setSalesRegpercentage(errNotAvasalesRegpercentage);
					postReconDto.setSalesRegtaxablevalue(
							errNotAvasalesRegtaxablevalue);
					postReconDto.setSalesRegtotaltax(errNotAvasalesRegtotaltax);
					postReconDto.setLevel(level);
					postReconDto.setDigigstStatus(errNotAvadigiStatus);
					postReconDto.setGstnStatus(errNotAvagstnStatus);
					postReconDto.setOrderPosition("D");
				} else if (sections.get(3)
						.equalsIgnoreCase(particular.get(i))) {
					postReconDto.setSection(particular.get(i));
					postReconDto.setEinvcount(errAvaeinvcount);
					postReconDto.setEinvpercentage(errAvaeinvpercentage);
					postReconDto.setEinvtaxablevalue(errAvaeinvtaxablevalue);
					postReconDto.setEinvtotaltax(errAvaeinvtotaltax);
					postReconDto.setSalesRegcount(errAvasalesRegcount);
					postReconDto
							.setSalesRegpercentage(errAvasalesRegpercentage);
					postReconDto.setSalesRegtaxablevalue(
							errAvasalesRegtaxablevalue);
					postReconDto.setSalesRegtotaltax(errAvasalesRegtotaltax);
					postReconDto.setLevel(level);
					postReconDto.setDigigstStatus(errAvadigiStatus);
					postReconDto.setGstnStatus(errAvagstnStatus);
					postReconDto.setOrderPosition("G");
				} else if (sections.get(4)
						.equalsIgnoreCase(particular.get(i))) {
					postReconDto.setSection(particular.get(i));
					postReconDto.setEinvcount(addNotAvaeinvcount);
					postReconDto.setEinvpercentage(addNotAvaeinvpercentage);
					postReconDto.setEinvtaxablevalue(addNotAvaeinvtaxablevalue);
					postReconDto.setEinvtotaltax(addNotAvaeinvtotaltax);
					postReconDto.setSalesRegcount(addNotAvasalesRegcount);
					postReconDto
							.setSalesRegpercentage(addNotAvasalesRegpercentage);
					postReconDto.setSalesRegtaxablevalue(
							addNotAvasalesRegtaxablevalue);
					postReconDto.setSalesRegtotaltax(addNotAvasalesRegtotaltax);
					postReconDto.setLevel(level);
					postReconDto.setDigigstStatus(addNotAvadigiStatus);
					postReconDto.setGstnStatus(addNotAvagstnStatus);
					postReconDto.setOrderPosition("I");
				} else if (sections.get(5)
						.equalsIgnoreCase(particular.get(i))) {
					postReconDto.setSection(particular.get(i));
					postReconDto.setEinvcount(addAvaeinvcount);
					postReconDto.setEinvpercentage(addAvaeinvpercentage);
					postReconDto.setEinvtaxablevalue(addAvaeinvtaxablevalue);
					postReconDto.setEinvtotaltax(addAvaeinvtotaltax);
					postReconDto.setSalesRegcount(addAvasalesRegcount);
					postReconDto
							.setSalesRegpercentage(addAvasalesRegpercentage);
					postReconDto.setSalesRegtaxablevalue(
							addAvasalesRegtaxablevalue);
					postReconDto.setSalesRegtotaltax(addAvasalesRegtotaltax);
					postReconDto.setLevel(level);
					postReconDto.setDigigstStatus(addAvadigiStatus);
					postReconDto.setGstnStatus(addAvagstnStatus);
					postReconDto.setOrderPosition("K");
				} else if (sections.get(6)
						.equalsIgnoreCase(particular.get(i))) {
					postReconDto.setSection(particular.get(i));
					postReconDto.setEinvcount(deletioneinvcount);
					postReconDto.setEinvpercentage(deletioneinvpercentage);
					postReconDto.setEinvtaxablevalue(deletioneinvtaxablevalue);
					postReconDto.setEinvtotaltax(deletioneinvtotaltax);
					postReconDto.setSalesRegcount(deletionsalesRegcount);
					postReconDto
							.setSalesRegpercentage(deletionsalesRegpercentage);
					postReconDto.setSalesRegtaxablevalue(
							deletionsalesRegtaxablevalue);
					postReconDto.setSalesRegtotaltax(deletionsalesRegtotaltax);
					postReconDto.setLevel(level);
					postReconDto.setDigigstStatus(deletiondigiStatus);
					postReconDto.setGstnStatus(deletiongstnStatus);
					postReconDto.setOrderPosition("M");
				}
				obj.add(postReconDto);
			}

			return obj;
		} catch (Exception e) {

			String msg = String.format(
					"Error while converting the PostReconSummary to ScreenLevel Data");
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage());
		}
	}

	private String getSubSectionBasedCateg(String digiStatus,
			String reportCategory) {

		String subSection = "";
		if (sections.get(1).equalsIgnoreCase(reportCategory)) {
			if (notAvailable.equalsIgnoreCase(digiStatus)) {
				subSection = subSectionMap
						.get(sections.get(1) + "-" + notAvailable);
			} else {
				subSection = subSectionMap
						.get(sections.get(1) + "-" + cancelled);
			}
		} else if (sections.get(2).equalsIgnoreCase(reportCategory)) {
			if (notAvailable.equalsIgnoreCase(digiStatus)) {
				subSection = subSectionMap
						.get(sections.get(2) + "-" + notAvailable);
			} else {
				subSection = subSectionMap
						.get(sections.get(2) + "-" + cancelled);
			}
		} else if (sections.get(3).equalsIgnoreCase(reportCategory)) {
			subSection = subSectionMap.get(sections.get(3) + "-" + available);

		} else if (sections.get(4).equalsIgnoreCase(reportCategory)) {
			subSection = subSectionMap.get(sections.get(4) + "-" + available);
		} else if (sections.get(5).equalsIgnoreCase(reportCategory)) {
			subSection = subSectionMap.get(sections.get(5) + "-" + available);
		} else if (sections.get(6).equalsIgnoreCase(reportCategory)) {
			if (available.equalsIgnoreCase(digiStatus)) {
				subSection = subSectionMap
						.get(sections.get(6) + "-" + available);
			} else if (cancelled.equalsIgnoreCase(digiStatus)) {
				subSection = subSectionMap
						.get(sections.get(6) + "-" + cancelled);
			} else {
				subSection = subSectionMap
						.get(sections.get(6) + "-" + notAvailable);
			}
		}
		return subSection;
	}

	@Override
	public Workbook getPostReconReport(List<String> recipientGstins,
			String taxPeriod, String entityId) {
		Long totalEinvCount = 0L;

		Long salesEinvCount = 0L;
		try {
			Workbook workbook = null;

			List<Object[]> objList = postReconSummaryDao
					.getPostReconSummaryData(recipientGstins,
							Integer.valueOf(taxPeriod));

			LOGGER.debug("Response from Proc {} for Gstin {} and TaxPeriod {}",
					objList, recipientGstins, taxPeriod);

			if (objList.isEmpty()) {
				String msg = String.format(
						"No Data Available" + "  for recipientGstins :%s"
								+ " TaxPeriod :%s ",
						recipientGstins, taxPeriod);
				LOGGER.error(msg);
				String appMsg = "No Data Available for Selected Gstins and TaxPeriod";
				throw new AppException(appMsg);
			}
			List<PostReconSummaryDto> retList = objList.stream()
					.map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			LOGGER.debug("Converting Query And converting to List END");

			for (PostReconSummaryDto resp : retList) {

				totalEinvCount = resp.getEinvcount() + totalEinvCount;

				salesEinvCount = resp.getSalesRegcount() + salesEinvCount;

			}
			List<PostReconSummaryDto> extractPercentage = calculatePercentage(
					retList, totalEinvCount, salesEinvCount);

			workbook = writeToExcel(extractPercentage, entityId, taxPeriod);

			return workbook;
		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing getReconSummaryDetails  "
							+ "  for recipientGstins :%s" + " TaxPeriod :%s ",
					recipientGstins, taxPeriod);
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());
		}
	}

	private Workbook writeToExcel(List<PostReconSummaryDto> retList,
			String entityId, String taxPeriod) {

		Workbook workbook = null;
		int startRow = 6;
		int startcolumn = 1;
		boolean isHeaderRequired = false;

		try {

			String getMonthInWords = GenUtil
					.getMonthinWordsFromNumber(taxPeriod.substring(4));

			String extractYear = taxPeriod.substring(0, 4);

			Long entityIds = Long.valueOf(entityId);

			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityIds);
			EntityInfoEntity entity = optional.get();
			String entityName = entity.getEntityName();

			if (retList != null && !retList.isEmpty()) {

				String[] invoiceHeaders = commonUtility
						.getProp("post.recon.summary.report.header").split(",");

				String fileName = "Post_Recon_Summary_Template.xlsx";
				workbook = commonUtility.createWorkbookWithExcelTemplate(
						"ReportTemplates", fileName);
				if (LOGGER.isDebugEnabled()) {
					String msg = "Post_Recon_Summary_.writeToExcel "
							+ "workbook created writing data to the workbook";
					LOGGER.debug(msg);
				}

				Worksheet sheet = workbook.getWorksheets().get(0);

				Cells reportCells = workbook.getWorksheets().get(0).getCells();

				sheet.setGridlinesVisible(false);

				Cell cellB2 = sheet.getCells().get("B3");

				LocalDateTime now = LocalDateTime.now();
				LocalDateTime istCreatedDate = EYDateUtil
						.toISTDateTimeFromUTC(now);

				String formatDate = istCreatedDate.toLocalDate().format(format);

				String formatTime = istCreatedDate.toLocalTime()
						.format(timeFormat);

				cellB2.setValue("ENTITY NAME - " + entityName + " | " + "Date -"
						+ formatDate + " | " + "Time -" + formatTime + "|"
						+ "Tax Period - " + getMonthInWords + " "
						+ extractYear);

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(true);
				font.setSize(12);
				reportCells.importCustomObjects(retList, invoiceHeaders,
						isHeaderRequired, startRow, startcolumn, retList.size(),
						true, "yyyy-mm-dd", false);

				if (LOGGER.isDebugEnabled()) {
					String msg = "Post_Recon_Summary_Download.writeToExcel "
							+ "saving workbook";
					LOGGER.debug(msg);
				}
				workbook.save(ConfigConstants.POSTRECONSUMMARY,
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
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}
	}
}
