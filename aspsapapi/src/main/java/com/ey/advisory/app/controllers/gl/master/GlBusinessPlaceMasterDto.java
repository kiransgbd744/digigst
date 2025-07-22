/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import lombok.Data;

@Data
public class GlBusinessPlaceMasterDto {
	 private Long id;
    private String businessPlace;
    private String businessArea;
    private String plantCode;
    private String profitCentre;
    private String costCentre;
    private String gstin;
    private String errorCode;
    private String errorDescription;
}

