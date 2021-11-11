package com.kh.spring.project.model.dto;

import java.sql.Date;

public class Project {

	private String projectIdx;
	private String proName;
	private String inviteCode;
	private int isDel;
	private Date regDate;
	private String proDescription;

	public Project() {
		// TODO Auto-generated constructor stub
	}

	public String getProjectIdx() {
		return projectIdx;
	}

	public void setProjectIdx(String projectIdx) {
		this.projectIdx = projectIdx;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getProDescription() {
		return proDescription;
	}

	public void setProDescription(String proDescription) {
		this.proDescription = proDescription;
	}

	@Override
	public String toString() {
		return "Project [projectIdx=" + projectIdx + ", proName=" + proName + ", inviteCode=" + inviteCode + ", isDel="
				+ isDel + ", regDate=" + regDate + ", proDescription=" + proDescription + "]";
	}
	
	

}
