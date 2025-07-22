package com.ey.advisory.core.async.domain.master;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * The Group Entity copied from the existing ASP service code.
 * 
 * @author Sai.Pakanati
 *
 */
@Data
@Entity
@Table(name = "DOC_REPO_SERVICE")
public class DocRepoServiceEntity implements Serializable {

	private static final long serialVersionUID = 1L;	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long groupId;

	@Column(name = "SERVICE_NAME")
	private String serviceName;
		
	@Column(name = "CLIENT_ID")
	private String clientId;
	
	@Column(name = "CLIENT_SECRET")
	private String clientSecret;
	
	@Column(name = "URL")
	private String url;
	
	@Column(name = "EXPIRY_TIME")
	private LocalDateTime expiryTime;

	@Column(name = "ID_TOKEN")
	private String idToken;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Date createdOn;
}
