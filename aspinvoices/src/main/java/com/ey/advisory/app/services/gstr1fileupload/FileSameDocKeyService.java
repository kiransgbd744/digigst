package com.ey.advisory.app.services.gstr1fileupload;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr1B2csDetailsEntity;

@Service("FileSameDocKeyService")
public class FileSameDocKeyService {

	
	public Gstr1B2csDetailsEntity b2csSame(Gstr1B2csDetailsEntity b2cs) {
		BigDecimal cessAmt = BigDecimal.ZERO;
		BigDecimal cgstAmt = BigDecimal.ZERO;
		BigDecimal igstAmt = BigDecimal.ZERO;
		BigDecimal newQnt = BigDecimal.ZERO;
		BigDecimal newRate = BigDecimal.ZERO;
		BigDecimal newTaxVal = BigDecimal.ZERO;
		BigDecimal newSupVal = BigDecimal.ZERO;
		BigDecimal orgQnt = BigDecimal.ZERO;
		BigDecimal orgRate = BigDecimal.ZERO;
		BigDecimal orgSupVal = BigDecimal.ZERO;
		BigDecimal orgTaxVal = BigDecimal.ZERO;
		BigDecimal sgstAmt = BigDecimal.ZERO;
		BigDecimal totalValue = BigDecimal.ZERO;
		
		Gstr1B2csDetailsEntity entityDetails = new Gstr1B2csDetailsEntity();
		
	     cessAmt = entityDetails.getCessAmt()
				.add(b2cs.getCessAmt());
		 cgstAmt = entityDetails.getCgstAmt()
				.add(b2cs.getCgstAmt());
		 igstAmt = entityDetails.getIgstAmt()
				.add(b2cs.getIgstAmt());
		 newQnt = entityDetails.getNewQnt()
				.add(b2cs.getNewQnt());
		 newRate = entityDetails.getNewRate()
				.add(b2cs.getNewRate());
		 newTaxVal = entityDetails.getNewTaxVal()
				.add(b2cs.getNewTaxVal());
		 newSupVal = entityDetails.getNewSupVal()
				.add(b2cs.getNewSupVal());
		 orgQnt = entityDetails.getOrgQnt()
				.add(b2cs.getOrgQnt());
		 orgRate = entityDetails.getOrgRate()
				.add(b2cs.getOrgRate());
		 orgSupVal = entityDetails.getOrgSupVal()
				.add(b2cs.getOrgSupVal());
		 orgTaxVal = entityDetails.getOrgTaxVal()
				.add(b2cs.getOrgTaxVal());
		 sgstAmt = entityDetails.getSgstAmt()
				.add(b2cs.getSgstAmt());
		 totalValue = entityDetails.getTotalValue()
				.add(b2cs.getTotalValue());
		    
		    entityDetails.setSgstin(b2cs.getSgstin());
		    entityDetails.setReturnPeriod(b2cs.getReturnPeriod());
		    entityDetails.setTransType(b2cs.getTransType());
		    entityDetails.setMonth(b2cs.getMonth());
		    entityDetails.setOrgPos(b2cs.getOrgPos());
		    entityDetails.setOrgHsnOrSac(b2cs.getOrgHsnOrSac());
		    entityDetails.setOrgUom(b2cs.getOrgUom());
		    entityDetails.setOrgCGstin(b2cs.getOrgCGstin());
		    entityDetails.setNewPos(b2cs.getNewPos());
		    entityDetails.setNewHsnOrSac(b2cs.getNewHsnOrSac());
		    entityDetails.setNewUom(b2cs.getNewUom());
		    entityDetails.setNewGstin(b2cs.getNewGstin());
		    entityDetails.setInvB2csKey(b2cs.getInvB2csKey());
		    entityDetails.setGstnB2csKey(b2cs.getGstnB2csKey());
		    entityDetails.setFileId(b2cs.getFileId());
		    entityDetails.setDerivedRetPeriod(b2cs.getDerivedRetPeriod());
		    entityDetails.setCessAmt(cessAmt);
			entityDetails.setCgstAmt(cgstAmt);
			entityDetails.setIgstAmt(igstAmt);
			entityDetails.setNewQnt(newQnt);
			entityDetails.setNewRate(newRate);
			entityDetails.setNewTaxVal(newTaxVal);
			entityDetails.setNewSupVal(newSupVal);
			entityDetails.setOrgQnt(orgQnt);
			entityDetails.setOrgRate(orgRate);
			entityDetails.setOrgSupVal(orgSupVal);
			entityDetails.setOrgTaxVal(orgTaxVal);
			entityDetails.setSgstAmt(sgstAmt);
			entityDetails.setTotalValue(totalValue);
			
			return entityDetails;
			
	}

			

}
