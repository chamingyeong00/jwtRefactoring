package yuseteam.mealticketsystemwas.domain.auth.oauth;

import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;
import yuseteam.mealticketsystemwas.domain.auth.repository.UserRepository;

@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserResponse oAuth2Response;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverUserResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleUserResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoUserResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자입니다: " + registrationId);
        }

        String socialname = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existData = userRepository.findBySocialname(socialname);

        if (existData == null) {
            User userEntity = User.builder()
                    .socialname(socialname)
                    .name(oAuth2Response.getName())
                    .role(null)
                    .phone(null)
                    .build();

            userRepository.save(userEntity);
            return new OAuthPrincipal(userEntity.getId());
        }

        existData.setName(oAuth2Response.getName());
        userRepository.save(existData);

        return new OAuthPrincipal(existData.getId());
    }
}
