package com.creativeitem.academy.JSONSchemas;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CertificateAddonSchema {
    @SerializedName("addon_status")
    @Expose
    private String addonStatus;
    @SerializedName("is_completed")
    @Expose
    private Integer isCompleted;
    @SerializedName("certificate_shareable_url")
    @Expose
    private String certificateShareableUrl;

    public String getAddonStatus() {
        return addonStatus;
    }

    public void setAddonStatus(String addonStatus) {
        this.addonStatus = addonStatus;
    }

    public Integer getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Integer isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getCertificateShareableUrl() {
        return certificateShareableUrl;
    }

    public void setCertificateShareableUrl(String certificateShareableUrl) {
        this.certificateShareableUrl = certificateShareableUrl;
    }
}
