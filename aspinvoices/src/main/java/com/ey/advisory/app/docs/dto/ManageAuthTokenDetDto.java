package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ManageAuthTokenDetDto {
	
 @Expose
 private String entityName;
 
 @Expose
 private String gstin;
 
 @Expose
 private String mobileNo;
 
 @Expose
 private String email;
 
 @Expose
 private String status;
 
 @Expose
 private boolean action;

}
