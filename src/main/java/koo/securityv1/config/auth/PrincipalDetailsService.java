package koo.securityv1.config.auth;

import koo.securityv1.model.User;
import koo.securityv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * SecurityConfig에서 loginProcessingUrl("/login")
 * /login 요청이 오면 자동으로 스프링 컨테이너에 올라가있는 PrincipalDetailsService의 loadUserByUsername 함수가 실행 된다.
 * /logout으로 들어가면 저절로 세션이 해제된다.
 * **/
@Service
public class PrincipalDetailsService implements UserDetailsService { // Authentication을 만들고 PrincipalDetals(UserDetails)를 Authentication에 넣는 역할

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // username은 로그인할때 넘어오는 username 프로퍼티 (다른 속성값으로 이용하고싶으면 SecurityConfig에서 .loginPage("") 아래 .usernameParameter("")로 변경해야한다)
        User userEntity = userRepository.findByUsername(username);// 먼저 해당 username을 갖는 유저가 존재하는지 확인

        if (userEntity != null) { // 패스워드 검증은 자동으로 된다.
            return new PrincipalDetails(userEntity); // 이때 리턴된 UserDetails가 Authentication 안으로 들어가게 된다. 만들어진 Authentication은 session 안으로 들어가게 된다.
        }

        return null;
    }

}
