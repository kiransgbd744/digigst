/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Itc04DuplicateDocCheckService {
	
	void softDeleteDupDocsDetails(List<Itc04HeaderEntity> docs);

}
