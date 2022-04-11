package com.example.shippingbotserver.service;

import com.example.shippingbotserver.entity.User;
import com.example.shippingbotserver.service.interfaces.Translator;
import com.example.shippingbotserver.service.translation.TextTranslator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TranslateService implements Translator {
    private final TextTranslator textTranslator;

    public void translate(User user) {
        if (user.getGender() == null || user.getPreference() == null){
            return;
        }
        textTranslator.userTranslate(user);
    }
}
