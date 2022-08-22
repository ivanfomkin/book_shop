//package com.github.ivanfomkin.bookshop.config;
//
//import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
//import org.apache.mahout.cf.taste.model.DataModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class MahoutConfig {
//    private final DataSource dataSource;
//
//    public MahoutConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @Bean
//    public DataModel dataModel() {
//        return new PostgreSQLJDBCDataModel(dataSource);
//    }
//}
