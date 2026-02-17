package com.drycleaning.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing database tables...");
        
        ClassPathResource resource = new ClassPathResource("db/init.sql");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                
                // 跳过注释和空行
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                
                sqlBuilder.append(line).append("\n");
                
                // 如果语句以分号结尾，执行它
                if (trimmed.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    // 移除末尾的分号
                    sql = sql.substring(0, sql.length() - 1);
                    
                    try {
                        jdbcTemplate.execute(sql);
                        logger.info("Executed: {}", sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        // 如果表已存在，忽略错误
                        if (e.getMessage().contains("already exists")) {
                            logger.debug("Table already exists, skipping: {}", sql.substring(0, Math.min(50, sql.length())));
                        } else {
                            logger.error("Error executing SQL: {}. Error: {}", sql, e.getMessage());
                        }
                    }
                    
                    sqlBuilder.setLength(0);
                }
            }
        }
        
        logger.info("Database initialization completed.");
    }
}
