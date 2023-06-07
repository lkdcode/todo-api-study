package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원가입 테스트")
    void saveTest() {
        //given
        User newUser = User.builder()
                .email("abc1234@abc.com")
                .password("1234")
                .userName("망아지")
                .build();

        //when
        User saved = userRepository.save(newUser);

        //then
        assertNotNull(saved);
    }

    @Test
    @DisplayName("이메일로 회원조회하기")
    void findByEmailTest() {
        //given
        String email = "abc1234@abc.com";

        //when
        Optional<User> userOptional = userRepository.findByEmail(email);

        //then
        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals("망아지", user.getUserName());
    }

    @Test
    @DisplayName("이메일 중복체크를 하면 중복값이 true 여야한다")
    void emailTureTest() {
        //given
        String email = "abc1234@abc.com";

        //when
        boolean flag = userRepository.existsByEmail(email);

        //then
        assertTrue(flag);
    }
    @Test
    @DisplayName("이메일 중복체크를 하면 중복값이 false 여야한다")
    void emailFalseTest() {
        //given
        String email = "def1234@abc.com";

        //when
        boolean flag = userRepository.existsByEmail(email);

        //then
        assertFalse(flag);
    }

}