package com.example.shippingbotserver.service.interfaces;

import com.example.shippingbotserver.entity.User;

public interface Drawer {
    byte[] draw(User user);
}
