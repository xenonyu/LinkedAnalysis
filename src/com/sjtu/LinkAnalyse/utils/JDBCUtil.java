package com.sjtu.LinkAnalyse.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.PreparedStatement;

public class JDBCUtil {
	
	static ComboPooledDataSource dataSource = null;		
	static {
		dataSource = new ComboPooledDataSource();
		
	}
	
	/**
	 * 获取连接对象
	 * @return
	 * @throws SQLException
	 */
	public static Connection getconn() throws SQLException {
		
		return 	dataSource.getConnection();
	}
	
	/**
	 * 释放资源
	 * @param conn
	 * @param st
	 * @param rs
	 */
	public static void release(Connection conn, Statement st, ResultSet rs) {
		closeRs(rs);
		closeSt(st);
		closeConn(conn);
		
	}
//	public static void release(Connection conn, PreparedStatement ps, ResultSet rs) {
//		closeRs(rs);
//		closePs(ps);
//		closeConn(conn);
//		
//	}
	public static void release(Connection conn, Statement st) {
		closeSt(st);
		closeConn(conn);
		
	}
//	public static void release(Connection conn, PreparedStatement ps) {
//		closePs(ps);
//		closeConn(conn);
//		
//	}
	private static void closeRs(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			rs = null;
		}
	}
	private static void closeSt(Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			st = null;
		}
	}
	private static void closePs(PreparedStatement  ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			ps = null;
		}
	}
	private static void closeConn(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			conn = null;
		}
	}
}
