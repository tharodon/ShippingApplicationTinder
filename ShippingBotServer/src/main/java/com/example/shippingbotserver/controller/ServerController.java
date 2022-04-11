package com.example.shippingbotserver.controller;

import com.example.shippingbotserver.service.UserService;
import com.example.shippingbotserver.entity.User;
import com.example.shippingbotserver.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class ServerController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto user(@PathVariable("id") Long id) {
        return userService.findUser(id);
    }

    @GetMapping("/{id}/search")
    public UserDto showQuestionnaire(@PathVariable Long id) {

        return userService.search(id);
    }

    @GetMapping("/{id}/favorites/{action}")
    public UserDto userFavorites(@PathVariable("id") Long id,
                                 @PathVariable("action") String action) {
        return userService.getFavorite(id, action);
    }

    @PostMapping("/attitude/{action}")
    public UserDto attitude(@RequestBody User user, @PathVariable String action) {
        return userService.attitudePush(user, action);
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String addPersonQuery(@RequestBody User user) {
        userService.saveLover(user);
        return "OK";
    }

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String personPutQuery(@RequestBody User person) {
        userService.update(person);
        return "OK";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String personDeleteQuery(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "OK";
    }
}