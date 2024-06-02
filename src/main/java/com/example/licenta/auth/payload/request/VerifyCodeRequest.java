package com.example.licenta.auth.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequest {
    private String username;
    private String password;
    private String code;
}
