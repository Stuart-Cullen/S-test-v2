package com.stuartcullen.Stockopediatestv2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * Stuart Cullen - 2021-02-14
 *
 * The application entry point
 */
@SpringBootApplication
public class StockopediaTestV2Application {

	/**
	 * The standard environment variable
	 */
	@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection") //that's ok for this assignment
	@Autowired
	Environment env;


	/**
	 * @return A datasource based on SQLite
	 */
	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("driverClassName")));
		dataSource.setUrl(env.getProperty("url"));
		return dataSource;
	}


	/**
	 * The main method of the entry point
	 */
	public static void main(String[] args) {
		SpringApplication.run(StockopediaTestV2Application.class, args);
	}

}
