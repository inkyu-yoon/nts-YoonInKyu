package com.nts.controller;

import com.nts.global.Response;
import com.nts.domain.user.dto.UserCreateRequest;
import com.nts.domain.user.dto.UserCreateResponse;
import com.nts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Response<UserCreateResponse>> create(@RequestBody UserCreateRequest requestDto) {

        UserCreateResponse response = userService.createUser(requestDto);

        return ResponseEntity.status(CREATED).body(Response.success(response));
    }
}

