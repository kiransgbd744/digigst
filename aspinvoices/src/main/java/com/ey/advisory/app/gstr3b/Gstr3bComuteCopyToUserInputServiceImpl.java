package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("Gstr3bComuteCopyToUserInputServiceImpl")
public class Gstr3bComuteCopyToUserInputServiceImpl
		implements Gstr3bComuteCopyToUserInputService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("Gstr3bComuteCopyToUserInputDaoImpl")
	private Gstr3bComuteCopyToUserInputDao dao;

	@Autowired
	private Gstr3BGstinDashboardDao gstnDao;

	@Autowired
	private Gstr3bUtil gstr3bUtil;

	private static List<String> allOtherItcSections = new ArrayList<>(
			Arrays.asList("4(a)(1)(1.1)", "4(a)(1)(1.2)", "4(a)(3)(3.1)",
					"4(a)(3)(3.1.a)", "4(a)(3)(3.1.b)", "4(a)(3)(3.2)",
					"4(a)(4)(4.1)", "4(a)(4)(4.2)", "4(a)(4)(4.2.a)",
					"4(a)(4)(4.2.b)", "4(a)(4)(4.3)", "4(a)(5)(5.1)",
					"4(a)(5)(5.1.a)", "4(a)(5)(5.1.b)", "4(a)(5)(5.1.c)",
					"4(a)(5)(5.1.d)", "4(a)(5)(5.2)", "4(a)(5)(5.2.a)",
					"4(a)(5)(5.2.b)", "4(a)(5)(5.2.c)", "4(b)(1)(1.1)",
					"4(b)(1)(1.2)", "4(b)(1)(1.2.a)", "4(b)(1)(1.2.b)",
					"4(b)(1)(1.2.c)", "4(b)(2)(2.1)", "4(b)(2)(2.2)",
					"4(b)(2)(2.3)", "4(b)(2)(2.3.a)", "4(b)(2)(2.3.b)",
					"4(b)(2)(2.3.c)", "4(d)(1)",
					"4(a)(1)(1.1.a)","4(a)(1)(1.1.b)","4(a)(1)(1.1.c)",
					"4(a)(3)(3.1.a.a)","4(a)(3)(3.1.a.b)","4(a)(3)(3.1.a.c)",
					"4(a)(4)(4.4)"));

	List<String> interestAndLateFeeSection = new ArrayList<>(
			Arrays.asList("5.1(a)", "5.1(b)"));

	private static final String ASP_COMPUTE = "ASPComp";
	private static final String GSTN_COMPUTE = "AutoCalc";

	@Override
	public String copyToUserInput(String taxPeriod, String gstin,
			String inwardFlag, String outwardFlag,
			String interestAndLateFeeFlag) throws AppException {
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info(" Inside Gstr3bComuteCopyToUserInputServiceImpl"
						+ ".copyToUserInput() method :");
			}
			Map<String, Gstr3BGstinDashboardDto> gst3bMap = gstr3bUtil
					.getGstr3BGstinDashboardMap();

			List<String> section3_2NameList = new ArrayList<>();
			section3_2NameList.add(Gstr3BConstants.Table3_2_A);
			section3_2NameList.add(Gstr3BConstants.Table3_2_B);
			section3_2NameList.add(Gstr3BConstants.Table3_2_C);

			List<Gstr3BGstinAspUserInputDto> computeDataList = dao
					.gstr3bCopyData(taxPeriod, gstin);

			List<Gstr3BGstinAspUserInputDto> autoCalList = gstnDao
					.getGstinAutoCalDtoList(gstin, taxPeriod);

			List<String> allSectionNameList = gst3bMap.keySet().stream()
					.distinct().collect(Collectors.toList());

			allSectionNameList.addAll(allOtherItcSections);

			List<String> outward = getOutwardSectionList();
			List<String> inward = getInwardSectionList(allSectionNameList);
			// interestAndLateFeeSection

			List<Gstr3BGstinAspUserInputEntity> entity = new ArrayList<>();
			// my code
			if (inwardFlag.equalsIgnoreCase("Blank")) {

				List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = inward
						.stream().map(o -> setDefaultValue(o, gstin, taxPeriod,
								gst3bMap))
						.collect(Collectors.toList());

				LOGGER.info(
						"Gstr3bComuteCopyToUserInputServiceImpl : setting as "
								+ "default zero value for all other sections ");

				entity.addAll(DefaultAutoCalEntities);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Gstr3bComuteCopyToUserInputServiceImpl : "
									+ "DefaultAutoCalEntities added to entity %s",
							DefaultAutoCalEntities);

			}

			if (outwardFlag.equalsIgnoreCase("Blank")) {

				List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = outward
						.stream().map(o -> setDefaultValue(o, gstin, taxPeriod,
								gst3bMap))
						.collect(Collectors.toList());

				LOGGER.info(
						"Gstr3bComuteCopyToUserInputServiceImpl : setting as "
								+ "default zero value for all other sections ");

				entity.addAll(DefaultAutoCalEntities);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Gstr3bComuteCopyToUserInputServiceImpl : "
									+ "DefaultAutoCalEntities added to entity %s",
							DefaultAutoCalEntities);

			}
			/// my code

			// outward digigst radio button
			if (outwardFlag.equalsIgnoreCase(ASP_COMPUTE)) {
				// inward digigst radio button
				if (inwardFlag.equalsIgnoreCase(ASP_COMPUTE)
						&& (computeDataList == null
								|| computeDataList.isEmpty())) {
					String msg = String
							.format("ASP Compute has no data for taxperiod %s "
									+ "and Gstin %s ", taxPeriod, gstin);
					LOGGER.debug(msg);

					List<String> sections = gst3bMap.keySet().stream()
							.distinct().collect(Collectors.toList());

					sections.addAll(allOtherItcSections);
					// softDelate
					userRepo.updateActiveFlag(taxPeriod, gstin, sections);
					LOGGER.info("Gstr3bComuteCopyToUserInputServiceImpl : "
							+ "updateActiveFlag");

					sections.removeAll(section3_2NameList);
					sections.removeAll(interestAndLateFeeSection);

					List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = sections
							.stream().map(o -> setDefaultValue(o, gstin,
									taxPeriod, gst3bMap))
							.collect(Collectors.toList());

					userRepo.saveAll(DefaultAutoCalEntities);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr3bComuteCopyToUserInputServiceImpl u"
								+ "pdating default values");
					}

				}
				// outward digigstn radio button
				List<Gstr3BGstinAspUserInputDto> outwardListCompute = gstnDao
						.getComputeDtoBySection(taxPeriod, gstin, outward);

				List<String> outwardComputeSectins = outwardListCompute.stream()
						.map(o -> o.getSectionName())
						.collect(Collectors.toList());

				outward.removeAll(section3_2NameList);
				outward.removeAll(outwardComputeSectins);

				List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = outward
						.stream().map(o -> setDefaultValue(o, gstin, taxPeriod,
								gst3bMap))
						.collect(Collectors.toList());

				List<Gstr3BGstinAspUserInputEntity> outwardComputeEntities = outwardListCompute
						.stream().map(o -> convertToEntity(o, gstin, taxPeriod))
						.collect(Collectors.toList());

				LOGGER.info(
						"Gstr3bComuteCopyToUserInputServiceImpl : setting as "
								+ "default zero value for all other sections ");

				outwardComputeEntities.addAll(DefaultAutoCalEntities);

				entity.addAll(outwardComputeEntities);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr3bComuteCopyToUserInputServiceImpl : "
									+ "outwardComputeEntities added to entity %s",
							outwardComputeEntities);
				}
			} else {// inward gstn radio button
				if (inwardFlag.equalsIgnoreCase(GSTN_COMPUTE)
						&& (autoCalList == null || autoCalList.isEmpty())) {
					String msg = String
							.format("GSTN Compute has no data for taxperiod %s "
									+ "and Gstin %s ", taxPeriod, gstin);
					LOGGER.debug(msg);

					List<String> sections = gst3bMap.keySet().stream()
							.distinct().collect(Collectors.toList());

					sections.addAll(allOtherItcSections);
					// softDelate
					userRepo.updateActiveFlag(taxPeriod, gstin, sections);
					LOGGER.info("Gstr3bComuteCopyToUserInputServiceImpl : "
							+ "updateActiveFlag");

					sections.removeAll(section3_2NameList);

					List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = sections
							.stream().map(o -> setDefaultValue(o, gstin,
									taxPeriod, gst3bMap))
							.collect(Collectors.toList());

					userRepo.saveAll(DefaultAutoCalEntities);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Gstr3bComuteCopyToUserInputServiceImpl u"
								+ "pdating default values");
					}

				}
				// outward gtin--issue
				if (outwardFlag.equalsIgnoreCase(GSTN_COMPUTE)) {
					List<Gstr3BGstinAspUserInputDto> outwardListAuto = gstnDao
							.getAutoCalDtoBySection(taxPeriod, gstin, outward);

					List<String> outwardAutoSectins = outwardListAuto.stream()
							.map(o -> o.getSectionName())
							.collect(Collectors.toList());

					outward.removeAll(section3_2NameList);
					outward.removeAll(outwardAutoSectins);

					List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = outward
							.stream()
							.map(o -> setDefaultValue(o, gstin, taxPeriod,
									gst3bMap))
							.collect(Collectors.toList());

					List<Gstr3BGstinAspUserInputEntity> outwarAutoCalEntities = outwardListAuto
							.stream()
							.map(o -> convertToEntity(o, gstin, taxPeriod))
							.collect(Collectors.toList());

					LOGGER.info(
							"Gstr3bComuteCopyToUserInputServiceImpl : setting as "
									+ "default zero value for all other sections ");

					outwarAutoCalEntities.addAll(DefaultAutoCalEntities);
					entity.addAll(outwarAutoCalEntities);
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"Gstr3bComuteCopyToUserInputServiceImpl : "
										+ "outwarAutoCalEntities added to entity %s",
								outwarAutoCalEntities);
				}
			}

			if (inwardFlag.equalsIgnoreCase(ASP_COMPUTE)) {

				List<Gstr3BGstinAspUserInputDto> inwardListCompute = gstnDao
						.getComputeDtoBySection(taxPeriod, gstin, inward);

				List<String> inwardComputeSectins = inwardListCompute.stream()
						.map(o -> o.getSectionName())
						.collect(Collectors.toList());
				inward.removeAll(inwardComputeSectins);
				inward.removeAll(allOtherItcSections);

				List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = inward
						.stream().map(o -> setDefaultValue(o, gstin, taxPeriod,
								gst3bMap))
						.collect(Collectors.toList());

				List<Gstr3BGstinAspUserInputEntity> computeInwardEntities = inwardListCompute
						.stream().map(o -> convertToEntity(o, gstin, taxPeriod))
						.collect(Collectors.toList());

				LOGGER.info(
						"Gstr3bComuteCopyToUserInputServiceImpl : setting as "
								+ "default zero value for all other sections ");

				computeInwardEntities.addAll(DefaultAutoCalEntities);
				entity.addAll(computeInwardEntities);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Gstr3bComuteCopyToUserInputServiceImpl : "
									+ "computeInwardEntities added to entity %s",
							computeInwardEntities);

			}
			if (inwardFlag.equalsIgnoreCase(GSTN_COMPUTE)) {

				List<Gstr3BGstinAspUserInputDto> inwardListAuto = gstnDao
						.getAutoCalDtoBySection(taxPeriod, gstin, inward);

				List<String> inwardAutoSectins = inwardListAuto.stream()
						.map(o -> o.getSectionName())
						.collect(Collectors.toList());
				inward.removeAll(inwardAutoSectins);
				inward.removeAll(allOtherItcSections);
				List<Gstr3BGstinAspUserInputEntity> DefaultAutoCalEntities = inward
						.stream().map(o -> setDefaultValue(o, gstin, taxPeriod,
								gst3bMap))
						.collect(Collectors.toList());

				List<Gstr3BGstinAspUserInputEntity> autoCalInwardEntities = inwardListAuto
						.stream().map(o -> convertToEntity(o, gstin, taxPeriod))
						.collect(Collectors.toList());

				LOGGER.info(
						"Gstr3bComuteCopyToUserInputServiceImpl : setting as "
								+ "default zero value for all other sections ");

				autoCalInwardEntities.addAll(DefaultAutoCalEntities);

				entity.addAll(autoCalInwardEntities);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"Gstr3bComuteCopyToUserInputServiceImpl : "
									+ "autoCalInwardEntities added to entity %s",
							autoCalInwardEntities);
			}

			// late fee and Interest
			List<String> interestSection = new ArrayList<>(
					Arrays.asList("5.1(a)", "5.1(b)"));
			List<Gstr3BGstinAspUserInputDto> intrLatefeeList = gstnDao
					.getAutoCalDtoBySection(taxPeriod, gstin, interestSection);

			List<String> intrLatefeeListSection = intrLatefeeList.stream()
					.map(o -> o.getSectionName()).collect(Collectors.toList());

			interestAndLateFeeSection.removeAll(intrLatefeeListSection);

			List<Gstr3BGstinAspUserInputEntity> DefaultInterestEntities = interestAndLateFeeSection
					.stream()
					.map(o -> setDefaultValue(o, gstin, taxPeriod, gst3bMap))
					.collect(Collectors.toList());

			List<Gstr3BGstinAspUserInputEntity> interestEntities = intrLatefeeList
					.stream().map(o -> convertToEntity(o, gstin, taxPeriod))
					.collect(Collectors.toList());

			LOGGER.info("Gstr3bComuteCopyToUserInputServiceImpl : setting as "
					+ "default zero value for all other sections ");

			interestEntities.addAll(DefaultInterestEntities);

			entity.addAll(interestEntities);

			List<String> sectionsToBeSoftDeleted = gst3bMap.keySet().stream()
					.distinct().collect(Collectors.toList());

			sectionsToBeSoftDeleted.addAll(allOtherItcSections);

			// softDelate
			userRepo.updateActiveFlag(taxPeriod, gstin,
					sectionsToBeSoftDeleted);
			LOGGER.info("Gstr3bComuteCopyToUserInputServiceImpl : "
					+ "updateActiveFlag");

			userRepo.saveAll(entity);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr3bComuteCopyToUserInputServiceImpl : "
						+ "entity %s Saved to db", entity);
			}
			return "success";

		} catch (Exception ex) {
			String msg = String.format(
					"Exception while saving '%s' GSTIN - '%s'Taxperiod ", gstin,
					taxPeriod);
			LOGGER.error(msg, ex);
			throw ex;
		}
	}

	private List<String> getOutwardSectionList() {
		List<String> allAutoCalSectionList = new ArrayList<>();
		allAutoCalSectionList.add(Gstr3BConstants.Table3_1_A);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_1_B);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_1_C);
		// allAutoCalSectionList.add(Gstr3BConstants.Table3_1_D);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_1_E);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_2_A);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_2_B);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_2_C);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_1_1_A);
		allAutoCalSectionList.add(Gstr3BConstants.Table3_1_1_B);
		return allAutoCalSectionList;
	}

	String userName = (SecurityContext.getUser() != null
			&& SecurityContext.getUser().getUserPrincipalName() != null)
					? SecurityContext.getUser().getUserPrincipalName()
					: "SYSTEM";

	private Gstr3BGstinAspUserInputEntity convertToEntity(
			Gstr3BGstinAspUserInputDto userInput, String gstin,
			String taxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3bComuteCopyToUserInputServiceImpl"
					+ ".convertToEntity method :");
		}

		BigDecimal zero = BigDecimal.ZERO;

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		/*BigDecimal cess = (userInput.getCess()).signum() != -1
				? userInput.getCess() : zero;

		BigDecimal cgst = (userInput.getCgst()).signum() != -1
				? userInput.getCgst() : zero;

		BigDecimal igst = (userInput.getIgst()).signum() != -1
				? userInput.getIgst() : zero;

		BigDecimal sgst = (userInput.getSgst()).signum() != -1
				? userInput.getSgst() : zero;

		BigDecimal taxableVal = (userInput.getTaxableVal()).signum() != -1
				? userInput.getTaxableVal() : zero;

		BigDecimal interState = (userInput.getInterState()).signum() != -1
				? userInput.getInterState() : zero;

		BigDecimal intraState = (userInput.getIntraState()).signum() != -1
				? userInput.getIntraState() : zero;*/
		
		userEntity.setCess(userInput.getCess());
		userEntity.setCgst(userInput.getCgst());
		userEntity.setIgst(userInput.getIgst());
		userEntity.setSgst(userInput.getSgst());
		userEntity.setTaxableVal(userInput.getTaxableVal());
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(LocalDateTime.now());
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(userInput.getInterState());
		userEntity.setIntraState(userInput.getIntraState());
		userEntity.setSectionName(userInput.getSectionName());
		userEntity.setSubSectionName(userInput.getSubSectionName());
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(userInput.getPos());
		return userEntity;
	}

	private Gstr3BGstinAspUserInputEntity setDefaultValue(String sectionName,
			String gstin, String taxPeriod,
			Map<String, Gstr3BGstinDashboardDto> gst3bMap) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BGstinDashboardServiceImpl"
					+ ".setDefaultValue() method :");
		}

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		BigDecimal defaultValue = BigDecimal.ZERO;
		userEntity.setCess(defaultValue);
		userEntity.setCgst(defaultValue);
		userEntity.setIgst(defaultValue);
		userEntity.setSgst(defaultValue);
		userEntity.setTaxableVal(defaultValue);
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(LocalDateTime.now());
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(defaultValue);
		userEntity.setIntraState(defaultValue);
		userEntity.setSectionName(sectionName);
		if (!allOtherItcSections.contains(sectionName)) {
			userEntity.setSubSectionName(
					gst3bMap.get(sectionName).getSupplyType());
		} else {
			userEntity.setSubSectionName("AOISub");
		}
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(null);
		return userEntity;

	}

	private List<String> getInwardSectionList(List<String> allSectionNameList) {

		List<String> outward = getOutwardSectionList();

		allSectionNameList.removeAll(outward);
		allSectionNameList.removeAll(interestAndLateFeeSection);
		return allSectionNameList;
	}
}
