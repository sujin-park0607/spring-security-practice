package com.example.joinpractice.service;

import com.example.joinpractice.domain.User;
import com.example.joinpractice.domain.dto.UserDto;
import com.example.joinpractice.domain.dto.UserJoinRequest;
import com.example.joinpractice.domain.exception.ErrorCode;
import com.example.joinpractice.domain.exception.JoinException;
import com.example.joinpractice.repository.UserRepository;
import com.example.joinpractice.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.token.secret}")
    private String key;
    private long expireTimeMs = 1000 * 60 * 60; //1시간

    public UserDto join(UserJoinRequest userJoinRequest) {
        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user ->{
                    throw new JoinException(ErrorCode.DEPLICATED_USER_NAME,userJoinRequest.getUserName()+"은 중복된 아이디입니다.");
                });
        User savedUser = userRepository.save(userJoinRequest.toEntity(encoder.encode(userJoinRequest.getPassword())));
        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .password(savedUser.getPassword())
                .email(savedUser.getEmail())
                .build();
    }

    public String login(String userName, String password){
        //userName이 있는지 확인
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()-> new JoinException(ErrorCode.USERNAME_NOT_FOUND,userName+"이 가입된 적이 없습니다"));

        //password일치 확인
        if(!encoder.matches(password,user.getPassword())){
            throw new JoinException(ErrorCode.INVALID_PASSWORD, "password가 일치하지 않습니다");
        }
        return JwtTokenUtil.createToken(userName, key, expireTimeMs);

    }
}
