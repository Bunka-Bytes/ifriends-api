package com.bunkabytes.ifriendsapi.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bunkabytes.ifriendsapi.api.JwtTokenFilter;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.impl.SecurityUserDetailsService;

@EnableWebSecurity
@EnableWebMvc
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private SecurityUserDetailsService userDetailsService;

	@Autowired
	private JwtService jwtService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}

	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter(jwtService, userDetailsService);
	}

	private static final String[] SWAGGER_WHITELIST = { "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
			"/api-docs", };
	
	private static final String[] GET_ABERTOS = { 
			"/api/respostas", 
			"/api/respostas/**", 
			"/api/perguntas", 
			"/api/perguntas/**",  
			"/api/categorias/**",
			"/api/categorias",
			"/api/cursos",
			"/api/dominios",
			"/api/motivosReport",
			"/api/eventos",
			"/api/eventos/**",
			"/api/usuarios"};
	
	private static final String[] POST_ABERTOS = { 
			"/api/usuarios",
			"/api/usuarios/autenticar",
			"/api/usuarios/email/{codigo}/confirmacao"
			};

	String REPORT_TO = "{\"group\":\"csp-violation-report\",\"max_age\":2592000,\"endpoints\":[{\"url\":\"https://ifriends-api.herokuapp.com/report\"}]}";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.csrf().disable().authorizeRequests()
				.antMatchers(SWAGGER_WHITELIST).permitAll()
				.antMatchers(HttpMethod.GET, "/api/perguntas/reportadas").hasRole("ADMIN")
				.antMatchers(HttpMethod.GET, GET_ABERTOS).permitAll()
				.antMatchers(HttpMethod.POST, POST_ABERTOS).permitAll()
				.antMatchers(HttpMethod.PATCH, "/api/usuarios/{id}/banir").hasRole("ADMIN")
				.anyRequest().hasAnyRole("USER", "ADMIN")
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
		        .headers().permissionsPolicy().policy("geolocation=(self)").and()
		        .referrerPolicy().and()
		        .addHeaderWriter(new StaticHeadersWriter("Report-To", REPORT_TO))
				.xssProtection().and()
				.contentSecurityPolicy("form-action 'self'; report-uri /report; report-to csp-violation-report");
	}
 
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {

		List<String> all = Arrays.asList("*");
 
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedMethods(all);
		config.setAllowedOriginPatterns(all);
		config.setAllowedHeaders(all);
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		CorsFilter corFilter = new CorsFilter(source);

		FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<CorsFilter>(corFilter);

		filter.setOrder(Ordered.HIGHEST_PRECEDENCE);

		return filter;
	}
}
