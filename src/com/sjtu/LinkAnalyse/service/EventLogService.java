package com.sjtu.LinkAnalyse.service;

import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LinkedLogFormat;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

public interface EventLogService {
    /**
     * 查询时间段内的所有记录
     */
    public List<LogFormat> findRecordWithPeriod(long startTime, long endTime) throws Exception;

    /**
     * 查询第一条记录的时间
     */
    public Long findFirstRecordTime() throws Exception;

    /**
     * 将链路上node串联
     * @return
     */
    public List<SortedMap<String, EventLogNodInf>> generateLinkedLogFormat(HashMap<String, ArrayList<LogFormat>> map);
    /**
     * 将所有链路缓存到数据库
     * @param linkedLogList
     * @throws Exception
     */
    public void saveAllLinked2DB(List<SortedMap<String, EventLogNodInf>> linkedLogList) throws Exception;
}
