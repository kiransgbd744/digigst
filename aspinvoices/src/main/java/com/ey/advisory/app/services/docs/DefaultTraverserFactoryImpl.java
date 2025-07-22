package com.ey.advisory.app.services.docs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.FileProcessingException;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * This class is responsible for providing default implementation methods 
 * to Traverser Factory interface. 
 * The default implementation methods determines the right Traverser 
 * based on the file extension
 *
 */
@Service("DefaultTraverserFactoryImpl")
public class DefaultTraverserFactoryImpl implements TraverserFactory {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultTraverserFactoryImpl.class);

	@Override
	public TabularDataSourceTraverser getTraverser(String fileName) {
		String fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
		
		TabularDataSourceTraverser traverser = null;
		try {
			switch (fileExt) {
			case JobStatusConstants.XLSX_TYPE:
				traverser = (TabularDataSourceTraverser) StaticContextHolder
						.getBean("XlsxLightCellsTraverser",
								TabularDataSourceTraverser.class);
				break;
			case JobStatusConstants.XLSM_TYPE:
				traverser = (TabularDataSourceTraverser) StaticContextHolder
						.getBean("XlsxLightCellsTraverser",
								TabularDataSourceTraverser.class);
				break;
			case JobStatusConstants.CSV_TYPE:
				traverser = (TabularDataSourceTraverser) StaticContextHolder
						.getBean("OpenCSVCharSepValuesFileTraverser",
								TabularDataSourceTraverser.class);
				break;
			default:
				String errMsg = String
						.format("No Traverser available for the type: '%s' "
								+ "for file: '%s'", fileExt, fileName);
				LOGGER.info(errMsg);
				throw new FileProcessingException(errMsg);

			}

		} catch (Exception ex) {
			String errMsg = "Error while determining the traverser";
			LOGGER.error(errMsg, ex);
			throw new FileProcessingException(errMsg, ex);
		}
		return traverser;
	}

}
