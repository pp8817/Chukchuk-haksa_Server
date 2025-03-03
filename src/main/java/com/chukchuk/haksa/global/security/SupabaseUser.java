package com.chukchuk.haksa.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SupabaseUser {
    private final String id;
    private final String email;
}
