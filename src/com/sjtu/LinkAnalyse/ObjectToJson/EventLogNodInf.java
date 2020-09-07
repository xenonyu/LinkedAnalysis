package com.sjtu.LinkAnalyse.ObjectToJson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.alibaba.fastjson.annotation.JSONType;
//Json持久化顺序
@JSONType(orders={"curServiceLogID", "gid","startServiceName","startServiceTime","requestNodeName","listRequest", "responseNodeName", "listResponse"})
public class EventLogNodInf {
	private String curServiceLogID;
	private String gid;
	private String startServiceName;
	private Long startServiceTime;
	private ArrayList<String> requestNodeName = new ArrayList<>();
	private ArrayList<LogFormat> listRequest = new ArrayList<>();
	private ArrayList<String> responseNodeName = new ArrayList<>();
	private ArrayList<LogFormat> listResponse = new ArrayList<>();
	//private String abnorInfo;
	
	public EventLogNodInf() {
	}
	
	public EventLogNodInf(String curServiceLogID,
						  String gid,
						  String startServiceName,
						  Long startServiceTime,
						  ArrayList<String> requestNodeName,
						  ArrayList<LogFormat> listRequest,
						  ArrayList<String> responseNodeName,
						  ArrayList<LogFormat> listResponse,
						  String abnorInfo) {
		super();
		this.curServiceLogID = curServiceLogID;
		this.gid = gid;
		this.startServiceName = startServiceName;
		this.startServiceTime = startServiceTime;
		this.requestNodeName = requestNodeName;
		this.listRequest = listRequest;
		this.responseNodeName = responseNodeName;
		this.listResponse = listResponse;
		//this.abnorInfo = abnorInfo;
	}
	public ArrayList<String> getRequestNodeName() {
		return requestNodeName;
	}
	public void addRequestNodeName(ArrayList<String> requestNodename) {
		//requestNodeName.clear();
		for(int i = 0; i < requestNodename.size(); i++) {
			this.requestNodeName.add(requestNodename.get(i));
		}
	}

	public void setRequestNodeName(ArrayList<String> requestNodename) {
		requestNodeName.clear();
		for(int i = 0; i < requestNodename.size(); i++) {
			this.requestNodeName.add(requestNodename.get(i));
		}
	}
	public ArrayList<LogFormat> getListRequest() {
		return listRequest;
	}
	public void setListRequest(ArrayList<LogFormat> listrequest) {
		//listRequest.clear();
		for(int i = 0; i < listrequest.size(); i++) {
			this.listRequest.add(listrequest.get(i));
		}
	}
	public ArrayList<String> getResponseNodeName() {
		return responseNodeName;
	}
	public void setResponseNodeName(ArrayList<String> responseNodename) {
		//responseNodeName.clear();
		for(int i = 0; i < responseNodename.size(); i++) {
			this.responseNodeName.add(responseNodename.get(i));
		}
	}
	public ArrayList<LogFormat> getListResponse() {
		return listResponse;
	}
	public void setListResponse(ArrayList<LogFormat> listresponse) {
		//listResponse.clear();
		for(int i = 0; i < listresponse.size(); i++) {
			this.listResponse.add(listresponse.get(i));
		}
	}
//	public String getAbnorInfo() {
//		return abnorInfo;
//	}
//	public void setAbnorInfo(String abnorInfo) {
//		this.abnorInfo = abnorInfo;
//	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getStartServiceName() {
		return startServiceName;
	}

	public void setStartServiceName(String startServiceName) {
		this.startServiceName = startServiceName;
	}

	public Long getStartServiceTime() {
		return startServiceTime;
	}

	public void setStartServiceTime(Long long1) {
		this.startServiceTime = long1;
	}

	public String getCurServiceLogID() {
		return curServiceLogID;
	}

	public void setCurServiceLogID(String curServiceLogID) {
		this.curServiceLogID = curServiceLogID;
	}
	
	
}
