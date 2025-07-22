package com.ey.advisory.app.gstr2bpr.reconresponse.upload;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class GSTR2bprAutoReconErrInfoDTO
		extends GSTR2bprAutoReconRespUploadDTO {

	private String infoId;
	private String infoDescription;
	private String errorId;
	private String errorDescription;

}
