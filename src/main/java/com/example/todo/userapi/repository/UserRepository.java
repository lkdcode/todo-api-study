package com.example.todo.userapi.repository;

import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // 쿼리 메서드

    // 이메일로 회원정보 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복체크
    //  @Query("SELECT COUNT(*) FROM User u WHERE u.email=:email")
    // existsBy 로 하면 자동으로 위의 쿼리가 실행됨
    boolean existsByEmail(@Param("email") String email);


}
