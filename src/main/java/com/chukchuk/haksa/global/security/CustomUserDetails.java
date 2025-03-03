package com.chukchuk.haksa.global.security;

import com.chukchuk.haksa.domain.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String profileNickname;
    private final String profileImage;
    private final boolean isDeleted;

    /**
     * Constructs a new CustomUserDetails instance by extracting user details from the provided User object.
     *
     * <p>This constructor initializes the unique identifier, email, profile nickname, profile image, and deletion status 
     * using the corresponding values obtained from the given User.</p>
     *
     * @param user the domain User object containing the details to populate the custom user information
     */
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.profileNickname = user.getProfileNickname();
        this.profileImage = user.getProfileImage();
        this.isDeleted = user.getIsDeleted();
    }

    /**
     * Retrieves the unique identifier for the user.
     *
     * @return the user's UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retrieves the user's profile nickname.
     *
     * @return the profile nickname of the user.
     */
    public String getProfileNickname() {
        return profileNickname;
    }

    /**
     * Returns the profile image associated with the user.
     *
     * @return the user's profile image as a URL or file path
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Returns an empty collection of granted authorities.
     *
     * <p>This method is implemented as part of the {@link UserDetails} interface. It returns an empty list
     * because the user currently has no specific authorities assigned.
     *
     * @return an empty collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 현재는 권한이 필요 없으므로 빈 리스트 반환
    }

    /**
     * Returns the password used for authentication.
     *
     * <p>This implementation always returns {@code null} because Supabase authentication does not require a password.</p>
     *
     * @return {@code null} since password-based authentication is not applicable
     */
    @Override
    public String getPassword() {
        return null; // Supabase 인증에서는 패스워드를 사용하지 않음
    }

    /**
     * Returns the user's email address, which is used as the username for authentication.
     *
     * @return the email address of this user.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Checks if the user's account is not locked.
     *
     * <p>The account is considered locked if it is marked as deleted.</p>
     *
     * @return {@code true} if the account is not marked as deleted, {@code false} otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !isDeleted;
    }

    /**
     * Always indicates that the user's credentials are valid (i.e., non-expired).
     *
     * @return true, signifying that credentials do not expire.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks whether the user account is enabled.
     * <p>
     * The account is considered enabled if it is not marked as deleted.
     * </p>
     *
     * @return {@code true} if the account is not deleted; {@code false} otherwise.
     */
    @Override
    public boolean isEnabled() {
        return !isDeleted;
    }
}