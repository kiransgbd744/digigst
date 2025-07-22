/**
 * 
 */
package com.ey.advisory.app.data.views.client;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class ITC04EntitylevelDto {

	private String gstin;
	private String tableDesc;
	private String availabledigigstCount;
	private String availabledigigstQuantity;
	private String availablelossesQuantity;
	private String availableTaxablevalue;
	private String availablegstnCount;
	private String availablegstnTaxablevalue;
	private String diffCount;
	private String diffTaxablevalue;
	private String returnPeriod;
	private String qreturnPeriod;
	
	public ITC04EntitylevelDto() {
		
	}
	
	/**
	 * @param gstin
	 * @param tableDesc
	 * @param availabledigigstCount
	 * @param availabledigigstQuantity
	 * @param availablelossesQuantity
	 * @param availableTaxablevalue
	 * @param availablegstnCount
	 * @param availablegstnTaxablevalue
	 * @param diffCount
	 * @param diffTaxablevalue
	 * @param returnPeriod
	 * @param qreturnPeriod
	 */
	public ITC04EntitylevelDto(String gstin, String tableDesc, String availabledigigstCount,
			String availabledigigstQuantity, String availablelossesQuantity, String availableTaxablevalue,
			String availablegstnCount, String availablegstnTaxablevalue, String diffCount, String diffTaxablevalue,
			String returnPeriod, String qreturnPeriod) {
		super();
		this.gstin = gstin;
		this.tableDesc = tableDesc;
		this.availabledigigstCount = availabledigigstCount;
		this.availabledigigstQuantity = availabledigigstQuantity;
		this.availablelossesQuantity = availablelossesQuantity;
		this.availableTaxablevalue = availableTaxablevalue;
		this.availablegstnCount = availablegstnCount;
		this.availablegstnTaxablevalue = availablegstnTaxablevalue;
		this.diffCount = diffCount;
		this.diffTaxablevalue = diffTaxablevalue;
		this.returnPeriod = returnPeriod;
		this.qreturnPeriod = qreturnPeriod;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

	public String getAvailabledigigstCount() {
		return availabledigigstCount;
	}

	public void setAvailabledigigstCount(String availabledigigstCount) {
		this.availabledigigstCount = availabledigigstCount;
	}

	public String getAvailabledigigstQuantity() {
		return availabledigigstQuantity;
	}

	public void setAvailabledigigstQuantity(String availabledigigstQuantity) {
		this.availabledigigstQuantity = availabledigigstQuantity;
	}

	public String getAvailablelossesQuantity() {
		return availablelossesQuantity;
	}

	public void setAvailablelossesQuantity(String availablelossesQuantity) {
		this.availablelossesQuantity = availablelossesQuantity;
	}

	public String getAvailableTaxablevalue() {
		return availableTaxablevalue;
	}

	public void setAvailableTaxablevalue(String availableTaxablevalue) {
		this.availableTaxablevalue = availableTaxablevalue;
	}

	public String getAvailablegstnCount() {
		return availablegstnCount;
	}

	public void setAvailablegstnCount(String availablegstnCount) {
		this.availablegstnCount = availablegstnCount;
	}

	public String getAvailablegstnTaxablevalue() {
		return availablegstnTaxablevalue;
	}

	public void setAvailablegstnTaxablevalue(String availablegstnTaxablevalue) {
		this.availablegstnTaxablevalue = availablegstnTaxablevalue;
	}

	public String getDiffCount() {
		return diffCount;
	}

	public void setDiffCount(String diffCount) {
		this.diffCount = diffCount;
	}

	public String getDiffTaxablevalue() {
		return diffTaxablevalue;
	}

	public void setDiffTaxablevalue(String diffTaxablevalue) {
		this.diffTaxablevalue = diffTaxablevalue;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getQreturnPeriod() {
		return qreturnPeriod;
	}

	public void setQreturnPeriod(String qreturnPeriod) {
		this.qreturnPeriod = qreturnPeriod;
	}

	@Override
	public String toString() {
		return "ITC04EntitylevelDto [gstin=" + gstin + ", tableDesc=" + tableDesc + ", availabledigigstCount="
				+ availabledigigstCount + ", availabledigigstQuantity=" + availabledigigstQuantity
				+ ", availablelossesQuantity=" + availablelossesQuantity + ", availableTaxablevalue="
				+ availableTaxablevalue + ", availablegstnCount=" + availablegstnCount + ", availablegstnTaxablevalue="
				+ availablegstnTaxablevalue + ", diffCount=" + diffCount + ", diffTaxablevalue=" + diffTaxablevalue
				+ ", returnPeriod=" + returnPeriod + ", qreturnPeriod=" + qreturnPeriod + "]";
	}

}
