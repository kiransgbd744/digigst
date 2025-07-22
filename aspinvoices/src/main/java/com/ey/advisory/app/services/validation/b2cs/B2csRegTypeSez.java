/**
 * 
 */
package com.ey.advisory.app.services.validation.b2cs;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredB2csEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * @author Mahesh.Golla
 *
 */
public class B2csRegTypeSez
		implements B2csBusinessRuleValidator<Gstr1AsEnteredB2csEntity> {

	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredB2csEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();
		String groupCode = TenantContext.getTenantId();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;
		
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);
		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getSgstin());

	/*	gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);
		List<GSTNDetailEntity> gstin = gstinInfoRepository.findByGstin(
				GenUtil.trimAndConvToUpperCase(document.getSgstin().trim()));*/
		if (gstin != null ) {
			if (gstin.getRegistrationType() != null && !gstin.getRegistrationType().isEmpty() ) {
				String regType = gstin.getRegistrationType().trim();
				if (GSTConstants.SEZ.equalsIgnoreCase(regType)) {

					BigDecimal cgstAmt = BigDecimal.ZERO;
					String cgst = (document.getCgstAmt() != null)
							? document.getCgstAmt().trim() : null;
					if (cgst != null && !cgst.isEmpty()) {
						cgstAmt = NumberFomatUtil.getBigDecimal(cgst);
					}
					BigDecimal sgstAmt = BigDecimal.ZERO;
					String sgsts = (document.getSgstAmt() != null)
							? document.getSgstAmt().trim() : null;
					if (sgsts != null && !sgsts.isEmpty()) {
						sgstAmt = NumberFomatUtil.getBigDecimal(sgsts);
					}

					if (sgstAmt.compareTo(BigDecimal.ZERO) != 0
							|| cgstAmt.compareTo(BigDecimal.ZERO) != 0) {
						errorLocations.add(GSTConstants.CGST_AMOUNT);
						errorLocations.add(GSTConstants.SGST_AMOUNT);
						TransDocProcessingResultLoc location = 
								new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(
								new ProcessingResult(APP_VALIDATION, "ER5504",
										" CGST / SGST cannot be applied as "
												+ "Supplier is Registered as SEZ.",
										location));
						return errors;

					}
				}

			}
		}
		return errors;
	}

}
