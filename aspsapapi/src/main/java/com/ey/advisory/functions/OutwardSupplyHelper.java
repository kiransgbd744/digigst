/*package com.ey.advisory.functions;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardSupplyEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.OutwardSupplyRepository;
import com.ey.advisory.app.data.repositories.client.OutwardTransDocumentRepository;
import com.ey.advisory.app.docs.dto.OutwardSupplyReqDto;
import com.ey.advisory.app.docs.dto.OutwardSupplyRespDto;
import com.ey.advisory.common.GsonLocalDateTimeConverter;
import com.ey.advisory.common.multitenancy.TenantContext;
//import com.ey.advisory.documents.OutwardTransDocument;
//import com.ey.advisory.documents.converters.OutwardTransDocumentRepository;
//import com.ey.advisory.documents.dto.OutwardSupplyReqDto;
//import com.ey.advisory.documents.dto.OutwardSupplyRespDto;
//import com.ey.advisory.documents.entities.client.OutwardSupplyEntity;
//import com.ey.advisory.sap.repositories.client.OutwardSupplyRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

*//**
 * OutwardSupplyHelper class is responsible for performing actual database
 * access of the flat file coming from ERP with out any business validation and
 * bifurcation
 *
 * @author Hemasundar.J
 *//*

@Component("outwardSupplyHelper")
public class OutwardSupplyHelper {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardSupplyHelper.class);



	@Autowired
	@Qualifier("outwardSupplyRepository")
	OutwardSupplyRepository outwardSupplyRepository;
	
	@Autowired
	OutwardSupplyRespDto<String> outwardSupplyRespDto;

//	@Autowired
//	OutwardSupplyToOutwardDocConverter outwardSupplyToOutwardDocConverter;

	*//**
	 * 
	 * @param jsonString
	 * @param groupCode
	 * @return
	 *//*
	public ResponseEntity<String> saveOutwardSupply(String jsonString,
			String groupCode) {

		//Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, 
				new GsonLocalDateTimeConverter()).create();
		
		ModelMapper modelMapper = new ModelMapper();
		
		if (jsonString != null) {
			try {
				OutwardSupplyReqDto outwardSupply = gson.fromJson(jsonString,
						OutwardSupplyReqDto.class);

				// set the tenantId which indicates the client
				LOGGER.info("groupCode is " + groupCode);
				TenantContext.setTenantId(groupCode);
				LOGGER.info("groupCode " + groupCode + " is set");

				// Model mapper to convert the dto to entity
				OutwardSupplyEntity outwardSupplyEntity = modelMapper
						.map(outwardSupply, OutwardSupplyEntity.class);

				// save the flat file(entity)
				outwardSupplyRepository.save(outwardSupplyEntity);
				LOGGER.info("outward supply is persisted");

				outwardSupplyRespDto.setInvoiceId(
						outwardSupplyEntity.getSourceIdentifier());
				outwardSupplyRespDto.setStatuscCd("1");
				outwardSupplyRespDto.setStatus("Success");
				outwardSupplyRespDto.setExternalRefId(
						outwardSupplyEntity.getId().toString());
				gson = new GsonBuilder().setPrettyPrinting().create();
				return new ResponseEntity<String>(
						gson.toJson(outwardSupplyRespDto), HttpStatus.CREATED);

			} catch (Exception ex) {
				outwardSupplyRespDto.setStatuscCd("0");
				outwardSupplyRespDto.setStatus(ex.getMessage());
				outwardSupplyRespDto.setErrorCode("101");
				LOGGER.error("Exception while Parsing the Json", ex);
				LOGGER.error("Invalid Request Json :"
							+ gson.toJson(outwardSupplyRespDto));
				return new ResponseEntity<String>(
						gson.toJson(outwardSupplyRespDto),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				TenantContext.clearTenant();
			}
		} else {
			outwardSupplyRespDto.setStatuscCd("0");
			outwardSupplyRespDto.setStatus("Invalid JSON");
			outwardSupplyRespDto.setErrorCode("100");
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Invalid Request Json :"
						+ gson.toJson(outwardSupplyRespDto));
			}
			return new ResponseEntity<String>(gson.toJson(outwardSupplyRespDto),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Autowired
  private OutwardTransDocumentRepository outwardTransDocumentRepository;
	//@Transactional
	public void saveOutwardTransDocuments(List<OutwardTransDocument> documents, String groupCode) {
		//int size = documents.size();
		//int counter = 0;
		// set the tenantId which indicates the client
		LOGGER.info("groupCode is " + groupCode);
		TenantContext.setTenantId(groupCode);
		LOGGER.info("groupCode " + groupCode + " is set");

		//List<OutwardTransDocument> temp = new ArrayList<OutwardTransDocument>();
		
		for (OutwardTransDocument doc : documents) {
			//temp.add(doc);
			
			//if ((counter + 1) % 500 == 0 || (counter + 1) == size) {
				outwardTransDocumentRepository.save(doc);
			//	temp.clear();
			//}
		//	counter++;
		}
	}
	
	
}*/