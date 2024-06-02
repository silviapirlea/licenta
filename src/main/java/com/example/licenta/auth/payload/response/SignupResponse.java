package com.example.licenta.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignupResponse {
    private boolean mfa;
    private String secretImageUri;
}
