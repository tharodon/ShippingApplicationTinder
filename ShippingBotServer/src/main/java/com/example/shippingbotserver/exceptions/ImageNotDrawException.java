package com.example.shippingbotserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "picture not draw")
public class ImageNotDrawException extends RuntimeException {
}
