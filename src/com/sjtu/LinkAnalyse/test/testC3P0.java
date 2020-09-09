package com.sjtu.LinkAnalyse.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sjtu.LinkAnalyse.dao.EventLogDao;
import com.sjtu.LinkAnalyse.daoImp.EventLogDaoImpl;
import com.sjtu.LinkAnalyse.service.EventLogService;
import com.sjtu.LinkAnalyse.serviceImp.EventLogServiceImpl;
import com.sjtu.LinkAnalyse.utils.C3P0Utils;
import com.sjtu.LinkAnalyse.utils.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.Test;

public class testC3P0 {
    private EventLogDao dao = new EventLogDaoImpl();
    private EventLogService service = new EventLogServiceImpl();
    
    @Test
    public void testC3P02() throws Exception{
    	Connection conn = null;
		PreparedStatement ps = null;
		try {		
			//1.创建dataSource
			ComboPooledDataSource dataSource = new ComboPooledDataSource("demo");
			System.out.println(dataSource.getPassword());

			// 3.得到连接对象
			conn = dataSource.getConnection();

			String sql = "select * from event_log where id = ?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, 12711);

			ResultSet resultSet = ps.executeQuery();
			while(resultSet.next()){
				String serviceName = resultSet.getString("serviceName");
				int spanType = resultSet.getInt("spanType");
				long spanTimestamp = resultSet.getLong("spanTimestamp");
				System.out.println("serviceName="+serviceName
						            +" spanType="+spanType);
				System.out.println("................................................");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.closeConnection();
		}
    }
    @Test
	public void testC3P003() throws SQLException{
    	Connection conn = null;
		PreparedStatement ps = null;
		try {	
			//1.创建dataSource：就new了一个对象。源码在用的时候可以直接使用类加载器加载源码文件
			//默认会找xml中的default-config分支
			ComboPooledDataSource dataSource = new ComboPooledDataSource();
			//2.得到连接对象
			conn = dataSource.getConnection();
			
			
			String sql = "select * from t_user where id=?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, 1);

			ResultSet resultSet = ps.executeQuery();
			while(resultSet.next()){
				int id = resultSet.getInt("id");// 获取第一个列的值 编号id
				String userName = resultSet.getString("username"); // 获取第二个列的值 图书名称 bookName
				String passWord = resultSet.getString("password");// 获取第三列的值 图书作者 author
				String nickName = resultSet.getString("nickname");// 获取第四列的值 图书价格 price
				System.out.println("id="+id+" passWord="+passWord
						            +" nickName="+nickName);
				System.out.println("................................................");
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.closeConnection();
		}
	}
    @Test
	public void testC3P004() throws SQLException{
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

			String sql = "select * from event_log where eventLog_request_id=?";
			//String sql = "select * from event_log where global_id=?";
			ps = conn.prepareStatement(sql);
			//ps.setLong(1, 2328L);
			ps.setLong(1, 6661300L);
			
//			ps.setString(1, "03aebd5c01f5e2aba09871210fce833882da907dd003");
			
			ResultSet resultSet = ps.executeQuery();
			while(resultSet.next()){
				long id = resultSet.getLong("eventLog_request_id");// 获取第一个列的值 编号id
				byte[] serviceNameByte = resultSet.getBytes(2); // 获取第二个列的值 图书名称 bookName
				char[] serviceNameChar = new char[serviceNameByte.length / 4];
				for(int i = 0; i < serviceNameByte.length / 4; i++) {
					serviceNameChar[i] = (char)((serviceNameByte[i*4 + 2] & 0xFF) << 8 | (serviceNameByte[i*4 + 3] & 0xFF));
				}
				//String serviceName = new String(serviceNameByte,"UTF-8").replace("\u0000", "");
				String serviceName = String.valueOf(serviceNameChar);
				int spanType = resultSet.getInt("span_type");// 获取第三列的值 图书作者 author
				long spanTimestamp = resultSet.getLong("span_timestamp");// 获取第四列的值 图书价格 price
				Timestamp time = resultSet.getTimestamp("spanTimestamp_date");
				byte[] endpointIpBytes = resultSet.getBytes(6);
				String endpointIp = new String(endpointIpBytes,"UTF-8").replace("\u0000", "");
				byte[] endpointPortBytes = resultSet.getBytes(7);
				String endpointPort = new String(endpointPortBytes,"UTF-8").replace("\u0000", "");
				byte[] parameterBytes = resultSet.getBytes(8);
				String parameter = new String(parameterBytes,"UTF-8").replace("\u0000", "");
				byte[] errContentByte = resultSet.getBytes(9);
				String errContent = new String(errContentByte,"UTF-8").replace("\u0000", "");
				Timestamp createDate = resultSet.getTimestamp("create_date");
				byte[] globalIdBytes = resultSet.getBytes(11);
				String globalId = new String(globalIdBytes,"UTF-8").replace("\u0000", "");
				byte[] reIdBytes = resultSet.getBytes(12);
				String reIdBy = new String(reIdBytes,"UTF-8").replace("\u0000", "");
				
				System.out.println("id="+id+" serviceName="+serviceName
						            +" spanType="+spanType);
				System.out.println("................................................");
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtil.release(conn, ps);
		}
	}
}
