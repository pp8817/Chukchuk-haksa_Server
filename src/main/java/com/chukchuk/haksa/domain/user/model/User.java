package com.chukchuk.haksa.domain.user.model;

import com.chukchuk.haksa.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "portal_connected")
    private Boolean portalConnected;

    @Column(name = "connected_at")
    private Instant connectedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt; /**
     * Creates a new User instance with the specified id, email, and profile nickname.
     *
     * <p>This builder constructor initializes the user with default settings:
     * the user is marked as active (not deleted) and not connected to a portal.</p>
     *
     * @param id the unique identifier for the user
     * @param email the user's email address
     * @param profileNickname the user's profile nickname
     */

    @Builder
    public User(UUID id, String email, String profileNickname) {
        this.id = id;
        this.email = email;
        this.profileNickname = profileNickname;
        this.isDeleted = false;
        this.portalConnected = false;
    }
}
