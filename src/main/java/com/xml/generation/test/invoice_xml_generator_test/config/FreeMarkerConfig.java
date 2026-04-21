package com.xml.generation.test.invoice_xml_generator_test.config;

import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class FreeMarkerConfig {

    @Value("${xml-gen.is-staging}")
    private boolean isStaging;

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
        String template = isStaging ? "invoice_2.0_.ftlh" : "invoice.ftlh";
        return freemarkerConfiguration
                .getObject()
                .getTemplate(template);
    }
}