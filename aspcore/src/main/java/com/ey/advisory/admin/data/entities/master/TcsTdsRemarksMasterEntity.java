package com.ey.advisory.admin.data.entities.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;



@Entity
@Table(name = "MASTER_GSTR2X_REMARKS")
@Data
public class TcsTdsRemarksMasterEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "EXPECTED_VALUE")
	private String expectedValue;

	@Expose
	@Column(name = "DESCRIPTION")
	private String desc;
}