package com.chukchuk.haksa.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    private String userId;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private Date expiry;

    public RefreshToken(String userId, String token, Date expiry) {
        this.userId = userId;
        this.token = token;
        this.expiry = expiry;
    }

    public RefreshToken() {

    }
}
