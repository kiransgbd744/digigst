package com.ey.advisory.app.glrecon;


import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GlFileStatusDto {

	@Expose
	private Long fileId;
	
	@Expose
	private String fileType;
		
	@Expose
	private String fileName;

	@Expose
	private String status;

	@Expose
	private String uploadedBy;
	
	@Expose
	private String createdOn;
	
	@Expose
	private Long entityId;
	
	@Expose
	private String errorDescription;
	
	@Expose
	private Boolean downloadFlag;
}
