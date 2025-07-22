package com.ey.advisory.app.service.ims.supplier;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1AInvoiceB2BAHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1AInvoiceB2BHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1AInvoiceCDNRAHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1AInvoiceCDNRHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1AInvoiceECOMAHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1AInvoiceECOMHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1InvoiceB2BAHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1InvoiceB2BHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1InvoiceCDNRAHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1InvoiceCDNRHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1InvoiceECOMAHeaderStagingRepository;
import com.ey.advisory.app.data.repositories.client.supplier.ims.SupplierImsGstr1InvoiceECOMHeaderStagingRepository;
import com.ey.advisory.app.ims.handlers.ImsInvoicesDataParser;
import com.ey.advisory.app.ims.handlers.ImsInvoicesProcCallService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Vishal.verma
 *
 */
@Slf4j
@Service("SupplierImsInvoicesDataParserImpl")
@Transactional(value = "clientTransactionManager")
public class SupplierImsInvoicesDataParserImpl
		implements ImsInvoicesDataParser {

	@Autowired
	private SupplierImsGstr1AInvoiceB2BHeaderStagingRepository gst1aB2bRepo;

	@Autowired
	private SupplierImsGstr1InvoiceB2BHeaderStagingRepository gst1B2bRepo;

	@Autowired
	private SupplierImsGstr1AInvoiceB2BAHeaderStagingRepository gst1aB2baRepo;

	@Autowired
	private SupplierImsGstr1InvoiceB2BAHeaderStagingRepository gst1B2baRepo;

	@Autowired
	private SupplierImsGstr1AInvoiceCDNRHeaderStagingRepository gst1aCdnrRepo;

	@Autowired
	private SupplierImsGstr1InvoiceCDNRHeaderStagingRepository gst1CdnrRepo;

	@Autowired
	private SupplierImsGstr1AInvoiceCDNRAHeaderStagingRepository gst1aCdnraRepo;

	@Autowired
	private SupplierImsGstr1InvoiceCDNRAHeaderStagingRepository gst1CdnraRepo;

	@Autowired
	private SupplierImsGstr1AInvoiceECOMHeaderStagingRepository gst1aEcomRepo;

	@Autowired
	private SupplierImsGstr1InvoiceECOMHeaderStagingRepository gst1EcomRepo;

	@Autowired
	private SupplierImsGstr1AInvoiceECOMAHeaderStagingRepository gst1aEcomaRepo;

	@Autowired
	private SupplierImsGstr1InvoiceECOMAHeaderStagingRepository gst1EcomaRepo;

	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;

	@Autowired
	@Qualifier("SupplierImsDupCheckJsonUtil")
	private SupplierImsDupCheckJsonUtil dupCheckHashService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CDNR, APIConstants.IMS_TYPE_CDNRA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	private static final String DOC_KEY_JOINER = "|";

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	@Override
	public void parseImsInvoicesData(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId, String jsonString) {
		TenantContext.setTenantId(dto.getGroupcode());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET IMS Invoices inside  ImsInvoicesDataParserImpl for batch id {} ",
					batchId);
		}
		List<String> resplist = new ArrayList<>();
		String jsonHashCode = null;

		if (Strings.isNullOrEmpty(jsonString)) {
			for (Long id : resultIds) {

				String apiResp = APIInvokerUtil.getResultById(id);

				resplist.add(apiResp);
			}

		} else {
			String apiResp = jsonString;
			resplist.add(apiResp);
		}

		// dupcheck in java

		// get Hash from batchTable ie jsonHashCode2and compare
		// if(jsonHashCode = jsonHashCode2){
		// return

		jsonHashCode = dupCheckHashService.supplierImsJsonDupCheck(
				dto.getGstin(), dto.getReturnPeriod(), dto.getSection(),
				dto.getReturnType(), resplist);

		List<GetAnx1BatchEntity> batchEntities = batchRepo.findImsStatusForDuplicateCheck(
				dto.getGstin(), dto.getReturnPeriod(), dto.getSection(),
				dto.getReturnType());
		GetAnx1BatchEntity entity = batchEntities.get(0);
		
		batchRepo.updateHashSupplierIms(jsonHashCode,dto.getSection(), dto.getGstin(),
				dto.getReturnPeriod(), dto.getReturnType());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Updated the new Hash in batch table{} ",
					entity);
		}
		
		if (entity.getHashKey() != null && jsonHashCode.equalsIgnoreCase(entity.getHashKey())) {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Same hashKey found,no new invoice is there,Hence returning{} ",
						entity);
			}
			return;

		}
			

		if (Strings.isNullOrEmpty(jsonString)) {
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if ("GSTR1".equalsIgnoreCase(dto.getReturnType())) {

					if (GETIMS_SUPPLY_TYPES.contains(dto.getSection())) {

						JsonObject respObject = JsonParser.parseString(apiResp)
								.getAsJsonObject();
						Gson gson = new Gson();

						SupplierGetImsInvoicesSectionDtlsDto reqDto = gson
								.fromJson(respObject,
										SupplierGetImsInvoicesSectionDtlsDto.class);

						Gstr1DTO gstr1 = reqDto.getGstr1();

						switch (dto.getSection().toUpperCase()) {
						case "B2B":
							List<SupplierImsGstr1B2BHeaderStaggingEntity> b2bEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getB2b(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1B2BHeaderStaggingEntity.class,
									SupplierImsGstr1B2BItemStaggingEntity.class);
							gst1B2bRepo.saveAll(b2bEntity);

							break;
						case "B2BA":

							List<SupplierImsGstr1B2BAHeaderStaggingEntity> b2baEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getB2ba(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1B2BAHeaderStaggingEntity.class,
									SupplierImsGstr1B2BAItemStaggingEntity.class);
							gst1B2baRepo.saveAll(b2baEntity);
							break;
						case "CDNR":

							List<SupplierImsGstr1CDNRHeaderStaggingEntity> cnEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getCdnr(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1CDNRHeaderStaggingEntity.class,
									SupplierImsGstr1CDNRItemStaggingEntity.class);
							gst1CdnrRepo.saveAll(cnEntity);
							break;
						case "CDNRA":

							List<SupplierImsGstr1CDNRAHeaderStaggingEntity> cnaEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getCdnra(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1CDNRAHeaderStaggingEntity.class,
									SupplierImsGstr1CDNRAItemStaggingEntity.class);
							gst1CdnraRepo.saveAll(cnaEntity);
							break;

						case "ECOM":

							List<B2BDataDTO> b2b = gstr1.getEcom().getB2b();
							List<B2BDataDTO> urp = gstr1.getEcom().getUrp2b();

							List<SupplierImsGstr1ECOMHeaderStaggingEntity> ecomEntity = new ArrayList<>();
							List<SupplierImsGstr1ECOMHeaderStaggingEntity> ecomEntity1 = parseImsInvoicesData(
									dto, reqDto, b2b, dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1ECOMHeaderStaggingEntity.class,
									SupplierImsGstr1ECOMItemStaggingEntity.class);

							List<SupplierImsGstr1ECOMHeaderStaggingEntity> ecomEntity2 = parseImsInvoicesData(
									dto, reqDto, urp, dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1ECOMHeaderStaggingEntity.class,
									SupplierImsGstr1ECOMItemStaggingEntity.class);

							ecomEntity.addAll(ecomEntity1);
							ecomEntity.addAll(ecomEntity2);
							gst1EcomRepo.saveAll(ecomEntity);
							break;
						case "ECOMA":

							List<B2BDataDTO> b2ba = gstr1.getEcoma().getB2b();
							List<B2BDataDTO> urpa = gstr1.getEcoma().getUrp2b();

							List<SupplierImsGstr1ECOMAHeaderStaggingEntity> ecomaEntity = new ArrayList<>();
							List<SupplierImsGstr1ECOMAHeaderStaggingEntity> ecomaEntity1 = parseImsInvoicesData(
									dto, reqDto, b2ba, dto.getSection(),
									batchId, dto.getGroupcode(),
									SupplierImsGstr1ECOMAHeaderStaggingEntity.class,
									SupplierImsGstr1ECOMAItemStaggingEntity.class);

							List<SupplierImsGstr1ECOMAHeaderStaggingEntity> ecomaEntity2 = parseImsInvoicesData(
									dto, reqDto, urpa, dto.getSection(),
									batchId, dto.getGroupcode(),
									SupplierImsGstr1ECOMAHeaderStaggingEntity.class,
									SupplierImsGstr1ECOMAItemStaggingEntity.class);

							ecomaEntity.addAll(ecomaEntity1);
							ecomaEntity.addAll(ecomaEntity2);
							gst1EcomaRepo.saveAll(ecomaEntity);
							break;
						}
					}

				} else if ("GSTR1A".equalsIgnoreCase(dto.getReturnType())) {

					if (GETIMS_SUPPLY_TYPES.contains(dto.getSection())) {

						JsonObject respObject = JsonParser.parseString(apiResp)
								.getAsJsonObject();
						Gson gson = new Gson();

						SupplierGetImsInvoicesSectionDtlsDto reqDto = gson
								.fromJson(respObject,
										SupplierGetImsInvoicesSectionDtlsDto.class);

						Gstr1DTO gstr1 = reqDto.getGstr1a();

						switch (dto.getSection().toUpperCase()) {
						case "B2B":
							List<SupplierImsGstr1AB2BHeaderStaggingEntity> b2bEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getB2b(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1AB2BHeaderStaggingEntity.class,
									SupplierImsGstr1AB2BItemStaggingEntity.class);
							gst1aB2bRepo.saveAll(b2bEntity);

							break;
						case "B2BA":

							List<SupplierImsGstr1AB2BAHeaderStaggingEntity> b2baEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getB2ba(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1AB2BAHeaderStaggingEntity.class,
									SupplierImsGstr1AB2BAItemStaggingEntity.class);
							gst1aB2baRepo.saveAll(b2baEntity);
							break;
						case "CDNR":

							List<SupplierImsGstr1ACDNRHeaderStaggingEntity> cnEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getCdnr(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1ACDNRHeaderStaggingEntity.class,
									SupplierImsGstr1ACDNRItemStaggingEntity.class);
							gst1aCdnrRepo.saveAll(cnEntity);
							break;
						case "CDNRA":

							List<SupplierImsGstr1ACDNRAHeaderStaggingEntity> cnaEntity = parseImsInvoicesData(
									dto, reqDto, gstr1.getCdnra(),
									dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1ACDNRAHeaderStaggingEntity.class,
									SupplierImsGstr1ACDNRAItemStaggingEntity.class);
							gst1aCdnraRepo.saveAll(cnaEntity);
							break;

						case "ECOM":

							List<B2BDataDTO> b2b = gstr1.getEcom().getB2b();
							List<B2BDataDTO> urp = gstr1.getEcom().getUrp2b();

							List<SupplierImsGstr1AECOMHeaderStaggingEntity> ecomEntity = new ArrayList<>();
							List<SupplierImsGstr1AECOMHeaderStaggingEntity> ecomEntity1 = parseImsInvoicesData(
									dto, reqDto, b2b, dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1AECOMHeaderStaggingEntity.class,
									SupplierImsGstr1AECOMItemStaggingEntity.class);

							List<SupplierImsGstr1AECOMHeaderStaggingEntity> ecomEntity2 = parseImsInvoicesData(
									dto, reqDto, urp, dto.getSection(), batchId,
									dto.getGroupcode(),
									SupplierImsGstr1AECOMHeaderStaggingEntity.class,
									SupplierImsGstr1AECOMItemStaggingEntity.class);

							ecomEntity.addAll(ecomEntity1);
							ecomEntity.addAll(ecomEntity2);
							gst1aEcomRepo.saveAll(ecomEntity);
							break;
						case "ECOMA":

							List<B2BDataDTO> b2ba = gstr1.getEcoma().getB2b();
							List<B2BDataDTO> urpa = gstr1.getEcoma().getUrp2b();

							List<SupplierImsGstr1AECOMAHeaderStaggingEntity> ecomaEntity = new ArrayList<>();
							List<SupplierImsGstr1AECOMAHeaderStaggingEntity> ecomaEntity1 = parseImsInvoicesData(
									dto, reqDto, b2ba, dto.getSection(),
									batchId, dto.getGroupcode(),
									SupplierImsGstr1AECOMAHeaderStaggingEntity.class,
									SupplierImsGstr1AECOMAItemStaggingEntity.class);

							List<SupplierImsGstr1AECOMAHeaderStaggingEntity> ecomaEntity2 = parseImsInvoicesData(
									dto, reqDto, urpa, dto.getSection(),
									batchId, dto.getGroupcode(),
									SupplierImsGstr1AECOMAHeaderStaggingEntity.class,
									SupplierImsGstr1AECOMAItemStaggingEntity.class);

							ecomaEntity.addAll(ecomaEntity1);
							ecomaEntity.addAll(ecomaEntity2);
							gst1aEcomaRepo.saveAll(ecomaEntity);
							break;
						}
					}
				}
			});
		} else {
			String apiResp = jsonString;
			if ("GSTR1".equalsIgnoreCase(dto.getReturnType())) {

				if (GETIMS_SUPPLY_TYPES.contains(dto.getSection())) {

					JsonObject respObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					Gson gson = new Gson();

					SupplierGetImsInvoicesSectionDtlsDto reqDto = gson.fromJson(
							respObject,
							SupplierGetImsInvoicesSectionDtlsDto.class);

					Gstr1DTO gstr1 = reqDto.getGstr1();

					switch (dto.getSection().toUpperCase()) {
					case "B2B":
						List<SupplierImsGstr1B2BHeaderStaggingEntity> b2bEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getB2b(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1B2BHeaderStaggingEntity.class,
								SupplierImsGstr1B2BItemStaggingEntity.class);
						gst1B2bRepo.saveAll(b2bEntity);

						break;
					case "B2BA":

						List<SupplierImsGstr1B2BAHeaderStaggingEntity> b2baEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getB2ba(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1B2BAHeaderStaggingEntity.class,
								SupplierImsGstr1B2BAItemStaggingEntity.class);
						gst1B2baRepo.saveAll(b2baEntity);
						break;
					case "CDNR":

						List<SupplierImsGstr1CDNRHeaderStaggingEntity> cnEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getCdnr(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1CDNRHeaderStaggingEntity.class,
								SupplierImsGstr1CDNRItemStaggingEntity.class);
						gst1CdnrRepo.saveAll(cnEntity);
						break;
					case "CDNRA":

						List<SupplierImsGstr1CDNRAHeaderStaggingEntity> cnaEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getCdnra(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1CDNRAHeaderStaggingEntity.class,
								SupplierImsGstr1CDNRAItemStaggingEntity.class);
						gst1CdnraRepo.saveAll(cnaEntity);
						break;

					case "ECOM":

						List<B2BDataDTO> b2b = gstr1.getEcom().getB2b();
						List<B2BDataDTO> urp = gstr1.getEcom().getUrp2b();

						List<SupplierImsGstr1ECOMHeaderStaggingEntity> ecomEntity = new ArrayList<>();
						List<SupplierImsGstr1ECOMHeaderStaggingEntity> ecomEntity1 = parseImsInvoicesData(
								dto, reqDto, b2b, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1ECOMHeaderStaggingEntity.class,
								SupplierImsGstr1ECOMItemStaggingEntity.class);

						List<SupplierImsGstr1ECOMHeaderStaggingEntity> ecomEntity2 = parseImsInvoicesData(
								dto, reqDto, urp, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1ECOMHeaderStaggingEntity.class,
								SupplierImsGstr1ECOMItemStaggingEntity.class);

						ecomEntity.addAll(ecomEntity1);
						ecomEntity.addAll(ecomEntity2);
						gst1EcomRepo.saveAll(ecomEntity);
						break;
					case "ECOMA":

						List<B2BDataDTO> b2ba = gstr1.getEcoma().getB2b();
						List<B2BDataDTO> urpa = gstr1.getEcoma().getUrp2b();

						List<SupplierImsGstr1ECOMAHeaderStaggingEntity> ecomaEntity = new ArrayList<>();
						List<SupplierImsGstr1ECOMAHeaderStaggingEntity> ecomaEntity1 = parseImsInvoicesData(
								dto, reqDto, b2ba, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1ECOMAHeaderStaggingEntity.class,
								SupplierImsGstr1ECOMAItemStaggingEntity.class);

						List<SupplierImsGstr1ECOMAHeaderStaggingEntity> ecomaEntity2 = parseImsInvoicesData(
								dto, reqDto, urpa, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1ECOMAHeaderStaggingEntity.class,
								SupplierImsGstr1ECOMAItemStaggingEntity.class);

						ecomaEntity.addAll(ecomaEntity1);
						ecomaEntity.addAll(ecomaEntity2);
						gst1EcomaRepo.saveAll(ecomaEntity);
						break;
					}
				}

			} else if ("GSTR1A".equalsIgnoreCase(dto.getReturnType())) {

				if (GETIMS_SUPPLY_TYPES.contains(dto.getSection())) {

					JsonObject respObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					Gson gson = new Gson();

					SupplierGetImsInvoicesSectionDtlsDto reqDto = gson.fromJson(
							respObject,
							SupplierGetImsInvoicesSectionDtlsDto.class);

					Gstr1DTO gstr1 = reqDto.getGstr1a();

					switch (dto.getSection().toUpperCase()) {
					case "B2B":
						List<SupplierImsGstr1AB2BHeaderStaggingEntity> b2bEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getB2b(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1AB2BHeaderStaggingEntity.class,
								SupplierImsGstr1AB2BItemStaggingEntity.class);
						gst1aB2bRepo.saveAll(b2bEntity);

						break;
					case "B2BA":

						List<SupplierImsGstr1AB2BAHeaderStaggingEntity> b2baEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getB2ba(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1AB2BAHeaderStaggingEntity.class,
								SupplierImsGstr1AB2BAItemStaggingEntity.class);
						gst1aB2baRepo.saveAll(b2baEntity);
						break;
					case "CDNR":

						List<SupplierImsGstr1ACDNRHeaderStaggingEntity> cnEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getCdnr(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1ACDNRHeaderStaggingEntity.class,
								SupplierImsGstr1ACDNRItemStaggingEntity.class);
						gst1aCdnrRepo.saveAll(cnEntity);
						break;
					case "CDNRA":

						List<SupplierImsGstr1ACDNRAHeaderStaggingEntity> cnaEntity = parseImsInvoicesData(
								dto, reqDto, gstr1.getCdnra(), dto.getSection(),
								batchId, dto.getGroupcode(),
								SupplierImsGstr1ACDNRAHeaderStaggingEntity.class,
								SupplierImsGstr1ACDNRAItemStaggingEntity.class);
						gst1aCdnraRepo.saveAll(cnaEntity);
						break;

					case "ECOM":

						List<B2BDataDTO> b2b = gstr1.getEcom().getB2b();
						List<B2BDataDTO> urp = gstr1.getEcom().getUrp2b();

						List<SupplierImsGstr1AECOMHeaderStaggingEntity> ecomEntity = new ArrayList<>();
						List<SupplierImsGstr1AECOMHeaderStaggingEntity> ecomEntity1 = parseImsInvoicesData(
								dto, reqDto, b2b, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1AECOMHeaderStaggingEntity.class,
								SupplierImsGstr1AECOMItemStaggingEntity.class);

						List<SupplierImsGstr1AECOMHeaderStaggingEntity> ecomEntity2 = parseImsInvoicesData(
								dto, reqDto, urp, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1AECOMHeaderStaggingEntity.class,
								SupplierImsGstr1AECOMItemStaggingEntity.class);

						ecomEntity.addAll(ecomEntity1);
						ecomEntity.addAll(ecomEntity2);
						gst1aEcomRepo.saveAll(ecomEntity);
						break;
					case "ECOMA":

						List<B2BDataDTO> b2ba = gstr1.getEcoma().getB2b();
						List<B2BDataDTO> urpa = gstr1.getEcoma().getUrp2b();

						List<SupplierImsGstr1AECOMAHeaderStaggingEntity> ecomaEntity = new ArrayList<>();
						List<SupplierImsGstr1AECOMAHeaderStaggingEntity> ecomaEntity1 = parseImsInvoicesData(
								dto, reqDto, b2ba, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1AECOMAHeaderStaggingEntity.class,
								SupplierImsGstr1AECOMAItemStaggingEntity.class);

						List<SupplierImsGstr1AECOMAHeaderStaggingEntity> ecomaEntity2 = parseImsInvoicesData(
								dto, reqDto, urpa, dto.getSection(), batchId,
								dto.getGroupcode(),
								SupplierImsGstr1AECOMAHeaderStaggingEntity.class,
								SupplierImsGstr1AECOMAItemStaggingEntity.class);

						ecomaEntity.addAll(ecomaEntity1);
						ecomaEntity.addAll(ecomaEntity2);
						gst1aEcomaRepo.saveAll(ecomaEntity);
						break;
					}
				}
			}

		}
	}

	/**
	 * @param dto
	 * @param sectionDto
	 * @param dataList
	 * @param type
	 * @param batchId
	 * @param groupcode
	 * @param entityClass
	 * @param entityChildClass
	 * @return
	 */
	private <T, L> List<T> parseImsInvoicesData(Gstr1GetInvoicesReqDto dto,
			SupplierGetImsInvoicesSectionDtlsDto sectionDto,
			List<B2BDataDTO> dataList, String type, //
			Long batchId, String groupcode, Class<T> entityClass,
			Class<L> entityChildClass) {
		List<T> entityList = new ArrayList<>();

		dataList.forEach(data -> {

			data.getInv().forEach(inv -> {

				try {

					List<L> childEntityList = new ArrayList<>();
					T entity = entityClass.newInstance();

					entity.getClass()
							.getMethod("setSupplierGstin", String.class)
							.invoke(entity,
									sectionDto.getGstin() != null
											? sectionDto.getGstin()
											: null);

					entity.getClass().getMethod("setReturnPeriod", String.class)
							.invoke(entity,
									sectionDto.getRtnprd() != null
											? sectionDto.getRtnprd()
											: null);

					entity.getClass()
							.getMethod("setDerivedRetPeriod", Long.class)
							.invoke(entity, Long.valueOf(sectionDto.getRtnprd()
									.substring(2, 6)
									+ sectionDto.getRtnprd().substring(0, 2)));

					if (type.equalsIgnoreCase("ECOM")
							|| type.equalsIgnoreCase("ECOMA")) {

						entity.getClass()
								.getMethod("setSupplyType", String.class)
								.invoke(entity,
										inv.getSply_ty() != null
												? inv.getSply_ty()
												: null);

						entity.getClass()
								.getMethod("setCustomerGstin", String.class)
								.invoke(entity,
										data.getRtin() != null ? data.getRtin()
												: null);

						if (type.equalsIgnoreCase("ECOMA")) {

							entity.getClass()
									.getMethod("setOrtin", String.class)
									.invoke(entity,
											data.getOrtin() != null
													? data.getOrtin()
													: null);
						} else {

							entity.getClass()
									.getMethod("setSupplierGstin", String.class)
									.invoke(entity,
											data.getStin() != null
													? data.getStin()
													: null);

						}
					}

					entity.getClass()
							.getMethod("setCustomerGstin", String.class)
							.invoke(entity,
									data.getCtin() != null ? data.getCtin()
											: null);

					if (type.equalsIgnoreCase("B2B")) {
						entity.getClass().getMethod("setCfs", String.class)
								.invoke(entity,
										data.getCfs() != null ? data.getCfs()
												: null);
					}

					entity.getClass()
							.getMethod("setInvoiceStatus", String.class)
							.invoke(entity,
									inv.getFlag() != null ? inv.getFlag()
											: null);

					entity.getClass()
							.getMethod("setInvoiceNumber", String.class)
							.invoke(entity,
									inv.getInum() != null ? inv.getInum()
											: null);

					entity.getClass().getMethod("setInvoiceDate", Date.class)
							.invoke(entity,
									inv.getIdt() != null
											? new SimpleDateFormat("dd-MM-yyyy")
													.parse(inv.getIdt())
											: null);

					entity.getClass().getMethod("setInvoiceType", String.class)
							.invoke(entity,
									inv.getInv_typ() != null ? inv.getInv_typ()
											: null);

					entity.getClass()
							.getMethod("setInvoiceValue", BigDecimal.class)
							.invoke(entity,
									inv.getVal() != null ? inv.getVal() : null);

					entity.getClass().getMethod("setPos", String.class).invoke(
							entity, inv.getPos() != null ? inv.getPos() : null);

					entity.getClass()
							.getMethod("setReverseCharge", String.class)
							.invoke(entity,
									inv.getRchrg() != null ? inv.getRchrg()
											: null);

					entity.getClass().getMethod("setETin", String.class).invoke(
							entity,
							inv.getEtin() != null ? inv.getEtin() : null);

					entity.getClass()
							.getMethod("setCounterPatryFlag", String.class)
							.invoke(entity,
									inv.getCflag() != null ? inv.getCflag()
											: null);

					entity.getClass().getMethod("setOrgPeriod", String.class)
							.invoke(entity,
									inv.getOpd() != null ? inv.getOpd() : null);

					entity.getClass().getMethod("setSrcIrn", String.class)
							.invoke(entity,
									inv.getSrctyp() != null ? inv.getSrctyp()
											: null);

					entity.getClass().getMethod("setIrnNum", String.class)
							.invoke(entity,
									inv.getIrn() != null ? inv.getIrn() : null);

					entity.getClass().getMethod("setIrnGenDate", Date.class)
							.invoke(entity,
									inv.getIrngendate() != null
											? new SimpleDateFormat("dd-MM-yyyy")
													.parse(inv.getIrngendate())
											: null);

					entity.getClass()
							.getMethod("setDiffPercentage", BigDecimal.class)
							.invoke(entity,
									inv.getDiff_percent() != null
											? inv.getDiff_percent()
											: null);

					entity.getClass().getMethod("setImsAction", String.class)
							.invoke(entity, inv.getImsactn() != null
									? inv.getImsactn() : "N");

					entity.getClass().getMethod("setUploadedBy", String.class)
							.invoke(entity,
									inv.getUpdby() != null ? inv.getUpdby()
											: null);

					entity.getClass().getMethod("setChksum", String.class)
							.invoke(entity,
									inv.getChksum() != null ? inv.getChksum()
											: null);

					entity.getClass().getMethod("setRemarks", String.class)
							.invoke(entity,
									inv.getRemarks() != null ? inv.getRemarks()
											: null);

					entity.getClass().getMethod("setBatchId", Long.class)
							.invoke(entity, batchId);

					entity.getClass().getMethod("setIsDelete", Boolean.class)
							.invoke(entity, false);

					entity.getClass().getMethod("setCreatedBy", String.class)
							.invoke(entity, groupcode);

					entity.getClass()
							.getMethod("setCreatedOn", LocalDateTime.class)
							.invoke(entity, LocalDateTime.now());

					// need for ORG_INV_NUM, ORG_INV_date oinum oidt

					if (type.equalsIgnoreCase("B2BA")
							|| type.equalsIgnoreCase("CDNRA")
							|| type.equalsIgnoreCase("ECOMA")) {
						entity.getClass()
								.getMethod("setOrgInvoiceNumber", String.class)
								.invoke(entity,
										inv.getOinum() != null ? inv.getOinum()
												: null);

						entity.getClass()
								.getMethod("setOrgInvoiceDate", Date.class)
								.invoke(entity, inv.getOidt() != null
										? new SimpleDateFormat("dd-MM-yyyy")
												.parse(inv.getOidt())
										: null);
					}

					String noteType = null;

					if (type.equalsIgnoreCase("CDNR")
							|| type.equalsIgnoreCase("CDNRA")) {

						entity.getClass().getMethod("setNoteType", String.class)
								.invoke(entity,
										inv.getNtty() != null ? inv.getNtty()
												: null);

						noteType = inv.getNtty();

						entity.getClass().getMethod("setNoteNo", String.class)
								.invoke(entity,
										inv.getNt_num() != null
												? inv.getNt_num()
												: null);

						entity.getClass().getMethod("setNoteDate", Date.class)
								.invoke(entity, inv.getNt_dt() != null
										? new SimpleDateFormat("dd-MM-yyyy")
												.parse(inv.getNt_dt())
										: null);

						entity.getClass().getMethod("setPGst", String.class)
								.invoke(entity,
										inv.getP_gst() != null ? inv.getP_gst()
												: null);

						entity.getClass()
								.getMethod("setDelinkFlag", String.class)
								.invoke(entity,
										inv.getD_flag() != null
												? inv.getD_flag()
												: null);
					}

					LocalDate invDate = LocalDate.parse(inv.getIdt(),
							formatter);					
					// FY(doc_date)|supplier_gstin|doc_typ|doc_num|cust_gstin
					entity.getClass().getMethod("setInvoiceKey", String.class)
							.invoke(entity, createInvKey(invDate,
									sectionDto.getGstin(), inv.getInv_typ(),
									inv.getInum(), data.getCtin(),
									type, noteType));

					// child table

					inv.getItms().forEach(items -> {

						try {

							L childEntity = entityChildClass.newInstance();

							ItemDetailDTO itm_det = items.getItm_det();

							childEntity.getClass()
									.getMethod("setItemNo", Long.class)
									.invoke(childEntity,
											items.getNum() != null
													? items.getNum()
													: null);

							childEntity.getClass()
									.getMethod("setTaxRate", BigDecimal.class)
									.invoke(childEntity,
											itm_det.getRt() != null
													? itm_det.getRt()
													: null);

							childEntity.getClass()
									.getMethod("setTaxableValue",
											BigDecimal.class)
									.invoke(childEntity,
											itm_det.getTxval() != null
													? itm_det.getTxval()
													: null);

							childEntity.getClass()
									.getMethod("setIgstAmt", BigDecimal.class)
									.invoke(childEntity,
											itm_det.getIamt() != null
													? itm_det.getIamt()
													: null);

							childEntity.getClass()
									.getMethod("setCgstAmt", BigDecimal.class)
									.invoke(childEntity,
											itm_det.getCamt() != null
													? itm_det.getCamt()
													: null);

							childEntity.getClass()
									.getMethod("setSgstAmt", BigDecimal.class)
									.invoke(childEntity,
											itm_det.getSamt() != null
													? itm_det.getSamt()
													: null);

							childEntity.getClass()
									.getMethod("setCessAmt", BigDecimal.class)
									.invoke(childEntity,
											itm_det.getCsamt() != null
													? itm_det.getCsamt()
													: null);

							childEntity.getClass()
							.getMethod("setDerivedRetPeriod", Long.class)
							.invoke(childEntity, Long.valueOf(sectionDto.getRtnprd()
									.substring(2, 6)
									+ sectionDto.getRtnprd().substring(0, 2)));
							
							childEntity.getClass().getMethod("setHeader", entityClass)
						    .invoke(childEntity, entity);

							/*childEntity.getClass()
									.getMethod("setInvoiceKey", String.class)
									.invoke(childEntity, createInvKey(
											invDate, sectionDto.getGstin(),
											inv.getInv_typ(), inv.getInum(),
											data.getCtin(), dto.getType(),
											inv.getNtty()));*/

							childEntityList.add(childEntity);

						} catch (InstantiationException | IllegalAccessException
								| NoSuchMethodException
								| InvocationTargetException
								| IllegalArgumentException
								| SecurityException e) {
							LOGGER.error(
									"Error while creating instance of child entity class {}",
									entityClass.getName(), e);
							throw new AppException(e);
						}

					});

					entity.getClass().getMethod("setLineItems", List.class)
							.invoke(entity, childEntityList);

					entityList.add(entity);

				} catch (InstantiationException | IllegalAccessException
						| NoSuchMethodException | InvocationTargetException
						| IllegalArgumentException | SecurityException
						| ParseException e) {
					LOGGER.error(
							"Error while creating instance of entity class {}",
							entityClass.getName(), e);
				}
			});
		});
		return entityList;
	}

	private String createInvKey(LocalDate docDate, String suppGstin,
			String docType, String docNum, String ctin, String type,
			String noteType) {

		// FY(doc_date)|supplier_gstin|doc_typ|doc_num|cust_gstin

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(type) || !isPresent(docNum) || !isPresent(suppGstin)
				|| !isPresent(ctin) || docDate == null) {
			return null;
		}

		if (type.equalsIgnoreCase("B2B") || type.equalsIgnoreCase("ECOM")) {
			docType = "INV";
		} else if (type.equalsIgnoreCase("B2BA")
				|| type.equalsIgnoreCase("ECOMA")) {
			docType = "RNV";
		} else if (type.equalsIgnoreCase("CDNR")) {

			if ("C".equals(noteType)) {
				docType = "CR";
			} else {
				docType = "DR";
			}
		} else if (type.equalsIgnoreCase("CDNRA")) {
			if ("C".equals(noteType)) {
				docType = "RCR";
			} else {
				docType = "RDR";
			}
		}

		docKey.append(GenUtil.getFinYear(docDate)).append(DOC_KEY_JOINER)
				.append(suppGstin).append(DOC_KEY_JOINER).append(docType)
				.append(DOC_KEY_JOINER).append(removeQuotes(docNum))
				.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(ctin);

		return docKey.toString();
	}

	private Object removeQuotes(String docNum) {
		if (docNum == null) {
			return null;
		}
		return docNum.replace("\"", "");
	}

	private boolean isPresent(String docNum) {
		return docNum != null && !docNum.trim().isEmpty();
	}

	public static void main(String[] args) {

		Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		dto.setGstin("33GSPTN0481G1ZA");

		String apiResp = "{\"gstin\":\"10AABCE2267R1Z7\",\"rtnprd\":\"122024\",\"gstr1\":{\"b2b\":[{\"ctin\":\"01AABCE2207R1Z5\",\"cfs\":\"Y\",\"inv\":[{\"chksum\":\"BBUIBUIUIJKKBJKGUYFTFGUY\",\"updby\":\"S\",\"inum\":\"S008400\",\"idt\":\"24-11-2016\",\"val\":729248.16,\"pos\":\"06\",\"rchrg\":\"N\",\"etin\":\"01AABCE5507R1Z4\",\"inv_typ\":\"R\",\"flag\":\"U\",\"cflag\":\"N\",\"diff_percent\":0.65,\"opd\":\"2016-12\",\"srctyp\":\"EInvoice\",\"irn\":\"897ADG56RTY78956HYUG90BNHHIJK453GFTD99845672FDHHHSHGFH4567FG56TR\",\"irngendate\":\"24-12-2019\",\"remarks\":\"b2b changes\",\"itms\":[{\"num\":1,\"itm_det\":{\"rt\":5,\"txval\":10000,\"iamt\":325,\"camt\":0,\"samt\":0,\"csamt\":10}}]}]}],\"b2ba\":[{\"ctin\":\"01AABCE2207R1Z5\",\"cfs\":\"Y\",\"inv\":[{\"chksum\":\"CHECKSUMVALUE\",\"updby\":\"S\",\"flag\":\"U\",\"cflag\":\"N\",\"oinum\":\"S008400\",\"oidt\":\"24-11-2016\",\"inum\":\"S008400\",\"idt\":\"24-11-2016\",\"val\":729248.16,\"pos\":\"06\",\"rchrg\":\"N\",\"etin\":\"01AABCE5507R1Z4\",\"inv_typ\":\"R\",\"opd\":\"2016-12\",\"remarks\":\"b2ba changes\",\"itms\":[{\"num\":1,\"itm_det\":{\"rt\":5,\"txval\":10000,\"iamt\":833.33,\"camt\":0,\"samt\":0,\"csamt\":500}}]}]}]}}";

		JsonObject respObject = JsonParser.parseString(apiResp)
				.getAsJsonObject();
		Gson gson = new Gson();

		SupplierGetImsInvoicesSectionDtlsDto reqDto = gson.fromJson(respObject,
				SupplierGetImsInvoicesSectionDtlsDto.class);

		SupplierImsInvoicesDataParserImpl obj = new SupplierImsInvoicesDataParserImpl();
		List<SupplierImsGstr1B2BHeaderStaggingEntity> b2bEntity = obj
				.parseImsInvoicesData(dto, reqDto, reqDto.getGstr1().getB2b(),
						"B2B", 100L, dto.getGroupcode(),
						SupplierImsGstr1B2BHeaderStaggingEntity.class,
						SupplierImsGstr1B2BItemStaggingEntity.class);

		System.out.println(b2bEntity);
	}

}
