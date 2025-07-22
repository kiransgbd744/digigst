package com.ey.advisory.app.vendorcomm.dto;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VendorEmailCommDto {

	private String vendorGstin;
	private String vendorName;
	private String vendPrimEmailId;
	private Set<EmailIdDto> secondaryEmailIds = new HashSet <>();
	private Set<EmailIdDto> recipientEmailIds = new HashSet<>();
	private String emailStatus;
	private String vendorContactNumber;
	private Long requestID;
	private String updatedOn;
	private String returnType;

	public void addSecondaryEmail(String emailId) {

		if (!Strings.isNullOrEmpty(emailId)) {

			secondaryEmailIds.add(new EmailIdDto(emailId));
		}
	}

	public void addRecipientEmail(String emailId) {

		if (!Strings.isNullOrEmpty(emailId)) {

			recipientEmailIds.add(new EmailIdDto(emailId));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emailStatus == null) ? 0 : emailStatus.hashCode());
		result = prime * result + ((recipientEmailIds == null) ? 0
				: recipientEmailIds.hashCode());
		result = prime * result
				+ ((requestID == null) ? 0 : requestID.hashCode());
		result = prime * result
				+ ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result + ((secondaryEmailIds == null) ? 0
				: secondaryEmailIds.hashCode());
		result = prime * result
				+ ((updatedOn == null) ? 0 : updatedOn.hashCode());
		result = prime * result
				+ ((vendPrimEmailId == null) ? 0 : vendPrimEmailId.hashCode());
		result = prime * result + ((vendorContactNumber == null) ? 0
				: vendorContactNumber.hashCode());
		result = prime * result
				+ ((vendorGstin == null) ? 0 : vendorGstin.hashCode());
		result = prime * result
				+ ((vendorName == null) ? 0 : vendorName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendorEmailCommDto other = (VendorEmailCommDto) obj;
		if (emailStatus == null) {
			if (other.emailStatus != null)
				return false;
		} else if (!emailStatus.equals(other.emailStatus))
			return false;
		if (recipientEmailIds == null) {
			if (other.recipientEmailIds != null)
				return false;
		} else if (!recipientEmailIds.equals(other.recipientEmailIds))
			return false;
		if (requestID == null) {
			if (other.requestID != null)
				return false;
		} else if (!requestID.equals(other.requestID))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		if (secondaryEmailIds == null) {
			if (other.secondaryEmailIds != null)
				return false;
		} else if (!secondaryEmailIds.equals(other.secondaryEmailIds))
			return false;
		if (updatedOn == null) {
			if (other.updatedOn != null)
				return false;
		} else if (!updatedOn.equals(other.updatedOn))
			return false;
		if (vendPrimEmailId == null) {
			if (other.vendPrimEmailId != null)
				return false;
		} else if (!vendPrimEmailId.equals(other.vendPrimEmailId))
			return false;
		if (vendorContactNumber == null) {
			if (other.vendorContactNumber != null)
				return false;
		} else if (!vendorContactNumber.equals(other.vendorContactNumber))
			return false;
		if (vendorGstin == null) {
			if (other.vendorGstin != null)
				return false;
		} else if (!vendorGstin.equals(other.vendorGstin))
			return false;
		if (vendorName == null) {
			if (other.vendorName != null)
				return false;
		} else if (!vendorName.equals(other.vendorName))
			return false;
		return true;
	}

	
	
}
