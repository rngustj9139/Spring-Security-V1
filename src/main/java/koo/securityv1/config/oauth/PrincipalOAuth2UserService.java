package koo.securityv1.config.oauth;

import koo.securityv1.config.auth.PrincipalDetails;
import koo.securityv1.config.oauth.provider.FacebookUserInfo;
import koo.securityv1.config.oauth.provider.GoogleUserInfo;
import koo.securityv1.config.oauth.provider.NaverUserInfo;
import koo.securityv1.config.oauth.provider.OAuth2UserInfo;
import koo.securityv1.model.User;
import koo.securityv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    /**
     * 구글로그인 후 후처리 되는 함수(구글로부터 받은 userRequest 데이터에 대한 후처리 되는 함수)
     * 구글로그인 후 엑세스 토큰과 사용자 정보가 userRequest에 담겨있음
     **/
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest: " + userRequest);
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
        System.out.println("getAccessToken: " + userRequest.getAccessToken());
        System.out.println("getRegistrationId: " + userRequest.getClientRegistration().getRegistrationId()); // registrationId로 어떤 소셜 플랫폼에서 로그인했는지 확인가능(ex-google)
        // access token을 받고 나서까지만 userRequest 정보에 들어있고, 회원 프로플 정보를 받을 때 사용되는게 loadUser 함수
        // userRequest 정보를 통해 loadUser 함수를 호출하고 구글로부터 회원프로필 정보를 받는다.
        System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes()); // 이곳에 유저 정보가 들어있다. (긴 숫자 ID값과 이름, 이메일, 사진정보가 들어있다. username을 google_10974285618291642 이런식으로 지으면 된다.)

        // 강제로 회원가입 진행(이미 회원가입 되어있을 경우에는 진행 안함)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 소셜 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("페이스북 소셜 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) { // 네이버의 getAttributes: {resultcode=00, message=success, response={id=d_6sW8FZ3gN6xYVS_1o-fA7zeuFswLw1DK0mwrw29Mw, email=mnb9139@naver.com, name=구현서}}
            System.out.println("네이버 소셜 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else {
            System.out.println("구글과 페이스북, 네이버 소셜 로그인만 지원합니다.");
        }

//      String provider = userRequest.getClientRegistration().getRegistrationId(); // google
//      String providerId = oAuth2User.getAttribute("sub"); // google에는 sub(긴 숫자 id)가 존재하지만 페이스북에서는 존재하지 않는다.(null)
//      String email = oAuth2User.getAttribute("email");
//      String username = provider + "_" + providerId;
//      String password = bCryptPasswordEncoder.encode("겟인데어"); // 크게 의미 없음
//      String role = "ROLE_USER";

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            System.out.println("소셜로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        } else {
            System.out.println("소셜로그인이 최초가 아닙니다.");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); // 이때 리턴된 UserDetails가 Authentication 안으로 들어가게 된다.
    }

}
