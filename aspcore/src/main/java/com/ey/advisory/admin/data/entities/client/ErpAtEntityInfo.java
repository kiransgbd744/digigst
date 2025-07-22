/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "ERP_AT_ENTITY")
@Setter
@Getter
public class ErpAtEntityInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

//	@Column(name = "ERP_ID")
//	private Long erpId;
	
	/*@ManyToOne
	@JoinColumn(name = "ERP_ID", referencedColumnName = "ID", nullable = false)
	protected ErpInfoEntity header;
	*/
	@Column(name="GROUP_CODE")
	private String groupcode;
	
	@Column(name="GROUP_ID")
	private Long groupId;

	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "ENTITY_PAN")
	private String entityPan;

	@Column(name = "ENTITY_NAME")
	private String entityName;
	
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
	
	
}
