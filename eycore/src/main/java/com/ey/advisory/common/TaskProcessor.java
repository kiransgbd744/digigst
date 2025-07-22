package com.ey.advisory.common;


public interface TaskProcessor {
	
	public void execute(Message message, AppExecContext context);

}
