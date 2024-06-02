package com.example.licenta.auth.controller;


import com.example.licenta.auth.mappers.UserMapper;
import com.example.licenta.auth.model.User;
import com.example.licenta.auth.payload.response.RegularUserDetails;
import com.example.licenta.auth.payload.response.UserInfoResponse;
import com.example.licenta.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RegularUserDetails>> getUsers() {
        return ResponseEntity.ok(this.userMapper.toApiModelList(userService.findUsers()));
    }
}
