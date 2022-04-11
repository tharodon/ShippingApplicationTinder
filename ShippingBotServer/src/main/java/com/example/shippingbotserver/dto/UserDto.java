package com.example.shippingbotserver.dto;

import com.example.shippingbotserver.entity.User;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private byte[] bytesFromFile;
    private User user;
    private String status;

    public UserDto(User user) {
        this.user = user;
        status = "";
    }

}
