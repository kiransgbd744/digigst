package com.ey.advisory.common.eyfileutils.tabular;

import com.aspose.cells.LightCellsDataHandler;

public interface ExtendedLightCellsDataHandler extends LightCellsDataHandler {
	/**
	 * This method is required due to a limitation in the events raised by 
	 * aspose. Aspose doesn't give an 'endRow' method. Also, the number of 
	 * column events 'startCell' or 'processCell' events raised by Aspose can
	 * be less or greater than the expected number of columns in the excel 
	 * sheet. Because of these reasons, we can handle a complete row, only at
	 * the startRow event of the subsequent row. This will become an issue if
	 * the last row needs to be processed separately. This method can be used
	 * to process such pending unprocessed state that exists after the default
	 * aspose lightcells processing completes. The handleUnprocessedData() 
	 * method should be invoked by the caller immediately after the light cells
	 * processing.
	 * 
	 * If there is no pending state to be handled, then the method doesn't have
	 * to be implemented. The default version just returns without doing 
	 * anything.
	 * 
	 */
	public default void handleUnprocessedData() { return; }
}
