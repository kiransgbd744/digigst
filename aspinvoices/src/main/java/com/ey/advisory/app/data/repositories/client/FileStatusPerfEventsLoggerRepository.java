package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.FileStatusPerfEventsLoggerEntity;

import jakarta.transaction.Transactional;

@Repository("FileStatusPerfEventsLoggerRepository")
@Transactional
public interface FileStatusPerfEventsLoggerRepository
		extends CrudRepository<FileStatusPerfEventsLoggerEntity, Long> {

}
