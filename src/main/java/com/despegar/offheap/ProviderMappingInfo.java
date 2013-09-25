package com.despegar.offheap;

public class ProviderMappingInfo {

	private static final long serialVersionUID = 1L;

	private String externalCityCode;
	private String externalHotelCode;
	private Integer hotelQuantity;
	private String supplierCode;

	public String getExternalCityCode() {
		return this.externalCityCode;
	}

	public void setExternalCityCode(String externalCityCode) {
		this.externalCityCode = externalCityCode;
	}

	public String getExternalHotelCode() {
		return this.externalHotelCode;
	}

	public void setExternalHotelCode(String externalHotelCode) {
		this.externalHotelCode = externalHotelCode;
	}

	public Integer getHotelQuantity() {
		return this.hotelQuantity;
	}

	public void setHotelQuantity(Integer hotelQuantity) {
		this.hotelQuantity = hotelQuantity;
	}

	public String getSupplierCode() {
		return this.supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

}
