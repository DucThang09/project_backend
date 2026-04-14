package com.luvina.la.config;
/**
 * Copyright(C) 2026 Luvina Software Company
 * <p>
 * sample.java, April 13, 2026 tdthang
 */
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageConstants {
    private final MessageSource messageSource;

    public MessageConstants(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Lấy message theo mã.
     *
     * @param code Mã message.
     * @param params Tham số truyền vào message.
     * @return Nội dung message đã được format.
     */
    public String get(String code, Object... params) {
        return messageSource.getMessage(code, params, Locale.JAPAN);
    }
}
