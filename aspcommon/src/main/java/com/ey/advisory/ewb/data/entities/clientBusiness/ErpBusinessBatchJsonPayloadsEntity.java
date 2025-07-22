/**
 * 
 */
package com.ey.advisory.ewb.data.entities.clientBusiness;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Entity
@Table(name = "ERP_PUSH_BATCH_PAYLOADS")
@Data
public class ErpBusinessBatchJsonPayloadsEntity {
	
	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ERP_PUSH_BATCH_PAYLOADS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Lob
	@Column(name = "REV_REQ_PAYLOAD")
	private String revReqPayload;

	@Lob
	@Column(name = "REV_RESP_PAYLOAD")
	private String revRespPayload;

	@Column(name = "HTTP_CODE")
	private Integer httpCode;

	@Column(name = "REV_ERR_MSG")
	private String revErrMsg;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

}
