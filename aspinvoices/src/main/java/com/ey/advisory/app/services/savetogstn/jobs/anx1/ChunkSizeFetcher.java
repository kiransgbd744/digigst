/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx1;

/**
 * @author Hemasundar.J
 *
 */
public interface ChunkSizeFetcher {

	/**
	 * This will gives the max size which allowed in GSTN SAVE Batch/chunk.
	 * @return
	 */
	public int getSize();
	
	/**
	 * This will gives the time delay configuration which is configured in DB for
	 * Original invoices save retry with a delay of.
	 * @return
	 */
	public Long getDelayOf();

}
