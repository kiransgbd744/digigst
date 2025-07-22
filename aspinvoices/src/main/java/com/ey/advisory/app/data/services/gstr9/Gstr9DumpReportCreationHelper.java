package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9TransAdvAdjAmdEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransAdvRecAmdEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransB2CSB2CSAEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransGstr1TransEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransGstr3bSmryEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransGstr3bTaxPrdEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransHsnEntity;
import com.ey.advisory.app.data.entities.gstr9.Gstr9TransNilNonExtEntity;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransAdvAdjAmdRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransAdvRecAmdRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransB2CSB2CSARepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransGstr1TransRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransGstr3bSmryRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransGstr3bTaxPrdRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransHsnRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9TransNilNonExtRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.ReportConfigFactory;
import com.opencsv.bean.StatefulBeanToCsv;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Component("Gstr9DumpReportCreationHelper")
@Slf4j
public class Gstr9DumpReportCreationHelper {

	@Autowired
	Gstr9TransAdvAdjAmdRepository gstr9AdvAdjRepo;

	@Autowired
	Gstr9TransAdvRecAmdRepository gstr9AdvRecRepo;

	@Autowired
	Gstr9TransB2CSB2CSARepository gstr9B2CSB2CSARepo;

	@Autowired
	Gstr9TransGstr1TransRepository gstr9Gstr1TransRepo;

	@Autowired
	Gstr9TransHsnRepository gstr9TransHsnRepo;

	@Autowired
	Gstr9TransNilNonExtRepository gstr9NilNonExtRepo;

	@Autowired
	Gstr9TransGstr3bSmryRepository gstr9Gstr3BSmryRepo;

	@Autowired
	Gstr9TransGstr3bTaxPrdRepository gstr9Gstr3BTxPrdRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	public EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	public Environment env;

	@Autowired
	@Qualifier("ReportConfigFactoryImpl")
	ReportConfigFactory reportConfigFactory;

	public void WriteTransGstr1toCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransGstr1TransEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransGstr1TransEntity> responseFromDao = gstr9Gstr1TransRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransGstr1TransEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from TransGstr1 "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransB2CSB2CSAtoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransB2CSB2CSAEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransB2CSB2CSAEntity> responseFromDao = gstr9B2CSB2CSARepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransB2CSB2CSAEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from B2CSB2CSA "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransAdvRecAmdtoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransAdvRecAmdEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {
				List<Gstr9TransAdvRecAmdEntity> responseFromDao = gstr9AdvRecRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransAdvRecAmdEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from AdvRecAmd "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransAdvAdjAmdtoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransAdvAdjAmdEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransAdvAdjAmdEntity> responseFromDao = gstr9AdvAdjRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransAdvAdjAmdEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from AdvAdjAmd "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransNilNonExttoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransNilNonExtEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransNilNonExtEntity> responseFromDao = gstr9NilNonExtRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransNilNonExtEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from NilNonExt "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransHsnSummtoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransHsnEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransHsnEntity> responseFromDao = gstr9TransHsnRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransHsnEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from TransHsnSum "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransGstr3bSmrytoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransGstr3bSmryEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransGstr3bSmryEntity> responseFromDao = gstr9Gstr3BSmryRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransGstr3bSmryEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}
				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from Gstr3bSummary "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

	public void WriteTransGstr3bTxPrdtoCsv(String reportType,
			StatefulBeanToCsv<Gstr9TransGstr3bTaxPrdEntity> beanWriter,
			String reportCateg, String sGstin, String fy, List<Long> chunkIds) {
		int srNoCounter = 1;
		try {
			for (Long chunkId : chunkIds) {

				List<Gstr9TransGstr3bTaxPrdEntity> responseFromDao = gstr9Gstr3BTxPrdRepo
						.findBySgstinAndGstr9FyAndIsDeleteAndChunkId(sGstin, fy,
								false, chunkId);
				for (Gstr9TransGstr3bTaxPrdEntity entity : responseFromDao) {
					entity.setRetPeriod("'" + entity.getRetPeriod());
					entity.setSrNo(srNoCounter++);
				}

				if (responseFromDao != null && !responseFromDao.isEmpty()) {
					beanWriter.write(responseFromDao);
				}
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured while fetching data from Gstr3bTaxPaid "
							+ "for ChunkIds %s and gstin %s and returnPeriod %s",
					chunkIds, sGstin, fy);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

}
