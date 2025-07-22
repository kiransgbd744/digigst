package com.ey.advisory.app.services.jobs.erp;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.Get2BErpConfigRequestRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntReqDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgAllClientsHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgHeaderBusinessMsgDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgHeaderChunkDtlsDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgInvDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Get2BRevIntgSummaryHeaderDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

@Service("Get2BConsolidatedHandler")
public class Get2BConsolidatedHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2BConsolidatedHandler.class);

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	@Qualifier("Get2BRevIntgServiceImpl")
	private Get2BRevIntgServiceImpl get2BRevIntgService;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	private DestinationConnectivity connectivity;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private Get2BErpConfigRequestRepository erpConfigRepo;
	
	@Autowired
	private GetAnx1BatchRepository batchRepo;
	/**
	 * Whenever calling procedure we are getting gstin,Return Period,
	 * section,batchId and erpId
	 * 
	 * @param dto
	 * @return
	 */
	public void Get2BConsolidateRptToErp(Get2BRevIntReqDto dto) {

		Integer respCode = 0;
		Long requestId = null;
		Long invocationId = dto.getInvocationId();
		try {

			String groupCode = TenantContext.getTenantId();
			String gstin = dto.getGstin();
			String retPeriod = dto.getRetPeriod();
			requestId = dto.getRequestId();
			Long erpId = dto.getErpId();
			String destName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			Long jobId = dto.getJobId();
			/*
			 * Procedure call for chunking the ERP Batch Id into header tables
			 * because chunking will be applied to result set with max volume
			 * 
			 */

			String entityName = "";
			String entityPan = "";
			GSTNDetailEntity gstnDtls = gstnDetailRepo
					.findByGstinAndIsDeleteFalse(gstin);

			Optional<EntityInfoEntity> entityOp = entityRepo
					.findById(gstnDtls.getEntityId());

			if (entityOp.isPresent()) {
				entityName = entityOp.get().getEntityName();
				entityPan = entityOp.get().getPan();
			}

			Integer noOfChunk = getNoOfChunks(requestId);
			
			Optional<LocalDateTime> result = batchRepo.findStartTimeByRequestId(invocationId);
			LocalDateTime getCallStartTime = null;
			if (result.isPresent()) {
				getCallStartTime = result.get();
				getCallStartTime = EYDateUtil.toISTDateTimeFromUTC(getCallStartTime);
			}

			if (noOfChunk != 0) {
				int chunkId = 0;

				while (chunkId < noOfChunk) {
					chunkId++;
					List<Get2BRevIntgInvDto> get2BInvDetails = get2BRevIntgService
							.get2BTransactionalData(requestId, chunkId,
									entityName, entityPan);

					String chunkStatus = chunkId + "/" + noOfChunk;

					/**
					 * Pushing Get 2B Transactional data to ERP
					 */

					if ("sp0005".equalsIgnoreCase(groupCode)) {

						Get2BRevIntgHeaderDto headerDto = new Get2BRevIntgHeaderDto();

						headerDto.setItemDtos(get2BInvDetails);

						Get2BRevIntgHeaderChunkDtlsDto chunkDtlsDto = new Get2BRevIntgHeaderChunkDtlsDto();

						chunkDtlsDto.setCurrentChunk(chunkId);
						chunkDtlsDto.setTotalChunk(noOfChunk);
						chunkDtlsDto.setTimestamp(getCallStartTime);
						Get2BRevIntgHeaderBusinessMsgDto finalDetailsDto = new Get2BRevIntgHeaderBusinessMsgDto();

						finalDetailsDto.setBussinessMsg(chunkDtlsDto);
						finalDetailsDto.setItmDtls(headerDto.getItemDtos());

						long currentBatchSize = 0;
						if (headerDto.getItemDtos() != null
								&& !headerDto.getItemDtos().isEmpty()) {
							currentBatchSize = headerDto.getItemDtos().size();
							AnxErpBatchEntity batch = erpBatchHandler
									.createErpBatch(groupCode, null, gstin,
											destName, scenarioId,
											currentBatchSize, null,
											ERPConstants.EVENT_BASED_JOB, erpId,
											null,
											APIConstants.SYSTEM.toUpperCase(),
											"2BTRANS", jobId, chunkStatus);

							respCode = connectivity.pushToErp(finalDetailsDto,
									"Get2BRevIntgHeaderBusinessMsgDto", batch);

							if (respCode == HttpURLConnection.HTTP_OK) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"2B Transactional data has been moved to "
													+ "ERP. ResponseCode {}",
											respCode);
								}

							}
						}
					} else {
						Get2BRevIntgAllClientsHeaderDto headerDto = new Get2BRevIntgAllClientsHeaderDto();

						headerDto.setItemDtos(get2BInvDetails);

						long currentBatchSize = 0;
						if (headerDto.getItemDtos() != null
								&& !headerDto.getItemDtos().isEmpty()) {
							currentBatchSize = headerDto.getItemDtos().size();
							AnxErpBatchEntity batch = erpBatchHandler
									.createErpBatch(groupCode, null, gstin,
											destName, scenarioId,
											currentBatchSize, null,
											ERPConstants.EVENT_BASED_JOB, erpId,
											null,
											APIConstants.SYSTEM.toUpperCase(),
											"2BTRANS", jobId, chunkStatus);

							respCode = connectivity.pushToErp(headerDto,
									"Get2BRevIntgAllClientsHeaderDto", batch);

							if (respCode == HttpURLConnection.HTTP_OK) {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"2B Transactional data has been moved to "
													+ "ERP. ResponseCode {}",
											respCode);
								}

							}
						}
					}
				}
			}
			/**
			 * Pushing Get 2B Summary data to ERP
			 */

			Long summScenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
					APIConstants.GSTR2B_SUMMARY_REV_INT);
			// Assuming it as Event based job

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario id for get 2B rev integration : {}",
						summScenarioId);
			}

			ErpEventsScenarioPermissionEntity summScenarioPermision = erpEventsScenPermissionRepo
					.findByScenarioIdAndErpIdAndIsDeleteFalse(summScenarioId,
							commonUtility.getErpIdfromGstin(gstin));

			if (summScenarioPermision != null) {

				List<Get2BRevIntgSummaryDto> get2BSummaryDetails = get2BRevIntgService
						.get2BSummaryData(gstin, retPeriod, entityName,
								entityPan);

				Get2BRevIntgSummaryHeaderDto summaryDto = new Get2BRevIntgSummaryHeaderDto();

				summaryDto.setItemDtos(get2BSummaryDetails);
				long currentBatchSize = 0;
				if (summaryDto.getItemDtos() != null
						&& !summaryDto.getItemDtos().isEmpty()) {
					currentBatchSize = summaryDto.getItemDtos().size();
					AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
							groupCode, null, gstin,
							summScenarioPermision.getDestName(), summScenarioId,
							currentBatchSize, null,
							ERPConstants.EVENT_BASED_JOB,
							summScenarioPermision.getErpId(), null,
							APIConstants.SYSTEM.toUpperCase());

					respCode = connectivity.pushToErp(summaryDto,
							"Get2BRevIntgSummaryHeaderDto", batch);

					if (respCode == HttpURLConnection.HTTP_OK) {

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"2B Transactional data has been moved to "
											+ "ERP. ResponseCode {}",
									respCode);
						}

					}
				}
			}
			erpConfigRepo.updateStatusByConfigId(invocationId, APIConstants.COMPLETED);
			
		} catch (Exception e) {

			String errMsg = String.format(
					"Gstr2B Rev Integ Failed for requestId %s for group code %s",
					requestId, TenantContext.getTenantId());
			failedBatAltUtility.prepareAndTriggerAlert(
					String.valueOf(requestId), "Get2B Rev Integ",
					String.format("Exception is %s", e.getMessage()));
			LOGGER.error(errMsg, e);
			erpConfigRepo.updateStatusByConfigId(invocationId,  APIConstants.FAILED);
			throw new AppException(errMsg, e);
		}
	}

	private int getNoOfChunks(Long requestId) {

		Integer noOfChunk = null;
		String msg = null;
		try {

			/*
			 * Map<String, Config> configMap = configManager
			 * .getConfigs("GSTR2AREVINTG", CONF_KEY, "DEFAULT"); chunkSize =
			 * configMap.get(CONF_KEY) == null ? 10000 :
			 * Integer.valueOf(configMap.get(CONF_KEY).getValue());
			 */

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_AUTO2B_ERP_INS_CHUNK");

			storedProc.registerStoredProcedureParameter("P_REQUEST_CONFIG_ID",
					Long.class, ParameterMode.IN);

			storedProc.setParameter("P_REQUEST_CONFIG_ID", requestId);

			storedProc.registerStoredProcedureParameter("P_CHUNK_SPILIT_VAL",
					Integer.class, ParameterMode.IN);

			// As a part of--136789--US--begin
			Map<String, Config> configMap = configManager.getConfigs(
					"GET2A2BREVINTEG", "get2b.rev.intg.chunk.limit");

			String chunkSizeNum = configMap
					.get("get2b.rev.intg.chunk.limit") == null
							? "10000"
							: String.valueOf(
									configMap.get("get2b.rev.intg.chunk.limit")
											.getValue());

			int chunkSize = Integer.parseInt(chunkSizeNum);
			// As a part of--136789--US--end

			storedProc.setParameter("P_CHUNK_SPILIT_VAL", chunkSize);
			/* storedProc.setParameter("P_CHUNK_SPILIT_VAL", 10000); */

			if (LOGGER.isDebugEnabled()) {
				msg = String.format(
						"Executing chunking proc"
								+ " USP_AUTO_2APR_INS_CHUNK_ERP_GSTIN: '%d'",
						10000);
				LOGGER.debug(msg);
			}

			Integer chunksize = (Integer) storedProc.getSingleResult();

			noOfChunk = chunksize;

			if (LOGGER.isDebugEnabled()) {
				msg = String.format("Chunking proc Executed"
						+ " USP_AUTO_2APR_INS_CHUNK_ERP_GSTIN: configId '%d', "
						+ "noOfChunk %d ", 10000, noOfChunk);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {

			msg = String.format(
					"Error while executing chunking proc " + "configId %d",
					10000);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		return noOfChunk;
	}

	public static void main(String[] args) {
		String xmlContent = "<item>" + "<RETURN_PERIOD>062024</RETURN_PERIOD>"
				+ "<RECIPIENT_GSTIN>32AAACI1681G2ZX</RECIPIENT_GSTIN>"
				+ "<SUPPLIER_GSTIN>32AAACT6204C1Z2</SUPPLIER_GSTIN>"
				+ "<SUPPLIER_NAME>THE FERTILISERS AND CHEMICALS TRAVANCORE LIMITED</SUPPLIER_NAME>"
				+ "<DOCUMENT_TYPE>SEWP</DOCUMENT_TYPE>"
				+ "<SUPPLY_TYPE>SEWP</SUPPLY_TYPE>"
				+ "<DOCUMENT_NUMBER>KL0031505642</DOCUMENT_NUMBER>"
				+ "<DOCUMENT_DATE>2024-06-21</DOCUMENT_DATE>"
				+ "<TAXABLE_VALUE>1947724.00</TAXABLE_VALUE>"
				+ "<TAX_RATE>18.00</TAX_RATE>"
				+ "<IGST_AMOUNT>350590.32</IGST_AMOUNT>"
				+ "<CGST_AMOUNT>0.00</CGST_AMOUNT>"
				+ "<SGST_AMOUNT>0.00</SGST_AMOUNT>"
				+ "<CESS_AMOUNT>0.00</CESS_AMOUNT>"
				+ "<INVOICE_VALUE>2298314.32</INVOICE_VALUE>" + "<POS>32</POS>"
				+ "<STATE_NAME>Kerala</STATE_NAME>"
				+ "<LINE_NUMBER>1</LINE_NUMBER>"
				+ "<GENERATION_DATE>2024-07-14 10:42:34.393</GENERATION_DATE>"
				+ "<GSTR_FP>062024</GSTR_FP>" + "<GSTR_FD>2024-07-11</GSTR_FD>"
				+ "<DIFF_PERCT>1.00</DIFF_PERCT>"
				+ "<REV_CHR_FLAG>N</REV_CHR_FLAG>" + "<ITC_AVAIL>Y</ITC_AVAIL>"
				+ "<RSN_ITC_UNAVAIL></RSN_ITC_UNAVAIL>"
				+ "<TABLE_TYPE>B2B</TABLE_TYPE>"
				+ "<ENTITY_NAME>Indian Oil Corporation Limited</ENTITY_NAME>"
				+ "<ENTITY_PAN>AAACI1681G</ENTITY_PAN>" + "</item>";

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(
					Get2BRevIntgInvDto.class,
					Get2BRevIntgHeaderBusinessMsgDto.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			// Parse the XML string to an object
			StringReader reader = new StringReader(xmlContent);
			Get2BRevIntgInvDto dto = (Get2BRevIntgInvDto) unmarshaller
					.unmarshal(reader);

			Get2BRevIntgHeaderDto headerDto = new Get2BRevIntgHeaderDto();
			headerDto.setItemDtos(Arrays.asList(dto));

			Get2BRevIntgHeaderChunkDtlsDto chunkDtlsDto = new Get2BRevIntgHeaderChunkDtlsDto();
			chunkDtlsDto.setCurrentChunk(2);
			chunkDtlsDto.setTotalChunk(10);

			Get2BRevIntgHeaderBusinessMsgDto itemDetailsDto = new Get2BRevIntgHeaderBusinessMsgDto();
			itemDetailsDto.setBussinessMsg(chunkDtlsDto);
			itemDetailsDto.setItmDtls(headerDto.getItemDtos());

			try {
				// Create a JAXB context for the XmlBean class
				JAXBContext context = JAXBContext
						.newInstance(Get2BRevIntgHeaderBusinessMsgDto.class);

				// Create a Marshaller to convert the object to XML
				Marshaller marshaller = context.createMarshaller();

				// Set Marshaller property to format the XML output
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);

				// Convert the XmlBean object to an XML string
				StringWriter sw = new StringWriter();
				marshaller.marshal(itemDetailsDto, sw);
				String xmlString = sw.toString();

				// Print the XML string
				System.out.println(xmlString);
			} catch (JAXBException e) {
				e.printStackTrace();
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
