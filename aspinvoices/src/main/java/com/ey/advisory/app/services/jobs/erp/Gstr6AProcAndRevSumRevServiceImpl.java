package com.ey.advisory.app.services.jobs.erp;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcessSummaryRespDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6AProcessedDataResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataRequestDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ASummaryDataResponseDto;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6AProcessedDataServiceImpl;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ASummaryDataDaoImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DestinationConnectivity;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

@Service("Gstr6AProcAndRevSumRevServiceImpl")
public class Gstr6AProcAndRevSumRevServiceImpl
		implements Gstr6AProcAndRevSumRevService {

	@Autowired
	private DestinationConnectivity connectivity;

	@Autowired
	@Qualifier("Gstr6AProcessedDataServiceImpl")
	private Gstr6AProcessedDataServiceImpl gstr6AProcessedDataService;

	@Autowired
	@Qualifier("Gstr6ASummaryDataDaoImpl")
	private Gstr6ASummaryDataDaoImpl gstr6ASummaryDataDao;

	private static Logger LOGGER = LoggerFactory
			.getLogger(Gstr6AProcAndRevSumRevServiceImpl.class);

	public List<Gstr6AProcessSummaryRespDto> convertProcessSummary(
			String gstin) throws Exception {

		Gstr6AProcessedDataRequestDto criteria = new Gstr6AProcessedDataRequestDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		criteria.setDataSecAttrs(dataSecAttrs);
		List<Gstr6AProcessedDataResponseDto> gstr6AProceDataResps = gstr6AProcessedDataService
				.getGstr6AProcessedData(criteria);
		List<Gstr6AProcessSummaryRespDto> respDtos = new ArrayList<>();
		gstr6AProceDataResps.forEach(gstr6AProceDataResp -> {
			Gstr6AProcessSummaryRespDto respDto = new Gstr6AProcessSummaryRespDto();
			respDto.setRetPeriod(gstr6AProceDataResp.getRetPeriod());
			respDto.setGstin(gstr6AProceDataResp.getGstin());
			respDto.setState(gstr6AProceDataResp.getState());
			respDto.setAuthToken(gstr6AProceDataResp.getAuthToken());
			respDto.setGstrStatus(gstr6AProceDataResp.getGstrStatus());
			respDto.setStatus(gstr6AProceDataResp.getStatus());
			respDto.setCount(gstr6AProceDataResp.getCount());
			respDto.setInVoiceVal(gstr6AProceDataResp.getInVoiceVal());
			respDto.setTaxableValue(gstr6AProceDataResp.getTaxableValue());
			respDto.setTotalTax(gstr6AProceDataResp.getTotalTax());
			respDto.setIgst(gstr6AProceDataResp.getIgst());
			respDto.setCgst(gstr6AProceDataResp.getCgst());
			respDto.setSgst(gstr6AProceDataResp.getSgst());
			respDto.setCess(gstr6AProceDataResp.getCess());
			respDtos.add(respDto);
		});
		return respDtos;
	}

	public List<Gstr6AReviewSummaryRespDto> convertReviewSummary(String gstin,
			String retPeriod) {
		Gstr6ASummaryDataRequestDto criteria = new Gstr6ASummaryDataRequestDto();
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		criteria.setDataSecAttrs(dataSecAttrs);
		criteria.setTaxPeriod(retPeriod);
		List<Gstr6AReviewSummaryRespDto> gstr6AProcSumRespDtos = new ArrayList<>();
		List<Gstr6ASummaryDataResponseDto> dataRespDtos = gstr6ASummaryDataDao
				.getGstr6ASummaryData(criteria);
		dataRespDtos.forEach(dataRespDto -> {
			Gstr6AReviewSummaryRespDto sumRespDto = new Gstr6AReviewSummaryRespDto();
			sumRespDto.setTableType("Gstr6");
			sumRespDto.setDocumentType(dataRespDto.getDocType());
			sumRespDto.setDocCount(dataRespDto.getCount());
			sumRespDto.setInvoiceValue(dataRespDto.getInVoiceVal());
			sumRespDto.setTaxableValue(dataRespDto.getTaxableValue());
			sumRespDto.setTotalTax(dataRespDto.getTotalTax());
			sumRespDto.setIgst(dataRespDto.getIgst());
			sumRespDto.setCgst(dataRespDto.getCgst());
			sumRespDto.setSgst(dataRespDto.getSgst());
			sumRespDto.setCess(dataRespDto.getCess());
			gstr6AProcSumRespDtos.add(sumRespDto);
		});
		return gstr6AProcSumRespDtos;
	}

	public Integer pushToErp(Gstr6AProcReviewHeaderSummaryRequestDto reqDto,
			String destName, AnxErpBatchEntity batch) {
		Integer response = 0;
		try {
			ByteArrayOutputStream headerOut = new ByteArrayOutputStream();
			JAXBContext contextHeader = JAXBContext
					.newInstance(Gstr6AProcReviewHeaderSummaryRequestDto.class);
			Marshaller headerMarshaller = contextHeader.createMarshaller();
			headerMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			headerMarshaller.marshal(reqDto, headerOut);
			String headerXml = headerOut.toString();
			if (headerXml != null && headerXml.length() > 0) {
				headerXml = headerXml.substring(headerXml.indexOf('\n') + 1);
			}

			String header = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
					+ "xmlns:urn='urn:sap-com:document:sap:soap:functions:mc-style'>"
					+ "<soapenv:Header/><soapenv:Body>";
			String footer = "</urn:ZeydigiGstr6aSummary></soapenv:Body></soapenv:Envelope>";
			String xml = null;
			if (headerXml != null) {
				xml = header + headerXml + footer;
			}
			if (xml != null && destName != null) {
			//	connectivity.post(destName, xml, batch);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{} ", e);
		}
		return response;
	}
}
