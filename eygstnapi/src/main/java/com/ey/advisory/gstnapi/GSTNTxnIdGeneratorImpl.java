package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIParams;

/**
 * This class uses a basic implementation of the transaction id generation
 * process. This does not ensure unique Id, but it will ensure that there are
 * very few clashes. Since GSTN is not validating this id for uniqueness,
 * we can use this approach. Ensuring absolute uniqueness incurs much more 
 * overhead (like maintaining a sequence in the DB). We don't have to go that
 * far to generate a unique transaction id. The current approach ensures that
 * there can be 1000 API hits to GSTN within a millisecond, which is far 
 * far higher than the usual frequency. Hence, we're safe here.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("GSTNTxnIdGeneratorImpl")
public class GSTNTxnIdGeneratorImpl implements GSTNTxnIdGenerator {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(GSTNTxnIdGeneratorImpl.class);
	
	@Override
	public String generateTxnId(APIParams params) {
		
		// Create a String builder with the max length of transaction no
		// (i.e. 32 characters)
		String txnNo = new StringBuilder(32)
			.append("EY")	// Concatenate the initials of the GSP as per
							// GSTN API suggestion
			.append(LocalDateTime.now()
				       .format(DateTimeFormatter.ofPattern(
				    		   	"yyyyMMddHHmmssSSS"))) // Concatenate the 
							// Date and time up to milli second precision.
		    .append("RET") 	// Concatenate a 3 Character separator string. 
			.append(String.format("%04d", 
					GenUtil.getRandomNumberInRange(1, 1000))) // Concatenate
							// A random number in the range of 1 to 1000.
							// This is to accomodate the fact that there can 
							// be more than one invocation per millisecond
							// especially in a multi node clustered deployment.
			
			.toString();	// Extract the string for logging and returning.
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "API Trans ID = " + txnNo;
			LOGGER.debug(msg);
		}
		return txnNo;
	}


}
