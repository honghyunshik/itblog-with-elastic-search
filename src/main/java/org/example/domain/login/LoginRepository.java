package org.example.domain.login;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, String> {

    @Query("select * from login where token = :token and expiration > CURRENT_TIMESTAMP")
    Optional<Login> findByEmailNotExpired(@Param("token") String token);
}
