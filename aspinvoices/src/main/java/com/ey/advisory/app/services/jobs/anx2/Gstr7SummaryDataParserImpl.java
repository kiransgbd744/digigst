/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr7SectionSummaryISDEntity;
import com.ey.advisory.app.data.entities.client.Gstr7SectionSummaryTdsPostEntity;
import com.ey.advisory.app.data.repositories.client.Gstr7SectionSummaryISDRepository;
import com.ey.advisory.app.data.repositories.client.Gstr7SectionSummaryTdsPostRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr7GetSummaryDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7GetSummaryTaxPaidDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7GstnTdsSummeyDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7TaxPaySummaryDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("Gstr7SummaryDataParserImpl")
@Slf4j
public class Gstr7SummaryDataParserImpl {

	@Autowired
	@Qualifier("Gstr7SectionSummaryISDRepository")
	private Gstr7SectionSummaryISDRepository gstr7SectionSummaryISDRepository;

	@Autowired
	@Qualifier("Gstr7SectionSummaryTdsPostRepository")
	private Gstr7SectionSummaryTdsPostRepository gstr7SectionSummaryTdsPostRepository;

	public void parsegstr7SummaryData(Anx2GetInvoicesReqDto dto, String apiResp,
			Long batchId) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			
			Gstr7GetSummaryDto jsonSummaryData = gson.fromJson(apiResp,
					Gstr7GetSummaryDto.class);

			String gstin = jsonSummaryData.getGstin();
			String returnPeriod = jsonSummaryData.getFp();
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
			// String checksum = jsonSummaryData.getChecksum();
			Gstr7GstnTdsSummeyDto tdsDtails = jsonSummaryData.getTds();
			Gstr7GstnTdsSummeyDto tdsaDtails = jsonSummaryData.getTdsa();
			List<Gstr7TaxPaySummaryDto> taxpayList = jsonSummaryData
					.getTax_pay();
			Gstr7GetSummaryTaxPaidDto tax_paid = jsonSummaryData.getTax_paid();

			gstr7SectionSummaryISDRepository.softlyDeleteBasedOnGstinAndTaxPeriod(gstin, returnPeriod);
			
			gstr7SectionSummaryTdsPostRepository.softlyDeleteBasedOnGstinAndTaxPeriod(gstin, returnPeriod);
			
			
			if (tdsDtails != null) {
				Gstr7SectionSummaryISDEntity tds = new Gstr7SectionSummaryISDEntity();
				tds.setGstIn(gstin);
				tds.setBatchId(batchId.toString());
				tds.setTaxperiod(returnPeriod);
				tds.setDeriviedReturnPeriod(derivedRetPeriod);
				tds.setCreatedBy(GSTConstants.SYSTEM);
				tds.setCreatedOn(LocalDateTime.now());
				tds.setModifiedBy(GSTConstants.SYSTEM);
				tds.setModifiedOn(LocalDateTime.now());
				tds.setSectionName(GSTConstants.TDS);
				tds.setTotalAmtDed(tdsDtails.getTtl_amtDed());
				tds.setTotalRecord(tdsDtails.getNo_rec());
				//tds.setTotalTax(BigDecimal.ZERO);
				tds.setTtcgst(tdsDtails.getTtl_cgst());
				tds.setTtigst(tdsDtails.getTtl_igst());
				tds.setTtsgst(tdsDtails.getTtl_sgst());
				gstr7SectionSummaryISDRepository.save(tds);

			}
			if (tdsaDtails != null) {
				Gstr7SectionSummaryISDEntity tds = new Gstr7SectionSummaryISDEntity();
				tds.setGstIn(gstin);
				tds.setBatchId(batchId.toString());
				tds.setTaxperiod(returnPeriod);
				tds.setDeriviedReturnPeriod(derivedRetPeriod);
				tds.setCreatedBy(GSTConstants.SYSTEM);
				tds.setCreatedOn(LocalDateTime.now());
				tds.setModifiedBy(GSTConstants.SYSTEM);
				tds.setModifiedOn(LocalDateTime.now());
				tds.setSectionName(GSTConstants.TDSA);
				tds.setTotalAmtDed(tdsaDtails.getTtl_amtDed());
				tds.setTotalRecord(tdsaDtails.getNo_rec());
				// tds.getTotalTax(tdsDtails.)
				tds.setTtcgst(tdsaDtails.getTtl_cgst());
				tds.setTtigst(tdsaDtails.getTtl_igst());
				tds.setTtsgst(tdsaDtails.getTtl_sgst());
				gstr7SectionSummaryISDRepository.save(tds);

			}

			if (taxpayList!=null && !taxpayList.isEmpty()) {

				List<Gstr7SectionSummaryTdsPostEntity> TaxpayEntityList = new ArrayList<>();

				for (Gstr7TaxPaySummaryDto taxpay : taxpayList) {
					Gstr7SectionSummaryTdsPostEntity taxpayEntity = new Gstr7SectionSummaryTdsPostEntity();
					taxpayEntity.setBatchId(batchId.toString());
					taxpayEntity.setGstIn(gstin);
					taxpayEntity.setTaxperiod(returnPeriod);
					taxpayEntity.setDeriviedReturnPeriod(derivedRetPeriod);
					// TaxpayEntity.setCess(cess);

					if (taxpay.getCess() != null) {
						taxpayEntity
								.setCessInterest(taxpay.getCess().getIntr());
						taxpayEntity.setCessOthers(taxpay.getCess().getOth());
						taxpayEntity.setCessPending(taxpay.getCess().getPen());
						taxpayEntity.setCessTax(taxpay.getCess().getTx());
						taxpayEntity.setCesstFee(taxpay.getCess().getFee());
						taxpayEntity.setCessTotal(taxpay.getCess().getTot());
					}

					if (taxpay.getCgst() != null) {
						taxpayEntity.setCgstFee(taxpay.getCgst().getFee());
						taxpayEntity
								.setCgstInterest(taxpay.getCgst().getIntr());
						taxpayEntity.setCgstOthers(taxpay.getCgst().getOth());
						taxpayEntity.setCgstPending(taxpay.getCgst().getPen());
						taxpayEntity.setCgstTax(taxpay.getCgst().getTx());
						taxpayEntity.setCgstTotal(taxpay.getCgst().getTot());
					}

					if (taxpay.getSgst() != null) {
						taxpayEntity.setSgstFee(taxpay.getSgst().getFee());
						taxpayEntity
								.setSgstInterest(taxpay.getSgst().getIntr());
						taxpayEntity.setSgstOthers(taxpay.getSgst().getOth());
						taxpayEntity.setSgstPending(taxpay.getSgst().getPen());
						taxpayEntity.setSgstTax(taxpay.getSgst().getTx());
						taxpayEntity.setSgstTotal(taxpay.getSgst().getTot());
					}
					if (taxpay.getIgst() != null) {
						taxpayEntity.setIgstFee(taxpay.getIgst().getFee());
						taxpayEntity
								.setIgstInterest(taxpay.getIgst().getIntr());
						taxpayEntity.setIgstOthers(taxpay.getIgst().getOth());
						taxpayEntity.setIgstPending(taxpay.getIgst().getPen());
						taxpayEntity.setIgstTax(taxpay.getIgst().getTx());
						taxpayEntity.setIgstTotal(taxpay.getIgst().getTot());
					}
					taxpayEntity.setLiabId(taxpay.getLiabId());
					taxpayEntity.setTransCode(taxpay.getTransCode());
					String transDate = taxpay.getTransDate();
				    LocalDate localDate = LocalDate.parse(transDate, formatter);
					taxpayEntity.setTransDate(localDate);
					taxpayEntity.setSectionName(GSTConstants.TAX_PAY);
					TaxpayEntityList.add(taxpayEntity);
				}
				gstr7SectionSummaryTdsPostRepository.saveAll(TaxpayEntityList);
			}

			if (tax_paid != null) {

				List<Gstr7TaxPaySummaryDto> pd_by_cash = tax_paid
						.getPd_by_cash();
				List<Gstr7SectionSummaryTdsPostEntity> TaxpayEntityList = new ArrayList<>();
				if (!pd_by_cash.isEmpty()) {
					for (Gstr7TaxPaySummaryDto taxpay : pd_by_cash) {
						Gstr7SectionSummaryTdsPostEntity taxpayEntity = new Gstr7SectionSummaryTdsPostEntity();
						taxpayEntity.setBatchId(batchId.toString());
						taxpayEntity.setGstIn(gstin);
						taxpayEntity.setTaxperiod(returnPeriod);
						taxpayEntity.setDeriviedReturnPeriod(derivedRetPeriod);
						// TaxpayEntity.setCess(cess);

						if (taxpay.getCess() != null) {
							taxpayEntity.setCessInterest(
									taxpay.getCess().getIntr());
							taxpayEntity
									.setCessOthers(taxpay.getCess().getOth());
							taxpayEntity
									.setCessPending(taxpay.getCess().getPen());
							taxpayEntity.setCessTax(taxpay.getCess().getTx());
							taxpayEntity.setCesstFee(taxpay.getCess().getFee());
							taxpayEntity
									.setCessTotal(taxpay.getCess().getTot());
						}

						if (taxpay.getCgst() != null) {
							taxpayEntity.setCgstFee(taxpay.getCgst().getFee());
							taxpayEntity.setCgstInterest(
									taxpay.getCgst().getIntr());
							taxpayEntity
									.setCgstOthers(taxpay.getCgst().getOth());
							taxpayEntity
									.setCgstPending(taxpay.getCgst().getPen());
							taxpayEntity.setCgstTax(taxpay.getCgst().getTx());
							taxpayEntity
									.setCgstTotal(taxpay.getCgst().getTot());
						}

						if (taxpay.getSgst() != null) {
							taxpayEntity.setSgstFee(taxpay.getSgst().getFee());
							taxpayEntity.setSgstInterest(
									taxpay.getSgst().getIntr());
							taxpayEntity
									.setSgstOthers(taxpay.getSgst().getOth());
							taxpayEntity
									.setSgstPending(taxpay.getSgst().getPen());
							taxpayEntity.setSgstTax(taxpay.getSgst().getTx());
							taxpayEntity
									.setSgstTotal(taxpay.getSgst().getTot());
						}
						if (taxpay.getIgst() != null) {
							taxpayEntity.setIgstFee(taxpay.getIgst().getFee());
							taxpayEntity.setIgstInterest(
									taxpay.getIgst().getIntr());
							taxpayEntity
									.setIgstOthers(taxpay.getIgst().getOth());
							taxpayEntity
									.setIgstPending(taxpay.getIgst().getPen());
							taxpayEntity.setIgstTax(taxpay.getIgst().getTx());
							taxpayEntity
									.setIgstTotal(taxpay.getIgst().getTot());
						}
						taxpayEntity.setLiabId(taxpay.getLiabId());
						taxpayEntity.setTransCode(taxpay.getTransCode());
						String transDate = taxpay.getTransDate();
					    LocalDate localDate = LocalDate.parse(transDate, formatter);
					    taxpayEntity.setTransDate(localDate);
						taxpayEntity.setDebitDesc(taxpay.getDebit_id());
						taxpayEntity.setSectionName(GSTConstants.TAX_PAID);
						TaxpayEntityList.add(taxpayEntity);
					}
				}
				gstr7SectionSummaryTdsPostRepository.saveAll(TaxpayEntityList);
			}

		} catch (Exception e) {
			String msg = "failed to parse gstr7 summary response";
			LOGGER.error(msg, e);

		}

	}

}