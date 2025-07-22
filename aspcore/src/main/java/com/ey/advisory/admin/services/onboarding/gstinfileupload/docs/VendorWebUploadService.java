package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.admin.data.repositories.client.MasterVendorRepository;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.MasterDataToVendorConverter;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * 
 * @author Mahesh.Golla
 *
 */

/*
 * This service is responsible for read the vendor data from excel than persist
 * the data into database
 */

@Service("VendorWebUploadService")
public class VendorWebUploadService {

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("MasterDataToVendorConverter")
	private MasterDataToVendorConverter masterDataToVendorConverter;

	@Autowired
	@Qualifier("masterVendorRepository")
	private MasterVendorRepository masterVendorRepository;

	@Autowired
	@Qualifier("VendorHeaderCheckService")
	private VendorHeaderCheckService vendorHeaderCheckService;
	public static final Logger LOGGER = LoggerFactory
			.getLogger(VendorWebUploadService.class);

	public ResponseEntity<String> ProcessingFile(MultipartFile[] files)
			throws IOException {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			for (MultipartFile file : files) {
				String fileName = StringUtils
						.cleanPath(file.getOriginalFilename());
				InputStream stream = file.getInputStream();

				TabularDataSourceTraverser traverser =
						 onboardingFileTraverserFactory
						.getTraverser(fileName);
				LOGGER.error("File Name  {} " + fileName);

				TabularDataLayout layout = new DummyTabularDataLayout(8);

				// Add a dummy row handler that will keep counting the rows.
				FileUploadDocRowHandler<Object> rowHandler = 
						                      new FileUploadDocRowHandler<>();
				long stTime = System.currentTimeMillis();
				traverser.traverse(stream, layout, rowHandler, null);
				Object[] getHeaders = rowHandler.getHeaderRow();
				Pair<Boolean, String> checkHeaderFormat = 
						vendorHeaderCheckService
						.validate(getHeaders);

				List<Object[]> listOfVendors = 
						((FileUploadDocRowHandler<?>) rowHandler)
						.getFileUploadList();

				// Convert the object array to domain objects.

				if (checkHeaderFormat.getValue0()) {
					LOGGER.error("File Process starting  {} ");
					LOGGER.info("Start Time for upload :" + stTime);
					/*List<MasterVendorEntity> vendorData = 
							masterDataToVendorConverter
							.convert(listOfVendors);*/

					Pair<List<String>, List<MasterVendorEntity>> vendordata
					=masterDataToVendorConverter
							.convert(listOfVendors);
					List<String> errorMsgs = vendordata.getValue0();
					LOGGER.info("errorMsgs  {} " + errorMsgs);
					
					if (errorMsgs.isEmpty() && errorMsgs.size() == 0) {
						List<MasterVendorEntity> listData = vendordata
								.getValue1();
						if (!listData.isEmpty() && listData.size() > 0) {
							masterVendorRepository.saveAll(listData);
							LOGGER.info("File Process Ended  {} ");
							long endTime = System.currentTimeMillis();
							LOGGER.info("End Time for upload :" + endTime);

							APIRespDto dto = new APIRespDto("Success",
									"File uploaded successfully");
							JsonObject resp = new JsonObject();
							JsonElement respBody = gson.toJsonTree(dto);
							resp.add("hdr", gson.toJsonTree(
									APIRespDto.createSuccessResp()));
							resp.add("resp", respBody);
							return new ResponseEntity<String>(resp.toString(),
									HttpStatus.OK);
						} else {
							APIRespDto dto = new APIRespDto("Failed",
									"File uploaded failed due to "
									+ "no data found in uploaded excel");
							JsonObject resp = new JsonObject();
							JsonElement respBody = gson.toJsonTree(dto);
							resp.add("hdr", gson
									.toJsonTree(APIRespDto.createSuccessResp()));
							resp.add("resp", respBody);
							return new ResponseEntity<String>(resp.toString(),
									HttpStatus.OK);
						}

					}else {
						APIRespDto dto = new APIRespDto("Failed",
								"File uploaded failed with error messages->"
										+ errorMsgs);
						JsonObject resp = new JsonObject();
						JsonElement respBody = gson.toJsonTree(dto);
						resp.add("hdr", gson
								.toJsonTree(APIRespDto.createSuccessResp()));
						resp.add("resp", respBody);
						return new ResponseEntity<String>(resp.toString(),
								HttpStatus.OK);
					}

				} else {

					APIRespDto dto = new APIRespDto("Failed",
							"File uploaded Failed");
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					String msg = "The size of the Header do not matched ";
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
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
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		}
	}
}

					
					
					
					
				/*	
					
					masterVendorRepository.saveAll(vendorData);
					LOGGER.error("File Process ended  {} ");
					long endTime = System.currentTimeMillis();
					LOGGER.info("End Time for upload :" + endTime);
					APIRespDto dto = new APIRespDto("Sucess",
							"File uploaded successfully");
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					resp.add("resp", respBody);
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);

				} else {

					APIRespDto dto = new APIRespDto("Failed",
							"File uploaded Failed");
					JsonObject resp = new JsonObject();
					JsonElement respBody = gson.toJsonTree(dto);
					String msg = "The size of the Header do not matched ";
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));
					resp.add("resp", respBody);
					return new ResponseEntity<String>(resp.toString(),
							HttpStatus.OK);
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

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", "File uploaded Failed");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);

		}
	}
}
*/