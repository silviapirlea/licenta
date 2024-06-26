package com.example.licenta.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private boolean mfa;

    public UserInfoResponse(boolean mfa) {
        this.mfa = mfa;
    }
}
