package com.ey.advisory.app.ims.handlers;

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
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingB2BARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingCNARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingCNRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingDNARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingDNRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingECOMARepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsInvoiceStagingECOMRepository;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingB2BAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingB2BEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingCNAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingCNEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingDNAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingDNEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingECOMAEntity;
import com.ey.advisory.app.service.ims.ImsInvoiceStagingECOMEntity;
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
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("ImsInvoicesDataParserImpl")
@Transactional(value = "clientTransactionManager")
public class ImsInvoicesDataParserImpl implements ImsInvoicesDataParser {

	@Autowired
	private ImsInvoiceStagingB2BARepository imsInvoiceStagingB2BARepository;

	@Autowired
	private ImsInvoiceStagingB2BRepository imsInvoiceStagingB2BRepository;

	@Autowired
	private ImsInvoiceStagingCNRepository imsInvoiceStagingCNRepository;

	@Autowired
	private ImsInvoiceStagingDNARepository imsInvoiceStagingDNARepository;

	@Autowired
	private ImsInvoiceStagingECOMRepository imsInvoiceStagingEcomRepository;

	@Autowired
	private ImsInvoiceStagingCNARepository imsInvoiceStagingCNARepository;

	@Autowired
	private ImsInvoiceStagingDNRepository imsInvoiceStagingDNRepository;

	@Autowired
	private ImsInvoiceStagingECOMARepository imsInvoiceStagingEcomARepository;

	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;

	private static final List<String> GETIMS_SUPPLY_TYPES = ImmutableList.of(
			APIConstants.IMS_TYPE_B2B, APIConstants.IMS_TYPE_B2BA,
			APIConstants.IMS_TYPE_CN, APIConstants.IMS_TYPE_CNA,
			APIConstants.IMS_TYPE_DN, APIConstants.IMS_TYPE_DNA,
			APIConstants.IMS_TYPE_ECOM, APIConstants.IMS_TYPE_ECOMA);

	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	private static final String DOC_KEY_JOINER = "|";

	/*
	 * private final static String formatter1 = D .ofPattern("dd-MM-yyyy");
	 */

	@Override
	public void parseImsInvoicesData(List<Long> resultIds,
			Gstr1GetInvoicesReqDto dto, Long batchId, String jsonString) {
		TenantContext.setTenantId(dto.getGroupcode());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GET IMS Invoices inside  ImsInvoicesDataParserImpl for batch id {} ",
					batchId);
		}

		if (Strings.isNullOrEmpty(jsonString)) {
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				if (GETIMS_SUPPLY_TYPES.contains(dto.getType())) {

					JsonObject respObject = JsonParser.parseString(apiResp)
							.getAsJsonObject();
					Gson gson = new Gson();

					GetImsInvoicesDtlsDto reqDto = gson.fromJson(respObject,
							GetImsInvoicesDtlsDto.class);

					switch (dto.getType().toUpperCase()) {
					case "B2B":
						List<ImsInvoiceStagingB2BEntity> b2bEntity = parseImsInvoicesData(
								dto, reqDto.b2b, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingB2BEntity.class);
						imsInvoiceStagingB2BRepository.saveAll(b2bEntity);

						break;
					case "B2BA":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingB2BAEntity> b2baEntity = parseImsInvoicesData(
								dto, reqDto.b2ba, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingB2BAEntity.class);
						imsInvoiceStagingB2BARepository.saveAll(b2baEntity);
						break;
					case "CN":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingCNEntity> cnEntity = parseImsInvoicesData(
								dto, reqDto.b2bcn, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingCNEntity.class);
						imsInvoiceStagingCNRepository.saveAll(cnEntity);
						break;
					case "CNA":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingCNAEntity> cnaEntity = parseImsInvoicesData(
								dto, reqDto.b2bcna, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingCNAEntity.class);
						imsInvoiceStagingCNARepository.saveAll(cnaEntity);
						break;
					case "DN":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingDNEntity> dnEntity = parseImsInvoicesData(
								dto, reqDto.b2bdn, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingDNEntity.class);
						imsInvoiceStagingDNRepository.saveAll(dnEntity);
						break;
					case "DNA":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingDNAEntity> dnaEntity = parseImsInvoicesData(
								dto, reqDto.b2bdna, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingDNAEntity.class);
						imsInvoiceStagingDNARepository.saveAll(dnaEntity);
						break;
					case "ECOM":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingECOMEntity> ecomEntity = parseImsInvoicesData(
								dto, reqDto.ecom, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingECOMEntity.class);
						imsInvoiceStagingEcomRepository.saveAll(ecomEntity);
						break;
					case "ECOMA":
						// Replace with the correct DTO list, entity class, and
						// repository
						List<ImsInvoiceStagingECOMAEntity> ecomaEntity = parseImsInvoicesData(
								dto, reqDto.ecoma, dto.getType(), batchId,
								dto.getGroupcode(),
								ImsInvoiceStagingECOMAEntity.class);
						imsInvoiceStagingEcomARepository.saveAll(ecomaEntity);
						break;
					}

				}
			});
		} else {
			String apiResp = jsonString;
			if (GETIMS_SUPPLY_TYPES.contains(dto.getType())) {

				JsonObject respObject = JsonParser.parseString(apiResp)
						.getAsJsonObject();
				Gson gson = new Gson();

				GetImsInvoicesDtlsDto reqDto = gson.fromJson(respObject,
						GetImsInvoicesDtlsDto.class);

				switch (dto.getType().toUpperCase()) {
				case "B2B":
					List<ImsInvoiceStagingB2BEntity> b2bEntity = parseImsInvoicesData(
							dto, reqDto.b2b, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingB2BEntity.class);

					LOGGER.debug(" b2bEntity {} ", b2bEntity.toArray());
					imsInvoiceStagingB2BRepository.saveAll(b2bEntity);
					break;

				case "B2BA":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingB2BAEntity> b2baEntity = parseImsInvoicesData(
							dto, reqDto.b2ba, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingB2BAEntity.class);
					imsInvoiceStagingB2BARepository.saveAll(b2baEntity);
					break;
				case "CN":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingCNEntity> cnEntity = parseImsInvoicesData(
							dto, reqDto.b2bcn, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingCNEntity.class);
					imsInvoiceStagingCNRepository.saveAll(cnEntity);
					break;
				case "CNA":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingCNAEntity> cnaEntity = parseImsInvoicesData(
							dto, reqDto.b2bcna, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingCNAEntity.class);
					imsInvoiceStagingCNARepository.saveAll(cnaEntity);
					break;
				case "DN":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingDNEntity> dnEntity = parseImsInvoicesData(
							dto, reqDto.b2bdn, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingDNEntity.class);
					imsInvoiceStagingDNRepository.saveAll(dnEntity);
					break;
				case "DNA":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingDNAEntity> dnaEntity = parseImsInvoicesData(
							dto, reqDto.b2bdna, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingDNAEntity.class);
					imsInvoiceStagingDNARepository.saveAll(dnaEntity);
					break;
				case "ECOM":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingECOMEntity> ecomEntity = parseImsInvoicesData(
							dto, reqDto.ecom, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingECOMEntity.class);
					imsInvoiceStagingEcomRepository.saveAll(ecomEntity);
					break;
				case "ECOMA":
					// Replace with the correct DTO list, entity class, and
					// repository
					List<ImsInvoiceStagingECOMAEntity> ecomaEntity = parseImsInvoicesData(
							dto, reqDto.ecoma, dto.getType(), batchId,
							dto.getGroupcode(),
							ImsInvoiceStagingECOMAEntity.class);
					imsInvoiceStagingEcomARepository.saveAll(ecomaEntity);
					break;
				}

			}

		}
	}

	private <T> List<T> parseImsInvoicesData(Gstr1GetInvoicesReqDto dto,
			List<GetImsInvoicesSectionDtlsDto> sectionDtlDto, String type,
			Long batchId, String groupcode, Class<T> entityClass) {
		List<T> entityList = new ArrayList<>();
		sectionDtlDto.forEach(sectionDto -> {
			try {
				T entity = entityClass.newInstance();

				entity.getClass().getMethod("setSupplierGstin", String.class)
						.invoke(entity, sectionDto.getSupplierGstin() != null
								? sectionDto.getSupplierGstin() : null);

				entity.getClass().getMethod("setRecipientGstin", String.class)
						.invoke(entity,
								dto.getGstin() != null ? dto.getGstin() : null);

				entity.getClass().getMethod("setReturnPeriod", String.class)
						.invoke(entity, sectionDto.getReturnPeriod() != null
								? sectionDto.getReturnPeriod() : null);

				entity.getClass().getMethod("setDerivedRetPeriod", Long.class)
						.invoke(entity,
								Long.valueOf(sectionDto.getReturnPeriod()
										.substring(2, 6)
										+ sectionDto
												.getReturnPeriod().substring(0,
														2)));

				entity.getClass().getMethod("setInvoiceNumber", String.class)
						.invoke(entity, sectionDto.getInvoiceNumber() != null
								? sectionDto.getInvoiceNumber() : null);

				// the invoice type coming from GSTN
				entity.getClass().getMethod("setGstnInvType", String.class)
						.invoke(entity, sectionDto.getInvoiceType() != null
								? sectionDto.getInvoiceType() : null);

				if (type.equalsIgnoreCase("B2B")
						|| type.equalsIgnoreCase("ECOM"))
					sectionDto.setInvoiceType("INV");
				else if (type.equalsIgnoreCase("B2BA")
						|| type.equalsIgnoreCase("ECOMA"))
					sectionDto.setInvoiceType("RNV");
				else if (type.equalsIgnoreCase("CN"))
					sectionDto.setInvoiceType("CR");
				else if (type.equalsIgnoreCase("CNA"))
					sectionDto.setInvoiceType("RCR");
				else if (type.equalsIgnoreCase("DN"))
					sectionDto.setInvoiceType("DR");
				else if (type.equalsIgnoreCase("DNA"))
					sectionDto.setInvoiceType("RDR");

				entity.getClass().getMethod("setInvoiceType", String.class)
						.invoke(entity, sectionDto.getInvoiceType() != null
								? sectionDto.getInvoiceType() : null);

				entity.getClass().getMethod("setAction", String.class)
						.invoke(entity, sectionDto.getAction() != null
								? sectionDto.getAction() : null);
				entity.getClass().getMethod("setFilingStatus", String.class)
						.invoke(entity, sectionDto.getSrcfilstatus() != null
								? sectionDto.getSrcfilstatus() : null);

				if (sectionDto.getIsPendingActionAllowed() != null
						&& sectionDto.getIsPendingActionBlocked() == null) {

					String isPendBlocked = ("N"
							.equals(sectionDto.getIsPendingActionAllowed())
									? "Y" : "N");

					entity.getClass().getMethod("setIsPendingActionBlocked",
							String.class).invoke(entity, isPendBlocked);
				} else {
					entity.getClass()
							.getMethod("setIsPendingActionBlocked",
									String.class)
							.invoke(entity,
									sectionDto
											.getIsPendingActionBlocked() != null
													? sectionDto
															.getIsPendingActionBlocked()
													: null);
				}

				/*
				 * if (sectionDto.getSourceForm() != null) { if
				 * (sectionDto.getSourceForm().equalsIgnoreCase("R1A"))
				 * sectionDto.setSourceForm("GSTR1A"); if
				 * (sectionDto.getSourceForm().equalsIgnoreCase("R1"))
				 * sectionDto.setSourceForm("GSTR1"); }
				 */

				entity.getClass().getMethod("setFormType", String.class)
						.invoke(entity, sectionDto.getSourceForm() != null
								? sectionDto.getSourceForm() : null);

				entity.getClass().getMethod("setInvoiceDate", Date.class)
						.invoke(entity, sectionDto.getInvoiceDate() != null
								? new SimpleDateFormat("dd-MM-yyyy").parse(
										sectionDto.getInvoiceDate())
								: null);
				entity.getClass().getMethod("setInvoiceValue", BigDecimal.class)
						.invoke(entity,
								sectionDto.getInvoiceValue() != null
										? sectionDto.getInvoiceValue()
										: BigDecimal.ZERO);

				entity.getClass().getMethod("setTaxableValue", BigDecimal.class)
						.invoke(entity,
								sectionDto.getTaxableValue() != null
										? sectionDto.getTaxableValue()
										: BigDecimal.ZERO);
				// need for igst, sgst, cgst, cess
				entity.getClass().getMethod("setIgstAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getIgstAmount() != null
								? sectionDto.getIgstAmount() : BigDecimal.ZERO);
				entity.getClass().getMethod("setCgstAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getCgstAmount() != null
								? sectionDto.getCgstAmount() : BigDecimal.ZERO);
				entity.getClass().getMethod("setSgstAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getSgstAmount() != null
								? sectionDto.getSgstAmount() : BigDecimal.ZERO);
				entity.getClass().getMethod("setCessAmt", BigDecimal.class)
						.invoke(entity, sectionDto.getCess() != null
								? sectionDto.getCess() : BigDecimal.ZERO);

				entity.getClass().getMethod("setPos", String.class)
						.invoke(entity, sectionDto.getPos() != null
								? sectionDto.getPos() : null);
				entity.getClass().getMethod("setTaxableValue", BigDecimal.class)
						.invoke(entity,
								sectionDto.getTaxableValue() != null
										? sectionDto.getTaxableValue()
										: BigDecimal.ZERO);

				entity.getClass().getMethod("setChksum", String.class)
						.invoke(entity, sectionDto.getHash() != null
								? sectionDto.getHash() : null);
				// need for isdelete, batchid, created on, created by
				entity.getClass().getMethod("setBatchId", Long.class)
						.invoke(entity, batchId);
				entity.getClass().getMethod("setIsDelete", Boolean.class)
						.invoke(entity, false);
				entity.getClass().getMethod("setCreatedBy", String.class)
						.invoke(entity, groupcode);
				entity.getClass().getMethod("setCreatedOn", LocalDateTime.class)
						.invoke(entity, LocalDateTime.now());

				entity.getClass().getMethod("setDocKey", String.class).invoke(
						entity,
						createInvKey(sectionDto.getSupplierGstin(),
								dto.getGstin(), sectionDto.getInvoiceNumber(),
								sectionDto.getInvoiceDate(),
								sectionDto.getInvoiceType(), type));

				LocalDate invDate = null;

				if (!Strings.isNullOrEmpty(sectionDto.getInvoiceDate())) {
					invDate = LocalDate.parse(sectionDto.getInvoiceDate(),
							formatter);
				}

				entity.getClass().getMethod("setLnkingDocKey", String.class)
						.invoke(entity,
								deriveLinkingKey(invDate, dto.getGstin(),
										sectionDto.getSupplierGstin(),
										sectionDto.getInvoiceType(),
										sectionDto.getInvoiceNumber()));

				// need for ORG_INV_NUM, ORG_INV_date

				if (type.equalsIgnoreCase("B2BA")
						|| type.equalsIgnoreCase("CNA")
						|| type.equalsIgnoreCase("DNA")
						|| type.equalsIgnoreCase("ECOMA")) {
					entity.getClass().getMethod("setOrgInvNum", String.class)
							.invoke(entity,
									sectionDto
											.getOriginalInvoiceNumber() != null
													? sectionDto
															.getOriginalInvoiceNumber()
													: null);
					entity.getClass().getMethod("setOrgInvDate", Date.class)
							.invoke(entity,
									sectionDto.getOriginalInvoiceDate() != null
											? new SimpleDateFormat("dd-mm-yyyy")
													.parse(sectionDto
															.getOriginalInvoiceDate())
											: null);
				}
				if (type.equalsIgnoreCase("B2BA")
				        || type.equalsIgnoreCase("CNA")
				        || type.equalsIgnoreCase("DNA")
				        || type.equalsIgnoreCase("ECOMA")
						|| type.equalsIgnoreCase("CN")) {

					entity.getClass().getMethod("setItcRedReq", String.class)
							.invoke(entity,sectionDto.getItcRedReq() != null
											? sectionDto.getItcRedReq(): null);

					entity.getClass().getMethod("setDeclIgst", BigDecimal.class)
							.invoke(entity,
									sectionDto.getDeclIgst() != null
											? sectionDto.getDeclIgst()
											: BigDecimal.ZERO);

					entity.getClass().getMethod("setDeclSgst", BigDecimal.class)
							.invoke(entity,
									sectionDto.getDeclSgst() != null
											? sectionDto.getDeclSgst()
											: BigDecimal.ZERO);

					entity.getClass().getMethod("setDeclCgst", BigDecimal.class)
							.invoke(entity,
									sectionDto.getDeclCgst() != null
											? sectionDto.getDeclCgst()
											: BigDecimal.ZERO);

					entity.getClass().getMethod("setDeclCess", BigDecimal.class)
							.invoke(entity,
									sectionDto.getDeclCess() != null
											? sectionDto.getDeclCess()
											: BigDecimal.ZERO);

				}
				entity.getClass().getMethod("setRemarks", String.class).invoke(
						entity,sectionDto.getRemarks() != null
								? sectionDto.getRemarks(): null);

				//add

				entityList.add(entity);
			} catch (InstantiationException | IllegalAccessException
					| NoSuchMethodException | InvocationTargetException
					| IllegalArgumentException | SecurityException
					| ParseException e) {
				LOGGER.error("Error while creating instance of entity class {}",
						entityClass.getName(), e);
				throw new AppException(e);
			}
		});
		return entityList;
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
	 * dto.setGstin("33GSPTN0481G1ZA"); dto.setType("B2BA");
	 * 
	 * String apiResp =
	 * "{\"b2b\":[{\"stin\":\"24MAYAS0100J1JD\",\"inum\":\"b1\",\"inv_typ\":\"R\",\"action\":\"A\",\"ispendactnallwd\":\"N\",\"srcform\":\"R1\",\"rtnprd\":\"012023\",\"srcfilstatus\":\"NotFiled\",\"idt\":\"23-01-2023\",\"val\":1000,\"pos\":\"24\",\"txval\":100,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"b2ba\":[{\"oinum\":\"ab2\",\"oidt\":\"24-02-2023\",\"stin\":\"24MAYAS0100J1JD\",\"rtnprd\":\"012023\",\"inum\":\"b1a\",\"action\":\"A\",\"ispendactnallwd\":\"N\",\"inv_typ\":\"R\",\"srcform\":\"R1\",\"idt\":\"23-01-2023\",\"val\":1000,\"pos\":\"07\",\"txval\":100,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"NotFiled\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"b2bdn\":[{\"stin\":\"24MAYAS0100J1JD\",\"nt_num\":\"dn2\",\"action\":\"A\",\"inv_typ\":\"R\",\"srcform\":\"R1\",\"ispendactnallwd\":\"N\",\"rtnprd\":\"012023\",\"nt_dt\":\"24-02-2023\",\"val\":1000.1,\"pos\":\"07\",\"txval\":1000.1,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"Filed\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"b2bdna\":[{\"stin\":\"24MAYAS0100J1JD\",\"ont_num\":\"ca2\",\"ont_dt\":\"24-02-2023\",\"nt_num\":\"dn2\",\"action\":\"A\",\"inv_typ\":\"R\",\"srcform\":\"R1\",\"ispendactnallwd\":\"N\",\"rtnprd\":\"012023\",\"nt_dt\":\"24-02-2023\",\"val\":1000.1,\"pos\":\"07\",\"txval\":1000.1,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"Filed\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"b2bcn\":[{\"stin\":\"24MAYAS0100J1JD\",\"nt_num\":\"dn2\",\"action\":\"A\",\"inv_typ\":\"R\",\"srcform\":\"R1\",\"rtnprd\":\"012023\",\"ispendactnallwd\":\"N\",\"nt_dt\":\"24-02-2023\",\"val\":1000.1,\"pos\":\"07\",\"txval\":1000.1,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"NotFiled\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"b2bcna\":[{\"stin\":\"24MAYAS0100J1JD\",\"ont_num\":\"ca2\",\"ont_dt\":\"24-02-2023\",\"nt_num\":\"dn2\",\"action\":\"A\",\"inv_typ\":\"R\",\"srcform\":\"R1\",\"ispendactnallwd\":\"N\",\"rtnprd\":\"012023\",\"nt_dt\":\"24-02-2023\",\"val\":1000.1,\"pos\":\"07\",\"txval\":1000.1,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"NotFiled\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"ecom\":[{\"stin\":\"24MAYAS0100J1JD\",\"rtnprd\":\"012023\",\"inum\":\"sd3\",\"action\":\"A\",\"srcform\":\"R1\",\"ispendactnallwd\":\"N\",\"idt\":\"24-02-2023\",\"val\":1000,\"pos\":\"07\",\"txval\":1000,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"NotFiled\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}],\"ecoma\":[{\"stin\":\"24MAYAS0100J1JD\",\"oinum\":\"get5\",\"oidt\":\"24-02-2023\",\"rtnprd\":\"012023\",\"inum\":\"sd3\",\"action\":\"A\",\"srcform\":\"R1\",\"ispendactnallwd\":\"N\",\"idt\":\"24-02-2023\",\"val\":1000,\"pos\":\"07\",\"txval\":1000,\"iamt\":20,\"camt\":20,\"samt\":20,\"cess\":0,\"srcfilstatus\":\"NotFiled\",\"hash\":\"1f5fb5de9e491c24dcdee9ac01d3fa5f7889f20d3a8c923bfd2e3c7f0d0125f1\"}]}";
	 * JsonObject respObject = JsonParser.parseString(apiResp)
	 * .getAsJsonObject(); Gson gson = new Gson();
	 * 
	 * GetImsInvoicesDtlsDto reqDto = gson.fromJson(respObject,
	 * GetImsInvoicesDtlsDto.class);
	 * 
	 * List<ImsInvoiceStagingB2BAEntity> b2bEntity = parseImsInvoicesData(dto,
	 * reqDto.b2ba, dto.getType(), 100L, dto.getGroupcode(),
	 * ImsInvoiceStagingB2BAEntity.class);
	 * 
	 * System.out.println(b2bEntity); }
	 */

	private String createInvKey(String suppGstin, String recipientGstin,
			String docNum, String docDate, String docType, String tableType) {

		StringBuilder docKey = new StringBuilder();

		if (!isPresent(docType) || !isPresent(docNum) || !isPresent(suppGstin)
				|| !isPresent(recipientGstin) || !isPresent(docDate)) {
			return null;
		}

		docKey.append(suppGstin).append(DOC_KEY_JOINER).append(recipientGstin)
				.append(DOC_KEY_JOINER).append(removeQuotes(docNum))
				.append(DOC_KEY_JOINER).append(removeQuotes(docDate))
				.append(DOC_KEY_JOINER).append(docType).append(DOC_KEY_JOINER)
				.append(tableType);

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

	// R - INV B2b, Ecom
	// R - RNV - b2ba, ecoma
	// R - CR - CN
	// R - RCR - CNA
	// r - DR- DN
	// R - RDR- DNA

	private String deriveLinkingKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}

}
