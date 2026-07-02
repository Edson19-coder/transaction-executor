package com.spin.transaction_executor.config;

import com.spin.transaction_executor.util.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    final String securitySchemeName = "ApiKeyAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Ejecución de Transacciones")
                        .version("1.0.0")
                        .description("Servicio encargado de validar, procesar y registrar transacciones.")
                        .contact(new Contact().name("Soporte Técnico").email("edson.lugo.sanchez@gmail.com")))
                        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                        .components(new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(Constants.API_KEY_HEADER)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .description("Introduce el API Key en este campo para autenticar las peticiones."))
                        );
    }
}