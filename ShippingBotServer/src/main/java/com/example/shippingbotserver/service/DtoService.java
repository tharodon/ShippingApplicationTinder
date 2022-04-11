package com.example.shippingbotserver.service;

import com.example.shippingbotserver.dto.UserDto;
import com.example.shippingbotserver.entity.User;
import com.example.shippingbotserver.service.interfaces.DtoConverter;
import lombok.Getter;

public class DtoService implements DtoConverter {
    public UserDto convert(byte[] image, User user, String status) {
        return new UserDto(image, user, status);
    }

    public UserDto convertEmpty(byte[] image) {
        return new UserDto(image, null, "");
    }
}
