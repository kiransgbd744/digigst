package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

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
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "CHECKER_APPROVAL_STATUS")
@Setter
@Getter
@ToString
@Component
public class ApprovalRequestStatusEntity {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "ID", nullable = false)
private Long id;

@Column(name = "REQUEST_ID")
private Long requestId;

@Column(name = "CHECKER_NAME")
private String checkerName;

@Column(name = "CHECKER_EMAIL")
private String checkerEmail;

@Column(name = "CHECKER_COMMENTS")
private String checkcomments;

@Column(name = "REQUEST_STATUS")
private String reqStatus;

@Column(name = "ACTION_BY")
private String actionBy;

@Column(name = "ACTION_DATE_TIME")
private LocalDateTime actionDateTime;

@Column(name = "REVERT_BY")
private String revertBy;

@Column(name = "REVERT_DATE_TIME")
private LocalDateTime revertDateTime;
	  
}
