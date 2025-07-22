package com.ey.advisory.core.async.domain.master;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.common.multitenancy.TenantDBConfig;

/**
 * 
 * The Group Entity copied from the existing ASP service code.
 * 
 * @author Sai.Pakanati
 *
 */
@Entity
@Table(name = "EY_GROUP", uniqueConstraints = @UniqueConstraint(columnNames = "GROUP_CODE"))
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Group.class);

	private static boolean isDBConfigSet = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long groupId;

	@Column(name = "GROUP_CODE", nullable = false)
	private String groupCode;

	@Column(name = "GROUP_NAME")
	private String groupName;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "IS_DIV_PARENT")
	private Boolean isDivParent;

	@Column(name = "REPOSITORY_NAME")
	private String repositoryName;

	@Column(name = "REPOSITORY_KEY")
	private String repositoryKey;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATE_DATE")
	private Date createdDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATE_DATE")
	private Date updatedDate;

	@Column(name = "SFTP_USER_NAME")
	private String sftpUserName;

	@Column(name = "SFTP_PASSWORD")
	private String sftpPassword;

	@Transient
	private TenantDBConfig dbConfig;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsDivParent() {
		return isDivParent;
	}

	public void setIsDivParent(Boolean isDivParent) {
		this.isDivParent = isDivParent;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoryKey() {
		return repositoryKey;
	}

	public void setRepositoryKey(String repositoryKey) {
		this.repositoryKey = repositoryKey;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public TenantDBConfig getDBConfig() {
		return dbConfig;
	}

	public String getSftpUserName() {
		return sftpUserName;
	}

	public void setSftpUserName(String sftpUserName) {
		this.sftpUserName = sftpUserName;
	}

	public String getSftpPassword() {
		return sftpPassword;
	}

	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}

	/**
	 * This method checks if the DBConfig is already set. If not, it sets the
	 * DBConfig to the specified value. Otherwise, it prints an error log and
	 * quietly ignores the request. This method is only required for startup
	 * loading of DBConfig.
	 * 
	 * @param groupDBConfig
	 */
	public void setDBConfigIfNotAlreadySet(TenantDBConfig groupDBConfig) {
		if (isDBConfigSet) {
			String msg = String.format(
					"DBConfig is already calculated "
							+ "and set for the group '%s'. Ignoring this request "
							+ "to reset the already set GroupConfig.",
					groupCode);
			LOGGER.error(msg);
			return;
		}
		this.dbConfig = groupDBConfig;
	}

	public boolean hasValidDBConfig() {
		return (dbConfig != null);
	}

	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", groupCode=" + groupCode
				+ ", groupName=" + groupName + ", isActive=" + isActive
				+ ", isDivParent=" + isDivParent + ", repositoryName = "
				+ repositoryName + ", repositoryKey = " + repositoryKey
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", updatedBy=" + updatedBy + ", updatedDate=" + updatedDate
				+ ", " + "hasValidTenantDBConfig=" + (dbConfig != null) + "]";
	}

}
