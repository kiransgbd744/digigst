package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class GSTR2AAutoReconErrInfoDTO extends GSTR2AAutoReconRespUploadDTO {

	
	private String infoId;
	private String infoDescription;
	private String errorId;
	private String errorDescription;

}
