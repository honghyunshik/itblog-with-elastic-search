package org.example.domain.logout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LogoutRepository extends JpaRepository<Logout,String> {

}
