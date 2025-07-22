package com.ey.advisory.app.services.common;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.ey.advisory.core.async.domain.master.EinvGstinMasterEntity;
import com.ey.advisory.core.async.repositories.master.EinvMasterGstinRepository;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra
 *
 */

@Slf4j
@Component("GSTINFileArrivalService")
@Transactional(value = "masterTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
public class GSTINFileArrivalService {

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("EinvMasterGstinRepository")
	private EinvMasterGstinRepository repo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	private static final List<String> headerList = Arrays.asList("GSTIN",
			"TRADENAME");

	public void validateGSTINfile(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");
		validateHeaders(fileName, folderName, fileId);

		try {
			// reading row data

			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			TabularDataLayout layout = new DummyTabularDataLayout(2);

			FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverse(fin, layout, rowHandler, null);

			List<Object[]> fileList = ((FileUploadDocRowHandler<?>) rowHandler)
					.getFileUploadList();

			if (fileList == null || fileList.isEmpty()) {

				String msg = "Failed Empty file..";
				LOGGER.error(msg);
				throw new AppException(msg);

			}

			// convert respDto to entity & save to db.
			List<EinvGstinMasterEntity> entityList = fileList.stream()
					.map(o -> convertToEntity(o))
					.collect(Collectors.toCollection(ArrayList::new));

			if (!entityList.isEmpty()) {
				entityList.forEach(e -> {

					try {

						repo.save(e);

					} catch (DataIntegrityViolationException ex) {

						if (LOGGER.isWarnEnabled()) {
							LOGGER.warn("Trying to insert duplicate gstin "
									+ "in Einvoice master table. "
									+ "Ignoring the error");
						}
					}
				});
			}

		}

		catch (Exception ex) {

			String msg = "Failed, error while reading file.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private InputStream getFileInpStream(String fileName, String folderName) {

		try {
			Document doc = DocumentUtility.downloadDocument(fileName,
					folderName);
			return doc.getContentStream().getStream();
		} catch (Exception ex) {
			throw new AppException(
					"Error occured while " + "reading the file " + fileName,
					ex);
		}
	}

	private void validateHeaders(String fileName, String folderName,
			Long fileId) {
		try {
			InputStream fin = getFileInpStream(fileName, folderName);

			String fileName1 = fileName.toLowerCase();

			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName1);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 2);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverseHeaderOnly(fin, layout, rowHandler, null);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<String> actualHeaderNames = new ArrayList(
					Arrays.asList(rowHandler.getHeaderRow()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != 2) {
				String msg = "The number of columns in the file should be 1. "
						+ "Aborting the file processing.";
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = headerList;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						expectedHeaderNames.toString(),
						expectedHeaderNames.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(expectedHeaderNames,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private boolean headersMatch(List<String> expected, List<String> actual) {
		return !Streams
				.zip(actual.stream(), expected.stream(),
						(a, e) -> createPair(a, e))
				.filter(p -> !p.getValue0().equals(p.getValue1())).findAny()
				.isPresent();
	}

	private Pair<String, String> createPair(String val1, String val2) {
		String val1Str = (val1 == null) ? "" : val1.trim().toUpperCase();
		String val2Str = (val2 == null) ? "" : val2.trim().toUpperCase();
		return new Pair<>(val1Str, val2Str);
	}

	private EinvGstinMasterEntity convertToEntity(Object[] arr) {

		EinvGstinMasterEntity entity = new EinvGstinMasterEntity();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"persisting the file data into db : %s - %s", arr[0],
					arr.length + "");
			LOGGER.debug(msg);
		}
		String gstin = arr[0] != null ? arr[0].toString().toUpperCase() : null;
		if (!Strings.isNullOrEmpty(gstin)) {
			entity.setGstin(gstin);
			entity.setTradeName(arr[1] != null ? arr[1].toString() : null);
			entity.setCreatedDate(LocalDateTime.now());
			entity.setSource("NIC");
			entity.setPan(gstin.substring(2, 12));
		}

		return entity;
	}
	
}
