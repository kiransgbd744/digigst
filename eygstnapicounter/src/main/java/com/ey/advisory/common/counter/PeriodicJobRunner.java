package com.ey.advisory.common.counter;

/**
 * This interface represents the component that runs the periodic jobs. The
 * implementation of this interface can determine how the jobs are executed. We
 * can use a scheduling mechanism like Spring Scheduler or Quartz scheduler to
 * achieve periodic execution of tasks.
 * 
 * @author Sai.Pakanati
 *
 */
public interface PeriodicJobRunner {

	public abstract void resetUsageMap();

	public void backUpLimitAndUsageMapPeriodically();

}
