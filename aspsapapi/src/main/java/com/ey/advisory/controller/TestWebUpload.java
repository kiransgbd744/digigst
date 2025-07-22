package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.docs.ATFileArrivalHandler;
import com.ey.advisory.app.services.docs.B2csFileArrivalHandler;
import com.ey.advisory.app.services.docs.CewbFileArrivalHandler;
import com.ey.advisory.app.services.docs.ComprehensiveInwardSRFileArrivalHandler;
import com.ey.advisory.app.services.docs.ComprehensiveSRFileArrivalHandler;
import com.ey.advisory.app.services.docs.Gstr3bFileArrivalHandler;
import com.ey.advisory.app.services.docs.Gstr9InOutwardFileArrivalHandler;
import com.ey.advisory.app.services.docs.HsnFileArrivalHandler;
import com.ey.advisory.app.services.docs.InterestAndLateFeeFileArrivalHandler;
import com.ey.advisory.app.services.docs.InvoiceFileArrivalHandler;
import com.ey.advisory.app.services.docs.InwardTable3H3IFileArrivalHandler;
import com.ey.advisory.app.services.docs.Itc04FileArrivalHandler;
import com.ey.advisory.app.services.docs.MasterCustomerFileArrivalHandler;
import com.ey.advisory.app.services.docs.NilFileArrivalHandler;
import com.ey.advisory.app.services.docs.OutwardB2cFileArrivalHandler;
import com.ey.advisory.app.services.docs.OutwardTable4FileArrivalHandler;
import com.ey.advisory.app.services.docs.RefundsFileArrivalHandler;
import com.ey.advisory.app.services.docs.Ret1And1AFileArrivalHandler;
import com.ey.advisory.app.services.docs.SRFileArrivalHandler;
import com.ey.advisory.app.services.docs.SRFileArrivalHandlerTest;
import com.ey.advisory.app.services.docs.SetOffAndUtilFileArrivalHandler;
import com.ey.advisory.app.services.docs.TcsAndTdsFileArrivalHandler;
import com.ey.advisory.app.services.docs.TxpdFileArrivalHandler;
import com.ey.advisory.app.services.docs.gstr2.Anx2InwardRawFileArrivalHandler;
import com.ey.advisory.app.services.docs.gstr2.Anx2InwardRawFileArrivalHandlerTest;
import com.ey.advisory.app.services.search.filestatussearch.AsyncInwardInvoiceManagementDownloadServiceImpl;
import com.ey.advisory.app.services.search.filestatussearch.OutwardFileStatusEwbReportDownloadServiceImpl;
import com.ey.advisory.app.services.search.filestatussearch.OutwardFileStatusReportDownloadServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class TestWebUpload {

	@Autowired
	@Qualifier("ComprehensiveInwardSRFileArrivalHandler")
	private ComprehensiveInwardSRFileArrivalHandler comprehensiveInwardSRFileArrivalHandler;

	@Autowired
	@Qualifier("Gstr3bFileArrivalHandler")
	private Gstr3bFileArrivalHandler gstr3bFileArrivalHandler;

	@Autowired
	@Qualifier("SRFileArrivalHandlerTest")
	private SRFileArrivalHandlerTest srFileArrivalHandlerTest;

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
	@Qualifier("InvoiceFileArrivalHandler")
	private InvoiceFileArrivalHandler invoiceFileArrivalHandler;

	@Autowired
	@Qualifier("ATFileArrivalHandler")
	private ATFileArrivalHandler aTFileArrivalHandler;

	@Autowired
	@Qualifier("TxpdFileArrivalHandler")
	private TxpdFileArrivalHandler txpdFileArrivalHandler;

	@Autowired
	@Qualifier("B2csFileArrivalHandler")
	private B2csFileArrivalHandler b2csFileArrivalHandler;

	@Autowired
	@Qualifier("SRFileArrivalHandler")
	private SRFileArrivalHandler sRFileArrivalHandler;

	@Autowired
	@Qualifier("RefundsFileArrivalHandler")
	private RefundsFileArrivalHandler refundsFileArrivalHandler;

	@Autowired
	@Qualifier("InterestAndLateFeeFileArrivalHandler")
	private InterestAndLateFeeFileArrivalHandler interestAndLateFeeFileArrivalHandler;

	@Autowired
	@Qualifier("SetOffAndUtilFileArrivalHandler")
	private SetOffAndUtilFileArrivalHandler setOffAndUtilFileArrivalHandler;

	@Autowired
	@Qualifier("Ret1And1AFileArrivalHandler")
	private Ret1And1AFileArrivalHandler ret1And1AFileArrivalHandler;

	@Autowired
	@Qualifier("ComprehensiveSRFileArrivalHandler")
	private ComprehensiveSRFileArrivalHandler eInvoiceSRFileArrivalHandler;

	@Autowired
	@Qualifier("Anx2InwardRawFileArrivalHandler")
	private Anx2InwardRawFileArrivalHandler anx2InwardRawFileArrivalHandler;

	@Autowired
	@Qualifier("NilFileArrivalHandler")
	private NilFileArrivalHandler nilFileArrivalHandler;

	@Autowired
	@Qualifier("Itc04FileArrivalHandler")
	private Itc04FileArrivalHandler itc04FileArrivalHandler;

	@Autowired
	@Qualifier("HsnFileArrivalHandler")
	private HsnFileArrivalHandler HsnFileArrivalHandler;

	@Autowired
	@Qualifier("TcsAndTdsFileArrivalHandler")
	private TcsAndTdsFileArrivalHandler tcsAndTdsFileArrivalHandler;

	@Autowired
	@Qualifier("OutwardFileStatusReportDownloadServiceImpl")
	private OutwardFileStatusReportDownloadServiceImpl outwardFileStatusReportDownloadProcessor;

	@Autowired
	@Qualifier("OutwardFileStatusEwbReportDownloadServiceImpl")
	private OutwardFileStatusEwbReportDownloadServiceImpl outwardFileStatusEwbReportDownloadServiceImpl;

	@Autowired
	@Qualifier("CewbFileArrivalHandler")
	private CewbFileArrivalHandler cewbFileArrivalHandler;

	@Autowired
	@Qualifier("AsyncInwardInvoiceManagementDownloadServiceImpl")
	private AsyncInwardInvoiceManagementDownloadServiceImpl asyncInwardInvoiceManagementDownloadProcessor;

	@Autowired
	@Qualifier("Gstr9InOutwardFileArrivalHandler")
	private Gstr9InOutwardFileArrivalHandler gstr9InOutwardFileArrivalHandler;

	@RequestMapping(value = "/ui/cewbWebUploadsTest", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String cewbFileArrivalHandler(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			cewbFileArrivalHandler.processCewbFileFile(msg, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/gstr9InOutwardWebUploadsTest", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String gstr9InOutwardWebUploadsTest(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			gstr9InOutwardFileArrivalHandler.processTdsTcsFile(msg, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/invinwardmang", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String asyncInwardInvoiceManagementDownloadProcessor(
			@RequestBody String fileStatusJson) {
		JsonObject jsonReqObj = (new JsonParser().parse(fileStatusJson)
				.getAsJsonObject());

		JsonObject json = jsonReqObj.get("req").getAsJsonObject();

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		// Execute the File status service method and get the result.

		Message fileReqDto = gson.fromJson(json, Message.class);
		asyncInwardInvoiceManagementDownloadProcessor
				.generateReports(fileReqDto.getId());
		return "Success";

	}

	@RequestMapping(value = "/ui/testCont", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String outwardFileStatusReportDownloadProcessor(
			@RequestBody String fileStatusJson) {
		JsonObject jsonReqObj = (new JsonParser().parse(fileStatusJson)
				.getAsJsonObject());

		JsonObject json = jsonReqObj.get("req").getAsJsonObject();

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		// Execute the File status service method and get the result.

		Message fileReqDto = gson.fromJson(json, Message.class);
		outwardFileStatusReportDownloadProcessor
				.generateReports(fileReqDto.getId());
		return "Success";

	}

	@RequestMapping(value = "/ui/testEwbCont", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String outwardFileStatusEwbReportDownloadProcessor(
			@RequestBody String fileStatusJson) {
		JsonObject jsonReqObj = (new JsonParser().parse(fileStatusJson)
				.getAsJsonObject());

		JsonObject json = jsonReqObj.get("req").getAsJsonObject();

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		// Execute the File status service method and get the result.

		Message fileReqDto = gson.fromJson(json, Message.class);
		outwardFileStatusEwbReportDownloadServiceImpl
				.generateReports(fileReqDto.getId());
		return "Success";

	}

	@RequestMapping(value = "/ui/EinwradwebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String comprehensiveInwardSRFileArrivalHandler(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			comprehensiveInwardSRFileArrivalHandler.processInwardSRFile(msg,
					null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/itc04webUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String itc04WebUploads(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			itc04FileArrivalHandler.processItc04File(msg, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/hsnwebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String hsnWebUploads(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			HsnFileArrivalHandler.processHsnFile(msg, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/inwardwebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String inwardWebUploads(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			anx2InwardRawFileArrivalHandler.processAnx2InwardRawFile(msg, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/nilNonExtwebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String nilNonExtwebUploads(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			nilFileArrivalHandler.processNilFile(msg, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/eInvoicewebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String eInvoiceWebUploads(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			eInvoiceSRFileArrivalHandler.processEInvoiceSRFile(msg, null, null);
		}
		return "Success";

	}

	@RequestMapping(value = "/ui/webUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String webUploads(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			outwardB2cFileArrivalHandler.processB2csFile(msg, null);

		}

		return "Success";

	}

	@PostMapping(value = "/ui/rawWebupload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String webUpload(@RequestParam("file") MultipartFile[] files) {
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message message = new Message();
			message.setUserName(fileName);
			sRFileArrivalHandler.processSRFile(message, null);
		}
		return "Success";
	}

	@RequestMapping(value = "/ui/invWebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String invoice(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			invoiceFileArrivalHandler.processInvoiceFile(msg, null);

		}

		return "Success";
	}

	@RequestMapping(value = "/ui/table4webUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String table4webUploads(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			outwardTable4FileArrivalHandler.processTable4File(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/table3h3iwebUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String table3h3iwebUploads(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			inwardTable3H3IFileArrivalHandler.processTable3H3IFile(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/customerUploads", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String customerData(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			masterCustomerFileArrivalHandler.processCusterData(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/webUploadsAt", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String at(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			aTFileArrivalHandler.processATsFile(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/webUploadsTxpd", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String txpd(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			txpdFileArrivalHandler.processAtaFile(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/webUploadsB2cs", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String b2cs(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			b2csFileArrivalHandler.processB2csFile(msg, null);

		}
		return "Success";
	}

	@RequestMapping(value = "/ui/ret1And1A", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String ret1And1A(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			ret1And1AFileArrivalHandler.processRet1And1AFile(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/setOffAndUtil", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String setOffAndUtil(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			setOffAndUtilFileArrivalHandler.processTableSetOffAndUtilFile(msg,
					null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/interestAndLateFees", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String interestAndLateFee(
			@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			interestAndLateFeeFileArrivalHandler
					.processInterestAndLateFeeFile(msg, null);

		}

		return "Success";

	}

	@RequestMapping(value = "/ui/refundss", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String refundss(@RequestParam("file") MultipartFile[] files) {

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message msg = new Message();
			msg.setUserName(fileName);
			// msg.setParamsJson(fileName);
			// msg.setUserName(fileName);
			refundsFileArrivalHandler.processRefundsFile(msg, null);

		}
		return "Success";
	}

	@Autowired
	@Qualifier("Anx2InwardRawFileArrivalHandlerTest")
	private Anx2InwardRawFileArrivalHandlerTest anx2InwardRawFileArrivalHandlerTest;

	@PostMapping(value = "/ui/anx2InwardRawWebupload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String webInwardUpload(@RequestParam("file") MultipartFile[] files) {
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message message = new Message();
			message.setUserName(fileName);
			anx2InwardRawFileArrivalHandlerTest
					.processAnx2InwardRawFile(message, null);
		}
		return "Success";
	}

	@PostMapping(value = "/ui/gstr3bWebUploadsTest", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String gstr3bFileArrivalHandler(
			@RequestParam("file") MultipartFile[] files) {
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message message = new Message();
			message.setUserName(fileName);
			gstr3bFileArrivalHandler.processGstr3BFile(message, null);
		}
		return "Success";
	}

	@PostMapping(value = "/ui/getr2xTcsTdsWebuploads", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public String getr2xTcsTdsWebuploads(
			@RequestParam("file") MultipartFile[] files) {
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			Message message = new Message();
			message.setUserName(fileName);
			tcsAndTdsFileArrivalHandler.processTdsTcsFile(message, null);
		}
		return "Success";
	}

}
