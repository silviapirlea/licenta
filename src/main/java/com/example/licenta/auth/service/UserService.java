package com.example.licenta.auth.service;

import com.example.licenta.auth.model.RoleEnum;
import com.example.licenta.auth.model.User;
import com.example.licenta.auth.repository.RoleRepository;
import com.example.licenta.auth.repository.UserRepository;
import com.example.licenta.auth.security.jwt.JwtUtils;
import com.example.licenta.auth.security.services.TotpManager;
import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.auth.security.services.UserDetailsServiceImpl;
import com.example.licenta.exceptions.BadRequestException;
import com.example.licenta.exceptions.InternalServerException;
import com.example.licenta.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TotpManager totpManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username).get();
        if(!user.isMfa()){
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            return jwtCookie.toString();
        }
        return "";
    }

    public String verify(String username, String password, String code) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("username %s", username)));

        if (!totpManager.verifyCode(code, user.getSecret())) {
            throw new BadRequestException("Code is incorrect");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        return jwtCookie.toString();
    }

    public List<User> findUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(roleRepository.findByName(RoleEnum.ROLE_USER).get()))
                .collect(Collectors.toList());
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
