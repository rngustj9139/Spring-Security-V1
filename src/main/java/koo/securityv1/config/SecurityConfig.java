package koo.securityv1.config;

import koo.securityv1.config.oauth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SecurityConfig 파일을 만든 후 부터 스프링 시큐리티가 /login 페이지로 낚아 채지 않는다.
 */
@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // @Secured 어노테이션 활성화 (IndexController의 info라는 함수 위에 @Secured("ROLE_ADMIN") 어노테이션 붙이면 Admin만 접근 가능) prePostEnabled는 @PreAuthorize어노테이션 활성화(여러개의 권한을 걸고싶을때 사용)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2UserService principalOauth2UserService;

//    @Bean
//    public BCryptPasswordEncoder encodePwd() {
//        return new BCryptPasswordEncoder();
//    }

    // /logout으로 들어가면 저절로 세션이 해제된다.
    // 로그인안하고 /user로 들어가면 로그인 페이지가 뜨고 그때 로그인하면 /user란 페이지로 자동으로 이동한다.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                    .antMatchers("/user/**").authenticated()
                    .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                    .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                    .anyRequest().permitAll() // default login 페이지 안 뜰 것임
                .and()
                    .formLogin()
                    .loginPage("/loginForm") // 로그인 없이 user, manager, admin 페이지로 이동하면 로그인 페이지로 이동하게 된다.
                    .loginProcessingUrl("/login") // /login 이라는 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
                    .defaultSuccessUrl("/") // 로그인 성공후 이동할 경로
                .and()
                    .oauth2Login() // 이걸 추가해야 /oauth2/authorization/google 접속시 로그인 창이 뜬다.
                    .loginPage("/loginForm") // 이걸 추가해야 /oauth2/authorization/google 접속시 로그인 창이 뜬다.
                    .userInfoEndpoint()
                    .userService(principalOauth2UserService); // 구글 로그인이 완료된 뒤 후처리 수행
        /**
         * http://localhost:8080/login/oauth2/code/google
         * 로그인을 완료하면 구글 서버쪽에서 위의 리다이렉션 uri를 통해 코드를 발급받음 이때 이 코드를 이용해 access token을 요청하고 발급받을 수 있는데
         * 이 access token으로 사용자 대신 우리 서버가 구글 서버를 사용자의 개인정보나 민감한 정보에 접근할 수 있는 권한이 생긴다.
         * 클라이언트 id와 클라이언트 보안 비밀번호(클라이언트 secret)는 다른사람에게 노출되지 않게 관리해야한다.
         * 구글 로그인이 완료되면 access token + 사용자프로필 정보를 한방에 받는다.
         * **/
    }

//    @Bean // 기존 WebSecurityConfigurerAdapter가 disabled됨, 아래 소스 사용
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//        http.authorizeRequests()
//                .antMatchers("/user/**").authenticated()
//                .antMatchers("/manager/**").access("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
//                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
//                .anyRequest().permitAll();
//
//        return http.build();
//    }

}
