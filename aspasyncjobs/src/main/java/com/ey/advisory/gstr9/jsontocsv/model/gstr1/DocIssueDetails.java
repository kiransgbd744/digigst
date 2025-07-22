/**
 * 
 */
package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Arun.KA
 *
 */
public class DocIssueDetails {
	
	@Expose
	@SerializedName("num")
	private String number;

	@Expose
	@SerializedName("from")
	private String from;

	@Expose
	@SerializedName("to")
	private String to;

	@Expose
	@SerializedName("totnum")
	private String totnum;

	@Expose
	@SerializedName("cancel")
	private String cancel;

	@Expose
	@SerializedName("net_issue")
	private String net_issue;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTotnum() {
		return totnum;
	}

	public void setTotnum(String totnum) {
		this.totnum = totnum;
	}

	public String getCancel() {
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
	}

	public String getNet_issue() {
		return net_issue;
	}

	public void setNet_issue(String net_issue) {
		this.net_issue = net_issue;
	}

	@Override
	public String toString() {
		return "DocIssueDetails [number=" + number + ", from=" + from + ", to="
				+ to + ", totnum=" + totnum + ", cancel=" + cancel
				+ ", net_issue=" + net_issue + "]";
	}
	
	

}
