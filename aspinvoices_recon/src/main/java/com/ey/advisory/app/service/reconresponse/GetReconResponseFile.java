package com.ey.advisory.app.service.reconresponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.google.common.collect.Streams;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GetReconResponseFile")
public class GetReconResponseFile {

	private static List<String> HEADER_LIST = null;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("UpdateReconResponseRowHandler")
	private ObjectFactory<RowHandler> rowHandlerFactory;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

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
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);
			TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 141);

			@SuppressWarnings("rawtypes")
			FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();
			traverser.traverseHeaderOnly(fin, layout, rowHandler, null);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<String> actualHeaderNames = new ArrayList(
					Arrays.asList(rowHandler.getHeaderRow()));
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("User upload file headers names %s "
						+ "and header count %d", 
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != 141) {
				String msg = "The number of columns in the file should be 141. "
						+ "Aborting the file processing.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

			List<String> expectedHeaderNames = null;
			expectedHeaderNames = loadExpectedHeaders();
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Expected User response headers names "
						+ "%s and header count %d", 
					expectedHeaderNames.toString(), expectedHeaderNames.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(expectedHeaderNames,
					actualHeaderNames);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Comparing two files header name "
						+ "and count: %s", isMatch);
				LOGGER.debug(msg);
			}
			
			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				markFileAsFailed(fileId, msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			markFileAsFailed(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private void markFileAsFailed(Long fileId, String reason) {
		try {
			fileStatusRepository.updateFileStatus(fileId, "Failed");
		} catch (Exception ex) {
			String msg = "[SEVERE] Unable to mark the file as failed. "
					+ "Reason for file failure is: [" + reason + "]";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private void processRecords(String fileName, String folderName,
			Long fileId) {

		try {
			InputStream fin = getFileInpStream(fileName, folderName);
			TabularDataSourceTraverser traverser = traverserFactory
					.getTraverser(fileName);

			UpdateReconResponseRowHandler reconRowHandler = 
					(UpdateReconResponseRowHandler) rowHandlerFactory.getObject();
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Setting file Id in process records "
						+ ": %s",fileId.toString());
				LOGGER.debug(msg);
			}
			reconRowHandler.setFileId(fileId);
			TabularDataLayout dataLayout = new DummyTabularDataLayout(0, 1, 0,
					142);
			traverser.traverse(fin, dataLayout, reconRowHandler, null);
			// Calling the processPendingObjects will ensure that the
			// pending objects that are collected in the list will be handled
			// in the end.
			reconRowHandler.processPendingObjects();
		} catch (Exception ex) {
			String msg = (ex instanceof AppException) ? ex.getMessage()
					: "Error occured while processing the file";
			LOGGER.error(msg, ex);
			markFileAsFailed(fileId, msg);
			throw (ex instanceof AppException) ? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	public void validateReconResponse(Long fileId, String fileName,
			String folderName) {

		LOGGER.debug("Validating user uploaded header with template header");
		validateHeaders(fileName, folderName, fileId);
		
		LOGGER.debug("Processing data records ");
		processRecords(fileName, folderName, fileId);

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

	private synchronized List<String> loadExpectedHeaders() {

		try {
			// Load the excel sheet and return the list of headers
			// using the traverseHeaderOnly method.
			if (HEADER_LIST == null) {
				HEADER_LIST = loadExpectedHeadersFromTemplate();
			}

			return HEADER_LIST;
		} catch (Exception ex) {
			String msg = "Failed to read the headers from the template file.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	private List<String> loadExpectedHeadersFromTemplate()
			throws FileNotFoundException {

		ClassLoader classLoader = this.getClass().getClassLoader();
		URL template_Dir = classLoader.getResource("ReportTemplates/");
		String templatePath = template_Dir.getPath()
				+ "ReconReportTemplate.csv";
		FileInputStream fin = new FileInputStream(new File(templatePath));

		TabularDataSourceTraverser traverser = traverserFactory
				.getTraverser("ReconReportTemplate.csv");
		TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, 141);

		@SuppressWarnings("rawtypes")
		FileUploadDocRowHandler rowHandler = new FileUploadDocRowHandler<>();

		traverser.traverseHeaderOnly(fin, layout, rowHandler, null);

		/*List<Object[]> headerNamesobj = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();*/

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> templateHeaderNames = new ArrayList(
				Arrays.asList(rowHandler.getHeaderRow()));

		return templateHeaderNames;
	}
}
