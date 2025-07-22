package com.ey.advisory.app.service.upload.way3recon;

import java.util.List;

import com.ey.advisory.app.data.entities.client.asprecon.EwbUploadProcessedHeaderEntity;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;

/**
 * 
 * @author vishal.verma
 *
 */

public class EwbGstinValidator implements BusinessRuleValidator<EwbUploadProcessedHeaderEntity>{{

}

@Override
public List<ProcessingResult> validate(EwbUploadProcessedHeaderEntity document, ProcessingContext context) {
	// TODO Auto-generated method stub
	return null;
}
}