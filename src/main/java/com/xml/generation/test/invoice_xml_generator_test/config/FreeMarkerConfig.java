package com.xml.generation.test.invoice_xml_generator_test.config;

import freemarker.template.Template;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FreeMarkerConfig {

    @Bean
    public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
        factory.setTemplateLoaderPath("classpath:/invoice/");
        factory.setDefaultEncoding("UTF-8");
        return factory;
    }

    @Bean
    public Template freemarkerTemplate(
            FreeMarkerConfigurationFactoryBean freemarkerConfiguration) throws Exception {
        return freemarkerConfiguration
                .getObject()
                .getTemplate("invoice_2.0_.ftlh");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}