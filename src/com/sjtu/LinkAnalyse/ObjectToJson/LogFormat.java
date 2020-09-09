package com.sjtu.LinkAnalyse.ObjectToJson;

import com.alibaba.fastjson.annotation.JSONType;
import com.sjtu.GenGid;
import com.sjtu.LinkAnalyse.ObjectToJson.parameter.Parameter;
import com.sjtu.LinkAnalyse.utils.JsonUtils;

import net.sf.json.JSONObject;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.UUID;
@JSONType(orders={"eventLog_request_id","service_name","span_type","span_timestamp","spanTimestamp_date", "endpoint_ip", "endpoint_port", "parameter", "errContent", "create_date", "global_id", "re_id", "parent_reid", "isAbormal", "abnormalType", "responseTime"})
public class LogFormat implements Comparable<LogFormat>{
    private Long eventLog_request_id;
    private String service_name;
    private Integer span_type;
    private Long span_timestamp;
    private Timestamp spanTimestamp_date;
    private String endpoint_ip;
    private String endpoint_port;
    private String parameter;
    private String errContent;
    private Timestamp create_date;
    private String global_id;
    private String re_id;
    private String parent_reid;
    //增加异常处理
    //1:异常；0:正常
    private Integer isAbormal = null;
    //1:无响应；2:超时有响应；3:超时无响应
    private Integer abnormalType = null;
    //响应时间
    private Long responseTime = null;
    public LogFormat() {
	}

	public LogFormat(Long eventLog_request_id, String service_name, Integer span_type, Long span_timestamp,
			Timestamp spanTimestamp_date, String endpoint_ip, String endpoint_port, String parameter, String errContent,
			Timestamp create_date, String global_id, String re_id, String parent_reid) {
		super();
		this.eventLog_request_id = eventLog_request_id;
		this.service_name = service_name;
		this.span_type = span_type;
		this.span_timestamp = span_timestamp;
		this.spanTimestamp_date = spanTimestamp_date;
		this.endpoint_ip = endpoint_ip;
		this.endpoint_port = endpoint_port;
		this.parameter = parameter;
		this.errContent = errContent;
		this.create_date = create_date;
		this.global_id = global_id;
		this.re_id = re_id;
		this.parent_reid = parent_reid;
	}
	public Long getEventLog_request_id() {
		return eventLog_request_id;
	}
	public void setEventLog_request_id(Long eventLog_request_id) {
		this.eventLog_request_id = eventLog_request_id;
	}
	public String getService_name() {
		return service_name;
	}
	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
	public Integer getSpan_type() {
		return span_type;
	}
	public void setSpan_type(Integer span_type) {
		this.span_type = span_type;
	}
	public Long getSpan_timestamp() {
		return span_timestamp;
	}
	public void setSpan_timestamp(Long span_timestamp) {
		this.span_timestamp = span_timestamp;
	}
	public Timestamp getSpanTimestamp_date() {
		return spanTimestamp_date;
	}
	public void setSpanTimestamp_date(Timestamp spanTimestamp_date) {
		this.spanTimestamp_date = spanTimestamp_date;
	}
	public String getEndpoint_ip() {
		return endpoint_ip;
	}
	public void setEndpoint_ip(String endpoint_ip) {
		this.endpoint_ip = endpoint_ip;
	}
	public String getEndpoint_port() {
		return endpoint_port;
	}
	public void setEndpoint_port(String endpoint_port) {
		this.endpoint_port = endpoint_port;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getErrContent() {
		return errContent;
	}
	public void setErrContent(String errContent) {
		this.errContent = errContent;
	}
	public Timestamp getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Timestamp create_date) {
		this.create_date = create_date;
	}
	public String getGlobal_id() {
		return global_id;
	}
	public void setGlobal_id(String global_id) {
		this.global_id = global_id;
	}
	public String getRe_id() {
		return re_id;
	}
	public void setRe_id(String re_id) {
		this.re_id = re_id;
	}
	public String getParent_reid() {
		return parent_reid;
	}
	public void setParent_reid(String parent_reid) {
		this.parent_reid = parent_reid;
	}

	public Integer getIsAbormal() {
		return isAbormal;
	}

	public void setIsAbormal(Integer isAbormal) {
		this.isAbormal = isAbormal;
	}

	public Integer getAbnormalType() {
		return abnormalType;
	}

	public void setAbnormalType(Integer abnormalType) {
		this.abnormalType = abnormalType;
	}

	public Long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Long responseTime) {
		this.responseTime = responseTime;
	}

	public static String Obj2Json(LogFormat log){
        return JsonUtils.objectToJson(log);
    }
	public static LogFormat Json2Obj(String json) {
		return JsonUtils.jsonToPojo(json, LogFormat.class);
	}
//	public static String Object2Json(Object obj){  
//        JSONObject json = JSONObject.fromObject(obj);//将java对象转换为json对象  
//        String str = json.toString();//将json对象转换为字符串  
//          
//        return str;  
//    }  
    public static void main(String[] args) throws Exception {
        Long eventLog_request_id = 2233L;
    	String serviceName = "ycxwrhfx";
        Integer spanType = 3;
        long spanTimestamp = System.currentTimeMillis();
        Timestamp spanTimeStamp = new Timestamp(spanTimestamp); 
        InetAddress addr = InetAddress.getLocalHost();
        String endpointPortIp = addr.getHostAddress().toString(); //获取本机ip
        String endpointPort = "80";
        String parameter = "";
        String errorConent = "请求超时，超时时间为15s";
        String gid = GenGid.generateGid(serviceName);
        String UUid = UUID.randomUUID().toString().replaceAll("-", "");
        LogFormat format = new LogFormat();
        format.setEventLog_request_id(eventLog_request_id);
        format.setService_name(serviceName);
        format.setSpan_type(spanType);
        format.setSpanTimestamp_date(spanTimeStamp);
        format.setEndpoint_ip(endpointPortIp);
        format.setEndpoint_port(endpointPort);
        format.setParameter(parameter);
        format.setErrContent(errorConent);
        format.setCreate_date(spanTimeStamp);
        format.setGlobal_id(gid);
        format.setRe_id(UUid);
        System.out.println(LogFormat.Obj2Json(format));
        //LogFormat format = new LogFormat(serviceName, spanType, spanTimestamp, endpointPortIp, endpointPortPort, null, errorConent, gid, UUid);
        //System.out.println(LogFormat.Obj2Json(format));
    }

	/**
	 * 添加对比
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(LogFormat o) {
		return (int) (this.eventLog_request_id - o.eventLog_request_id);
	}
}

