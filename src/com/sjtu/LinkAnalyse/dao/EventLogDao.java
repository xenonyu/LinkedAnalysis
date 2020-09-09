package com.sjtu.LinkAnalyse.dao;

import com.sjtu.LinkAnalyse.ObjectToJson.EventLogNodInf;
import com.sjtu.LinkAnalyse.ObjectToJson.LogFormat;

import java.util.List;
import java.util.SortedMap;

public interface EventLogDao {
    /**
     * 查询时间段内的所有记录
     */
    public List<LogFormat> findRecordWithID(long startID) throws Exception;

    public List<LogFormat> findRecordWithGlobalID(String globalID) throws Exception;

    /**
     * 将所有链路缓存到数据库
     * @param list
     * @throws Exception
     */
    public void saveAllLinked2DB(List<SortedMap<String, EventLogNodInf>> list) throws Exception;

    /**
     * 判断数据库linked_record表中是否存在global_id
     * @param global_id
     * @param delete
     * @throws Exception
     * @return
     */
    public boolean checkExistGlobalID(String global_id, boolean delete) throws Exception;

    /**
     * 判断数据库linked_record表中是否存在global_id
     * @param global_id
     * @return
     * @throws Exception
     */
    public boolean checkExistGlobalID(String global_id) throws Exception;


}
