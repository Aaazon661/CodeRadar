package org.coderadar.controller.impl;

import lombok.RequiredArgsConstructor;
import org.coderadar.common.Code;
import org.coderadar.common.ResultResponse;
import org.coderadar.controller.UserController;
import org.coderadar.dto.user.LoginDTO;
import org.coderadar.dto.user.RegisterDTO;
import org.coderadar.dto.user.UpdatePasswordDTO;
import org.coderadar.dto.user.UpdateUsernameDTO;
import org.coderadar.pojo.User;
import org.coderadar.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResultResponse<User> register(@RequestBody RegisterDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getAccount())
                || !StringUtils.hasText(dto.getUsername())
                || !StringUtils.hasText(dto.getPasswordHash())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }

        // 调用Service层的register方法
        User created = userService.register(dto.getAccount(), dto.getUsername(), dto.getPasswordHash());

        if (created == null) {
            return ResultResponse.fail(Code.PARAM_ERROR.getCode(), "账号已存在或注册失败");
        }
        return ResultResponse.success(created);
    }

    @PostMapping("/login")
    public ResultResponse<User> login(@RequestBody LoginDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getAccount()) || !StringUtils.hasText(dto.getPasswordHash())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }

        User user = userService.login(dto.getAccount(), dto.getPasswordHash());
        if (user == null) {
            return ResultResponse.fail(Code.LOGIN_ERROR);
        }

        if ("DISABLED".equalsIgnoreCase(user.getStatus())) {
            return ResultResponse.fail(Code.ACCOUNT_DISABLED);
        }

        return ResultResponse.success(user);
    }

    @GetMapping("/{userId}")
    public ResultResponse<User> getById(@PathVariable Long userId) {
        if (userId == null || userId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);

        User user = userService.getUserById(userId);
        if (user == null) return ResultResponse.fail(Code.NOT_FOUND);
        return ResultResponse.success(user);
    }

    @PutMapping("/{userId}/username")
    public ResultResponse<Void> updateUsername(@PathVariable Long userId, @RequestBody UpdateUsernameDTO dto) {
        if (userId == null || userId <= 0 || dto == null || !StringUtils.hasText(dto.getUsername())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = userService.updateUsername(userId, dto.getUsername());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @PutMapping("/{userId}/password")
    public ResultResponse<Void> updatePassword(@PathVariable Long userId, @RequestBody UpdatePasswordDTO dto) {
        if (userId == null || userId <= 0 || dto == null || !StringUtils.hasText(dto.getPasswordHash())) {
            return ResultResponse.fail(Code.PARAM_ERROR);
        }
        int rows = userService.updatePassword(userId, dto.getPasswordHash());
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }

    @PutMapping("/{userId}/disable")
    public ResultResponse<Void> disable(@PathVariable Long userId) {
        if (userId == null || userId <= 0) return ResultResponse.fail(Code.PARAM_ERROR);
        int rows = userService.disableUser(userId);
        return rows > 0 ? ResultResponse.success() : ResultResponse.fail(Code.NOT_FOUND);
    }
}

