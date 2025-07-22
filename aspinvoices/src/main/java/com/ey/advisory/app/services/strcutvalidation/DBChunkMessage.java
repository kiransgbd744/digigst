package com.ey.advisory.app.services.strcutvalidation;

import com.ey.advisory.common.eyfileutils.tabular.Chunk;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class contains all the information required to process the file Arrival
 * Message.
 * 
 * @author Sai.Pakanati
 *
 */
public class DBChunkMessage {
	
	
	@Expose @SerializedName("fileId")
	private Long fileId;
	
	@Expose @SerializedName("chunk")	
	private Chunk chunk;

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public void setChunk(Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public String toString() {
		return "DBChunkMessage [fileId=" + fileId + ", chunk=" + chunk + "]";
	}
	

	

	
}
