package yuseteam.mealticketsystemwas.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import yuseteam.mealticketsystemwas.domain.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findBySocialname(String socialname);
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByPhone(String phone);
}