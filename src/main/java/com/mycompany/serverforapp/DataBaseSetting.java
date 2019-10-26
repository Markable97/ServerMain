/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.mycompany.serverforapp;

import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @author march
 */
public class DataBaseSetting {
    
    public static DataBaseSetting ds;
    
    private final String url = "jdbc:mysql://localhost:3306/football_main_work"
            + "?useSSL=false"
            + "&dataSource.cachePrepStmts=true" +
              "&dataSource.prepStmtCacheSize=250" +
              "&dataSource.prepStmtCacheSqlLimit=2048" +
              "&dataSource.useServerPrepStmts=true" +
              "&dataSource.useLocalSessionState=true" +
              "&dataSource.rewriteBatchedStatements=true" +
              "&dataSource.cacheResultSetMetadata=true" +
              "&dataSource.cacheServerConfiguration=true" +
              "&dataSource.elideSetAutoCommits=true" +
              "&dataSource.maintainTimeStats=false";
    private final String user = "root";
    private final String password = "7913194";
    //private final String password = "Dan-dg7913194";
    public HikariDataSource dataSource;
    
    private DataBaseSetting(){
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }
    
    public static DataBaseSetting getInstance(){
        if(ds == null){
            ds = new DataBaseSetting();
        }
        return ds;
    }
    
}
