/**
 * 
 */
package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Arun.KA
 *
 */
public class DocIssueHeader {
	
	@Expose
	@SerializedName("doc_num")
	private String docnum;
	
	@Expose
	@SerializedName("docs")
	private List<DocIssueDetails> docIssueDetails;

	public String getDocnum() {
		return docnum;
	}

	public void setDocnum(String docnum) {
		this.docnum = docnum;
	}

	public List<DocIssueDetails> getDocIssueDetails() {
		return docIssueDetails;
	}

	public void setDocIssueDetails(List<DocIssueDetails> docIssueDetails) {
		this.docIssueDetails = docIssueDetails;
	}
	
	

}


