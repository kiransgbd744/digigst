package com.ey.advisory.app.gstr3b;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author vishal.verma
 *
 */
@ToString
@Setter
@Getter
@Entity
@Table(name = "GSTR3B_ERROR_MASTER")
public class Gstr3BErrorMaterEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GSTR3B_ERROR_MASTER_ID", nullable = false)
	private Long id;
	
	@Column(name = "ERROR_CODE", nullable = false)
	private String errorCode;
	
	@Column(name = "DESCRIPTION")
	private String description;
	

}
