package com.sjtu.LinkAnalyse.serviceImp;

import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LinkedLogFormat;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;
import com.sjtu.LinkAnalyse.dao.EventLogDao;
import com.sjtu.LinkAnalyse.service.EventLogService;
import com.sjtu.LinkAnalyse.daoImp.EventLogDaoImpl;
import com.sjtu.LinkAnalyse.utils.JsonUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class EventLogServiceImpl implements EventLogService {
    private EventLogDao eventLogDao = new EventLogDaoImpl();
    public List<LogFormat> findRecordWithPeriod(long startTime, long endTime) throws Exception{
        return eventLogDao.findRecordWithPeriod(startTime, endTime);
    }

    public Long findFirstRecordTime() throws Exception {

        return eventLogDao.findFirstRecordTime();
    }

    public List<SortedMap<String, EventLogNodInf>> generateLinkedLogFormat(HashMap<String, ArrayList<LogFormat>> map){
    	ArrayList<SortedMap<String, EventLogNodInf>> list = new ArrayList<>();
		/**
		 * 遍历每个globalId, 即每个链路请求
		 */
		for (Map.Entry<String, ArrayList<LogFormat>> entry: map.entrySet()) {
            ArrayList<LogFormat> resList = entry.getValue();
            LinkedLogFormat headExcepLinkedLog = null;
            LinkedLogFormat secondExcepLinkedLog = null;
            boolean excepFirst = true;
            ArrayList<LogFormat> list_logType4And1 = new ArrayList<>();
            ArrayList<LogFormat> list_logType8And5 = new ArrayList<>();
			/**
			 * 遍历链路的每个节点
			 * list_logType4And1 保存类型1，4的日志
			 * list_logType8And5 保存类型8，5的日志
			 * 将
			 */
			for (int i = 0; i < resList.size(); i++) {
                LogFormat logFormat = resList.get(i);
                //分别向两类list种添加日志节点
                int spanType = logFormat.getSpan_type();
                
                if(spanType == 4 || spanType == 1){
                	list_logType4And1.add(logFormat);
                }
                else if(spanType == 5 || spanType == 8){
                	list_logType8And5.add(logFormat);
                }
                //非类型为4，1；5，8的日志节点，即为异常日志节点。需要进行研判
                else {
                	LogFormat errLogFormat = resList.get(i);
                	LinkedLogFormat errorLinked = new LinkedLogFormat(errLogFormat);
                	if(excepFirst) {
                		headExcepLinkedLog = errorLinked;
                		secondExcepLinkedLog = headExcepLinkedLog;
                		excepFirst = false;
                	}
                	else {
                		secondExcepLinkedLog.setNex(errorLinked);
                		errorLinked.setPre(secondExcepLinkedLog);
                		secondExcepLinkedLog = secondExcepLinkedLog.getNex();
                	}
                }
               
            }
            SortedMap<String, EventLogNodInf> sortedMap = new TreeMap<>();
			/**
			 * 通过循环迭代
			 * hasNext():判断是否存在下一个元素
			 * 1.1 找到链路中所有parent_id为空，且类型为4（发送请求）的日志节点，即为初始节点
			 */
            ArrayList<String> reIDList;
            LogFormat format;
            Iterator<LogFormat> iterator = list_logType4And1.iterator();
            LogFormat startFormat;
            while(iterator.hasNext()){
            	format = iterator.next();
            	if(format.getSpan_type() == 4 && (format.getParent_reid() == null || format.getParent_reid().equals(""))) {
            		String startReID = format.getRe_id();
            		startFormat = format; //startFormat 即为初始节点
					/**
					 * 1.2 找到类型为1且parent_reid 为 reID的所有节点。即为父节点的下一个节点（需要改）
					 */
					Iterator<LogFormat> iteratorSecond = list_logType4And1.iterator();
//					iterator = list_logType4And1.iterator(); // 初始化 iterator 用来寻找第二个节点
					LogFormat secondFormat;
                    while(iteratorSecond.hasNext()){
                    	format = iteratorSecond.next();
						/**
						 * 如果找到了第二个节点的类型1数据
						 */
						if(format.getSpan_type() == 1 && format.getParent_reid().equals(startReID)) {
							/**
							 * 如果 sortedMap 不包含初始节点，那么就添加到sortedMap，
							 * requestNodeName 添加初始节点信息，第二个节点信息
							 * ListRequest 添加初始节点，第二个节点
							 */
							secondFormat = format;
							if(!sortedMap.containsKey(startFormat.getService_name()+":first")) {
                				ArrayList<String> requestNodeName = new ArrayList<>();
                				requestNodeName.add(startFormat.getService_name()+":"+startFormat.getRe_id());
                				requestNodeName.add(secondFormat.getService_name()+":"+secondFormat.getRe_id());
                				ArrayList<LogFormat> listRequest = new ArrayList<>();
                				listRequest.add(startFormat);
                				listRequest.add(secondFormat);
                				ArrayList<String> responseNodeName = new ArrayList<>();
                				ArrayList<LogFormat> listResponse = new ArrayList<>();
								EventLogNodInf startNodInf = new EventLogNodInf();
                				startNodInf.setCurServiceLogID(startFormat.getService_name()+":first");
                				startNodInf.setGid(startFormat.getGlobal_id());
                				startNodInf.setRequestNodeName(requestNodeName);
                				startNodInf.setListRequest(listRequest);
                				startNodInf.setResponseNodeName(responseNodeName);
                				startNodInf.setListResponse(listResponse);
                				startNodInf.setStartServiceName(startFormat.getService_name());
                				startNodInf.setStartServiceTime(startFormat.getSpan_timestamp());
                				sortedMap.put(startFormat.getService_name()+":first", startNodInf);
                			}
							/**
							 * 如果sortedMap 包含初始节点信息
							 * startNodeName 添加初始节点和第二个节点的信息
							 * ListRequest 添加初始节点和第二个节点的信息
							 */
                			else {
                				EventLogNodInf startNodInf = sortedMap.get(startFormat.getService_name()+":first");
                				ArrayList<String> startNodeName = startNodInf.getRequestNodeName();
                				startNodeName.add(startFormat.getService_name()+":"+startFormat.getRe_id());
                				startNodeName.add(secondFormat.getService_name()+":"+secondFormat.getRe_id());
                				ArrayList<LogFormat> listRequest = startNodInf.getListRequest();
                				listRequest.add(startFormat);
                				listRequest.add(secondFormat);
								/**
								 * 找到类型为4，parentID为第二个节点的点
								 */
//								for(int i = 0; i < list_logType4And1.size(); i++) {
//                					LogFormat format3 = list_logType4And1.get(i);
//                					if(format3.getSpan_type() == 4 && format3.getParent_reid() != null && format3.getParent_reid().equals(secondFormat.getRe_id())) {
//                						listRequest.add(format3);
//                						startNodeName.add(format3.getService_name()+":"+format3.getRe_id());
//                					}
//                				}
                				startNodInf.setCurServiceLogID(startFormat.getService_name()+":first");
                				startNodInf.setGid(startFormat.getGlobal_id());
                				startNodInf.setRequestNodeName(startNodeName);
                				sortedMap.put(startFormat.getService_name()+":first", startNodInf);
                			}
							/**
							 * 找到1对4_1后，研判其是否存在超时有响应和超时无响应两种异常
							 */
							Iterator<LogFormat> iterator8_5 = list_logType8And5.iterator();
                			boolean findResponseLog5 = false;
                			EventLogNodInf startNodInf = sortedMap.get(startFormat.getService_name()+":"+"first");
                			while(iterator8_5.hasNext()){
                				LogFormat formatType5 = iterator8_5.next();
								/**
								 * 如果有请求日志4，1，且有响应日志5，8
								 */
								if(formatType5.getSpan_type() == 5 && formatType5.getRe_id().equals(startReID)) {
                        			findResponseLog5 = true;
                        			long responseTime = formatType5.getSpan_timestamp() - startFormat.getSpan_timestamp();
									/**
									 * 判断是否超时，即responseTime>15s 即为超时有响应异常，对应异常类型为2
									 */

									ArrayList<LogFormat> listRequest;
                        			if(responseTime > 15000) {
                        				listRequest= startNodInf.getListRequest();
                        				for (int i = 0; i < listRequest.size(); i++) {
											LogFormat logFormat = listRequest.get(i);
											if(logFormat.getSpan_type() == 1 && logFormat.getParent_reid().equals(startReID)) {
												logFormat.setIsAbormal(1);
												logFormat.setAbnormalType(2);
												logFormat.setResponseTime(responseTime);
												break;
											}
										}
                        			}
									/**
									 * 表示可以找到对应4的5，而且响应未超时,则找到第二个节点类型1的数据，置abnormal 0
									 */
                        			else {
                        				listRequest = startNodInf.getListRequest();
                        				for (int i = 0; i < listRequest.size(); i++) {
											LogFormat logFormat = listRequest.get(i);
											if(logFormat.getSpan_type() == 1 && logFormat.getParent_reid().equals(startReID)) {
												logFormat.setIsAbormal(0);
												logFormat.setResponseTime(responseTime);
												break;
											}
										}
                        			}
                        			sortedMap.put(startFormat.getService_name()+":"+"first", startNodInf);
                        			break;
                        		}
                			}
							/**
							 * 对应无响应超时该种异常，异常类型为3  添加空日志节点，设置异常类型和是否异常
							 */
                			if(findResponseLog5 == false) {
                				ArrayList<LogFormat> listRequest = startNodInf.getListRequest();
                				for (int i = 0; i < listRequest.size(); i++) {
									LogFormat logFormat = listRequest.get(i);
									if(logFormat.getSpan_type() == 1 && logFormat.getParent_reid().equals(startReID)) {
										logFormat.setIsAbormal(1);
										logFormat.setAbnormalType(3);
										break;
									}
								}
                				sortedMap.put(startFormat.getService_name()+":"+"first", startNodInf);
                			}
                		}
            		}
                    //已经遍历完所有的4_1节点，并且没有生成serviceName:first主键，则证明没有对应的1。.表示无响应这种异常，异常类型为1
                    if(!sortedMap.containsKey(startFormat.getService_name()+":"+"first")) {
                    	EventLogNodInf nodInf = new EventLogNodInf();
        				ArrayList<String> requestNodeName = new ArrayList<>();
        				requestNodeName.add(startFormat.getService_name()+":"+startFormat.getRe_id());
        				requestNodeName.add(startFormat.getEndpoint_ip() + ":" + startFormat.getEndpoint_port());
        				ArrayList<LogFormat> listRequest = new ArrayList<>();
        				
        				listRequest.add(startFormat);
        				
        				LogFormat format2 = new LogFormat();
        				format2.setService_name(startFormat.getEndpoint_ip());
        				format2.setIsAbormal(1);
        				format2.setAbnormalType(1);
        				listRequest.add(format2);
//        				for(int i = 0; i < list_logType4And1.size(); i++) {
//        					LogFormat format3 = list_logType4And1.get(i);
//        					if(format3.getSpan_type() == 4 && format3.getParent_reid().equals(format2.getRe_id())) {
//        						listRequest.add(format3);
//        						requestNodeName.add(format3.getService_name()+":"+format3.getRe_id());
//        					}
//        				}
        				ArrayList<String> responseNodeName = new ArrayList<>();
        				ArrayList<LogFormat> listResponse = new ArrayList<>();
        				//String abnorInfo = "";
        				nodInf.setCurServiceLogID(startFormat.getService_name()+":"+"first");
        				nodInf.setGid(startFormat.getGlobal_id());
        				nodInf.setRequestNodeName(requestNodeName);
        				nodInf.setListRequest(listRequest);
        				nodInf.setResponseNodeName(responseNodeName);
        				nodInf.setListResponse(listResponse);
        				//nodInf.setAbnorInfo(abnorInfo);
        				nodInf.setStartServiceName(startFormat.getService_name());
        				nodInf.setStartServiceTime(startFormat.getSpan_timestamp());
        				sortedMap.put(startFormat.getService_name()+":"+"first", nodInf);
                    }
            	}
            	
    		}
			/**
			 * 2.遍历剩下4_1类型节点，找到除首节点外的其他初始4节点，并找到其对应的1节点
			 */
            iterator = list_logType4And1.iterator();
            while(iterator.hasNext()){
    			//如果存在，则调用next实现迭代
    			//Object-->Integer-->int
            	format = iterator.next();  //把Object型强转成int型
            	//跳过第一个节点
            	if(format.getParent_reid() == null || format.getParent_reid().length() == 0) continue;
            	String reID = "";
            	if(!sortedMap.containsKey(format.getService_name() + ":" + format.getRe_id()) && format.getSpan_type() == 4) {
            		reID = format.getRe_id();
            		for(int i = 0; i < list_logType4And1.size(); i++) {
                		LogFormat format2 = list_logType4And1.get(i);
            			if(format2.getSpan_type() == 1 && format2.getParent_reid().equals(reID)) {
            				if(!sortedMap.containsKey(format.getService_name()+":"+format.getRe_id())) {
                				EventLogNodInf nodInf = new EventLogNodInf();
                				ArrayList<String> requestNodeName = new ArrayList<>();
                				requestNodeName.add(format.getService_name()+":"+format.getRe_id());
                				requestNodeName.add(format2.getService_name()+":"+format2.getRe_id());
                				ArrayList<LogFormat> listRequest = new ArrayList<>();
                				listRequest.add(format);
                				listRequest.add(format2);
//                				for(int i1 = 0; i1 < list_logType4And1.size(); i1++) {
//                					LogFormat format3 = list_logType4And1.get(i1);
//                					if(format3.getSpan_type() == 4 && format3.getParent_reid().equals(format2.getRe_id())) {
//                						listRequest.add(format3);
//                						requestNodeName.add(format3.getService_name()+":"+format3.getRe_id());
//                					}
//                				}
                				ArrayList<String> responseNodeName = new ArrayList<>();
                				ArrayList<LogFormat> listResponse = new ArrayList<>();
                				nodInf.setCurServiceLogID(format.getService_name() + ":" + format.getRe_id());
                				nodInf.setGid(format.getGlobal_id());
                				nodInf.setRequestNodeName(requestNodeName);
                				nodInf.setListRequest(listRequest);
                				nodInf.setResponseNodeName(responseNodeName);
                				nodInf.setListResponse(listResponse);
                				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                			}
                			else {
                				EventLogNodInf nodInf = sortedMap.get(format.getService_name()+":"+format.getRe_id());
                				ArrayList<String> requestNodeName = nodInf.getRequestNodeName();
                				requestNodeName.add(format.getService_name()+":"+format.getRe_id());
                				requestNodeName.add(format2.getService_name()+":"+format2.getRe_id());
                				ArrayList<LogFormat> listRequest = nodInf.getListRequest();
                				listRequest.add(format);
                				listRequest.add(format2);
                				nodInf.setCurServiceLogID(format.getService_name()+":"+format.getRe_id());
                				nodInf.setGid(format.getGlobal_id());
                				nodInf.setRequestNodeName(requestNodeName);
                				nodInf.setListRequest(listRequest);
                				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                			}

							Iterator<LogFormat> iterator3 = list_logType8And5.iterator();
                			boolean findResponseLog5 = false;
                			EventLogNodInf nodInf = sortedMap.get(format.getService_name() + ":" + format.getRe_id());
                			while(iterator3.hasNext()){
                				LogFormat formatType5 = iterator3.next();
								/**
								 * 表示有请求日志4，1；且有响应日志5，8
								 */
								if(formatType5.getSpan_type() == 5 && formatType5.getRe_id().equals(reID)) {
                        			findResponseLog5 = true;
                        			long responseTime = formatType5.getSpan_timestamp() - format.getSpan_timestamp();
									/**
									 * 判断是否超时，即responseTime>15s 即为超时有响应异常，对应异常类型为2
									 */

                        			if(responseTime > 15000) {
                        				ArrayList<LogFormat> listRequest = nodInf.getListRequest();
                        				for (int i1 = 0; i1 < listRequest.size(); i1++) {
											LogFormat logFormat = listRequest.get(i1);
											if(logFormat.getSpan_type() == 1 && logFormat.getParent_reid().equals(reID)) {
												logFormat.setIsAbormal(1);
												logFormat.setAbnormalType(2);
												logFormat.setResponseTime(responseTime);
												break;
											}
										}
                        				//nodInf.setListRequest(listRequest);
                        				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                        			}
									/**
									 * 表示可以找到对应4的5，而且响应未超时
									 */

                        			else {
                        				ArrayList<LogFormat> listRequest = nodInf.getListRequest();
                        				for (int i1 = 0; i1 < listRequest.size(); i1++) {
											LogFormat logFormat = listRequest.get(i1);
											if(logFormat.getSpan_type() == 1 && logFormat.getParent_reid().equals(reID)) {
												logFormat.setIsAbormal(0);
												logFormat.setResponseTime(responseTime);
												break;
											}
										}
                        				//nodInf.setListRequest(listRequest);
                        				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                        			}
                        			break;
                        		}
                			}
							/**
							 * 对应无响应超时该种异常，异常类型为3
							 */

							if(findResponseLog5 == false) {
                				
                				ArrayList<LogFormat> listRequest = nodInf.getListRequest();
//                				LogFormat format3 = new LogFormat();
//                				format3.setIsAbormal(1);
//                				format3.setAbnormalType(3);
//                				listRequest.add(format3);
                				for (int i1 = 0; i1 < listRequest.size(); i1++) {
									LogFormat logFormat = listRequest.get(i1);
									if(logFormat.getSpan_type() == 1 && logFormat.getParent_reid().equals(reID)) {
										logFormat.setIsAbormal(1);
										logFormat.setAbnormalType(3);
										break;
									}
								}
                				
                				//nodInf.setListRequest(listRequest);
                				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                			}
            			}
                	}
					/**
					 * 已经遍历完所有的4_1节点，并且没有生成serviceName:reid主键，则证明没有对应的1。.表示无响应这种异常，异常类型为1
					 */

                    if(!sortedMap.containsKey(format.getService_name()+":"+format.getRe_id())) {
                    	EventLogNodInf nodInf = new EventLogNodInf();
        				ArrayList<String> requestNodeName = new ArrayList<>();
        				requestNodeName.add(format.getService_name()+":"+format.getRe_id());
        				requestNodeName.add(format.getEndpoint_ip() + ":" + format.getEndpoint_port());
        				ArrayList<LogFormat> listRequest = new ArrayList<>();
        				
        				listRequest.add(format);
        				LogFormat format2 = new LogFormat();
        				format2.setService_name(format.getEndpoint_ip());
        				format2.setIsAbormal(1);
        				format2.setAbnormalType(1);
        				listRequest.add(format2);
//        				for(int i = 0; i < list_logType4And1.size(); i++) {
//        					LogFormat format3 = list_logType4And1.get(i);
//        					if(format3.getSpan_type() == 4 && format3.getParent_reid().equals(format2.getRe_id())) {
//        						listRequest.add(format3);
//        						requestNodeName.add(format3.getService_name()+":"+format3.getRe_id());
//        					}
//        				}
        				ArrayList<String> responseNodeName = new ArrayList<>();
        				ArrayList<LogFormat> listResponse = new ArrayList<>();
        				//String abnorInfo = "";
        				nodInf.setCurServiceLogID(format.getService_name()+":"+format.getRe_id());
        				nodInf.setGid(format.getGlobal_id());
        				nodInf.setRequestNodeName(requestNodeName);
        				nodInf.setListRequest(listRequest);
        				nodInf.setResponseNodeName(responseNodeName);
        				nodInf.setListResponse(listResponse);
        				//nodInf.setAbnorInfo(abnorInfo);
        				nodInf.setStartServiceName(format.getService_name());
        				nodInf.setStartServiceTime(format.getSpan_timestamp());
        				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                    }
            		
            	}
            	
            	
    		}
			/**
			 * 找链路最后一个节点
			 */
			iterator = list_logType4And1.iterator();
            while(iterator.hasNext()){
            	format = iterator.next();
            	if(format.getSpan_type() == 1) {
            		LogFormat curFormat = format;
            		String currID = curFormat.getRe_id();
            		LogFormat format2 = null;
            		boolean canFind = false;
            		for(int i = 0; i < list_logType4And1.size(); i++) {
            			format2 = list_logType4And1.get(i);
            			if(format2.getSpan_type() == 4 && format2.getParent_reid() != null && format2.getParent_reid().equals(currID)) {
            				canFind = true;
            				break;
            			}
            		}
            		if(canFind == false) {
            			if(!sortedMap.containsKey(curFormat.getService_name()+":"+curFormat.getRe_id())) {
            				EventLogNodInf nodInf = new EventLogNodInf();
            				ArrayList<String> requestNodeName = new ArrayList<>();
            				requestNodeName.add(curFormat.getService_name()+":"+curFormat.getRe_id());
            				//requestNodeName.add(curFormat.getService_name()+":"+curFormat.getRe_id());
            				ArrayList<LogFormat> listRequest = new ArrayList<>();
            				listRequest.add(curFormat);
            				ArrayList<String> responseNodeName = new ArrayList<>();
            				ArrayList<LogFormat> listResponse = new ArrayList<>();
            				//String abnorInfo = "";
            				nodInf.setCurServiceLogID(format.getService_name()+":"+format.getRe_id());
            				nodInf.setGid(format.getGlobal_id());
            				nodInf.setRequestNodeName(requestNodeName);
            				nodInf.setListRequest(listRequest);
            				nodInf.setResponseNodeName(responseNodeName);
            				nodInf.setListResponse(listResponse);
            				//nodInf.setAbnorInfo(abnorInfo);
            				sortedMap.put(curFormat.getService_name()+":"+curFormat.getRe_id(), nodInf);
            			}
            			else {
            				EventLogNodInf nodInf = sortedMap.get(curFormat.getService_name()+":"+curFormat.getRe_id());
            				ArrayList<String> requestNodeName = new ArrayList<>(nodInf.getRequestNodeName());
            				//requestNodeName.add(format2.getService_name()+":"+format2.getRe_id());
            				ArrayList<LogFormat> listRequest = new ArrayList<>(nodInf.getListRequest());
            				//listRequest.add(format2);
            				nodInf.setCurServiceLogID(format.getService_name()+":"+format.getRe_id());
            				nodInf.setGid(format.getGlobal_id());
            				nodInf.setRequestNodeName(requestNodeName);
            				nodInf.setListRequest(listRequest);
            				sortedMap.put(curFormat.getService_name()+":"+curFormat.getRe_id(), nodInf);
            			}
            		}
            	}
            }
            
            //3.遍历5_8节点
            //3.1找到第一个响应节点，节点类型为8，且parent_id为空
            startFormat = null;
            reIDList = new ArrayList<>();
            iterator = list_logType8And5.iterator();
            while(iterator.hasNext()){
    			//如果存在，则调用next实现迭代
    			//Object-->Integer-->int
            	format = iterator.next();  //把Object型强转成int型
            	if(format.getSpan_type() == 8 && (format.getParent_reid() == null || format.getParent_reid().equals(""))) {
            		String reID = format.getRe_id();
            		reIDList.add(reID);
            		//startFormat = format;
            		
            		Iterator<LogFormat> iterator2 = list_logType8And5.iterator();
                    while(iterator2.hasNext()){
                    	LogFormat format2 = iterator2.next();
                		if(format2.getSpan_type() == 5 && format2.getParent_reid().equals(format.getRe_id())) {
                			//异常处理
                			if(!sortedMap.containsKey(format.getService_name()+":"+ format.getRe_id())) {
//                				EventLogNodInf nodInf = new EventLogNodInf();
//                				ArrayList<String> requestNodeName = new ArrayList<>();
//                				requestNodeName.add(format2.getService_name()+":"+format2.getRe_id());
//                				ArrayList<LogFormat> listRequest = new ArrayList<>();
//                				listRequest.add(format2);
//                				ArrayList<String> responseNodeName = new ArrayList<>();
//                				ArrayList<LogFormat> listResponse = new ArrayList<>();
//                				String abnorInfo = "";
//                				nodInf.setRequestNodeName(requestNodeName);
//                				nodInf.setListRequest(listRequest);
//                				nodInf.setResponseNodeName(responseNodeName);
//                				nodInf.setListResponse(listResponse);
//                				nodInf.setAbnorInfo(abnorInfo);
//                				sortedMap.put(startFormat.getService_name()+":"+"first", nodInf);
                			}
                			else {
                				EventLogNodInf nodInf = sortedMap.get(format.getService_name()+":"+format.getRe_id());
                				ArrayList<String> responseNodeName = new ArrayList<>(nodInf.getResponseNodeName());
                				responseNodeName.add(format.getService_name()+":"+format.getRe_id());
                				responseNodeName.add(format2.getService_name()+":"+format2.getRe_id());
                				ArrayList<LogFormat> listResponse = new ArrayList<>(nodInf.getListResponse());
                				listResponse.add(format);
                				listResponse.add(format2);
                				nodInf.setGid(format.getGlobal_id());
                				nodInf.setResponseNodeName(responseNodeName);
                				nodInf.setListResponse(listResponse);
                				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                			}
                		}
            		}
            	}
    		}
            //遍历剩下的8_5节点
            iterator = list_logType8And5.iterator();
            while(iterator.hasNext()){
    			//如果存在，则调用next实现迭代
    			//Object-->Integer-->int
            	format = iterator.next();  //把Object型强转成int型
            	String reID = "";
            	String service_name = format.getService_name();
            	String key = service_name + ":" +format.getParent_reid();
            	Integer type = format.getSpan_type();
            	boolean flag = false;
            	for (Entry<String, EventLogNodInf> entry1 : sortedMap.entrySet()) {
					if(entry1.getKey().equals(key)) {
						flag = true;
						break;
					}
				}
            	
            	if(type == 8 && flag) {
            		reID = format.getRe_id();
            		for(int i = 0; i < list_logType8And5.size(); i++) {
                		LogFormat format2 = list_logType8And5.get(i);
            			if(format2.getSpan_type() == 5 && format2.getParent_reid().equals(reID)) {
            				//异常
            				if(!sortedMap.containsKey(format.getService_name()+":"+format.getParent_reid())) {
//                				EventLogNodInf nodInf = new EventLogNodInf();
//                				ArrayList<String> requestNodeName = new ArrayList<>();
//                				//requestNodeName.add(format2.getService_name()+":"+format2.getRe_id());
//                				ArrayList<LogFormat> listRequest = new ArrayList<>();
//                				ArrayList<String> responseNodeName = new ArrayList<>();
//                				responseNodeName.add(format2.getService_name()+":"+format2.getRe_id());
//                				ArrayList<LogFormat> listResponse = new ArrayList<>();
//                				listResponse.add(format2);
//                				String abnorInfo = "";
//                				nodInf.setRequestNodeName(requestNodeName);
//                				nodInf.setListRequest(listRequest);
//                				nodInf.setResponseNodeName(responseNodeName);
//                				nodInf.setListResponse(listResponse);
//                				nodInf.setAbnorInfo(abnorInfo);
//                				sortedMap.put(format.getService_name()+":"+format.getRe_id(), nodInf);
                			}
                			else {
                				EventLogNodInf nodInf = sortedMap.get(format.getService_name()+":"+format.getParent_reid());
                				ArrayList<String> responseNodeName = new ArrayList<>(nodInf.getResponseNodeName());
                				responseNodeName.add(format.getService_name()+":"+format.getRe_id());
                				responseNodeName.add(format2.getService_name()+":"+format2.getRe_id());
                				ArrayList<LogFormat> listResponse = new ArrayList<>(nodInf.getListResponse());
                				listResponse.add(format);
                				listResponse.add(format2);
                				nodInf.setGid(format.getGlobal_id());
                				nodInf.setResponseNodeName(responseNodeName);
                				nodInf.setListResponse(listResponse);
                				sortedMap.put(format.getService_name()+":"+format.getParent_reid(), nodInf);
                			}
            			}
                	}
            	}
    		}
//            SortedMap<String, EventLogNodInf> sortedMap = new TreeMap<>();
            SortedMap<String, EventLogNodInf> sortedMapResult = new TreeMap<>();
            for(Entry<String, EventLogNodInf> entryMap : sortedMap.entrySet()) {
            	String key = entryMap.getKey();
            	EventLogNodInf eventLogNodInf = entryMap.getValue();
            	String[] keyArray = key.split(":");
            	if(keyArray[1].equals("first")) {
            		if(!sortedMapResult.containsKey(key)) {
            			sortedMapResult.put(key, eventLogNodInf);
            		}else {
            			EventLogNodInf enventLogNodeResult = sortedMapResult.get(key);
            			//合并curServiceLogID
//            			String curServiceLogID = enventLogNodeResult.getCurServiceLogID();
//            			String []serviceLogIDArray = curServiceLogID.split(":");
//            			String curServiceLogIDResult = enventLogNodeResult.getCurServiceLogID() + ":" + serviceLogIDArray[1];
            			//合并requestNodeName
            			ArrayList<String> requestNodeNameResult = enventLogNodeResult.getRequestNodeName();
            			ArrayList<String> requestNodeName = eventLogNodInf.getRequestNodeName();
            			for(int i = 0; i < requestNodeName.size(); i++) {
            				requestNodeNameResult.add(requestNodeName.get(i));
            			}
            			//合并requestNodeList
            			ArrayList<LogFormat> requestNodeListResult = enventLogNodeResult.getListRequest();
            			ArrayList<LogFormat> requestNodeList = eventLogNodInf.getListRequest();
            			for(int i = 0; i < requestNodeList.size(); i++) {
            				requestNodeListResult.add(requestNodeList.get(i));
            			}
            			//合并responseNodeName
            			ArrayList<String> responseNodeNameResult = enventLogNodeResult.getRequestNodeName();
            			ArrayList<String> responseNodeName = eventLogNodInf.getResponseNodeName();
            			for(int i = 0; i < responseNodeName.size(); i++) {
            				responseNodeNameResult.add(responseNodeName.get(i));
            			}
            			//合并responseNodeList
            			ArrayList<LogFormat> responseNodeListResult = enventLogNodeResult.getListResponse();
            			ArrayList<LogFormat> responseNodeList = eventLogNodInf.getListResponse();
            			for(int i = 0; i < responseNodeList.size(); i++) {
            				responseNodeListResult.add(responseNodeList.get(i));
            			}
            			
            		}
            	}
            	else {
            		if(!sortedMapResult.containsKey(keyArray[0])) {
            			sortedMapResult.put(keyArray[0], eventLogNodInf);
            		}else {
            			EventLogNodInf enventLogNodeResult = sortedMapResult.get(keyArray[0]);
            			//合并requestNodeName
            			ArrayList<String> requestNodeNameResult = enventLogNodeResult.getRequestNodeName();
            			ArrayList<String> requestNodeName = eventLogNodInf.getRequestNodeName();
            			for(int i = 0; i < requestNodeName.size(); i++) {
            				requestNodeNameResult.add(requestNodeName.get(i));
            			}
            			//合并requestNodeList
            			ArrayList<LogFormat> requestNodeListResult = enventLogNodeResult.getListRequest();
            			ArrayList<LogFormat> requestNodeList = eventLogNodInf.getListRequest();
            			for(int i = 0; i < requestNodeList.size(); i++) {
            				requestNodeListResult.add(requestNodeList.get(i));
            			}
            			//合并responseNodeName
            			ArrayList<String> responseNodeNameResult = enventLogNodeResult.getResponseNodeName();
            			ArrayList<String> responseNodeName = eventLogNodInf.getResponseNodeName();
            			for(int i = 0; i < responseNodeName.size(); i++) {
            				responseNodeNameResult.add(responseNodeName.get(i));
            			}
            			//合并responseNodeList
            			ArrayList<LogFormat> responseNodeListResult = enventLogNodeResult.getListResponse();
            			ArrayList<LogFormat> responseNodeList = eventLogNodInf.getListResponse();
            			for(int i = 0; i < responseNodeList.size(); i++) {
            				responseNodeListResult.add(responseNodeList.get(i));
            			}
            		}
            	}
            }
            list.add(sortedMapResult);
        }
        return list;
    }
    /**
     * 持久化所有链路至数据库
     */
	@Override
	public void saveAllLinked2DB(List<SortedMap<String, EventLogNodInf>> list) throws Exception {
		// TODO Auto-generated method stub
		eventLogDao.saveAllLinked2DB(list);
	}
}
