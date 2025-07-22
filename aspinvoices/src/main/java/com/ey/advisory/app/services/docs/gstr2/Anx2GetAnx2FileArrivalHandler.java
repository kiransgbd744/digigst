package com.ey.advisory.app.services.docs.gstr2;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx2B2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx2DeInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx2SezwopInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.simplified.client.GetAnx2SezwpInvoicesHeaderEntity;
import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx2B2bInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx2DeInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx2SezwopInvoicesRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx2SezwpInvoicesRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.gstr2fileupload.Gstr2HeaderCheckService;
import com.ey.advisory.app.util.Gstr1FileUploadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Anx2GetAnx2FileArrivalHandler")
public class Anx2GetAnx2FileArrivalHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2GetAnx2FileArrivalHandler.class);

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("Anx2GetAnx2FileHeaderCheckService")
	private Gstr2HeaderCheckService gstr2HeaderCheckService;

	@Autowired
	@Qualifier("Gstr1FileUploadUtil")
	private Gstr1FileUploadUtil gstr1FileUploadUtil;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("GetAnx2B2bInvoicesRepository")
	private GetAnx2B2bInvoicesRepository anx2b2bInvoicesRepository;

	@Autowired
	@Qualifier("GetAnx2DeInvoicesRepository")
	private GetAnx2DeInvoicesRepository anx2deInvoicesRepository;

	@Autowired
	@Qualifier("GetAnx2SezwpInvoicesRepository")
	private GetAnx2SezwpInvoicesRepository anx2sezwpInvoicesRepository;

	@Autowired
	@Qualifier("GetAnx2SezwopInvoicesRepository")
	private GetAnx2SezwopInvoicesRepository anx2sezwopInvoicesRepository;

	@Autowired
	@Qualifier("asyncExecJobRepository")
	private AsyncExecJobRepository asyncExecJobRepository;

	public void processGetAnx2File(Message message, AppExecContext context) {

		LOGGER.error("processGetAnx2File");
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.error(fileArrivalMsg);

		// Extract the File Arrival message from the serialized Job params
		// object.
		FileArrivalMsgDto msg = extractAndValidateMessage(message);

		// Join the file path and file name to get the file path.
		String fileName = msg.getFileName();
		String fileFolder = msg.getFilePath();

		InputStream inputStream = null;
		try {
			Session openCmisSession = gstr1FileUploadUtil.getCmisSession();
			LOGGER.error("openCmisSession " + openCmisSession);
			Document document = gstr1FileUploadUtil.getDocument(openCmisSession,
					fileName, fileFolder);
			LOGGER.error("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error(
					"Exception occured in get anx2 " + "File Arrival Processor",
					e);
		}
		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(27);

		// Add a dummy row handler that will keep counting the rows.
		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		LOGGER.info("Start Time " + stTime);
		traverser.traverse(inputStream, layout, rowHandler, null);

		List<Object[]> anx2List = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);

		Map<String, List<Object[]>> finalMap = new HashMap<String, List<Object[]>>();

		for (Object obj[] : anx2List) {
			String tableName = (String) obj[0];
			if (finalMap.containsKey(tableName)) {
				List<Object[]> list = finalMap.get(tableName);
				list.add(obj);
				finalMap.put(tableName, list);
			} else {
				List<Object[]> al = new ArrayList<>();
				al.add(obj);
				finalMap.put(tableName, al);
			}
		}

		Integer totalRecords = 0;
		Integer errorRecords = 0;
		Integer processedRecords = 0;
		Integer information = 0;

		if (!finalMap.isEmpty()) {

			finalMap.keySet().forEach(tableName -> {
				switch (tableName) {
				case "B2B":
					List<Object[]> b2bList = finalMap.get(tableName);
					List<GetAnx2B2bInvoicesHeaderEntity> b2bAnx2List = convertObjectArrayToB2BList(
							b2bList, tableName);
					anx2b2bInvoicesRepository.saveAll(b2bAnx2List);
					break;
				case "DE":
					List<Object[]> deList = finalMap.get(tableName);
					List<GetAnx2DeInvoicesHeaderEntity> deAnx2List = convertObjectArrayToDEList(
							deList, tableName);
					anx2deInvoicesRepository.saveAll(deAnx2List);
					break;
				case "SEZWP":
					List<Object[]> sezwpList = finalMap.get(tableName);
					List<GetAnx2SezwpInvoicesHeaderEntity> sezwpAnx2List = convertObjectArrayToSezwpList(
							sezwpList, tableName);
					anx2sezwpInvoicesRepository.saveAll(sezwpAnx2List);
					break;
				case "SEZWOP":
					List<Object[]> sezwopList = finalMap.get(tableName);
					List<GetAnx2SezwopInvoicesHeaderEntity> sezwopAnx2List = convertObjectArrayToSezwopList(
							sezwopList, tableName);
					anx2sezwopInvoicesRepository.saveAll(sezwopAnx2List);
					break;
				}
			});

			updateFileStatus.setTotal(totalRecords);
			updateFileStatus.setProcessed(finalMap.values().size());
			updateFileStatus.setError(errorRecords);
			updateFileStatus.setInformation(information);
			updateFileStatus.setFileStatus(JobStatusConstants.PROCESSED);
			gstr1FileStatusRepository.save(updateFileStatus);
		}

	}

	private List<GetAnx2SezwopInvoicesHeaderEntity> convertObjectArrayToSezwopList(
			List<Object[]> sezwopList, String tableName) {
		List<GetAnx2SezwopInvoicesHeaderEntity> entities = new ArrayList<>();
		for (Object obj[] : sezwopList) {
			GetAnx2SezwopInvoicesHeaderEntity entity = new GetAnx2SezwopInvoicesHeaderEntity();
			String cgstin = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;
			if (cgstin != null && cgstin.length() > 22) {
				cgstin = cgstin.substring(0, 22);
			}
			entity.setCgstin(cgstin);

			String sgstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;
			if (sgstin != null && sgstin.length() > 22) {
				sgstin = sgstin.substring(0, 22);
			}
			entity.setSgstin(sgstin);

			String cfs = (obj[3] != null && !obj[3].toString().trim().isEmpty())
					? String.valueOf(obj[3]).trim() : null;
			if (cfs != null && cfs.length() > 1) {
				cfs = cfs.substring(0, 1);
			}
			entity.setCfs(cfs);

			String chkSum = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
			if (chkSum != null && chkSum.length() > 70) {
				chkSum = chkSum.substring(0, 70);
			}
			entity.setChkSum(chkSum);

			String uploadDate = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;
			LocalDate localUploadDate = DateUtil.parseObjToDate(uploadDate);
			entity.setUploadDate(localUploadDate);

			String invNum = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim() : null;
			if (invNum != null && invNum.length() > 16) {
				invNum = invNum.substring(0, 16);
			}
			entity.setInvNum(invNum);

			String supInvoiceDate = (obj[7] != null
					&& !obj[7].toString().trim().isEmpty())
							? String.valueOf(obj[7]).trim() : null;
			LocalDate localsupInvoiceDate = DateUtil
					.parseObjToDate(supInvoiceDate);
			entity.setInvDate(localsupInvoiceDate);
			BigDecimal invValue = BigDecimal.ZERO;
			if (obj[8] != null && !obj[8].toString().trim().isEmpty()) {
				String invValueStr = (String.valueOf(obj[8])).trim();
				invValue = new BigDecimal(invValueStr);

			}
			entity.setInvValue(invValue);

			String pos = (obj[9] != null && !obj[9].toString().trim().isEmpty())
					? String.valueOf(obj[9]).trim() : null;
			if (pos != null && pos.length() > 5) {
				pos = pos.substring(0, 5);
			}
			entity.setPos(pos);

			String invType = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]).trim() : null;
			if (invType != null && invType.length() > 20) {
				invType = invType.substring(0, 20);
			}
			entity.setInvType(invType);

			Long batchId = 0L;
			if (obj[12] != null && !obj[12].toString().trim().isEmpty()) {
				batchId = Long.parseLong(obj[12].toString().trim());
			}
			entity.setSezwopBatchIdAnx2(batchId);

			BigDecimal taxVal = BigDecimal.ZERO;
			if (obj[17] != null && !obj[17].toString().trim().isEmpty()) {
				String taxValStr = (String.valueOf(obj[17])).trim();
				taxVal = new BigDecimal(taxValStr);

			}
			entity.setTaxable(taxVal);

			String retPeriod = (obj[18] != null
					&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]).trim() : null;
			if (retPeriod != null && retPeriod.length() > 6) {
				retPeriod = retPeriod.substring(0, 6);
			}
			entity.setReturnPeriod(retPeriod);

			String action = (obj[19] != null
					&& !obj[19].toString().trim().isEmpty())
							? String.valueOf(obj[19]).trim() : null;
			if (action != null && action.length() > 5) {
				action = action.substring(0, 5);
			}
			entity.setAction(action);

			Integer deRetPeriod = 0;
			if (obj[20] != null && !obj[20].toString().trim().isEmpty()) {
				String deRetPeriodStr = (String.valueOf(obj[20])).trim();
				deRetPeriod = Integer.valueOf(deRetPeriodStr);
			}
			entity.setDerivedRetPeriod(deRetPeriod);

			int isDelete = 0;
			if (obj[21] != null) {
				isDelete = Integer.parseInt(obj[21].toString().trim());
			}
			entity.setDelete(isDelete == 0 ? false : true);

			String cgstinPan = (obj[22] != null
					&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;
			if (cgstinPan != null && cgstinPan.length() > 15) {
				cgstinPan = cgstinPan.substring(0, 15);
			}
			entity.setCgstinPan(cgstinPan);

			String sgstinPan = (obj[23] != null
					&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;
			if (sgstinPan != null && sgstinPan.length() > 15) {
				sgstinPan = sgstinPan.substring(0, 15);
			}
			entity.setSgstinPan(sgstinPan);

			String itc = (obj[24] != null
					&& !obj[24].toString().trim().isEmpty())
							? String.valueOf(obj[24]).trim() : null;
			if (itc != null && itc.length() > 2) {
				itc = itc.substring(0, 2);
			}
			entity.setItc(itc);

			String rfndFlag = (obj[25] != null
					&& !obj[25].toString().trim().isEmpty())
							? String.valueOf(obj[25]).trim() : null;
			if (rfndFlag != null && rfndFlag.length() > 2) {
				rfndFlag = rfndFlag.substring(0, 2);
			}
			entity.setRfndElg(rfndFlag);

			entity.setTableSection(tableName);

			entities.add(entity);
		}
		return entities;
	}

	private List<GetAnx2SezwpInvoicesHeaderEntity> convertObjectArrayToSezwpList(
			List<Object[]> sezwpList, String tableName) {
		List<GetAnx2SezwpInvoicesHeaderEntity> entities = new ArrayList<>();
		for (Object obj[] : sezwpList) {
			GetAnx2SezwpInvoicesHeaderEntity entity = new GetAnx2SezwpInvoicesHeaderEntity();
			String cgstin = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;
			if (cgstin != null && cgstin.length() > 22) {
				cgstin = cgstin.substring(0, 22);
			}
			entity.setCgstin(cgstin);

			String sgstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;
			if (sgstin != null && sgstin.length() > 22) {
				sgstin = sgstin.substring(0, 22);
			}
			entity.setSgstin(sgstin);

			String cfs = (obj[3] != null && !obj[3].toString().trim().isEmpty())
					? String.valueOf(obj[3]).trim() : null;
			if (cfs != null && cfs.length() > 1) {
				cfs = cfs.substring(0, 1);
			}
			entity.setCfs(cfs);

			String chkSum = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
			if (chkSum != null && chkSum.length() > 70) {
				chkSum = chkSum.substring(0, 70);
			}
			entity.setChkSum(chkSum);

			String uploadDate = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;
			LocalDate localUploadDate = DateUtil.parseObjToDate(uploadDate);
			entity.setUploadDate(localUploadDate);

			String invNum = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim() : null;
			if (invNum != null && invNum.length() > 16) {
				invNum = invNum.substring(0, 16);
			}
			entity.setInvNum(invNum);

			String supInvoiceDate = (obj[7] != null
					&& !obj[7].toString().trim().isEmpty())
							? String.valueOf(obj[7]).trim() : null;
			LocalDate localsupInvoiceDate = DateUtil
					.parseObjToDate(supInvoiceDate);
			entity.setInvDate(localsupInvoiceDate);
			BigDecimal invValue = BigDecimal.ZERO;
			if (obj[8] != null && !obj[8].toString().trim().isEmpty()) {
				String invValueStr = (String.valueOf(obj[8])).trim();
				invValue = new BigDecimal(invValueStr);

			}
			entity.setInvValue(invValue);

			String pos = (obj[9] != null && !obj[9].toString().trim().isEmpty())
					? String.valueOf(obj[9]).trim() : null;
			if (pos != null && pos.length() > 5) {
				pos = pos.substring(0, 5);
			}
			entity.setPos(pos);

			String invType = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]).trim() : null;
			if (invType != null && invType.length() > 20) {
				invType = invType.substring(0, 20);
			}
			entity.setInvType(invType);

			BigDecimal diffPerc = BigDecimal.ZERO;
			if (obj[11] != null && !obj[11].toString().trim().isEmpty()) {
				String diffPercStr = (String.valueOf(obj[11])).trim();
				diffPerc = new BigDecimal(diffPercStr);

			}
			entity.setDiffPercentage(diffPerc);
			Long batchId = 0L;
			if (obj[12] != null && !obj[12].toString().trim().isEmpty()) {
				batchId = Long.parseLong(obj[12].toString().trim());
			}
			entity.setSezwpBatchIdAnx2(batchId);

			BigDecimal igstAmt = BigDecimal.ZERO;
			if (obj[13] != null && !obj[13].toString().trim().isEmpty()) {
				String igstAmtStr = (String.valueOf(obj[13])).trim();
				igstAmt = new BigDecimal(igstAmtStr);

			}
			entity.setIgstAmt(igstAmt);
			BigDecimal cgstAmt = BigDecimal.ZERO;
			if (obj[14] != null && !obj[14].toString().trim().isEmpty()) {
				String cgstAmtStr = (String.valueOf(obj[14])).trim();
				cgstAmt = new BigDecimal(cgstAmtStr);

			}
			entity.setCgstAmt(cgstAmt);
			BigDecimal sgstAmt = BigDecimal.ZERO;
			if (obj[15] != null && !obj[15].toString().trim().isEmpty()) {
				String sgstAmtStr = (String.valueOf(obj[15])).trim();
				sgstAmt = new BigDecimal(sgstAmtStr);

			}
			entity.setSgstAmt(sgstAmt);
			BigDecimal cessAmt = BigDecimal.ZERO;
			if (obj[16] != null && !obj[16].toString().trim().isEmpty()) {
				String cessAmtStr = (String.valueOf(obj[16])).trim();
				cessAmt = new BigDecimal(cessAmtStr);

			}
			entity.setCessAmt(cessAmt);
			BigDecimal taxVal = BigDecimal.ZERO;
			if (obj[17] != null && !obj[17].toString().trim().isEmpty()) {
				String taxValStr = (String.valueOf(obj[17])).trim();
				taxVal = new BigDecimal(taxValStr);

			}
			entity.setTaxable(taxVal);

			String retPeriod = (obj[18] != null
					&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]).trim() : null;
			if (retPeriod != null && retPeriod.length() > 6) {
				retPeriod = retPeriod.substring(0, 6);
			}
			entity.setReturnPeriod(retPeriod);

			String action = (obj[19] != null
					&& !obj[19].toString().trim().isEmpty())
							? String.valueOf(obj[19]).trim() : null;
			if (action != null && action.length() > 5) {
				action = action.substring(0, 5);
			}
			entity.setAction(action);

			Integer deRetPeriod = 0;
			if (obj[20] != null && !obj[20].toString().trim().isEmpty()) {
				String deRetPeriodStr = (String.valueOf(obj[20])).trim();
				deRetPeriod = Integer.valueOf(deRetPeriodStr);
			}
			entity.setDerivedRetPeriod(deRetPeriod);

			int isDelete = 0;
			if (obj[21] != null) {
				isDelete = Integer.parseInt(obj[21].toString().trim());
			}
			entity.setDelete(isDelete == 0 ? false : true);

			String cgstinPan = (obj[22] != null
					&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;
			if (cgstinPan != null && cgstinPan.length() > 15) {
				cgstinPan = cgstinPan.substring(0, 15);
			}
			entity.setCgstinPan(cgstinPan);

			String sgstinPan = (obj[23] != null
					&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;
			if (sgstinPan != null && sgstinPan.length() > 15) {
				sgstinPan = sgstinPan.substring(0, 15);
			}
			entity.setSgstinPan(sgstinPan);

			String itc = (obj[24] != null
					&& !obj[24].toString().trim().isEmpty())
							? String.valueOf(obj[24]).trim() : null;
			if (itc != null && itc.length() > 2) {
				itc = itc.substring(0, 2);
			}
			entity.setItc(itc);

			String rfndFlag = (obj[25] != null
					&& !obj[25].toString().trim().isEmpty())
							? String.valueOf(obj[25]).trim() : null;
			if (rfndFlag != null && rfndFlag.length() > 2) {
				rfndFlag = rfndFlag.substring(0, 2);
			}
			entity.setRfndElg(rfndFlag);
			entity.setTableSection(tableName);

			entities.add(entity);
		}
		return entities;
	}

	private List<GetAnx2DeInvoicesHeaderEntity> convertObjectArrayToDEList(
			List<Object[]> deList, String tableName) {
		List<GetAnx2DeInvoicesHeaderEntity> entities = new ArrayList<>();
		for (Object obj[] : deList) {
			GetAnx2DeInvoicesHeaderEntity entity = new GetAnx2DeInvoicesHeaderEntity();

			String cgstin = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;
			if (cgstin != null && cgstin.length() > 22) {
				cgstin = cgstin.substring(0, 22);
			}
			entity.setCgstin(cgstin);

			String sgstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;
			if (sgstin != null && sgstin.length() > 22) {
				sgstin = sgstin.substring(0, 22);
			}
			entity.setSgstin(sgstin);

			String cfs = (obj[3] != null && !obj[3].toString().trim().isEmpty())
					? String.valueOf(obj[3]).trim() : null;
			if (cfs != null && cfs.length() > 1) {
				cfs = cfs.substring(0, 1);
			}
			entity.setCfs(cfs);

			String chkSum = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
			if (chkSum != null && chkSum.length() > 70) {
				chkSum = chkSum.substring(0, 70);
			}
			entity.setChkSum(chkSum);

			String uploadDate = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;
			LocalDate localUploadDate = DateUtil.parseObjToDate(uploadDate);
			entity.setUploadDate(localUploadDate);

			String invNum = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim() : null;
			if (invNum != null && invNum.length() > 16) {
				invNum = invNum.substring(0, 16);
			}
			entity.setInvNum(invNum);

			String supInvoiceDate = (obj[7] != null
					&& !obj[7].toString().trim().isEmpty())
							? String.valueOf(obj[7]).trim() : null;
			LocalDate localsupInvoiceDate = DateUtil
					.parseObjToDate(supInvoiceDate);
			entity.setInvDate(localsupInvoiceDate);
			BigDecimal invValue = BigDecimal.ZERO;
			if (obj[8] != null && !obj[8].toString().trim().isEmpty()) {
				String invValueStr = (String.valueOf(obj[8])).trim();
				invValue = new BigDecimal(invValueStr);

			}
			entity.setInvValue(invValue);

			String pos = (obj[9] != null && !obj[9].toString().trim().isEmpty())
					? String.valueOf(obj[9]).trim() : null;
			if (pos != null && pos.length() > 5) {
				pos = pos.substring(0, 5);
			}
			entity.setPos(pos);

			String invType = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]).trim() : null;
			if (invType != null && invType.length() > 20) {
				invType = invType.substring(0, 20);
			}
			entity.setInvType(invType);

			BigDecimal diffPerc = BigDecimal.ZERO;
			if (obj[11] != null && !obj[11].toString().trim().isEmpty()) {
				String diffPercStr = (String.valueOf(obj[11])).trim();
				diffPerc = new BigDecimal(diffPercStr);

			}
			entity.setDiffPercentage(diffPerc);
			Long batchId = 0L;
			if (obj[12] != null && !obj[12].toString().trim().isEmpty()) {
				batchId = Long.parseLong(obj[12].toString().trim());
			}
			entity.setDeBatchIdAnx2(batchId);

			BigDecimal igstAmt = BigDecimal.ZERO;
			if (obj[13] != null && !obj[13].toString().trim().isEmpty()) {
				String igstAmtStr = (String.valueOf(obj[13])).trim();
				igstAmt = new BigDecimal(igstAmtStr);

			}
			entity.setIgstAmt(igstAmt);
			BigDecimal cgstAmt = BigDecimal.ZERO;
			if (obj[14] != null && !obj[14].toString().trim().isEmpty()) {
				String cgstAmtStr = (String.valueOf(obj[14])).trim();
				cgstAmt = new BigDecimal(cgstAmtStr);

			}
			entity.setCgstAmt(cgstAmt);
			BigDecimal sgstAmt = BigDecimal.ZERO;
			if (obj[15] != null && !obj[15].toString().trim().isEmpty()) {
				String sgstAmtStr = (String.valueOf(obj[15])).trim();
				sgstAmt = new BigDecimal(sgstAmtStr);

			}
			entity.setSgstAmt(sgstAmt);
			BigDecimal cessAmt = BigDecimal.ZERO;
			if (obj[16] != null && !obj[16].toString().trim().isEmpty()) {
				String cessAmtStr = (String.valueOf(obj[16])).trim();
				cessAmt = new BigDecimal(cessAmtStr);

			}
			entity.setCessAmt(cessAmt);
			BigDecimal taxVal = BigDecimal.ZERO;
			if (obj[17] != null && !obj[17].toString().trim().isEmpty()) {
				String taxValStr = (String.valueOf(obj[17])).trim();
				taxVal = new BigDecimal(taxValStr);

			}
			entity.setTaxable(taxVal);

			String retPeriod = (obj[18] != null
					&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]).trim() : null;
			if (retPeriod != null && retPeriod.length() > 6) {
				retPeriod = retPeriod.substring(0, 6);
			}
			entity.setReturnPeriod(retPeriod);

			String action = (obj[19] != null
					&& !obj[19].toString().trim().isEmpty())
							? String.valueOf(obj[19]).trim() : null;
			if (action != null && action.length() > 5) {
				action = action.substring(0, 5);
			}
			entity.setAction(action);

			Integer deRetPeriod = 0;
			if (obj[20] != null && !obj[20].toString().trim().isEmpty()) {
				String deRetPeriodStr = (String.valueOf(obj[20])).trim();
				deRetPeriod = Integer.valueOf(deRetPeriodStr);
			}
			entity.setDerivedRetPeriod(deRetPeriod);

			int isDelete = 0;
			if (obj[21] != null) {
				isDelete = Integer.parseInt(obj[21].toString().trim());
			}
			entity.setDelete(isDelete == 0 ? false : true);

			String cgstinPan = (obj[22] != null
					&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;
			if (cgstinPan != null && cgstinPan.length() > 15) {
				cgstinPan = cgstinPan.substring(0, 15);
			}
			entity.setCgstinPan(cgstinPan);

			String sgstinPan = (obj[23] != null
					&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;
			if (sgstinPan != null && sgstinPan.length() > 15) {
				sgstinPan = sgstinPan.substring(0, 15);
			}
			entity.setSgstinPan(sgstinPan);

			String itc = (obj[24] != null
					&& !obj[24].toString().trim().isEmpty())
							? String.valueOf(obj[24]).trim() : null;
			if (itc != null && itc.length() > 2) {
				itc = itc.substring(0, 2);
			}
			entity.setItc(itc);

			String rfndFlag = (obj[25] != null
					&& !obj[25].toString().trim().isEmpty())
							? String.valueOf(obj[25]).trim() : null;
			if (rfndFlag != null && rfndFlag.length() > 2) {
				rfndFlag = rfndFlag.substring(0, 2);
			}
			entity.setRfndElg(rfndFlag);

			String sec7 = (obj[26] != null
					&& !obj[26].toString().trim().isEmpty())
							? String.valueOf(obj[26]).trim() : null;
			if (sec7 != null && sec7.length() > 2) {
				sec7 = sec7.substring(0, 2);
			}
			entity.setSec7(sec7);
			entity.setTableSection(tableName);

			entities.add(entity);
		}
		return entities;
	}

	private List<GetAnx2B2bInvoicesHeaderEntity> convertObjectArrayToB2BList(
			List<Object[]> b2bList, String tableName) {
		List<GetAnx2B2bInvoicesHeaderEntity> entities = new ArrayList<>();
		for (Object obj[] : b2bList) {
			GetAnx2B2bInvoicesHeaderEntity entity = new GetAnx2B2bInvoicesHeaderEntity();

			String cgstin = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;
			if (cgstin != null && cgstin.length() > 22) {
				cgstin = cgstin.substring(0, 22);
			}
			entity.setCgstin(cgstin);

			String sgstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;
			if (sgstin != null && sgstin.length() > 22) {
				sgstin = sgstin.substring(0, 22);
			}
			entity.setSgstin(sgstin);

			String cfs = (obj[3] != null && !obj[3].toString().trim().isEmpty())
					? String.valueOf(obj[3]).trim() : null;
			if (cfs != null && cfs.length() > 1) {
				cfs = cfs.substring(0, 1);
			}
			entity.setCfs(cfs);

			String chkSum = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
			if (chkSum != null && chkSum.length() > 70) {
				chkSum = chkSum.substring(0, 70);
			}
			entity.setChkSum(chkSum);

			String uploadDate = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;
			LocalDate localUploadDate = DateUtil.parseObjToDate(uploadDate);
			entity.setUploadDate(localUploadDate);

			String invNum = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]).trim() : null;
			if (invNum != null && invNum.length() > 16) {
				invNum = invNum.substring(0, 16);
			}
			entity.setInvNum(invNum);

			String supInvoiceDate = (obj[7] != null
					&& !obj[7].toString().trim().isEmpty())
							? String.valueOf(obj[7]).trim() : null;
			LocalDate localsupInvoiceDate = DateUtil
					.parseObjToDate(supInvoiceDate);
			entity.setInvDate(localsupInvoiceDate);
			BigDecimal invValue = BigDecimal.ZERO;
			if (obj[8] != null && !obj[8].toString().trim().isEmpty()) {
				String invValueStr = (String.valueOf(obj[8])).trim();
				invValue = new BigDecimal(invValueStr);

			}
			entity.setInvValue(invValue);

			String pos = (obj[9] != null && !obj[9].toString().trim().isEmpty())
					? String.valueOf(obj[9]).trim() : null;
			if (pos != null && pos.length() > 5) {
				pos = pos.substring(0, 5);
			}
			entity.setPos(pos);

			String invType = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]).trim() : null;
			if (invType != null && invType.length() > 20) {
				invType = invType.substring(0, 20);
			}
			entity.setInvType(invType);

			Long batchId = 0L;
			if (obj[12] != null && !obj[12].toString().trim().isEmpty()) {
				batchId = Long.parseLong(obj[12].toString().trim());
			}
			entity.setB2bBatchIdAnx2(batchId);

			BigDecimal igstAmt = BigDecimal.ZERO;
			if (obj[13] != null && !obj[13].toString().trim().isEmpty()) {
				String igstAmtStr = (String.valueOf(obj[13])).trim();
				igstAmt = new BigDecimal(igstAmtStr);

			}
			entity.setIgstAmt(igstAmt);
			BigDecimal cgstAmt = BigDecimal.ZERO;
			if (obj[14] != null && !obj[14].toString().trim().isEmpty()) {
				String cgstAmtStr = (String.valueOf(obj[14])).trim();
				cgstAmt = new BigDecimal(cgstAmtStr);

			}
			entity.setCgstAmt(cgstAmt);
			BigDecimal sgstAmt = BigDecimal.ZERO;
			if (obj[15] != null && !obj[15].toString().trim().isEmpty()) {
				String sgstAmtStr = (String.valueOf(obj[15])).trim();
				sgstAmt = new BigDecimal(sgstAmtStr);

			}
			entity.setSgstAmt(sgstAmt);
			BigDecimal cessAmt = BigDecimal.ZERO;
			if (obj[16] != null && !obj[16].toString().trim().isEmpty()) {
				String cessAmtStr = (String.valueOf(obj[16])).trim();
				cessAmt = new BigDecimal(cessAmtStr);

			}
			entity.setCessAmt(cessAmt);
			BigDecimal taxVal = BigDecimal.ZERO;
			if (obj[17] != null && !obj[17].toString().trim().isEmpty()) {
				String taxValStr = (String.valueOf(obj[17])).trim();
				taxVal = new BigDecimal(taxValStr);

			}
			entity.setTaxable(taxVal);

			String retPeriod = (obj[18] != null
					&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]).trim() : null;
			if (retPeriod != null && retPeriod.length() > 6) {
				retPeriod = retPeriod.substring(0, 6);
			}
			entity.setReturnPeriod(retPeriod);

			String action = (obj[19] != null
					&& !obj[19].toString().trim().isEmpty())
							? String.valueOf(obj[19]).trim() : null;
			if (action != null && action.length() > 5) {
				action = action.substring(0, 5);
			}
			entity.setAction(action);

			Integer deRetPeriod = 0;
			if (obj[20] != null && !obj[20].toString().trim().isEmpty()) {
				String deRetPeriodStr = (String.valueOf(obj[20])).trim();
				deRetPeriod = Integer.valueOf(deRetPeriodStr);
			}
			entity.setDerivedRetPeriod(deRetPeriod);

			int isDelete = 0;
			if (obj[21] != null) {
				isDelete = Integer.parseInt(obj[21].toString().trim());
			}
			entity.setDelete(isDelete == 0 ? false : true);

			String cgstinPan = (obj[22] != null
					&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;
			if (cgstinPan != null && cgstinPan.length() > 15) {
				cgstinPan = cgstinPan.substring(0, 15);
			}
			entity.setCgstinPan(cgstinPan);

			String sgstinPan = (obj[23] != null
					&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;
			if (sgstinPan != null && sgstinPan.length() > 15) {
				sgstinPan = sgstinPan.substring(0, 15);
			}
			entity.setSgstinPan(sgstinPan);

			String itc = (obj[24] != null
					&& !obj[24].toString().trim().isEmpty())
							? String.valueOf(obj[24]).trim() : null;
			if (itc != null && itc.length() > 2) {
				itc = itc.substring(0, 2);
			}
			entity.setItc(itc);

			String rfndFlag = (obj[25] != null
					&& !obj[25].toString().trim().isEmpty())
							? String.valueOf(obj[25]).trim() : null;
			if (rfndFlag != null && rfndFlag.length() > 2) {
				rfndFlag = rfndFlag.substring(0, 2);
			}
			entity.setRfndElg(rfndFlag);

			String sec7 = (obj[26] != null
					&& !obj[26].toString().trim().isEmpty())
							? String.valueOf(obj[26]).trim() : null;
			if (sec7 != null && sec7.length() > 2) {
				sec7 = sec7.substring(0, 2);
			}
			entity.setSec7(sec7);

			entity.setTableSection(tableName);
			entities.add(entity);
		}
		return entities;
	}

	/**
	 * Validate the Input JSON to check if 'path', 'fileName' and Group Code are
	 * actually present within the JSON. If not, throw an EWB Exception so that
	 * the file processing status is marked as 'Failed'. Otherwise, extract
	 * these values and return them.
	 * 
	 * @param jsonMessage
	 *            The 'Message' instance passed by the AsyncExecution framework
	 * 
	 */
	private FileArrivalMsgDto extractAndValidateMessage(Message message) {
		FileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance()
				.fromJson(message.getParamsJson(), FileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(logMsg);
		}
		return msg;
	}

}
