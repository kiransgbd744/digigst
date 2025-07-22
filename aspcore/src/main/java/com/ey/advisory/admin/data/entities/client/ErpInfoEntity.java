/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ERP_INFO")
@Setter
@Getter
@ToString
public class ErpInfoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PROTOCOL")
	private String protocal;

	@Column(name = "HOST_NAME")
	private String hostName;
	
	@Column(name = "PORT")
	private String port;

	@Column(name = "LOCATION")
	private String location;
	
	@Column(name = "USERNAME")
	private String user;

	@Column(name = "PASSWORD")
	private String pass;

	@Column(name = "SYSTEM_ID")
	private String systemId;
	
	/*@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<ErpAtEntityInfo> lineItems = new ArrayList<>();
	*/
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name="SOURCE_TYPE")
	private String sourceType;
	
//	@Column(name = "GSTIN_ID")
//	private Long gstinId;
	
	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GSTNDetailEntity> lineItems = new ArrayList<>();
	
}
