package com.ey.advisory.app.caches;

import java.math.BigDecimal;

/**
 * @author Siva.Nandam
 *
 */
public interface RateCache {

	public int findByIgst(BigDecimal igst);

	public int findByCgst(BigDecimal cgst); 

	public int findBySgst(BigDecimal sgst); 
	
	}

