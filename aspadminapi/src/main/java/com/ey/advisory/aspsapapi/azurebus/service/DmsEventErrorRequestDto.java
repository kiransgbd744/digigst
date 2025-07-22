package com.ey.advisory.aspsapapi.azurebus.service;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */
@Data
public class DmsEventErrorRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("Uploaded_Filename")
	@Expose
	private String uploadedFileName;

	@SerializedName("Uploaded_Source_Filename")
	@Expose
	private String uploadedSourceFileName;

	@SerializedName("Sheet_name_if_excel")
	@Expose
	private String sheetName;

	@SerializedName("Error_target_column")
	@Expose
	private String errorColumn;

	@SerializedName("Error_description")
	@Expose
	private String errorDesc;

	@SerializedName("Rule_Number")
	@Expose
	private String ruleNumber;

	@SerializedName("Rule_Name")
	@Expose
	private String ruleName;

	@SerializedName("Rule_Type")
	@Expose
	private String ruleType;

	@SerializedName("Sub_Rule_Number")
	@Expose
	private String subRuleNumber;

	@SerializedName("Source_File_Name")
	@Expose
	private String sourceFileName;

	@SerializedName("Target_File_Name")
	@Expose
	private String targetFileName;

}
