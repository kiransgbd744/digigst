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
@Table(name = "MASTER_PRODUCT")
@Setter
@Getter
@ToString
public class MasterProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN_PAN")
	private String gstinPan;

	@Column(name = "PRODUCT_CODE")
	private String productCode;

	@Column(name = "PRODUCT_DESC")
	private String productDesc;

	@Column(name = "PRODUCT_CATEGORY")
	private String productCategory;

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

	@Column(name = "NIL/NON/EXMT")
	private String nilOrNonOrExmt;

	@Column(name = "IF_Y_CIRCULAR_NOTIFICATION_NUM")
	private String ifYCircularNotificationNum;

	@Column(name = "IF_Y_CIRCULAR_NOTIFICATION_DATE")
	private LocalDate notificationDate;

	@Column(name = "EF_CIRCULAR_DATE")
	private LocalDate efCircularDate;

	@Column(name = "RATE")
	private BigDecimal rate = BigDecimal.ZERO;

	@Column(name = "ITC_FLAG")
	private String itcFlag;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "PRODUCT_KEY")
	protected String prodKey;

	public MasterProductEntity add(MasterProductEntity prodMaster) {
		this.gstinPan = prodMaster.gstinPan;
		this.productCode = prodMaster.productCode;
		this.productDesc = prodMaster.productDesc;
		this.productCategory = prodMaster.productCategory;
		this.hsnOrSac = prodMaster.hsnOrSac;
		this.uom = prodMaster.uom;
		this.reverseChargeFlag = prodMaster.reverseChargeFlag;
		this.tdsFlag = prodMaster.tdsFlag;
		this.diffPercent = prodMaster.diffPercent;
		this.nilOrNonOrExmt = prodMaster.nilOrNonOrExmt;
		this.ifYCircularNotificationNum = prodMaster.ifYCircularNotificationNum;
		this.notificationDate = prodMaster.notificationDate;
		this.efCircularDate = prodMaster.efCircularDate;
		this.rate = prodMaster.rate;
		this.itcFlag = prodMaster.itcFlag;
		this.isDelete = prodMaster.isDelete;
		this.fileId = prodMaster.fileId;
		this.fileName = prodMaster.fileName;
		this.prodKey = prodMaster.prodKey;
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
		MasterProductEntity other = (MasterProductEntity) obj;
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