package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.exception.DuplicateEmailException;
import com.example.todo.exception.NoRegisteredArgumentException;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.dto.response.UserSignUpResponseDTO;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    @Value("${upload.path}")
    private String uploadRootPath;

    // 회원 가입 처리
    public UserSignUpResponseDTO create(
            final UserRequestSignUpDTO dto
            , String uploadFilePath) {

        String email = dto.getEmail();
        if (dto == null || email.equals("")) {
            throw new NoRegisteredArgumentException("가입 정보가 없습니다.");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. - {}", email);
            throw new DuplicateEmailException("중복된 이메일입니다.");
        }

        // 패스워드 인코딩
        dto.setPassword(encoder.encode(dto.getPassword()));

        // 유저 엔티티로 변환
        User user = dto.toEntity(uploadFilePath);
        User saved = userRepository.save(user);

        log.info("회원가입 정상 수행됨! - saved user - {}", saved);

        return new UserSignUpResponseDTO(saved);
    }

    public boolean isDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public LoginResponseDTO authenticate(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입된 회원이 아닙니다.")
        );

        //패스워드 검증
        String rawPassword = dto.getPassword(); // 입력 비번
        String encodedPassword = user.getPassword(); // DB에 저장된 비번

        if (!encoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }


        // 로그인 성공 후에 클라이언트에 뭘 리턴할 것인가??
        // -> JWT를 클라이언트에게 발급해줘야함
        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }


    public LoginResponseDTO promoteToPremium(TokenUserInfo userInfo) throws IllegalStateException, NoRegisteredArgumentException {

        // 예외 처리
        User foundUser = userRepository
                .findById(userInfo.getUserId())
                .orElseThrow(
                        () -> new NoRegisteredArgumentException("회원 조회에 실패!")
                );

        // 일반 회원이 아니면 예외
//        if (foundUser.getRole() != Role.COMMON) {
//            throw new IllegalStateException("일반 회원이 아니면 등급을 상승 시킬 수 없습니다.");
//        }

        // 등급 변경
        foundUser.changeRole(Role.PREMIUM);
        User saved = userRepository.save(foundUser);

        // 토큰을 재발급
        String token = tokenProvider.createToken(saved);

        return new LoginResponseDTO(saved, token);
    }

    /**
     * 업로드된 파일을 서버에 저장하고 저장 경로를 리턴
     *
     * @param originalFile - 업로드된 파일의 정보
     * @return 실제로 저장된 이미지의 경로
     */
    public String uploadProfileImage(MultipartFile originalFile) throws IOException {

        // 루트 디렉토리가 존재하는지 확인 후 존재하지 않으면 생성
        File rootDir = new File(uploadRootPath);
        if (!rootDir.exists()) rootDir.mkdir();

        // 파일명을 유니크하게 변경
        String uniqueFileName = UUID.randomUUID() + "_" + originalFile.getOriginalFilename();


        // 파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);

        originalFile.transferTo(uploadFile);

        return uniqueFileName;
    }

    public String getProfilePath(String userId) {
        return uploadRootPath + "/" + userRepository.findById(userId).get().getProfileImg();
    }
}
