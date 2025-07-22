/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Getgstr6CounterPartyEntity;
import com.ey.advisory.app.data.entities.client.Getgstr6IsdSummaryEntity;
import com.ey.advisory.app.data.entities.client.Getgstr6IsdUnitSummaryEntity;
import com.ey.advisory.app.data.entities.client.Getgstr6LateFeeOffSetEntity;
import com.ey.advisory.app.data.entities.client.Getgstr6SummaryEntity;
import com.ey.advisory.app.data.repositories.client.Anx1SummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx2B2BDESummaryAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Anx2CounterPartySummaryRepository;
import com.ey.advisory.app.data.repositories.client.Anx2IsdcSummaryRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx2ActionSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Getgstr6CounterPartyRepository;
import com.ey.advisory.app.data.repositories.client.Getgstr6IsdItcSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Getgstr6IsdSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Getgstr6IsdUnitSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Getgstr6LateFeeOffSetRepository;
import com.ey.advisory.app.data.repositories.client.Getgstr6SummaryRepository;
import com.ey.advisory.app.docs.dto.anx1.CounterPartyDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr6GetSummaryData;
import com.ey.advisory.app.docs.dto.anx1.LateFeemain;
import com.ey.advisory.app.docs.dto.anx1.SectionSummary;
import com.ey.advisory.app.docs.dto.anx1.TtcDetails;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("Gstr6SummaryDataParserImpl")
@Slf4j
public class Gstr6SummaryDataParserImpl {

	@Autowired
	@Qualifier("Anx1SummaryAtGstnRepository")
	private Anx1SummaryAtGstnRepository repository;

	@Autowired
	@Qualifier("Anx2B2BDESummaryAtGstnRepository")
	private Anx2B2BDESummaryAtGstnRepository secSummRepository;

	@Autowired
	@Qualifier("Anx2IsdcSummaryRepository")
	private Anx2IsdcSummaryRepository isdcRepository;

	@Autowired
	@Qualifier("GetAnx2ActionSummaryRepository")
	private GetAnx2ActionSummaryRepository actionSumRepository;

	@Autowired
	@Qualifier("Getgstr6SummaryRepository")
	private Getgstr6SummaryRepository b2bcdnRepository;

	@Autowired
	@Qualifier("Getgstr6IsdSummaryRepository")
	private Getgstr6IsdSummaryRepository isdRepository;

	@Autowired
	@Qualifier("Anx2CounterPartySummaryRepository")
	private Anx2CounterPartySummaryRepository counterPartySummRepository;

	@Autowired
	@Qualifier("Getgstr6CounterPartyRepository")
	private Getgstr6CounterPartyRepository getgstr6CounterPartyRepository;
	@Autowired
	@Qualifier("Getgstr6IsdUnitSummaryRepository")
	Getgstr6IsdUnitSummaryRepository getgstr6IsdUnitSummaryRepository;
	@Autowired
	@Qualifier("Getgstr6LateFeeOffSetRepository")
	private Getgstr6LateFeeOffSetRepository getgstr6LateFeeOffSetRepository;

	@Autowired
	@Qualifier("Getgstr6IsdItcSummaryRepository")
	private Getgstr6IsdItcSummaryRepository getgstr6IsdItcSummaryRepository;

	private static final List<String> SEC_TYPE = ImmutableList.of(GSTConstants.CDNAT,
			GSTConstants.CDNT, GSTConstants.B2BT, GSTConstants.B2BAT);

	
	private static final List<String> ISD_ELIGIBLE = ImmutableList.of(
			GSTConstants.EISDAT, GSTConstants.IEISDAT, GSTConstants.EISDCNT,
			GSTConstants.EISDURAT, GSTConstants.IEISDCNAT,
			GSTConstants.EISDCNAT, GSTConstants.IEISDURAT,
			GSTConstants.IEISDCNURAT, GSTConstants.EISDCNURAT,
			GSTConstants.IEISDCNURT, GSTConstants.EISDURT, GSTConstants.IEISDT,
			GSTConstants.EISDCNURT, GSTConstants.IEISDURT);

	private static final String EI = "EI";
	private static final String IE = "IE";
	public void parseAnx2SummaryData(Gstr6GetInvoicesReqDto dto, String apiResp,
			Long batchId) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr6GetSummaryData jsonSummaryData = gson.fromJson(apiResp,
					Gstr6GetSummaryData.class);

			String gstin = jsonSummaryData.getGstin();
			String returnPeriod = jsonSummaryData.getReturnPeriod();
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
			TtcDetails itcDetails = jsonSummaryData.getItcDetails();
			LateFeemain lateFeemain = jsonSummaryData.getLateFeemain();
			List<SectionSummary> sectionSummary = jsonSummaryData
					.getSectionSummary();

			isdRepository.softlyDeleteBasedOnGstinAndTaxPeriod(gstin, returnPeriod);
			b2bcdnRepository.softlyDeleteBasedOnGstinAndTaxPeriod(gstin, returnPeriod);
			getgstr6LateFeeOffSetRepository.softlyDeleteBasedOnGstinAndTaxPeriod(gstin, returnPeriod);
			//getgstr6IsdItcSummaryRepository.softlyDeleteBasedOnGstinAndTaxPeriod(gstin, returnPeriod);
			
			
			List<Getgstr6IsdUnitSummaryEntity> isdUnitSummeryList = new ArrayList<>();
			if (!sectionSummary.isEmpty()) {
				for (SectionSummary summery : sectionSummary) {
					if (ISD_ELIGIBLE.contains(
							trimAndConvToUpperCase(summery.getSec_nm()))) {

						Getgstr6IsdSummaryEntity isdentity = new Getgstr6IsdSummaryEntity();
						isdentity.setBatchId(batchId.toString());
						isdentity.setChksum(summery.getChksum());
						isdentity.setCreatedBy(GSTConstants.SYSTEM);
						isdentity.setCreatedOn(LocalDateTime.now());
						isdentity.setDeriviedReturnPeriod(derivedRetPeriod);
						isdentity.setGstIn(gstin);
						isdentity.setModifiedBy(GSTConstants.SYSTEM);
						isdentity.setModifiedOn(LocalDateTime.now());
						isdentity.setRecordCount(summery.getRc());
						isdentity.setTableSection(summery.getSec_nm());
						isdentity.setTotalValue(summery.getTtl_val()
								.add(summery.getTtl_txpd_cess())
								.add(summery.getTtl_txpd_cgst())
								.add(summery.getTtl_txpd_igst())
								.add(summery.getTtl_txpd_sgst()));
						isdentity.setTaxperiod(returnPeriod);
						isdentity.setTotalTaxaBleValue(summery.getTtl_val());
						isdentity.setTtcess(summery.getTtl_txpd_cess());
						isdentity.setTtcgst(summery.getTtl_txpd_cgst());
						isdentity.setTtigst(summery.getTtl_txpd_igst());
						isdentity.setTtsgst(summery.getTtl_txpd_sgst());
						String eligible = summery.getSec_nm().substring(0, 2);
						if (EI.equalsIgnoreCase(eligible)) {
							isdentity.setEligible(true);
						}
						if (IE.equalsIgnoreCase(summery.getSec_nm())) {
							isdentity.setInEligible(true);
						}
						isdRepository.save(isdentity);
						List<CounterPartyDto> isdCptySumlist = summery
								.getCptySum();
						if (isdCptySumlist != null) {
							for (CounterPartyDto cptySum : isdCptySumlist) {
								Getgstr6IsdUnitSummaryEntity cpEntity = new Getgstr6IsdUnitSummaryEntity();
								cpEntity.setChksum(cptySum.getChksum());
								cpEntity.setCtin(cptySum.getCtin());
								cpEntity.setDeriviedReturnPeriod(
										derivedRetPeriod);

								cpEntity.setRecordCount(cptySum.getRc());
								cpEntity.setTotalTaxaBleValue(cptySum.getTtl_val());
								cpEntity.setTtcess(cptySum.getTtl_txpd_cess());
								cpEntity.setTtcgst(cptySum.getTtl_txpd_cgst());
								cpEntity.setTtigst(cptySum.getTtl_txpd_igst());
								cpEntity.setTtsgst(cptySum.getTtl_txpd_sgst());
								cpEntity.setHeaderId(isdentity);
								isdUnitSummeryList.add(cpEntity);

							}
						}
					}
				}
			}
			getgstr6IsdUnitSummaryRepository.saveAll(isdUnitSummeryList);
			List<Getgstr6CounterPartyEntity> cpEntityList = new ArrayList<>();
			if (!sectionSummary.isEmpty()) {
				for (SectionSummary summery : sectionSummary) {
					if (SEC_TYPE.contains(
							trimAndConvToUpperCase(summery.getSec_nm()))) {

						Getgstr6SummaryEntity b2bentity = new Getgstr6SummaryEntity();
						b2bentity.setBatchId(batchId.toString());
						b2bentity.setChksum(summery.getChksum());
						b2bentity.setCreatedBy(GSTConstants.SYSTEM);
						b2bentity.setCreatedOn(LocalDateTime.now());
						b2bentity.setDeriviedReturnPeriod(derivedRetPeriod);
						b2bentity.setGstIn(gstin);
						b2bentity.setModifiedBy(GSTConstants.SYSTEM);
						b2bentity.setModifiedOn(LocalDateTime.now());
						b2bentity.setRecordCount(summery.getRc());
						b2bentity.setTableSection(summery.getSec_nm());
						b2bentity.setTotalValue(summery.getTtl_val()
								.add(summery.getTtl_txpd_cess())
								.add(summery.getTtl_txpd_cgst())
								.add(summery.getTtl_txpd_igst())
								.add(summery.getTtl_txpd_sgst()));
						b2bentity.setTaxperiod(returnPeriod);
						b2bentity.setTotalTaxaBleValue(summery.getTtl_val());
						b2bentity.setTtcess(summery.getTtl_txpd_cess());
						b2bentity.setTtcgst(summery.getTtl_txpd_cgst());
						b2bentity.setTtigst(summery.getTtl_txpd_igst());
						b2bentity.setTtsgst(summery.getTtl_txpd_sgst());
						b2bcdnRepository.save(b2bentity);

						List<CounterPartyDto> cptySumList = summery
								.getCptySum();
						if (cptySumList != null) {
							for (CounterPartyDto cptySum : cptySumList) {
								Getgstr6CounterPartyEntity cpEntity 
								       = new Getgstr6CounterPartyEntity();
								cpEntity.setChksum(cptySum.getChksum());
								cpEntity.setCtin(cptySum.getCtin());
								cpEntity.setDeriviedReturnPeriod(
										derivedRetPeriod);

								cpEntity.setRecordCount(cptySum.getRc());
								 cpEntity.setTotalTaxaBleValue(cptySum.getTtl_val());
								 cpEntity.setTotalValue(summery.getTtl_val()
											.add(summery.getTtl_txpd_cess())
											.add(summery.getTtl_txpd_cgst())
											.add(summery.getTtl_txpd_igst())
											.add(summery.getTtl_txpd_sgst()));
								cpEntity.setTtcess(cptySum.getTtl_txpd_cess());
								cpEntity.setTtcgst(cptySum.getTtl_txpd_cgst());
								cpEntity.setTtigst(cptySum.getTtl_txpd_igst());
								cpEntity.setTtsgst(cptySum.getTtl_txpd_sgst());
								cpEntity.setHeaderId(b2bentity);
								cpEntityList.add(cpEntity);

							}
						}
					}
				}
			}
			getgstr6CounterPartyRepository.saveAll(cpEntityList);
			if (lateFeemain != null) {
				Getgstr6LateFeeOffSetEntity lateFee = new Getgstr6LateFeeOffSetEntity();

				lateFee.setBatchId(batchId.toString());
				lateFee.setCreatedBy(GSTConstants.SYSTEM);
				lateFee.setCreatedOn(LocalDateTime.now());
				lateFee.setDbitNm(lateFeemain.getLateFee().getDebitId());
				lateFee.setDeriviedReturnPeriod(derivedRetPeriod);
				lateFee.setGstIn(gstin);
				lateFee.setModifiedBy(GSTConstants.SYSTEM);
				lateFee.setModifiedOn(LocalDateTime.now());
				lateFee.setTaxperiod(returnPeriod);
				lateFee.setTtcgst(lateFeemain.getLateFee().getCLamt());
				lateFee.setTtsgst(lateFeemain.getLateFee().getSLamt());
				getgstr6LateFeeOffSetRepository.save(lateFee);
			}
			

		} catch (Exception e) {
			String msg = "failed to parse Anx2 summary response";
			LOGGER.error(msg, e);

		}

	}

}