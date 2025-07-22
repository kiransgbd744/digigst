/**
 * 
 */
package com.ey.advisory.app.services.businessvalidation.table3h3i;

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
import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;
import com.ey.advisory.app.services.validation.BusinessRuleValidator;
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
public class SupTypeSez
		implements BusinessRuleValidator<InwardTable3I3HExcelEntity> {

	/*@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;*/
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(InwardTable3I3HExcelEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		/*gstinInfoRepository = StaticContextHolder
				.getBean("GSTNDetailRepository", GSTNDetailRepository.class);*/

		if (document.getSupplierGSTINorpan() != null) {
			ehcachegstin = StaticContextHolder.
					getBean("Ehcachegstin",Ehcachegstin.class);
			String groupCode = TenantContext.getTenantId();	
			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getSupplierGSTINorpan());
			/*List<GSTNDetailEntity> gstin = gstinInfoRepository
					.findByGstin(GenUtil.trimAndConvToUpperCase(
							document.getSupplierGSTINorpan()));*/
			if (gstin != null ) {
				if (gstin.getRegistrationType() != null) {
					String regType = gstin.getRegistrationType();
					if (GSTConstants.SEZ.equalsIgnoreCase(regType)) {
						BigDecimal cgst = BigDecimal.ZERO;
						String cgsts = document.getCentralTaxAmount();
						if (cgsts != null && !cgsts.isEmpty()) {
							cgst = NumberFomatUtil.getBigDecimal(cgsts);
						}

						BigDecimal sgst = BigDecimal.ZERO;
						String sgstss = document.getStateUTTaxAmount();
						if (sgstss != null && !sgstss.isEmpty()) {
							sgst = NumberFomatUtil.getBigDecimal(sgstss);
						}
						if (sgst.compareTo(BigDecimal.ZERO) != 0
								|| cgst.compareTo(BigDecimal.ZERO) != 0) {
							errorLocations.add(GSTConstants.CGST_AMOUNT);
							errorLocations.add(GSTConstants.SGST_AMOUNT);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0432",
									" CGST / SGST cannot be applied as "
											+ "Supplier is Registered "
											+ "as SEZ.",
									location));
							return errors;

						}
					}
				}
			}
		}

		return errors;
	}

}
