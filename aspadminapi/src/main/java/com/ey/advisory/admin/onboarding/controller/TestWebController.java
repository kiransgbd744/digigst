package com.ey.advisory.admin.onboarding.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.docs.Gstr6SRFileArrivalHandlerTest;
//import com.ey.advisory.app.services.docs.Gstr6SRFileArrivalHandlerTest;
import com.ey.advisory.app.services.docs.InwardTable3H3IFileArrivalHandler;
import com.ey.advisory.app.services.docs.MasterCustomerFileArrivalHandler;
import com.ey.advisory.app.services.docs.OutwardB2cFileArrivalHandler;
import com.ey.advisory.app.services.docs.OutwardTable4FileArrivalHandler;
import com.ey.advisory.common.Message;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TestWebController {
	@Autowired
	@Qualifier("OutwardB2cFileArrivalHandler")
	private OutwardB2cFileArrivalHandler outwardB2cFileArrivalHandler;
	
	@Autowired
	@Qualifier("OutwardTable4FileArrivalHandler")
	private OutwardTable4FileArrivalHandler outwardTable4FileArrivalHandler;
	
	@Autowired
	@Qualifier("InwardTable3H3IFileArrivalHandler")
	private InwardTable3H3IFileArrivalHandler inwardTable3H3IFileArrivalHandler;
	
	@Autowired
	@Qualifier("MasterCustomerFileArrivalHandler")
	private MasterCustomerFileArrivalHandler masterCustomerFileArrivalHandler;
	
	@Autowired
	@Qualifier("Gstr6SRFileArrivalHandlerTest")
	private Gstr6SRFileArrivalHandlerTest gstr6FileArrivalHandler;

	/*@Autowired
	@Qualifier("Gstr6DistrbtnFileArrivalHandler")
	Gstr6DistrbtnFileArrivalHandler gstr6FileArrivalHandler;
	*/
	@RequestMapping(value = "/webUploads" , method = RequestMethod.POST,
			produces  = { MediaType.APPLICATION_XML_VALUE})	
	public String webUploads(@RequestParam("file") MultipartFile[] files){
		
		for(MultipartFile file:files){
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			//msg.setParamsJson(fileName);
			//msg.setUserName(fileName);
			outwardB2cFileArrivalHandler.processB2csFile(msg, null);
			
		}
		
		return "Success";
		
		
	}
	
	@RequestMapping(value = "/table4webUploads" , method = RequestMethod.POST,
			produces  = { MediaType.APPLICATION_XML_VALUE})	
	public String table4webUploads(@RequestParam("file") MultipartFile[] files){
		
		for(MultipartFile file:files){
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			//msg.setParamsJson(fileName);
			//msg.setUserName(fileName);
			outwardTable4FileArrivalHandler.processTable4File(msg, null);
			
		}
		
		return "Success";
		
		
	}
	
	@RequestMapping(value = "/table3h3iwebUploads" , method = RequestMethod.POST,
			produces  = { MediaType.APPLICATION_XML_VALUE})	
	public String table3h3iwebUploads(@RequestParam("file") MultipartFile[] files){
		
		for(MultipartFile file:files){
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			//msg.setParamsJson(fileName);
			//msg.setUserName(fileName);
			inwardTable3H3IFileArrivalHandler.processTable3H3IFile(msg, null);
			
		}
		
		return "Success";
		
		
	}
	
	
	@RequestMapping(value = "/customerUploads" , method = RequestMethod.POST,
			produces  = { MediaType.APPLICATION_XML_VALUE})	
	public String customerData(@RequestParam("file") MultipartFile[] files){
		
		for(MultipartFile file:files){
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			//msg.setParamsJson(fileName);
			//msg.setUserName(fileName);
			masterCustomerFileArrivalHandler.processCusterData(msg, null);
			
		}
		
		return "Success";
		
		
	}
	
	
	@GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> 
	   downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		
		return null;
	}
	
	@PostMapping(value = "/gstr6webUploads" ,
			produces  = { MediaType.APPLICATION_XML_VALUE})	
	public String webUploadsGstr6(@RequestParam("file") MultipartFile[] files){
		
		for(MultipartFile file:files){
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			//msg.setParamsJson(fileName);
			//msg.setUserName(fileName);
			gstr6FileArrivalHandler.processProductFile(msg, null);
			
		}
		
		return "Success";
		
		
	}
	
	
}
