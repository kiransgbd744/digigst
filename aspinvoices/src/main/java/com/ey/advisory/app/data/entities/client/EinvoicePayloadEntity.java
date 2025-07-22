package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Sai.Pakanati
 *
 */
@Entity
@Table(name = "EINVOICE_DUMP")
@Setter
@Getter
@ToString
public class EinvoicePayloadEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Lob
	@Column(name = "REQUEST_PAYLOAD")
	protected String requestPayload;
	
	@Lob
	@Column(name = "KSA_XMLPAYLOAD")
	protected String ksaXmlPayload;

	@Column(name = "CREATED_ON")
	protected LocalDate createdOn;
	
	@Column(name = "ENTITY_NAME")
	protected String entityName;
	

}
