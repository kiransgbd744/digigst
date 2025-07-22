package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.repositories.client.MasterCustomerRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToCustomerConverter;
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
 * 
 * @author Mahesh.Golla
 *
 */

/*
 * This service is responsible for reading the data from excel and writing into
 * Hana data base
 */
@Component("CustomerWebUploadService")
public class CustomerWebUploadService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(CustomerWebUploadService.class);

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("MasterDataToCustomerConverter")
	private MasterDataToCustomerConverter masterDataToCustomerConverter;

	@Autowired
	@Qualifier("masterCustomerRepository")
	private MasterCustomerRepository masterCustomerRepository;

	@Autowired
	@Qualifier("CustomerHeaderCheckService")
	private MasterHeaderCheckService masterHeaderCheckService;

	Gson gson = GsonUtil.newSAPGsonInstance();

	public ResponseEntity<String> ProcessingFile(MultipartFile[] files)
			throws IOException {
		InputStream stream = null;
		try {
			for (MultipartFile file : files) {

				String fileName = StringUtils
						.cleanPath(file.getOriginalFilename());
				stream = file.getInputStream();

				TabularDataSourceTraverser traverser = 
						onboardingFileTraverserFactory.getTraverser(fileName);
				LOGGER.error("File Name is {} ", fileName);

				TabularDataLayout layout = new DummyTabularDataLayout(9);

				// Add a dummy row handler that will keep counting the rows.
				@SuppressWarnings("rawtypes")
				FileUploadDocRowHandler rowHandler = 
				                          new FileUploadDocRowHandler<>();
				long stTime = System.currentTimeMillis();
				LOGGER.info("End Time for upload :" + stTime);
				traverser.traverse(stream, layout, rowHandler, null);
				Object[] getHeaders = rowHandler.getHeaderRow();
				Pair<Boolean, String> checkHeaderFormat =
						masterHeaderCheckService.validate(getHeaders);

				List<Object[]> listOfcustomers = 
						((FileUploadDocRowHandler<?>) rowHandler)
						.getFileUploadList();

				// Convert the object array to domain objects.

				if (checkHeaderFormat.getValue0()) {
					LOGGER.error("File Process starting  {} ");

					Pair<List<String>, List<MasterCustomerEntity>> customersData 
					= null/*masterDataToCustomerConverter.convert(listOfcustomers)*/;
					List<MasterCustomerEntity> cust = new ArrayList<>();

					for (MasterCustomerEntity masterCustomerEntity : 
						customersData.getValue1()) {
						cust.add(masterCustomerEntity);
					}
					List<String> errors = customersData.getValue0();

					if (errors.isEmpty() || errors.size() == 0) {

						masterCustomerRepository.saveAll(cust);
						return customerWebUploadSuccess();
					}

					// }
					else {
						return customerWebuploadFailureResp(errors);
					}
				} else {

					return createInvalidTemplateGstinCustomerUploadFailureResp();
				}
			}
			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Invalid File Content, "
					+ "File Content is not of type Multipart";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(),
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		} finally {
			if (stream != null) {
				stream.close();

			}
		}
	}

	public ResponseEntity<String> customerWebuploadFailureResp(
			List<String> errors) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String message = "File uploaded failed due to errors->" + errors;
		APIRespDto dto = new APIRespDto("Failed", message);
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> customerWebUploadSuccess() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto("Success",
				"File uploaded successfully check your status in Master "
				+ "Customer Tab..");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}

	public ResponseEntity<String> 
	createInvalidTemplateGstinCustomerUploadFailureResp() {
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIRespDto dto = new APIRespDto(" Upload Failed ", "Invalid template,"
				+ " Please upload again with correct template.");
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(dto);
		String msg = "Unexpected error while uploading  files";
		resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		resp.add("resp", respBody);
		return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
	}
}
