package com.ey.advisory.app.data.services.sac;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.sac.SacGroupConfigEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component
public interface SacDashBoardProcHelperService {

	void procHelper(List<SacGroupConfigEntity> entityList);
}
