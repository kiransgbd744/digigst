package com.ey.advisory.app.data.gstr1A.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.ey.advisory.app.data.entities.client.GetGstr1SummaryEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETGSTR1A_DOCISSUED_SUMMARY")
@Data
public class Gstr1ASummaryDocIssuedEntity extends GetGstr1SummaryEntity {

	@Expose
	@SerializedName("sec_nm")
	@Column(name = "SECTION_NAME")
	private String secName;

	@Expose
	@SerializedName("ttl_rec")
	@Column(name = "TOT_RECORD")
	private int ttlRec;

	@Expose
	@SerializedName("ttl_doc_issued")
	@Column(name = "TOT_DOC_ISSUES")
	private int ttlDocIssued;

	@Expose
	@SerializedName("ttl_doc_cancelled")
	@Column(name = "TOT_DOC_CANCELLED")
	private int ttlDocCancelled;

	@Expose
	@SerializedName("net_doc_issued")
	@Column(name = "NET_DOC_ISSUED")
	private int netDocIssued;

	
}
