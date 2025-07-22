package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.IGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("SgstCgstIgstValidator")
public class SgstCgstIgstValidator
		implements DocRulesValidator<OutwardTransDocument> {
	
	private static final String INTRA_STATE_ERR_CODE = "ER0507";
			
	private static final String INTER_STATE_ERR_CODE = "ER0508";
			
	private static final String INTRA_STATE_ERR_MSG = 
			"IGST cannot be applied in case of INTRA state supply";
	
	private static final String INTER_STATE_ERR_MSG = 
			"CGST / SGST cannot be applied in case of INTER state supply";
			
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;
	
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		// TODO Auto-generated method stub
		List<ProcessingResult> errors = new ArrayList<>();
		List<OutwardTransDocLineItem> items = document.getLineItems();
		
		String groupCode = TenantContext.getTenantId();	
		// If any one of sgstin or POS or Supply Type is not there
		// return immediately without any errors. We are assuming that
		// these conditions will be validated in other rules.
		if (!sgstinExists(document) || !posExists(document)
				|| !supplyTypeExists(document)) return errors;

		// If it is not a tax document or a Deemed Exports document
		// then return the empty errors array.
		if (!isTaxOrDxp(document)) return errors; 
		
		for (int idx = 0; idx < items.size(); idx++) {
			
			OutwardTransDocLineItem item = items.get(idx);
			boolean isIntra = isIntraState(document);
			
			if (isIntra && igstExists(item)) {
				if(GSTConstants.Y.equalsIgnoreCase(
						document.getSection7OfIgstFlag())) 
					return errors;
				/*gstinInfoRepository = StaticContextHolder
						.getBean("GSTNDetailRepository", GSTNDetailRepository.class);
				*/
				ehcachegstin = StaticContextHolder.
						getBean("Ehcachegstin",Ehcachegstin.class);
				
				GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
						document.getSgstin());
				
				/*List<GSTNDetailEntity> gstin = gstinInfoRepository
						.findByGstin(document.getSgstin());*/
				if (gstin == null) return errors;
					if (gstin.getRegistrationType() == null) return errors;
						String regType = gstin.getRegistrationType();
						if (GSTConstants.SEZ.equalsIgnoreCase(regType)) return errors;
				ProcessingResult res =  createErrorsForFields(
						idx, INTRA_STATE_ERR_CODE, 
						INTRA_STATE_ERR_MSG, IGST_AMOUNT);
				errors.add(res);
			} 
			
			if (!isIntra  && cgstOrSgstExists(item)) {
				ProcessingResult res = createErrorsForFields(
						idx, INTER_STATE_ERR_CODE, 
						INTER_STATE_ERR_MSG, CGST_AMOUNT, SGST_AMOUNT);
				errors.add(res);			
			}
		}
		
		return errors;
	}

	private boolean sgstinExists(OutwardTransDocument doc) {
		return (doc.getSgstin() != null) && (!doc.getSgstin().isEmpty());
	}

	private boolean posExists(OutwardTransDocument doc) {
		return (doc.getPos() != null) && (!doc.getPos().isEmpty());
	}

	private boolean supplyTypeExists(OutwardTransDocument doc) {
		return (doc.getSupplyType() != null)
				&& (!doc.getSupplyType().isEmpty());
	}

	private String getFirst2CharsOfSgstin(OutwardTransDocument doc) {
		return doc.getSgstin().substring(0, 2);
	}

	private boolean isTaxOrDxp(OutwardTransDocument doc) {
	   String sType = doc.getSupplyType();
		return GSTConstants.TAX.equalsIgnoreCase(sType) 
				|| GSTConstants.DXP.equalsIgnoreCase(sType);
	}
	
	private boolean isIntraState(OutwardTransDocument doc) {
		String first2Chars = getFirst2CharsOfSgstin(doc);
		return first2Chars.equals(doc.getPos());
	}

	private boolean igstExists(OutwardTransDocLineItem item) {
		
		BigDecimal igstAmount = item.getIgstAmount();
		BigDecimal igstRate = item.getIgstRate();
		if (igstAmount == null) {
			igstAmount = BigDecimal.ZERO;
		}
		if (igstRate == null) {
			igstRate = BigDecimal.ZERO;
		}
		
		return (BigDecimal.ZERO.compareTo(igstAmount) != 0) 
				|| BigDecimal.ZERO.compareTo(igstRate) != 0;
	}
	
	private boolean cgstOrSgstExists(OutwardTransDocLineItem item) {
		
		BigDecimal sgstAmount = item.getSgstAmount();
		BigDecimal sgstRate = item.getSgstRate();
		BigDecimal cgstAmount = item.getCgstAmount();
		BigDecimal cgstRate = item.getCgstRate();
		if (sgstAmount == null) {
			sgstAmount = BigDecimal.ZERO;
		}
		if (sgstRate == null) {
			sgstRate = BigDecimal.ZERO;
		}
		
		
		if (cgstAmount == null) {
			cgstAmount = BigDecimal.ZERO;
		}
		if (cgstRate == null) {
			cgstRate = BigDecimal.ZERO;
		}
		
		
		
		
		return (BigDecimal.ZERO.compareTo(cgstAmount) != 0) ||
				(BigDecimal.ZERO.compareTo(sgstAmount) != 0) 
				|| (BigDecimal.ZERO.compareTo(cgstRate) != 0) ||
						BigDecimal.ZERO.compareTo(sgstRate) != 0;
	}
	
	private ProcessingResult createErrorsForFields(int itemNo,
			String errCode, String errDesc, String...colNames) {
		TransDocProcessingResultLoc location = 
				new TransDocProcessingResultLoc(itemNo, colNames);
		return new ProcessingResult(
				APP_VALIDATION, errCode, errDesc, location);				
	}
}
