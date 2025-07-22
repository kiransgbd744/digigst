package com.ey.advisory.app.gstr2jsonupload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.Gstr2FileStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetGstr2B2baInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetCdnInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetCdnaInvoicesAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2aGetIsdInvoicesAtGstnRepository;
import com.ey.advisory.app.util.Gstr2FileUploadUtil;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("Gstr2JsonUploadServiceImpl")
public class Gstr2JsonUploadServiceImpl implements Gstr2JsonUploadService {

	@Autowired
	@Qualifier("Gstr2FileUploadUtil")
	private Gstr2FileUploadUtil gstr2FileUploadUtil;

	@Autowired
	@Qualifier("Gstr2JsonParserImpl")
	private Gstr2JsonParser parseService;

	@Autowired
	@Qualifier("GetGstr2B2bInvoicesRepository")
	private GetGstr2B2bInvoicesRepository b2bRepo;

	@Autowired
	@Qualifier("GetGstr2B2baInvoicesRepository")
	private GetGstr2B2baInvoicesRepository b2bARepo;

	@Autowired
	@Qualifier("Gstr2aGetIsdInvoicesAtGstnRepository")
	private Gstr2aGetIsdInvoicesAtGstnRepository isdRepo;

	@Autowired
	@Qualifier("Gstr2aGetCdnInvoicesAtGstnRepository")
	private Gstr2aGetCdnInvoicesAtGstnRepository cdnRepo;

	@Autowired
	@Qualifier("Gstr2aGetCdnaInvoicesAtGstnRepository")
	private Gstr2aGetCdnaInvoicesAtGstnRepository cdnARepo;
	
	@Autowired
	@Qualifier("Gstr2FileStatusRepository")
	private Gstr2FileStatusRepository fileStatusRepo;

	
	Gson gson = GsonUtil.newSAPGsonInstance();

	public ResponseEntity<String> gstr2JsonFileUpload(MultipartFile[] files, 
			String folderName, String gstin) {
		
		Document doc = null;
		Session openCmisSession = null;
		String fileName = null;
		
		try {
			for (MultipartFile file : files) {
				fileName = gstr2FileUploadUtil.getFileName(file);
				openCmisSession = gstr2FileUploadUtil.getCmisSession();
				// access the root folder of the repository
				Folder root = openCmisSession.getRootFolder();
				LOGGER.debug("Root Folder name is ", root.getName());

				// create a new folder
				Map<String, String> newFolderProps = new HashMap<String, String>();
				newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
				newFolderProps.put(PropertyIds.NAME, folderName);
				Folder createdFolder = null;
				try {
					OperationContext oc = openCmisSession.getDefaultContext();
					String query = String.format("cmis:name='%s'", folderName);
					ItemIterable<CmisObject> folders = openCmisSession
							.queryObjects("cmis:folder", query, false, oc);
					for (CmisObject obj : folders) {
						if (obj instanceof Folder) {
							Folder folder = (Folder) obj;
							LOGGER.debug("Folder Name  1" + folder.getName());
							LOGGER.debug("Folder Name  2" + folderName);
							if (folder.getName().equalsIgnoreCase(folderName)) {
								LOGGER.debug("Folder already exists");
								createdFolder = folder;
							}
						}
					}
					if (createdFolder != null) {
						LOGGER.debug("Creating Folder");
						createdFolder = root.createFolder(newFolderProps);
						LOGGER.debug("Folder Created");
					}
				} catch (CmisNameConstraintViolationException e) {
					LOGGER.error("Exception while creating folder", e);
				}

				if (createdFolder == null) {
					LOGGER.info("Creating Folder");
					createdFolder = root.createFolder(newFolderProps);
					LOGGER.info("Created Folder");
				}

				// create a new file in the root folder
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties.put(PropertyIds.NAME, fileName);
				InputStream stream = null;
				if (!file.isEmpty()) {
					try {
						byte[] fileContent = file.getBytes();
						stream = new ByteArrayInputStream(fileContent);
						ContentStream contentStream = openCmisSession
								.getObjectFactory().createContentStream(
										fileName, fileContent.length,
										"application/json", stream);

						LOGGER.debug("Creating document in the repo");

						Document createdDocument = createdFolder.createDocument(
								properties, contentStream,
								VersioningState.MAJOR);

						LOGGER.debug("Created document in the repo "
								+ createdDocument.getId());
						LOGGER.debug("Created document name " + "in the repo  "
								+ createdDocument.getName());
					} catch (CmisNameConstraintViolationException e) {
						LOGGER.error(
								"Exception while creating the document in repo",
								e);
					}

				}
				
			 doc = DocumentUtility.downloadDocument(fileName, folderName);
			 
			 LOGGER.error("Downloded document name" + doc);
			 
			if(doc == null){
				String msg = "File Failed, doc is null";
				LOGGER.error(msg);
				JsonObject resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		        return new ResponseEntity<>(resp.toString(), HttpStatus.EXPECTATION_FAILED);
			}
			
			InputStream contentUrl = doc.getContentStream().getStream();
			LOGGER.error("document contentUrl : " + doc.getContentStream().getStream());
			
			
			//updating filestatus table
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM"; 
							
				Gstr2FileStatusEntity fileStatus = new Gstr2FileStatusEntity();

				LocalDateTime localDate = LocalDateTime.now();
				String oriFileName = fileName.substring(0,
						fileName.lastIndexOf("."));
				String extension = fileName
						.substring(fileName.lastIndexOf(".") + 1);
				String status = APIConstants.JSON_UPLOADED;

				fileStatus.setFileName(oriFileName);
				fileStatus.setFileType(extension);
				fileStatus.setUpdatedBy(userName);
				fileStatus.setUpdatedOn(localDate);
				fileStatus.setFileStatus(status);
				
				fileStatusRepo.save(fileStatus);	
	
			ResponseEntity<String> readJson = readJson(gstin, contentUrl);
			
			if(readJson!= null && readJson.getStatusCode()==HttpStatus.EXPECTATION_FAILED){
				return readJson;
			}else {
				String msg = "Success, Gstr2a file uploaded successfully";
				JsonObject resp = new JsonObject();
				resp.add("hdr", gson.toJsonTree(new APIRespDto("S", msg))); 
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			
		}
	}
		catch (Exception e) {
			e.printStackTrace();
			//fileStatusRepo.updateFileStatus(id, APIConstants.FAILED);
			String msg = "File Failed, could not connect to doc repository";
			LOGGER.error(msg);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
	        return new ResponseEntity<>(resp.toString(), HttpStatus.EXPECTATION_FAILED);
	        
		}
		return null;
		
	}


	private ResponseEntity<String> readJson( String gstin, InputStream contentUrl) {
		
		LOGGER.error("Inside readJson() method ");
		int count = 0;

		try {
			/*Object jsonResponse = parser.parse(
					//new FileReader("C:/Users/QD194RK/Desktop/sampleJson.json"));
					new FileReader(contentUrl));*/
			
			JsonReader reader = new JsonReader(new InputStreamReader(contentUrl, "UTF-8"));
			    reader.setLenient(true);
			    
			    JsonObject jsonResponse = (JsonObject) new JsonParser().parse(reader)
			    		.getAsJsonObject();

			/*Object jsonResponse = (JsonObject) new JsonParser().parse(
				      new InputStreamReader(contentUrl, "UTF-8"));*/
			
			LOGGER.error("Inside readJson() method, jsonResponse " + jsonResponse);
			
			String apiResp = gson.toJson(jsonResponse);
			JsonObject requestObject = (new JsonParser()
					.parse((String) apiResp)).getAsJsonObject();
			Header reqDto = gson.fromJson(requestObject, Header.class);
			LOGGER.error(apiResp + " : " + reqDto);

			String cgstin = reqDto.getGstin();
			String taxPeriod = reqDto.getFp();
			
			if(!gstin.equalsIgnoreCase(cgstin)){
				String msg = "File Failed, gstin is not matching";
				LOGGER.error(msg);
				JsonObject resp = new JsonObject();
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		        return new ResponseEntity<>(resp.toString(), HttpStatus.EXPECTATION_FAILED);
				
			}

			if (requestObject.has(APIConstants.B2B)) {

				List<GetGstr2aB2bInvoicesHeaderEntity> b2bEntity = parseService
						.parseB2bData(reqDto, apiResp);

				b2bRepo.softlyDeleteB2bHeader(cgstin, taxPeriod,null);

				b2bRepo.saveAll(b2bEntity);
				LOGGER.error("succuessfully updated B2B data ", b2bEntity);
				count = count + b2bEntity.size();
			}

			if (requestObject.has(APIConstants.B2BA)) {
				List<GetGstr2aB2baInvoicesHeaderEntity> b2bAEntity = parseService
						.parseB2baData(reqDto, apiResp);
				LOGGER.error("b2bAEntity", b2bAEntity);

				b2bARepo.softlyDeleteB2baHeader(cgstin, taxPeriod,null);

				b2bARepo.saveAll(b2bAEntity);
				LOGGER.error("succuessfully updated B2BA data ", b2bAEntity);
				count = count + b2bAEntity.size();

			}

			if (requestObject.has(APIConstants.CDN)) {
				List<GetGstr2aCdnInvoicesHeaderEntity> cdnEntity = parseService
						.parseCdnData(reqDto, apiResp);

				cdnRepo.softlyDeleteCdnaHeader(cgstin, taxPeriod);

				cdnRepo.saveAll(cdnEntity);
				LOGGER.error("succuessfully updated CDN data ", cdnEntity);
				
				count = count + cdnEntity.size();
			}

			if (requestObject.has(APIConstants.CDNA)) {
				List<GetGstr2aCdnaInvoicesHeaderEntity> cdnAEntity = parseService
						.parseCdnAData(reqDto, apiResp);

				cdnARepo.softlyDeleteCdnaHeader(cgstin, taxPeriod);

				cdnARepo.saveAll(cdnAEntity);
				LOGGER.error("succuessfully updated CDNA data ", cdnAEntity);
				
				count = count + cdnAEntity.size();
			}

			if (requestObject.has(APIConstants.ISD)) {
				List<GetGstr2aIsdInvoicesHeaderEntity> isdEntity = parseService
						.parseIsdData(reqDto, apiResp);

				isdRepo.softlyDeleteIsdHeader(cgstin, taxPeriod,null);
				isdRepo.saveAll(isdEntity);
				LOGGER.error("succuessfully updated ISD data ", isdEntity);
				
				count = count + isdEntity.size();
				
			}
		
			APIRespDto dto = new APIRespDto("Success", 
					"Gstr2a Json file uploaded successfully");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			

		} catch (Exception e) {
			String msg = "Error while Getting File Name";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.EXPECTATION_FAILED);
		}
		
		
}}
