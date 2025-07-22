package com.ey.advisory.app.data.entities.client.asprecon;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Entity
@Data
@Table(name = "TBL_GETIRN_PAYLOADS")

public class GetIrnDetailPayloadEntity  {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIRN_PAYLOADS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "IRN")
	private String irn;

	@Expose
	@Column(name = "IRN_STATUS")
	private String irnSts;
	
	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Lob
	@Column(name = "PAYLOAD")
	private Clob payload;
	
	@Expose
	@Column(name = "BATCH_ID")
	private Long btchId;
	}
