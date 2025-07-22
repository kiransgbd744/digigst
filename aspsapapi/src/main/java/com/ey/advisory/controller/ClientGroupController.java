package com.ey.advisory.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;
import com.ey.advisory.admin.data.entities.client.GroupLevelUserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.DmsConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GroupLevelUserPermissionsRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.docs.dto.GstinReqDto;
import com.ey.advisory.app.docs.dto.UserAttributesDto;
import com.ey.advisory.app.docs.dto.UserPermissionDto;
import com.ey.advisory.app.services.gen.DefaultClientGroupService;
import com.ey.advisory.app.services.gen.EntityDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.security.um.user.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ClientGroupController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ClientGroupController.class);

	@Autowired
	@Qualifier("DefaultClientGroupService")
	private DefaultClientGroupService defaultClientGroupService;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	@Autowired
	@Qualifier("GroupLevelUserPermissionsRepository")
	private GroupLevelUserPermissionsRepository groupLevelUserPermissionsRepository;
	
	@Autowired
	@Qualifier("DmsConfigPrmtRepository")
	private DmsConfigPrmtRepository dmsRepo;
	
	@Autowired
	private GroupService groupService;

	/**
	 * getGstins - Gets the GSTINS based on given Entity
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/gstins", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstins(@RequestBody String jsonString) {
		try {
			String groupCode = TenantContext.getTenantId();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request " + json + "  Group Code " + groupCode);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			GstinReqDto gstinReq = gson.fromJson(json, GstinReqDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstin Req is " + gstinReq + " Group Code "
						+ groupCode);
			}
			List<Long> entityIds = gstinReq.getEntityId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request EntityId List  " + entityIds
						+ " Group Code " + groupCode);
			}
			List<GstinDto> gstinsForGivenEntity = defaultClientGroupService
					.getGstinsForEntity(entityIds, groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response Gstins List  " + gstinsForGivenEntity
						+ " Group Code " + groupCode);
			}

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(gstinsForGivenEntity);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response  " + respBody + " for Group Code "
						+ groupCode);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching gstins";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			TenantContext.clearTenant();
		}
	}

	/**
	 * getGstins - Gets the GSTINS based on given User for All Entities
	 * 
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/getUserGstins", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstinsByGroupCode(
			@RequestBody String jsonString) {
		try {
			String groupCode = TenantContext.getTenantId();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request " + json + "  Group Code " + groupCode);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			GstinReqDto gstinReq = gson.fromJson(json, GstinReqDto.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstin Req is " + gstinReq + " Group Code "
						+ groupCode);
			}
			List<Long> entityIds = new ArrayList<>();
			if (groupCode.equalsIgnoreCase("sp0056")) {
				entityIds = defaultClientGroupService.getEntities(entityIds);
			} else {
				entityIds = gstinReq.getEntityId();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request EntityId List  " + entityIds
						+ " Group Code " + groupCode);
			}
			List<GstinDto> gstinsForGivenEntity = defaultClientGroupService
					.getGstinsForEntity(entityIds, groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response Gstins List  " + gstinsForGivenEntity
						+ " Group Code " + groupCode);
			}

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(gstinsForGivenEntity);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response  " + respBody + " for Group Code "
						+ groupCode);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching gstins";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			TenantContext.clearTenant();
		}
	}

	@RequestMapping(value = "/ui/entities", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEntities() {
		try {
			String groupCode = TenantContext.getTenantId();
			List<EntityDto> entitiesForGroup = defaultClientGroupService
					.getEntitiesForGroup(groupCode);
			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(entitiesForGroup);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching entities";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/groups", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllGroups() {
		try {
			List<GroupInfoEntity> entitiesForGroup = defaultClientGroupService
					.getAllGroups();
			Gson gson = new Gson();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(entitiesForGroup);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while searching groups";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = { "/ui/userDetails",
			"/itp/userDetails" }, method = RequestMethod.POST, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getUserDetails(HttpServletRequest req) {
		JsonObject jsonResp = new JsonObject();
		try {
			String grpCode = TenantContext.getTenantId();
			LOGGER.debug("Group Code {} ", grpCode);
			UserAttributesDto userAttrs = getUserAttributes();
			String optedAns = groupConfigPrmtRepository.findByGroupLevelOpted();
			String imsRoles = groupConfigPrmtRepository
					.findByGroupLevelImsRoles();
			String dmsRoles = dmsRepo
					.findByGroupLevelDmsRoles();

			UserPermissionDto permDto = new UserPermissionDto();
			List<String> permissions = new ArrayList<>();
			Set<String> imsRolesList = new HashSet<>();
			if (!Strings.isNullOrEmpty(optedAns)
					&& "A".equalsIgnoreCase(optedAns)) {
				permDto.setOpted(true);
			}
			if (!Strings.isNullOrEmpty(dmsRoles)
					&& "A".equalsIgnoreCase(dmsRoles)) {
				imsRolesList.add("GR10");
			}
			
			if (!Strings.isNullOrEmpty(imsRoles)
					&& "A".equalsIgnoreCase(imsRoles)) {
				imsRolesList.add("GR1");
				if (!Strings.isNullOrEmpty(grpCode)) {
					String optAns = onbrdConsolidated2BvsPROptionOpted(grpCode);
					String imsAutoReconOptedAns = groupConfigPrmtRepository
							.findByImsAutoReconResponseParam(grpCode);

					if (optAns != null & optAns != "") {
						if (optAns.contains(",")) {
							String[] ansArr = optAns.split(",");
							for (String ans : ansArr) {
								imsRolesList.add(ans);
							}
						} else {
							imsRolesList.add(optAns);
						}
					}
					if (!Strings.isNullOrEmpty(imsAutoReconOptedAns)
							&& "A".equalsIgnoreCase(imsAutoReconOptedAns)) {
						imsRolesList.add("GR5");
					}
				}
			}

			Long userConfgId = userCreationRepository
					.findIdByUserName(userAttrs.getUserId());

			List<GroupLevelUserPermissionsEntity> permEntity = groupLevelUserPermissionsRepository
					.findByUserIdAndIsApplicableTrueAndIsDeleteFalse(
							userConfgId);

			if (permEntity != null) {
				permissions.addAll(permEntity.stream().map(o -> o.getPermCode())
						.collect(Collectors.toList()));

			}
			permDto.setPermissions(permissions);

			permDto.setImsRoles(imsRolesList);

			userAttrs.setGroupPermissions(permDto);

			Gson gson = new GsonBuilder().serializeNulls().create();
			jsonResp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonResp.add("resp", gson.toJsonTree(userAttrs));
			return new ResponseEntity<>(jsonResp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Exception while testingAuth", ex);
			jsonResp.addProperty("msg", ex.getMessage());
			return new ResponseEntity<>(jsonResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private UserAttributesDto getUserAttributes() {
		try {
			String groupCode = TenantContext.getTenantId();
			Group group = groupService.getGroupInfo(groupCode);
			String groupName = group.getGroupName();
			if (groupCode == null) {
				groupCode = TenantContext.getTenantId();
				groupName = groupCode;
			}
			String firstName = null;
			String lastName = null;
			String phoneNo = null;
			String email = null;
			String roles = null;
			String displayName = null;
			List<String> rolesList = Arrays
					.asList(roles != null ? roles.split(",") : new String[1]);
			return new UserAttributesDto(firstName, lastName, phoneNo,
					groupCode, email, groupName, displayName, rolesList,
					SecurityContext.getUser().getUserPrincipalName());
		} catch (Exception ex) {
			LOGGER.error("Exception while occured"
					+ " while extracting the user information", ex);
			return new UserAttributesDto();
		}

	}
	
	private UserAttributesDto getUserAttributes(User user) {
		try {
			String groupCode = user.getAttribute("DigiGSTGroupCode");
			String groupName = user.getAttribute("DigiGSTGroupName");
			if (groupCode == null) {
				groupCode = TenantContext.getTenantId();
				groupName = groupCode;
			}
			String firstName = user.getAttribute("first_name");
			String lastName = user.getAttribute("last_name");
			String phoneNo = user.getAttribute("telephone");
			String email = user.getAttribute("mail");
			String roles = user.getAttribute("digigst_roles");
			String displayName = user.getAttribute("display_name");
			List<String> rolesList = Arrays
					.asList(roles != null ? roles.split(",") : new String[1]);
			return new UserAttributesDto(firstName, lastName, phoneNo,
					groupCode, email, groupName, displayName, rolesList,
					SecurityContext.getUser().getUserPrincipalName());
		} catch (Exception ex) {
			LOGGER.error("Exception while occured"
					+ " while extracting the user information", ex);
			return new UserAttributesDto();
		}
	}

	private String onbrdConsolidated2BvsPROptionOpted(String grpCode) {

		HashMap<String, String> imsReconReportPermMap = new HashMap<String, String>();

		imsReconReportPermMap.put("A", "GR2");
		imsReconReportPermMap.put("B", "GR3");
		imsReconReportPermMap.put("C", "GR4");
		imsReconReportPermMap.put("A*B", "GR2,GR3");
		imsReconReportPermMap.put("B*A", "GR3,GR2");
		imsReconReportPermMap.put("B*C", "GR3,GR4");
		imsReconReportPermMap.put("C*B", "GR4,GR3");
		imsReconReportPermMap.put("C*A", "GR4,GR2");
		imsReconReportPermMap.put("A*C", "GR2,GR4");
		imsReconReportPermMap.put("A*B*C", "GR2,GR3,GR4");

		// "Which recon report is required to be enhanced alongwith IMS
		// columns?"
		String optAns = groupConfigPrmtRepository
				.findIms2BvsPROptionOpted(grpCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("optAns " + optAns);
		}
		if (optAns != null & optAns != "") {
			return imsReconReportPermMap.get(optAns);
		}
		return "";

	}
	
	public static void main(String[] args) {
		String optAns ="GR3,GR1";
		Set<String> imsRolesList = new HashSet<>();
		
		imsRolesList.add("GR1");
		
		if (optAns != null & optAns != "") {
			if (optAns.contains(",")) {
				String[] ansArr = optAns.split(",");
				for (String ans : ansArr) {
					imsRolesList.add(ans);
				}
			} else {
				imsRolesList.add(optAns);
			}
		}
		
		System.out.println(imsRolesList);
	}

}
