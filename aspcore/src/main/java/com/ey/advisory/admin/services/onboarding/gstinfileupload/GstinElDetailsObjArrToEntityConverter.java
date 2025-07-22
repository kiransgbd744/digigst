package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.common.EYDateUtil;

/**
 * @author Sasidhar Reddy
 *
 */
@Service("GstinElDetailsObjArrToEntityConverter")
public class GstinElDetailsObjArrToEntityConverter {

	public static final String EY = "EY";
	public static final String Normal = "Normal";

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	public Pair<List<String>, List<EntityInfoEntity>> convertDataIntoRecords(
			List<Object[]> elrGstinObjects, String groupCode, Long groupId)
			throws Exception {List<String> errorMsgs = new ArrayList<>();
			List<EntityInfoEntity> entityInfoEntities = new ArrayList<>();
			for (Object[] entityInfo : elrGstinObjects) {
			    EntityInfoEntity entityInfoEntity = new EntityInfoEntity();

			    String entityName = (entityInfo[0] != null) ? entityInfo[0].toString().trim() : null;
			    String pan = (entityInfo[1] != null) ? entityInfo[1].toString().trim() : null;
			    String companyHq = (entityInfo[2] != null) ? entityInfo[2].toString().trim() : null;

			    // Check for mandatory fields
			    if (entityName == null || entityName.isEmpty()) {
			        errorMsgs.add(" <- Entity Name is mandatory.");
			    }
			    if (companyHq == null || companyHq.isEmpty()) {
			        errorMsgs.add(" <- CompanyHq is mandatory.");
			    }
			    if (pan == null || pan.equals("null") || pan.isEmpty()) {
			        pan = "";
			        errorMsgs.add(" <- Pan number is mandatory.");
			    }

			    // Validate PAN format
			    String regex = "^[a-zA-Z]{5}[0-9]{4}[a-zA-Z]$";
			    Pattern pattern = Pattern.compile(regex);
			    Matcher matcher = pattern.matcher(pan);
			    if (!pan.isEmpty() && (pan.length() != 10 || !matcher.matches())) {
			        errorMsgs.add(pan + " <- Invalid Pan number, "
			                + "Please maintain as per the given example - PANNU1234A");
			    }

			    // Check for duplicate entries in the database
			    if (!pan.isEmpty() && entityInfoDetailsRepository.pancount(pan) > 0) {
			        errorMsgs.add(pan + " <- Pan number already exists.");
			    }
			    if (entityName != null && entityInfoDetailsRepository.entityNamecount(entityName) > 0) {
			        errorMsgs.add(entityName + " <- Entity Name already exists.");
			    }
			    if (companyHq != null && entityInfoDetailsRepository.hqNamecount(companyHq) > 0) {
			        errorMsgs.add(companyHq + " <- CompanyHq already exists.");
			    }

			    // Add to entity list if all fields are valid
			    if (entityName != null && !entityName.isEmpty() &&
			        pan != null && !pan.isEmpty() &&
			        companyHq != null && !companyHq.isEmpty()) {
			        entityInfoEntity.setGroupId(groupId);
			        entityInfoEntity.setGroupCode(groupCode);
			        entityInfoEntity.setEntityName(entityName);
			        entityInfoEntity.setEntityType(Normal);
			        entityInfoEntity.setPan(pan);
			        entityInfoEntity.setCompanyHq(companyHq);
			        entityInfoEntity.setGrossTurnover(BigDecimal.ZERO);
			        entityInfoEntity.setCreatedOn(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
			        entityInfoEntity.setModifiedOn(EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

			        entityInfoEntities.add(entityInfoEntity);
			    }
			}
			return new Pair<>(errorMsgs, entityInfoEntities);
}

	public List<EntityInfoEntity> disableElEntitlement(String groupCode) {
		return entityInfoDetailsRepository
				.findAllEntitlementEntitydetails(groupCode);
	}
}
