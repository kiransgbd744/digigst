package com.ey.advisory.app.data.daos.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaInvoicesEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTdsAndTdsaInvoicesEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2XProcessedTcsTdsRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCSAndTCSADetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTDSAndTDSADetailsAtGstnRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("TDSUpdateActionDaoImpl")
public class TDSUpdateActionDaoImpl {

	public static final String ACCEPT = "accept";
	public static final String REJECT = "reject";
	public static final String NOACTION = "noaction";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	/*
	 * @Autowired private TDSProcessedRecordsCommonUtil
	 * tdsProcessedRecordsCommonUtil;
	 */
	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("GSTR2XTdstcsGetFilupdRepository") private
	 * GSTR2XTdstcsGetFilupdRepository tdstcsFileUploadRepo;
	 */
	@Autowired
	@Qualifier("Gstr2xGetTDSAndTDSADetailsAtGstnRepository")
	private Gstr2xGetTDSAndTDSADetailsAtGstnRepository gstr2xTdsTcs;

	@Autowired
	@Qualifier("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
	Gstr2xGetTCSAndTCSADetailsAtGstnRepository gstr2xTdsaTcsa;

	@Autowired
	@Qualifier("Gstr2XProcessedTcsTdsRepository")
	private Gstr2XProcessedTcsTdsRepository fileUploadProcess;

	public void fetchTdsSummaryRecords(ITC04RequestDto req) {

		// List<Long> entityId = req.getEntityId();
		String taxPeriodReq = req.getTaxPeriod();
		String action = req.getAction();
		String recordType = req.getType();
		List<String> gstinDeductor = req.getGstinDeductor();
		String screenType = req.getScreenType();
		List<String> docKey = req.getDocKey();
		String type = req.getType();
		// List<TDSSummaryRespDto> retList = new ArrayList<>();
		Integer taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		// int executeUpdate =-1;
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		List<GetGstr2xTdsAndTdsaInvoicesEntity> findByfilproKey = new ArrayList<>();
		if (!docKey.isEmpty() && docKey.size() > 0) {
			try {
				if (type.equalsIgnoreCase("TDS")
						|| type.equalsIgnoreCase("TDSA")) {
					
					if(screenType.equalsIgnoreCase("P")){
						
						findByfilproKey = gstr2xTdsTcs.findByfilPSproKey(docKey);
					//	gstr2xTdsTcs.updatePsUserActionFlag(docKey);

					}else{
					
					findByfilproKey = gstr2xTdsTcs.findByfilproKey(docKey);
				//	gstr2xTdsTcs.updateUserActionFlag(docKey);
					}
					List<Gstr2XProcessedTcsTdsEntity> listEntity = new ArrayList<Gstr2XProcessedTcsTdsEntity>();
					findByfilproKey.stream().forEach(key -> {

						gstr2xTdsTcs.updatePsUserActionFlag(key.getDocKey());
						Gstr2XProcessedTcsTdsEntity entity = new Gstr2XProcessedTcsTdsEntity();

						if (action.equalsIgnoreCase(ACCEPT)) {
							entity.setUserAction("A");
						} else if (action.equalsIgnoreCase(REJECT)) {
							entity.setUserAction("R");
						} else if (action.equalsIgnoreCase(NOACTION)) {
							entity.setUserAction("N");
						}
						entity.setSaveAction(key.getSaveAction());
						entity.setCtin(key.getCgstin());
						entity.setDataOriginTypeCode("U");
						entity.setDeductorUplMonth(
								key.getDeductorUploadedMonth());
						entity.setOrgDeductorUplMonth(
								key.getOrgDeductorUploadedMonth());
						entity.setDelete(false);
						entity.setDerivedRetPeriod(key.getDerReturnPeriod());
						entity.setDocKey(key.getDocKey());
						entity.setPsKey(key.getPsKey());
						entity.setTaxableType(key.getTaxableType());
						entity.setRetPeriod(key.getRetPeriod());
						entity.setGstin(key.getSgstin());
						entity.setIgstAmt(key.getIgstAmt());
						entity.setCgstAmt(key.getCgstAmt());
						entity.setSgstAmt(key.getSgstAmt());
						entity.setRecordType(key.getRecordType());
						fileUploadProcess.softDeleteSameDocKey(key.getDocKey());
						/*if(!key.getDocKey().isEmpty()){
							if(screenType.equalsIgnoreCase("P")){
								fileUploadProcess.softDeleteSamePsDocKey(key.getPsKey());
							}else {
								
					
							}
						}*/
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						listEntity.add(entity);

					});
					List<Gstr2XProcessedTcsTdsEntity> saveAll = fileUploadProcess
							.saveAll(listEntity);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"TDS & TCS Data Persisted in the Table GSTR2X_PROCESSED_TCS_TDS ",
								saveAll);
					}

				} else if (type.equalsIgnoreCase("TCS")
						|| type.equalsIgnoreCase("TCSA")) {
					
					List<GetGstr2xTcsAndTcsaInvoicesEntity> findByfilproKeyTdsa = new ArrayList<>();
					
					if(screenType.equalsIgnoreCase("P")){
						findByfilproKeyTdsa =gstr2xTdsaTcsa.findByfilPSproKey(docKey);
					//	gstr2xTdsaTcsa.updatePsUserActionFlag(docKey);

					}else {
						
						findByfilproKeyTdsa = gstr2xTdsaTcsa
								.findByfilproKey(docKey);
					//	gstr2xTdsaTcsa.updateUserActionFlag(docKey);

					}
					 
					List<Gstr2XProcessedTcsTdsEntity> entityListTdsaTcsa = new ArrayList<>();
					

					findByfilproKeyTdsa.stream().forEach(keyA -> {

						gstr2xTdsaTcsa.updatePsUserActionFlag(keyA.getDocKey());
						Gstr2XProcessedTcsTdsEntity entity = new Gstr2XProcessedTcsTdsEntity();
						entity.setGstin(keyA.getSgstin());
						entity.setCtin(keyA.getCgstin());
						entity.setRetPeriod(keyA.getRetPeriod());
						entity.setDerivedRetPeriod(keyA.getDerReturnPeriod());
						entity.setRecordType(keyA.getRecordType());
						entity.setDeductorUplMonth(
								keyA.getDeductorUploadedMonth());
						entity.setOrgDeductorUplMonth(
								keyA.getOrgDeductorUploadedMonth());
						/*
						 * entity.setTaxableType(keyA.getTaxableType());
						 * entity.setRegSup(keyA.getRegSupplier());
						 * entity.setRegSupRet(keyA.getRegRetSupplier());
						 * entity.setUnRegSup(keyA.getUnRegSupplier());
						 * entity.setUnRegSupRet(keyA.getUnRegRetSupplier());
						 */
						entity.setDataOriginTypeCode("U");

						if (action.equalsIgnoreCase(ACCEPT)) {
							entity.setUserAction("A");
						} else if (action.equalsIgnoreCase(REJECT)) {
							entity.setUserAction("R");
							entity.setDigiGstRemarks(req.getRemark());
							entity.setDigiGstComment(req.getComment());
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"INSIDE TCS OR TCSA WHERE action is reject ",
										entity);
							}
						} else if (action.equalsIgnoreCase(NOACTION)) {
							entity.setUserAction("N");
						}

						// entity.setUserAction(action);
						entity.setSaveAction(keyA.getSaveAction());
						entity.setDelete(false);
						entity.setDocKey(keyA.getDocKey());
						entity.setPsKey(keyA.getPsKey());
						LocalDateTime convertNow = EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now());
						entity.setCreatedOn(convertNow);

						entity.setCgstAmt(keyA.getCgstAmt());
						entity.setIgstAmt(keyA.getIgstAmt());
						entity.setSgstAmt(keyA.getSgstAmt());
						
                         //Column added
						
						entity.setDeductorName(keyA.getDeductorName());
						entity.setDocNo(keyA.getDocNo());
						entity.setDocDate(keyA.getDocDate());
						entity.setOrgDocNo(keyA.getOrgDocNo());
						entity.setOrgDocDate(keyA.getOrgDocDate());
						entity.setSuppliesCollected(keyA.getSuppliesCollected());
						entity.setSuppliesReturned(keyA.getSuppliesReturned());
						entity.setNetSupplies(keyA.getNetSupplies());
						entity.setInvoiceValue(keyA.getInvoiceValue());
						entity.setOrgTaxableValue(keyA.getOrgTaxableValue());
						entity.setOrgInvoiceValue(keyA.getOrgInvoiceValue());
						entity.setPos(keyA.getPos());
						entity.setChkSum(keyA.getChkSum());
						entity.setGstnRemarks(keyA.getGstnRemarks());
						entity.setGstnComment(keyA.getGstnComment());
						
						
/*						if(!keyA.getDocKey().isEmpty()){
							if(screenType.equalsIgnoreCase("P")){
								fileUploadProcess.softDeleteSamePsDocKey(keyA.getPsKey());
							}else{
*/							fileUploadProcess.softDeleteSameDocKey(keyA.getDocKey());
						//	}
						//	}
						
						entityListTdsaTcsa.add(entity);

					});
					
				/*	docKey.stream().forEach(key -> {
						
						fileUploadProcess.softDeleteSameDocKey(key);
					});*/
					
					List<Gstr2XProcessedTcsTdsEntity> saveAll = fileUploadProcess
							.saveAll(entityListTdsaTcsa);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"TDSA & TCSA Data Persisted in the Table GSTR2X_PROCESSED_TCS_TDS ",
								saveAll);
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				LOGGER.error("Error Getting Saved data In Processing Table ",
						e);

			}

		}
	}
}
