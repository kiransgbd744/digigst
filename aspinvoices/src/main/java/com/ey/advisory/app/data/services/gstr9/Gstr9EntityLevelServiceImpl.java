package com.ey.advisory.app.data.services.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9DigiComputeEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9AutoCalculateRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9DigiComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr9EntityLevelServiceImpl")
@Slf4j
public class Gstr9EntityLevelServiceImpl implements Gstr9EntityLevelService {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;
	
	@Autowired
	private Gstr9HsnProcessedRepository gstr9HsnProcessedRepository;

	/*@Autowired
	private Gstr9GetCallComputeRepository gstr9GetCallCompRepo;*/
	
	@Autowired
	private Gstr9DigiComputeRepository gstr9DigiComputeRepository;

	@Autowired
	private Gstr9AutoCalculateRepository gstr9AutoCalRepo;

	@Autowired
	Gstr9OutwardUtil gstr9OutwardUtil;

	@Autowired
	Gstr9InwardUtil gstr9InwardUtil;

	/*private static ImmutableList<String> sectionList = ImmutableList.of("4",
			"5", "6", "7", "8", "10", "11", "12", "13", "14", "15", "16");

	private static ImmutableList<String> excludeSubSectionList = ImmutableList
			.of("6B", "6C", "6D", "6E", "8A");

	private static ImmutableList<String> gstr9SecList = ImmutableList.of("4",
			"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16");
*/
	private static ImmutableList<String> autoCalSectionList = ImmutableList
			.of("4", "5", "6", "8");

	private static ImmutableList<String> autoCalSoftDelList = ImmutableList
			.of("4", "5", "6", "7", "8", "9");

	private static ImmutableList<String> excludeSubSecsAutoCal = ImmutableList
			.of("6A", "8A");
	
	private static ImmutableList<String> OutInPySectionsToCopy = ImmutableList.of("4",
			"5", "6", "7", "8", "10", "11", "12", "13");
	
	private static ImmutableList<String> hsnSectionsToCopy = ImmutableList.of("17","18");

	
	@Override
	public APIResponse getGstr9Details(String gstin, String taxPeriod) {
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR9_GETDETAILS,
					param1, param2);
			return apiExecutor.execute(params, null);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking getGstr9Details '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					APIIdentifiers.GSTR9_GETDETAILS, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw ex;
		}

	}

	@Override
	public APIResponse getAutoCalcDetails(String gstin, String taxPeriod) {
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN, APIIdentifiers.GSTR9_AUTOCAlCDETAILS,
					param1, param2);
			return apiExecutor.execute(params, null);
		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking getGstr9Details '%s',"
							+ " GSTIN - '%s' and Taxperiod - '%s'",
					APIIdentifiers.GSTR9_AUTOCAlCDETAILS, gstin, taxPeriod);
			LOGGER.error(msg, ex);
			throw ex;
		}

	}

	/*@Override
	public String copyGstr9ComputeData(String gstin, String taxPeriod,
			String fy,HttpServletRequest req) {

		String createdBy = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		try {

			Integer convertedFy = GenUtil
					.convertFytoIntFromReturnPeriod(taxPeriod);

			List<Gstr9GetCallComputeEntity> gstr9GetCompList = gstr9GetCallCompRepo
					.findRecordsforSelectedSections(gstin, convertedFy,
							sectionList, excludeSubSectionList);

			List<Gstr9GetCallComputeEntity> gstr9T9GetCompList = gstr9GetCallCompRepo
					.findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
							gstin, convertedFy, Arrays.asList("9"));
	
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr9InwardServiceImpl"
						+ ".getgstr9DashBoardList gstr9GetCompEntityList list size = "
						+ gstr9GetCompList.size());
			}

			if (gstr9GetCompList.isEmpty() && gstr9T9GetCompList.isEmpty()) {
				LOGGER.error(
						"No Active Records Found for Gstin {} and "
								+ "TaxPeriod {} in Computed Table",
						gstin, taxPeriod);
				String msg = String
						.format("Computed Data is not available for Gstin %s and "
								+ "TaxPeriod %s", gstin, taxPeriod);
				throw new AppException(msg);
			}

			List<Gstr9UserInputEntity> listofUserList = gstr9GetCompList
					.stream().map(o -> convertDataToEntity(o, fy, taxPeriod))
					.collect(Collectors.toCollection(ArrayList::new));

			List<Gstr9UserInputEntity> consolidatedList = listofUserList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSubSection(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addDto(a, b, fy, taxPeriod)),
									Optional::get)))
					.values().stream()
					.collect(Collectors.toCollection(ArrayList::new));

			// Below code written for Table9 because for table 9 we have to save
			// the data in a single row not in multiple rows

			if (!gstr9T9GetCompList.isEmpty()) {
				Gstr9UserInputEntity userEntity = convertToEntityForTbl9(
						gstr9T9GetCompList, gstin, fy, createdBy, taxPeriod);
				consolidatedList.add(userEntity);
			} else {
				LOGGER.error(
						"No Active Records Found for Section 9 Gstin {} and "
								+ "TaxPeriod {} in Computed Table",
						gstin, taxPeriod);
			}

			if (!listofUserList.isEmpty()) {
				int softdeleteCount = gstr9UserInputRepository
						.softDeleteBasedOnGstinandFy(gstin, fy, gstr9SecList,
								"C", createdBy);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"{} records soft deleted for Gstin {} and TaxPeriod"
									+ " {} in UserInput Table",
							softdeleteCount, gstin, taxPeriod);
				}
				gstr9UserInputRepository.saveAll(consolidatedList);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Records Saved in Successfully in UserInput Table for "
								+ "Gstin {} and Taxperiod {}",
						gstin, taxPeriod);
			}
			return "Success";
		} catch (Exception e) {
			String msg = "Exception Occured while copying the Data";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}
	}*/
	
	
	
	@Override
	public String copyGstr9ComputeData(String gstin, String taxPeriod, String fy) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside Gstr9EntityLevelServiceImpl .copyGstr9ComputeData "
					+ "method for Gstin: {}, TaxPeriod: {}",
					gstin, taxPeriod);
		}

		String createdBy = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName() : "SYSTEM";

		try {
			
		//	Integer convertedFy = GenUtil.convertFytoIntFromReturnPeriod(taxPeriod);

			Map<String, String> itcTypeMap = gstr9InwardUtil.getItcTypeMap();

			List<Gstr9DigiComputeEntity> gstr9DigiComputeEntityList = gstr9DigiComputeRepository
					.findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(gstin, OutInPySectionsToCopy, taxPeriod);
			
			//hsn section list
			List<Gstr9DigiComputeEntity> gstr9DigiComputeEntityHsnList = gstr9DigiComputeRepository
					.findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(gstin, hsnSectionsToCopy, taxPeriod);

			// Check if both lists are null or empty
			if (ObjectUtils.isEmpty(gstr9DigiComputeEntityList) && ObjectUtils.isEmpty(gstr9DigiComputeEntityHsnList)) {
				/* String emptySections = "";

				if (ObjectUtils.isEmpty(gstr9DigiComputeEntityList)) {
					emptySections += "with missing data: " + OutInPySectionsToCopy;
				}
				if (ObjectUtils.isEmpty(gstr9DigiComputeEntityHsnList)) {
					emptySections += (emptySections.isEmpty() ? "" : " and ") + "HSN sections with missing data: "
							+ hsnSectionsToCopy;
				}*/

				LOGGER.error("No Active Records Found for Gstin {} and TaxPeriod {}. {}", gstin, taxPeriod);
				String msg = String.format(
						"DigiGST Processed data is unavailable for Gstin %s and taxPeriod %s ", gstin,
						taxPeriod);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Found {} records in Outward,Inward and PyTransaction sections and {} records in HSN sections for Gstin: {}, TaxPeriod: {}",
						gstr9DigiComputeEntityList.size(), gstr9DigiComputeEntityHsnList.size(), gstin, taxPeriod);
			}

		
			if (!ObjectUtils.isEmpty(gstr9DigiComputeEntityList)) {
				List<Gstr9UserInputEntity> userInputEntities = gstr9DigiComputeEntityList.stream()
						.map(entity -> convertDataToEntityForCopyDataToUserInput(entity, gstin, fy, createdBy,
								taxPeriod, itcTypeMap))
						.collect(Collectors.toList());

				if (!userInputEntities.isEmpty()) {
					int softDeleteCount = gstr9UserInputRepository.softDeleteBasedOnGstinandFy(gstin, fy,
							OutInPySectionsToCopy, "U", createdBy);
					LOGGER.debug("{} records soft deleted for Gstin {} and TaxPeriod {} in UserInput Table",
							softDeleteCount, gstin, taxPeriod);
					gstr9UserInputRepository.saveAll(userInputEntities);
				}
			}

			// Process HSN sections
			if (!ObjectUtils.isEmpty(gstr9DigiComputeEntityHsnList)) {
				List<Gstr9HsnProcessEntity> hsnProcessEntities = gstr9DigiComputeEntityHsnList.stream().map(
						entity -> convertDataToHsnEntityForCopyDataToUserInput(entity, gstin, fy, createdBy, taxPeriod))
						.collect(Collectors.toList());

				if (!hsnProcessEntities.isEmpty()) {
					int softDeleteCount = gstr9HsnProcessedRepository.softDeleteBasedOnGstinAndFy(gstin, fy,
							hsnSectionsToCopy, createdBy);
					LOGGER.debug("{} records soft deleted for Gstin {} and TaxPeriod {} in HSN Processed Table",
							softDeleteCount, gstin, taxPeriod);
					gstr9HsnProcessedRepository.saveAll(hsnProcessEntities);
				}
			}

			LOGGER.debug("Records saved successfully in UserInput Table for Gstin: {} and TaxPeriod: {}", gstin,
					taxPeriod);
			return "Success";

		} catch (Exception e) {
			String errorMsg = "Exception occurred while copying the data for Gstin: " + gstin + ", TaxPeriod: "
					+ taxPeriod;
			LOGGER.error(errorMsg, e);
			throw new AppException(e.getMessage(), e);
		}
	}


	/*private Gstr9UserInputEntity convertToEntityForTbl9(
			List<Gstr9GetCallComputeEntity> getCompInput, String gstin,
			String fy, String userName, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside Gstr9TaxPaidTabServiceImpl"
					+ ".convertToEntity method :");
		}
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();

		List<Gstr9GetCallComputeEntity> consolidatedList = getCompInput.stream()
				.collect(Collectors.groupingBy(o -> o.getSubSection(),
						Collectors.collectingAndThen(
								Collectors.reducing(
										(a, b) -> addDtoForTbl9(a, b)),
								Optional::get)))
				.values().stream()
				.collect(Collectors.toCollection(ArrayList::new));

		consolidatedList.forEach((input) -> {
			if (input.getSubSection().equalsIgnoreCase("9A"))
				userEntity.setIgst(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9B"))
				userEntity.setCgst(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9C"))
				userEntity.setSgst(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9D"))
				userEntity.setCess(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9E"))
				userEntity.setIntr(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9F"))
				userEntity.setFee(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9G"))
				userEntity.setPen(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9H"))
				userEntity.setOth(input.getTxPyble());
			userEntity.setTxpaidCash(input.getTxpaidCash());
			userEntity.setTaxPaidItcIamt(input.getTaxPaidItcIamt());
			userEntity.setTaxPaidItcSamt(input.getTaxPaidItcSamt());
			userEntity.setTaxPaidItcCamt(input.getTaxPaidItcCamt());
			userEntity.setTaxPaidItcCSamt(input.getTaxPaidItcCSamt());
		});
		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				Gstr9TaxPaidConstants.Table_9);
		userEntity.setDocKey(docKey);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setGstin(gstin);
		userEntity.setFy(fy);
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setSection(Gstr9TaxPaidConstants.Table_9);
		userEntity.setSubSection(Gstr9TaxPaidConstants.Table_9);
		userEntity.setSource("C");
		userEntity
				.setNatureOfSupplies(Gstr9Util.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9));
		userEntity.setActive(true);
		return userEntity;
	}*/

	// private List<Gstr9DiffTaxSaveDto> convertToSaveDto(
	// List<Gstr9GetCallComputeEntity> oList) {
	//
	// Gstr9DiffTaxSaveDto saveDto1 = new Gstr9DiffTaxSaveDto();
	// Gstr9DiffTaxSaveDto saveDto2 = new Gstr9DiffTaxSaveDto();
	//
	// List<Gstr9DiffTaxSaveDto> saveDtoList = new ArrayList<>();
	//
	// for (Gstr9GetCallComputeEntity input : oList) {
	// if (input.getSubSection().equalsIgnoreCase("14A"))
	// saveDto1.setSubSection("14(1)");
	// saveDto1.setIgst(input.getTxPyble());
	// saveDto2.setSubSection("14(2)");
	// saveDto2.setIgst(input.getTxPaid());
	// if (input.getSubSection().equalsIgnoreCase("14B")) {
	// saveDto1.setCgst(input.getTxPyble());
	// saveDto2.setCgst(input.getTxPaid());
	// }
	// if (input.getSubSection().equalsIgnoreCase("14C")) {
	// saveDto1.setSgst(input.getTxPyble());
	// saveDto2.setSgst(input.getTxPaid());
	// }
	// if (input.getSubSection().equalsIgnoreCase("14D")) {
	// saveDto1.setCess(input.getTxPyble());
	// saveDto2.setCess(input.getTxPaid());
	// }
	// if (input.getSubSection().equalsIgnoreCase("14E")) {
	// saveDto1.setIntr(input.getTxPyble());
	// saveDto2.setIntr(input.getTxPaid());
	// }
	// }
	//
	// saveDtoList.add(saveDto1);
	// saveDtoList.add(saveDto2);
	// return saveDtoList;
	// }
	//
	// private Gstr9UserInputEntity convertToEntityForTbl14(String gstin,
	// String taxPeriod, String fy, Gstr9DiffTaxSaveDto userInput) {
	//
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.info(" Inside Gstr9DifferentialTaxServieImpl"
	// + ".convertToEntity method :");
	// }
	// String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
	// userInput.getSubSection());
	// Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
	// userEntity.setGstin(gstin);
	// userEntity.setRetPeriod(taxPeriod);
	// userEntity.setFy(fy);
	// userEntity.setIgst(userInput.getIgst());
	// userEntity.setCgst(userInput.getCgst());
	// userEntity.setSgst(userInput.getSgst());
	// userEntity.setCess(userInput.getCess());
	// userEntity.setIntr(userInput.getIntr());
	// userEntity.setSection("14");
	// userEntity.setSubSection(userInput.getSubSection());
	// userEntity.setSource("C");
	// userEntity
	// .setDerivedRetPeriod(GenUtil.convertTaxPeriodToInt(taxPeriod));
	// userEntity.setActive(true);
	// userEntity.setDocKey(docKey);
	// userEntity.setNatureOfSupplies(Gstr9Util
	// .getNatureOfSuppliesForSubSection(userInput.getSubSection()));
	// userEntity.setCreatedOn(LocalDateTime.now());
	// userEntity.setCreatedBy("SYSTEM");
	// return userEntity;
	// }

	/*private Gstr9UserInputEntity addDto(Gstr9UserInputEntity a,
			Gstr9UserInputEntity b, String fy, String taxPeriod) {
		Gstr9UserInputEntity dto = new Gstr9UserInputEntity();

		dto.setGstin(a.getGstin());
		dto.setFy(fy);
		dto.setRetPeriod(taxPeriod);
		dto.setRetPeriod(GenUtil.getFinancialPeriodFromFY(fy));
		dto.setDerivedRetPeriod(a.getDerivedRetPeriod());
		dto.setSection(a.getSection());
		dto.setSubSection(a.getSubSection());
		dto.setItcTyp(a.getItcTyp());
		dto.setDesc(a.getDesc());
		dto.setLiabId(a.getLiabId());
		dto.setDebitId(a.getDebitId());
		dto.setTransCode(a.getTransCode());
		dto.setTransDate(a.getTransDate());

		dto.setSource("C");
		dto.setActive(true);
		dto.setCreatedBy("SYSTEM");
		String docKey = CommonUtility.generateGstr9DocKey(a.getGstin(), fy,
				a.getSubSection());
		dto.setDocKey(docKey);
		dto.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(dto.getSubSection()));
		dto.setCreatedOn(LocalDateTime.now());

		dto.setCess(defaultToZeroIfNull(a.getCess())
				.add(defaultToZeroIfNull(b.getCess())));
		dto.setCgst(defaultToZeroIfNull(a.getCgst())
				.add(defaultToZeroIfNull(b.getCgst())));
		dto.setIgst(defaultToZeroIfNull(a.getIgst())
				.add(defaultToZeroIfNull(b.getIgst())));
		dto.setSgst(defaultToZeroIfNull(a.getSgst())
				.add(defaultToZeroIfNull(b.getSgst())));
		dto.setTxVal(defaultToZeroIfNull(a.getTxVal())
				.add(defaultToZeroIfNull(b.getTxVal())));
		dto.setTxpaidCash(defaultToZeroIfNull(a.getTxpaidCash())
				.add(defaultToZeroIfNull(b.getTxpaidCash())));
		dto.setTaxPaidItcIamt(defaultToZeroIfNull(a.getTaxPaidItcIamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcIamt())));
		dto.setTaxPaidItcCamt(defaultToZeroIfNull(a.getTaxPaidItcCamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcCamt())));
		dto.setTaxPaidItcSamt(defaultToZeroIfNull(a.getTaxPaidItcSamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcSamt())));
		dto.setTaxPaidItcCSamt(defaultToZeroIfNull(a.getTaxPaidItcCSamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcCSamt())));
		dto.setTxPyble(defaultToZeroIfNull(a.getTxPyble())
				.add(defaultToZeroIfNull(b.getTxPyble())));
		dto.setTxPaid(defaultToZeroIfNull(a.getTxPaid())
				.add(defaultToZeroIfNull(b.getTxPaid())));
		dto.setIntr(defaultToZeroIfNull(a.getIntr())
				.add(defaultToZeroIfNull(b.getIntr())));
		dto.setFee(defaultToZeroIfNull(a.getFee())
				.add(defaultToZeroIfNull(b.getFee())));
		dto.setPen(defaultToZeroIfNull(a.getPen())
				.add(defaultToZeroIfNull(b.getPen())));
		dto.setPen(defaultToZeroIfNull(a.getPen())
				.add(defaultToZeroIfNull(b.getPen())));
		dto.setOth(defaultToZeroIfNull(a.getOth())
				.add(defaultToZeroIfNull(b.getOth())));
		dto.setTot(defaultToZeroIfNull(a.getOth())
				.add(defaultToZeroIfNull(b.getTot())));

		return dto;
	}

	private Gstr9GetCallComputeEntity addDtoForTbl9(Gstr9GetCallComputeEntity a,
			Gstr9GetCallComputeEntity b) {
		Gstr9GetCallComputeEntity dto = new Gstr9GetCallComputeEntity();
		dto.setSection(a.getSection());
		dto.setSubSection(a.getSubSection());
		dto.setTxPyble(defaultToZeroIfNull(a.getTxPyble())
				.add(defaultToZeroIfNull(b.getTxPyble())));
		dto.setTxpaidCash(defaultToZeroIfNull(a.getTxpaidCash())
				.add(defaultToZeroIfNull(b.getTxpaidCash())));
		dto.setTaxPaidItcIamt(defaultToZeroIfNull(a.getTaxPaidItcIamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcIamt())));
		dto.setTaxPaidItcCamt(defaultToZeroIfNull(a.getTaxPaidItcCamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcCamt())));
		dto.setTaxPaidItcSamt(defaultToZeroIfNull(a.getTaxPaidItcSamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcSamt())));
		dto.setTaxPaidItcCSamt(defaultToZeroIfNull(a.getTaxPaidItcCSamt())
				.add(defaultToZeroIfNull(b.getTaxPaidItcCSamt())));
		return dto;
	}

	private Gstr9UserInputEntity convertDataToEntity(
			Gstr9GetCallComputeEntity getCallComp, String fy,
			String taxPeriod) {
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
		userEntity.setGstin(getCallComp.getGstin());
		userEntity.setFy(fy);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setDerivedRetPeriod(getCallComp.getDerivedRetPeriod());
		userEntity.setSection(getCallComp.getSection());
		userEntity.setSubSection(getCallComp.getSubSection());
		userEntity.setTxVal(getCallComp.getTxVal());
		userEntity.setIgst(getCallComp.getIgst());
		userEntity.setCgst(getCallComp.getCgst());
		userEntity.setSgst(getCallComp.getSgst());
		userEntity.setCess(getCallComp.getCess());
		userEntity.setItcTyp(getCallComp.getItcTyp());
		userEntity.setDesc(getCallComp.getDesc());
		userEntity.setTxpaidCash(getCallComp.getTxpaidCash());
		userEntity.setTaxPaidItcIamt(getCallComp.getTaxPaidItcIamt());
		userEntity.setTaxPaidItcSamt(getCallComp.getTaxPaidItcSamt());
		userEntity.setTaxPaidItcCamt(getCallComp.getTaxPaidItcCamt());
		userEntity.setTaxPaidItcCSamt(getCallComp.getTaxPaidItcCSamt());
		userEntity.setTxPyble(getCallComp.getTxPyble());
		userEntity.setTxPaid(getCallComp.getTxPaid());
		userEntity.setIntr(getCallComp.getIntr());
		userEntity.setFee(getCallComp.getFee());
		userEntity.setPen(getCallComp.getPen());
		userEntity.setLiabId(getCallComp.getLiabId());
		userEntity.setDebitId(getCallComp.getDebitId());
		userEntity.setTransCode(getCallComp.getTransCode());
		userEntity.setTransDate(getCallComp.getTransDate());
		userEntity.setOth(getCallComp.getOth());
		userEntity.setTot(getCallComp.getTot());
		userEntity.setSource("C");
		userEntity.setActive(true);
		userEntity.setCreatedBy("SYSTEM");
		String docKey = CommonUtility.generateGstr9DocKey(
				getCallComp.getGstin(), fy, getCallComp.getSubSection());
		userEntity.setDocKey(docKey);
		userEntity.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(getCallComp.getSubSection()));
		userEntity.setCreatedOn(LocalDateTime.now());
		return userEntity;
	}

	private BigDecimal defaultToZeroIfNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}*/

	@Override
	@Transactional(value = "clientTransactionManager")
	public String copyGstr9AutoComputeData(String gstin, String taxPeriod,
			String fy) {

		String createdBy = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		try {

			List<Gstr9AutoCalculateEntity> gstr9AutoCompList = gstr9AutoCalRepo
					.findByGstinAndRetPeriodAndSectionInAndSubSectionNotInAndIsActiveTrue(
							gstin, taxPeriod, autoCalSectionList,
							excludeSubSecsAutoCal);

			List<Gstr9AutoCalculateEntity> gstr9AutoCompTbl9List = gstr9AutoCalRepo
					.findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(gstin,
							taxPeriod, Arrays.asList("9"));

			if (gstr9AutoCompList.isEmpty()
					&& gstr9AutoCompTbl9List.isEmpty()) {
				LOGGER.error(
						"No Active Records Found for Gstin {} and "
								+ "TaxPeriod {} in Computed Table",
						gstin, taxPeriod);
				String msg = String
						.format("Auto Calculate Data is not available for Gstin"
								+ " %s", gstin);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr9EntityLevelServiceImpl"
								+ ".copyGstr9AutoComputeData gstr9AutoCompList "
								+ "size = %s and gstr9AutoCompTbl9List size = %s",
						gstr9AutoCompList.size(), gstr9AutoCompTbl9List.size());
				LOGGER.info(msg);
			}

			Map<String, String> itcTypeMap = gstr9InwardUtil.getItcTypeMap();

			List<Gstr9UserInputEntity> copiedUserList = gstr9AutoCompList
					.stream()
					.map(o -> convertDataToEntity(o, fy, taxPeriod, itcTypeMap))
					.collect(Collectors.toCollection(ArrayList::new));

			// Below code written for Table9 because for table 9 we have to save
			// the data in a single row not in multiple rows

			Gstr9UserInputEntity userEntity = convertToEntityForTbl9ForAutoComp(
					gstr9AutoCompTbl9List, gstin, fy, createdBy, taxPeriod);
			copiedUserList.add(userEntity);

			if (!copiedUserList.isEmpty()) {
				int softdeleteCount = gstr9UserInputRepository
						.softDeleteBasedOnGstinandFy(gstin, fy,
								autoCalSoftDelList, "GSTN", createdBy);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"{} records soft deleted for Gstin {} and TaxPeriod"
									+ " {} in UserInput Table",
							softdeleteCount, gstin, taxPeriod);
				}
				gstr9UserInputRepository.saveAll(copiedUserList);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Records Saved in Successfully in UserInput Table for "
								+ "Gstin {} and Taxperiod {}",
						gstin, taxPeriod);
			}
			return "Success";
		} catch (Exception e) {
			String msg = "Exception Occured while copying Auto Compute Data";
			LOGGER.error(msg, e);
			throw new AppException(e.getMessage(), e);
		}

	}

	private Gstr9UserInputEntity convertDataToEntity(
			Gstr9AutoCalculateEntity getAutoComp, String fy, String taxPeriod,
			Map<String, String> itcTypeMap) {
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
		userEntity.setGstin(getAutoComp.getGstin());
		userEntity.setFy(fy);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setDerivedRetPeriod(getAutoComp.getDerivedRetPeriod());
		userEntity.setSection(getAutoComp.getSection());
		userEntity.setSubSection(getAutoComp.getSubSection());
		userEntity.setTxVal(getAutoComp.getTxVal());
		userEntity.setIgst(getAutoComp.getIamt());
		userEntity.setCgst(getAutoComp.getCamt());
		userEntity.setSgst(getAutoComp.getSamt());
		userEntity.setCess(getAutoComp.getCsamt());
		userEntity.setTxpaidCash(getAutoComp.getTxPaidCash());
		userEntity.setTaxPaidItcIamt(getAutoComp.getTaxPaidItcIamt());
		userEntity.setTaxPaidItcSamt(getAutoComp.getTaxPaidItcSamt());
		userEntity.setTaxPaidItcCamt(getAutoComp.getTaxPaidItcCamt());
		userEntity.setTaxPaidItcCSamt(getAutoComp.getTaxPaidItcCSamt());
		userEntity.setTxPyble(getAutoComp.getTxPyble());
		userEntity.setSource("GSTN");
		userEntity.setActive(true);
		userEntity.setCreatedBy("SYSTEM");
		String docKey = CommonUtility.generateGstr9DocKey(
				getAutoComp.getGstin(), fy, getAutoComp.getSubSection());
		userEntity.setDocKey(docKey);
		userEntity.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(getAutoComp.getSubSection()));
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setItcTyp(itcTypeMap.get(userEntity.getSubSection()));
		return userEntity;
	}

	private Gstr9UserInputEntity convertToEntityForTbl9ForAutoComp(
			List<Gstr9AutoCalculateEntity> table9AutoCompList, String gstin,
			String fy, String userName, String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside Gstr9EntityLevelServiceImpl"
					+ ".convertToEntityForTbl9ForAutoComp method :");
		}
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();

		table9AutoCompList.forEach((input) -> {
			if (input.getSubSection().equalsIgnoreCase("9A"))
				userEntity.setIgst(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9B"))
				userEntity.setCgst(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9C"))
				userEntity.setSgst(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9D"))
				userEntity.setCess(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9E"))
				userEntity.setIntr(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9F"))
				userEntity.setFee(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9G"))
				userEntity.setPen(input.getTxPyble());
			else if (input.getSubSection().equalsIgnoreCase("9H"))
				userEntity.setOth(input.getTxPyble());
			userEntity.setTxpaidCash(input.getTxPaidCash());
			userEntity.setTaxPaidItcIamt(input.getTaxPaidItcIamt());
			userEntity.setTaxPaidItcSamt(input.getTaxPaidItcSamt());
			userEntity.setTaxPaidItcCamt(input.getTaxPaidItcCamt());
			userEntity.setTaxPaidItcCSamt(input.getTaxPaidItcCSamt());
			userEntity.setDerivedRetPeriod(input.getDerivedRetPeriod());
		});
		String docKey = CommonUtility.generateGstr9DocKey(gstin, fy,
				Gstr9TaxPaidConstants.Table_9);
		userEntity.setDocKey(docKey);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setGstin(gstin);
		userEntity.setFy(fy);
		userEntity.setCreatedOn(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setSection(Gstr9TaxPaidConstants.Table_9);
		userEntity.setSubSection(Gstr9TaxPaidConstants.Table_9);
		userEntity.setSource("GSTN");
		userEntity
				.setNatureOfSupplies(Gstr9Util.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9));
		userEntity.setActive(true);
		return userEntity;
	}
	
	
	private Gstr9UserInputEntity convertDataToEntityForCopyDataToUserInput(
			Gstr9DigiComputeEntity digiCompute, String gstin,
			String fy, String userName, String taxPeriod, Map<String,String> itcTypeMap){
		
		Gstr9UserInputEntity userEntity = new Gstr9UserInputEntity();
		userEntity.setGstin(digiCompute.getGstin());
		userEntity.setFy(fy);
		userEntity.setRetPeriod(taxPeriod);
		userEntity.setDerivedRetPeriod(digiCompute.getDerivedRetPeriod());
		userEntity.setSection(digiCompute.getSection());
		userEntity.setSubSection(digiCompute.getSubSection());
		userEntity.setTxVal((BigDecimal)digiCompute.getTaxableValue());
		userEntity.setIgst((BigDecimal)digiCompute.getIgstAmount());
		userEntity.setCgst((BigDecimal)digiCompute.getCgstAmount());
		userEntity.setSgst((BigDecimal)digiCompute.getSgstAmount());
		userEntity.setCess((BigDecimal)digiCompute.getCessAmount());
		userEntity.setSource(digiCompute.getSection().contains("4") || digiCompute.getSection().contains("5") ? "U" : "E");			
		userEntity.setActive(true);
		userEntity.setCreatedBy(userName);
		String docKey = CommonUtility.generateGstr9DocKey(
				digiCompute.getGstin(), fy, digiCompute.getSubSection());
		userEntity.setDocKey(docKey);
		userEntity.setNatureOfSupplies(Gstr9Util
				.getNatureOfSuppliesForSubSection(digiCompute.getSubSection()));
		userEntity.setCreatedOn(LocalDateTime.now());
		if(digiCompute.getSection().contains("6") || digiCompute.getSection().contains("7") || digiCompute.getSection().contains("8")){
			userEntity.setItcTyp(itcTypeMap.get(digiCompute.getSubSection()));
		}		
		return userEntity;	
	}
	
	
	private Gstr9HsnProcessEntity convertDataToHsnEntityForCopyDataToUserInput(
			Gstr9DigiComputeEntity digiCompute, String gstin,
			String fy, String userName, String taxPeriod){
		
		Gstr9HsnProcessEntity gstr9ProcessEntity = new Gstr9HsnProcessEntity();
				
		gstr9ProcessEntity.setHsn(digiCompute.getHsnsac());
	//	gstr9ProcessEntity.setDesc(digiCompute.getDerivedRetPeriod());
		gstr9ProcessEntity.setRateOfTax(digiCompute.getTaxRate());
		gstr9ProcessEntity.setUqc(digiCompute.getUqc());
		gstr9ProcessEntity.setTotalQnt(digiCompute.getQty());
		gstr9ProcessEntity.setTaxableVal(digiCompute.getTaxableValue());
		gstr9ProcessEntity.setConRateFlag(digiCompute.getIsConcesstional());
		gstr9ProcessEntity.setIgst(digiCompute.getIgstAmount());
		gstr9ProcessEntity.setCgst(digiCompute.getCgstAmount());
		gstr9ProcessEntity.setSgst(digiCompute.getSgstAmount());
		gstr9ProcessEntity.setCess(digiCompute.getCessAmount());
		gstr9ProcessEntity.setGstin(gstin);
		gstr9ProcessEntity.setTableNumber(digiCompute.getSubSection());
		gstr9ProcessEntity.setCreatedBy(userName);
		gstr9ProcessEntity.setDelete(false);
		gstr9ProcessEntity.setCreatedOn(LocalDateTime.now());
		gstr9ProcessEntity.setFy(fy);
		String docKey = CommonUtility.generateGstr9DocKey(
				digiCompute.getGstin(), fy, digiCompute.getSubSection(),digiCompute.getHsnsac(),
				digiCompute.getTaxRate().toString(),digiCompute.getUqc());
		
		gstr9ProcessEntity.setGst9HsnDocKey(docKey);
		gstr9ProcessEntity.setRetPeriod(digiCompute.getRetPeriod());
		gstr9ProcessEntity.setSource("U");
		return gstr9ProcessEntity;
	}
}
