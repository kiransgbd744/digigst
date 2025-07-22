/**
 * 
 */
package com.ey.advisory.admin.onboarding.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.GstinElDetailsFuncObjArrToEntityConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.EleFuncFileUploadService;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OnboardingFileTraverserFactory;
import com.ey.advisory.app.util.HeaderCheckerUtil;
import com.ey.advisory.common.GsonUtil;
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

@Service("ELDetailsFuncFileUploadHelper")
public class ELDetailsFuncFileUploadHelper {
	protected static final String[] EXPECTED_HEADERS = { "Functionality",
			"FromTaxPeriod", "ToTaxPeriod", "ELValue", "FromContractPeriod",
			"ToContractPeriod", "Renewal", "GFISID", "PACEID" };

	@Autowired
	@Qualifier("DefalutGstinELeFuncFileUploadService")
	private EleFuncFileUploadService eleFuncFileUploadService;

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("GstinElDetailsFuncObjArrToEntityConverter")
	private GstinElDetailsFuncObjArrToEntityConverter gstinElDetailsFuncObjArrToEntityConverter;

	@Autowired
	@Qualifier("HeaderCheckerUtil")
	private HeaderCheckerUtil headerCheckerUtil;

	public ResponseEntity<String> gstinEleeUpload(
			@RequestParam("file") MultipartFile[] files, String groupCode,
			Long entityId) throws Exception {
		try {
			// Disable the previous uploaded data
			List<ELEntitlementEntity> elEntitlementEntitiesList = gstinElDetailsFuncObjArrToEntityConverter
					.findEnEntitlements(groupCode, entityId);
			if (!elEntitlementEntitiesList.isEmpty()) {
				eleFuncFileUploadService
						.markEntitlementsAsDeleted(elEntitlementEntitiesList);
			}
			// Assuming that the multi-part request will have only one part.
			// Hence, directly accessing the first element.
			MultipartFile file = files[0];

			// Get the uploaded file name and a reference to the input stream of
			// the uploaded file.
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			InputStream stream = file.getInputStream();
			TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(9);

			// Add a dummy row handler that will keep counting the rows.
			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(stream, layout, rowHandler, null);
			Object[] getHeaders = rowHandler.getHeaderRow();
			List<Object[]> list = rowHandler.getFileUploadList();
			removeNullRecords(list, 9);

			Pair<Boolean, String> pair = headerCheckerUtil
					.validateHeaders(EXPECTED_HEADERS, getHeaders);
			// Now that we have the filename and the input stream, get the data
			// from the file into an object array.
			// List<Object[]> data = readFromStream(fileName, stream);
			if (pair.getValue0()) {
				Pair<List<String>, List<ELEntitlementEntity>> entities = gstinElDetailsFuncObjArrToEntityConverter
						.convertToELDetailsRecords(
								rowHandler.getFileUploadList(), groupCode,
								entityId);
				// Include all the validations (if needed)

				// Save all GSTINs (Return value of saveAll is not used
				// currently).
				List<String> errors = entities.getValue0();

				if (errors == null || errors.isEmpty()) {
					List<ELEntitlementEntity> recordsToBeInserted = entities
							.getValue1();

					if (recordsToBeInserted != null
							&& !recordsToBeInserted.isEmpty()) {
						eleFuncFileUploadService.saveAll(entities.getValue1());
						return createGstinElFunctionalitySuccessResp();
					}
				} else {
					return createFuncationalityFailureResp(errors);
				}
			} else {
				return createInvalidTemplateElFunctionalityFailureResp();
			}
		} catch (Exception e) {
			Gson gson = GsonUtil.newSAPGsonInstance();
			APIRespDto dto = new APIRespDto("Failed.", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
		return null;
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

	public ResponseEntity<String> createFuncationalityFailureResp(
			List<String> errors) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String message = "File uploaded failed, because No valid records "
				+ "found -> " + errors;
		APIRespDto dto = new APIRespDto("Failed", message);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createGstinElFunctionalitySuccessResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Success",
				"File uploaded successfully check your status in ELDetails Tab..");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createInvalidTemplateElFunctionalityFailureResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Upload Failed ", "Invalid template,"
				+ " Please upload again with correct template.");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		String msg = "Unexpected error while uploading files.";
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	// This method uses the uploaded file name to determine the appropriate
	// traverser.
	public List<Object[]> readFromStream(String fileName, InputStream stream) {

		// Get the appropriate traverser based on the file type.
		TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(9);

		// Add a dummy row handler that will keep counting the rows.
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		traverser.traverse(stream, layout, rowHandler, null);

		return rowHandler.getFileUploadList();
	}

}
