package de.mz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Session;

@Configuration
public class AppBeans {

	@Bean
	public Session session() {
		return sessionManager().getSession();
	}

	@Bean
	public CassandraSessionManager sessionManager() {
		return new CassandraSessionManager();
	}
}