package com.psp.sellcenter.controller.springmvc.req;

import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 新建客户
 **/
public class AddUserParam {
	@NotEmpty(message = "姓名不能为空")
	private String name; // 姓名
	@NotEmpty(message = "手机号不能为空！")
	@Pattern(regexp = "^(1[1-9])\\d{9}$", message = "手机号格式不正确！")
	private String phoneNum; // 手机号码
	private String companyName; // 公司名称
	private String position; // 职位
	private Integer ctype; // 1
	private String label; // 标签
	private String visitDest; // 参观目的
	private Integer visitNum; // 参观人数
	private String refCompany; // 推荐单位
	private String referrer; // 推荐人
	private String visitTime; // 参观时间：yyyy-MM-dd HH:mm
	private String escort; // 陪同人
	private String introducer; // 引导介绍
	private String visitflow; // 参观流程
	private String remark; // 备注
	@Pattern(regexp = "^0|1$", message = "更新：1:更新 0不更新")
	private String isUpdate; // 更新：1:更新 0不更新
	@Pattern(regexp = "^0|1$", message = "认领：1:认领 0不认领")
	private String isClaim; // 认领：1:认领 0不认领

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

	public void setCtype(Integer ctype) {
		this.ctype = ctype;
	}

	public Integer getCtype() {
		return ctype;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setVisitDest(String visitDest) {
		this.visitDest = visitDest;
	}

	public String getVisitDest() {
		return visitDest;
	}

	public void setVisitNum(Integer visitNum) {
		this.visitNum = visitNum;
	}

	public Integer getVisitNum() {
		return visitNum;
	}

	public void setRefCompany(String refCompany) {
		this.refCompany = refCompany;
	}

	public String getRefCompany() {
		return refCompany;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setEscort(String escort) {
		this.escort = escort;
	}

	public String getEscort() {
		return escort;
	}

	public void setIntroducer(String introducer) {
		this.introducer = introducer;
	}

	public String getIntroducer() {
		return introducer;
	}

	public void setVisitflow(String visitflow) {
		this.visitflow = visitflow;
	}

	public String getVisitflow() {
		return visitflow;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getIsUpdate() {
		return isUpdate;
	}

	public void setIsClaim(String isClaim) {
		this.isClaim = isClaim;
	}

	public String getIsClaim() {
		return isClaim;
	}

}
