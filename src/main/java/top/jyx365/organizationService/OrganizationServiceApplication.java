package top.jyx365.organizationService;





import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.hateoas.VndErrors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class OrganizationServiceApplication {

    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(-10000);
        return bean;
    }

    @Bean
    @ConfigurationProperties(prefix="ldap")
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    @Bean
    public OrganizationRepository repository() {
        return new OrganizationRepository();
    }

	public static void main(String[] args) {
		SpringApplication.run(OrganizationServiceApplication.class, args);
	}
}

@ControllerAdvice
@Slf4j
class OrganizationServiceContollerAdvice {


    @ResponseBody
    @ExceptionHandler(NameAlreadyBoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    VndErrors nameAlreadyBoundExceptionHandler(NameAlreadyBoundException ex) {
        log.debug("Caught exception while handling a request",ex);
        return new VndErrors(ex.getClass().getSimpleName(),ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(NameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors nameNotFoundExceptionHandler(NameNotFoundException ex) {
        log.debug("Caught exception while handling a request",ex);
        return new VndErrors(ex.getClass().getSimpleName(),ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(InvalidNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors invalidNameExceptionHandler(InvalidNameException ex) {
        log.debug("Caught exception while handling a request",ex);
        return new VndErrors(ex.getClass().getSimpleName(),ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors exceptionHandler(Exception ex) {
        log.info("Caught exception while handling a request",ex);
        return new VndErrors(ex.getClass().getSimpleName(),ex.getMessage());
    }
}


@EnableResourceServer
@Configuration
@EnableConfigurationProperties(AuthorityConfiguration.class)
class ResourceServerConfigurer extends ResourceServerConfigurerAdapter{
    @Autowired
    AuthorityConfiguration config;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/**").hasAnyAuthority(config.getAuthorities());
    }
}

@ConfigurationProperties(prefix="authority")
class AuthorityConfiguration {
    private String authorities;

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public String getAuthorities() {
        return authorities;
    }
}
