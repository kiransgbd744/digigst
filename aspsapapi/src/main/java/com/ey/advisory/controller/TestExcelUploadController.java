package com.ey.advisory.controller;

//To-DO This class is To be removed
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.service.upload.way3recon.Ewb3WayReconUploadFileArrivalService;
import com.ey.advisory.app.services.docs.ComprehensiveInwardSRFileArrivalHandler;
import com.ey.advisory.app.services.docs.ComprehensiveSRFileArrivalHandler;
import com.ey.advisory.app.services.docs.Gstr6SRFileArrivalHandlerTest;
import com.ey.advisory.app.services.docs.SRFileArrivalHandler;
import com.ey.advisory.app.services.docs.gstr2.Anx2InwardRawFileArrivalHandler;
import com.ey.advisory.app.services.gstr7fileupload.Gstr7FileArrivalHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
public class TestExcelUploadController {
	@Autowired
	@Qualifier("Anx2InwardRawFileArrivalHandler")
	Anx2InwardRawFileArrivalHandler anx2InwardRawFileArrivalHandler;
	@Autowired
	@Qualifier("SRFileArrivalHandler")
	private SRFileArrivalHandler srFileArrivalHandler;
	
	@Autowired
	@Qualifier("Gstr6SRFileArrivalHandlerTest")
	Gstr6SRFileArrivalHandlerTest gstr6SRFileArrivalHandlerTest;
	@Autowired
	@Qualifier("ComprehensiveSRFileArrivalHandler")
	ComprehensiveSRFileArrivalHandler comprehensiveSRFileArrivalHandler;
	
	@Autowired
	@Qualifier("Gstr7FileArrivalHandler")
	Gstr7FileArrivalHandler gstr7FileArrivalHandler;
	
	@Autowired
	@Qualifier("ComprehensiveInwardSRFileArrivalHandler")
	ComprehensiveInwardSRFileArrivalHandler comprehensiveInwardSRFileArrivalHandler;
	
	@Autowired
	@Qualifier("Ewb3WayReconUploadFileArrivalServiceImpl")
	Ewb3WayReconUploadFileArrivalService service;

	
	@RequestMapping(value = "/ui/getFileDetails", method = RequestMethod.POST)
	public ResponseEntity<String> getFileDetails(){
		Message message = new Message();
		
		
		/*String paramJson = 
				"{\"filePath\":\"C:/umesha/InwardRawFile\","
				+ "\"fileName\":\"InwardRaw.xlsx\"}";*/
		/*String paramJson = 
				"{\"filePath\":\"C:/Users/Siva.Nandam/Downloads\","
				+ "\"fileName\":\"Copy of UAT 5 EINV EWB  - Copy.xlsx\"}";*/
		
		/*String paramJson = 
				"{\"filePath\":\"C:/Users/QD194RK/Desktop\","
				+ "\"fileName\":\"Test NIC format  sample data.xlsx\"}";*/
		
		service.validateResponseFile(101L, "Test NIC format  sample data.xlsx", "C:/Users/QD194RK/Desktop");
		
		
		/*message.setParamsJson(paramJson);
		FileArrivalMsgDto msg = extractAndValidateMessage(message);
		*/
		//gstr6SRFileArrivalHandlerTest.processProductFile(message, null);

		//srFileArrivalHandler.processSRFile(message,null);
		//anx2InwardRawFileArrivalHandler.processAnx2InwardRawFile(message, null);
		//gstr7FileArrivalHandler.processGstr7File(message, null);
		//comprehensiveSRFileArrivalHandler.processEInvoiceSRFile(message, null);
		//comprehensiveInwardSRFileArrivalHandler.processInwardSRFile(message, null);
		
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value = "/ui/testDetails1", method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> testDetails1(){
		
		TestDto  testDto1  = new TestDto();
		testDto1.setTableSection("4A");
		
		TestDto  testDto2  = new TestDto();
		testDto2.setTableSection("4B");
		
		TestDto  testDto3  = new TestDto();
		testDto3.setTableSection("4C");
		
		TestDto  testDto4  = new TestDto();
		testDto4.setTableSection("6B");
		
		TestDto  testDto5  = new TestDto();
		testDto5.setTableSection("6C");
		
		
		
		List<TestDto> defaultEy = new ArrayList<>();
		
		TestDto defaultGstn = new TestDto();
		
		
		defaultEy.add(testDto1);
		defaultEy.add(testDto2);
		defaultEy.add(testDto3);
		defaultEy.add(testDto4);
		defaultEy.add(testDto5);
		
		List<TestDto> gstnEyListForUI = getEyDetailsForUI(defaultEy);
		
		
		TestDto testDto = new TestDto();
		testDto.setDiff(new BigDecimal("0"));
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		JsonElement gstnRespBody = gson.toJsonTree(defaultGstn);
		
		JsonElement eyRespBody = gson.toJsonTree(gstnEyListForUI);
		
		JsonElement diffRespBody = gson.toJsonTree(testDto);
		
		Map<String, JsonElement> combined = new HashMap<>();
		combined.put("gstn", gstnRespBody);
		combined.put("ey", eyRespBody);
		combined.put("diff", diffRespBody);
		
		JsonElement b2bRespBody = gson.toJsonTree(combined);
		JsonObject b2bResp = new JsonObject();
		b2bResp.add("b2b", b2bRespBody);
		
		JsonElement respBody = gson.toJsonTree(b2bResp);
		
		JsonObject resp = new JsonObject();
		resp.add("resp", respBody);
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
	
	private List<TestDto> getViewDetails(){
		List<TestDto> list = new ArrayList<>();
		
		TestDto testDto1 = new TestDto();
		testDto1.setTableSection("4A");
		testDto1.setRecords(10);
		
		TestDto  testDto2  = new TestDto();
		testDto2.setTableSection("4B");
		testDto2.setRecords(20);
		
		list.add(testDto1);
		list.add(testDto2);
		
		return list;
	}
	
	private List<TestDto> getEyDetailsForUI(List<TestDto> testList){
		List<TestDto> listFromView = getViewDetails();
		
		//Filter the List for 4A
		List<TestDto> view4AFiltered =
				listFromView
			        .stream()
			        .filter(p -> p.getTableSection().equalsIgnoreCase("4A"))
			        .collect(Collectors.toList());

		//Filter the List for 4A
		List<TestDto> view4bFiltered =
				listFromView
			        .stream()
			        .filter(p -> p.getTableSection().equalsIgnoreCase("4B"))
			        .collect(Collectors.toList());
		
		//If the filtered list is not null 
		if (view4AFiltered != null & view4bFiltered.size() > 0) {
			//then filter default List for 4A
			List<TestDto> default4AFiltered = testList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("4A"))
					.collect(Collectors.toList());
			
			//If the default filtered list is not null 
			default4AFiltered.forEach(default4A -> {
				//then remove it from List
				testList.remove(default4A);
			});
			
			//Iterate view list
			view4AFiltered.forEach(view4A -> {
				TestDto testDto = new TestDto();
				testDto.setTableSection(view4A.getTableSection());
				testDto.setRecords(view4A.getRecords());
				//Add 4A to the default list
				testList.add(testDto);
			});
		}
		
		if (view4AFiltered != null & view4bFiltered.size() > 0) {
			List<TestDto> default4BFiltered = testList.stream()
					.filter(p -> p.getTableSection().equalsIgnoreCase("4B"))
					.collect(Collectors.toList());
			default4BFiltered.forEach(default4B -> {
				testList.remove(default4B);
			});
			view4bFiltered.forEach(view4B -> {
				TestDto testDto = new TestDto();
				testDto.setTableSection(view4B.getTableSection());
				testDto.setRecords(view4B.getRecords());
				testList.add(testDto);
			});
		}
		
		
		// Sort the list in Ascending Order
		Collections.sort(testList, new Comparator<TestDto>() {
			@Override
			public int compare(TestDto testDto1, TestDto testDto2) {
				return testDto1.getTableSection()
						.compareTo(testDto2.getTableSection());
			}
		});
		
		return testList;
	}
	
}
	
