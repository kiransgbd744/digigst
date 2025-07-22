/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import lombok.Data;

@Data
public class GlDocTypeMasterDto {
	 private Long id;
    private String docType;
    private String docTypeMs;
    private String errorCode;
    private String errorDescription;
}
