package com.ey.advisory.app.data.entities.client.asprecon;

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
@Table(name = "MAKER_CHECKER_PREFERENCE")
@Setter
@Getter
@ToString
@Component
public class ApprovalPreferenceChildEntity {

	  	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "ID", nullable = false)
	    private Long id;

	    @Column(name = "MAPPING_ID")
	    private Long mappingId;

	    @Column(name = "NAME")
	    private String name;

	    @Column(name = "IS_MAKER")
	    private Boolean isMaker;

	    @Column(name = "EMAIL")
	    private String email;
	  
}
