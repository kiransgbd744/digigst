package com.ey.advisory.gstnapi.domain.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "AZURE_SAP_GROUP_MAPPING")
public class AzureSapGroupMappingEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")		
	private Long id;
	
	@Column(name = "SAP_GROUP_CODE")
	private String sapGroupCode;
	
	@Column(name = "AZURE_GROUP_CODE")
	private String azureGroupCode;
}
