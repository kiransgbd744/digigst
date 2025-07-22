/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BAllOtherItcSumService")
public class Gstr3BAllOtherItcSumService {

	@Autowired
	Gstr3BGstinAspUserInputRepository repo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	private EntityInfoDetailsRepository entityInfo;

	public static List<String> sectionNameList = Arrays.asList("4(a)(5)(5.1.a)",
			"4(a)(5)(5.1.b)", "4(a)(5)(5.1.c)", "4(a)(5)(5.1.d)",
			"4(a)(5)(5.2.a)", "4(a)(5)(5.2.b)", "4(a)(5)(5.2.c)");

	List<String> OtherReveralSectionNameList = Arrays.asList("4(b)(2)(b)",
			"4(b)(2)(c)", "4(b)(2)(3)(a)", "4(b)(2)(3)(b)");

	public static List<String> InwrdISD = Arrays.asList("4(a)(4)(4.1)",
			"4(a)(4)(4.2)", "4(a)(4)(4.2.a)", "4(a)(4)(4.2.b)", "4(a)(4)(4.3)");

	public static List<String> ITCAVai = Arrays.asList("4(a)", "4(b)");

	public static List<String> a4List = Arrays.asList("4(a)(1)", "4(a)(2)",
			"4(a)(3)", "4(a)(4)", "4(a)(5)");

	public static List<String> b4List = Arrays.asList("4(b)(1)", "4(b)(2)");

	List<String> OtherReveralRule38SectionNameList = Arrays.asList("4(b)(1)(1)",
			"4(b)(1)(2)(a)", "4(b)(1)(2)(b)");

	public void sumOfITcSection(
			List<Gstr3BGstinAspUserInputDto> userInputDtoList, String gstin,
			String taxPeriod, Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside Gstr3BAllOtherItcSumService {} ",
					userInputDtoList);
		}

		List<Gstr3BGstinAspUserInputDto> finalList = new ArrayList<>();
		List<Gstr3BGstinAspUserInputDto> itcAvailable = new ArrayList<>();

		List<Gstr3BGstinAspUserInputDto> sectionWiseList = new ArrayList<>();

		List<Gstr3BGstinAspUserInputDto> a4finalList = new ArrayList<>();

		List<Gstr3BGstinAspUserInputDto> b4finalList = new ArrayList<>();

		List<Gstr3BGstinAspUserInputDto> otherReversaRule38lList = new ArrayList<>();

		userInputDtoList.forEach(o -> {
			if (sectionNameList.contains(o.getSectionName().toString())) {
				sectionWiseList.add(o);
			}
		});

		for (Gstr3BGstinAspUserInputDto o : sectionWiseList) {
			if (o.getRadioFlag() != null && o.getRadioFlag()) {
				finalList.add(o);
			}
		}

		String optedOption = "A";
		/*
		 * List<EntityInfoEntity> entityIds = entityInfo
		 * .findByPanAndIsDeleteFalse(gstin.substring(2, 12));
		 */optedOption = onbrdOptionOpted(entityId);

		List<Gstr3BGstinAspUserInputDto> defaultList = new ArrayList<>();
		if (finalList.isEmpty()) {

			if ("A".equalsIgnoreCase(optedOption)) {
				defaultList = sectionWiseList.stream()
						.filter(o -> o.getSectionName()
								.equalsIgnoreCase("4(a)(5)(5.1.a)"))
						.collect(Collectors.toList());
			} else if ("B".equalsIgnoreCase(optedOption) ||"c".equalsIgnoreCase(optedOption) ) {
				defaultList = sectionWiseList.stream()
						.filter(o -> o.getSectionName()
								.equalsIgnoreCase("4(a)(5)(5.1.b)"))
						.collect(Collectors.toList());
			}
		}
		if (!defaultList.isEmpty()) {
			finalList.addAll(defaultList);
		}

		List<Gstr3BGstinAspUserInputDto> listDnE = sectionWiseList.stream()
				.filter(o -> o.getSectionName()
						.equalsIgnoreCase("4(a)(5)(5.2.a)")
						|| o.getSectionName().equalsIgnoreCase("4(a)(5)(5.2.b)")
						|| o.getSectionName()
								.equalsIgnoreCase("4(a)(5)(5.2.c)"))
				.collect(Collectors.toList());

		finalList.addAll(listDnE);

		Optional<Gstr3BGstinAspUserInputDto> entities = finalList.stream()
				.reduce((a, b) -> addDto(a, b));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside Gstr3BAllOtherItcSumService entities {} ",
					entities);
		}

		if (entities.isPresent()) {

			Gstr3BGstinAspUserInputEntity entitiy = setValue(entities.get(),
					gstin, taxPeriod, "4(a)(5)");

			List<String> updateSection = Arrays.asList("4(a)(5)");
			repo.updateActiveFlag(taxPeriod, gstin, updateSection);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside before saving entity {} ", entitiy);
			}
			repo.save(entitiy);

		}
		// a4 list

		userInputDtoList.forEach(o -> {
			if (a4List.contains(o.getSectionName().toString())) {
				a4finalList.add(o);
			}
		});

		Optional<Gstr3BGstinAspUserInputDto> a4entity = a4finalList.stream()
				.reduce((a, b) -> addDto(a, b));

		if (a4entity.isPresent()) {

			Gstr3BGstinAspUserInputEntity a4entitiy = setValue(a4entity.get(),
					gstin, taxPeriod, "4(a)");

			repo.updateActiveFlag(taxPeriod, gstin, Arrays.asList("4(a)"));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside before saving entity {} ", a4entitiy);
			}
			repo.save(a4entitiy);

		}

		// b4 list

		userInputDtoList.forEach(o -> {
			if (b4List.contains(o.getSectionName().toString())) {
				b4finalList.add(o);
			}
		});

		Optional<Gstr3BGstinAspUserInputDto> b4entity = b4finalList.stream()
				.reduce((a, b) -> addDto(a, b));

		if (b4entity.isPresent()) {

			Gstr3BGstinAspUserInputEntity b4entitiy = setValue(b4entity.get(),
					gstin, taxPeriod, "4(b)");

			repo.updateActiveFlag(taxPeriod, gstin, Arrays.asList("4(b)"));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside before saving entity {} ", b4entitiy);
			}
			repo.save(b4entitiy);

		}
		// Net ITC Available (A) - (B)
		userInputDtoList.forEach(o -> {
			if (ITCAVai.contains(o.getSectionName().toString())) {
				itcAvailable.add(o);
			}
		});

		Gstr3BGstinAspUserInputDto a4 = new Gstr3BGstinAspUserInputDto();
		Gstr3BGstinAspUserInputDto b4 = new Gstr3BGstinAspUserInputDto();
		Gstr3BGstinAspUserInputDto dto = new Gstr3BGstinAspUserInputDto();

		Gstr3BGstinAspUserInputEntity itcEntity = new Gstr3BGstinAspUserInputEntity();
		for (Gstr3BGstinAspUserInputDto itcAvai : itcAvailable) {
			if (itcAvai.getSectionName().equalsIgnoreCase("4(a)"))
				a4 = itcAvai;
			else
				b4 = itcAvai;

		}
		dto = subtractDto(a4, b4);
		
	// Bug 111135: 

	/*	itcEntity = setValueItcAva(dto, gstin, taxPeriod, "4(c)");
		repo.updateActiveFlag(taxPeriod, gstin, Arrays.asList("4(c)"));
		
		
		repo.save(itcEntity);*/

		// Inward supplies from ISD
		/*
		 * userInputDtoList.forEach(o -> { if
		 * (InwrdISD.contains(o.getSectionName().toString())) { isdlist.add(o);
		 * } });
		 * 
		 * for (Gstr3BGstinAspUserInputDto o : sectionWiseList) { if
		 * (o.getRadioFlag() != null && o.getRadioFlag()) { finalListIsd.add(o);
		 * } }
		 * 
		 * List<Gstr3BGstinAspUserInputDto> defaultListIsd = new ArrayList<>();
		 * if (finalListIsd.isEmpty()) {
		 * 
		 * if ("A".equalsIgnoreCase(optedOption)) { defaultListIsd =
		 * sectionWiseList.stream().filter( o ->
		 * o.getSectionName().equalsIgnoreCase("4(a)(4)(4.1)"))
		 * .collect(Collectors.toList()); }else if
		 * ("B".equalsIgnoreCase(optedOption)) { defaultListIsd =
		 * sectionWiseList.stream().filter( o ->
		 * o.getSectionName().equalsIgnoreCase("4(a)(4)(4.2)"))
		 * .collect(Collectors.toList()); } } if (!defaultListIsd.isEmpty()) {
		 * finalListIsd.addAll(defaultList); }
		 * 
		 * 
		 * Optional<Gstr3BGstinAspUserInputDto> entity = finalListIsd.stream()
		 * .reduce((a, b) -> addDto(a, b));
		 * 
		 * if (LOGGER.isDebugEnabled()) {
		 * LOGGER.debug("Inside Gstr3BAllOtherItcSumService entities {} ",
		 * entities); }
		 * 
		 * if (entity.isPresent()) {
		 * 
		 * Gstr3BGstinAspUserInputEntity entitiy = setValue(entity.get(), gstin,
		 * taxPeriod, "4(a)(4)");
		 * 
		 * List<String> updateSection = Arrays.asList("4(a)(4)");
		 * repo.updateActiveFlag(taxPeriod, gstin, updateSection);
		 * 
		 * if (LOGGER.isDebugEnabled()) {
		 * LOGGER.debug("Inside before saving entity {} ", entitiy); }
		 * repo.save(entitiy);
		 * 
		 * }
		 */
	}

	/*
	 * userInputDtoList.forEach(o -> { if (OtherReveralSectionNameList
	 * .contains(o.getSectionName().toString())) { otherReversalList.add(o); }
	 * });
	 * 
	 * Optional<Gstr3BGstinAspUserInputDto> Otherentities = otherReversalList
	 * .stream().reduce((a, b) -> addDto(a, b));
	 * 
	 * if (Otherentities.isPresent()) {
	 * 
	 * Gstr3BGstinAspUserInputEntity othierEntitiy = setValue(
	 * Otherentities.get(), gstin, taxPeriod, "4(b)(2)");
	 * 
	 * List<String> updateSection = Arrays.asList("4(b)(2)");
	 * repo.updateActiveFlag(taxPeriod, gstin, updateSection);
	 * 
	 * if (LOGGER.isDebugEnabled()) {
	 * LOGGER.debug("Inside before saving OtherEntity {} ", othierEntitiy); }
	 * repo.save(othierEntitiy);
	 * 
	 * }
	 * 
	 * userInputDtoList.forEach(o -> { if (OtherReveralRule38SectionNameList
	 * .contains(o.getSectionName().toString())) {
	 * otherReversaRule38lList.add(o); } });3
	 * 
	 * Optional<Gstr3BGstinAspUserInputDto> OtherenRule38tities =
	 * otherReversaRule38lList .stream().reduce((a, b) -> addDto(a, b));
	 * 
	 * if (OtherenRule38tities.isPresent()) {
	 * 
	 * Gstr3BGstinAspUserInputEntity OtherenRule38tity = setValue(
	 * OtherenRule38tities.get(), gstin, taxPeriod, "4(b)(1)");
	 * 
	 * List<String> updateSection = Arrays.asList("4(b)(1)");
	 * repo.updateActiveFlag(taxPeriod, gstin, updateSection);
	 * 
	 * if (LOGGER.isDebugEnabled()) {
	 * LOGGER.debug("Inside before saving OtherEntity {} ", OtherenRule38tity);
	 * } repo.save(OtherenRule38tity);
	 * 
	 * }
	 * 
	 * }
	 */
	private Gstr3BGstinAspUserInputDto addDto(Gstr3BGstinAspUserInputDto a,
			Gstr3BGstinAspUserInputDto b) {
		Gstr3BGstinAspUserInputDto dto = new Gstr3BGstinAspUserInputDto();
		dto.setCess(a.getCess().add(b.getCess()));
		dto.setCgst(a.getCgst().add(b.getCgst()));
		dto.setIgst(a.getIgst().add(b.getIgst()));
		dto.setSgst(a.getSgst().add(b.getSgst()));
		return dto;
	}

	private Gstr3BGstinAspUserInputDto subtractDto(Gstr3BGstinAspUserInputDto a,
			Gstr3BGstinAspUserInputDto b) {
		Gstr3BGstinAspUserInputDto dto = new Gstr3BGstinAspUserInputDto();
		dto.setCess(a.getCess().subtract(b.getCess()));
		dto.setCgst(a.getCgst().subtract(b.getCgst()));
		dto.setIgst(a.getIgst().subtract(b.getIgst()));
		dto.setSgst(a.getSgst().subtract(b.getSgst()));
		return dto;
	}

	private Gstr3BGstinAspUserInputEntity setValue(
			Gstr3BGstinAspUserInputDto entitiy, String gstin, String taxPeriod,
			String sectionName) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BAllOtherItcSumService"
					+ ".setDefaultValue() method :");
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		userEntity.setCess(entitiy.getCess());
		userEntity.setCgst(entitiy.getCgst());
		userEntity.setIgst(entitiy.getIgst());
		userEntity.setSgst(entitiy.getSgst());
		userEntity.setTaxableVal(entitiy.getTaxableVal());
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(LocalDateTime.now());
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(BigDecimal.ZERO);
		userEntity.setIntraState(BigDecimal.ZERO);
		userEntity.setSectionName(sectionName);
		userEntity.setSubSectionName("AlOthrItc");
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(null);
		return userEntity;

	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

	private Gstr3BGstinAspUserInputEntity setValueItcAva(
			Gstr3BGstinAspUserInputDto entitiy, String gstin, String taxPeriod,
			String sectionName) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Inside Gstr3BAllOtherItcSumService"
					+ ".setDefaultValue() method :");
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Gstr3BGstinAspUserInputEntity userEntity = new Gstr3BGstinAspUserInputEntity();

		userEntity.setCess(entitiy.getCess());
		userEntity.setCgst(entitiy.getCgst());
		userEntity.setIgst(entitiy.getIgst());
		userEntity.setSgst(entitiy.getSgst());
		userEntity.setTaxableVal(entitiy.getTaxableVal());
		userEntity.setGstin(gstin);
		userEntity.setTaxPeriod(taxPeriod);
		userEntity.setCreateDate(LocalDateTime.now());
		userEntity.setCreatedBy(userName);
		userEntity.setUpdateDate(LocalDateTime.now());
		userEntity.setUpdatedBy(userName);
		userEntity.setInterState(BigDecimal.ZERO);
		userEntity.setIntraState(BigDecimal.ZERO);
		userEntity.setSectionName(sectionName);
		userEntity.setSubSectionName("itcAvai");
		userEntity.setIsActive(true);
		userEntity.setIsITCActive(true);
		userEntity.setPos(null);
		return userEntity;

	}

}
