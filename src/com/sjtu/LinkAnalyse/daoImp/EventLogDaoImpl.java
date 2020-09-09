package com.sjtu.LinkAnalyse.daoImp;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;
import com.sjtu.LinkAnalyse.dao.EventLogDao;

import com.sjtu.LinkAnalyse.utils.JDBCUtil;
import com.sjtu.LinkAnalyse.utils.JSONSerializer;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

public class EventLogDaoImpl implements EventLogDao {
	public List<LogFormat> findRecordWithID(long startID) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		List<LogFormat> list = new ArrayList<>();
		try {
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource("121");
			//2.得到连接对象
			conn = dataSource.getConnection();
			String sql = "select * from event_log where id + 0 > ?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, startID);
			parseResultSet(ps, list);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}


		return list;
	}

	private void parseResultSet(PreparedStatement ps, List<LogFormat> list) throws SQLException, IOException {
		ResultSet resultSet = ps.executeQuery();

		while (resultSet.next()) {
			long eventLog_request_id = resultSet.getLong("id");
			String serviceName = resultSet.getString("serviceName");
			serviceName = serviceName_check(serviceName);
			int spanType = resultSet.getInt("spanType");
			long spanTimestamp = resultSet.getLong("spanTimestamp");
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
			format.setEndpoint_ip(endpointIp);
			format.setEndpoint_port(endpointPort);
			format.setParameter(parameter);
			format.setErrContent(errContent);
			format.setGlobal_id(globalId);
			format.setRe_id(reId);
			format.setParent_reid(parentReID);
			if (globalId != null && globalId.length() != 0) {
				list.add(format);
			}
		}
	}

	@Override
	public List<LogFormat> findRecordWithGlobalID(String globalID) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		List<LogFormat> list = new ArrayList<>();
		try {
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			ComboPooledDataSource dataSource = new ComboPooledDataSource("121");
			//2.得到连接对象
			conn = dataSource.getConnection();
			String sql = "select * from event_log where globalID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, globalID);
			parseResultSet(ps, list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}


		return list;
	}

	public static String serviceName_check(String serviceName) throws IOException {
		if ((serviceName == null) || (serviceName == "")) return serviceName;
		if (serviceName.equals("XD_Approve")) return "西电核准系统";
		else if (serviceName.equals("XD_Authentication")) return "西电认证系统";
		else if (serviceName.equals("XD_Middle")) return "西电中间件";
		else if (serviceName.equals("XD_Controller")) return "西电调度系统";
		else if (serviceName.equals("QueryInteraction")) return "交付查询接口";
		else return serviceName;
	}

	public void saveAllLinked2DB(List<SortedMap<String, EventLogNodInf>> list) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource("ljw");
			//2.得到连接对象
			conn = dataSource.getConnection();

			for (int i = 0; i < list.size(); i++) {
				SortedMap<String, EventLogNodInf> map = list.get(i);
				ArrayList<EventLogNodInf> arrayList = new ArrayList<>();
				String globalID = "";
				String startName = "";
				int isAbormal = 0;
				Long startServiceTime = 0L;
				boolean findAbnornal = false;
				for (Entry<String, EventLogNodInf> entry : map.entrySet()) {
					String serviceNameStore = entry.getKey();
					if (serviceNameStore.endsWith("first")) {
						startName = entry.getValue().getStartServiceName();
						startServiceTime = entry.getValue().getStartServiceTime();
						globalID = entry.getValue().getGid();
					}
					//将日志节点第一个异常赋予链路作为链路的异常类型
					ArrayList<LogFormat> listRequest = entry.getValue().getListRequest();
					for (int j = 0; j < listRequest.size() && !findAbnornal; j++) {
						LogFormat format = listRequest.get(j);
						if (format.getSpan_type() != null && format.getSpan_type() == 1 && format.getIsAbormal() != null && format.getIsAbormal() != 0) {
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

	@Override
	public boolean checkExistGlobalID(String global_id, boolean delete) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		boolean exist = false;
		try {
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource("ljw");
			//2.得到连接对象
			conn = dataSource.getConnection();
			String sql = "select global_id from linked_record where global_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, global_id);
			res = ps.executeQuery();
			/**
			 * 可以选择是否删除查询的数据
			 */
			if (delete) {
				sql = "DELETE FROM linked_record where global_id = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, global_id);
				ps.executeUpdate();
			}
			if (res.next()){
				exist = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}

		return exist;
	}

	@Override
	public boolean checkExistGlobalID(String global_id) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		boolean exist = false;
		try {
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource();
			//2.得到连接对象
			conn = dataSource.getConnection();
			String sql = "select global_id from linked_record where global_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, global_id);
			res = ps.executeQuery();
			if (res.next()) {
				exist = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}
		return exist;
	}
}
