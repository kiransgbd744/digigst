/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.anx2.Anx2InvoicesAtGstn;
import com.ey.advisory.app.services.jobs.anx2.RequestIdWiseUtil;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2GstnGetJobHandlerImpl")
@Slf4j
public class Anx2GstnGetJobHandlerImpl implements Anx2GstnGetJobHandler {
	
	@Autowired
	@Qualifier("anx2B2bInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn b2b;

	@Autowired
	@Qualifier("anx2SezwpInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn sezwp;

	@Autowired
	@Qualifier("anx2SezwopInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn sezwop;

	@Autowired
	@Qualifier("anx2DeInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn de;
	
	@Autowired
	@Qualifier("anx2IsdcInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn isdc;
	
	@Autowired
	@Qualifier("Anx2ItcSumryInvoicesAtGstnImpl")
	private Anx2InvoicesAtGstn itc;
	
	@Autowired
	private RequestIdWiseUtil reqIdUtil;
	
	@Override
	public void anx2GstnGetCall(String jsonReq,
			String groupCode) {
		
		//Extra logic for Request 
		reqIdUtil.updateStatus(jsonReq, groupCode, APIConstants.INITIATED);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Started.", APIConstants.B2B);
		}
		b2b.findInvFromGstn(jsonReq, groupCode, APIConstants.B2B);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Ended.", APIConstants.B2B);
			LOGGER.debug("Anx2 {} Get Call is Started.", APIConstants.DE);
		}
		de.findInvFromGstn(jsonReq, groupCode, APIConstants.DE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Ended.", APIConstants.DE);
			LOGGER.debug("Anx2 {} Get Call is Started.", APIConstants.SEZWP);
		}
		sezwp.findInvFromGstn(jsonReq, groupCode, APIConstants.SEZWP);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Ended.", APIConstants.SEZWP);
			LOGGER.debug("Anx2 {} Get Call is Started.", APIConstants.SEZWOP);
		}
		sezwop.findInvFromGstn(jsonReq, groupCode, APIConstants.SEZWOP);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Ended.", APIConstants.SEZWOP);
			LOGGER.debug("Anx2 {} Get Call is Started.", APIConstants.ISDC);
		}
		isdc.findInvFromGstn(jsonReq, groupCode, APIConstants.ISDC);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Ended.", APIConstants.ISDC);
			LOGGER.debug("Anx2 {} Get Call is Started.", APIConstants.ITCSUM);
		}
		itc.findInvFromGstn(jsonReq, groupCode, APIConstants.ITCSUM);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx2 {} Get Call is Ended.", APIConstants.ITCSUM);
		}
		
		//Extra logic for Request 
		reqIdUtil.updateStatus(jsonReq, groupCode, APIConstants.COMPLETED);

	}
	
	
}
