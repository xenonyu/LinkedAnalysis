package com.sjtu.LinkAnalyse.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;

public class test {
    public static void main(String[] args) throws Exception{
        ComboPooledDataSource pool = new ComboPooledDataSource("demo");
        pool.getConnection();
        System.out.println(pool.getPassword());

    }

}
