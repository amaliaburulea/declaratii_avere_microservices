package com.declaratiiavere.iam.user;

import com.declaratiiavere.common.auth.AuthenticationInterceptor;
import com.declaratiiavere.restclient.RestClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication(scanBasePackages = "com.declaratiiavere")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableSwagger2
@EntityScan(value = "com.declaratiiavere")
public class IdentityAccessManagementApplication extends WebMvcConfigurerAdapter {
    @Autowired
    private PasswordEncoder standardPasswordEncoder;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(IdentityAccessManagementApplication.class, args);
    }

    @Bean
    public DataSource dataSource() throws IOException {
        Properties dsProps = PropertiesLoaderUtils.loadAllProperties("datasource.properties");
        Properties hikariProps = PropertiesLoaderUtils.loadAllProperties("hikari.properties");
        hikariProps.put("dataSourceProperties", dsProps);
        return new HikariDataSource(new HikariConfig(hikariProps));
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws IOException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public StandardPasswordEncoder standardPasswordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    @Profile("test")
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.declaratiiavere.iam"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    @Profile("production")
    public Docket blockedApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**");;
    }

    @Bean
    public RestClient restClient() {
        return new RestClient();
    }
}
