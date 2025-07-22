package com.ey.advisory.app.services.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusPerfEventsLoggerEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusPerfEventsLoggerRepository;

/**
 * Class copied form ASP tenant service.
 * 
 * @author Sai.Pakanati
 *
 */
@Component("FileStatusPerfEventsLogger")
public class FileStatusPerfEventsLogger {

	/**
	 * Make the class non-instantiable.
	 */
	private FileStatusPerfEventsLogger() {
	}

	/*
	 * @PersistenceContext(unitName = "masterDataUnit") private EntityManager
	 * entityManager;
	 */

	@Autowired
	@Qualifier("FileStatusPerfEventsLoggerRepository")
	private FileStatusPerfEventsLoggerRepository repo;
	private static final ThreadLocal<Long> context = new ThreadLocal<>();

	public void setFileId(Long fileId) {
		context.set(fileId);
	}

	public Long getFileId() {

		return context.get();
	}

	public void logEvent(String event) {
		LocalDateTime date = LocalDateTime.now();
		Long fileId = getFileId();
		FileStatusPerfEventsLoggerEntity entity = new FileStatusPerfEventsLoggerEntity();
		entity.setFileId(fileId);
		entity.setEvent(event);
		entity.setDate(date);
		repo.save(entity);
	}
	
	public void logEvent(String event,String desc) {
		LocalDateTime date = LocalDateTime.now();
		Long fileId = getFileId();
		FileStatusPerfEventsLoggerEntity entity = new FileStatusPerfEventsLoggerEntity();
		entity.setFileId(fileId);
		entity.setEvent(event);
		entity.setDesc(desc);
		entity.setDate(date);
		repo.save(entity);
	}

	public void logValidationPerfStats(List<Pair<String, String>> events) {
		List<FileStatusPerfEventsLoggerEntity> listEntity = new ArrayList<>();
		LocalDateTime date = LocalDateTime.now();
		events.forEach(event -> {
			FileStatusPerfEventsLoggerEntity loggerEntity = new FileStatusPerfEventsLoggerEntity();
			Long fileId = getFileId();
			loggerEntity.setFileId(fileId);
			loggerEntity.setEvent(event.getValue0());
			loggerEntity.setDesc(event.getValue1());
			loggerEntity.setDate(date);
			listEntity.add(loggerEntity);
		});
		repo.saveAll(listEntity);
	}

}
