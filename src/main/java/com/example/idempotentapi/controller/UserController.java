package com.example.idempotentapi.controller;

import com.example.idempotentapi.dto.IdemUser;
import com.example.idempotentapi.dto.OperationResponse;
import com.example.idempotentapi.model.User;
import com.example.idempotentapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<OperationResponse> create(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }

    @PostMapping("/create-idem")
    public ResponseEntity<OperationResponse> createIdem(@Valid @RequestBody IdemUser request) {
        return ResponseEntity.ok(userService.createIdem(request.getUserData()));
    }

    @PostMapping("/create-with-key")
    public ResponseEntity<OperationResponse> createWithKey(@Valid @RequestBody IdemUser request) {
        return ResponseEntity.ok(userService.createWithKey(request.getUserData(), request.getIdemKey()));
    }

    @PostMapping("/create-async")
    public ResponseEntity<OperationResponse> createAsync(@Valid @RequestBody IdemUser request) {
        return ResponseEntity.ok(userService.createAsync(request.getUserData(), request.getIdemKey()));
    }
}
