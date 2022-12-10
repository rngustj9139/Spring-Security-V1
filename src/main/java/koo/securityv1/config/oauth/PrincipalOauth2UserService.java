package koo.securityv1.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    /**
     * 구글로그인 후 후처리 되는 함수(구글로부터 받은 userRequest 데이터에 대한 후처리 되는 함수)
     * 구글로그인 후 엑세스 토큰과 사용자 정보가 userRequest에 담겨있음음
     **/
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest: " + userRequest);
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
        System.out.println("getAccessToken: " + userRequest.getAccessToken());
        System.out.println("getRegistrationId: " + userRequest.getClientRegistration().getRegistrationId());
        System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes()); // 이곳에 유저 정보가 들어있다. (긴 ID값과 이름, 이메일, 사진정보가 들어있다. username을 google_10974285618291642 이런식으로 지으면 된다.)

        return super.loadUser(userRequest);
    }

}
