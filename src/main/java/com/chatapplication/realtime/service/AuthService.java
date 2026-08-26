package com.chatapplication.realtime.service;


import com.chatapplication.realtime.dto.LoginResponseDto;
import com.chatapplication.realtime.dto.SignUpRequestDto;
import com.chatapplication.realtime.dto.SignUpResponseDto;
import com.chatapplication.realtime.dto.UserResponseDto;
import com.chatapplication.realtime.dto.tokenDto.NewAccessTokenResponse;
import com.chatapplication.realtime.dto.tokenDto.RefreshTokenRequestDto;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.type.Roles;
import com.chatapplication.realtime.exception.InvalidCredentialsException;
import com.chatapplication.realtime.repository.UserRepository;
import com.chatapplication.realtime.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    public SignUpResponseDto userSignUp(SignUpRequestDto signUpRequest) {
        User user = userRepository.findByEmail(signUpRequest.getEmail()).orElse(null);
        if(user != null){
            throw new IllegalArgumentException("user already exist");

        }
         user = User.builder()
                 .username(signUpRequest.getUsername())
                 .email(signUpRequest.getEmail())
                 .roles(Set.of(Roles.USER))
                 .password(passwordEncoder.encode(signUpRequest.getPassword()))
                 .build();

         userRepository.save(user);

         return new SignUpResponseDto(user.getUsername(),"you are succesfully signup");



    }
    public LoginResponseDto login(SignUpRequestDto loginRequest){
        System.out.println("before authentication");
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()
                    ));

            User user = (User) authentication.getPrincipal();

            String token = authUtil.generateAccesToken(user);
            String refreshToken = authUtil.generateRefreshToken(user);
            System.out.println(token);

            return new LoginResponseDto(token,refreshToken,new UserResponseDto(user.getEmail(),user.getId(),user.getUsername()));
        } catch (BadCredentialsException e){
            throw new InvalidCredentialsException("Invalid Email or password");
        }


    }

    public UserResponseDto getUser(User user){
        return new UserResponseDto(user.getEmail(),user.getId(),user.getUsername());
    }

    public NewAccessTokenResponse newAccessToken(RefreshTokenRequestDto request){
        String refreshToken = request.getRefreshToken();
        if (!authUtil.isTokenValid(refreshToken)) {
            return null;
        }
        String email = authUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElse(null);
        String newAccessToken = authUtil.generateAccesToken(user);
        return new NewAccessTokenResponse(newAccessToken);



    }

}
