package yuseteam.mealticketsystemwas.domain.oauthjwt.service;

import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.*;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;
import yuseteam.mealticketsystemwas.domain.oauthjwt.repository.UserRepository;

@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResDTO oAuth2Response = null;

        if (registrationId.equals("naver")){
            oAuth2Response = new NaverResDTO(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")){
            oAuth2Response = new GoogleResDTO(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")){
            oAuth2Response = new KakaoResDTO(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        String socialname = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existData = userRepository.findBySocialname(socialname);

        if (existData == null){

            User userEntity = new User();
            userEntity.setSocialname(socialname);
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole(null);

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(userEntity.getId());
            userDTO.setSocialname(socialname);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(null);

            return new CustomOAuth2UserDTO(userDTO);
        }
        else {

            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(existData.getId());
            userDTO.setSocialname(existData.getSocialname());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRole());

            return new CustomOAuth2UserDTO(userDTO);
        }

    }

}
