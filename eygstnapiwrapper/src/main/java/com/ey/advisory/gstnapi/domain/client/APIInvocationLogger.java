/**
 * 
 */
package com.ey.advisory.gstnapi.domain.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Entity
@Data
@Table(name = "API_INVOCATION_LOGGER")
public class APIInvocationLogger {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "API_INVOCATION_LOGGER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "USER_MESSAGE", length = 100)
	private String userMessage;

	@Column(name = "CREATE_BY", length = 60)
	private String createdBy;

	@Column(name = "CREATE_ON")
	private LocalDateTime createdOn;

	public APIInvocationLogger(Long requestId, String type, String message,
			String userMessage, String createdBy, LocalDateTime createdOn) {
		super();
		this.requestId = requestId;
		this.type = type;
		this.message = message;
		this.userMessage = userMessage;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}

	public APIInvocationLogger() {
		super();
	}

}
