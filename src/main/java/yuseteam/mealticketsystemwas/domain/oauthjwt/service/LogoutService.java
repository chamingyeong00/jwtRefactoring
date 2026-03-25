package yuseteam.mealticketsystemwas.domain.oauthjwt.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;
import yuseteam.mealticketsystemwas.domain.oauthjwt.repository.UserRepository;

@Service
public class LogoutService {

    private final UserRepository userRepository;

    public LogoutService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void logoutByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Integer current = user.getTokenVersion();
        if (current == null) current = 0;
        user.setTokenVersion(current + 1);
        userRepository.save(user);
    }
}

