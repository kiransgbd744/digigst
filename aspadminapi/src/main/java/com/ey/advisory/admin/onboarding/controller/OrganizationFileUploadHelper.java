/**
 * 
 */
package com.ey.advisory.admin.onboarding.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.OrganizationObjArrToEntityConverter;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OnboardingFileTraverserFactory;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.docs.OrganizationFileUploadService;
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

@Service("OrganizationFileUploadHelper")
public class OrganizationFileUploadHelper {

	@Autowired
	@Qualifier("DefaultOrganizationFileUploadService")
	private OrganizationFileUploadService orgService;

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("OrganizationObjArrToEntityConverter")
	private OrganizationObjArrToEntityConverter organizationObjArrToEntityConverter;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstinFileUploadController.class);

	public void orgUpload(@RequestParam("file") MultipartFile[] files,
			String groupCode, Long entityId) throws Exception {
		// Assuming that the multi-part request will have only one part.
		// Hence, directly accessing the first element.
		MultipartFile file = files[0];

		// Get the uploaded file name and a reference to the input stream of
		// the uploaded file.
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		InputStream stream = file.getInputStream();
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		// Now that we have the filename and the input stream, get the data
		// from the file into an object array.
		Pair<List<Object[]>, Object[]> streamData = readFromStream(fileName,
				stream);
		List<Object[]> data = streamData.getValue0(); // Get the file data.
		Object[] header = streamData.getValue1(); // Get the header.
		List<Object[]> list = rowHandler.getFileUploadList();
		removeNullRecords(list, 16);

		// Include all the validations (if needed)
		// Convert the object array to domain objects.
		Set<EntityAtValueEntity> setEntityValues = organizationObjArrToEntityConverter
				.convert(data, header, groupCode, entityId);

		if (setEntityValues != null && !setEntityValues.isEmpty()) {
			List<EntityAtValueEntity> attributes = new ArrayList<>();
			attributes.addAll(setEntityValues);
			// Save all GSTINs (Return value of saveAll is not used currently).
			orgService.saveAll(attributes);
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
				"File uploaded successfully check your status in Organisation Tab.");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> createGstinRegFailureResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto(" Upload Failed ", "Invalid template,"
				+ " Please upload again with correct template.");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		String msg = "Unexpected error while uploading files";
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	// This method uses the uploaded file name to determine the appropriate
	// traverser.
	public Pair<List<Object[]>, Object[]> readFromStream(String fileName,
			InputStream stream) {

		// Get the appropriate traverser based on the file type.
		TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(16);

		// Add a dummy row handler that will keep counting the rows.
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler();
		long stTime = System.currentTimeMillis();
		traverser.traverse(stream, layout, rowHandler, null);

		// Get the header row for validation.
		Object[] headerRow = rowHandler.getHeaderRow();

		return new Pair<List<Object[]>, Object[]>(
				rowHandler.getFileUploadList(), headerRow);
	}

}
