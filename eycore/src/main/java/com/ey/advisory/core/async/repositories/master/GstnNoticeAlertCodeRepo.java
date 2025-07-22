package com.ey.advisory.core.async.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.GstnNoticeAlertCodeEntity;

@Repository("GstnNoticeAlertCodeRepo")
@Transactional
public interface GstnNoticeAlertCodeRepo
		extends JpaRepository<GstnNoticeAlertCodeEntity, Long> {

	List<GstnNoticeAlertCodeEntity> findByIsActiveTrue();
}
