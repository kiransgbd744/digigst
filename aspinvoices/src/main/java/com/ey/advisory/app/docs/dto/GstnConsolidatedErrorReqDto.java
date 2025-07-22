package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


	public class GstnConsolidatedErrorReqDto extends SearchCriteria{
		
		public GstnConsolidatedErrorReqDto() {
			super(SearchTypeConstants.REPORTS_SEARCH);
		}
		
		@Expose
		@SerializedName("gstin")
		private String gstin;

		@Expose
		@SerializedName("taxPeriod")
		private String taxPeriod;

		@Expose
		@SerializedName("gstnRefId")
		private String gstnRefId;
		
		@Expose
		@SerializedName("answer")
		private Integer answer;
		
		@Expose
		@SerializedName("entityId")
		private List<Long> entityId;
		
		@Expose
		@SerializedName("dataType")
		private String dataType;

		
		@Expose
		@SerializedName("type")
		private String type;
		
		@Expose
		@SerializedName("returnType")
		private String returnType;
		
		public String getReturnType() {
			return returnType;
		}

		public void setReturnType(String returnType) {
			this.returnType = returnType;
		}

		@Expose
		@SerializedName("dataSecAttrs")
		private Map<String, List<String>> dataSecAttrs;

		public String getGstin() {
			return gstin;
		}

		public void setGstin(String gstin) {
			this.gstin = gstin;
		}

		public String getTaxPeriod() {
			return taxPeriod;
		}

		public void setTaxPeriod(String taxPeriod) {
			this.taxPeriod = taxPeriod;
		}

		public String getGstnRefId() {
			return gstnRefId;
		}

		public void setGstnRefId(String gstnRefId) {
			this.gstnRefId = gstnRefId;
		}

		public Integer getAnswer() {
			return answer;
		}

		public void setAnswer(Integer answer) {
			this.answer = answer;
		}

		public List<Long> getEntityId() {
			return entityId;
		}

		public void setEntityId(List<Long> entityId) {
			this.entityId = entityId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Map<String, List<String>> getDataSecAttrs() {
			return dataSecAttrs;
		}

		public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
			this.dataSecAttrs = dataSecAttrs;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
		
		
	}



