package com.acgist.mssql;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 多数据源配置
 * 
 * @author acgist
 */
@Configuration
@EnableConfigurationProperties(MssqlServiceProperties.class)
@ConditionalOnExpression(value = "${acgist.mssql.enabled:true}")
// 保证默认配置
@Import({DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
// 配置template不用配置factory
@MapperScan(basePackages = "com.acgist.mssql.mapper", sqlSessionTemplateRef = "mssqlSqlSessionTemplate")
public class MssqlApplicationConfiguration {

	@Resource
	private MssqlServiceProperties mssqlServiceProperties;

	@Bean("mssqlDataSource")
	public DataSource mssqlDataSource() {
		final HikariDataSource dataSource;
		if(this.mssqlServiceProperties.getDatasource() == null) {
			dataSource = new HikariDataSource();
			dataSource.setJdbcUrl(this.mssqlServiceProperties.getUrl());
			dataSource.setDriverClassName(this.mssqlServiceProperties.getDriverClassName());
			dataSource.setUsername(this.mssqlServiceProperties.getUsername());
			dataSource.setPassword(this.mssqlServiceProperties.getPassword());
		} else {
			dataSource = new HikariDataSource(this.mssqlServiceProperties.getDatasource());
		}
		return dataSource;
	}

	@Bean(name = "mssqlTransactionManager")
	public DataSourceTransactionManager mssqlTransactionManager(@Qualifier("mssqlDataSource") DataSource mssqlDataSource) {
		return new DataSourceTransactionManager(mssqlDataSource);
	}

	@Bean(name = "mssqlSqlSessionFactory")
	public SqlSessionFactory mssqlSqlSessionFactory(@Qualifier("mssqlDataSource") DataSource mssqlDataSource) throws Exception {
		final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
		sessionFactory.setDataSource(mssqlDataSource);
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mybatis/mssql/mapper/*.xml"));
		return sessionFactory.getObject();
	}
	
    @Bean(name = "mssqlSqlSessionTemplate")
    public SqlSessionTemplate hyxtSqlSessionTemplate(@Qualifier("mssqlSqlSessionFactory") SqlSessionFactory mssqlSqlSessionFactory) {
    	return new SqlSessionTemplate(mssqlSqlSessionFactory);
    }

}
