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
    public List<LogFormat> findRecordWithID(long startID) throws Exception;

    public List<LogFormat> findRecordWithGlobalID(String globalID) throws Exception;

    /**
     * 判断数据库linked_record表中是否存在global_id
     * @param global_id
     * @throws Exception
     * @return
     */
    public boolean checkExistGlobalID(String global_id) throws Exception;

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
