package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MASTER_ITEM")
@Setter
@Getter
@ToString
public class MasterItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "GSTIN_PAN")
	private String gstinPan;

	@Column(name = "ITM_CODE")
	private String itmCode;

	@Column(name = "ITM_DESC")
	private String itemDesc;

	@Column(name = "ITM_CATEGORY")
	private String itmCategory;

	@Column(name = "HSNORSAC")
	private Integer hsnOrSac;

	@Column(name = "UOM")
	private String uom;

	@Column(name = "REV_CHARGE_FLAG")
	private String reverseChargeFlag;

	@Column(name = "TDS_FLAG")
	private String tdsFlag;

	@Column(name = "DIFF_PERCENT")
	private String diffPercent;

	@Column(name = "NIL_NON_EXMT")
	private String nilOrNonOrExmt;

	@Column(name = "IF_Y_CIRCULAR_NOTIFICATION_NUM")
	private String ifYCirularNotificationNum;

	@Column(name = "IF_Y_CIRCULAR_NOTIFICATION_DATE")
	private LocalDate ifYCirularNotificationDate;

	@Column(name = "EF_CIRCULAR_DATE")
	private LocalDate efCircularDate;

	@Column(name = "RATE")
	private BigDecimal rate;

	@Column(name = "ELGBL_INDICATOR")
	private String elgblIndicator;

	@Column(name = "PER_OF_ELGBL")
	private BigDecimal perOfElgbl;

	@Column(name = "COMMON_SUPP_INDICATOR")
	private String commonSuppIndicator;

	@Column(name = "ITC_REVERSAL_IDENTIFIER")
	private String itcReversalIdentifier;

	@Column(name = "ITC_ENTITLEMENT")
	private String itcsEntitlement;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "ITEM_KEY")
	protected String itemKey;

	public MasterItemEntity add(MasterItemEntity itemMaster) {
		this.gstinPan = itemMaster.gstinPan;
		this.itmCode = itemMaster.itmCode;
		this.itemDesc = itemMaster.itemDesc;
		this.itmCategory = itemMaster.itmCategory;
		this.hsnOrSac = itemMaster.hsnOrSac;
		this.uom = itemMaster.uom;
		this.reverseChargeFlag = itemMaster.reverseChargeFlag;
		this.tdsFlag = itemMaster.tdsFlag;
		this.diffPercent = itemMaster.diffPercent;
		this.nilOrNonOrExmt = itemMaster.nilOrNonOrExmt;
		this.ifYCirularNotificationNum = itemMaster.ifYCirularNotificationNum;
		this.ifYCirularNotificationDate = itemMaster.ifYCirularNotificationDate;
		this.efCircularDate = itemMaster.efCircularDate;
		this.rate = itemMaster.rate;
		this.elgblIndicator = itemMaster.elgblIndicator;
		this.perOfElgbl = itemMaster.perOfElgbl;
		this.commonSuppIndicator = itemMaster.commonSuppIndicator;
		this.itcReversalIdentifier = itemMaster.itcReversalIdentifier;
		this.itcsEntitlement = itemMaster.itcsEntitlement;
		this.isDelete = itemMaster.isDelete;
		this.fileId = itemMaster.fileId;
		this.fileName = itemMaster.fileName;
		this.itemKey = itemMaster.itemKey;
		return this;

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((gstinPan == null) ? 0 : gstinPan.hashCode());
		result = prime * result
				+ ((hsnOrSac == null) ? 0 : hsnOrSac.hashCode());
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MasterItemEntity other = (MasterItemEntity) obj;
		if (gstinPan == null) {
			if (other.gstinPan != null)
				return false;
		} else if (!gstinPan.equals(other.gstinPan))
			return false;
		if (hsnOrSac == null) {
			if (other.hsnOrSac != null)
				return false;
		} else if (!hsnOrSac.equals(other.hsnOrSac))
			return false;
		if (rate == null) {
			if (other.rate != null)
				return false;
		} else if (!rate.equals(other.rate))
			return false;
		return true;
	}
}