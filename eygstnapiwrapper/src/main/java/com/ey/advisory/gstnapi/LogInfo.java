/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Khalid1.Khan
 *
 */
@RequiredArgsConstructor
@Getter
public final class LogInfo {
	
	private final Long reqId;
	
	private final String msgType;
	
	private final String msg;
	
	private final String errCode;
	
	private final LocalDateTime time;
	
	private final String groupCode;
	
	@Override
	public String toString() {
		return String.format(
				"Req Id: %d, MsgType: %s, ErrCode: %s, MSG: [%s],TIME: [%s],"
				+ "GROUPCODE : [%s]", 
				reqId, msgType, errCode, msg, time, groupCode);
	}
	

}
