package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * This Entity class is responsible for on boarding module - User Creation
 * 
 * @author Balakrishna.S
 *
 */
@Data
@Entity
@Table(name = "CLIENT_USERINFO")
@Component
public class UserCreationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "GROUP_ID")
	private Long groupId;

	@Expose
	@Column(name = "USER_NAME")
	private String userName;

	@Expose
	@Column(name = "ITP_USERNAME")
	private String itpUserName;

	@Expose
	@Column(name = "FIRST_NAME")
	private String firstName;

	@Expose
	@Column(name = "LAST_NAME")
	private String lastName;

	@Expose
	@Column(name = "EMAIL")
	private String email;

	@Expose
	@Column(name = "MOBILE")
	private String mobile;

	@Expose
	@Column(name = "USER_ROLE")
	private String userRole;

	@Expose
	@Column(name = "IS_DELETE")
	private Boolean isFlag;

	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Expose
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

}
