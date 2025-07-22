/**
 * 
 */
package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;
import com.ey.advisory.admin.data.repositories.client.ELEntitlementRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.GroupService;

/**
 * @author Sasidhar Reddy
 *
 */

@Service("GstinElDetailsFuncObjArrToEntityConverter")
public class GstinElDetailsFuncObjArrToEntityConverter {

	@Autowired
	@Qualifier("elEntitlementRepository")
	private ELEntitlementRepository elEntitlementRepository;
	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	private void validateUploadData(List<Object[]> rows) {

	}

	public Pair<List<String>, List<ELEntitlementEntity>> convertToELDetailsRecords(
			List<Object[]> elrGstinObjects, String groupCode, Long entityId)
			throws ParseException {
		List<String> errorMsgs = new ArrayList<String>();
		List<ELEntitlementEntity> elEntitlementEntities = new ArrayList<ELEntitlementEntity>();
		for (Object[] elentitlementinfo : elrGstinObjects) {
			Boolean isError = false;
			ELEntitlementEntity elEntitlementEntity = new ELEntitlementEntity();

			String functionalityCode = (elentitlementinfo[0] != null)
					? elentitlementinfo[0].toString().trim() : null;
			/*
			 * String functionalityCode = String .valueOf(elentitlementinfo[0])
			 * == "null" ? null : String.valueOf(elentitlementinfo[0]);
			 */
			// if (functionalityCode.equals("null")
			// || functionalityCode.equals("")) {
			// errorMsgs = functionalityCode
			// + " : functionalityCode is mandatory and should not be empty";
			// return new Pair<String, List<ELEntitlementEntity>>(errorMsgs,
			// elEntitlementEntities);
			// }
			String fromTaxPeriod = String
					.valueOf(elentitlementinfo[1]) == "null" ? null
							: String.valueOf(elentitlementinfo[1]);
			String toTaxPeriod = String.valueOf(elentitlementinfo[2]) == "null"
					? null : String.valueOf(elentitlementinfo[2]);
			String elValue = String.valueOf(elentitlementinfo[3]) == "null"
					? null : String.valueOf(elentitlementinfo[3]);
			SimpleDateFormat uiDateFormat = new SimpleDateFormat("MMyyyy");
			SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(uiDateFormat.parse(fromTaxPeriod));
			String fromTaxPeriod1 = dbDateFormat.format(calendar2.getTime());
			Date startDate1 = new SimpleDateFormat("yyyy-MM-dd")
					.parse(fromTaxPeriod1.toString());
			Calendar calendar3 = Calendar.getInstance();
			calendar3.setTime(uiDateFormat.parse(toTaxPeriod));
			String toTaxPeriod1 = dbDateFormat.format(calendar3.getTime());
			Date endDate1 = new SimpleDateFormat("yyyy-MM-dd")
					.parse(toTaxPeriod1.toString());
			if (startDate1.after(endDate1)) {
				errorMsgs.add(toTaxPeriod
						+ " Totaxperiod should be after fromtaxperiod.");
				isError = true;
			}
			String contractStartPeriod = String
					.valueOf(elentitlementinfo[4]) == "null" ? null
							: String.valueOf(elentitlementinfo[4]);
			String contractEndPeriod = String
					.valueOf(elentitlementinfo[5]) == "null" ? null
							: String.valueOf(elentitlementinfo[5]);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(uiDateFormat.parse(contractStartPeriod));
			String contractStartPeriod1 = dbDateFormat
					.format(calendar.getTime());
			Date startDate = new SimpleDateFormat("yyyy-MM-dd")
					.parse(contractStartPeriod1.toString());
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(uiDateFormat.parse(contractEndPeriod));
			String contractEndPeriod1 = dbDateFormat
					.format(calendar1.getTime());
			Date endDate = new SimpleDateFormat("yyyy-MM-dd")
					.parse(contractEndPeriod1.toString());
			if (startDate.after(endDate)) {
				errorMsgs.add(contractEndPeriod
						+ " End period should be after start period.");
				isError = true;
			}
			String renewal = String.valueOf(elentitlementinfo[6]);
			// LocalDate renewal1 = LocalDate.parse(renewal, formatter);
			// if (renewal1.compareTo(date) <= 0) {
			// // Should be after 2017-07-01
			// errorMsgs = renewal1 + "Should be after 2017-07-01.";
			// isError = true;
			// }
			//
			// if (renewal1.compareTo(contractStartPeriod1) <= 0) {
			// // Should be after start date
			// errorMsgs = renewal1 + "Should be after start date.";
			// isError = true;
			// }
			String gfisId = String.valueOf(elentitlementinfo[7]) == "null"
					? null : String.valueOf(elentitlementinfo[7]);
			String paceId = String.valueOf(elentitlementinfo[8]) == "null"
					? null : String.valueOf(elentitlementinfo[8]);
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			elEntitlementEntity.setEntityId(entityId);
			if (functionalityCode != null && fromTaxPeriod != null
					&& toTaxPeriod != null && elValue != null
					&& contractStartPeriod != null && contractEndPeriod != null
					&& renewal != null && gfisId != null && paceId != null) {
				Long groupId = groupInfoDetailsRepository
						.findByGroupId(groupCode);
				elEntitlementEntity.setGroupCode(groupCode);
				elEntitlementEntity.setGroupId(groupId);
				elEntitlementEntity.setFunctionalityId(
						elEntitlementEntity.getFunctionalityId());
				elEntitlementEntity.setFunctionalityCode(functionalityCode);
				elEntitlementEntity.setFromTaxPeriod(fromTaxPeriod.toString());
				elEntitlementEntity.setToTaxPeriod(toTaxPeriod.toString());
				elEntitlementEntity.setElValue(elValue);
				elEntitlementEntity
						.setContractStartPeriod(contractStartPeriod.toString());
				elEntitlementEntity
						.setContractEndPeriod(contractEndPeriod.toString());
				elEntitlementEntity.setRenewal(renewal);
				elEntitlementEntity.setGfisId(gfisId);
				elEntitlementEntity.setPaceId(paceId);
				elEntitlementEntity.setIsDelete(false);
				elEntitlementEntity.setCreatedBy(userName);
				elEntitlementEntity.setCreatedOn(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
				elEntitlementEntity.setModifiedBy(userName);
				elEntitlementEntity.setModifiedOn(
						EYDateUtil.toLocalDateTimeFromUTC(LocalDate.now()));
				elEntitlementEntities.add(elEntitlementEntity);
			}
			if (isError) {
				continue;
			}
		}
		return new Pair<List<String>, List<ELEntitlementEntity>>(errorMsgs,
				elEntitlementEntities);

	}

	public List<ELEntitlementEntity> findEnEntitlements(String groupCode,
			Long entityId) {
		return elEntitlementRepository.findAllEntitlementdetails(groupCode,
				entityId);
	}
}
