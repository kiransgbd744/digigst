package com.ey.advisory.app.services.docs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.weaver.ast.HasAnnotation;

import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
public class Gstr3BFileUploadRowHandler<K> implements RowHandler {

	private Object[] headerData = null;
	private DataBlockKeyBuilder<K> keyBuilder;
	
	Gstr3BFileUploadRowHandler() {

	}

	public Object[] getHeaderData() {
		return headerData;
	}
	
	/**
	 * The List to be populated with the the data from the file.
	 */
	private  List<Object[]> fileList = new ArrayList<>();
	private Map<String, List<Object[]>> documentMap = new LinkedHashMap<>();


	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Row No in Gstr3BFileUploadRowHandler " + rowNo);
			LOGGER.debug("Row in Gstr3BFileUploadRowHandler " + row);
		}
		
		Object[] arr = row.clone();
		
		boolean isRowEmpty = true;
		for (Object object : arr) {
		    if (object != null) {
			isRowEmpty = false;
		    }
		}
		String key = (String) keyBuilder.buildDataBlockKey(arr, layout);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("key in Gstr3BFileUploadRowHandler " + key);
		}
		if (!isRowEmpty) {
		    documentMap.computeIfAbsent(key, k -> new ArrayList<>()).add(arr);
			fileList.add(arr);
		}
		return true;
		
	}

	public Gstr3BFileUploadRowHandler(DataBlockKeyBuilder<K> keyBuilder) {
		this.keyBuilder = keyBuilder;
	}

	@Override
	public void handleHeaderRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		// clone the shared array passed by the framework.
		Object[] arr = row.clone();
		headerData = arr;
	}
	
	public List<Object[]> getFileList() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getFileList in Gstr3BFileUploadRowHandler"
					+ fileList.toString());
		}
		return fileList;
	}
	
	public Map<String, List<Object[]>> getDocumentMap() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("documentMap in Gstr3BFileUploadRowHandler"
					+ documentMap.toString());
		}
		return documentMap;
	}

}
