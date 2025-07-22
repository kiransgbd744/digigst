/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGetLiabilityAutoCalcRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspComputeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstr3BConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Component("Gstr3BGstinNewDashboardServiceImpl")
@Slf4j
public class Gstr3BGstinNewDashboardServiceImpl
		implements Gstr3BGstinNewDashboardService {

	@Autowired
	private Gstr3bUtil gstr3bUtil;

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspComputeRepository")
	private Gstr3BGstinAspComputeRepository digiRepo;

	@Autowired
	@Qualifier("Gstr3BGetLiabilityAutoCalcRepository")
	private Gstr3BGetLiabilityAutoCalcRepository gstnRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static List<String> l2Sections = ImmutableList.of(
			Gstr3BConstants.Table4_A_3_1_a, Gstr3BConstants.Table4_A_3_1_b,
			Gstr3BConstants.Table4_A_4_2_a, Gstr3BConstants.Table4_A_4_2_b,
			Gstr3BConstants.Table4_A_1_1_A, Gstr3BConstants.Table4_A_1_1_B,
			Gstr3BConstants.Table4_A_1_1_C);

	private static List<String> l3Sections = ImmutableList.of(
			Gstr3BConstants.Table4_A_3_1_a_a, Gstr3BConstants.Table4_A_3_1_a_b,
			Gstr3BConstants.Table4_A_3_1_a_c);

	@Override
	public List<Gstr3BNewSuppliesDto> get4SubsectionsData(String taxPeriod,
			String gstin, String subsection, Long entityId) {
		List<Gstr3BNewSuppliesDto> respList = new ArrayList<>();
		List<String> sections = null;
		Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4 = null;

		try {

			int count = 0;
			int count4a1 = 0;
			int count4a3 = 0;
			String optedOption = "A";
			/*
			 * List<EntityInfoEntity> entityIds = entityInfo
			 * .findByPanAndIsDeleteFalse(gstin.substring(2, 12));
			 */
			optedOption = onbrdOptionOpted(entityId);

			switch (subsection) {
			case "4.a.1":
				sections = ImmutableList.of(Gstr3BConstants.Table4_A_1_1,
						Gstr3BConstants.Table4_A_1_1_A,
						Gstr3BConstants.Table4_A_1_1_B,
						Gstr3BConstants.Table4_A_1_1_C,
						Gstr3BConstants.Table4_A_1_2);
				gstr3B4 = gstr3bUtil.gstr3B4_1();
				break;
			case "4.a.3":
				sections = ImmutableList.of(Gstr3BConstants.Table4_A_3_1,
						Gstr3BConstants.Table4_A_3_1_a,
						Gstr3BConstants.Table4_A_3_1_a_a,
						Gstr3BConstants.Table4_A_3_1_a_b,
						Gstr3BConstants.Table4_A_3_1_a_c,
						Gstr3BConstants.Table4_A_3_1_b,
						Gstr3BConstants.Table4_A_3_2);
				gstr3B4 = gstr3bUtil.gstr3B4_3();
				break;
			case "4.a.4":
				sections = ImmutableList.of(Gstr3BConstants.Table4_A_4_1,
						Gstr3BConstants.Table4_A_4_2,
						Gstr3BConstants.Table4_A_4_2_a,
						Gstr3BConstants.Table4_A_4_2_b,
						Gstr3BConstants.Table4_A_4_3,
						Gstr3BConstants.Table4_A_4_4);
				gstr3B4 = gstr3bUtil.gstr3B4_4();
				break;
			}
			List<Gstr3BGstinAspUserInputEntity> respUser = userRepo
					.getITC10PercSectionData(taxPeriod, gstin, sections);

			List<Gstr3BGstinAspComputeEntity> respDigi = digiRepo
					.findBySectionNameList(taxPeriod, gstin, sections);

			List<Gstr3BGetLiabilityAutoCalcEntity> respGstn = gstnRepo
					.findBySectionNameList(taxPeriod, gstin, sections);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinNewDashboardServiceImpl"
						+ ".getSubsectionsData respUser list Size"
						+ respUser.size() + ", respDigi size = "
						+ respUser.size() + "respGstn size" + respGstn.size());
			}
			// Creating map by adding values
			Map<String, Gstr3BGstinAspUserInputEntity> userMap = respUser
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalUserEntity(a, b)),
									Optional::get)));

			Map<String, Gstr3BGstinAspComputeEntity> digiMap = respDigi.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalAspEntity(a, b)),
									Optional::get)));

			Map<String, Gstr3BGetLiabilityAutoCalcEntity> gstnMap = respGstn
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalGstnEntity(a, b)),
									Optional::get)));

			// Creating final dto based on section names
			for (Map.Entry<String, Gstr3BExcemptNilNonGstnDto> entry : gstr3B4
					.entrySet()) {
				Gstr3BNewSuppliesDto dto = new Gstr3BNewSuppliesDto();
				if (userMap.containsKey(entry.getKey())) {
					Gstr3BGstinAspUserInputEntity entity = userMap
							.get(entry.getKey());
					dto.setUserIgst(entity.getIgst());
					dto.setUserCgst(entity.getCgst());
					dto.setUserCess(entity.getCess());
					dto.setUserSgst(entity.getSgst());
					if ("4.a.4".equalsIgnoreCase(subsection)) {
						if (entity.getRadioFlag() != null
								&& entity.getRadioFlag()) {
							count += 1;
						}
					}
					if ("4.a.1".equalsIgnoreCase(subsection)) {
						if (entity.getRadioFlag() != null
								&& entity.getRadioFlag()) {
							count4a1 += 1;
						}
					}

					if ("4.a.3".equalsIgnoreCase(subsection)) {
						if (entity.getRadioFlag() != null
								&& entity.getRadioFlag()) {
							count4a3 += 1;
						}
					}

					dto.setRadioFlag(entity.getRadioFlag());
				}
				if (entry.getKey().equals("4(a)(3)(3.1)")
						|| entry.getKey().equals("4(a)(4)(4.2)")
						|| digiMap.containsKey(entry.getKey())) {
					Gstr3BGstinAspComputeEntity entity = new Gstr3BGstinAspComputeEntity();

					if (entry.getKey().equals("4(a)(3)(3.1)")) {
						entity = sumOf2Sections(
								digiMap.containsKey("4(a)(3)(3.1.a)")
										? digiMap.get("4(a)(3)(3.1.a)")
										: new Gstr3BGstinAspComputeEntity(),
								digiMap.containsKey("4(a)(3)(3.1.b)")
										? digiMap.get("4(a)(3)(3.1.b)")
										: new Gstr3BGstinAspComputeEntity(),
								entity);

					} else if (entry.getKey().equals("4(a)(4)(4.2)")) {
						entity = sumOf2Sections(
								digiMap.containsKey("4(a)(4)(4.2.a)")
										? digiMap.get("4(a)(4)(4.2.a)")
										: new Gstr3BGstinAspComputeEntity(),
								digiMap.containsKey("4(a)(4)(4.2.b)")
										? digiMap.get("4(a)(4)(4.2.b)")
										: new Gstr3BGstinAspComputeEntity(),
								entity);

					} else {

						entity = digiMap.get(entry.getKey());
					}
					dto.setDigiIgst(entity.getIgst());
					dto.setDigiCgst(entity.getCgst());
					dto.setDigiCess(entity.getCess());
					dto.setDigiSgst(entity.getSgst());
					// added as a part of 130203
					dto.setUserIgst(entity.getIgst());
					dto.setUserCgst(entity.getCgst());
					dto.setUserCess(entity.getCess());
					dto.setUserSgst(entity.getSgst());

				}

				if (entry.getKey().equals("4(a)(3)(3.1)")
						|| entry.getKey().equals("4(a)(4)(4.2)")
						|| gstnMap.containsKey(entry.getKey())) {
					Gstr3BGetLiabilityAutoCalcEntity entity = new Gstr3BGetLiabilityAutoCalcEntity();

					if (entry.getKey().equals("4(a)(3)(3.1)")) {

						entity = sumOf2SectionsGstn(
								gstnMap.containsKey("4(a)(3)(3.1.a)")
										? gstnMap.get("4(a)(3)(3.1.a)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),
								gstnMap.containsKey("4(a)(3)(3.1.b)")
										? gstnMap.get("4(a)(3)(3.1.b)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),
								entity);

					} else if (entry.getKey().equals("4(a)(4)(4.2)")) {
						entity = sumOf2SectionsGstn(
								gstnMap.containsKey("4(a)(4)(4.2.a)")
										? gstnMap.get("4(a)(4)(4.2.a)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),
								gstnMap.containsKey("4(a)(4)(4.2.b)")
										? gstnMap.get("4(a)(4)(4.2.b)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),
								entity);

					} else {

						entity = gstnMap.get(entry.getKey());
					}
					dto.setAutoCompIgst(entity.getIgst());
					dto.setAutoCompCgst(entity.getCgst());
					dto.setAutoCompCess(entity.getCess());
					dto.setAutoCompSgst(entity.getSgst());
				}
				if (l2Sections.contains(entry.getKey()))
					dto.setLevel("L2");

				if (l3Sections.contains(entry.getKey()))
					dto.setLevel("L3");

				dto.setSectionName(entry.getValue().getSectionName());
				dto.setSubSectionName(entry.getValue().getSubSectionName());

				respList.add(dto);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinNewDashboardServiceImpl"
						+ ".getSubsectionsData returning response"
						+ ", response size = " + respList.size() + "");
			}

			if ((count == 0) && ("4.a.4".equalsIgnoreCase(subsection))) {
				for (Gstr3BNewSuppliesDto dto : respList) {

					if ("A".equalsIgnoreCase(optedOption)) {
						if ("4(a)(4)(4.1)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					} else if ("B".equalsIgnoreCase(optedOption)) {
						if ("4(a)(4)(4.2)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					}
				}
			}

			//

			if ((count4a1 == 0) && ("4.a.1".equalsIgnoreCase(subsection))) {
				for (Gstr3BNewSuppliesDto dto : respList) {

					if ("A".equalsIgnoreCase(optedOption)
							|| "C".equalsIgnoreCase(optedOption)) {
						if ("4(a)(1)(1.1.a)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					} else if ("B".equalsIgnoreCase(optedOption)) {
						if ("4(a)(1)(1.1.b)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					}
				}
			}

			if ((count4a3 == 0) && ("4.a.3".equalsIgnoreCase(subsection))) {
				for (Gstr3BNewSuppliesDto dto : respList) {

					if ("A".equalsIgnoreCase(optedOption)
							|| "C".equalsIgnoreCase(optedOption)) {
						if ("4(a)(3)(3.1.a.a)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					} else if ("B".equalsIgnoreCase(optedOption)) {
						if ("4(a)(3)(3.1.a.b)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					}
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinNewDashboardServiceImpl"
						+ ".getSubsectionsData returning response"
						+ ", response = " + respList + "");
			}
			return respList;
		} catch (Exception ex) {
			LOGGER.error("Error occured in GSTR3B Sub section service Impl",
					ex);
			throw new AppException(ex);

		}
	}

	@Override
	public List<Gstr3BInfoSuppliesDto> getInfoSubsectionsData(String taxPeriod,
			String gstin, String subsection) {
		List<Gstr3BInfoSuppliesDto> respList = new ArrayList<>();
		List<String> sections = null;
		Map<String, Gstr3BExcemptNilNonGstnDto> gstr3B4 = null;

		try {
			switch (subsection) {
			case "4A-1.1":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_1_1_1_a,
						Gstr3BConstants.Table4_a_1_1_1_b,
						Gstr3BConstants.Table4_a_1_1_1_c);
				gstr3B4 = gstr3bUtil.gstr3B4A_1();
				break;
			//new 	
			case "4A-1.1.b":
				sections = ImmutableList.of(Gstr3BConstants.Table4_A_1_1_B_A,
						Gstr3BConstants.Table4_A_1_1_B_B,
						Gstr3BConstants.Table4_A_1_1_B_C);
				gstr3B4 = gstr3bUtil.gstr3B4A_1_B();
				break;	
				
			case "4A-1.2":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_1_1_2_a,
						Gstr3BConstants.Table4_a_1_1_2_b);
				gstr3B4 = gstr3bUtil.gstr3B4A_12();
				break;
			case "4A-3.1.a":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_3_3_1_a_a,
						Gstr3BConstants.Table4_a_3_3_1_a_b,
						Gstr3BConstants.Table4_a_3_3_1_a_c);
				gstr3B4 = gstr3bUtil.gstr3B4A_31();
				break;
				
				//new 
			case "4A-3.1.a.b":
				sections = ImmutableList.of(Gstr3BConstants.Table4_A_3_1_a_b_a,
						Gstr3BConstants.Table4_A_3_1_a_b_b,
						Gstr3BConstants.Table4_A_3_1_a_b_c);
				gstr3B4 = gstr3bUtil.gstr3B4A_31_B();
				break;
				
			case "4A-3.1.b":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_3_3_1_b_a,
						Gstr3BConstants.Table4_a_3_3_1_b_b);
				gstr3B4 = gstr3bUtil.gstr3B4A_31b();
				break;
			case "4A-4.2.a":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_4_4_2_a_a,
						Gstr3BConstants.Table4_a_4_4_2_a_b,
						Gstr3BConstants.Table4_a_4_4_2_a_c);
				gstr3B4 = gstr3bUtil.gstr3B4A_42a();
				break;
			case "4A-4.2.b":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_4_4_2_b_a,
						Gstr3BConstants.Table4_a_4_4_2_b_b);
				gstr3B4 = gstr3bUtil.gstr3B4A_42b();
				break;
			case "4A-5.1.b":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_5_5_1_b_a,
						Gstr3BConstants.Table4_a_5_5_1_b_b,
						Gstr3BConstants.Table4_a_5_5_1_b_c);
				gstr3B4 = gstr3bUtil.gstr3B4A_51b();
				break;
			case "4A-5.2.c":
				sections = ImmutableList.of(Gstr3BConstants.Table4_a_5_5_2_c_a,
						Gstr3BConstants.Table4_a_5_5_2_c_b);
				gstr3B4 = gstr3bUtil.gstr3B4A_51c();
				break;

			}

			List<Gstr3BGstinAspComputeEntity> respDigi = digiRepo
					.findBySectionNameList(taxPeriod, gstin, sections);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinNewDashboardServiceImpl"
						+ ".getSubsectionsInfoData" + ", digiRepo size = "
						+ respDigi.size());
			}
			// Creating map by adding values
			Map<String, Gstr3BGstinAspComputeEntity> digiMap = respDigi.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalAspEntity(a, b)),
									Optional::get)));

			// Creating final dto based on section names
			for (Map.Entry<String, Gstr3BExcemptNilNonGstnDto> entry : gstr3B4
					.entrySet()) {
				Gstr3BInfoSuppliesDto dto = new Gstr3BInfoSuppliesDto();

				if (digiMap.containsKey(entry.getKey())) {
					Gstr3BGstinAspComputeEntity entity = digiMap
							.get(entry.getKey());
					dto.setDigiIgst(entity.getIgst());
					dto.setDigiCgst(entity.getCgst());
					dto.setDigiCess(entity.getCess());
					dto.setDigiSgst(entity.getSgst());
				}

				dto.setSectionName(entry.getValue().getSectionName());
				dto.setSubSectionName(entry.getValue().getSubSectionName());
				respList.add(dto);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("Gstr3BGstinNewDashboardServiceImpl"
						+ ".getInfoSubsectionsData returning response"
						+ ", response size = " + respList.size() + "");
			}
			return respList;
		} catch (Exception ex) {
			LOGGER.error(
					"Error occured in GSTR3B Sub section info service Impl",
					ex);
			throw new AppException(ex);

		}
	}

	private Gstr3BGstinAspUserInputEntity addInternalUserEntity(
			Gstr3BGstinAspUserInputEntity a, Gstr3BGstinAspUserInputEntity b) {
		Gstr3BGstinAspUserInputEntity dto = new Gstr3BGstinAspUserInputEntity();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		return dto;
	}

	private Gstr3BGstinAspComputeEntity addInternalAspEntity(
			Gstr3BGstinAspComputeEntity a, Gstr3BGstinAspComputeEntity b) {
		Gstr3BGstinAspComputeEntity dto = new Gstr3BGstinAspComputeEntity();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		return dto;
	}

	private Gstr3BGetLiabilityAutoCalcEntity addInternalGstnEntity(
			Gstr3BGetLiabilityAutoCalcEntity a,
			Gstr3BGetLiabilityAutoCalcEntity b) {
		Gstr3BGetLiabilityAutoCalcEntity dto = new Gstr3BGetLiabilityAutoCalcEntity();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		return dto;
	}

	private Gstr3BGstinAspComputeEntity sumOf2Sections(
			Gstr3BGstinAspComputeEntity a, Gstr3BGstinAspComputeEntity b,
			Gstr3BGstinAspComputeEntity entity) {
		entity.setIgst(a.getIgst().add(b.getIgst()));
		entity.setCgst(a.getCgst().add(b.getCgst()));
		entity.setSgst(a.getSgst().add(b.getSgst()));
		entity.setCess(a.getCess().add(b.getCess()));
		return entity;
	}

	private Gstr3BGetLiabilityAutoCalcEntity sumOf2SectionsGstn(
			Gstr3BGetLiabilityAutoCalcEntity a,
			Gstr3BGetLiabilityAutoCalcEntity b,
			Gstr3BGetLiabilityAutoCalcEntity entity) {
		entity.setIgst(a.getIgst().add(b.getIgst()));
		entity.setCgst(a.getCgst().add(b.getCgst()));
		entity.setSgst(a.getSgst().add(b.getSgst()));
		entity.setCess(a.getCess().add(b.getCess()));
		return entity;
	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

}