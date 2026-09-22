package com.booking.modules.user.service;

import com.booking.common.BadRequestException;
import com.booking.common.ResourceNotFoundException;
import com.booking.modules.user.dto.PasswordChangeRequest;
import com.booking.modules.user.dto.UpdateUserDto;
import com.booking.modules.user.dto.UserAdminUpdateDto;
import com.booking.modules.user.dto.UserProfileDto;
import com.booking.modules.user.entity.Role;
import com.booking.modules.user.entity.RoleName;
import com.booking.modules.user.entity.User;
import com.booking.modules.user.repository.RoleRepository;
import com.booking.modules.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 取得自己個人資料 (一般使用者與管理者皆可)
     */
    @Transactional(readOnly = true)
    public UserProfileDto getMyProfile(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));
        return mapToDto(user);
    }

    /**
     * 一般使用者修改自己的資料
     */
    @Transactional
    public UserProfileDto updateMyProfile(Long currentUserId, UpdateUserDto updateDto) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        if (updateDto.getFullName() != null) {
            user.setFullName(updateDto.getFullName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }

        User updated = userRepository.save(user);
        return mapToDto(updated);
    }

    /**
     * 修改密碼
     */
    @Transactional
    public void changePassword(Long currentUserId, PasswordChangeRequest req) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到使用者"));

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("原密碼輸入錯誤，請重新確認！");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 管理者：查看所有使用者清單 (包含管理者與一般會員)
     */
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * 管理者：查看所有非管理者使用者清單
     */
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllNonAdminUsers() {
        return userRepository.findAllNonAdminUsers().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * 管理者：查看特定非管理者使用者
     */
    @Transactional(readOnly = true)
    public UserProfileDto getNonAdminUserById(Long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該使用者"));
        return mapToDto(targetUser);
    }

    /**
     * 管理者：變更使用者權限角色 (ROLE_ADMIN / ROLE_USER)
     */
    @Transactional
    public UserProfileDto changeUserRole(Long targetUserId, String newRoleStr, Long currentAdminId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到指定的帳號"));

        if (targetUserId.equals(currentAdminId)) {
            throw new BadRequestException("安全保護機制：無法變更目前登入中自己的權限！");
        }

        RoleName targetRoleName = "ROLE_ADMIN".equalsIgnoreCase(newRoleStr) ? RoleName.ROLE_ADMIN : RoleName.ROLE_USER;
        Role role = roleRepository.findByName(targetRoleName)
                .orElseGet(() -> roleRepository.save(new Role(null, targetRoleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        if (targetRoleName == RoleName.ROLE_ADMIN) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_USER)));
            roles.add(userRole);
        }

        targetUser.setRoles(roles);
        User saved = userRepository.save(targetUser);
        return mapToDto(saved);
    }

    /**
     * 管理者：刪除使用者帳號
     */
    @Transactional
    public void deleteUser(Long targetUserId, Long currentAdminId) {
        if (targetUserId.equals(currentAdminId)) {
            throw new BadRequestException("無法刪除當前登入之管理員帳號！");
        }
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該使用者"));
        userRepository.delete(targetUser);
    }

    /**
     * 管理者：修改非管理者使用者相關資訊
     */
    @Transactional
    public UserProfileDto updateNonAdminUser(Long targetUserId, UserAdminUpdateDto updateDto) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該使用者"));

        boolean isTargetAdmin = targetUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);

        if (isTargetAdmin) {
            throw new AccessDeniedException("安全保護機制：管理者不可修改其他管理者之帳號資訊！");
        }

        if (updateDto.getEmail() != null && !updateDto.getEmail().equalsIgnoreCase(targetUser.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new BadRequestException("此 Email 已被其他帳號使用");
            }
            targetUser.setEmail(updateDto.getEmail());
        }
        if (updateDto.getFullName() != null) {
            targetUser.setFullName(updateDto.getFullName());
        }
        if (updateDto.getPhone() != null) {
            targetUser.setPhone(updateDto.getPhone());
        }
        if (updateDto.getTwoFactorEnabled() != null) {
            targetUser.setTwoFactorEnabled(updateDto.getTwoFactorEnabled());
            if (!updateDto.getTwoFactorEnabled()) {
                targetUser.setTwoFactorSecret(null);
            }
        }

        User saved = userRepository.save(targetUser);
        return mapToDto(saved);
    }

    private UserProfileDto mapToDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .oauthProvider(user.getOauthProvider())
                .roles(user.getRoles().stream().map(Role::getName).map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
