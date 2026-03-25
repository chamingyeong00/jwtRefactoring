package yuseteam.mealticketsystemwas.domain.oauthjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findBySocialname(String socialname);
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByPhone(String phone);
}
