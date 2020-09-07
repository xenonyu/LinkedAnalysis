package com.sjtu.LinkAnalyse.dao;

import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LinkedLogFormat;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

public interface EventLogDao {
    /**
     * 查询时间段内的所有记录
     */
    public List<LogFormat> findRecordWithPeriod(long startTime, long endTime) throws Exception;

    /**
     * 查询第一条记录的时间
     */
    public Long findFirstRecordTime() throws Exception;
    /**
     * 将所有链路缓存到数据库
     * @param list
     * @throws Exception
     */
    public void saveAllLinked2DB(List<SortedMap<String, EventLogNodInf>> list) throws Exception;

}
