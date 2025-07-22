package com.ey.advisory.app.services.jobs.gstr2a;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.docs.dto.IsdInvoiceData;
import com.ey.advisory.app.docs.dto.IsdInvoices;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Santosh.Gururaj
 *
 */
@Slf4j
@Component("gstr2aIsdIsdaDataParserImpl")
public class Gstr2aIsdIsdaDataParserImpl implements Gstr2aIsdIsdaDataParser {
	
	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> docKeyGenerator;
	
	@Override
	public List<GetGstr2aStagingIsdInvoicesHeaderEntity> parseIsdData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId) {

		List<GetGstr2aStagingIsdInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonObject = null;
		Type listType = null;
		//try {
			jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
						.get(APIConstants.ISD).getAsJsonArray();

			
			listType = new TypeToken<List<IsdInvoices>>() {
			}.getType();

			List<IsdInvoices> baseEntities = gson.fromJson(jsonObject,
					listType);
			baseEntities.forEach(baseEntity -> {
				List<IsdInvoiceData> isdinv = baseEntity.getIsdInvoiceData();
				for (IsdInvoiceData is : isdinv) {

					List<GetGstr2aStagingIsdInvoicesItemEntity> itemList = new ArrayList<>();
					GetGstr2aStagingIsdInvoicesHeaderEntity invoice = 
							new GetGstr2aStagingIsdInvoicesHeaderEntity();
					
					invoiceList.add(invoice);
					// IsdInvoiceData dto1=new IsdInvoiceData();
					invoice.setIsdBatchIdGstr2a(batchId);
					invoice.setGstin(dto.getGstin());
					invoice.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						invoice.setDerReturnPeriod(
								GenUtil.convertTaxPeriodToInt(
										dto.getReturnPeriod()));
					}
					invoice.setCgstin(baseEntity.getCgstin());
					invoice.setCounFillStatus(baseEntity.getCfs());
					//entity.setCountergstin();
					invoice.setDocumentNumber(is.getDocumentNumber());
					
					if (is.getDocumentDate() != null && is.getDocumentDate()
							.trim().length() > 0) {
						invoice.setDocumentDate(LocalDate.
								parse(is.getDocumentDate(),
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					invoice.setIsdDocumentType(is.getDocumentType());
					invoice.setItcElg(is.getItcEligible());
					invoice.setIgstamt(is.getIgst());
					invoice.setCgstamt(is.getCgst());
					invoice.setSgstamt(is.getSgst());
					invoice.setCesamt(is.getCess());
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					invoice.setCreatedOn(convertNow);
					invoice.setCreatedBy("SYSTEM");
					
					//adding inv_key
					String docType = is.getDocumentType() == "R" ? "INV"
							: (is.getDocumentType() == "C" ? "CR"
									: (is.getDocumentType() == "D" ? "DR"
											: (is.getDocumentType() == "B" ? "BOS"
													: is.getDocumentType())));
					String fy = GenUtil.getFinYear(LocalDate.parse(is.getDocumentDate(), DateUtil.SUPPORTED_DATE_FORMAT2));
					
					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(is.getDocumentNumber());
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(baseEntity.getCgstin());//output
					inwardDoc.setCgstin(dto.getGstin());//input
					
					String generateKey = docKeyGenerator.generateKey(inwardDoc);
					
					//String generateKey = docKeyGenerator.generateKey(baseEntity.getCgstin(), is.getDocumentNumber(), fy, docType);
					invoice.setInvKey(generateKey);
					//set header data in item table as well
					GetGstr2aStagingIsdInvoicesItemEntity item = new GetGstr2aStagingIsdInvoicesItemEntity();
					item.setIgstAmt(is.getIgst());
					item.setSgstAmt(is.getSgst());
					item.setCgstAmt(is.getCgst());
					item.setCessAmt(is.getCess()	);
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						item.setDerReturnPeriod(
								GenUtil.convertTaxPeriodToInt(
										dto.getReturnPeriod()));
					}
					itemList.add(item);
					invoice.setLineItems(itemList);
					itemList.forEach(itm -> {
						itm.setHeader(invoice);
					});
					
				}
			});

		/*} catch (Exception e) {
			String msg = "Failed to parse ISD and ISDA Data";
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(msg, e);
			}
		}*/
		return invoiceList;
	}


	@Override
	public List<GetGstr2aStagingIsdaInvoicesHeaderEntity> parseIsdaData(
			Gstr1GetInvoicesReqDto dto, String apiResp, String type,
			Long batchId) {
		
		List<GetGstr2aStagingIsdaInvoicesItemEntity> itemList = new ArrayList<>();
		List<GetGstr2aStagingIsdaInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonObject = null;
		Type listType = null;
		//try {
			
				jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
						.get(APIConstants.ISDA).getAsJsonArray();
			
		listType = new TypeToken<List<IsdInvoices>>() {
		}.getType();

		List<IsdInvoices> baseEntities = gson.fromJson(jsonObject,
				listType);
		baseEntities.forEach(baseEntity -> {
			List<IsdInvoiceData> isdinv = baseEntity.getIsdInvoiceData();
			for (IsdInvoiceData is : isdinv) {

				GetGstr2aStagingIsdaInvoicesHeaderEntity invoice = 
						new GetGstr2aStagingIsdaInvoicesHeaderEntity();
				
				invoiceList.add(invoice);
				// IsdInvoiceData dto1=new IsdInvoiceData();
				invoice.setIsdaBatchIdGstr2a(batchId);
				invoice.setGstin(dto.getGstin());
				invoice.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					invoice.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(
									dto.getReturnPeriod()));
				}
				invoice.setCgstin(baseEntity.getCgstin());
				invoice.setCounFillStatus(baseEntity.getCfs());
				// entity.setCountergstin();
				invoice.setDocumentNumber(is.getDocumentNumber());
				if (is.getDocumentDate() != null && is.getDocumentDate()
						.trim().length() > 0) {
					invoice.setDocumentDate(LocalDate.parse(is.getDocumentDate(),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				} 
				invoice.setIsdDocumentType(is.getDocumentType());
				invoice.setItcElg(is.getItcEligible());
				invoice.setIgstamt(is.getIgst());
				invoice.setCgstamt(is.getCgst());
				invoice.setSgstamt(is.getSgst());
				invoice.setCesamt(is.getCess());
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				invoice.setCreatedOn(convertNow);
				invoice.setCreatedBy("SYSTEM");
				//adding inv_key
				String docType = is.getDocumentType() == "R" ? "INV"
						: (is.getDocumentType() == "C" ? "CR"
								: (is.getDocumentType() == "D" ? "DR"
										: (is.getDocumentType() == "B" ? "BOS"
												: is.getDocumentType())));
				String fy = GenUtil.getFinYear(LocalDate.parse(is.getDocumentDate(), DateUtil.SUPPORTED_DATE_FORMAT2));
				
				InwardTransDocument inwardDoc = new InwardTransDocument();
				inwardDoc.setFinYear(fy);
				inwardDoc.setDocNo(is.getDocumentNumber());
				inwardDoc.setDocType(docType);
				inwardDoc.setSgstin(baseEntity.getCgstin());//output
				inwardDoc.setCgstin(dto.getGstin());//input
				
				String generateKey = docKeyGenerator.generateKey(inwardDoc);
				//String generateKey = docKeyGenerator.generateKey(baseEntity.getCgstin(), is.getDocumentNumber(), fy, docType);
				invoice.setInvKey(generateKey);	
				//set header data in item table as well
				GetGstr2aStagingIsdaInvoicesItemEntity item = new GetGstr2aStagingIsdaInvoicesItemEntity();
				item.setIgstAmt(is.getIgst());
				item.setSgstAmt(is.getSgst());
				item.setCgstAmt(is.getCgst());
				item.setCessAmt(is.getCess()	);
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					item.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(
									dto.getReturnPeriod()));
				}
				itemList.add(item);
				invoice.setLineItems(itemList);
				itemList.forEach(itm -> {
					itm.setHeader(invoice);
				});
			}
		});
	/*} catch (Exception e) {
		String msg = "Failed to parse ISD and ISDA Data";
		if (LOGGER.isErrorEnabled()) {
			LOGGER.error(msg, e);
		}
	}*/
	return invoiceList;
	}
}
