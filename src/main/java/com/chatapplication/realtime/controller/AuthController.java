package com.chatapplication.realtime.controller;


import com.chatapplication.realtime.dto.LoginResponseDto;
import com.chatapplication.realtime.dto.SignUpRequestDto;
import com.chatapplication.realtime.dto.SignUpResponseDto;
import com.chatapplication.realtime.dto.UserResponseDto;
import com.chatapplication.realtime.dto.tokenDto.NewAccessTokenResponse;
import com.chatapplication.realtime.dto.tokenDto.RefreshTokenRequestDto;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.service.AuthService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequest) {
        System.out.println("signup controller started");
        SignUpResponseDto signUpResponse = authService.userSignUp(signUpRequest);
        return ResponseEntity.ok(signUpResponse);

    }
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDto> userLogin(@RequestBody SignUpRequestDto loginRequest){
        System.out.println("login controller started ");
        LoginResponseDto loginResponseDto = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponseDto);
    }

    @GetMapping("/getuser")
    public ResponseEntity<UserResponseDto>  getUser(Authentication authentication){
        User user = (User)  authentication.getPrincipal();
        UserResponseDto responseDto= authService.getUser(user);
        return ResponseEntity.ok(responseDto);

    }
    @PostMapping("/refresh")
    public ResponseEntity<?> newAccessToken(@RequestBody RefreshTokenRequestDto request){
        System.out.println("refresh controller started ");
        NewAccessTokenResponse response = authService.newAccessToken(request);
        if(response==null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token expired or invalid");
        }
        return ResponseEntity.ok(response);
    }
}
