package com.ey.advisory.app.data.entities.client.asprecon;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.ey.advisory.common.GenUtil;
import com.google.gson.annotations.Expose;

import lombok.Data;

@Entity
@Data
@Table(name = "TBL_GETIRN_LOG")

public class GetIrnDetailErrorEntity  {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIRN_LOG_SEQ", allocationSize = 1)
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
	@Column(name = "ERROR_PAYLOAD")
	private Clob errPayload;
	
	@Expose
	@Column(name = "BATCH_ID")
	private Long btchId;
	
	}
