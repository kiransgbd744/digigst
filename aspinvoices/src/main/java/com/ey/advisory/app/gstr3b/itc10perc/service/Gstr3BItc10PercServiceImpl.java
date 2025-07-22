package com.ey.advisory.app.gstr3b.itc10perc.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGetLiabilityAutoCalcRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspComputeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.gstr3b.Gstr3BExcemptNilNonGstnDto;
import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcEntity;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspComputeEntity;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.app.gstr3b.Gstr3BNewSuppliesDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstr3BConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Component("Gstr3BItc10PercServiceImpl")
@Slf4j
public class Gstr3BItc10PercServiceImpl implements Gstr3BItc10PercService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("Gstr3BGstinAspComputeRepository")
	private Gstr3BGstinAspComputeRepository computeRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private EntityInfoDetailsRepository entityInfo;

	@Autowired
	@Qualifier("Gstr3BGetLiabilityAutoCalcRepository")
	private Gstr3BGetLiabilityAutoCalcRepository autoCalRepo;

	private static List<String> l2Sections = ImmutableList.of("4(a)(5)(5.1.a)",
			"4(a)(5)(5.1.b)", "4(a)(5)(5.1.c)", "4(a)(5)(5.1.d)",
			"4(a)(5)(5.2.a)", "4(a)(5)(5.2.b)", "4(a)(5)(5.2.c)");

	@Override
	public List<Gstr3BItc10PercDto> getItcData(String gstin, String taxPeriod, Long entityId) {

		List<Gstr3BItc10PercDto> respList = new ArrayList<>();

		List<String> sectionNameList = Arrays.asList("4(a)(5)(5.1)",
				"4(a)(5)(5.1.a)", "4(a)(5)(5.1.b)", "4(a)(5)(5.1.c)",
				"4(a)(5)(5.1.d)", "4(a)(5)(5.2)", "4(a)(5)(5.2.a)",
				"4(a)(5)(5.2.b)", "4(a)(5)(5.2.c)");
		try {

			int count = 0;
			String optedOption = "A";/*
			List<EntityInfoEntity> entityIds = entityInfo
					.findByPanAndIsDeleteFalse(gstin.substring(2, 12));*/
			
			optedOption = onbrdOptionOpted(entityId);
			List<Gstr3BGstinAspUserInputEntity> userData = userRepo
					.getITC10PercSectionData(taxPeriod, gstin, sectionNameList);

			List<Gstr3BGstinAspComputeEntity> aspResp = computeRepo
					.findBySectionNameList(taxPeriod, gstin, sectionNameList);

			List<Gstr3BGetLiabilityAutoCalcEntity> autoCalData = autoCalRepo
					.findBySectionNameList(taxPeriod, gstin, sectionNameList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("getItcData"
						+ ".getSubsectionsData userRepo list Size"
						+ userData.size() + ", aspResp size = " + aspResp.size()
						+ "autoCalData size" + autoCalData.size());
			}

			Map<String, Gstr3BGstinAspUserInputEntity> userMap = userData
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalUserEntity(a, b)),
									Optional::get)));

			Map<String, Gstr3BGstinAspComputeEntity> digiMap = aspResp.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalAspEntity(a, b)),
									Optional::get)));

			Map<String, Gstr3BGetLiabilityAutoCalcEntity> gstnMap = autoCalData
					.stream()
					.collect(Collectors.groupingBy(o -> o.getSectionName(),
							Collectors.collectingAndThen(Collectors.reducing(
									(a, b) -> addInternalGstnEntity(a, b)),
									Optional::get)));
			Map<String, Gstr3BItc10PercDto> itc10Dto = str3BItc10PercMap();
			for (Map.Entry<String, Gstr3BItc10PercDto> entry : itc10Dto
					.entrySet()) {
				Gstr3BItc10PercDto dto = new Gstr3BItc10PercDto(
						entry.getValue().getSectionName(),
						entry.getValue().getSubSectionName());
				if (userMap.containsKey(entry.getKey())
						|| entry.getKey().equals("4(a)(5)(5.2)")
						|| entry.getKey().equals("4(a)(5)(5.1)")) {
					Gstr3BGstinAspUserInputEntity entity = new Gstr3BGstinAspUserInputEntity();
					if (entry.getKey().equals("4(a)(5)(5.2)")) {
						entity = sumOf3SectionsUser(
								userMap.containsKey("4(a)(5)(5.2.a)")
										? userMap.get("4(a)(5)(5.2.a)")
										: new Gstr3BGstinAspUserInputEntity(),
								userMap.containsKey("4(a)(5)(5.2.b)")
										? userMap.get("4(a)(5)(5.2.b)")
										: new Gstr3BGstinAspUserInputEntity(),
								userMap.containsKey("4(a)(5)(5.2.c)")
										? userMap.get("4(a)(5)(5.2.c)")
										: new Gstr3BGstinAspUserInputEntity(),
								entity);

					} else if (entry.getKey().equals("4(a)(5)(5.1)")) {
						if (userMap.containsKey("4(a)(5)(5.2.a)")
								&& userMap.get("4(a)(5)(5.2.a)")
										.getRadioFlag() != null
								&& userMap.get("4(a)(5)(5.2.a)")
										.getRadioFlag()) {
							entity = userMap.get("4(a)(5)(5.2.a)");
						} else if (userMap.containsKey("4(a)(5)(5.2.b)")
								&& userMap.get("4(a)(5)(5.2.b)")
										.getRadioFlag() != null
								&& userMap.get("4(a)(5)(5.2.b)")
										.getRadioFlag()) {
							entity = userMap.get("4(a)(5)(5.2.b)");
						} else if (userMap.containsKey("4(a)(5)(5.2.c)")
								&& userMap.get("4(a)(5)(5.2.c)")
										.getRadioFlag() != null
								&& userMap.get("4(a)(5)(5.2.c)")
										.getRadioFlag()) {
							entity = userMap.get("4(a)(5)(5.2.c)");
						} else {
							if ("A".equalsIgnoreCase(optedOption)) {
								Gstr3BGstinAspUserInputEntity entity1 = new Gstr3BGstinAspUserInputEntity();
								entity1 = userMap.containsKey("4(a)(5)(5.1.a)")
										? userMap.get("4(a)(5)(5.1.a)")
										: new Gstr3BGstinAspUserInputEntity();
								entity = entity1;
								entity.setSectionName("4(a)(5)(5.1)");
							} else if ("B".equalsIgnoreCase(optedOption) || "C".equalsIgnoreCase(optedOption)) {
								Gstr3BGstinAspUserInputEntity entity1 = new Gstr3BGstinAspUserInputEntity();
								entity1 = userMap.containsKey("4(a)(5)(5.1.b)")
										? userMap.get("4(a)(5)(5.1.b)")
										: new Gstr3BGstinAspUserInputEntity();
								entity = entity1;
								entity.setSectionName("4(a)(5)(5.1)");
							}
						}

					} else {
						entity = userMap.get(entry.getKey());
					}
					dto.setIgstUser(entity.getIgst());
					dto.setCgstUser(entity.getCgst());
					dto.setCessUser(entity.getCess());
					dto.setSgstUser(entity.getSgst());
					if (dto.getRadioFlag() != null && dto.getRadioFlag()) {
						count += 1;
					}
					dto.setRadioFlag(entity.getRadioFlag());

				}
				if (entry.getKey().equals("4(a)(5)(5.1)")
						|| entry.getKey().equals("4(a)(5)(5.2)")
						|| digiMap.containsKey(entry.getKey())) {

					Gstr3BGstinAspComputeEntity entity = new Gstr3BGstinAspComputeEntity();
					if (entry.getKey().equals("4(a)(5)(5.1)")) {
						if ("A".equalsIgnoreCase(optedOption)) {
							Gstr3BGstinAspComputeEntity entity1 = new Gstr3BGstinAspComputeEntity();
							entity1 = digiMap.containsKey("4(a)(5)(5.1.a)")
									? digiMap.get("4(a)(5)(5.1.a)")
									: new Gstr3BGstinAspComputeEntity();
							entity = entity1;
							entity.setSectionName("4(a)(5)(5.1)");
						} else if ("B".equalsIgnoreCase(optedOption) || "C".equalsIgnoreCase(optedOption)) {
							Gstr3BGstinAspComputeEntity entity1 = new Gstr3BGstinAspComputeEntity();
							entity1 = digiMap.containsKey("4(a)(5)(5.1.b)")
									? digiMap.get("4(a)(5)(5.1.b)")
									: new Gstr3BGstinAspComputeEntity();
							entity = entity1;
							entity.setSectionName("4(a)(5)(5.1)");
						}
					} else if (entry.getKey().equals("4(a)(5)(5.2)")) {

						entity = sumOf3Sections(
								digiMap.containsKey("4(a)(5)(5.2.a)")
										? digiMap.get("4(a)(5)(5.2.a)")
										: new Gstr3BGstinAspComputeEntity(),
								digiMap.containsKey("4(a)(5)(5.2.b)")
										? digiMap.get("4(a)(5)(5.2.b)")
										: new Gstr3BGstinAspComputeEntity(),
								digiMap.containsKey("4(a)(5)(5.2.c)")
										? digiMap.get("4(a)(5)(5.2.c)")
										: new Gstr3BGstinAspComputeEntity(),
								entity);

					} else {
						entity = digiMap.get(entry.getKey());
					}
					dto.setIgstAsp(entity.getIgst());
					dto.setCgstAsp(entity.getCgst());
					dto.setCessAsp(entity.getCess());
					dto.setSgstAsp(entity.getSgst());
				}

				if (entry.getKey().equals("4(a)(5)(5.1)")
						|| entry.getKey().equals("4(a)(5)(5.2)")
						|| gstnMap.containsKey(entry.getKey())) {
					Gstr3BGetLiabilityAutoCalcEntity entity = new Gstr3BGetLiabilityAutoCalcEntity();

					if (entry.getKey().equals("4(a)(5)(5.1)")) {
						if ("A".equalsIgnoreCase(optedOption)) {
							Gstr3BGetLiabilityAutoCalcEntity entity1 = new Gstr3BGetLiabilityAutoCalcEntity();
							entity1 = gstnMap.containsKey("4(a)(5)(5.1.a)")
									? gstnMap.get("4(a)(5)(5.1.a)")
									: new Gstr3BGetLiabilityAutoCalcEntity();
							entity = entity1;
							entity.setSectionName("4(a)(5)(5.1)");
						} else if ("B".equalsIgnoreCase(optedOption) || "C".equalsIgnoreCase(optedOption)) {
							Gstr3BGetLiabilityAutoCalcEntity entity1 = new Gstr3BGetLiabilityAutoCalcEntity();
							entity1 = gstnMap.containsKey("4(a)(5)(5.1.b)")
									? gstnMap.get("4(a)(5)(5.1.b)")
									: new Gstr3BGetLiabilityAutoCalcEntity();
							entity = entity1;
							entity.setSectionName("4(a)(5)(5.1)");
						}
						/*
						 * entity = sumOf4SectionsGstn(
						 * gstnMap.containsKey("4(a)(5)(5.1.a)") ?
						 * gstnMap.get("4(a)(5)(5.1.a)") : new
						 * Gstr3BGetLiabilityAutoCalcEntity(),
						 * gstnMap.containsKey("4(a)(5)(5.1.b)") ?
						 * gstnMap.get("4(a)(5)(5.1.b)") : new
						 * Gstr3BGetLiabilityAutoCalcEntity(),
						 * gstnMap.containsKey("4(a)(5)(5.1.c)") ?
						 * gstnMap.get("4(a)(5)(5.1.c)") : new
						 * Gstr3BGetLiabilityAutoCalcEntity(),
						 * gstnMap.containsKey("4(a)(5)(5.1.d)") ?
						 * gstnMap.get("4(a)(5)(5.1.d)") : new
						 * Gstr3BGetLiabilityAutoCalcEntity(), entity);
						 */

					} else if (entry.getKey().equals("4(a)(5)(5.2)")) {
						entity = sumOf3SectionsGstn(
								gstnMap.containsKey("4(a)(5)(5.2.a)")
										? gstnMap.get("4(a)(5)(5.2.a)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),

								gstnMap.containsKey("4(a)(5)(5.2.b)")
										? gstnMap.get("4(a)(5)(5.2.b)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),

								gstnMap.containsKey("4(a)(5)(5.2.c)")
										? gstnMap.get("4(a)(5)(5.2.c)")
										: new Gstr3BGetLiabilityAutoCalcEntity(),
								entity);

					} else {

						entity = gstnMap.get(entry.getKey());
					}
					dto.setIgstAutoCal(entity.getIgst());
					dto.setCgstAutoCal(entity.getCgst());
					dto.setCessAutoCal(entity.getCess());
					dto.setSgstAutoCal(entity.getSgst());
				}

				if (l2Sections.contains(entry.getKey()))
					dto.setLevel("L2");

				respList.add(dto);
			}
			
			List<Gstr3BItc10PercDto> radioTrueEntity = respList.stream()
			        .filter(Gstr3BItc10PercDto::getRadioFlag)
			        .collect(Collectors.toList());
			
			if (radioTrueEntity.isEmpty()) {
				for (Gstr3BItc10PercDto dto : respList) {

					if ("A".equalsIgnoreCase(optedOption)) {
						if ("4(a)(5)(5.1.a)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					} else if ("B".equalsIgnoreCase(optedOption) || "C".equalsIgnoreCase(optedOption)) {
						if ("4(a)(5)(5.1.b)"
								.equalsIgnoreCase(dto.getSectionName()))
							dto.setRadioFlag(true);
					}
				}
			}

		} catch (Exception ex) {
			String msg = String
					.format("error occured in Gstr3BItc10PercServiceImpl", ex);
			LOGGER.error(msg);
			throw new AppException(ex);
		}
		
		List<Gstr3BItc10PercDto> radioTrueEntity = respList.stream()
        .filter(Gstr3BItc10PercDto::getRadioFlag)
        .collect(Collectors.toList());
		
		if (!radioTrueEntity.isEmpty()) {
			Gstr3BItc10PercDto radioTrueDto = radioTrueEntity.get(0);
			for (Gstr3BItc10PercDto dto : respList) {
				if (dto.getSectionName().equalsIgnoreCase("4(a)(5)(5.1)")) {
					dto.setTaxableValAsp(radioTrueDto.getTaxableValAsp());
					dto.setIgstAsp(radioTrueDto.getIgstAsp());
					dto.setCgstAsp(radioTrueDto.getCgstAsp());
					dto.setSgstAsp(radioTrueDto.getSgstAsp());
					dto.setCessAsp(radioTrueDto.getCessAsp());
				}
			}
		}
		
		return respList;
	}

	private Map<String, Gstr3BItc10PercDto> str3BItc10PercMap() {
		Map<String, Gstr3BItc10PercDto> map = new LinkedHashMap<>();

		map.put("4(a)(5)(5.1)", new Gstr3BItc10PercDto("4(a)(5)(5.1)", "CTP"));
		map.put("4(a)(5)(5.1.a)",
				new Gstr3BItc10PercDto("4(a)(5)(5.1.a)", "APPPR"));
		map.put("4(a)(5)(5.1.b)",
				new Gstr3BItc10PercDto("4(a)(5)(5.1.b)", "APGG2B"));
		map.put("4(a)(5)(5.1.c)",
				new Gstr3BItc10PercDto("4(a)(5)(5.1.c)", "APG2B3BR"));
		map.put("4(a)(5)(5.1.d)",
				new Gstr3BItc10PercDto("4(a)(5)(5.1.d)", "APG2A3BR"));
		map.put("4(a)(5)(5.2)",
				new Gstr3BItc10PercDto("4(a)(5)(5.2)", "ITCTP"));
		map.put("4(a)(5)(5.2.a)",
				new Gstr3BItc10PercDto("4(a)(5)(5.2.a)", "ITCRF"));
		map.put("4(a)(5)(5.2.b)",
				new Gstr3BItc10PercDto("4(a)(5)(5.2.b)", "180DRU"));
		map.put("4(a)(5)(5.2.c)",
				new Gstr3BItc10PercDto("4(a)(5)(5.2.c)", "APRRU"));

		return map;
	}

	private static Gstr3BGstinAspUserInputEntity addInternalUserEntity(
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

	private Gstr3BGstinAspUserInputEntity sumOf3SectionsUser(
			Gstr3BGstinAspUserInputEntity a, Gstr3BGstinAspUserInputEntity b,
			Gstr3BGstinAspUserInputEntity c,
			Gstr3BGstinAspUserInputEntity entity) {
		entity.setIgst(a.getIgst().add(b.getIgst().add(c.getIgst())));
		entity.setCgst(a.getCgst().add(b.getCgst().add(c.getCgst())));
		entity.setSgst(a.getSgst().add(b.getSgst().add(c.getSgst())));
		entity.setCess(a.getCess().add(b.getCess().add(c.getCess())));
		return entity;
	}

	private Gstr3BGstinAspComputeEntity sumOf3Sections(
			Gstr3BGstinAspComputeEntity a, Gstr3BGstinAspComputeEntity b,
			Gstr3BGstinAspComputeEntity c, Gstr3BGstinAspComputeEntity entity) {
		entity.setIgst(a.getIgst().add(b.getIgst().add(c.getIgst())));
		entity.setCgst(a.getCgst().add(b.getCgst().add(c.getCgst())));
		entity.setSgst(a.getSgst().add(b.getSgst().add(c.getSgst())));
		entity.setCess(a.getCess().add(b.getCess().add(c.getCess())));
		return entity;
	}

	private Gstr3BGetLiabilityAutoCalcEntity sumOf4SectionsGstn(
			Gstr3BGetLiabilityAutoCalcEntity a,
			Gstr3BGetLiabilityAutoCalcEntity b,
			Gstr3BGetLiabilityAutoCalcEntity c,
			Gstr3BGetLiabilityAutoCalcEntity d,
			Gstr3BGetLiabilityAutoCalcEntity entity) {
		entity.setIgst(
				a.getIgst().add(b.getIgst().add(c.getIgst().add(d.getIgst()))));
		entity.setCgst(
				a.getCgst().add(b.getCgst().add(c.getCgst().add(d.getCgst()))));
		entity.setSgst(
				a.getSgst().add(b.getSgst().add(c.getSgst().add(d.getSgst()))));
		entity.setCess(
				a.getCess().add(b.getCess().add(c.getCess().add(d.getCess()))));
		return entity;
	}

	private Gstr3BGetLiabilityAutoCalcEntity sumOf3SectionsGstn(
			Gstr3BGetLiabilityAutoCalcEntity a,
			Gstr3BGetLiabilityAutoCalcEntity b,
			Gstr3BGetLiabilityAutoCalcEntity c,
			Gstr3BGetLiabilityAutoCalcEntity entity) {
		entity.setIgst(a.getIgst().add(b.getIgst().add(c.getIgst())));
		entity.setCgst(a.getCgst().add(b.getCgst().add(c.getCgst())));
		entity.setSgst(a.getSgst().add(b.getSgst().add(c.getSgst())));
		entity.setCess(a.getCess().add(b.getCess().add(c.getCess())));
		return entity;
	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

}
