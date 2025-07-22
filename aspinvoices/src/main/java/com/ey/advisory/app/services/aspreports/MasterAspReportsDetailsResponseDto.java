package com.ey.advisory.app.services.aspreports;

import java.util.ArrayList;
import java.util.List;

public class MasterAspReportsDetailsResponseDto {

	private List<MasterAspReportsDocTypeDto> docType = new ArrayList<>();
	private List<MasterAspReportsSupplyTypeDto> supplyType = new ArrayList<>();
	private List<MasterAspReportsAttributesDto> attributes = new ArrayList<>();
	
	public List<MasterAspReportsDocTypeDto> getDocType() {
		return docType;
	}

	public void setDocType(List<MasterAspReportsDocTypeDto> docType) {
		this.docType = docType;
	}

	public List<MasterAspReportsSupplyTypeDto> getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(List<MasterAspReportsSupplyTypeDto> supplyType) {
		this.supplyType = supplyType;
	}

	public List<MasterAspReportsAttributesDto> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<MasterAspReportsAttributesDto> attributes) {
		this.attributes = attributes;
	}

	public class MasterAspReportsDocTypeDto {

		private String docTypeKey;
		private String docTypeName;

		public String getDocTypeKey() {
			return docTypeKey;
		}

		public void setDocTypeKey(String docTypeKey) {
			this.docTypeKey = docTypeKey;
		}

		public String getDocTypeName() {
			return docTypeName;
		}

		public void setDocTypeName(String docTypeName) {
			this.docTypeName = docTypeName;
		}
	}

	public class MasterAspReportsSupplyTypeDto {

		private String supplyTypeKey;
		private String supplyTypeName;

		public String getSupplyTypeKey() {
			return supplyTypeKey;
		}

		public void setSupplyTypeKey(String supplyTypeKey) {
			this.supplyTypeKey = supplyTypeKey;
		}

		public String getSupplyTypeName() {
			return supplyTypeName;
		}

		public void setSupplyTypeName(String supplyTypeName) {
			this.supplyTypeName = supplyTypeName;
		}
	}

	public class MasterAspReportsAttributesDto {

		private String attributesName;

		public String getAttributesName() {
			return attributesName;
		}

		public void setAttributesName(String attributesName) {
			this.attributesName = attributesName;
		}
	}
}
