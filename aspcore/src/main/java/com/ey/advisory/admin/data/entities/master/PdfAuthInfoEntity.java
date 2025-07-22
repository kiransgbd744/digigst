/**
 * 
 */

package com.ey.advisory.admin.data.entities.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "PDF_API_AUTH_INFO")
@Data
public class PdfAuthInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "ID_TOKEN")
	private String idToken;
	
	@Column(name = "PDF_TOKEN_GEN_TIME")
	private LocalDateTime pdfTokenGenTime;
	
	@Column(name = "PDF_TOKEN_EXP_TIME")
	private LocalDateTime pdfTokenExpTime;
	
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;
	
	@Column(name = "UPDATE_DATE")
	private LocalDateTime updateDate;
	
	
}
