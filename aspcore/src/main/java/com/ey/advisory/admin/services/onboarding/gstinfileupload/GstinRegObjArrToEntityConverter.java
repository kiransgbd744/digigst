package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GstinElRegRepository;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.repositories.client.GstnAPIGstinConfigRepository;
import com.google.common.collect.ImmutableList;

@Service("GstinRegObjArrToEntityConverter")
public class GstinRegObjArrToEntityConverter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstinRegObjArrToEntityConverter.class);
	private static final List<String> REG_TYPE = ImmutableList.of("REGULAR",
			"SEZD", "SEZU", "ISD", "COMPOSITION", "UIN", "GOVT DEPT", "NRI",
			"TDS", "TCS", "OIDAR");

	private static final List<String> REG_TYPE1 = ImmutableList.of("REGULAR",
			"SEZD", "SEZU", "ISD", "COMPOSITION");
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupInfoDetailsRepository;

	@Autowired
	@Qualifier("GstinElRegRepository")
	private GstinElRegRepository gstinElRegRepository;
	
	@Autowired
	@Qualifier("GstnAPIGstinConfigRepository")
	private GstnAPIGstinConfigRepository gstnAPIGstinConfigRepo;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;
	

	public Pair<List<String>, List<GSTNDetailEntity>> convert(
			List<Object[]> elrGstinObjects, String groupCode, Long entityId) {
		List<String> errorMsgs = new ArrayList<>();
		List<GSTNDetailEntity> gstnDetailEntities = new ArrayList<>();
		List<GstnAPIGstinConfig> gstnAPIGstinEntities = new ArrayList<>();

		Long groupId = groupInfoDetailsRepository.findByGroupId(groupCode);

		for (Object[] gstnDetailInfo : elrGstinObjects) {
			GSTNDetailEntity gstnDetailEntity = new GSTNDetailEntity();
			GstnAPIGstinConfig gstnAPIGstinEntity = new GstnAPIGstinConfig();


			LocalDateTime curDate = LocalDateTime.now();
			String gstinTrim = String.valueOf(gstnDetailInfo[0]);
			String gstin = gstinTrim.trim();
			if (gstinTrim == null || gstinTrim.trim().isEmpty()
					|| gstinTrim.equals("null")) {
				errorMsgs.add(" Gstin is mandatory.");
				errorMsgs.add(" Gstin should not be empty.");
				continue;
			} else if (gstinTrim != null && !gstinTrim.isEmpty()
					&& gstin.length() == 15) {
				String subStringGstn = gstin.substring(2, 12);
				EntityInfoEntity entityPan = entityInfoDetailsRepository
						.findEntityByEntityId(entityId, subStringGstn);
				/*
				 * if (entityPan == null) { errorMsgs.add(gstin +
				 * " <- For this GSTIN <-" + subStringGstn +
				 * " <- PAN was not provide with onboarded entity."); continue;
				 * }
				 */
				int gstinCount = gstinElRegRepository.gstincount(gstin);
				if (gstinCount > 0) {
					errorMsgs.add(gstin
							+ " <- This GSTIN is already exists in Database.");
					continue;
				}

			}

			String registrationTypeTrim = (gstnDetailInfo[1] != null
					&& !gstnDetailInfo[1].toString().trim().isEmpty())
							? String.valueOf(gstnDetailInfo[1]).trim() : null;
			if (registrationTypeTrim == null) {
				registrationTypeTrim = "";
			}
			String registrationType = registrationTypeTrim.trim();
			if (!REG_TYPE.contains(trimAndConvToUpperCase(registrationType))) {
				errorMsgs.add(registrationType + " <- Invalid Regtype.");
				continue;
			}
			String registrationName = gstnDetailInfo[2] != null
					? String.valueOf(gstnDetailInfo[2]) : null;

			String regex = "^[0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z]"
					+ "[0-9][0-9][0-9][0-9][a-zA-Z][1-9A-Za-z][Zz1-9A-Ja-j][0-9a-zA-Z]$";
			String regex1 = "^[0-9][0-9][0-9][0-9][A-Z][A-Z][A-Z][0-9][0-9]"
					+ "[0-9][0-9][0-9][UO][N][A-Z0-9]$";
			String regex2 = "^[0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z]"
					+ "[0-9][0-9][0-9][0-9][0-9][a-zA-Z][0-9][Z][0-9]$";
			String regex3 = "^[0-9][0-9][0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z]"
					+ "[0-9][0-9][0-9][0-9][0-9][N][R][0-9a-zA-Z]$";
			String regex4 = "^[0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z]"
					+ "[a-zA-Z0-9][0-9][0-9][0-9][0-9][a-zA-Z][1-9A-Za-z][D][0-9a-zA-Z]$";
			String regex5 = "^[0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z]"
					+ "[a-zA-Z][0-9][0-9][0-9][0-9][a-zA-Z][1-9A-Za-z][C][0-9a-zA-Z]$";
			String regex6 = "^[9][9][0-9][0-9][a-zA-Z][a-zA-Z][a-zA-Z]"
					+ "[0-9][0-9][0-9][0-9][0-9][O][S][0-9a-zA-Z]$";

			Pattern pattern = Pattern.compile(regex);
			Pattern pattern1 = Pattern.compile(regex1);
			Pattern pattern2 = Pattern.compile(regex2);
			Pattern pattern3 = Pattern.compile(regex3);
			// Pattern pattern4 = Pattern.compile(regex4);
			Pattern pattern5 = Pattern.compile(regex5);
			Pattern pattern6 = Pattern.compile(regex6);

			Matcher matcher = pattern.matcher(gstin);
			Matcher matcher1 = pattern1.matcher(gstin);
			Matcher matcher2 = pattern2.matcher(gstin);
			Matcher matcher3 = pattern3.matcher(gstin);
			// Matcher matcher4 = pattern4.matcher(gstin);
			Matcher matcher5 = pattern5.matcher(gstin);
			Matcher matcher6 = pattern6.matcher(gstin);

			if (REG_TYPE1.contains(trimAndConvToUpperCase(registrationType))
					&& !matcher.matches()) {
				errorMsgs.add(registrationType
						+ " <- Invalid GSTIN for given regtype ."
						+ " * Do Remaining based on given example "
						+ " : 12ABCDS1234AW46 ");
				continue;
			}
			if (("UIN".equalsIgnoreCase(registrationType)
					&& !matcher1.matches())) {
				errorMsgs.add(registrationType
						+ " - Invalid GSTIN for given regtype ."
						+ " * 13th character should be 'U' or 'O' is mandatory."
						+ " * 14th character should be 'N' is mandatory."
						+ " * Do Remaining based on given example "
						+ " : 1234ABC56789UNA ");
				continue;
			}
			if (("GOVT DEPT".equalsIgnoreCase(registrationType)
					&& !matcher2.matches())) {
				errorMsgs.add(registrationType
						+ " - Invalid GSTIN for given regtype ."
						+ " * 14th character should be 'Z' is mandatory. "
						+ " * Do Remaining based on given example "
						+ " : 12ABCD34567E8Z9 ");
				continue;
			}
			if (("NRI".equalsIgnoreCase(registrationType)
					&& !matcher3.matches())) {
				errorMsgs.add(registrationType
						+ " - Invalid GSTIN for given regtype ."
						+ " * 13th and 14th character should be 'NR' is mandatory. "
						+ " * Do Remaining based on given example "
						+ " : 1234ABC56789NRD ");
				continue;
			}
			/*
			 * if (("TDS".equalsIgnoreCase(registrationType) &&
			 * !matcher4.matches())) { errorMsgs.add(registrationType +
			 * " - Invalid GSTIN for given regtype ." +
			 * " * 14th character should be 'D' is mandatory. " +
			 * " * Do Remaining based on given example " +
			 * " : 12ABCDE3456A7D8 "); continue; }
			 */
			if (("TCS".equalsIgnoreCase(registrationType)
					&& !matcher5.matches())) {
				errorMsgs.add(registrationType
						+ " - Invalid GSTIN for given regtype ."
						+ " * 14th character should be 'C' is mandatory. "
						+ " * Do Remaining based on given example "
						+ " : 12ABCDE3456F7C8 ");
				continue;
			}
			if (("OIDAR".equalsIgnoreCase(registrationType)
					&& !matcher6.matches())) {
				errorMsgs.add(registrationType + " Invalid GSTIN "
						+ " Unable to add this given regtype ."
						+ " * First two digits 99 is mandatory."
						+ " * 13th and 14th character should be 'OS' is mandatory. "
						+ " * Do Remaining based on given example "
						+ " : 9912ABC12345OS6");
				continue;
			}
			String gstnUsername = gstnDetailInfo[3] != null
					? String.valueOf(gstnDetailInfo[3]) : null;
			LocalDate regdate = LocalDate.of(2017, 07, 01);
			LocalDate localeffectiveDate = DateUtil
					.parseObjToDate(gstnDetailInfo[4]);
			if (localeffectiveDate == null) {
			    errorMsgs.add(" Effective Date of Registration is mandatory.");
			    continue;
			}
			if (localeffectiveDate.compareTo(regdate) < 0) {
				errorMsgs.add(localeffectiveDate + "  On or after 2017-07-01.");
				continue;
			}

			String registeredEmailTrim = gstnDetailInfo[5] != null
					? String.valueOf(gstnDetailInfo[5]) : null;
			if (registeredEmailTrim == null) {
				registeredEmailTrim = "";
			}
			String registeredEmail = registeredEmailTrim.trim();
			BigDecimal mobileDecimalFormat = BigDecimal.ZERO;
			if (gstnDetailInfo[6] != null) {
				String mobileDecimalFormatStr = (String
						.valueOf(gstnDetailInfo[6])).trim();
				mobileDecimalFormat = new BigDecimal(mobileDecimalFormatStr);
			}
			String registeredMobileNo = gstnDetailInfo[6] != null
					? String.valueOf(mobileDecimalFormat.longValue()) : null;
			String primaryAuthEmail = gstnDetailInfo[7] != null
					? String.valueOf(gstnDetailInfo[7]) : null;
			String secondaryAuthEmail = gstnDetailInfo[8] != null
					? String.valueOf(gstnDetailInfo[8]) : null;
			if (primaryAuthEmail != null && !primaryAuthEmail.isEmpty()) {
				if (secondaryAuthEmail != null
						&& !secondaryAuthEmail.isEmpty()) {
					if (secondaryAuthEmail.equalsIgnoreCase(primaryAuthEmail)) {
						errorMsgs.add(" Secondary Authorized "
								+ "Signatory cannot be same as Primary Authorized signatory.");
					}
				}
			}
			String primaryContactEmail = gstnDetailInfo[9] != null
					? String.valueOf(gstnDetailInfo[9]) : null;
			String secondaryContactEmail = gstnDetailInfo[10] != null
					? String.valueOf(gstnDetailInfo[10]) : null;
			if (primaryContactEmail != null && !primaryContactEmail.isEmpty()) {
				if (secondaryContactEmail != null
						&& !secondaryContactEmail.isEmpty()) {
					if (primaryContactEmail
							.equalsIgnoreCase(secondaryContactEmail)) {
						errorMsgs.add(" Secondary ContactEmail "
								+ "Signatory cannot be same as Primary ContactEmail "
								+ ".");
					}
				}
			}
			Object obj = exponentialAndZeroCheck(gstnDetailInfo[11]);

			BigDecimal turnover = gstnDetailInfo[12] != null
					? evaluateTurnover(gstnDetailInfo[12]) : null;
			String address1 = gstnDetailInfo[13] != null
					? String.valueOf(gstnDetailInfo[13]) : null;
			String address2 = gstnDetailInfo[14] != null
					? String.valueOf(gstnDetailInfo[14]) : null;
			String address3 = gstnDetailInfo[15] != null
					? String.valueOf(gstnDetailInfo[15]) : null;
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			if (gstnUsername == null || gstnUsername.isEmpty()) {
				errorMsgs.add(" GstnUsername is mandatory.");
				continue;
			}
			if (localeffectiveDate == null) {
			    errorMsgs.add("localeffectiveDate is mandatory.");
			    continue;
			}

			if (registeredEmail == null || registeredEmail.isEmpty()
					|| registeredEmail.equals("")) {
				errorMsgs.add("  registeredEmail is mandatory.");
				continue;
			}

			if (primaryAuthEmail == null || primaryAuthEmail.isEmpty()) {
				errorMsgs.add("  primaryAuthEmail is mandatory.");
				continue;
			}
			if (primaryContactEmail == null || primaryContactEmail.isEmpty()) {
				errorMsgs.add("  primaryContactEmail is mandatory.");
				continue;
			}
			gstnDetailEntity.setEntityId(entityId);
			gstnDetailEntity.setGroupCode(groupCode);
			gstnDetailEntity.setGroupId(groupId);
			gstnDetailEntity.setGstin(gstin);
			gstnDetailEntity
					.setRegistrationType(registrationType.toUpperCase());
			gstnDetailEntity.setRegisteredName(registrationName);

			gstnDetailEntity.setGstnUsername(gstnUsername);
			gstnDetailEntity.setRegDate(localeffectiveDate);
			gstnDetailEntity.setRegisteredEmail(registeredEmail);
			gstnDetailEntity.setRegisteredMobileNo(registeredMobileNo);
			gstnDetailEntity.setPrimaryAuthEmail(primaryAuthEmail);
			if (secondaryAuthEmail == null) {
				gstnDetailEntity.setSecondaryAuthEmail("");
			} else {
				gstnDetailEntity.setSecondaryAuthEmail(secondaryAuthEmail);
			}

			gstnDetailEntity.setPrimaryContactEmail(primaryContactEmail);
			if (secondaryContactEmail == null) {
				gstnDetailEntity.setSecondaryContactEmail("");
			} else {
				gstnDetailEntity
						.setSecondaryContactEmail(secondaryContactEmail);
			}
			gstnDetailEntity
					.setBankAccNum(obj != null ? String.valueOf(obj) : null);
			gstnDetailEntity.setTurnover(turnover);
			gstnDetailEntity.setIsDelete(false);
			String statecode = gstin.substring(0, 2);
			gstnDetailEntity.setStateCode(statecode);
			gstnDetailEntity.setCreatedBy(userName);
			gstnDetailEntity.setCreatedOn(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
			gstnDetailEntity.setModifiedBy(userName);
			gstnDetailEntity.setModifiedOn(
					EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));
			gstnDetailEntity.setAddress1(address1);
			gstnDetailEntity.setAddress2(address2);
			gstnDetailEntity.setAddress3(address3);
			gstnDetailEntities.add(gstnDetailEntity);
			
			//about to make entry in config table as well(US 146675)
			
			
			gstnAPIGstinEntity.setGstin(gstin);
			gstnAPIGstinEntity.setGroupCode(groupCode);
			gstnAPIGstinEntity.setGstinUserName(gstnUsername);
			gstnAPIGstinEntity.setMobileNo(registeredMobileNo);
			gstnAPIGstinEntity.setCreatedDate(
					EYDateUtil.toDate(LocalDateTime.now()));
			gstnAPIGstinEntity.setUpdatedDate(
					EYDateUtil.toDate(LocalDateTime.now()));
			gstnAPIGstinEntity.setEmailId(registeredEmail);
			
			gstnAPIGstinEntities.add(gstnAPIGstinEntity);
			
			
			
		}
		gstnAPIGstinConfigRepo.saveAll(gstnAPIGstinEntities);
		gstNDetailRepository.saveAll(gstnDetailEntities);

		return new Pair<>(errorMsgs, gstnDetailEntities);
	}

	private BigDecimal evaluateTurnover(Object obj) {
		BigDecimal turnover = BigDecimal.ZERO;
		if (obj instanceof Integer) {
			turnover = new BigDecimal((Integer) obj);
		} else if (obj instanceof Double) {
			turnover = new BigDecimal((Double) obj);
		}

		return turnover;
	}

	private static Object exponentialAndZeroCheck(Object obj) {
		Object object = obj != null ? obj.toString().trim() : null;
		if (obj != null && !obj.toString().trim().isEmpty()) {
			if (obj.toString().contains(".") && obj.toString()
					.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)")) {

				BigDecimal docNoDecimalFormat = BigDecimal.ZERO;
				docNoDecimalFormat = new BigDecimal(obj.toString());
				Long supplierPhoneLong = docNoDecimalFormat.longValue();
				object = String.valueOf(supplierPhoneLong);
			}
		}
		return object;
	}
}
