/**
 * 
 */
package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9DigiComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9GetCallComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9DigiComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetCallComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.docs.dto.gstr9.GSTR9ReportConvertUtil;
import com.ey.advisory.app.docs.dto.gstr9.GSTR9ReportDownloadDto;
import com.ey.advisory.app.docs.dto.gstr9.GSTR9ReportDownloadStringDto;
import com.ey.advisory.app.docs.dto.gstr9.GSTR9TaxPaidReportDownloadDto;
import com.ey.advisory.app.docs.dto.gstr9.GSTR9TaxPaidReportDownloadInnerDto;
import com.ey.advisory.app.docs.dto.gstr9.GSTR9TaxPaidReportStringDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9DownloadInnerDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnReportDownloadDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnReportDownloadStringDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("GSTR9ReportDownloadServiceImpl")
public class GSTR9ReportDownloadServiceImpl
		implements GSTR9ReportDownloadService {

	@Autowired
	@Qualifier("Gstr9HsnProcessedRepository")
	private Gstr9HsnProcessedRepository hsnRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr9UserInputRepository")
	private Gstr9UserInputRepository gstr9UserInputRepository;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepository;

	@Autowired
	@Qualifier("Gstr9AutoCalculateRepository")
	private Gstr9AutoCalculateRepository gstr9AutoCalculateRepository;

	@Autowired
	@Qualifier("GSTR9ReportFormulaCalculationService")
	private GSTR9ReportFormulaCalculationService formulaService;

	@Autowired
	@Qualifier("Gstr9GetCallComputeRepository")
	private Gstr9GetCallComputeRepository gstr9GetCallComputRepo;
	
	@Autowired
	private Gstr9DigiComputeRepository gstr9DigiComputeRepository;
	
	@Autowired
	@Qualifier("GSTR9ReportConvertUtil")
	private GSTR9ReportConvertUtil convertUtil;

	private static ImmutableList<String> section = ImmutableList.of("4", "5",
			"6", "7", "8", "10", "11", "12", "13", "15", "16");

	private static ImmutableList<String> sectionTaxPaid = ImmutableList.of("9",
			"14");

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final BigDecimal zeroVal = BigDecimal.ZERO;

	@Override
	public Workbook getData(String gstin, String taxPeriod, String entityId,
			String finYear, Integer fy) {

		List<GSTR9ReportDownloadDto> respList = null;
		Workbook workbook = null;

		List<String> subSectionList = new ArrayList<>(Arrays.asList("4A", "4B",
				"4C", "4D", "4E", "4F", "4G", "4G1" , "4H", "4I", "4J", "4K", "4L",
				"4M", "4N", "5A", "5B", "5C", "5C1" , "5D", "5E", "5F", "5G", "5H",
				"5I", "5J", "5K", "5L", "5M", "5N", "6A", "6B1", "6B2", "6B3",
				"6C1", "6C2", "6C3", "6D1", "6D2", "6D3", "6E1", "6E2", "6F",
				"6G", "6H", "6I", "6J", "6K", "6L", "6M", "6N", "6O", "7A",
				"7B", "7C", "7D", "7E", "7F", "7G",
				"7H", /* "7H1", "7H2", "7H3", */
				"7I", "7J", "8A", "8B", "8C", "8D", "8E", "8F", "8G", "8H",
				"8I", "8J", "8K", "10", "11", "12", "13", "15A", "15B", "15C",
				"15D", "15E", "15F", "15G", "16A", "16B", "16C"));

		try {
			
			List<Gstr9DigiComputeEntity> digiComputeArr = gstr9DigiComputeRepository
	                .findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(gstin, section, taxPeriod);

	        List<Gstr9DownloadInnerDto> digiComputeList = digiComputeArr.stream()
	                .map(o -> convertToDigiComputeDto(o))
	                .collect(Collectors.toCollection(ArrayList::new));
	        
	        if (digiComputeList != null) {
	        	digiComputeList = formulaService.getFormulaValues(digiComputeList);
	        	LOGGER.debug("Digi Compute List after formula processing: {} "
	        			 + digiComputeList);
			}

			List<Gstr9AutoCalculateEntity> autoCalArr = gstr9AutoCalculateRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, section);

			List<Gstr9DownloadInnerDto> autoCalList = autoCalArr.stream()
					.map(o -> convertToAutoDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (autoCalList != null) {

				autoCalList = formulaService.getFormulaValues(autoCalList);
			}

			List<Gstr9UserInputEntity> userInputArr = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin, finYear,
							section);

			List<Gstr9DownloadInnerDto> userInputList = userInputArr.stream()
					.map(o -> convertToUserDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			List<String> userSubSectionList = userInputList.stream()
					.map(o -> o.getSubSection())
					.collect(Collectors.toCollection(ArrayList::new));

			if (!userSubSectionList.contains("6A")
					&& !userSubSectionList.contains("8A")) {

				List<Gstr9DownloadInnerDto> gstnCal6Aand8AList = autoCalList
						.stream()
						.filter(o -> o.getSubSection().equalsIgnoreCase("6A")
								|| o.getSubSection().equalsIgnoreCase("8A"))
						.collect(Collectors.toCollection(ArrayList::new));

				// 6A and 8A (User edited ) = 6A and 8A (Gstn Computed)
				userInputList.addAll(gstnCal6Aand8AList);
			}
			// TO DO formulas
			if (userInputList != null) {

				userInputList = formulaService.getFormulaValues(userInputList);
			}

			List<Gstr9DownloadInnerDto> userSectionList = userInputList.stream()
					.filter(o -> o.getSubSection().equalsIgnoreCase("7H"))
					.collect(Collectors.toCollection(ArrayList::new));
			
			int j = 1;
			List<String>  desc = new ArrayList<>();
			//7-H
			for (Gstr9DownloadInnerDto section7H : userSectionList) {
				
				section7H.setSubSection("7H" + j);
				String descp = section7H.getDescption();
				desc.add(descp);
				j++;
				
			}
			if (userSectionList != null && !userSectionList.isEmpty()) {
				Gstr9DownloadInnerDto sum7H = userSectionList.stream().collect(
						Collectors.reducing(new Gstr9DownloadInnerDto(),
								(acc, ele) -> acc.merge(ele)));

				userInputList.add(sum7H);
			}
			int userH7Count = userSectionList != null ? userSectionList.size()
					: 0;
			
			Map<String, Gstr9DownloadInnerDto> userInputMap = userInputList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("GSTR9ReportDownloadServiceImpl"
						+ ".userInputList   size = " + userInputArr.size()
						+ " now fetching data from getSummary table");
			}

			List<Gstr9GetSummaryEntity> getSummaryArr = gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, section);

			List<Gstr9DownloadInnerDto> getSummaryList = getSummaryArr.stream()
					.map(o -> convertToGstnDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (getSummaryList != null) {
				getSummaryList = formulaService
						.getFormulaValues(getSummaryList);
			}

			List<Gstr9DownloadInnerDto> gstnSectionList = getSummaryList
					.stream().filter(o -> o.getSubSection().startsWith("7H"))
					.collect(Collectors.toCollection(ArrayList::new));
			
			int gstnH7Count = gstnSectionList != null ? gstnSectionList.size()
					: 0;

			int countH7 = gstnH7Count > userH7Count ? gstnH7Count : userH7Count;

			Map<String, Gstr9DownloadInnerDto> getSummaryMap = getSummaryList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDtoForDigiGst(a, b)),
									Optional::get)));

			List<Gstr9DownloadInnerDto> autoCalSectionList = autoCalList
					.stream().filter(o -> o.getSubSection().startsWith("7H"))
					.collect(Collectors.toCollection(ArrayList::new));

			int autoCalH7Count = autoCalSectionList != null
					? autoCalSectionList.size() : 0;

			countH7 = autoCalH7Count > countH7 ? autoCalH7Count : countH7;

			Map<String, GSTR9ReportDownloadDto> gstr9Map = getGstr9SubsectionMap(
					gstin, countH7, desc);

			// checking for H7 subsection
			if (countH7 > 1) {

				for (int i = 1; i < countH7 +1; i++) {

					String h7 = "7H" + i;
					subSectionList.add(h7);

				}
			}

			Collections.sort(subSectionList, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {

					return o1.compareTo(o2);
				}

			});

			List<Gstr9DownloadInnerDto> userInputListAll = subSectionList
					.stream().map(o -> createAllList(o, userInputMap))
					.collect(Collectors.toCollection(ArrayList::new));

			/*
			 * Collections.sort(userInputListAll, new
			 * Comparator<Gstr9DownloadInnerDto>() {
			 * 
			 * @Override public int compare(Gstr9DownloadInnerDto o1,
			 * Gstr9DownloadInnerDto o2) {
			 * 
			 * return o1.getSubSection() .compareTo(o2.getSubSection()); }
			 * 
			 * });
			 */

			List<Gstr9DownloadInnerDto> gstnSumryListAll = subSectionList
					.stream().map(o -> createAllList(o, getSummaryMap))
					.collect(Collectors.toCollection(ArrayList::new));

			/*
			 * * Collections.sort(gstnSumryListAll, new
			 * Comparator<Gstr9DownloadInnerDto>() {
			 * 
			 * @Override public int compare(Gstr9DownloadInnerDto o1,
			 * Gstr9DownloadInnerDto o2) {
			 * 
			 * return o1.getSubSection() .compareTo(o2.getSubSection()); }
			 * 
			 * });
			 */

			List<Gstr9DownloadInnerDto> diffList = new ArrayList<>();

			int userSize = userInputListAll.size();

			for (int i = 0; i < userSize; i++) {

				Gstr9DownloadInnerDto diffDto = getDifferenceDto(
						userInputListAll.get(i), gstnSumryListAll.get(i));
				diffList.add(diffDto);
			}

			// gstr9Get call compute gstr3b & gstr1 filing

			List<Gstr9GetCallComputeEntity> getFilingArr = gstr9GetCallComputRepo
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, fy, section);

			List<Gstr9DownloadInnerDto> getFilingList = getFilingArr.stream()
					.map(o -> convertToGstnFilingDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (getFilingList != null) {

				getFilingList = formulaService.getFormulaValues(getFilingList);
			}

			Map<String, Gstr9DownloadInnerDto> getFilingMap = getFilingList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			/*
			 * The below code will merge the dto list by adding the rows which
			 * has same section and makes it map
			 */

			Map<String, Gstr9DownloadInnerDto> autoCalculateMap = autoCalList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));

			Map<String, Gstr9DownloadInnerDto> getDiffMap = diffList.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing((a, b) -> addDto(a, b)),
									Optional::get)));
			
			Map<String, Gstr9DownloadInnerDto> digiComputeMap = digiComputeList.stream()
                    .collect(Collectors.groupingBy(o -> o.getSubSection(),
                            Collectors.collectingAndThen(
                                    Collectors.reducing((a, b) -> addDtoForDigiGst(a, b)),
                                    Optional::get)));

			gstr9Map.replaceAll((k, v) -> {
				if(digiComputeMap.containsKey(k))
					setDigiGstnValues(v, digiComputeMap.get(k));
				if (getFilingMap.containsKey(k))
					setFilingValues(v, getFilingMap.get(k));
				if (autoCalculateMap.containsKey(k))
					setAutoCalValues(v, autoCalculateMap.get(k));
				if (userInputMap.containsKey(k))
					setUserInputValues(v, userInputMap.get(k));
				if (getSummaryMap.containsKey(k))
					setGstnValues(v, getSummaryMap.get(k));
				if (getDiffMap.containsKey(k))
					setDiffValues(v, getDiffMap.get(k));

				return v;
			});

			respList = new ArrayList<>(gstr9Map.values());

			Long entityIds = Long.valueOf(entityId);

			Optional<EntityInfoEntity> optional = entityInfoRepo
					.findById(entityIds);
			EntityInfoEntity entity = optional.get();
			String entityName = entity.getEntityName();

			// Invoking for Taxpaid sheet 2 Data
			List<GSTR9TaxPaidReportDownloadDto> taxPaidData = getTaxPaidData(
					gstin, taxPeriod, fy, finYear);

			// Involing for HsnData
			List<Gstr9HsnReportDownloadDto> hsnData = getHsnData(gstin,
					taxPeriod, finYear);

			workbook = writeToExcel(respList, taxPaidData, hsnData, entityName,
					finYear);

		} catch (Exception e) {
			String msg = String.format(
					"Error occueed in GSTR9ReportDownloadServiceImpl.getData()",
					e);
			LOGGER.error(msg);
			throw new AppException(msg, e);

		}

		return workbook;
	}

	private Map<String, GSTR9ReportDownloadDto> getGstr9SubsectionMap(
			String gstin, int countH7, List<String>  desc) {
		Map<String, GSTR9ReportDownloadDto> map = new LinkedHashMap<>();

		map.put(GSTR9Constants.Table_4A, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4A,
				"Outward - Supplies made to un-registered persons (B2C)"));
		map.put(GSTR9Constants.Table_4B, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4B,
				"Outward - Supplies made to registered persons (B2B)"));
		map.put(GSTR9Constants.Table_4C,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4C,
						"Outward - Zero rated supply (Export) on payment of tax "
								+ "(except supplies to SEZs)"));
		map.put(GSTR9Constants.Table_4D,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4D,
						"Outward - Supply to SEZs on payment of tax"));
		map.put(GSTR9Constants.Table_4E, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4E, "Outward - Deemed Exports"));
		map.put(GSTR9Constants.Table_4F, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4F,
				"Outward - Advances on which tax has been paid but invoice has "
						+ "not been issued (not covered under (A) to (E) above)"));
		map.put(GSTR9Constants.Table_4G,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4G,
						"Inward supplies on which tax is to be paid on reverse "
								+ "charge basis"));
		map.put(GSTR9Constants.Table_4G1, new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4G1,
				"Supplies on which e-commerce operator is required to pay tax as per section 9(5) (including amendments, if any) [E-commerce operator to report] "));
		map.put(GSTR9Constants.Table_4H, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4H, "Outward - Sub-total (A to G1 above)"));
		map.put(GSTR9Constants.Table_4I,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4I,
						"Outward - Credit Notes issued in respect of transactions "
								+ "specified in (B) to (E) above (-)"));
		map.put(GSTR9Constants.Table_4J,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4J,
						"Outward - Debit Notes issued in respect of transactions "
								+ "specified in (B) to (E) above (+)"));
		map.put(GSTR9Constants.Table_4K, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4K,
				"Outward - Supplies / tax declared through Amendments (+)"));
		map.put(GSTR9Constants.Table_4L, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4L,
				"Outward - Supplies / tax reduced through Amendments (-)"));
		map.put(GSTR9Constants.Table_4M, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_4M, "Outward - Sub-total (I to L above)"));
		map.put(GSTR9Constants.Table_4N,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_4N,
						"Outward - Supplies and advances on which tax is to be paid "
								+ "(H + M) above"));

		map.put(GSTR9Constants.Table_5A, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_5A,
				"Outward - Zero rated supply (Export) without payment of tax"));
		map.put(GSTR9Constants.Table_5B,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5B,
						"Outward - Supply to SEZs without payment of tax"));
		map.put(GSTR9Constants.Table_5C,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5C,
						"Outward - Supplies on which tax is to be paid by recipient "
								+ "on reverse charge basis"));
		map.put(GSTR9Constants.Table_5C1, new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5C1,
				"Supplies on which tax is to be paid by e-commerce operators as per section 9(5) [Supplier to report] "));
		map.put(GSTR9Constants.Table_5D, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_5D, "Outward - Exempted"));
		map.put(GSTR9Constants.Table_5E, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_5E, "Outward - Nil Rated"));
		map.put(GSTR9Constants.Table_5F,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5F,
						"Outward - Non-GST supply (includes 'no supply')"));
		map.put(GSTR9Constants.Table_5G, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_5G, "Outward - Sub-total (A to F above)"));
		map.put(GSTR9Constants.Table_5H,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5H,
						"Outward - Credit Notes issued in respect of transactions "
								+ "specified in A to F above (-)"));
		map.put(GSTR9Constants.Table_5I,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5I,
						"Outward - Debit Notes issued in respect of transactions "
								+ "specified in A to F above (+)"));
		map.put(GSTR9Constants.Table_5J,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5J,
						"Outward - Supplies declared through Amendments (+)"));
		map.put(GSTR9Constants.Table_5K,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5K,
						"Outward - Supplies reduced through Amendments (-)"));
		map.put(GSTR9Constants.Table_5L, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_5L, "Outward - Sub-Total (H to K above)"));
		map.put(GSTR9Constants.Table_5M,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5M,
						"Outward - Turnover on which tax is not to be paid  "
								+ "(G + L) above"));
		map.put(GSTR9Constants.Table_5N,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_5N,
						"Outward - Total Turnover (including advances) (4N + 5M - 4G - 4G1) "
								+ "above"));

		map.put(GSTR9InwardConstants.Table_6A, new GSTR9ReportDownloadDto(gstin,
				GSTR9InwardConstants.Table_6A,
				"Inward - Total amount of input tax credit availed through "
						+ "FORM GSTR-3B (Sum total of table 4A of FORM GSTR-3B)"));
		map.put(GSTR9InwardConstants.Table_6B1,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6B1,
						"Inward - Inward supplies (other than imports and inward "
								+ "supplies liable to reverse charge but includes services "
								+ "received from SEZs) - Inputs"));
		map.put(GSTR9InwardConstants.Table_6B2,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6B2,
						"Inward - Inward supplies (other than imports and inward "
								+ "supplies liable to reverse charge but includes services "
								+ "received from SEZs) - Capital Goods"));
		map.put(GSTR9InwardConstants.Table_6B3,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6B3,
						"Inward - Inward supplies (other than imports and inward "
								+ "supplies liable to reverse charge but includes services "
								+ "received from SEZs) - Input Services"));
		map.put(GSTR9InwardConstants.Table_6C1,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6C1,
						"Inward - Inward supplies received from unregistered persons "
								+ "liable to reverse charge  (other than B above) on which tax "
								+ "is paid & ITC availed - Inputs"));
		map.put(GSTR9InwardConstants.Table_6C2,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6C2,
						"Inward - Inward supplies received from unregistered persons "
								+ "liable to reverse charge  (other than B above) on which "
								+ "tax is paid & ITC availed - Capital Goods"));
		map.put(GSTR9InwardConstants.Table_6C3,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6C3,
						"Inward - Inward supplies received from unregistered persons "
								+ "liable to reverse charge  (other than B above) on which tax "
								+ "is paid & ITC availed - Input Services"));
		map.put(GSTR9InwardConstants.Table_6D1,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6D1,
						"Inward - Inward supplies received from registered persons "
								+ "liable to reverse charge (other than B above) on which tax "
								+ "is paid and ITC availed- Inputs"));
		map.put(GSTR9InwardConstants.Table_6D2, new GSTR9ReportDownloadDto(
				gstin, GSTR9InwardConstants.Table_6D2,
				"Inward - Inward supplies received from registered persons "
						+ "liable to reverse charge (other than B above) on which "
						+ "tax is paid and ITC availed- Capital Goods"));
		map.put(GSTR9InwardConstants.Table_6D3,
				new GSTR9ReportDownloadDto(gstin,
						GSTR9InwardConstants.Table_6D3,
						"Inward - Inward supplies received from registered persons "
								+ "liable to reverse charge (other than B above) on which tax "
								+ "is paid and ITC availed- Input Services"));
		map.put(GSTR9InwardConstants.Table_6E1,
				new GSTR9ReportDownloadDto(gstin,
						"'" + GSTR9InwardConstants.Table_6E1,
						"Inward - Import of goods (including supplies from SEZ) - "
								+ "Inputs"));
		map.put(GSTR9InwardConstants.Table_6E2,
				new GSTR9ReportDownloadDto(gstin,
						"'" + GSTR9InwardConstants.Table_6E2,
						"Inward - Import of goods (including supplies from SEZ) - "
								+ "Capital Goods"));
		map.put(GSTR9InwardConstants.Table_6F,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6F,
						"Inward - Import of services (excluding inward supplies from "
								+ "SEZs)"));
		map.put(GSTR9InwardConstants.Table_6G,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6G,
						"Inward - Input Tax  credit received from ISD"));
		map.put(GSTR9InwardConstants.Table_6H,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6H,
						"Inward - Amount of ITC reclaimed (other than B above) under "
								+ "the provisions of the Act"));
		map.put(GSTR9InwardConstants.Table_6I,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6I,
						"Inward - Sub-total (B to H above)"));
		map.put(GSTR9InwardConstants.Table_6J,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6J,
						"Inward - Difference (I - A) above"));
		map.put(GSTR9InwardConstants.Table_6K,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6K,
						"Inward - Transition Credit through TRAN-1 (including "
								+ "revisions if any)"));
		map.put(GSTR9InwardConstants.Table_6L,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6L,
						"Inward - Transition Credit through TRAN-2"));
		map.put(GSTR9InwardConstants.Table_6M, new GSTR9ReportDownloadDto(gstin,
				GSTR9InwardConstants.Table_6M,
				"Inward - Any other ITC availed but not specified above"));
		map.put(GSTR9InwardConstants.Table_6N,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6N,
						"Inward - Sub-total (K to M above)"));
		map.put(GSTR9InwardConstants.Table_6O,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_6O,
						"Inward - Total ITC availed (I + N) above"));
		map.put(GSTR9InwardConstants.Table_7A,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7A,
						"ITC Reversal - As per Rule 37"));
		map.put(GSTR9InwardConstants.Table_7B,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7B,
						"ITC Reversal - As per Rule 39"));
		map.put(GSTR9InwardConstants.Table_7C,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7C,
						"ITC Reversal - As per Rule 42"));
		map.put(GSTR9InwardConstants.Table_7D,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7D,
						"ITC Reversal - As per Rule 43"));
		map.put(GSTR9InwardConstants.Table_7E,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7E,
						"ITC Reversal - As per section 17(5)"));
		map.put(GSTR9InwardConstants.Table_7F,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7F,
						"ITC Reversal - Reversal of TRAN-I credit"));
		map.put(GSTR9InwardConstants.Table_7G,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7G,
						"ITC Reversal - Reversal of TRAN-II credit"));
		map.put(GSTR9InwardConstants.Table_7H,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_7H,
						"ITC Reversal - Other Reversals"));

		// checking for H7 subsection
		if (countH7 > 1) {
			int k = 0;
			for (int i = 1; i < countH7 + 1; i++) {
				map.put(GSTR9InwardConstants.Table_7H + i,
						new GSTR9ReportDownloadDto(gstin,
								GSTR9InwardConstants.Table_7H + i,
								desc.get(k)));
				k++;
			}
		}

		map.put(GSTR9InwardConstants.Table_7I, new GSTR9ReportDownloadDto(gstin,
				GSTR9InwardConstants.Table_7I,
				"ITC Reversal - Total ITC Reversed (Sum of A to H above)"));
		map.put(GSTR9InwardConstants.Table_7J, new GSTR9ReportDownloadDto(gstin,
				GSTR9InwardConstants.Table_7J,
				"ITC Reversal - Net ITC Available for Utilization (6O - 7I)"));
		map.put(GSTR9InwardConstants.Table_8A,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8A,
						"Other ITC Information - ITC as per GSTR-2A (Table 3 & 5 "
								+ "thereof)"));
		map.put(GSTR9InwardConstants.Table_8B,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8B,
						"Other ITC Information - ITC as per sum total 6(B) and 6(H)  "
								+ "above"));
		map.put(GSTR9InwardConstants.Table_8C,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8C,
						"Other ITC Information - ITC on inward supplies (other than "
								+ "imports and inward supplies liable to reverse charge but "
								+ "includes services received from SEZs) received during the "
								+ "financial year but availed in the next financial year upto "
								+ "specified period"));
		map.put(GSTR9InwardConstants.Table_8D,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8D,
						"Other ITC Information - Difference [A-(B+C)]"));
		map.put(GSTR9InwardConstants.Table_8E, new GSTR9ReportDownloadDto(gstin,
				GSTR9InwardConstants.Table_8E,
				"Other ITC Information - ITC available but not availed"));
		map.put(GSTR9InwardConstants.Table_8F, new GSTR9ReportDownloadDto(gstin,
				GSTR9InwardConstants.Table_8F,
				"Other ITC Information - ITC available but ineligible"));
		map.put(GSTR9InwardConstants.Table_8G,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8G,
						"Other ITC Information - IGST paid  on import of goods "
								+ "(including supplies from SEZ)"));
		map.put(GSTR9InwardConstants.Table_8H,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8H,
						"Other ITC Information - IGST credit availed on import of "
								+ "goods (as per 6(E) above)"));
		map.put(GSTR9InwardConstants.Table_8I,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8I,
						"Other ITC Information - Difference (G-H)"));
		map.put(GSTR9InwardConstants.Table_8J,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8J,
						"Other ITC Information - ITC available but not availed on "
								+ "import of goods (Equal to I)"));
		map.put(GSTR9InwardConstants.Table_8K,
				new GSTR9ReportDownloadDto(gstin, GSTR9InwardConstants.Table_8K,
						"Other ITC Information - Total ITC to be lapsed in current "
								+ "financial year (E + F + J)"));

		map.put(Gstr9PyTransInCyConstants.Table_10,
				new GSTR9ReportDownloadDto(gstin,
						Gstr9PyTransInCyConstants.Table_10,
						"Particulars of the transactions for the financial year "
								+ "declared in returns of the next financial year till the "
								+ "specified period - Supplies / tax declared through "
								+ "Amendments (+) (net of debit notes)"));
		map.put(Gstr9PyTransInCyConstants.Table_11,
				new GSTR9ReportDownloadDto(gstin,
						Gstr9PyTransInCyConstants.Table_11,
						"Particulars of the transactions for the financial year "
								+ "declared in returns of the next financial year till the "
								+ "specified period - Supplies / tax reduced through "
								+ "Amendments (-) (net of credit notes)"));
		map.put(Gstr9PyTransInCyConstants.Table_12,
				new GSTR9ReportDownloadDto(gstin,
						Gstr9PyTransInCyConstants.Table_12,
						"Particulars of the transactions for the financial year "
								+ "declared in returns of the next financial year till "
								+ "the specified period - Reversal of ITC availed during "
								+ "previous financial year"));
		map.put(Gstr9PyTransInCyConstants.Table_13,
				new GSTR9ReportDownloadDto(gstin,
						Gstr9PyTransInCyConstants.Table_13,
						"Particulars of the transactions for the financial year "
								+ "declared in returns of the next financial year till "
								+ "the specified period - ITC availed for the previous "
								+ "financial year"));

		map.put(GSTR9Constants.Table_15A,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_15A,
						"Demands and Refunds - Total Refund claimed"));
		map.put(GSTR9Constants.Table_15B,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_15B,
						"Demands and Refunds - Total Refund sanctioned"));
		map.put(GSTR9Constants.Table_15C,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_15C,
						"Demands and Refunds - Total Refund Rejected"));
		map.put(GSTR9Constants.Table_15D,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_15D,
						"Demands and Refunds - Total Refund Pending"));
		map.put(GSTR9Constants.Table_15E,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_15E,
						"Demands and Refunds - Total demand of taxes"));
		map.put(GSTR9Constants.Table_15F, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_15F,
				"Demands and Refunds - Total taxes paid in respect of E above"));
		map.put(GSTR9Constants.Table_15G, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_15G,
				"Demands and Refunds - Total demands pending out of E above"));

		map.put(GSTR9Constants.Table_16A, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_16A,
				"Information on supplies received from composition taxpayers, "
						+ "deemed supply under section 143 and goods sent on approval "
						+ "basis - Supplies received from Composition taxpayers"));
		map.put(GSTR9Constants.Table_16B,
				new GSTR9ReportDownloadDto(gstin, GSTR9Constants.Table_16B,
						"Information on supplies received from composition taxpayers, "
								+ "deemed supply under section 143 and goods sent on approval "
								+ "basis - Deemed supply under section 143"));
		map.put(GSTR9Constants.Table_16C, new GSTR9ReportDownloadDto(gstin,
				GSTR9Constants.Table_16C,
				"Information on supplies received from composition taxpayers, "
						+ "deemed supply under section 143 and goods sent on approval "
						+ "basis - Goods sent on approval basis but not returned"));

		return map;
	}
	
	private Gstr9DownloadInnerDto convertToDigiComputeDto(
			Gstr9DigiComputeEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetCallComputeEntity "
					+ " Gstr9DownloadInnerDto object";
			LOGGER.debug(str);
		}
		Gstr9DownloadInnerDto obj = new Gstr9DownloadInnerDto();
		obj.setSubSection(dto.getSubSection());
		obj.setIgst(dto.getIgstAmount() != null ? dto.getIgstAmount() : zeroVal);
		obj.setCgst(dto.getCgstAmount() != null ? dto.getCgstAmount() : zeroVal);
		obj.setSgst(dto.getSgstAmount() != null ? dto.getSgstAmount() : zeroVal);
		obj.setCess(dto.getCessAmount() != null ? dto.getCessAmount() : zeroVal);
		obj.setInterest(zeroVal);
		obj.setLateFee(zeroVal);
		obj.setOther(zeroVal);
		obj.setPenalty(zeroVal);
		obj.setTaxableValue(dto.getTaxableValue() != null ? dto.getTaxableValue() :zeroVal);
		return obj;
	}

	private Gstr9DownloadInnerDto convertToUserDto(Gstr9UserInputEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9UserInputEntity "
					+ " Gstr9DownloadInnerDto object";
			LOGGER.debug(str);
		}
		Gstr9DownloadInnerDto obj = new Gstr9DownloadInnerDto();
		obj.setSubSection(dto.getSubSection());
		obj.setIgst(dto.getIgst() != null ? dto.getIgst() : zeroVal);
		obj.setCgst(dto.getCgst() != null ? dto.getCgst() : zeroVal);
		obj.setSgst(dto.getSgst() != null ? dto.getSgst() : zeroVal);
		obj.setCess(dto.getCess() != null ? dto.getCess() : zeroVal);
		obj.setInterest(dto.getIntr() != null ? dto.getIntr() : zeroVal);
		obj.setLateFee(dto.getFee() != null ? dto.getFee() : zeroVal);
		obj.setOther(dto.getOth() != null ? dto.getOth() : zeroVal);
		obj.setPenalty(dto.getPen() != null ? dto.getPen() : zeroVal);
		obj.setTaxableValue(dto.getTxVal() != null ? dto.getTxVal() : zeroVal);
		obj.setDescption(dto.getDesc());
		return obj;
	}

	private Gstr9DownloadInnerDto convertToGstnDto(Gstr9GetSummaryEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetSummaryEntity "
					+ " Gstr9DownloadInnerDto object";
			LOGGER.debug(str);
		}
		Gstr9DownloadInnerDto obj = new Gstr9DownloadInnerDto();
		obj.setSubSection(dto.getSubSection());
		obj.setIgst(dto.getIamt() != null ? dto.getIamt() : zeroVal);
		obj.setCgst(dto.getCamt() != null ? dto.getCamt() : zeroVal);
		obj.setSgst(dto.getSamt() != null ? dto.getSamt() : zeroVal);
		obj.setCess(dto.getCsamt() != null ? dto.getCsamt() : zeroVal);
		obj.setInterest(dto.getIntr() != null ? dto.getIntr() : zeroVal);
		obj.setLateFee(dto.getFee() != null ? dto.getFee() : zeroVal);
		obj.setOther(dto.getOth() != null ? dto.getOth() : zeroVal);
		obj.setPenalty(dto.getPen() != null ? dto.getPen() : zeroVal);
		obj.setTaxableValue(dto.getTxVal() != null ? dto.getTxVal() : zeroVal);
		return obj;
	}

	private Gstr9DownloadInnerDto convertToGstnFilingDto(
			Gstr9GetCallComputeEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9GetCallComputeEntity "
					+ " Gstr9DownloadInnerDto object";
			LOGGER.debug(str);
		}
		Gstr9DownloadInnerDto obj = new Gstr9DownloadInnerDto();
		obj.setSubSection(dto.getSubSection());
		obj.setIgst(dto.getIgst() != null ? dto.getIgst() : zeroVal);
		obj.setCgst(dto.getCgst() != null ? dto.getCgst() : zeroVal);
		obj.setSgst(dto.getSgst() != null ? dto.getSgst() : zeroVal);
		obj.setCess(dto.getCess() != null ? dto.getCess() : zeroVal);
		obj.setInterest(dto.getIntr() != null ? dto.getIntr() : zeroVal);
		obj.setLateFee(dto.getFee() != null ? dto.getFee() : zeroVal);
		obj.setOther(dto.getOth() != null ? dto.getOth() : zeroVal);
		obj.setPenalty(dto.getPen() != null ? dto.getPen() : zeroVal);
		obj.setTaxableValue(dto.getTxVal() != null ? dto.getTxVal() : zeroVal);
		return obj;
	}

	private Gstr9DownloadInnerDto convertToAutoDto(
			Gstr9AutoCalculateEntity dto) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting Gstr9AutoCalculateEntity "
					+ " Gstr9DownloadInnerDto object";
			LOGGER.debug(str);
		}
		Gstr9DownloadInnerDto obj = new Gstr9DownloadInnerDto();
		obj.setSubSection(dto.getSubSection());
		obj.setIgst(dto.getIamt() != null ? dto.getIamt() : zeroVal);
		obj.setCgst(dto.getCamt() != null ? dto.getCamt() : zeroVal);
		obj.setSgst(dto.getSamt() != null ? dto.getSamt() : zeroVal);
		obj.setCess(dto.getCsamt() != null ? dto.getCsamt() : zeroVal);
		obj.setInterest(zeroVal);
		obj.setLateFee(zeroVal);
		obj.setOther(zeroVal);
		obj.setPenalty(zeroVal);
		obj.setTaxableValue(dto.getTxVal() != null ? dto.getTxVal() : zeroVal);
		return obj;
	}

	private Gstr9DownloadInnerDto getDifferenceDto(Gstr9DownloadInnerDto user,
			Gstr9DownloadInnerDto gstn) {

		Gstr9DownloadInnerDto diffDto = new Gstr9DownloadInnerDto();
		diffDto.setSubSection("Default");
		if (user.getSubSection() != null && !user.getSubSection().isEmpty()
				&& gstn.getSubSection() != null
				&& !gstn.getSubSection().isEmpty()) {
			if (user.getSubSection().equalsIgnoreCase(gstn.getSubSection())) {
				diffDto.setSubSection(user.getSubSection() != null
						? user.getSubSection() : gstn.getSubSection());
				diffDto.setIgst(user.getIgst().subtract(gstn.getIgst()));
				diffDto.setCgst(user.getCgst().subtract(gstn.getCgst()));
				diffDto.setSgst(user.getSgst().subtract(gstn.getSgst()));
				diffDto.setCess(user.getCess().subtract(gstn.getCess()));
				diffDto.setInterest(
						user.getInterest().subtract(gstn.getInterest()));
				diffDto.setLateFee(
						user.getLateFee().subtract(gstn.getLateFee()));
				diffDto.setOther(user.getOther().subtract(gstn.getOther()));
				diffDto.setPenalty(
						user.getPenalty().subtract(gstn.getPenalty()));
				diffDto.setTaxableValue(user.getTaxableValue()
						.subtract(gstn.getTaxableValue()));
			}
		}
		return diffDto;

	}

	private Gstr9DownloadInnerDto createAllList(String section,
			Map<String, Gstr9DownloadInnerDto> map) {
		Gstr9DownloadInnerDto obj = new Gstr9DownloadInnerDto();
		if (map.containsKey(section)) {

			Gstr9DownloadInnerDto dto = map.get(section);
			obj.setSubSection(dto.getSubSection());
			obj.setIgst(dto.getIgst() != null ? dto.getIgst() : zeroVal);
			obj.setCgst(dto.getCgst() != null ? dto.getCgst() : zeroVal);
			obj.setSgst(dto.getSgst() != null ? dto.getSgst() : zeroVal);
			obj.setCess(dto.getCess() != null ? dto.getCess() : zeroVal);
			obj.setInterest(
					dto.getInterest() != null ? dto.getInterest() : zeroVal);
			obj.setLateFee(
					dto.getLateFee() != null ? dto.getLateFee() : zeroVal);
			obj.setOther(dto.getOther() != null ? dto.getOther() : zeroVal);
			obj.setPenalty(
					dto.getPenalty() != null ? dto.getPenalty() : zeroVal);
			obj.setTaxableValue(dto.getTaxableValue() != null
					? dto.getTaxableValue() : zeroVal);
		} else {
			obj.setSubSection(section);
			obj.setIgst(zeroVal);
			obj.setCgst(zeroVal);
			obj.setSgst(zeroVal);
			obj.setCess(zeroVal);
			obj.setInterest(zeroVal);
			obj.setLateFee(zeroVal);
			obj.setOther(zeroVal);
			obj.setPenalty(zeroVal);
			obj.setTaxableValue(zeroVal);
		}
		return obj;
	}

	private Gstr9DownloadInnerDto addDto(Gstr9DownloadInnerDto a,
			Gstr9DownloadInnerDto b) {
		Gstr9DownloadInnerDto dto = new Gstr9DownloadInnerDto();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		dto.setTaxableValue(a.getTaxableValue().add(b.getTaxableValue()));
		dto.setInterest(a.getInterest().add(b.getInterest()));
		dto.setLateFee(a.getLateFee().add(b.getLateFee()));
		dto.setPenalty(a.getPenalty().add(b.getPenalty()));
		dto.setOther(a.getOther().add(b.getOther()));
		dto.setSubSection(a.getSubSection() != null ? a.getSubSection() 
				: b.getSubSection());

		return dto;
	}
	
	private Gstr9DownloadInnerDto addDtoForDigiGst(Gstr9DownloadInnerDto a, Gstr9DownloadInnerDto b) {
		Gstr9DownloadInnerDto dto = new Gstr9DownloadInnerDto();
		dto.setCess(a.getCess() != null ? a.getCess() : b.getCess());
		dto.setCgst(a.getCgst() != null ? a.getCgst() : b.getCgst());
		dto.setIgst(a.getIgst() != null ? a.getIgst() : b.getIgst());
		dto.setSgst(a.getSgst() != null ? a.getSgst() : b.getSgst());
		dto.setTaxableValue(a.getTaxableValue() != null ? a.getTaxableValue() : b.getTaxableValue());
		dto.setInterest(a.getInterest() != null ? a.getInterest() : b.getInterest());
		dto.setLateFee(a.getLateFee() != null ? a.getLateFee() : b.getLateFee());
		dto.setPenalty(a.getPenalty() != null ? a.getPenalty() : b.getPenalty());
		dto.setOther(a.getOther() != null ? a.getOther() : b.getOther());
		dto.setSubSection(a.getSubSection() != null ? a.getSubSection() : b.getSubSection());

		return dto;
	}
	
	private void setDigiGstnValues(GSTR9ReportDownloadDto gstr9DigiGstnDto,
			Gstr9DownloadInnerDto gstr9InwarDigiGGstnDTO) {
		gstr9DigiGstnDto.setDigiCessProAutoComp(gstr9InwarDigiGGstnDTO.getCess());
		gstr9DigiGstnDto.setDigiCgstProAutoComp(gstr9InwarDigiGGstnDTO.getCgst());
		gstr9DigiGstnDto.setDigiIgstProAutoComp(gstr9InwarDigiGGstnDTO.getIgst());
		gstr9DigiGstnDto.setDigiSgstProAutoComp(gstr9InwarDigiGGstnDTO.getSgst());
		gstr9DigiGstnDto.setDigiTaxableValueProAutoComp(gstr9InwarDigiGGstnDTO.getTaxableValue());
		
		gstr9DigiGstnDto.setDigiOtherProAutoComp(gstr9InwarDigiGGstnDTO.getOther());
		gstr9DigiGstnDto.setDigiPenaltyProAutoComp(gstr9InwarDigiGGstnDTO.getPenalty());
		gstr9DigiGstnDto.setDigiInterestProAutoComp(gstr9InwarDigiGGstnDTO.getInterest());
		gstr9DigiGstnDto.setDigiLateFeeProAutoComp(gstr9InwarDigiGGstnDTO.getLateFee());
	}

	private void setUserInputValues(GSTR9ReportDownloadDto gst9UserInputDto,
			Gstr9DownloadInnerDto Gstr9DownloadInnerDto) {
		gst9UserInputDto.setCessUserInput(Gstr9DownloadInnerDto.getCess());
		gst9UserInputDto.setCgstUserInput(Gstr9DownloadInnerDto.getCgst());
		gst9UserInputDto.setIgstUserInput(Gstr9DownloadInnerDto.getIgst());
		gst9UserInputDto.setSgstUserInput(Gstr9DownloadInnerDto.getSgst());
		gst9UserInputDto.setTaxableValueUserInput(
				Gstr9DownloadInnerDto.getTaxableValue());
		
		gst9UserInputDto.setOtherUserInput(Gstr9DownloadInnerDto.getOther());
		gst9UserInputDto.setPenaltyUserInput(Gstr9DownloadInnerDto.getPenalty());
		gst9UserInputDto.setLateFeeUserInput(Gstr9DownloadInnerDto.getLateFee());
		gst9UserInputDto.setInterestUserInput(Gstr9DownloadInnerDto.getInterest());
		
	}

	private void setFilingValues(GSTR9ReportDownloadDto gstr9FilingDto,
			Gstr9DownloadInnerDto Gstr9DownloadInnerDto) {
		gstr9FilingDto.setCessFiledAutoComp(Gstr9DownloadInnerDto.getCess());
		gstr9FilingDto.setCgstFiledAutoComp(Gstr9DownloadInnerDto.getCgst());
		gstr9FilingDto.setIgstFiledAutoComp(Gstr9DownloadInnerDto.getIgst());
		gstr9FilingDto.setSgstFiledAutoComp(Gstr9DownloadInnerDto.getSgst());
		gstr9FilingDto.setTaxableValueFiledAutoComp(
				Gstr9DownloadInnerDto.getTaxableValue());
		
		gstr9FilingDto.setOtherFiledAutoComp(Gstr9DownloadInnerDto.getOther());
		gstr9FilingDto.setPenaltyFiledAutoComp(Gstr9DownloadInnerDto.getPenalty());
		gstr9FilingDto.setLateFeeFiledAutoComp(Gstr9DownloadInnerDto.getLateFee());
		gstr9FilingDto.setInterestFiledAutoComp(Gstr9DownloadInnerDto.getInterest());

	}

	private void setAutoCalValues(GSTR9ReportDownloadDto gstr9AutoDto,
			Gstr9DownloadInnerDto Gstr9DownloadInnerDto) {
		gstr9AutoDto.setCessAutoCal(Gstr9DownloadInnerDto.getCess());
		gstr9AutoDto.setCgstAutoCal(Gstr9DownloadInnerDto.getCgst());
		gstr9AutoDto.setIgstAutoCal(Gstr9DownloadInnerDto.getIgst());
		gstr9AutoDto.setSgstAutoCal(Gstr9DownloadInnerDto.getSgst());
		gstr9AutoDto.setTaxableValueAutoCal(
				(Gstr9DownloadInnerDto.getTaxableValue()));
		
		gstr9AutoDto.setOtherAutoCal(Gstr9DownloadInnerDto.getOther());
		gstr9AutoDto.setPenaltyAutoCal(Gstr9DownloadInnerDto.getPenalty());
		gstr9AutoDto.setInterestAutoCal(Gstr9DownloadInnerDto.getInterest());
		gstr9AutoDto.setLateFeeAutoCal(Gstr9DownloadInnerDto.getLateFee());
	}

	private void setGstnValues(GSTR9ReportDownloadDto gstr9GstnDto,
			Gstr9DownloadInnerDto gstr9InwarGstnDTO) {
		gstr9GstnDto.setCessGstn(gstr9InwarGstnDTO.getCess());
		gstr9GstnDto.setCgstGstn(gstr9InwarGstnDTO.getCgst());
		gstr9GstnDto.setIgstGstn(gstr9InwarGstnDTO.getIgst());
		gstr9GstnDto.setSgstGstn(gstr9InwarGstnDTO.getSgst());
		gstr9GstnDto.setTaxableValueGstn(gstr9InwarGstnDTO.getTaxableValue());
		
		gstr9GstnDto.setOtherGstn(gstr9InwarGstnDTO.getOther());
		gstr9GstnDto.setPenaltyGstn(gstr9InwarGstnDTO.getPenalty());
		gstr9GstnDto.setInterestGstn(gstr9InwarGstnDTO.getInterest());
		gstr9GstnDto.setLateFeeGstn(gstr9InwarGstnDTO.getLateFee());
	}

	private void setDiffValues(GSTR9ReportDownloadDto gstr9GstnDto,
			Gstr9DownloadInnerDto gstr9InwarGstnDTO) {
		gstr9GstnDto.setCessDiff(gstr9InwarGstnDTO.getCess());
		gstr9GstnDto.setCgstDiff(gstr9InwarGstnDTO.getCgst());
		gstr9GstnDto.setIgstDiff(gstr9InwarGstnDTO.getIgst());
		gstr9GstnDto.setSgstDiff(gstr9InwarGstnDTO.getSgst());
		gstr9GstnDto.setTaxableValueDiff(gstr9InwarGstnDTO.getTaxableValue());
		
		gstr9GstnDto.setOtherDiff(gstr9InwarGstnDTO.getOther());
		gstr9GstnDto.setPenaltyDiff(gstr9InwarGstnDTO.getPenalty());
		gstr9GstnDto.setInterestDiff(gstr9InwarGstnDTO.getInterest());
		gstr9GstnDto.setLateFeeDiff(gstr9InwarGstnDTO.getLateFee());
	}

	private Workbook writeToExcel(List<GSTR9ReportDownloadDto> respList,
			List<GSTR9TaxPaidReportDownloadDto> taxPaidData,
			List<Gstr9HsnReportDownloadDto> hsnData, String entityName,
			String finYear) {
		Workbook workbook = null;
		int startRow = 7;
		int startcolumn = 1;
		boolean isHeaderRequired = false;
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin GSTR9ReportDownloadServiceImpl.writeToExcel "
					+ "List Size = " + respList.size();
			LOGGER.debug(msg);
		}

		if (respList != null && !respList.isEmpty()) {
			
			List<Gstr9HsnReportDownloadStringDto> hsnDataString = convertUtil
					.convertHsn(hsnData);
			List<GSTR9ReportDownloadStringDto> respListString = convertUtil
					.convertMainReport(respList);
			List<GSTR9TaxPaidReportStringDto> taxPaidDataString = convertUtil
					.convertTaxPiad(taxPaidData);

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates",
					"GSTR9_Download Report_From GSTR9 Return Screen.xlsx");
			if (LOGGER.isDebugEnabled()) {
				String msg = "GSTR9ReportDownloadServiceImpl.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			for (int i = 0; i < 3; i++) {

				Cells reportCells = workbook.getWorksheets().get(i).getCells();

				Worksheet sheet = workbook.getWorksheets().get(i);

				Cell cellB2 = sheet.getCells().get("B2");

				cellB2.setValue(entityName + " | " + "GSTR-9 Summary Report");
				Cell cellB3 = sheet.getCells().get("B3");
				cellB3.setValue("FY- " + finYear);

				Cell cellC3 = sheet.getCells().get("C3");
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime istCreatedDate = EYDateUtil
						.toISTDateTimeFromUTC(now);

				String formatDateTime = istCreatedDate.format(format);

				cellC3.setValue("Date & Time of report generation - " + ""
						+ formatDateTime);

				Style style = workbook.createStyle();
				Font font = style.getFont();
				font.setBold(true);
				font.setSize(12);

				String[] invoiceHeaders = null;

				if (i == 0) {
					invoiceHeaders = commonUtility
							.getProp("gstr9.summary.report.header").split(",");
					reportCells.importCustomObjects(respListString,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, respListString.size(), true,
							"yyyy-mm-dd", false);

				}
				if (i == 1 && taxPaidData != null && !taxPaidData.isEmpty()) {
					invoiceHeaders = commonUtility
							.getProp("gstr9.summary.tax.paid.report.header")
							.split(",");
					reportCells.importCustomObjects(taxPaidDataString,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, taxPaidDataString.size(), true,
							"yyyy-mm-dd", false);

				}
				if (i == 2) {
					invoiceHeaders = commonUtility
							.getProp("gstr9.summary.hsn.header").split(",");
					startRow = 5;
					reportCells.importCustomObjects(hsnDataString,
							invoiceHeaders, isHeaderRequired, startRow,
							startcolumn, hsnDataString.size(), true,
							"yyyy-mm-dd", false);

				}

				try {
					if (LOGGER.isDebugEnabled()) {
						String msg = "GSTR9ReportDownloadServiceImpl.writeToExcel "
								+ "saving workbook";
						LOGGER.debug(msg);
					}
					workbook.save(ConfigConstants.GSTR9_SUMMARY_REPORT,
							SaveFormat.XLSX);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Workbook has been generated successfully with the"
										+ " error response list in the directory : %s",
								workbook.getAbsolutePath());
					}
				} catch (Exception e) {
					String msg = String.format(
							"Exception occured while "
									+ "saving excel sheet into folder, %s ",
							e.getMessage());
					LOGGER.error(msg);
					throw new AppException(e.getMessage(), e);
				}

			}
		}

		else {
			throw new AppException("No records found, cannot generate report");
		}
		return workbook;

	}

	private List<GSTR9TaxPaidReportDownloadDto> getTaxPaidData(String gstin,
			String taxPeriod, Integer fy, String finYear) {

		List<GSTR9TaxPaidReportDownloadDto> respList = null;

		List<String> subSectionList = Arrays.asList("9A", "9B", "9C", "9D",
				"9E", "9F", "9G", "9H", "14A", "14B", "14C", "14D", "14E");

		try {

			List<Gstr9GetCallComputeEntity> filingArr = gstr9GetCallComputRepo
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, fy, sectionTaxPaid);

			Map<String, Gstr9GetCallComputeEntity> filingEntityMap = filingArr
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addTaxPaidEntity(a, b)),
									Optional::get)));

			List<GSTR9TaxPaidReportDownloadInnerDto> filingList = new ArrayList<>();
			List<Gstr9GetCallComputeEntity> filingEntityList = new ArrayList<>();

			if (filingEntityMap != null && !filingEntityMap.isEmpty()) {
				filingEntityList = new ArrayList<>(filingEntityMap.values());
			}
			// filingList = convertToTaxPaidFilingDto(filingEntityList);

			//for section 9
			filingList = filingEntityList.stream()
					.map(o -> convertToTaxPaidFilingDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			
			//create for 14(1) and add to filingList.
			List<GSTR9TaxPaidReportDownloadInnerDto> filingList14 = 
					convertTo14FilingDto(filingEntityList);
			
			//adding to list
			filingList.addAll(filingList14);

			Map<String, GSTR9TaxPaidReportDownloadInnerDto> filingMap = filingList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addTaxPaidDto(a, b)),
									Optional::get)));

			List<Gstr9UserInputEntity> userInputArr = gstr9UserInputRepository
					.findByGstinAndFyAndSectionInAndIsActiveTrue(gstin, finYear,
							sectionTaxPaid);

			List<GSTR9TaxPaidReportDownloadInnerDto> userInputList = convertToTaxPaidUserDto(
					userInputArr);

			Map<String, GSTR9TaxPaidReportDownloadInnerDto> userInputMap = userInputList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addTaxPaidDto(a, b)),
									Optional::get)));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("GSTR9ReportDownloadServiceImpl"
						+ ".userInputList   size = " + userInputArr.size()
						+ " now fetching data from getSummary table");
			}

			List<Gstr9GetSummaryEntity> getSummaryArr = gstr9GetSummaryRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, sectionTaxPaid);

			List<GSTR9TaxPaidReportDownloadInnerDto> getSummaryList = getSummaryArr
					.stream().map(o -> convertToTaxPaidGstnDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String, GSTR9TaxPaidReportDownloadInnerDto> getSummaryMap = getSummaryList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addTaxPaidDto(a, b)),
									Optional::get)));

			List<Gstr9AutoCalculateEntity> autoCalArr = gstr9AutoCalculateRepository
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, sectionTaxPaid);

			List<GSTR9TaxPaidReportDownloadInnerDto> autoCalList = autoCalArr
					.stream().map(o -> convertToTaxPaidAutoDto(o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<GSTR9TaxPaidReportDownloadInnerDto> userInputListAll = subSectionList
					.stream().map(o -> createTaxPaidAllList(o, userInputMap))
					.collect(Collectors.toCollection(ArrayList::new));

			Collections.sort(userInputListAll,
					new Comparator<GSTR9TaxPaidReportDownloadInnerDto>() {

						@Override
						public int compare(
								GSTR9TaxPaidReportDownloadInnerDto o1,
								GSTR9TaxPaidReportDownloadInnerDto o2) {

							return o1.getSubSection()
									.compareTo(o2.getSubSection());
						}

					});

			List<GSTR9TaxPaidReportDownloadInnerDto> gstnSumryListAll = subSectionList
					.stream().map(o -> createTaxPaidAllList(o, getSummaryMap))
					.collect(Collectors.toCollection(ArrayList::new));

			Collections.sort(gstnSumryListAll,
					new Comparator<GSTR9TaxPaidReportDownloadInnerDto>() {

						@Override
						public int compare(
								GSTR9TaxPaidReportDownloadInnerDto o1,
								GSTR9TaxPaidReportDownloadInnerDto o2) {

							return o1.getSubSection()
									.compareTo(o2.getSubSection());
						}

					});

			List<GSTR9TaxPaidReportDownloadInnerDto> diffList = new ArrayList<>();

			int userSize = userInputListAll.size();

			for (int i = 0; i < userSize; i++) {

				GSTR9TaxPaidReportDownloadInnerDto diffDto = getTaxPaidDifferenceDto(
						userInputListAll.get(i), gstnSumryListAll.get(i));
				diffList.add(diffDto);
			}

			/*
			 * The below code will merge the dto list by adding the rows which
			 * has same section and makes it map
			 */
			Map<String, GSTR9TaxPaidReportDownloadInnerDto> autoCalculateMap = autoCalList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addTaxPaidDto(a, b)),
									Optional::get)));

			Map<String, GSTR9TaxPaidReportDownloadInnerDto> getDiffMap = diffList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(
									Collectors.reducing(
											(a, b) -> addTaxPaidDto(a, b)),
									Optional::get)));

			Map<String, GSTR9TaxPaidReportDownloadDto> gstr9TaxPaidMap = getGstr9TaxPaidSubsectionMap(
					gstin);

			gstr9TaxPaidMap.replaceAll((k, v) -> {

				if (filingMap.containsKey(k))
					setTaxPaidFilingValues(v, filingMap.get(k));
				if (autoCalculateMap.containsKey(k))
					setTaxPaidAutoCalValues(v, autoCalculateMap.get(k));
				if (userInputMap.containsKey(k))
					setTaxPaidUserInputValues(v, userInputMap.get(k));
				if (getSummaryMap.containsKey(k))
					setTaxPaidGstnValues(v, getSummaryMap.get(k));
				if (getDiffMap.containsKey(k))
					setTaxPaidDiffValues(v, getDiffMap.get(k));

				return v;
			});

			respList = new ArrayList<>(gstr9TaxPaidMap.values());

		} catch (Exception e) {

			String msg = String.format(
					"Error occueed in GSTR9ReportDownloadServiceImpl.getData()",
					e);
			LOGGER.error(msg);
			throw new AppException(msg, e);

		}

		return respList;

	}

	private void setTaxPaidDiffValues(GSTR9TaxPaidReportDownloadDto v,
			GSTR9TaxPaidReportDownloadInnerDto innerDto) {
		v.setPdCashDiff(innerDto.getPdCash());
		v.setPdCessDiff(innerDto.getPdCess());
		v.setPdIgstDiff(innerDto.getPdIgst());
		v.setPdCgstDiff(innerDto.getPdCgst());
		v.setPdSgstDiff(innerDto.getPdSgst());
		v.setTaxPayableDiff(innerDto.getTaxPayable());

	}

	private void setTaxPaidGstnValues(GSTR9TaxPaidReportDownloadDto v,
			GSTR9TaxPaidReportDownloadInnerDto innerDto) {
		v.setPdCashGstn(innerDto.getPdCash());
		v.setPdCessGstn(innerDto.getPdCess());
		v.setPdIgstGstn(innerDto.getPdIgst());
		v.setPdCgstGstn(innerDto.getPdCgst());
		v.setPdSgstGstn(innerDto.getPdSgst());
		v.setTaxPayableGstn(innerDto.getTaxPayable());

	}

	private void setTaxPaidUserInputValues(GSTR9TaxPaidReportDownloadDto v,
			GSTR9TaxPaidReportDownloadInnerDto innerDto) {
		v.setPdCashUserInput(innerDto.getPdCash());
		v.setPdCessUserInput(innerDto.getPdCess());
		v.setPdIgstUserInput(innerDto.getPdIgst());
		v.setPdCgstUserInput(innerDto.getPdCgst());
		v.setPdSgstUserInput(innerDto.getPdSgst());
		v.setTaxPayableUserInput(innerDto.getTaxPayable());

	}

	private void setTaxPaidAutoCalValues(GSTR9TaxPaidReportDownloadDto v,
			GSTR9TaxPaidReportDownloadInnerDto innerDto) {
		v.setPdCashAutoCal(innerDto.getPdCash());
		v.setPdCessAutoCal(innerDto.getPdCess());
		v.setPdIgstAutoCal(innerDto.getPdIgst());
		v.setPdCgstAutoCal(innerDto.getPdCgst());
		v.setPdSgstAutoCal(innerDto.getPdSgst());
		v.setTaxPayableAutoCal(innerDto.getTaxPayable());

	}

	private void setTaxPaidFilingValues(GSTR9TaxPaidReportDownloadDto v,
			GSTR9TaxPaidReportDownloadInnerDto innerDto) {
		v.setPdCashFiledAutoComp(innerDto.getPdCash());
		v.setPdCessFiledAutoComp(innerDto.getPdCess());
		v.setPdIgstFiledAutoComp(innerDto.getPdIgst());
		v.setPdCgstFiledAutoComp(innerDto.getPdCgst());
		v.setPdSgstFiledAutoComp(innerDto.getPdSgst());
		v.setTaxPayableFiledAutoComp(innerDto.getTaxPayable());

	}

	private Map<String, GSTR9TaxPaidReportDownloadDto> getGstr9TaxPaidSubsectionMap(
			String gstin) {

		Map<String, GSTR9TaxPaidReportDownloadDto> map = new LinkedHashMap<>();

		map.put(Gstr9TaxPaidConstants.Table_9A,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9A, "IGST"));
		map.put(Gstr9TaxPaidConstants.Table_9B,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9B, "CGST"));
		map.put(Gstr9TaxPaidConstants.Table_9C,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9C, "SGST"));
		map.put(Gstr9TaxPaidConstants.Table_9D,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9D, "CESS"));
		map.put(Gstr9TaxPaidConstants.Table_9E,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9E, "Interest"));
		map.put(Gstr9TaxPaidConstants.Table_9F,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9F, "Late fee"));
		map.put(Gstr9TaxPaidConstants.Table_9G,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9G, "Penalty"));
		map.put(Gstr9TaxPaidConstants.Table_9H,
				new GSTR9TaxPaidReportDownloadDto(gstin,
						Gstr9TaxPaidConstants.Table_9H, "Other"));

		map.put(GSTR9Constants.Table_14A, new GSTR9TaxPaidReportDownloadDto(
				gstin, GSTR9Constants.Table_14A, "IGST"));
		map.put(GSTR9Constants.Table_14B, new GSTR9TaxPaidReportDownloadDto(
				gstin, GSTR9Constants.Table_14B, "CGST"));
		map.put(GSTR9Constants.Table_14C, new GSTR9TaxPaidReportDownloadDto(
				gstin, GSTR9Constants.Table_14C, "SGST"));
		map.put(GSTR9Constants.Table_14D, new GSTR9TaxPaidReportDownloadDto(
				gstin, GSTR9Constants.Table_14D, "CESS"));
		map.put(GSTR9Constants.Table_14E, new GSTR9TaxPaidReportDownloadDto(
				gstin, GSTR9Constants.Table_14E, "Interest"));

		return map;
	}

	private GSTR9TaxPaidReportDownloadInnerDto getTaxPaidDifferenceDto(
			GSTR9TaxPaidReportDownloadInnerDto a,
			GSTR9TaxPaidReportDownloadInnerDto b) {

		GSTR9TaxPaidReportDownloadInnerDto dto = new GSTR9TaxPaidReportDownloadInnerDto();
		dto.setSubSection(a.getSubSection());
		dto.setPdCash(a.getPdCash().subtract(b.getPdCash()));
		dto.setPdCess(a.getPdCess().subtract(b.getPdCess()));
		dto.setPdCgst(a.getPdCgst().subtract(b.getPdCgst()));
		dto.setPdIgst(a.getPdIgst().subtract(b.getPdIgst()));
		dto.setPdSgst(a.getPdSgst().subtract(b.getPdSgst()));
		dto.setTaxPayable(a.getTaxPayable().subtract(b.getTaxPayable()));

		return dto;
	}

	private GSTR9TaxPaidReportDownloadInnerDto addTaxPaidDto(
			GSTR9TaxPaidReportDownloadInnerDto a,
			GSTR9TaxPaidReportDownloadInnerDto b) {
		GSTR9TaxPaidReportDownloadInnerDto dto = new GSTR9TaxPaidReportDownloadInnerDto();
		dto.setPdCash(a.getPdCash().add(b.getPdCash()));
		dto.setPdCess(a.getPdCess().add(b.getPdCess()));
		dto.setPdCgst(a.getPdCgst().add(b.getPdCgst()));
		dto.setPdIgst(a.getPdIgst().add(b.getPdIgst()));
		dto.setPdSgst(a.getPdSgst().add(b.getPdSgst()));
		dto.setTaxPayable(a.getTaxPayable().add(b.getTaxPayable()));

		return dto;
	}

	private Gstr9GetCallComputeEntity addTaxPaidEntity(
			Gstr9GetCallComputeEntity a, Gstr9GetCallComputeEntity b) {
		Gstr9GetCallComputeEntity dto = new Gstr9GetCallComputeEntity();
		dto.setSubSection(a.getSubSection() != null ? a.getSubSection()
				: b.getSubSection());
		dto.setTxpaidCash(
				(a.getTxpaidCash() != null ? a.getTxpaidCash() : zeroVal)
						.add(b.getTxpaidCash() != null ? b.getTxpaidCash()
								: zeroVal));
		dto.setTaxPaidItcCSamt((a.getTaxPaidItcCSamt() != null
				? a.getTaxPaidItcCSamt() : zeroVal)
						.add(b.getTaxPaidItcCSamt() != null
								? b.getTaxPaidItcCSamt() : zeroVal));
		dto.setTaxPaidItcSamt((a.getTaxPaidItcSamt() != null
				? a.getTaxPaidItcSamt() : zeroVal)
						.add(b.getTaxPaidItcSamt() != null
								? b.getTaxPaidItcSamt() : zeroVal));
		dto.setTaxPaidItcIamt((a.getTaxPaidItcIamt() != null
				? a.getTaxPaidItcIamt() : zeroVal)
						.add(b.getTaxPaidItcIamt() != null
								? b.getTaxPaidItcIamt() : zeroVal));
		dto.setTaxPaidItcCamt((a.getTaxPaidItcCamt() != null
				? a.getTaxPaidItcCamt() : zeroVal)
						.add(b.getTaxPaidItcCamt() != null
								? b.getTaxPaidItcCamt() : zeroVal));
		dto.setTxPyble((a.getTxPyble() != null ? a.getTxPyble() : zeroVal)
				.add(b.getTxPyble() != null ? b.getTxPyble() : zeroVal));
		
		dto.setIgst((a.getIgst() != null ? a.getIgst() : zeroVal)
				.add(b.getIgst() != null ? b.getIgst() : zeroVal));
		
		dto.setCgst((a.getCgst() != null ? a.getCgst() : zeroVal)
				.add(b.getCgst() != null ? b.getCgst() : zeroVal));
		
		dto.setSgst((a.getSgst() != null ? a.getSgst() : zeroVal)
				.add(b.getSgst() != null ? b.getSgst() : zeroVal));
		
		dto.setCess((a.getCess() != null ? a.getCess() : zeroVal)
				.add(b.getCess() != null ? b.getCess() : zeroVal));

		return dto;
	}

	private GSTR9TaxPaidReportDownloadInnerDto createTaxPaidAllList(
			String section,
			Map<String, GSTR9TaxPaidReportDownloadInnerDto> map) {

		GSTR9TaxPaidReportDownloadInnerDto dto = new GSTR9TaxPaidReportDownloadInnerDto();
		if (map.containsKey(section)) {

			GSTR9TaxPaidReportDownloadInnerDto o = map.get(section);
			dto.setPdCash(o.getPdCash() != null ? o.getPdCash() : zeroVal);
			dto.setPdCess(o.getPdCess() != null ? o.getPdCess() : zeroVal);
			dto.setPdCgst(o.getPdCgst() != null ? o.getPdCgst() : zeroVal);
			dto.setPdIgst(o.getPdIgst() != null ? o.getPdIgst() : zeroVal);
			dto.setPdSgst(o.getPdSgst() != null ? o.getPdSgst() : zeroVal);
			dto.setSubSection(o.getSubSection());
			dto.setTaxPayable(
					o.getTaxPayable() != null ? o.getTaxPayable() : zeroVal);
		} else {
			dto.setPdCash(zeroVal);
			dto.setPdCess(zeroVal);
			dto.setPdCgst(zeroVal);
			dto.setPdIgst(zeroVal);
			dto.setPdSgst(zeroVal);
			dto.setSubSection(section);
			dto.setTaxPayable(zeroVal);
		}
		return dto;
	}

	private GSTR9TaxPaidReportDownloadInnerDto convertToTaxPaidAutoDto(
			Gstr9AutoCalculateEntity o) {
		GSTR9TaxPaidReportDownloadInnerDto dto = new GSTR9TaxPaidReportDownloadInnerDto();
		dto.setPdCash(o.getTxPaidCash() != null ? o.getTxPaidCash() : zeroVal);
		dto.setPdCess(o.getTaxPaidItcCSamt() != null ? o.getTaxPaidItcCSamt()
				: zeroVal);
		dto.setPdCgst(o.getTaxPaidItcCamt() != null ? o.getTaxPaidItcCamt()
				: zeroVal);
		dto.setPdIgst(o.getTaxPaidItcIamt() != null ? o.getTaxPaidItcIamt()
				: zeroVal);
		dto.setPdSgst(o.getTaxPaidItcSamt() != null ? o.getTaxPaidItcSamt()
				: zeroVal);
		dto.setSubSection(o.getSubSection());
		dto.setTaxPayable(o.getTxPyble() != null ? o.getTxPyble() : zeroVal);

		return dto;
	}

	private GSTR9TaxPaidReportDownloadInnerDto convertToTaxPaidGstnDto(
			Gstr9GetSummaryEntity o) {
		GSTR9TaxPaidReportDownloadInnerDto dto = new GSTR9TaxPaidReportDownloadInnerDto();
		dto.setPdCash(o.getTxPaid() != null ? o.getTxPaid() : zeroVal);
		dto.setPdCess(o.getTaxPaidItcCSamt() != null ? o.getTaxPaidItcCSamt()
				: zeroVal);
		dto.setPdCgst(o.getTaxPaidItcCamt() != null ? o.getTaxPaidItcCamt()
				: zeroVal);
		dto.setPdIgst(o.getTaxPaidItcIamt() != null ? o.getTaxPaidItcIamt()
				: zeroVal);
		dto.setPdSgst(o.getTaxPaidItcSamt() != null ? o.getTaxPaidItcSamt()
				: zeroVal);
		dto.setSubSection(o.getSubSection());
		dto.setTaxPayable(o.getTxPyble() != null ? o.getTxPyble() : zeroVal);

		return dto;
	}

	private List<GSTR9TaxPaidReportDownloadInnerDto> convertToTaxPaidUserDto(
			List<Gstr9UserInputEntity> userEntities) {
		BigDecimal defaultValue = BigDecimal.ZERO;

		Gstr9UserInputEntity entity9 = null;
		Gstr9UserInputEntity entity14_1 = null;
		Gstr9UserInputEntity entity14_2 = null;

		for (Gstr9UserInputEntity entity : userEntities) {
			if (entity.getSubSection().equals("9")) {
				entity9 = entity;
			} else if (entity.getSubSection().equals("14(1)")) {
				entity14_1 = entity;
			} else if (entity.getSubSection().equals("14(2)")) {
				entity14_2 = entity;
			}
		}
		List<GSTR9TaxPaidReportDownloadInnerDto> userInputList = new ArrayList<>();

		GSTR9TaxPaidReportDownloadInnerDto dto1 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto2 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto3 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto4 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto5 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto6 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto7 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto8 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto9 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto10 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto11 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto12 = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto13 = new GSTR9TaxPaidReportDownloadInnerDto();

		if (entity9 == null) {
			dto1.setTaxPayable(defaultValue);
			dto2.setTaxPayable(defaultValue);
			dto3.setTaxPayable(defaultValue);
			dto4.setTaxPayable(defaultValue);
			dto5.setTaxPayable(defaultValue);
			dto6.setTaxPayable(defaultValue);
			dto7.setTaxPayable(defaultValue);
			dto8.setTaxPayable(defaultValue);

		} else {
			dto1.setTaxPayable(entity9.getIgst() != null ? entity9.getIgst()
					: defaultValue);
			dto2.setTaxPayable(entity9.getCgst() != null ? entity9.getCgst()
					: defaultValue);
			dto3.setTaxPayable(entity9.getSgst() != null ? entity9.getSgst()
					: defaultValue);
			dto4.setTaxPayable(entity9.getCess() != null ? entity9.getCess()
					: defaultValue);
			dto5.setTaxPayable(entity9.getIntr() != null ? entity9.getIntr()
					: defaultValue);
			dto6.setTaxPayable(
					entity9.getFee() != null ? entity9.getFee() : defaultValue);
			dto7.setTaxPayable(
					entity9.getPen() != null ? entity9.getPen() : defaultValue);
			dto8.setTaxPayable(
					entity9.getOth() != null ? entity9.getOth() : defaultValue);
		}
		if (entity14_1 == null) {
			dto9.setTaxPayable(defaultValue);
			dto10.setTaxPayable(defaultValue);
			dto11.setTaxPayable(defaultValue);
			dto12.setTaxPayable(defaultValue);
			dto13.setTaxPayable(defaultValue);
		} else {
			dto9.setTaxPayable(entity14_1.getIgst() != null
					? entity14_1.getIgst() : defaultValue);
			dto10.setTaxPayable(entity14_1.getCgst() != null
					? entity14_1.getCgst() : defaultValue);
			dto11.setTaxPayable(entity14_1.getSgst() != null
					? entity14_1.getSgst() : defaultValue);
			dto12.setTaxPayable(entity14_1.getCess() != null
					? entity14_1.getCess() : defaultValue);
			dto13.setTaxPayable(entity14_1.getIntr() != null
					? entity14_1.getIntr() : defaultValue);
		}
		if (entity14_2 == null) {
			dto9.setPdCash(defaultValue);
			dto10.setPdCash(defaultValue);
			dto11.setPdCash(defaultValue);
			dto12.setPdCash(defaultValue);
			dto13.setPdCash(defaultValue);

		} else {
			dto9.setPdCash(entity14_2.getIgst() != null ? entity14_2.getIgst()
					: defaultValue);
			dto10.setPdCash(entity14_2.getCgst() != null ? entity14_2.getCgst()
					: defaultValue);
			dto11.setPdCash(entity14_2.getSgst() != null ? entity14_2.getSgst()
					: defaultValue);
			dto12.setPdCash(entity14_2.getCess() != null ? entity14_2.getCess()
					: defaultValue);
			dto13.setPdCash(entity14_2.getIntr() != null ? entity14_2.getIntr()
					: defaultValue);
		}
		dto1.setSubSection(Gstr9TaxPaidConstants.Table_9A);
		dto2.setSubSection(Gstr9TaxPaidConstants.Table_9B);
		dto3.setSubSection(Gstr9TaxPaidConstants.Table_9C);
		dto4.setSubSection(Gstr9TaxPaidConstants.Table_9D);
		dto5.setSubSection(Gstr9TaxPaidConstants.Table_9E);
		dto6.setSubSection(Gstr9TaxPaidConstants.Table_9F);
		dto7.setSubSection(Gstr9TaxPaidConstants.Table_9G);
		dto8.setSubSection(Gstr9TaxPaidConstants.Table_9H);
		dto9.setSubSection(GSTR9Constants.Table_14A);
		dto10.setSubSection(GSTR9Constants.Table_14B);
		dto11.setSubSection(GSTR9Constants.Table_14C);
		dto12.setSubSection(GSTR9Constants.Table_14D);
		dto13.setSubSection(GSTR9Constants.Table_14E);

		userInputList.add(dto1);
		userInputList.add(dto2);
		userInputList.add(dto3);
		userInputList.add(dto4);
		userInputList.add(dto5);
		userInputList.add(dto6);
		userInputList.add(dto7);
		userInputList.add(dto8);
		userInputList.add(dto9);
		userInputList.add(dto10);
		userInputList.add(dto11);
		userInputList.add(dto12);
		userInputList.add(dto13);

		return userInputList;
	}

	private List<Gstr9HsnReportDownloadDto> getHsnData(String gstin,
			String taxPeriod, String finYear) {

		List<Gstr9HsnProcessEntity> hsnList = hsnRepo
				.listAllGstr9ProcessedDetails(gstin, finYear);

		List<Gstr9HsnReportDownloadDto> hsnDataList = hsnList.stream()
				.map(o -> convertHsnDto(o))
				.collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(hsnDataList,
				new Comparator<Gstr9HsnReportDownloadDto>() {

					@Override
					public int compare(Gstr9HsnReportDownloadDto o1,
							Gstr9HsnReportDownloadDto o2) {
						return o1.getTableNumber()
								.compareTo(o2.getTableNumber());
					}

				});

		return hsnDataList;
	}

	private Gstr9HsnReportDownloadDto convertHsnDto(Gstr9HsnProcessEntity o) {
		Gstr9HsnReportDownloadDto dto = new Gstr9HsnReportDownloadDto();
		dto.setCess(o.getCess() != null ? o.getCess() : zeroVal);
		dto.setCgst(o.getCgst() != null ? o.getCgst() : zeroVal);
		dto.setConcessionalRateFlag(o.getConRateFlag());
		dto.setDescription(o.getDesc());
		dto.setFy(o.getFy());
		dto.setGstin(o.getGstin());
		dto.setIgst(o.getIgst() != null ? o.getIgst() : zeroVal);
		dto.setHsn(o.getHsn());
		dto.setRateofTax(o.getRateOfTax() != null ? o.getRateOfTax() : zeroVal);
		dto.setSgst(o.getSgst() != null ? o.getSgst() : zeroVal);
		dto.setTableNumber(o.getTableNumber());
		dto.setTaxableValue(
				o.getTaxableVal() != null ? o.getTaxableVal() : zeroVal);
		dto.setTotalQuantity(
				o.getTotalQnt() != null ? o.getTotalQnt() : zeroVal);
		dto.setUqc(o.getUqc());
		return dto;
	}

	private GSTR9TaxPaidReportDownloadInnerDto convertToTaxPaidFilingDto(
			Gstr9GetCallComputeEntity filingEntities) {

		BigDecimal zero = BigDecimal.ZERO;

		GSTR9TaxPaidReportDownloadInnerDto dto = new GSTR9TaxPaidReportDownloadInnerDto();
		if (!filingEntities.getSubSection().equalsIgnoreCase("14(1)")
				|| !filingEntities.getSubSection().equalsIgnoreCase("14(2)")) {
			dto.setPdCash(filingEntities.getTxpaidCash() != null
					? filingEntities.getTxpaidCash() : zero);
			dto.setPdCess(filingEntities.getTaxPaidItcCSamt() != null
					? filingEntities.getTaxPaidItcCSamt() : zero);
			dto.setPdIgst(filingEntities.getTaxPaidItcIamt() != null
					? filingEntities.getTaxPaidItcIamt() : zero);
			dto.setPdCgst(filingEntities.getTaxPaidItcCamt() != null
					? filingEntities.getTaxPaidItcCamt() : zero);
			dto.setPdSgst(filingEntities.getTaxPaidItcSamt() != null
					? filingEntities.getTaxPaidItcSamt() : zero);
			dto.setSubSection(filingEntities.getSubSection());
			dto.setTaxPayable(filingEntities.getTxPyble() != null
					? filingEntities.getTxPyble() : zero);

		}
		return dto;
	}
	
	private List<GSTR9TaxPaidReportDownloadInnerDto> convertTo14FilingDto(
			List<Gstr9GetCallComputeEntity> entityList) {

		BigDecimal defaultValue = BigDecimal.ZERO;

		Gstr9GetCallComputeEntity entity14_1 = null;
		Gstr9GetCallComputeEntity entity14_2 = null;

		for (Gstr9GetCallComputeEntity entity : entityList) {
			if (entity.getSubSection().equals("14(1)")) {
				entity14_1 = entity;
			} else if (entity.getSubSection().equals("14(2)")) {
				entity14_2 = entity;
			}
		}
		List<GSTR9TaxPaidReportDownloadInnerDto> userInputList = new ArrayList<>();

		GSTR9TaxPaidReportDownloadInnerDto dto14A = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto14B = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto14C = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto14D = new GSTR9TaxPaidReportDownloadInnerDto();
		GSTR9TaxPaidReportDownloadInnerDto dto14E = new GSTR9TaxPaidReportDownloadInnerDto();

		if (entity14_1 == null) {
			dto14A.setTaxPayable(defaultValue);
			dto14B.setTaxPayable(defaultValue);
			dto14C.setTaxPayable(defaultValue);
			dto14D.setTaxPayable(defaultValue);
			dto14E.setTaxPayable(defaultValue);
		} else {
			dto14A.setTaxPayable(entity14_1.getIgst() != null
					? entity14_1.getIgst() : defaultValue);
			dto14B.setTaxPayable(entity14_1.getCgst() != null
					? entity14_1.getCgst() : defaultValue);
			dto14C.setTaxPayable(entity14_1.getSgst() != null
					? entity14_1.getSgst() : defaultValue);
			dto14D.setTaxPayable(entity14_1.getCess() != null
					? entity14_1.getCess() : defaultValue);
			dto14E.setTaxPayable(entity14_1.getIntr() != null
					? entity14_1.getIntr() : defaultValue);
		}
		if (entity14_2 == null) {
			dto14A.setPdCash(defaultValue);
			dto14B.setPdCash(defaultValue);
			dto14C.setPdCash(defaultValue);
			dto14D.setPdCash(defaultValue);
			dto14E.setPdCash(defaultValue);

		} else {
			dto14A.setPdCash(entity14_2.getIgst() != null ? entity14_2.getIgst()
					: defaultValue);
			dto14B.setPdCash(entity14_2.getCgst() != null ? entity14_2.getCgst()
					: defaultValue);
			dto14C.setPdCash(entity14_2.getSgst() != null ? entity14_2.getSgst()
					: defaultValue);
			dto14D.setPdCash(entity14_2.getCess() != null ? entity14_2.getCess()
					: defaultValue);
			dto14E.setPdCash(entity14_2.getIntr() != null ? entity14_2.getIntr()
					: defaultValue);
		}

		dto14A.setSubSection(GSTR9Constants.Table_14A);
		dto14B.setSubSection(GSTR9Constants.Table_14B);
		dto14C.setSubSection(GSTR9Constants.Table_14C);
		dto14D.setSubSection(GSTR9Constants.Table_14D);
		dto14E.setSubSection(GSTR9Constants.Table_14E);

		userInputList.add(dto14A);
		userInputList.add(dto14B);
		userInputList.add(dto14C);
		userInputList.add(dto14D);
		userInputList.add(dto14E);

		return userInputList;
	}
	
}