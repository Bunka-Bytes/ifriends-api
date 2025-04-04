package com.bunkabytes.ifriendsapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


@Configuration
@OpenAPIDefinition
public class SwaggerConfiguration implements WebMvcConfigurer{
	
	private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME = "bearer";
    
	
	@Bean
	public OpenAPI api() {
		return new OpenAPI()
				.info(getInfo())
				.components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, createSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
	}

	private Info getInfo() {
        return new Info()
                .title("IFriends")
                .description("Comunidade online do Instituto Federal de São Paulo com a missão de oferecer uma plataforma que quebre barreiras de comunicação e acolha os alunos no seu processo de exploração acadêmica.")
                .version("3.1.7")
                .license(getLicense());
    }
	
	private License getLicense() {
		return new License()
		        .name("Bunka Bytes")
		        .url("https://github.com/Bunka-Bytes");
	 }
	
	private SecurityScheme createSecurityScheme() {
	        return new SecurityScheme()
	                .name(SCHEME_NAME)
	                .type(SecurityScheme.Type.HTTP)
	                .scheme(SCHEME);
	 }

}

