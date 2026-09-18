package com.booking.modules.auth.service;

import com.booking.common.BadRequestException;
import com.booking.modules.auth.dto.SocialLoginRequest;
import com.booking.modules.user.entity.Role;
import com.booking.modules.user.entity.RoleName;
import com.booking.modules.user.entity.User;
import com.booking.modules.user.repository.RoleRepository;
import com.booking.modules.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
public class SocialAuthService {

    private static final Logger log = LoggerFactory.getLogger(SocialAuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public SocialAuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User processSocialUser(SocialLoginRequest request) {
        String provider = request.getProvider().toUpperCase();
        if (!provider.equals("GOOGLE") && !provider.equals("LINE")) {
            throw new BadRequestException("不支援的第三方登入提供者: " + provider);
        }

        // In production, verify Google ID token / Line Access token with Google/Line OpenID servers
        String socialId = request.getProviderUserId() != null && !request.getProviderUserId().isBlank()
                ? request.getProviderUserId()
                : UUID.randomUUID().toString();

        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            email = provider.toLowerCase() + "_" + socialId + "@social.booking.com";
        }

        final String userEmail = email;

        return userRepository.findByOauthProviderAndOauthId(provider, socialId)
                .orElseGet(() -> {
                    // Check if email is already registered
                    return userRepository.findByEmail(userEmail)
                            .map(existingUser -> {
                                existingUser.setOauthProvider(provider);
                                existingUser.setOauthId(socialId);
                                return userRepository.save(existingUser);
                            })
                            .orElseGet(() -> {
                                Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                        .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_USER).build()));

                                String baseUsername = (request.getName() != null && !request.getName().isBlank())
                                        ? request.getName().toLowerCase().replaceAll("\\s+", "")
                                        : provider.toLowerCase() + "_user";
                                String username = baseUsername + "_" + UUID.randomUUID().toString().substring(0, 6);

                                User newUser = User.builder()
                                        .username(username)
                                        .email(userEmail)
                                        .fullName(request.getName() != null ? request.getName() : "Social User")
                                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                        .oauthProvider(provider)
                                        .oauthId(socialId)
                                        .twoFactorEnabled(false)
                                        .roles(Collections.singleton(userRole))
                                        .build();

                                return userRepository.save(newUser);
                            });
                });
    }
}
