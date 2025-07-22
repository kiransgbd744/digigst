package com.ey.advisory.controller.gstr2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.gstr2.Gstr2AspInnerErrorDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RestController
public class AspInnerErrorController {
	

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AspInnerErrorController.class);

	
	@RequestMapping(value = "/ui/getGst2AspInnerError", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2AspInnerError(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
						
			List<Gstr2AspInnerErrorDto> respList = 
					new ArrayList<Gstr2AspInnerErrorDto>();
			Gstr2AspInnerErrorDto dto = new Gstr2AspInnerErrorDto();
			dto.setAccountVoucherDate(LocalDate.now());
			dto.setAccountVoucherNum("123126");
			dto.setBillToState("State1");
			dto.setCessAmountAdvalorem(BigDecimal.valueOf(200.0));
			dto.setCessRateAdvalorem(BigDecimal.valueOf(10.0));
			dto.setCessRateSpecific(BigDecimal.valueOf(25.0));
			dto.setCgstAmount(BigDecimal.valueOf(32.0));
			dto.setCgstin("33GSPTN0481G1ZA");
			dto.setCgstRate(BigDecimal.valueOf(12.0));
			dto.setCrdPreGst("33gspt");
			dto.setCustomerCode("1232");
			dto.setCustomerNum("12345");
			dto.setDocDate(LocalDate.now());
			dto.setDocNum("123451");
			dto.setDocType("INV");
			dto.setEgstin("33GSPTN0481G1ZA");
			dto.setErrorCode("ER061");
			dto.setExportDuty("Export");
			dto.setFob("aaa");
			dto.setFob("33GSPTN0481G1ZA");
			dto.setHsnSac("9912345");
			dto.setIgstAmount(BigDecimal.valueOf(10.0));
			dto.setIgstRate(BigDecimal.valueOf(21.0));
			dto.setInvoiceValue(BigDecimal.valueOf(56.0));
			dto.setItcFlag(false);
			dto.setItemCategory("metal");
			dto.setItemCode("191");
			dto.setItemDescp("metal");
			dto.setLineNumber(1);
			dto.setOriginalCgstin("33GSPTN0481G1ZA");
			dto.setOriginalDocDate(LocalDate.now());
			dto.setOriginalDocNum("1234");
			dto.setPortCode("99");
			dto.setPos("33");
			dto.setQtySupplied("100");
			dto.setReasonFrCrDbNote("credit");
			dto.setReverseCharge(false);
			dto.setSgstAmount(BigDecimal.valueOf(13.0));
			dto.setSgstRate(BigDecimal.valueOf(47.0));
			dto.setShippingBillDate(LocalDate.now());
			dto.setShippingBillNo("5465");
			dto.setShipToState("50");
			dto.setSupplyType("TAX");
			dto.setTaxableValue(BigDecimal.valueOf(34.0));
			dto.setUinOrComposition("uin");
			dto.setUom("kg");
			dto.setUserdefinedField1("user1");
			dto.setUserdefinedField2("user2");
			dto.setUserdefinedField3("user3");
			respList.add(dto);
			
			Gstr2AspInnerErrorDto dto1 = new Gstr2AspInnerErrorDto();

			dto1.setAccountVoucherDate(LocalDate.now());
			dto1.setAccountVoucherNum("123126");
			dto1.setBillToState("State1");
			dto1.setCessAmountAdvalorem(BigDecimal.valueOf(200.0));
			dto1.setCessRateAdvalorem(BigDecimal.valueOf(10.0));
			dto1.setCessRateSpecific(BigDecimal.valueOf(25.0));
			dto1.setCgstAmount(BigDecimal.valueOf(32.0));
			dto1.setCgstin("33GSPTN0481G1ZA");
			dto1.setCgstRate(BigDecimal.valueOf(12.0));
			dto1.setCrdPreGst("33gspt");
			dto1.setCustomerCode("1232");
			dto1.setCustomerNum("12345");
			dto1.setDocDate(LocalDate.now());
			dto1.setDocNum("123451");
			dto1.setDocType("INV");
			dto1.setEgstin("33GSPTN0481G1ZA");
			dto1.setErrorCode("ER061");
			dto1.setExportDuty("Export");
			dto1.setFob("aaa");
			dto1.setFob("33GSPTN0481G1ZA");
			dto1.setHsnSac("9912345");
			dto1.setIgstAmount(BigDecimal.valueOf(10.0));
			dto1.setIgstRate(BigDecimal.valueOf(21.0));
			dto1.setInvoiceValue(BigDecimal.valueOf(56.0));
			dto1.setItcFlag(false);
			dto1.setItemCategory("metal");
			dto1.setItemCode("191");
			dto1.setItemDescp("metal");
			dto1.setLineNumber(2);
			dto1.setOriginalCgstin("33GSPTN0481G1ZA");
			dto1.setOriginalDocDate(LocalDate.now());
			dto1.setOriginalDocNum("1234");
			dto1.setPortCode("99");
			dto1.setPos("33");
			dto1.setQtySupplied("100");
			dto1.setReasonFrCrDbNote("credit");
			dto1.setReverseCharge(false);
			dto1.setSgstAmount(BigDecimal.valueOf(13.0));
			dto1.setSgstRate(BigDecimal.valueOf(47.0));
			dto1.setShippingBillDate(LocalDate.now());
			dto1.setShippingBillNo("5465");
			dto1.setShipToState("50");
			dto1.setSupplyType("TAX");
			dto1.setTaxableValue(BigDecimal.valueOf(34.0));
			dto1.setUinOrComposition("uin");
			dto1.setUom("kg");
			dto1.setUserdefinedField1("user1");
			dto1.setUserdefinedField2("user2");
			dto1.setUserdefinedField3("user3");
			respList.add(dto1);
			
			Gstr2AspInnerErrorDto dto2 = new Gstr2AspInnerErrorDto();

			dto2.setAccountVoucherDate(LocalDate.now());
			dto2.setAccountVoucherNum("123126");
			dto2.setBillToState("State1");
			dto2.setCessAmountAdvalorem(BigDecimal.valueOf(200.0));
			dto2.setCessRateAdvalorem(BigDecimal.valueOf(10.0));
			dto2.setCessRateSpecific(BigDecimal.valueOf(25.0));
			dto2.setCgstAmount(BigDecimal.valueOf(32.0));
			dto2.setCgstin("33GSPTN0481G1ZA");
			dto2.setCgstRate(BigDecimal.valueOf(12.0));
			dto2.setCrdPreGst("33gspt");
			dto2.setCustomerCode("1232");
			dto2.setCustomerNum("12345");
			dto2.setDocDate(LocalDate.now());
			dto2.setDocNum("123451");
			dto2.setDocType("INV");
			dto2.setEgstin("33GSPTN0481G1ZA");
			dto2.setErrorCode("ER061");
			dto2.setExportDuty("Export");
			dto2.setFob("aaa");
			dto2.setFob("33GSPTN0481G1ZA");
			dto2.setHsnSac("9912345");
			dto2.setIgstAmount(BigDecimal.valueOf(10.0));
			dto2.setIgstRate(BigDecimal.valueOf(21.0));
			dto2.setInvoiceValue(BigDecimal.valueOf(56.0));
			dto2.setItcFlag(false);
			dto2.setItemCategory("metal");
			dto2.setItemCode("191");
			dto2.setItemDescp("metal");
			dto2.setLineNumber(3);
			dto2.setOriginalCgstin("33GSPTN0481G1ZA");
			dto2.setOriginalDocDate(LocalDate.now());
			dto2.setOriginalDocNum("1234");
			dto2.setPortCode("99");
			dto2.setPos("33");
			dto2.setQtySupplied("100");
			dto2.setReasonFrCrDbNote("credit");
			dto2.setReverseCharge(false);
			dto2.setSgstAmount(BigDecimal.valueOf(13.0));
			dto2.setSgstRate(BigDecimal.valueOf(47.0));
			dto2.setShippingBillDate(LocalDate.now());
			dto2.setShippingBillNo("5465");
			dto2.setShipToState("50");
			dto2.setSupplyType("TAX");
			dto2.setTaxableValue(BigDecimal.valueOf(34.0));
			dto2.setUinOrComposition("uin");
			dto2.setUom("kg");
			dto2.setUserdefinedField1("user1");
			dto2.setUserdefinedField2("user2");
			dto2.setUserdefinedField3("user3");
			respList.add(dto2);
			
			Gstr2AspInnerErrorDto dto3 = new Gstr2AspInnerErrorDto();
			
			dto3.setAccountVoucherDate(LocalDate.now());
			dto3.setAccountVoucherNum("123126");
			dto3.setBillToState("State1");
			dto3.setCessAmountAdvalorem(BigDecimal.valueOf(200.0));
			dto3.setCessRateAdvalorem(BigDecimal.valueOf(10.0));
			dto3.setCessRateSpecific(BigDecimal.valueOf(25.0));
			dto3.setCgstAmount(BigDecimal.valueOf(32.0));
			dto3.setCgstin("33GSPTN0481G1ZA");
			dto3.setCgstRate(BigDecimal.valueOf(12.0));
			dto3.setCrdPreGst("33gspt");
			dto3.setCustomerCode("1232");
			dto3.setCustomerNum("12345");
			dto3.setDocDate(LocalDate.now());
			dto3.setDocNum("123451");
			dto3.setDocType("INV");
			dto3.setEgstin("33GSPTN0481G1ZA");
			dto3.setErrorCode("ER061");
			dto3.setExportDuty("Export");
			dto3.setFob("aaa");
			dto3.setFob("33GSPTN0481G1ZA");
			dto3.setHsnSac("9912345");
			dto3.setIgstAmount(BigDecimal.valueOf(10.0));
			dto3.setIgstRate(BigDecimal.valueOf(21.0));
			dto3.setInvoiceValue(BigDecimal.valueOf(56.0));
			dto3.setItcFlag(false);
			dto3.setItemCategory("metal");
			dto3.setItemCode("191");
			dto3.setItemDescp("metal");
			dto3.setLineNumber(4);
			dto3.setOriginalCgstin("33GSPTN0481G1ZA");
			dto3.setOriginalDocDate(LocalDate.now());
			dto3.setOriginalDocNum("1234");
			dto3.setPortCode("99");
			dto3.setPos("33");
			dto3.setQtySupplied("100");
			dto3.setReasonFrCrDbNote("credit");
			dto3.setReverseCharge(false);
			dto3.setSgstAmount(BigDecimal.valueOf(13.0));
			dto3.setSgstRate(BigDecimal.valueOf(47.0));
			dto3.setShippingBillDate(LocalDate.now());
			dto3.setShippingBillNo("5465");
			dto3.setShipToState("50");
			dto3.setSupplyType("TAX");
			dto3.setTaxableValue(BigDecimal.valueOf(34.0));
			dto3.setUinOrComposition("uin");
			dto3.setUom("kg");
			dto3.setUserdefinedField1("user1");
			dto3.setUserdefinedField2("user2");
			dto3.setUserdefinedField3("user3");
			respList.add(dto3);
			
			Gstr2AspInnerErrorDto dto4 = new Gstr2AspInnerErrorDto();

			dto4.setAccountVoucherDate(LocalDate.now());
			dto4.setAccountVoucherNum("123126");
			dto4.setBillToState("State1");
			dto4.setCessAmountAdvalorem(BigDecimal.valueOf(200.0));
			dto4.setCessRateAdvalorem(BigDecimal.valueOf(10.0));
			dto4.setCessRateSpecific(BigDecimal.valueOf(25.0));
			dto4.setCgstAmount(BigDecimal.valueOf(32.0));
			dto4.setCgstin("33GSPTN0481G1ZA");
			dto4.setCgstRate(BigDecimal.valueOf(12.0));
			dto4.setCrdPreGst("33gspt");
			dto4.setCustomerCode("1232");
			dto4.setCustomerNum("12345");
			dto4.setDocDate(LocalDate.now());
			dto4.setDocNum("123451");
			dto4.setDocType("INV");
			dto4.setEgstin("33GSPTN0481G1ZA");
			dto4.setErrorCode("ER061");
			dto4.setExportDuty("Export");
			dto4.setFob("aaa");
			dto4.setFob("33GSPTN0481G1ZA");
			dto4.setHsnSac("9912345");
			dto4.setIgstAmount(BigDecimal.valueOf(10.0));
			dto4.setIgstRate(BigDecimal.valueOf(21.0));
			dto4.setInvoiceValue(BigDecimal.valueOf(56.0));
			dto4.setItcFlag(false);
			dto4.setItemCategory("metal");
			dto4.setItemCode("191");
			dto4.setItemDescp("metal");
			dto4.setLineNumber(1);
			dto4.setOriginalCgstin("33GSPTN0481G1ZA");
			dto4.setOriginalDocDate(LocalDate.now());
			dto4.setOriginalDocNum("1234");
			dto4.setPortCode("99");
			dto4.setPos("33");
			dto4.setQtySupplied("100");
			dto4.setReasonFrCrDbNote("credit");
			dto4.setReverseCharge(false);
			dto4.setSgstAmount(BigDecimal.valueOf(13.0));
			dto4.setSgstRate(BigDecimal.valueOf(47.0));
			dto4.setShippingBillDate(LocalDate.now());
			dto4.setShippingBillNo("5465");
			dto4.setShipToState("50");
			dto4.setSupplyType("TAX");
			dto4.setTaxableValue(BigDecimal.valueOf(34.0));
			dto4.setUinOrComposition("uin");
			dto4.setUom("kg");
			dto4.setUserdefinedField1("user1");
			dto4.setUserdefinedField2("user2");
			dto4.setUserdefinedField3("user3");
			respList.add(dto4);



			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respList));

			return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting DataStatus for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

}
