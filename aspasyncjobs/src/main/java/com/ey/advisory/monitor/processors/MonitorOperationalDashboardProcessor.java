package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.OperationalPartnerDashboardMasterEntity;
import com.ey.advisory.admin.data.repositories.master.OperationDashboardPartnerRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author sakshi.jain, for Operational Partners  Dasboard
 */
@Slf4j
@Component("MonitorOperationalDashboardProcessor")

public class MonitorOperationalDashboardProcessor
		extends DefaultMultiTenantTaskProcessor {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private OperationDashboardPartnerRepository operationDashRepo;
	
	@Autowired
	@Qualifier("GroupRepository")
	private GroupRepository grpRepo;

	
	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	
	 
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Inside MonitorOperationalDashboardProcessor for groupCode {}",
						group.getGroupCode());
			}

			List<OperationalPartnerDashboardMasterEntity> groupDashboardENtity = fetchClientsInfo(
					group.getGroupCode());
			
			String groupName = null;
			Group grp = grpRepo.findByGroupCodeAndIsActiveTrue(group.getGroupCode());
			if(grp!=null)
			{
				groupName = grp.getGroupName();
			}

			groupDashboardENtity.get(0).setGroupName(groupName);
			
			OperationalPartnerDashboardMasterEntity activeEntry = operationDashRepo
					.findActiveGroupCode(group.getGroupCode());
			if (activeEntry != null) {
				LOGGER.debug(" INSIDE if ");
				if (activeEntry.getEinvDttm() != null
						&& activeEntry.getEwbDttm() != null) {

					String bcapiTimeSTamp = LocalDateTime.now().toString();
					/*callBcapiGroup(group.getGroupCode(),
							"OPD");
					*/groupDashboardENtity.get(0)
							.setEinvDttm((LocalDateTime.parse(bcapiTimeSTamp)));
					  groupDashboardENtity.get(0)
							.setEwbDttm((LocalDateTime.parse(bcapiTimeSTamp)));
				} else {

					groupDashboardENtity.get(0)
							.setEinvDttm((activeEntry != null && activeEntry.getEinvDttm() != null)
									? activeEntry.getEinvDttm() : null);
					groupDashboardENtity.get(0).setEwbDttm(
							(activeEntry != null && activeEntry.getEwbDttm() != null) ? 
									activeEntry.getEwbDttm()
									: null);
				}

			} else {
				LOGGER.debug(" INSIDE ELSE BLOCK");
				String bcapiTimeSTamp = LocalDateTime.now().toString();
				/*callBcapiGroup(group.getGroupCode(),
						"OPD");
				*/groupDashboardENtity.get(0).setEinvDttm((LocalDateTime.parse(bcapiTimeSTamp)));
				groupDashboardENtity.get(0).setEwbDttm((LocalDateTime.parse(bcapiTimeSTamp)));

			}
			operationDashRepo.softDeleteId(group.getGroupCode());
			operationDashRepo.saveAll(groupDashboardENtity);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" Ended MonitorOperationalDashboardProcessor for groupCode {}",
						group.getGroupCode());
			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			LOGGER.error(" error ocured in monitor operation dashbaord ",ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	private List<OperationalPartnerDashboardMasterEntity> fetchClientsInfo(
			String groupCode) {
		TenantContext.setTenantId(groupCode);

		String queryStr = createQueryString();
		Query q = entityManager.createNativeQuery(queryStr);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<OperationalPartnerDashboardMasterEntity> entityList = list.stream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Inside OperationalPartnerDashboardMasterEntity entityList {}",
					entityList);
		}

		return entityList;

	}

private OperationalPartnerDashboardMasterEntity convert(Object[] arr) {
		
		OperationalPartnerDashboardMasterEntity entity = new OperationalPartnerDashboardMasterEntity();
		entity.setGroupName((arr[0] != null) ? arr[0].toString() : null);
		entity.setDigigstCreatedOn((arr[1] != null)
				? LocalDateTime.parse(arr[1].toString(),formatter) : null);
		entity.setGroupCode((arr[2] != null) ? arr[2].toString() : null);
		entity.setApp((arr[3] != null) ? arr[3].toString() : null);
		entity.setEntityCount(
				(arr[4] != null) ? Long.valueOf(arr[4].toString()) : null);
		entity.setGstinCount(
				(arr[5] != null) ? Long.valueOf(arr[5].toString()) : null);
		entity.setUsers(
				(arr[6] != null) ? Long.valueOf(arr[6].toString()) : null);
		entity.setRetDttm((arr[7] != null)
				? LocalDateTime.parse(arr[7].toString(),formatter) : null);
		entity.setAimDttm((arr[8] != null)
				? LocalDateTime.parse(arr[8].toString(),formatter) : null);
		entity.setImsDttm((arr[9] != null)
				? LocalDateTime.parse(arr[9].toString(),formatter) : null);
		entity.setInwardJsnDttm((arr[10] != null)
				? LocalDateTime.parse(arr[10].toString(),formatter) : null);

		entity.setCreatedOn(LocalDateTime.now());
		entity.setCreatedBy("SYSTEM");
		entity.setDelete(false);
		return entity;
	}
	private String createQueryString() {
		return  " SELECT  "
				 + "     G.GROUP_NAME AS GROUPNAME, "
				 + "     TO_VARCHAR(MAX(G.CREATED_DATE),'DD-MM-YYYY HH24:MI:SS') AS CREATEDDATE, "
				 + "     G.GROUP_CODE AS GROUPCODE, "
				 + "     'SAP' AS APP, "
				 + "     COUNT(DISTINCT E.ID) AS ENTITYCOUNT, "
				 + "     COUNT(DISTINCT GI.ID) AS GSTINCOUNT, "
				 + "     (SELECT COUNT(*) FROM CLIENT_USERINFO) AS USERS, "
				 + "     IFNULL(TO_VARCHAR(MIN(GI.CREATED_ON),'DD-MM-YYYY HH24:MI:SS'),NULL) AS RET_DTTM, "
				 + "     IFNULL(TO_VARCHAR(MIN(A.CREATED_ON),'DD-MM-YYYY HH24:MI:SS'),NULL) AS AIM, "
				 + "     IFNULL(TO_VARCHAR(MIN(IMS.CREATED_ON),'DD-MM-YYYY HH24:MI:SS'),NULL) AS IMS_DTTM, "
				 + "     IFNULL(TO_VARCHAR(MIN(INV.CREATED_ON),'DD-MM-YYYY HH24:MI:SS'),NULL) AS INWARD_JSN_DTTM "
				 + " FROM GROUP_INFO G "
				 + " INNER JOIN ENTITY_INFO E ON G.ID = E.GROUP_ID "
				 + " INNER JOIN GSTIN_INFO GI ON E.ID = GI.ENTITY_ID "
				 + " INNER JOIN ( "
				 + "     SELECT P.ENTITY_ID, P.ANSWER, MIN(P.CREATED_ON) AS CREATED_ON "
				 + "     FROM CONFG_QUESTION Q  "
				 + "     INNER JOIN CONFG_ANSWER A ON Q.ID=A.CONFG_QUESTION_ID "
				 + "     INNER JOIN ENTITY_CONFG_PRMTR P ON Q.ID=P.CONFG_QUESTION_ID "
				 + "     WHERE Q.QUESTION_CODE='I27' "
				 + "     AND Q.QUESTION_DESCRIPTION LIKE 'Do you want to enable Auto recon (2AvsPR) under AIM? (This has to be kept%' "
				 + " 	AND P.ANSWER = 'A' "
				 + "     GROUP BY P.ENTITY_ID, P.ANSWER "
				 + "     ) A ON E.ID = A.ENTITY_ID "
				 + " INNER JOIN ( "
				 + "     SELECT P.GROUP_CODE, P.ANSWER, MIN(P.CREATED_ON) AS CREATED_ON "
				 + "     FROM CONFG_QUESTION Q  "
				 + "     INNER JOIN CONFG_ANSWER A ON Q.ID=A.CONFG_QUESTION_ID "
				 + "     INNER JOIN GROUP_CONFG_PRMTR P ON Q.ID=P.CONFG_QUESTION_ID  "
				 + "     WHERE Q.QUESTION_CODE='G36' "
				 + "     AND Q.QUESTION_DESCRIPTION LIKE 'Whether IMS functionality is required?' "
				 + " 	AND P.ANSWER = 'A' "
				 + "     GROUP BY P.GROUP_CODE, P.ANSWER "
				 + "     ) IMS ON G.GROUP_CODE = IMS.GROUP_CODE "
				 + " INNER JOIN ( "
				 + "     SELECT P.ENTITY_ID, P.ANSWER, MIN(P.CREATED_ON) AS CREATED_ON "
				 + "     FROM CONFG_QUESTION Q  "
				 + "     INNER JOIN CONFG_ANSWER A ON Q.ID=A.CONFG_QUESTION_ID "
				 + "     INNER JOIN ENTITY_CONFG_PRMTR P ON Q.ID=P.CONFG_QUESTION_ID "
				 + "     WHERE Q.QUESTION_CODE='I51' "
				 + "     AND Q.QUESTION_DESCRIPTION LIKE 'Do you want to enable Inward E-invoice functionality?' "
				 + " 	AND P.ANSWER = 'A' "
				 + "     GROUP BY P.ENTITY_ID, P.ANSWER "
				 + "     ) INV ON E.ID = INV.ENTITY_ID "
				 + " GROUP BY G.GROUP_NAME,G.GROUP_CODE; ";
	}

	private String callBcapiGroup(String groupCode, String apiIdentifer) {
		String apiStatus = null;
		String url = "abc";
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request groupCode {}", groupCode);
			}
		/*	if (url != null && !url.isEmpty()) {
				String msg = "Bcapi save url is not configured";
				LOGGER.error(msg);
				throw new AppException(msg);
			}*/

			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("groupCode",groupCode);
			JsonObject jsonObj = new JsonObject();
			jsonObj.addProperty("groupCode", groupCode);
			StringEntity entity = new StringEntity(jsonObj.toString());
			httpPost.setEntity(entity);

			HttpResponse resp = httpClient.execute(httpPost);

			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("BCAPI Return Response  {}", apiResp);
			}
			String bcapiTime = null;
			if (httpStatusCd == 200) {
				apiStatus = "SUCCESS";
				JsonObject respObj = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				String status = respObj.has("hdr") ? respObj.get("hdr")
						.getAsJsonObject().get("status").getAsString() : null;

				if ("S".equalsIgnoreCase(status)) {
					bcapiTime = respObj.has("resp")
							? respObj.get("resp").getAsString() : null;
					return bcapiTime;
				} else
					return bcapiTime;

			} else {
				apiStatus = "FAILED";
				return null;
			}
		} catch (Exception ex) {
			LOGGER.error("callBcapiGroup - " + "Exception while calling BCAPI ",
					ex);
			throw new AppException(ex);
			
		}

	}
	
	/*public static void main(String[] args) {
		try {
			Object[] dataObject = {
			        "y8nvcqp4f9", "08-01-2020 07:14:41", // Created Date
			        "y8nvcqp4f9",                                   // Unique ID
			        "SAP",                                          // System
			        6,                                              // Value1
			        220,                                            // Value2
			        97,                                             // Value3
	"30-01-2020 11:17:53", // Date1
	"09-07-2021 07:26:31", // Date2
	"08-10-2024 04:21:35", // Date3
	"04-12-2023 07:35:24"  // Date4
			    };
			
			OperationalPartnerDashboardMasterEntity entity = new OperationalPartnerDashboardMasterEntity();
			entity= convert(dataObject);
			List<OperationalPartnerDashboardMasterEntity> groupDashboardENtity = new ArrayList<>();
			groupDashboardENtity.add(entity);
			
			String bcapiTimeSTamp = LocalDateTime.now().toString();
			callBcapiGroup(group.getGroupCode(),
					"OPD");
			groupDashboardENtity.get(0).setEinvDttm((LocalDateTime.parse(bcapiTimeSTamp)));
			groupDashboardENtity.get(0).setEwbDttm((LocalDateTime.parse(bcapiTimeSTamp)));

			System.out.println(groupDashboardENtity);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
