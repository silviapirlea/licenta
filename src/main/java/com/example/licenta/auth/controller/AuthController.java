package com.example.licenta.auth.controller;


import com.example.licenta.auth.model.Role;
import com.example.licenta.auth.model.RoleEnum;
import com.example.licenta.auth.model.User;
import com.example.licenta.auth.payload.request.LoginRequest;
import com.example.licenta.auth.payload.request.SignupRequest;
import com.example.licenta.auth.payload.request.VerifyCodeRequest;
import com.example.licenta.auth.payload.response.MessageResponse;
import com.example.licenta.auth.payload.response.SignupResponse;
import com.example.licenta.auth.payload.response.UserInfoResponse;
import com.example.licenta.auth.repository.RoleRepository;
import com.example.licenta.auth.repository.UserRepository;
import com.example.licenta.auth.security.jwt.JwtUtils;
import com.example.licenta.auth.security.services.TotpManager;
import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String LOGOUT_MESSAGE = "You've been signed out!";
    private static final String USERNAME_ERROR = "Error: Username is already taken!";
    private static final String EMAIL_ERROR = "Error: Email is already in use!";
    private static final String ROLE_NOT_FOUND = "Error: Role is note found!";
    private static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully";
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private TotpManager totpManager;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(role -> role.getAuthority())
//                .collect(Collectors.toList());
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
//                .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
        String jwtCookie = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        if(StringUtils.hasLength(jwtCookie)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(role -> role.getAuthority())
                    .collect(Collectors.toList());
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie)
                    .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles, userDetails.isMfa()));
        }
        return ResponseEntity.ok().body(new UserInfoResponse(true));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
        String jwtCookie = userService.verify(verifyCodeRequest.getUsername(), verifyCodeRequest.getPassword(), verifyCodeRequest.getCode());
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie)
                .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles, userDetails.isMfa()));

    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse(LOGOUT_MESSAGE));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse(USERNAME_ERROR));
        }
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse(EMAIL_ERROR));
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()), signupRequest.isMfa());

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
            roles.add(userRole);
        }
        else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
                        roles.add(userRole);
                }
            });
        }

        //verifici daca user.isMfa atunci steezi secret cu totpManager.generateSecret
        if(user.isMfa()) {
            user.setSecret(totpManager.generateSecret());
        }

        user.setRoles(roles);
        userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity
                .created(location)
                .body(new SignupResponse(user.isMfa(),
                        totpManager.getUriForImage(user.getSecret())));
    }


//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
//        if(userRepository.existsByUsername(signupRequest.getUsername())) {
//            return ResponseEntity.badRequest().body(new MessageResponse(USERNAME_ERROR));
//        }
//        if(userRepository.existsByEmail(signupRequest.getEmail())) {
//            return ResponseEntity.badRequest().body(new MessageResponse(EMAIL_ERROR));
//        }
//
//        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()));
//
//        Set<String> strRoles = signupRequest.getRole();
//        Set<Role> roles = new HashSet<>();
//
//        if(strRoles == null) {
//            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
//            roles.add(userRole);
//        }
//        else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "ADMIN":
//                        Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
//                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
//                        roles.add(adminRole);
//                        break;
//                    default:
//                        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
//                                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
//                        roles.add(userRole);
//                }
//            });
//        }
//
//        //verifici daca user.isMfa atunci steezi secret cu totpManager.generateSecret
//
//        user.setRoles(roles);
//        userRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse(USER_REGISTERED_SUCCESSFULLY));
//    }
}
