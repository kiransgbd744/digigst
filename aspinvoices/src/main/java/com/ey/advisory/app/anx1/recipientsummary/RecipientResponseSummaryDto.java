/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Arun KA
 *
 **/

@Getter
@Setter
@NoArgsConstructor
public class RecipientResponseSummaryDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	private String cPan;

	@Expose
	private String cName;

	@Expose
	private String tableType;

	@Expose
	private String cgstin;

	@Expose
	private String docType;

	@Expose
	private String level;

	@Expose
	private Integer notSavedCount = 0;

	@Expose
	private Integer savedCount = 0;

	@Expose
	private Integer acceptedCount = 0;

	@Expose
	private Integer rejectedCount = 0;

	@Expose
	private Integer pendingCount = 0;

	@Expose
	private Integer noActionCount = 0;

	public String getKey() {
		if ("L1".equals(level))
			return cPan;
		if ("L2".equals(level))
			return cPan + "#" + cgstin;
		return cPan + "#" + cgstin + "#" + tableType + "#" + docType;
	}

	public RecipientResponseSummaryDto(String level) {
		this.level = level;
	}

	public RecipientResponseSummaryDto(String level, String cPan,String cName,
			String tableType, String cgstin, String docType,
			Integer notSavedCount, Integer savedCount, Integer acceptedCount,
			Integer rejectedCount, Integer pendingCount,
			Integer noActionCount) {
		super();
		this.level = level;
		this.cPan = cPan;
		this.cName = cName;
		this.tableType = tableType;
		this.cgstin = cgstin;
		this.docType = docType;
		this.notSavedCount = notSavedCount;
		this.savedCount = savedCount;
		this.acceptedCount = acceptedCount;
		this.rejectedCount = rejectedCount;
		this.pendingCount = pendingCount;
		this.noActionCount = noActionCount;
	}

}
