package com.ey.advisory.app.glrecon;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_GL_RECON")
@Data
public class GlReconSrFileUploadEntity {
	@Id
	@Column(name = "ID")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "sequence", sequenceName = "GL_RECON_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "REQUEST_ID")
	private Long reqId;

	@Column(name = "UPLOADED_DOC_NAME")
	private String uploadedDocName;
	
	@Column(name = "UPLOADED_DOC_NUMBER")
	private String uploadedDocNumber;
	
	@Column(name = "FILE_TYPE")
	private String fileType;
	
	@Column(name = "USERNAME")
	private String userName;
	
	@Column(name = "CREATED_ON")
	private String createdOn;
	
	public List<String> getAllValuesAsStringList() {
        List<String> values = new ArrayList<>();
        values.add(uploadedDocNumber);
        return values;
    }
}
