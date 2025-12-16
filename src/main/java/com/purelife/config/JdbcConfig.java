package com.purelife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

@Configuration
@EnableJdbcRepositories(basePackages = "com.purelife.repository")
public class JdbcConfig {

    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                return property.getName(); 
            }
            
            @Override
            public String getTableName(Class<?> type) {
                return type.getSimpleName();
            }
        };
    }
}
