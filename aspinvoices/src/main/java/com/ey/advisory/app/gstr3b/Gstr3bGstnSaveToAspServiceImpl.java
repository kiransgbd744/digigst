package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr3BTaxPaymentEntity;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bTaxPaymentRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * This Service is Responsible to save GSTN Response into DB.
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3bGstnSaveToAspServiceImpl")
public class Gstr3bGstnSaveToAspServiceImpl
		implements Gstr3bGstnSaveToAspService {

	@Autowired
	@Qualifier("Gstr3BGstnSaveToAspRepository")
	private Gstr3BGstnSaveToAspRepository gstr3bRepo;
	
	@Autowired
	@Qualifier("Gstr3bTaxPaymentRepository")
	private Gstr3bTaxPaymentRepository gstr3bTaxRepo;
	
	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Override
	@Transactional(value = "clientTransactionManager")
	public void saveGstnResponse(String gstin, String taxPeriod,
			List<Gstr3BGstinsDto> gstnRes, List<Gstr3bTaxPaymentDto> taxRes,
			String apiResp) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3bGstnSaveToAspServiceImpl.saveGstnResponse begin,"
					+ " for gstin : " + gstin + " and taxPeriod : " + " "
					+ taxPeriod);
		}
		try{
			
		List<Gstr3bGstnSaveToAspEntity> resEntities = gstnRes.parallelStream()
				.map(o -> convertToEntity(gstin, taxPeriod, o))
				.collect(Collectors.toCollection(ArrayList::new));

		List<String> sectionNameList = gstnRes.stream()
				.map(o -> o.getSectionName())
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"Gstr3bGstnSaveToAspServiceImpl.saveGstnResponse fetching "
							+ "sectionNameList :" + " : " + sectionNameList);
		}
		gstr3bRepo.updateAllActiveFlag(taxPeriod, gstin);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"Gstr3bGstnSaveToAspServiceImpl.saveGstnResponse updated"
							+ " Active flag ");
		}
		gstr3bRepo.saveAll(resEntities);
		
		if(taxRes != null){
			saveGstnTaxPaymentResponse(gstin, taxPeriod, taxRes);
		}
		
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3bGstnSaveToAspServiceImpl.saveGstnResponse Saved "
					+ "GSTN response Successfully ");
		}
		}catch(Exception e){
			String msg = "error occured wihile persisiting GSTR3B data..";
			LOGGER.error(msg, e);
			new AppException(msg, e);
		}
	}

	private Gstr3bGstnSaveToAspEntity convertToEntity(String gstin,
			String taxPeriod, Gstr3BGstinsDto res) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					" Inside Gstr3bGstnSaveToAspServiceImpl.convertToEntity "
							+ "method :");
		}

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		if (res.getSectionName().equalsIgnoreCase(Gstr3BConstants.Table5A)
				|| res.getSectionName()
						.equalsIgnoreCase(Gstr3BConstants.Table5B))
			res.setTaxableVal(res.getInterState().add(res.getIntraState()));
		Gstr3bGstnSaveToAspEntity resEntity = new Gstr3bGstnSaveToAspEntity();
		
		Integer derivedTaxPeriod = Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

		resEntity.setGstin(gstin);
		resEntity.setTaxPeriod(taxPeriod);
		resEntity.setSectionName(res.getSectionName());
		resEntity.setSubSectionName(res.getSubSectionName());
		resEntity.setIsActive(true);
		resEntity.setDerivedTaxPeriod(derivedTaxPeriod);
		
		if (res.getCess() != null)
			resEntity.setCess(res.getCess());
		else {
			resEntity.setCess(BigDecimal.ZERO);
		}
		if (res.getCgst() != null) {
			resEntity.setCgst(res.getCgst());
		} else {
			resEntity.setCgst(BigDecimal.ZERO);
		}
		if (res.getIgst() != null) {
			resEntity.setIgst(res.getIgst());
		} else {
			resEntity.setIgst(BigDecimal.ZERO);
		}
		if (res.getSgst() != null) {
			resEntity.setSgst(res.getSgst());
		} else {
			resEntity.setSgst(BigDecimal.ZERO);
		}
		if (res.getTaxableVal() != null) {
			resEntity.setTaxableVal(res.getTaxableVal());
		} else {
			resEntity.setTaxableVal(BigDecimal.ZERO);
		}
		
		resEntity.setCreateDate(EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now()));
		resEntity.setCreatedBy(userName);
		
		if (res.getInterState() != null) {
			resEntity.setInterState(res.getInterState());
		} else {
			resEntity.setInterState(BigDecimal.ZERO);
		}
		if (res.getIntraState() != null) {
			resEntity.setIntraState(res.getIntraState());
		} else {
			resEntity.setIntraState(BigDecimal.ZERO);
		}
		if (res.getPos() != null)
			resEntity.setPos(res.getPos());
		else {
			resEntity.setPos("0");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(" Gstr3bGstnSaveToAspServiceImpl.convertToEntity, "
					+ "before returning Gstr3bGstnSaveEntity " + "resEntity :"
					+ resEntity);
		}

		return resEntity;

	}

	public void saveGstnTaxPaymentResponse(String gstin, String taxPeriod,
			List<Gstr3bTaxPaymentDto> taxPaymentResult) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr3bGstnSaveToAspServiceImpl.saveGstnTaxPayment Response "
					+ "begin, for gstin : " + gstin + " and taxPeriod : " + " "
					+ taxPeriod);
		}
		List<Gstr3BTaxPaymentEntity> resultEntities = taxPaymentResult.parallelStream()
				.map(o -> convertToTaxPaymentEntity(gstin, taxPeriod, o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		gstr3bTaxRepo.updateAllActiveFlag(taxPeriod, gstin);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"Gstr3bGstnSaveToAspServiceImpl.saveGstnTaxPayment Response "
					+ "updated Active flag ");
		}
		gstr3bTaxRepo.saveAll(resultEntities);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info(
					"Gstr3bGstnSaveToAspServiceImpl.saveGstnTaxPayment "
					+ "successfully saved in DB");
		}
		
	}

	private static Gstr3BTaxPaymentEntity convertToTaxPaymentEntity(String gstin, String taxPeriod,
			Gstr3bTaxPaymentDto o) {
		
		Gstr3BTaxPaymentEntity entity = new Gstr3BTaxPaymentEntity();
		
		entity.setGstin(gstin);
		entity.setTaxPeriod(taxPeriod);
		entity.setLiabilityLedgerId(o.getLiabilityLedgerId());
		entity.setSubSection(o.getSubSection());
		entity.setTransactionType(o.getTransactionType());
		entity.setTaxType(o.getTaxType());
		entity.setTaxAmt(o.getTaxAmt());
		entity.setInterestAmt(o.getInterestAmt());
		entity.setFeeAmt(o.getFeeAmt());
		entity.setPaidUsingIgst(o.getPaidUsingIgst());
		entity.setPaidUsingCgst(o.getPaidUsingCgst());
		entity.setPaidUsingSgst(o.getPaidUsingSgst());
		entity.setPaidUsingCess(o.getPaidUsingCess());
		//entity.setFy();
		entity.setCreatedDt(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
		entity.setIsActive(true);

		return entity;
	}
}
