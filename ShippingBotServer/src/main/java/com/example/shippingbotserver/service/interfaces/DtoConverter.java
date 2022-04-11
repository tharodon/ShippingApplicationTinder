package com.example.shippingbotserver.service.interfaces;

import com.example.shippingbotserver.dto.UserDto;
import com.example.shippingbotserver.entity.User;

public interface DtoConverter {
    UserDto convert(byte[] image, User user, String status);
    UserDto convertEmpty(byte[] image);
}
