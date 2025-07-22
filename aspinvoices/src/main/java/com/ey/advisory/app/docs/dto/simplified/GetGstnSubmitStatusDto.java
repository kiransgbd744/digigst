package com.ey.advisory.app.docs.dto.simplified;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GetGstnSubmitStatusDto {

	
	private String status;
    private String timeStamp;
    private String gstnStatus;
    private String aspStatus;
    private LocalDateTime modified_on;
    
	
	
}
