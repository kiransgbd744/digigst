/**
 * 
 */
package com.ey.advisory.app.data.entities.master;

import java.time.LocalDateTime;

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
 * @author Siva.Reddy
 *
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "ID_TOKEN_GRP_MAPPING")
public class IdTokenGrpMapEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GROUP_CODE")
	protected String groupCode;

	@Column(name = "USERNAME")
	protected String username;

	@Column(name = "PASSWORD")
	protected String password;

	@Column(name = "EXPIRY_TIME")
	protected LocalDateTime expiryTime;

	@Column(name = "ID_TOKEN")
	protected String idToken;

}
