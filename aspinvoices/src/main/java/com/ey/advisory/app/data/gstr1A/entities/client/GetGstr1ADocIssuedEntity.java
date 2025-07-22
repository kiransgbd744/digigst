package com.ey.advisory.app.data.gstr1A.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1A_DOC_ISSUED")
@Setter
@Getter
@ToString
public class GetGstr1ADocIssuedEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1A_DOC_ISSUED_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "FLAG")
	protected String flag;

	@Column(name = "INV_CHKSUM")
	protected String invChksum;

	@Column(name = "DOC_NUM")
	protected Integer docNum;

	@Column(name = "SERIAL_NUM")
	protected Integer serialNum;

	@Column(name = "FROM_SERIAL_NUM")
	protected String fromSerialNum;

	@Column(name = "TO_SERIAL_NUM")
	protected String toSerialNum;

	@Column(name = "TOT_NUM")
	protected Integer totNum;

	@Column(name = "CANCEL")
	protected Integer cancel;

	@Column(name = "NET_ISSUE")
	protected Integer netIssue;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "GSTIN")
	protected String gstin;
}
