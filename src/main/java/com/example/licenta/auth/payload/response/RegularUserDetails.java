package com.example.licenta.auth.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegularUserDetails {
    private Long id;
    private String username;
    private String email;
}
