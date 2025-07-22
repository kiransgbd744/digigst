package com.ey.advisory.app.reconewbvsitc04;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */
@Entity
@Table(name = "TBL_LOGITEM")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ErrorLogItemEntity {

	@Id
	@Column(name = "LOGID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ERR_CODE")
	private String errorCode;

	@Column(name = "ERR_MSG")
	private String errorMessage;
	
	@Column(name = "PROCEDURE_NAME")
	private String procedureName;
	
	@Column(name = "INPUTPARAMS")
	private String inputParams;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "START_TIME")
	private LocalDateTime startTime;
	
	@Column(name = "END_TIME")
	private LocalDateTime endTime;
	
	@Column(name = "ERR_TIME")
	private LocalDateTime errorTime;

}
