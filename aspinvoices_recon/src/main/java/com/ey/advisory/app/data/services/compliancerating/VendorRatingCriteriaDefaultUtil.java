package com.ey.advisory.app.data.services.compliancerating;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.VendorRatingCriteriaEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorRatingCriteriaRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

/**
 * this class is responsible for persisting default rating criteria
 * 
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("VendorRatingCriteriaDefaultUtil")
public class VendorRatingCriteriaDefaultUtil {

	@Autowired
	private VendorRatingCriteriaRepository repo;

	private static final String GSTR1 = "GSTR1";
	private static final String GSTR3B = "GSTR3B";
	private static final String PRIOR = "PRIOR";
	private static final String AFTER = "AFTER";
	public static final String VENDOR = "vendor";
	public static final String CUSTOMER = "customer";
	public static final String MY_COMPLIANCE = "my";

	public static final Map<String, String> SOURCEMAP = ImmutableMap
			.<String, String>builder().put("vendor", "VENDOR")
			.put("customer", "CUSTOMER").put("my", "MY_COMPLIANCE").build();

	public void persistDefaultRatingCriteria(Long entityId, String source) {

		if (LOGGER.isDebugEnabled()) {

			LOGGER.debug("Defualt vendor rating persist starts");
		}
		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			List<VendorRatingCriteriaEntity> entities = new ArrayList<>();

			VendorRatingCriteriaEntity e1 = new VendorRatingCriteriaEntity();
			e1.setReturnType(GSTR1);
			e1.setDueType(PRIOR);
			e1.setFromDay(1);
			e1.setToDay(2);
			e1.setRating(new BigDecimal(5));
			e1.setEntityId(entityId);
			e1.setSource(SOURCEMAP.get(source));
			e1.setDelete(false);
			e1.setCreatedBy(userName);
			e1.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e2 = new VendorRatingCriteriaEntity();
			e2.setReturnType(GSTR1);
			e2.setDueType(PRIOR);
			e2.setFromDay(3);
			e2.setToDay(5);
			e2.setRating(new BigDecimal(5));
			e2.setEntityId(entityId);
			e2.setSource(SOURCEMAP.get(source));
			e2.setDelete(false);
			e2.setCreatedBy(userName);
			e2.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e3 = new VendorRatingCriteriaEntity();
			e3.setReturnType(GSTR1);
			e3.setDueType(PRIOR);
			e3.setFromDay(6);
			e3.setToDay(10);
			e3.setRating(new BigDecimal(5));
			e3.setEntityId(entityId);
			e3.setSource(SOURCEMAP.get(source));
			e3.setDelete(false);
			e3.setCreatedBy(userName);
			e3.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e4 = new VendorRatingCriteriaEntity();
			e4.setReturnType(GSTR1);
			e4.setDueType("ON");
			e4.setRating(new BigDecimal(5));
			e4.setEntityId(entityId);
			e4.setSource(SOURCEMAP.get(source));
			e4.setDelete(false);
			e4.setCreatedBy(userName);
			e4.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e5 = new VendorRatingCriteriaEntity();
			e5.setReturnType(GSTR1);
			e5.setDueType(AFTER);
			e5.setFromDay(1);
			e5.setToDay(2);
			e5.setRating(new BigDecimal(4));
			e5.setEntityId(entityId);
			e5.setSource(SOURCEMAP.get(source));
			e5.setDelete(false);
			e5.setCreatedBy(userName);
			e5.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e6 = new VendorRatingCriteriaEntity();
			e6.setReturnType(GSTR1);
			e6.setDueType(AFTER);
			e6.setFromDay(3);
			e6.setToDay(5);
			e6.setRating(new BigDecimal(3));
			e6.setEntityId(entityId);
			e6.setSource(SOURCEMAP.get(source));
			e6.setDelete(false);
			e6.setCreatedBy(userName);
			e6.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e7 = new VendorRatingCriteriaEntity();
			e7.setReturnType(GSTR1);
			e7.setDueType(AFTER);
			e7.setFromDay(6);
			e7.setToDay(10);
			e7.setRating(new BigDecimal(2));
			e7.setEntityId(entityId);
			e7.setSource(SOURCEMAP.get(source));
			e7.setDelete(false);
			e7.setCreatedBy(userName);
			e7.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e8 = new VendorRatingCriteriaEntity();
			e8.setReturnType(GSTR1);
			e8.setDueType(AFTER);
			e8.setFromDay(10);
			e8.setToDay(1096);
			e8.setRating(new BigDecimal(1));
			e8.setEntityId(entityId);
			e8.setSource(SOURCEMAP.get(source));
			e8.setDelete(false);
			e8.setCreatedBy(userName);
			e8.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e9 = new VendorRatingCriteriaEntity();
			e9.setReturnType(GSTR1);
			e9.setDueType("NOT");
			e9.setRating(BigDecimal.valueOf(0.1));
			e9.setEntityId(entityId);
			e9.setSource(SOURCEMAP.get(source));
			e9.setDelete(false);
			e9.setCreatedBy(userName);
			e9.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e10 = new VendorRatingCriteriaEntity();
			e10.setReturnType(GSTR3B);
			e10.setDueType(PRIOR);
			e10.setFromDay(1);
			e10.setToDay(2);
			e10.setRating(new BigDecimal(5));
			e10.setEntityId(entityId);
			e10.setSource(SOURCEMAP.get(source));
			e10.setDelete(false);
			e10.setCreatedBy(userName);
			e10.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e11 = new VendorRatingCriteriaEntity();
			e11.setReturnType(GSTR3B);
			e11.setDueType(PRIOR);
			e11.setFromDay(3);
			e11.setToDay(5);
			e11.setRating(new BigDecimal(5));
			e11.setEntityId(entityId);
			e11.setSource(SOURCEMAP.get(source));
			e11.setDelete(false);
			e11.setCreatedBy(userName);
			e11.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e12 = new VendorRatingCriteriaEntity();
			e12.setReturnType(GSTR3B);
			e12.setDueType(PRIOR);
			e12.setFromDay(6);
			e12.setToDay(10);
			e12.setRating(new BigDecimal(5));
			e12.setEntityId(entityId);
			e12.setSource(SOURCEMAP.get(source));
			e12.setDelete(false);
			e12.setCreatedBy(userName);
			e12.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e13 = new VendorRatingCriteriaEntity();
			e13.setReturnType(GSTR3B);
			e13.setDueType("ON");
			e13.setRating(new BigDecimal(5));
			e13.setEntityId(entityId);
			e13.setSource(SOURCEMAP.get(source));
			e13.setDelete(false);
			e13.setCreatedBy(userName);
			e13.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e14 = new VendorRatingCriteriaEntity();
			e14.setReturnType(GSTR3B);
			e14.setDueType(AFTER);
			e14.setFromDay(1);
			e14.setToDay(2);
			e14.setRating(new BigDecimal(4));
			e14.setEntityId(entityId);
			e14.setSource(SOURCEMAP.get(source));
			e14.setDelete(false);
			e14.setCreatedBy(userName);
			e14.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e15 = new VendorRatingCriteriaEntity();
			e15.setReturnType(GSTR3B);
			e15.setDueType(AFTER);
			e15.setFromDay(3);
			e15.setToDay(5);
			e15.setRating(new BigDecimal(3));
			e15.setEntityId(entityId);
			e15.setSource(SOURCEMAP.get(source));
			e15.setDelete(false);
			e15.setCreatedBy(userName);
			e15.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e16 = new VendorRatingCriteriaEntity();
			e16.setReturnType(GSTR3B);
			e16.setDueType(AFTER);
			e16.setFromDay(6);
			e16.setToDay(10);
			e16.setRating(new BigDecimal(2));
			e16.setEntityId(entityId);
			e16.setSource(SOURCEMAP.get(source));
			e16.setDelete(false);
			e16.setCreatedBy(userName);
			e16.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e17 = new VendorRatingCriteriaEntity();
			e17.setReturnType(GSTR3B);
			e17.setDueType(AFTER);
			e17.setFromDay(10);
			e17.setToDay(1096);
			e17.setRating(new BigDecimal(1));
			e17.setEntityId(entityId);
			e17.setSource(SOURCEMAP.get(source));
			e17.setDelete(false);
			e17.setCreatedBy(userName);
			e17.setCreatedOn(LocalDateTime.now());

			VendorRatingCriteriaEntity e18 = new VendorRatingCriteriaEntity();
			e18.setReturnType(GSTR3B);
			e18.setDueType("NOT");
			e18.setRating(BigDecimal.valueOf(0.1));
			e18.setEntityId(entityId);
			e18.setSource(SOURCEMAP.get(source));
			e18.setDelete(false);
			e18.setCreatedBy(userName);
			e18.setCreatedOn(LocalDateTime.now());

			Collections.addAll(entities, e1, e2, e3, e4, e5, e6, e7, e8, e9,
					e10, e11, e12, e13, e14, e15, e16, e17, e18);

			repo.saveAll(entities);
		} catch (Exception e) {
			String errMsg = "Error while persisting default values";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

}
