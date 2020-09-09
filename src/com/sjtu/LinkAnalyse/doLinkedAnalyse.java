package com.sjtu.LinkAnalyse;

import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;
import com.sjtu.LinkAnalyse.service.EventLogService;
import com.sjtu.LinkAnalyse.serviceImp.EventLogServiceImpl;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class doLinkedAnalyse {
	/**
	 * 从配置文件中读取内容
	 */
	//1.监测间隔。单位为s
	static String MonitorTimePeroid = null;
	//上次监测ID
    static volatile long lastAnalysisID = -1;
    static Long currentID = null;
    //初始化log4J
    private static void initLogRecord(){
        Properties props;
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
		lastAnalysisID = Long.parseLong(properties.getProperty("lastAnalysisID"));
		MonitorTimePeroid = properties.getProperty("MonitorTimePeriod");
        Date startDate = new Date();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run()
            {
                EventLogService service = new EventLogServiceImpl();
                try {
                	logger.info("开始研判!");
//                	logger.
//                	logger.info("数据库记录开始时间 :%d{yyyy-MM-dd HH:mm:ss}" ,lastTimeMillis);
                	//System.out.println("开始研判");

                    HashMap<String, ArrayList<LogFormat>> map = new HashMap<>();
                    //把位于lastTimeMillis和currentTimeMillis之间的记录全部取出来，并按照gid，将链路上的日志节点放入Map中。
                    List<LogFormat> list = service.findRecordWithID(lastAnalysisID);

                    if(list == null || list.size() == 0) {
                    	logger.error("数据库数据为空或已研判完毕。请检查数据库!");
                    	System.out.println("数据库无剩余数据。");
                    	return;
//                    	throw new RuntimeException("数据库数据为空或已研判完毕。请检查数据库!");
                    }
                    Collections.sort(list);
                    currentID = list.get(list.size()-1).getEventLog_request_id();
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
                    lastAnalysisID = currentID;

                    properties.setProperty("lastAnalysisID", Long.toString(lastAnalysisID));
                    OutputStream out = new FileOutputStream("./properties/analysis.properties");

                    properties.store(out, "Update " +"lastAnalysisID" + " name");
//                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    timer.cancel();
                }
            }
        },startDate,Integer.parseInt(MonitorTimePeroid) * 1000);
    	//},startDate,10 * 1000);
    }
}

