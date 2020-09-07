package com.sjtu.LinkAnalyse;

import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LinkedLogFormat;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;
import com.sjtu.LinkAnalyse.daoImp.EventLogDaoImpl;
import com.sjtu.LinkAnalyse.service.EventLogService;
import com.sjtu.LinkAnalyse.serviceImp.EventLogServiceImpl;
import com.sjtu.LinkAnalyse.utils.JsonUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class doLinkedAnalyse {
	/**
	 * 从配置文件中读取内容
	 */
	//1.监测间隔。单位为s
	static String MonitorTimePeroid = null;
	//2.首次监测时间。1970-01-01 08:00:00表示从第一条记录开始检测，否则设定监测时间
	static String firstMonitorTime = null;
	//3.数据库研判时间段
	static String DatePeroid = null;
	//上次监测时间
    static volatile long lastTimeMillis = 0;
    static volatile long firstTimeMillis = 0;
    static long currentTimeMillis = System.currentTimeMillis();
    //初始化log4J
    private static void initLogRecord(){
        Properties props = null;
        FileInputStream fis = null;
        try {
            props = new Properties();
            fis = new FileInputStream("./properties/log4j.properties");
            props.load(fis);
            PropertyConfigurator.configure(props);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            fis = null;
        }
    }
    public static void main(String[] args) throws Exception{
    	initLogRecord();
    	Logger logger  =  Logger.getLogger(doLinkedAnalyse.class );
    	
    	Properties properties = new Properties();
    	//法1，使用字节流加载文件
		InputStream is = new FileInputStream("./properties/analysis.properties");
		//法2，使用类加载器读取资源文件  当将src下源码编译成bin文件夹下的class时，顺便加载该文件流
		//InputStream is = JDBCUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
		//2.导入输入流
		properties.load(is);
		//3.读取属性
		firstMonitorTime = properties.getProperty("firstMonitorTime");
		
		//int firstMonitorTT = Integer.parseInt(firstMonitorTime);
		//int startAtFirstMonitorTime = 0;
		//if(firstMonitorTT != 0) startAtFirstMonitorTime = 1;
		//else startAtFirstMonitorTime = 0;
		DatePeroid = properties.getProperty("DatePeroid");
		MonitorTimePeroid = properties.getProperty("MonitorTimePeroid");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Date firstTime = new Date();
		long time = dateFormatter.parse(firstMonitorTime).getTime();
		boolean first = true;
        Date startDate = new Date();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run()
            {
            	//HashMap<String, ArrayList<LogFormat>> map = new HashMap<>();
                EventLogService service = new EventLogServiceImpl();
                try {
                	//如果默认，则设置首次监测时间
                	if(lastTimeMillis == firstTimeMillis) {
                		if (time == 0) {
                        	firstTimeMillis =  service.findFirstRecordTime();
                            lastTimeMillis = firstTimeMillis;
                        }
                        else {
                        	long firstTime = dateFormatter.parse(firstMonitorTime).getTime();
                        	lastTimeMillis = firstTime;
                        }
                		currentTimeMillis = lastTimeMillis + Long.parseLong(DatePeroid) * 1000L;
                	}
                	
                	logger.info("开始研判!");
//                	logger.
//                	logger.info("数据库记录开始时间 :%d{yyyy-MM-dd HH:mm:ss}" ,lastTimeMillis);
                	//System.out.println("开始研判");
                	
                    HashMap<String, ArrayList<LogFormat>> map = new HashMap<>();
                    //把位于lastTimeMillis和currentTimeMillis之间的记录全部取出来，并按照gid，将链路上的日志节点放入Map中。
                    List<LogFormat> list = service.findRecordWithPeriod(lastTimeMillis, currentTimeMillis);
                    if(list == null || list.size() == 0) {
                    	logger.error("数据库数据为空或已研判完毕。请检查数据库!");
//                    	throw new RuntimeException("数据库数据为空或已研判完毕。请检查数据库!");
                    }
                    for (int i = 0; i < list.size(); i++) {
                        LogFormat log = list.get(i);
                        if(log == null) continue;
                        String gid = log.getGlobal_id();
                        if(gid != null && gid.length() != 0 &&!gid.equals("")) {
                        	if(!map.containsKey(gid)) {
                        		ArrayList<LogFormat> firstList = new ArrayList<>();
                            	firstList.add(log);
                                map.put(gid, firstList);
                        	}else {
                        		ArrayList<LogFormat> resultList = map.get(gid);
                            	resultList.add(log);
                                map.put(gid, resultList);
                        	}
                        }
                    }
                    if(!map.isEmpty()) {
                    	//将同一global_id上的节点放入一个SortedMap中，Entry构造：key为日志类型为4的节点的service_name : re_id； value为EventLogNodInf类的实例
                    	List<SortedMap<String, EventLogNodInf>> linkedLogList = service.generateLinkedLogFormat(map);
                        //将链路持久化到数据库
                    	service.saveAllLinked2DB(linkedLogList);
                    }
                    Set<String> keySet = new HashSet<>();
                    if(!map.isEmpty()) {
                    	keySet = map.keySet();
                    }
                    //打印链路到日志文件
                    String result = "LinkedId is :";
                    result += keySet.toString();
                    logger.info(result);
                    System.out.println(result);
                    map = new HashMap<>();
                    lastTimeMillis = currentTimeMillis;
                    currentTimeMillis = lastTimeMillis + Long.parseLong(DatePeroid) * 1000L;
                } catch (Exception e) {
                    e.printStackTrace();
                    timer.cancel();
                }
            }
        },startDate,Integer.parseInt(MonitorTimePeroid) * 1000);
    	//},startDate,10 * 1000);
    }
}

