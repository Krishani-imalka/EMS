package com.Java.EMS.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "organization")
public class Organizations {
    @Id
    @Column(name = "organization_id",length = 20,nullable = false)
    private String organizationId;

    @ManyToOne
    @JoinColumn(name = "president_id", nullable = false)
    private User president;

    @Column(name = "org_name", length = 200, nullable = false)
    private String orgName;

    @Column(name = "org_email", length = 100)
    private String orgEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "org_status", nullable = false)
    private OrgStatus orgStatus = OrgStatus.ACTIVE;

    public Organizations() {
    }

    public Organizations(String organizationId, User president, String orgName, String orgEmail, OrgStatus orgStatus) {
        this.organizationId = organizationId;
        this.president = president;
        this.orgName = orgName;
        this.orgEmail = orgEmail;
        this.orgStatus = orgStatus;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public User getPresident() {
        return president;
    }

    public void setPresident(User president) {
        this.president = president;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public OrgStatus getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(OrgStatus orgStatus) {
        this.orgStatus = orgStatus;
    }

    enum OrgStatus{
        ACTIVE, INACTIVE, SUSPENDED
    }
}
