package com.ngcloudapps.myapp.config;


import com.ngcloudapps.myapp.security.AjaxLogoutSuccessHandler;
import com.ngcloudapps.myapp.security.AuthoritiesConstants;
import com.ngcloudapps.myapp.security.Http401UnauthorizedEntryPoint;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
public class OAuth2ServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Inject
        private Http401UnauthorizedEntryPoint authenticationEntryPoint;

        @Inject
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
            .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .and()
                .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
                .disable()
                .headers()
                .frameOptions().disable()
                .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/logs/**").hasAnyAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/websocket/**").permitAll()
                .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/configprops/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/protected/**").authenticated();

        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter implements EnvironmentAware {

    	 private static final String ENV_OAUTH = "authentication.clients.";
         
         private static final String OAUTH_CLIENTID = "oauth.clientid";
         private static final String OAUTH_SECRET = "oauth.secret";
         private static final String OAUTH_TOKEN_VALIDITY_SECONDS = "oauth.tokenValidityInSeconds";        
         
         private static final String WEB_CLIENTID = "web.clientid";
         private static final String WEB_SECRET = "web.secret";
         private static final String WEB_TOKEN_VALIDITY_SECONDS = "web.tokenValidityInSeconds";
         
         private static final String MOBILE_CLIENTID = "mobile.clientid";
         private static final String MOBILE_SECRET = "mobile.secret";
         private static final String MOBILE_TOKEN_VALIDITY_SECONDS = "mobile.tokenValidityInSeconds";        
         
         private static final String RESOURCEPROVIDER_CLIENTID = "resourceprovider.clientid";
         private static final String RESOURCEPROVIDER_SECRET = "resourceprovider.secret";
         private static final String RESOURCEPROVIDER_TOKEN_VALIDITY_SECONDS = "resourceprovider.tokenValidityInSeconds";        


        private RelaxedPropertyResolver propertyResolver;

        @Inject
        private DataSource dataSource;

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Inject
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {

            endpoints
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
          //   clients
          //      .inMemory()
          //      .withClient(propertyResolver.getProperty(PROP_CLIENTID))
          //      .scopes("read", "write")
          //      .authorities(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
          //      .authorizedGrantTypes("password", "refresh_token")
          //      .secret(propertyResolver.getProperty(PROP_SECRET))
          //      .accessTokenValiditySeconds(propertyResolver.getProperty(PROP_TOKEN_VALIDITY_SECONDS, Integer.class, 1800));
       
            clients.inMemory()
        	.withClient(propertyResolver.getProperty(OAUTH_CLIENTID))
        		.resourceIds("cloud-oauth")
        		.scopes("read", "write","delete")
        		.autoApprove(true)
        		.authorities("ROLE_ADMIN")
        		.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
        		.secret(propertyResolver.getProperty(OAUTH_SECRET))
        		.accessTokenValiditySeconds(propertyResolver.getProperty(OAUTH_TOKEN_VALIDITY_SECONDS, Integer.class, 1800))
        .and()
        	.withClient(propertyResolver.getProperty(MOBILE_CLIENTID))
        		.resourceIds("cloud-mobile")
        		.scopes("read", "write" 
        				)
        		.autoApprove(true)
        		.authorities("ROLE_USER")
        		//.authorizedGrantTypes("password")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .secret(propertyResolver.getProperty(MOBILE_SECRET))
        		.accessTokenValiditySeconds(propertyResolver.getProperty(MOBILE_TOKEN_VALIDITY_SECONDS, Integer.class, 1800))
        .and()
        	.withClient(propertyResolver.getProperty(RESOURCEPROVIDER_CLIENTID))
        		.resourceIds("cloud-web")
        		.scopes("read", "write" 
        				)
        		.autoApprove(true)
        		.authorities("ROLE_USER")
        		.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .secret(propertyResolver.getProperty(RESOURCEPROVIDER_SECRET))
        		.accessTokenValiditySeconds(propertyResolver.getProperty(RESOURCEPROVIDER_TOKEN_VALIDITY_SECONDS, Integer.class, 1800))
        .and()
        	.withClient(propertyResolver.getProperty(WEB_CLIENTID))
        		.resourceIds("cloud-web")
        		.scopes("read", "write","delete")
        		.autoApprove(true)
        		.authorities("ROLE_USER")
        		//.authorizedGrantTypes("password")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
            	.secret(propertyResolver.getProperty(WEB_SECRET))
        		.accessTokenValiditySeconds(propertyResolver.getProperty(WEB_TOKEN_VALIDITY_SECONDS, Integer.class, 1800));
        
            
        
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_OAUTH);
        }
    }
}
