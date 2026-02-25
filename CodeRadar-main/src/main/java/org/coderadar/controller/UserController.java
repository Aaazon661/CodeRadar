package org.coderadar.controller;

import org.coderadar.common.ResultResponse;
import org.coderadar.dto.user.LoginDTO;
import org.coderadar.dto.user.RegisterDTO;
import org.coderadar.dto.user.UpdatePasswordDTO;
import org.coderadar.dto.user.UpdateUsernameDTO;
import org.coderadar.pojo.User;
import org.springframework.web.bind.annotation.*;

public interface UserController {
    ResultResponse<User> register(@RequestBody RegisterDTO dto);
    ResultResponse<User> login(@RequestBody LoginDTO dto);
    ResultResponse<User> getById(@PathVariable Long userId);
    ResultResponse<Void> updateUsername(@PathVariable Long userId, @RequestBody UpdateUsernameDTO dto);
    ResultResponse<Void> updatePassword(@PathVariable Long userId, @RequestBody UpdatePasswordDTO dto);
    ResultResponse<Void> disable(@PathVariable Long userId);
}
