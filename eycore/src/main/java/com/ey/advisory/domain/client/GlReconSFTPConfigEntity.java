package com.ey.advisory.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_GL_SFTP_CONFIG")
@Setter
@Getter
@ToString
@Component
public class GlReconSFTPConfigEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

	@Column(name = "SFTP_USERNAME")
	private String sftpUsername;
	
	@Column(name = "SFTP_PASSWORD")
	private String sftpPaswrd;

	
}