package com.ey.advisory.app.get.notices.handlers;

import lombok.Data;

@Data
public class NoticeStats {

	private String gstin;
	private int totalIssued;
	private int totalResponded;
	private int totalPending;

	public NoticeStats(String gstin, int totalCount, int respondedCount,
			int pendingCount) {
		this.gstin = gstin;
		this.totalIssued = totalCount;
		this.totalResponded = respondedCount;
		this.totalPending = pendingCount;
	}

}
