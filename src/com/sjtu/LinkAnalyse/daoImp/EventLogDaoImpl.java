package com.sjtu.LinkAnalyse.daoImp;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LinkedLogFormat;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;
import com.sjtu.LinkAnalyse.dao.EventLogDao;

import com.sjtu.LinkAnalyse.utils.C3P0Utils;
import com.sjtu.LinkAnalyse.utils.JDBCUtil;
import com.sjtu.LinkAnalyse.utils.JsonUtils;
import com.sjtu.LinkAnalyse.utils.JSONSerializer;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

public class EventLogDaoImpl implements EventLogDao {
    public List<LogFormat> findRecordWithPeriod(long startTime, long endTime) throws Exception{
        //QueryRunner runner = new QueryRunner(C3P0Utils.getDataSource());
//        String sql = "select * from event_log where span_timestamp between " + startTime + " and " + endTime;
//        List<LogFormat> list = runner.query(sql, new BeanListHandler<LogFormat>(LogFormat.class));
    	Connection conn = null;
		PreparedStatement ps = null;
		List<LogFormat> list = new ArrayList<>();
		try {	
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource();
			//2.得到连接对象
			conn = dataSource.getConnection();

//			String sql = "insert into account values(null, ?, ?)";
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, "guodegang");
//			ps.setInt(2, 1000);
//
//			ps.executeUpdate();
			
			String sql = "select * from event_log where spanTimestamp + 0 between ? and ?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, startTime);
			ps.setLong(2, endTime);
			ResultSet resultSet = ps.executeQuery();

			while(resultSet.next()){
//				long eventLog_request_id = resultSet.getLong("eventLog_request_id");// 获取第一个列的值 编号id
//				byte[] serviceNameByte = resultSet.getBytes(2); // 获取第二个列的值 图书名称 bookName
//				String serviceName = "";
//
//				char[] serviceNameChar = new char[serviceNameByte.length / 4];
//				for(int i = 0; i < serviceNameByte.length / 4; i++) {
//					serviceNameChar[i] = (char)((serviceNameByte[i*4 + 2] & 0xFF) << 8 | (serviceNameByte[i*4 + 3] & 0xFF));
//				}
//				//String serviceName = new String(serviceNameByte,"UTF-8").replace("\u0000", "");
//				//String serviceName = String.valueOf(serviceNameChar);
//
//				if(serviceNameByte != null) {
//					serviceName = String.valueOf(serviceNameChar);
//				}
//				int spanType = resultSet.getInt("span_type");// 获取第三列的值 图书作者 author
//				long spanTimestamp = resultSet.getLong("span_timestamp");// 获取第四列的值 图书价格 price
//				Timestamp time = resultSet.getTimestamp("spanTimestamp_date");
//				byte[] endpointIpBytes = resultSet.getBytes(6);
//				String endpointIp = "";
//				if(endpointIpBytes != null) {
//					endpointIp = new String(endpointIpBytes,"UTF-8").replace("\u0000", "");
//				}
//				byte[] endpointPortBytes = resultSet.getBytes(7);
//				String endpointPort = "";
//				if(endpointPortBytes != null) {
//					endpointPort = new String(endpointPortBytes,"UTF-8").replace("\u0000", "");
//				}
//				byte[] parameterBytes = resultSet.getBytes(8);
//				String parameter = "";
//				if(parameterBytes != null) {
//					parameter = new String(parameterBytes,"UTF-8").replace("\u0000", "");
//				}
//				byte[] errContentByte = resultSet.getBytes(9);
//				String errContent = "";
//				if(errContentByte != null) {
//					errContent = new String(errContentByte,"UTF-8").replace("\u0000", "");
//				}
//				Timestamp createDate = resultSet.getTimestamp("create_date");
//				byte[] globalIdBytes = resultSet.getBytes(11);
//				String globalId = "";
//				if(globalIdBytes != null) {
//					globalId = new String(globalIdBytes,"UTF-8").replace("\u0000", "");
//				}
//				byte[] reIdBytes = resultSet.getBytes(12);
//				String reId = "";
//				if(reIdBytes != null) {
//					reId = new String(reIdBytes,"UTF-8").replace("\u0000", "");
//				}
//				byte[] parent_reidByte = resultSet.getBytes(13);
//				String parentReID = "";
//				if(parent_reidByte != null) {
//					parentReID = new String(parent_reidByte,"UTF-8").replace("\u0000", "");
//				}
//				LogFormat format = new LogFormat();
//				format.setEventLog_request_id(eventLog_request_id);
//				format.setService_name(serviceName);
//				format.setSpan_type(spanType);
//				format.setSpan_timestamp(spanTimestamp);
//				format.setSpanTimestamp_date(time);
//				format.setEndpoint_ip(endpointIp);
//				format.setEndpoint_port(endpointPort);
//				format.setParameter(parameter);
//				format.setErrContent(errContent);
//				format.setCreate_date(createDate);
//				format.setGlobal_id(globalId);
//				format.setRe_id(reId);
//				format.setParent_reid(parentReID);
				long eventLog_request_id = resultSet.getLong("id");// 获取第一个列的值 编号id
				String serviceName = resultSet.getString("serviceName");
				serviceName = serviceName_check(serviceName);
				int spanType = resultSet.getInt("spanType");// 获取第三列的值 图书作者 author
				long spanTimestamp = resultSet.getLong("spanTimestamp");// 获取第四列的值 图书价格 price
				String endpointIp = resultSet.getString("endpointIp");
				String endpointPort = resultSet.getString("endpointPort");
				String parameter = resultSet.getString("parameter");
				String errContent = resultSet.getString("errContent");
				String globalId = resultSet.getString("globalId");
				String reId = resultSet.getString("reId");
				String parentReID = resultSet.getString("parentReID");
				LogFormat format = new LogFormat();
				format.setEventLog_request_id(eventLog_request_id);
				format.setService_name(serviceName);
				format.setSpan_type(spanType);
				format.setSpan_timestamp(spanTimestamp);
				//format.setSpanTimestamp_date(time);
				format.setEndpoint_ip(endpointIp);
				format.setEndpoint_port(endpointPort);
				format.setParameter(parameter);
				format.setErrContent(errContent);
				//format.setCreate_date(createDate);
				format.setGlobal_id(globalId);
				format.setRe_id(reId);
				format.setParent_reid(parentReID);
				if(globalId != null && globalId.length() != 0) {
					list.add(format);
				}
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}
    	
    	
        return list;
    }

	public static String serviceName_check(String serviceName) throws IOException {
		if((serviceName==null)||(serviceName=="")) return serviceName;
		if(serviceName.equals("XD_Approve")) return "西电核准系统";
		else if(serviceName.equals("XD_Authentication")) return "西电认证系统";
		else if(serviceName.equals("XD_Middle")) return "西电中间件";
		else if(serviceName.equals("XD_Controller")) return "西电调度系统";
		else if(serviceName.equals("QueryInteraction")) return "交付查询接口";
		else return serviceName;
	}

	public Long findFirstRecordTime() throws Exception {
        //QueryRunner runner = new QueryRunner(C3P0Utils.getDataSource());
//        String sql = "select * from event_log limit 1";
//        LogFormat logFormat = runner.query(sql, new BeanHandler<LogFormat>(LogFormat.class));
//        String sql ="select * from event_log where eventLog_request_id= ?";
//        //LogFormat logFormat = runner.query(sql, new BeanHandler<LogFormat>(LogFormat.class),2328L);
//        Object[] param = {2328L}; 
//        LogFormat format = runner.query(sql, param, new BeanHandler<LogFormat>(LogFormat.class));
        Connection conn = null;
		PreparedStatement ps = null;
		LogFormat format = new LogFormat();
		try {	
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource();
			//2.得到连接对象
			conn = dataSource.getConnection();

//			String sql = "insert into account values(null, ?, ?)";
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, "guodegang");
//			ps.setInt(2, 1000);
//
//			ps.executeUpdate();
			
			String sql = "select * from event_log order by spanTimestamp + 0 limit ?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, 1);
			ResultSet resultSet = ps.executeQuery();
			while(resultSet.next()){
				long eventLog_request_id = resultSet.getLong("id");// 获取第一个列的值 编号id
				String serviceName = resultSet.getString("serviceName");
				serviceName = serviceName_check(serviceName);
				int spanType = resultSet.getInt("spanType");// 获取第三列的值 图书作者 author
				long spanTimestamp = resultSet.getLong("spanTimestamp");// 获取第四列的值 图书价格 price
				String endpointIp = resultSet.getString("endpointIp");
				String endpointPort = resultSet.getString("endpointPort");
				String parameter = resultSet.getString("parameter");
				String errContent = resultSet.getString("errContent");
				String globalId = resultSet.getString("globalId");
				String reId = resultSet.getString("reId");
				String parentReID = resultSet.getString("parentReID");
				format.setEventLog_request_id(eventLog_request_id);
				format.setService_name(serviceName);
				format.setSpan_type(spanType);
				format.setSpan_timestamp(spanTimestamp);
				//format.setSpanTimestamp_date(time);
				format.setEndpoint_ip(endpointIp);
				format.setEndpoint_port(endpointPort);
				format.setParameter(parameter);
				format.setErrContent(errContent);
				//format.setCreate_date(createDate);
				format.setGlobal_id(globalId);
				format.setRe_id(reId);
				format.setParent_reid(parentReID);
//				long eventLog_request_id = resultSet.getLong("id");// 获取第一个列的值 编号id
//				byte[] serviceNameByte = resultSet.getBytes("serviceName"); // 获取第二个列的值 serviceName
//				String serviceName = new String(serviceNameByte,"UTF-8").replace("\u0000", "");
//				int spanType = resultSet.getInt("spanType");// 获取第三列的值 图书作者 author
//				long spanTimestamp = resultSet.getLong("spanTimestamp");// 获取第四列的值 图书价格 price
//				Timestamp time = resultSet.getTimestamp("spanTimestamp_date");
//				byte[] endpointIpBytes = resultSet.getBytes(6);
//				String endpointIp = new String(endpointIpBytes,"UTF-8").replace("\u0000", "");
//				byte[] endpointPortBytes = resultSet.getBytes(7);
//				String endpointPort = new String(endpointPortBytes,"UTF-8").replace("\u0000", "");
//				byte[] parameterBytes = resultSet.getBytes(8);
//				String parameter = new String(parameterBytes,"UTF-8").replace("\u0000", "");
//				byte[] errContentByte = resultSet.getBytes(9);
//				String errContent = new String(errContentByte,"UTF-8").replace("\u0000", "");
//				Timestamp createDate = resultSet.getTimestamp("create_date");
//				byte[] globalIdBytes = resultSet.getBytes(11);
//				String globalId = new String(globalIdBytes,"UTF-8").replace("\u0000", "");
//				byte[] reIdBytes = resultSet.getBytes(12);
//				String reId = new String(reIdBytes,"UTF-8").replace("\u0000", "");
//				byte[] parent_reidByte = resultSet.getBytes(13);
//				String parentReID = new String(parent_reidByte,"UTF-8").replace("\u0000", "");
//
//				format.setEventLog_request_id(eventLog_request_id);
//				format.setService_name(serviceName);
//				format.setSpan_type(spanType);
//				format.setSpan_timestamp(spanTimestamp);
//				format.setSpanTimestamp_date(time);
//				format.setEndpoint_ip(endpointIp);
//				format.setEndpoint_port(endpointPort);
//				format.setParameter(parameter);
//				format.setErrContent(errContent);
//				format.setCreate_date(createDate);
//				format.setGlobal_id(globalId);
//				format.setRe_id(reId);
//				format.setParent_reid(parentReID);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
			return format.getSpan_timestamp();
		}
        
    }
	public void saveAllLinked2DB(List<SortedMap<String, EventLogNodInf>> list) throws Exception{
//        //QueryRunner runner = new QueryRunner(C3P0Utils.getDataSource());
////        String sql = "select * from event_log where span_timestamp between " + startTime + " and " + endTime;
////        List<LogFormat> list = runner.query(sql, new BeanListHandler<LogFormat>(LogFormat.class));
    	Connection conn = null;
		PreparedStatement ps = null;
		try {	
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource();
			//2.得到连接对象
			conn = dataSource.getConnection();

//			String sql = "insert into account values(null, ?, ?)";
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, "guodegang");
//			ps.setInt(2, 1000);
//
//			ps.executeUpdate();
			for(int i = 0; i < list.size(); i++) {
				SortedMap<String, EventLogNodInf> map = list.get(i);
				ArrayList<EventLogNodInf> arrayList = new ArrayList<>();
				String globalID = "";
				String startName = "";
				int isAbormal = 0;
				Long startServiceTime = 0L;
				boolean findAbnornal = false;
				for(Entry<String, EventLogNodInf> entry : map.entrySet()) {
					String serviceNameStore = entry.getKey();
					if(serviceNameStore.endsWith("first")) {
						startName = entry.getValue().getStartServiceName();
						startServiceTime = entry.getValue().getStartServiceTime();
						globalID = entry.getValue().getGid();
					}
					//将日志节点第一个异常赋予链路作为链路的异常类型
					ArrayList<LogFormat> listRequest = entry.getValue().getListRequest();
					for(int j = 0; j < listRequest.size() && findAbnornal == false; j++) {
						LogFormat format = listRequest.get(j);
						if(format.getSpan_type() == 1 && format.getIsAbormal() != null && format.getIsAbormal() != 0) {
							isAbormal = 1;
							findAbnornal = true;
							break;
						}
					}
					arrayList.add(entry.getValue());
				}
				String sql = "insert into linked_record values(null, ?, ?, ?, ?, ?)";
				ps = conn.prepareStatement(sql);

				ps.setString(1, globalID);
				//String jsonArray = JsonUtils.list2Json(arrayList);
				String jsonArray = JSONSerializer.serializeIncludeNull(arrayList);
				ps.setString(2, jsonArray);
				ps.setString(3, startName);
				ps.setTimestamp(4, new Timestamp(startServiceTime));
				ps.setInt(5, isAbormal);
				ps.executeUpdate();
				System.out.println("globalID:\n" + globalID);
				System.out.println("\njsonArray:\n" + jsonArray);
				System.out.println("\nstartName:\n" + startName);
				System.out.println("\nstartTime:\n" + new Timestamp(startServiceTime));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}
    	
    	
    }
}
