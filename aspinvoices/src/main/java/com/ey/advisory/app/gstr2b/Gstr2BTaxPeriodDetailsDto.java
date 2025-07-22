package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
*
* @author Hema G M
*
*/

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2BTaxPeriodDetailsDto {
	
		@Expose
		@SerializedName("taxPeriod")
		private String taxPeriod;

		@Expose
		@SerializedName("status")
		private String apiStatus;

		@Expose
		@SerializedName("initiatedOn")
		private LocalDateTime initiatedOn;

		@Expose
		@SerializedName("reportStatus")
		private String reportStatus;
		
		@Expose
		@SerializedName("filePath")
		private String filepath;

		@Expose
		@SerializedName("downloadbleFlag")
		private Boolean downloadbleFlag;
		


	}

