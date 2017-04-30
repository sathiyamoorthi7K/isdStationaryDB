package com.test.isd.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class SubmitRequestDTO {

	private RequestorDTO requestorDTO;
	private String requestId;
	private String requestBy;
	private Date deliveryDate;
	private String requestLog;
	private String status;
	private String approvalLog;
	List<ItemRequestedDTO> itemList;
	private Timestamp creationDate;
	
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}
	public List<ItemRequestedDTO> getItemList() {
		return itemList;
	}
	public void setItemList(List<ItemRequestedDTO> itemList) {
		this.itemList = itemList;
	}
	public RequestorDTO getRequestorDTO() {
		return requestorDTO;
	}
	public void setRequestorDTO(RequestorDTO requestorDTO) {
		this.requestorDTO = requestorDTO;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getRequestBy() {
		return requestBy;
	}
	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getRequestLog() {
		return requestLog;
	}
	public void setRequestLog(String requestLog) {
		this.requestLog = requestLog;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApprovalLog() {
		return approvalLog;
	}
	public void setApprovalLog(String approvalLog) {
		this.approvalLog = approvalLog;
	}
}
