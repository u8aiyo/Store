package com.apuri.sutoaa;

public class AddressesModel {

    private Boolean selected;
    private String city;
    private String locality;
    private String flatNo;
    private String pinCode;
    private String landmark;
    private String name;
    private String mobileNo;
    private String alternateMobileNo;
    private String stateSpinner;

    public AddressesModel(Boolean selected, String city, String locality, String flatNo, String pinCode, String landmark, String name, String mobileNo, String alternateMobileNo, String stateSpinner) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.flatNo = flatNo;
        this.pinCode = pinCode;
        this.landmark = landmark;
        this.name = name;
        this.mobileNo = mobileNo;
        this.alternateMobileNo = alternateMobileNo;
        this.stateSpinner = stateSpinner;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAlternateMobileNo() {
        return alternateMobileNo;
    }

    public void setAlternateMobileNo(String alternateMobileNo) {
        this.alternateMobileNo = alternateMobileNo;
    }

    public String getStateSpinner() {
        return stateSpinner;
    }

    public void setStateSpinner(String stateSpinner) {
        this.stateSpinner = stateSpinner;
    }
}
