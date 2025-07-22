package com.ey.advisory.domain.client;

import java.time.LocalDateTime;

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

@Entity
@Table(name="PERFORMANCE_STATISTICS")
@Setter
@Getter
@ToString
public class PerformanceStatistics {

	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "sequence", sequenceName = "PERFORMANCE_STSTCS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	protected Long id;

	@Column(name = "TRACK_ID")
	protected String trackId;

	@Column(name = "MODULE_NAME")
	protected String moduleName;

	@Column(name = "EVENT_NAME")
	protected String eventName;

	@Column(name = "CLASS_NAME")
	protected String className;

	@Column(name = "METHOD_NAME")
	protected String methodName;

	@Column(name = "CODE_LOC")
	protected String codeLocation;

	@Column(name = "CONTEXT")
	protected String context;

	@Column(name = "EVENT_DATE_TIME")
	protected LocalDateTime eventDateTime;

	public PerformanceStatistics(String trackId, String moduleName,
			String eventName, String className, String methodName,
			String codeLocation, String context) {
		super();
		this.trackId = trackId;
		this.moduleName = moduleName;
		this.eventName = eventName;
		this.className = className;
		this.methodName = methodName;
		this.codeLocation = codeLocation;
		this.context = context;
	}
	
	
}
