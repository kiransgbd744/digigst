/**
 * 
 */
package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BRule86BRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffSnapDetailsEntityRepository;
import com.ey.advisory.app.gstr3b.Gstr3BRule86BEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr3BSetOffSnapDetailsServiceImpl")
public class Gstr3BSetOffSnapDetailsServiceImpl
		implements Gstr3BSetOffSnapDetailsService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3BSetOffSnapDetailsEntityRepository")
	private Gstr3BSetOffSnapDetailsEntityRepository snapRepo;

	@Autowired
	@Qualifier("Gstr3BRule86BRepository")
	private Gstr3BRule86BRepository rule86BRepo;
	
	@Override
	public String saveToDb(Gstr3BSetOffSnapSaveDto reqDto) {

		LOGGER.debug("Inside Gstr3BSetOffSnapDetailsServiceImpl"
				+ ".getComputeStatus() method");

		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		LocalDateTime now = LocalDateTime.now();

		List<Gstr3BSetOffSnapDetailsEntity> entityList = new ArrayList<>();

		try {

			String gstin = reqDto.getGstin();
			String taxPeriod = reqDto.getTaxPeriod();
			
			Gstr3BSetOffSnapDetailsEntity obj1 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col2", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj2 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col3", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj3 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col4", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj4 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col5", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj5 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col6", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj6 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col7", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj7 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col8", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj8 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col10", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj9 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col14", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj10 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col15", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj11 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col12", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj2A = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col2A", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj2B = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col2B", false, userName, now, userName,
					now);
			
			//Negative Liability
			Gstr3BSetOffSnapDetailsEntity obj2i = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col2i", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj2ii = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col2ii", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj8a = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col8a", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity objRci9 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "col9", false, userName, now, userName,
					now);
			
			List<Gstr3bDetailsDto> gstr3bDetails = reqDto.getGstr3bDetails();
			List<LedgerDetailsDto> ledgerDetails = reqDto.getLedgerDetails();

			for (Gstr3bDetailsDto gstr3bDetailsDto : gstr3bDetails) {

				if (gstr3bDetailsDto.getDesc()
						.equalsIgnoreCase("Integrated Tax")) {

					obj1.setIgst(gstr3bDetailsDto.getOtrci());
					obj2.setIgst(gstr3bDetailsDto.getPdi());
					obj3.setIgst(gstr3bDetailsDto.getPdc());
					obj4.setIgst(gstr3bDetailsDto.getPds());
					obj5.setIgst(gstr3bDetailsDto.getPdcs());
					obj6.setIgst(gstr3bDetailsDto.getOtrc7());
					obj7.setIgst(gstr3bDetailsDto.getRci8());
					obj8.setIgst(gstr3bDetailsDto.getInti10());
					obj9.setIgst(gstr3bDetailsDto.getUcb14());
					obj10.setIgst(BigDecimal.ZERO);
					obj11.setIgst(gstr3bDetailsDto.getLateFee12());
					obj2A.setIgst(gstr3bDetailsDto.getOtrci2A());
					obj2B.setIgst(gstr3bDetailsDto.getOtrci2B());
					
					obj2i.setIgst(gstr3bDetailsDto.getAdjNegative2i());
					obj2ii.setIgst(gstr3bDetailsDto.getNetOthRecTaxPayable2i());
					obj8a.setIgst(gstr3bDetailsDto.getAdjNegative8A());
					objRci9.setIgst(gstr3bDetailsDto.getRci9());

				} else if (gstr3bDetailsDto.getDesc()
						.equalsIgnoreCase("Central Tax")) {

					obj1.setCgst(gstr3bDetailsDto.getOtrci());
					obj2.setCgst(gstr3bDetailsDto.getPdi());
					obj3.setCgst(gstr3bDetailsDto.getPdc());
					obj4.setCgst(gstr3bDetailsDto.getPds());
					obj5.setCgst(gstr3bDetailsDto.getPdcs());
					obj6.setCgst(gstr3bDetailsDto.getOtrc7());
					obj7.setCgst(gstr3bDetailsDto.getRci8());
					obj8.setCgst(gstr3bDetailsDto.getInti10());
					obj9.setCgst(gstr3bDetailsDto.getUcb14());
					obj10.setCgst(BigDecimal.ZERO);
					obj11.setIgst(gstr3bDetailsDto.getLateFee12());
					obj2A.setCgst(gstr3bDetailsDto.getOtrci2A());
					obj2B.setCgst(gstr3bDetailsDto.getOtrci2B());
					
					obj2i.setCgst(gstr3bDetailsDto.getAdjNegative2i());
					obj2ii.setCgst(gstr3bDetailsDto.getNetOthRecTaxPayable2i());
					obj8a.setCgst(gstr3bDetailsDto.getAdjNegative8A());
					objRci9.setCgst(gstr3bDetailsDto.getRci9());

				} else if (gstr3bDetailsDto.getDesc()
						.equalsIgnoreCase("State/UT Tax")) {

					obj1.setSgst(gstr3bDetailsDto.getOtrci());
					obj2.setSgst(gstr3bDetailsDto.getPdi());
					obj3.setSgst(gstr3bDetailsDto.getPdc());
					obj4.setSgst(gstr3bDetailsDto.getPds());
					obj5.setSgst(gstr3bDetailsDto.getPdcs());
					obj6.setSgst(gstr3bDetailsDto.getOtrc7());
					obj7.setSgst(gstr3bDetailsDto.getRci8());
					obj8.setSgst(gstr3bDetailsDto.getInti10());
					obj9.setSgst(gstr3bDetailsDto.getUcb14());
					obj10.setSgst(BigDecimal.ZERO);
					obj11.setIgst(gstr3bDetailsDto.getLateFee12());
					obj2A.setSgst(gstr3bDetailsDto.getOtrci2A());
					obj2B.setSgst(gstr3bDetailsDto.getOtrci2B());
					
					obj2i.setSgst(gstr3bDetailsDto.getAdjNegative2i());
					obj2ii.setSgst(gstr3bDetailsDto.getNetOthRecTaxPayable2i());
					obj8a.setSgst(gstr3bDetailsDto.getAdjNegative8A());
					objRci9.setSgst(gstr3bDetailsDto.getRci9());

				} else if (gstr3bDetailsDto.getDesc()
						.equalsIgnoreCase("Cess")) {

					obj1.setCess(gstr3bDetailsDto.getOtrci());
					obj2.setCess(gstr3bDetailsDto.getPdi());
					obj3.setCess(gstr3bDetailsDto.getPdc());
					obj4.setCess(gstr3bDetailsDto.getPds());
					obj5.setCess(gstr3bDetailsDto.getPdcs());
					obj6.setCess(gstr3bDetailsDto.getOtrc7());
					obj7.setCess(gstr3bDetailsDto.getRci8());
					obj8.setCess(gstr3bDetailsDto.getInti10());
					obj9.setCess(gstr3bDetailsDto.getUcb14());
					obj10.setCess(BigDecimal.ZERO);
					obj11.setIgst(gstr3bDetailsDto.getLateFee12());
					obj2A.setCess(gstr3bDetailsDto.getOtrci2A());
					obj2B.setCess(gstr3bDetailsDto.getOtrci2B());
					
					obj2i.setCess(gstr3bDetailsDto.getAdjNegative2i());
					obj2ii.setCess(gstr3bDetailsDto.getNetOthRecTaxPayable2i());
					obj8a.setCess(gstr3bDetailsDto.getAdjNegative8A());
					objRci9.setCess(gstr3bDetailsDto.getRci9());

				}

			}
			
			entityList.add(obj1);
			entityList.add(obj2);
			entityList.add(obj3);
			entityList.add(obj4);
			entityList.add(obj5);
			entityList.add(obj6);
			entityList.add(obj7);
			entityList.add(obj8);
			entityList.add(obj9);
			entityList.add(obj10);
			entityList.add(obj11);
			entityList.add(obj2A);
			entityList.add(obj2B);
			
			entityList.add(obj2i);
			entityList.add(obj2ii);
			entityList.add(obj8a);
			entityList.add(objRci9);
			
			
			Gstr3BSetOffSnapDetailsEntity obj12 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "CLB_TAX", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj13 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "CLB_INTEREST", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj14 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "CLB_LATEFEE", false, userName, now, userName,
					now);
			Gstr3BSetOffSnapDetailsEntity obj15 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "CRLB_TAX_C_MONTH", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj21 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "NEGATIVE_TAX_LIAB", false, userName, now, userName,
					now);
			
			//closing balance
			Gstr3BSetOffSnapDetailsEntity obj16 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "currMonthUtil_Credit", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj31 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "currMonthUtil_Cash", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj32 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "currMonthUtil_Negative", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj17 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "clsBal_Credit", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj33 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "clsBal_Cash", false, userName, now, userName,
					now);
			
			Gstr3BSetOffSnapDetailsEntity obj34 = new Gstr3BSetOffSnapDetailsEntity(
					gstin, taxPeriod, "clsBal_Negative", false, userName, now, userName,
					now);
			
			for (LedgerDetailsDto ledgerDetailsDto : ledgerDetails) {
				
				if(ledgerDetailsDto.getDesc().equalsIgnoreCase("tx")) {
					obj12.setIgst(ledgerDetailsDto.getI());
					obj12.setCgst(ledgerDetailsDto.getC());
					obj12.setSgst(ledgerDetailsDto.getS());
					obj12.setCess(ledgerDetailsDto.getCs());
					obj12.setTotalTax(ledgerDetailsDto.getTotal());
					
					obj15.setIgst(ledgerDetailsDto.getCri());
					obj15.setCgst(ledgerDetailsDto.getCrc());
					obj15.setSgst(ledgerDetailsDto.getCrs());
					obj15.setCess(ledgerDetailsDto.getCrcs());
					obj15.setTotalTax(ledgerDetailsDto.getCrTotal());
					
					obj21.setIgst(ledgerDetailsDto.getNlbIgst());
					obj21.setCgst(ledgerDetailsDto.getNlbCgst());
					obj21.setSgst(ledgerDetailsDto.getNlbSgst());
					obj21.setCess(ledgerDetailsDto.getNlbCess());
					//obj21.setTotalTax(ledgerDetailsDto.getTotal());
					
				}else if(ledgerDetailsDto.getDesc().equalsIgnoreCase("intr")) {
					
					obj13.setIgst(ledgerDetailsDto.getI());
					obj13.setCgst(ledgerDetailsDto.getC());
					obj13.setSgst(ledgerDetailsDto.getS());
					obj13.setCess(ledgerDetailsDto.getCs());
					obj13.setTotalTax(ledgerDetailsDto.getTotal());
					
				}else if(ledgerDetailsDto.getDesc().equalsIgnoreCase("fee")) {
					
					obj14.setIgst(ledgerDetailsDto.getI());
					obj14.setCgst(ledgerDetailsDto.getC());
					obj14.setSgst(ledgerDetailsDto.getS());
					obj14.setCess(ledgerDetailsDto.getCs());
					obj14.setTotalTax(ledgerDetailsDto.getTotal());
				}
				else if(ledgerDetailsDto.getDesc().equalsIgnoreCase("currMonthUtil")) {
					obj31.setIgst(ledgerDetailsDto.getI());
					obj31.setCgst(ledgerDetailsDto.getC());
					obj31.setSgst(ledgerDetailsDto.getS());
					obj31.setCess(ledgerDetailsDto.getCs());
					obj31.setTotalTax(ledgerDetailsDto.getTotal());
					
					obj16.setIgst(ledgerDetailsDto.getCri());
					obj16.setCgst(ledgerDetailsDto.getCrc());
					obj16.setSgst(ledgerDetailsDto.getCrs());
					obj16.setCess(ledgerDetailsDto.getCrcs());
					obj16.setTotalTax(ledgerDetailsDto.getCrTotal());
					
					obj32.setIgst(ledgerDetailsDto.getNlbIgst());
					obj32.setCgst(ledgerDetailsDto.getNlbCgst());
					obj32.setSgst(ledgerDetailsDto.getNlbSgst());
					obj32.setCess(ledgerDetailsDto.getNlbCess());
					
				} else if(ledgerDetailsDto.getDesc().equalsIgnoreCase("clsBal")) {
					obj33.setIgst(ledgerDetailsDto.getI());
					obj33.setCgst(ledgerDetailsDto.getC());
					obj33.setSgst(ledgerDetailsDto.getS());
					obj33.setCess(ledgerDetailsDto.getCs());
					obj33.setTotalTax(ledgerDetailsDto.getTotal());
					
					obj17.setIgst(ledgerDetailsDto.getCri());
					obj17.setCgst(ledgerDetailsDto.getCrc());
					obj17.setSgst(ledgerDetailsDto.getCrs());
					obj17.setCess(ledgerDetailsDto.getCrcs());
					obj17.setTotalTax(ledgerDetailsDto.getCrTotal());
					
					obj34.setIgst(ledgerDetailsDto.getNlbIgst());
					obj34.setCgst(ledgerDetailsDto.getNlbCgst());
					obj34.setSgst(ledgerDetailsDto.getNlbSgst());
					obj34.setCess(ledgerDetailsDto.getNlbCess());
					
					
				}
				
			}
			
			entityList.add(obj12);
			entityList.add(obj13);
			entityList.add(obj14);
			entityList.add(obj15);
			
			entityList.add(obj16);
			entityList.add(obj17);
			entityList.add(obj21);
			
			entityList.add(obj31);
			entityList.add(obj32);
			entityList.add(obj33);
			entityList.add(obj34);
			
			
			snapRepo.softDelete(taxPeriod, gstin);
			snapRepo.saveAll(entityList);

		} catch (Exception ex) {
			LOGGER.error("Error while saving into"
					+ " Gstr3BSetOffSnapDetailsServiceImpl table {} : ", ex);
		}
		
		return "success";

	}

	@Override
	public String saveRule86BToDb(Gstr3BSetRule86BSaveDto reqDto) {
		
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		LocalDateTime now = LocalDateTime.now();
	
		try {
			Gstr3BRule86BEntity obj = new Gstr3BRule86BEntity();
			obj.setActive(true);
			obj.setCreatedBy(userName);
			obj.setCreatedDate(now);
			obj.setGstin(reqDto.getGstin());
			obj.setRule86B(reqDto.isRule86B());
			obj.setTaxPeriod(reqDto.getTaxPeriod());
			
			rule86BRepo.updateIsActive(reqDto.getGstin(), 
					reqDto.getTaxPeriod());
			
			rule86BRepo.save(obj);
			
			return "Success";
			
			
		} catch (Exception e) {
			LOGGER.error("Exception occured in saveRule86BToDb");
			throw new AppException();
		}
		
		
		
		
		
	}

}
