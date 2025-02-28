package com.chukchuk.haksa.domain.user.model;

import com.chukchuk.haksa.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_nickname")
    private String profileNickname;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "portal_connected")
    private Boolean portalConnected;

    @Column(name = "connected_at")
    private Instant connectedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt; // Soft delete 적용
}
