package com.example.shippingbotserver.configs;

import com.example.shippingbotserver.service.DrawerService;
import com.example.shippingbotserver.service.DtoService;
import com.example.shippingbotserver.service.TranslateService;
import com.example.shippingbotserver.service.interfaces.Drawer;
import com.example.shippingbotserver.service.interfaces.DtoConverter;
import com.example.shippingbotserver.service.interfaces.Translator;
import com.example.shippingbotserver.service.translation.TextTranslator;
import com.example.shippingbotserver.view.TextEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    Translator getTranslator() {
        return new TranslateService(new TextTranslator());
    }

    @Bean
    DtoConverter getDtoConverter() {
        return new DtoService();
    }

    @Bean
    Drawer getDrawer() {
        return new DrawerService(new TextEditor());
    }
}
