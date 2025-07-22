package com.ey.advisory.app.gstr3b;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author vishal.verma
 *
 */

@Entity
@Getter
@Setter
@Table(name = "GSTR3B_UPLOAD_ERRORS")
public class Gstr3BWebUploadEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GSTR3B_UPLOAD_ERROR_ID", nullable = false)
	private Long Id ;
	
	@Column(name = "KEY", nullable = false)
	private String key;
	
	@Column(name = "FILE_ID", nullable = false)
	private Long fileId;
	
	@Column(name = "ERROR_ID", nullable = false )
	private Long errorId;

	
	public Gstr3BWebUploadEntity() {
		super();
	}


	/**
	 * @param id
	 * @param key
	 * @param fileId
	 * @param errorId
	 */
	public Gstr3BWebUploadEntity(Long id, String key, Long fileId,
			Long errorId) {
		super();
		Id = id;
		this.key = key;
		this.fileId = fileId;
		this.errorId = errorId;
	}


	@Override
	public String toString() {
		return "Gstr3BWebUploadEntity [Id=" + Id + ", key=" + key + ", fileId="
				+ fileId + ", errorId=" + errorId + "]";
	}
	

}
