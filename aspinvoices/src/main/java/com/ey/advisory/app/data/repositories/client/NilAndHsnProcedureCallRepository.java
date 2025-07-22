package com.ey.advisory.app.data.repositories.client;

/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface NilAndHsnProcedureCallRepository {
	public void getNilNonProc(String sgstin, Integer intDevPeriod);
	public void getHsnProc(String sgstin, Integer intDevPeriod);
}
