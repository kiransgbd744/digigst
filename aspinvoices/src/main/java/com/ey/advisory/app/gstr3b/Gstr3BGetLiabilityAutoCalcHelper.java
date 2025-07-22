/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGetLiabilityAutoCalcRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Component("Gstr3BGetLiabilityAutoCalcHelper")
@Slf4j
public class Gstr3BGetLiabilityAutoCalcHelper {

	@Autowired
	@Qualifier("Gstr3BGetLiabilityAutoCalcRepository")
	private Gstr3BGetLiabilityAutoCalcRepository gstr3bAutoCalcRepo;

	public void saveOrUpdateAutoCalc(String gstin, String taxPeriod,
			List<Gstr3BGstinsDto> resp, String flag) {

		try {
			if (flag.equalsIgnoreCase("Interest")) {
				gstr3bAutoCalcRepo.softDeleteActiveInterest(taxPeriod, gstin);
			} else {
				gstr3bAutoCalcRepo.softDeleteActiveRecords(taxPeriod, gstin);
			}
			List<Gstr3BGetLiabilityAutoCalcEntity> resEntities = resp.stream()
					.map(o -> convertToEntity(gstin, taxPeriod, o))
					.collect(Collectors.toCollection(ArrayList::new));

			List<String> sectionNameList = resp.stream()
					.map(o -> o.getSectionName())
					.collect(Collectors.toCollection(ArrayList::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr3BGetLiabilityAutoCalcHelper.saveOrUpdateAutoCalc "
								+ "fetching sectionNameList : {} ",
						sectionNameList);
			}

			gstr3bAutoCalcRepo.saveAll(resEntities);

		} catch (Exception e) {
			String msg = "Exception occured wihile persisiting GSTR3B AutoCalc";
			LOGGER.error(msg, e);
			new AppException(msg, e);
		}

	}

	private Gstr3BGetLiabilityAutoCalcEntity convertToEntity(String gstin,
			String taxPeriod, Gstr3BGstinsDto res) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					" Inside Gstr3BGetLiabilityAutoCalcDetailServiceImpl.convertToEntity "
							+ "method :");
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		Gstr3BGetLiabilityAutoCalcEntity resEntity = new Gstr3BGetLiabilityAutoCalcEntity();

		Integer derivedTaxPeriod = Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

		resEntity.setGstin(gstin);
		resEntity.setTaxPeriod(taxPeriod);
		resEntity.setSectionName(res.getSectionName());
		resEntity.setSubSectionName(res.getSubSectionName());
		resEntity.setIsActive(true);
		resEntity.setDerivedRetPeriod(derivedTaxPeriod);
		resEntity.setCess(res.getCess());
		resEntity.setCgst(res.getCgst());
		resEntity.setIgst(res.getIgst());
		resEntity.setSgst(res.getSgst());
		resEntity.setTaxableVal(res.getTaxableVal());
		resEntity.setCreateDate(LocalDateTime.now());
		resEntity.setCreatedBy(userName);
		resEntity.setInterState(res.getInterState());
		resEntity.setIntraState(res.getIntraState());
		resEntity.setPos(res.getPos());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Gstr3BGetLiabilityAutoCalcHelper.convertToEntity, "
					+ "before returning Gstr3BGetLiabilityAutoCalcEntity {} "
					+ ":", resEntity);
		}

		return resEntity;

	}

}
