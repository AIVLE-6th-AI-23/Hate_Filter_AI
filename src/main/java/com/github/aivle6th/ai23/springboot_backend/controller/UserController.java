package com.github.aivle6th.ai23.springboot_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.aivle6th.ai23.springboot_backend.dto.ApiResponseDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserLoginRequestDto;
import com.github.aivle6th.ai23.springboot_backend.dto.UserProfileResponseDto;
import com.github.aivle6th.ai23.springboot_backend.entity.User;
import com.github.aivle6th.ai23.springboot_backend.dto.UserInfoRequestDto;
import com.github.aivle6th.ai23.springboot_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관리 API")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "사용자의 로그인 요청을 처리합니다.")
    public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> login(
        @Parameter(description = "로그인 요청 데이터", required = true) @RequestBody UserLoginRequestDto loginRequest,
        HttpServletRequest request
    ){
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmployeeId(),
                            loginRequest.getPassword()
                    );
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Profile 정보 조회
            String employeeId = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userService.getUserByEmployeeId(employeeId);

            return ResponseEntity.ok(new ApiResponseDto<>(true, "로그인 성공", new UserProfileResponseDto(user)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "아이디 또는 비밀번호가 잘못되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "로그인 중 문제가 발생했습니다.", null));
        }
    }
    
    @Operation(summary = "회원가입", description = "새로운 사용자를 회원가입 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> signup(
            @Parameter(description = "회원가입 요청 데이터", required = true) @RequestBody UserInfoRequestDto user) {
        String response = userService.signup(user);
        return ResponseEntity.ok(new ApiResponseDto<>(true, "회원가입 성공", response));
    }

    @Operation(summary = "회원 정보 수정", description = "사용자 정보를 수정하고 비밀번호 변경 시 세션을 무효화합니다.")
    @PostMapping("/update")
    public ResponseEntity<ApiResponseDto<String>> update(
            @Parameter(description = "회원 정보 수정 요청 데이터", required = true) @RequestBody UserInfoRequestDto user,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // 비밀번호 업데이트 로직
            String updateResponse = userService.updateUserInfo(user);

            // 현재 세션 무효화 처리
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

            return ResponseEntity.ok(new ApiResponseDto<>(true, "비밀번호 변경 성공, 재로그인이 필요합니다.", updateResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @Operation(summary = "유저 프로필 조회", description = "현재 세션의 로그인된 사용자의 정보를 반환")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<UserProfileResponseDto>> getProfile() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDto<>(false, "현재 세션이 만료 되었습니다. 재로그인이 필요합니다.", null));
            }

            String employeeId = ((UserDetails) authentication.getPrincipal()).getUsername();
            
            UserProfileResponseDto userProfileResponseDto = new UserProfileResponseDto(userService.getUserByEmployeeId(employeeId));
            return ResponseEntity.ok(new ApiResponseDto<UserProfileResponseDto>(true, "프로필 조회", userProfileResponseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "로그인 중 문제가 발생했습니다.", null));
        }
    }
}
