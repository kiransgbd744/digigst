/**
 * 
 */
package com.ey.advisory.admin.onboarding.controller;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.EntityUserMapping;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.entities.client.UserPermissionsEntity;
import com.ey.advisory.admin.data.repositories.client.EntityUserMappingRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.admin.data.repositories.client.UserPermissionsRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.UserCreationObjArrToEntityConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OnboardingFileTraverserFactory;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.UserCreationFileUploadService;
import com.ey.advisory.app.util.HeaderCheckerUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Sasidhar Reddy
 *
 */
@Service("UserCreationFileUploadHelper")
public class UserCreationFileUploadHelper {

	protected static final String[] EXPECTED_HEADERS = { "UserName", "FirstName",
			"LastName", "Email", "MobileNo", "UserRole" };

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserCreationFileUploadHelper.class);

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	@Autowired
	@Qualifier("UserCreationObjArrToEntityConverter")
	private UserCreationObjArrToEntityConverter converter;

	@Autowired
	@Qualifier("DefaultUserCreationFileUploadService")
	private UserCreationFileUploadService userCreationService;

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userRepo;

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("UserCreationObjArrToEntityConverter")
	private UserCreationObjArrToEntityConverter userCreationObjArrToEntityConverter;

	@Autowired
	@Qualifier("EntityUserMappingRepository")
	private EntityUserMappingRepository entityUserMappingRepository;

	@Autowired
	@Qualifier("UserPermissionsRepository")
	private UserPermissionsRepository userPermissionsRepository;

	public ResponseEntity<String> userCreation(
			@RequestParam("file") MultipartFile[] files, String groupCode,
			Long entityId) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("UserCreationFileUploadHelper usercreation begin:");
		}
		try {
			// Assuming that the multi-part request will have only one part.
			// Hence, directly accessing the first element.
			MultipartFile file = files[0];

			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());

			InputStream stream = file.getInputStream();

			TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(6);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(stream, layout, rowHandler, null);
			Object[] getHeaders = rowHandler.getHeaderRow();
			List<Object[]> list = rowHandler.getFileUploadList();
			removeNullRecords(list, 6);

			Pair<Boolean, String> pair = headerCheckerUtil
					.validateHeaders(EXPECTED_HEADERS, getHeaders);

			// Convert the object array to domain objects.
			if (pair.getValue0()) {
				Pair<List<String>, List<UserCreationEntity>> filePairs = userCreationObjArrToEntityConverter
						.convert(list, groupCode, entityId);

				List<String> errors = filePairs.getValue0();
				List<EntityUserMapping> byUserAndEntityId = new ArrayList<>();
				if (errors == null || errors.isEmpty()) {
					for (UserCreationEntity user : filePairs.getValue1()) {
						if (user.getUserName() != null) {
							List<Long> userIds = userRepo.findUserIdByUserName(
									user.getUserName(), groupCode);
							if (userIds != null && !userIds.isEmpty()) {
								// By assuming only one user exist in a list.
								user.setId(userIds.get(0));
								byUserAndEntityId = entityUserMappingRepository
										.getByUserAndEntityId(groupCode,
												entityId, userIds);
								if (byUserAndEntityId != null
										&& byUserAndEntityId.size() > 0) {
									List<Long> entityIds = byUserAndEntityId
											.stream()
											.map(mapping -> mapping
													.getEntityId())
											.collect(Collectors.toList());
									if (entityIds.containsAll(userIds)) {
										return createusernameFailureResp(
												user.getUserName());
									}
								}

							}
							userRepo.disableUserDetails(user.getId());
							user = userRepo.save(user);
						}
						User userPrinciple = SecurityContext.getUser();
						if (user != null) {
							if (byUserAndEntityId != null
									&& byUserAndEntityId.isEmpty()) {
								EntityUserMapping entityUserMapping = new EntityUserMapping();
								entityUserMapping.setUserId(user.getId());
								entityUserMapping.setEntityId(entityId);
								entityUserMapping.setGroupId(user.getGroupId());
								entityUserMapping
										.setGroupCode(user.getGroupCode());
								entityUserMapping.setIsFlag(false);
								entityUserMapping.setCreatedBy(
										System.getProperty(userPrinciple
												.getUserPrincipalName()));
								entityUserMapping
										.setCreatedOn(LocalDateTime.now());
								entityUserMapping.setUpdatedBy(
										System.getProperty(userPrinciple
												.getUserPrincipalName()));
								entityUserMapping
										.setUpdateOn(LocalDateTime.now());
								EntityUserMapping userMapping = entityUserMappingRepository
										.save(entityUserMapping);
								if (userMapping != null) {
									UserPermissionsEntity entity = new UserPermissionsEntity();
									entity.setPermCode("P1");
									entity.setEntityId(
											userMapping.getEntityId());
									entity.setUserId(userMapping.getUserId());
									entity.setApplicable(true);

									entity.setCreatedBy(userPrinciple
											.getUserPrincipalName());
									entity.setCreatedOn(
											EYDateUtil.toUTCDateTimeFromIST(
													LocalDateTime.now()));
									userPermissionsRepository.save(entity);
								}
							}
						}
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"UserCreationFileUploadHelper usercreation end: {}");
					}
					return createGstinRegSuccessResp();
				} else {
					return createGstinRegFailureResp(filePairs.getValue0());
				}
			} else {
				return createGstinRegFailureResp();
			}
		} catch (DataIntegrityViolationException e) {
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto dto = new APIRespDto("Failed",
					"Email already exists in Database.");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("resp", respBody);
			LOGGER.error("Exption Occred: {}", e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto dto = new APIRespDto("Failed",
					"File upload failed ->" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("resp", respBody);
			LOGGER.error("Exption Occred: {}", e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}
	}

	private void removeNullRecords(List<Object[]> list, int count) {
		List<Object[]> matchedNullList = new ArrayList<Object[]>();
		list.forEach(obj -> {
			List<Object> nullList = Arrays.asList(obj).stream()
					.filter(val -> val == null).collect(Collectors.toList());
			if (Arrays.asList(obj).contains(null) && nullList.size() == count) {
				matchedNullList.add(obj);
			}
		});
		list.removeAll(matchedNullList);
	}

	public ResponseEntity<String> createGstinRegSuccessResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Success",
				"File uploaded successfully check your status in UserCreation Tab. ");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createGstinRegFailureResp(List<String> info) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String message = "File uploaded failed ." + info;
		APIRespDto dto = new APIRespDto("Failed", message);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createusernameFailureResp(String userName) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String message = userName + " is already exists";
		APIRespDto dto = new APIRespDto("Failed", message);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createGstinRegFailureResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Upload Failed ", "Invalid template,"
				+ " Please upload again with correct template");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		String msg = "Unexpected error while uploading file.";
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	// This method uses the uploaded file name to determine the appropriate
	// traverser.
	public List<Object[]> readFromStream(String fileName, InputStream stream) {

		// Get the appropriate traverser based on the file type.
		TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(6);

		// Add a dummy row handler that will keep counting the rows.
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler();
		long stTime = System.currentTimeMillis();
		traverser.traverse(stream, layout, rowHandler, null);

		return rowHandler.getFileUploadList();
	}

}
