package com.ey.advisory.app.data.entities.client;

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
@Table(name="FILE_STATUS_PERF_EVENTS")
@Setter
@Getter
@ToString
public class FileStatusPerfEventsLoggerEntity {

	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "sequence", sequenceName = "FILE_STATUS_PERF_EVENTS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	protected Long id;
	
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	@Column(name = "EVENT")
	protected String event;
	
	@Column(name = "DATE")
	protected LocalDateTime date;
	
	@Column(name = "DESCRIPTION")
	protected String desc;
}
