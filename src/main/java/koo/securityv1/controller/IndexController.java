package koo.securityv1.controller;

import koo.securityv1.config.auth.PrincipalDetails;
import koo.securityv1.model.User;
import koo.securityv1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @ResponseBody
    @GetMapping("/")
    public String index() {
        return "인덱스 페이지 입니다.";
    }

    @ResponseBody
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails.getUser()" + principalDetails.getUser());

        return "user";
    }

    @ResponseBody
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @ResponseBody
    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        log.info("user = {}", user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); // 패스워드 암호화가 안되었을 경우는 시큐리티 사용불가

        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @ResponseBody
    @GetMapping("/info")
    public String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @ResponseBody
    @GetMapping("/data")
    public String data() {
        return "데이터 정보";
    }

    @ResponseBody
    @GetMapping("/test/login") // 소셜 로그인 말고 일반 로그인 사용하기
    public String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) { // @AuthenticationPrincipal 어노테이션을 통해 세션에 접근 가능
        System.out.println("test/login =========");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // Authentication을 통해서도 세션에 접근가능
        System.out.println("principalDetails.getUser(): " + principalDetails.getUser());

        System.out.println("userDetails.getUsername(): " + userDetails.getUser());

        return "세션 정보 확인하기";
    }

    @ResponseBody
    @GetMapping("/test/oauth/login") // 소셜로그인 사용하기
    public String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) {
        System.out.println("test/oauth/login =========");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // Authentication을 통해서도 세션에 접근가능
        System.out.println("oAuth2User.getAttributes(): " + oAuth2User.getAttributes());

        System.out.println("oauth: " + oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

}
