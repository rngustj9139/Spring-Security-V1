package koo.securityv1.config.auth;

import koo.securityv1.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인 진행이 완료가 되면 session을 만들어준다. (Security ContextHolder에 세션을 저장함)
 * 세션에 들어갈 수 있는 정보(오브젝트)는 Authentication 객체
 * Authentication 안에는 User 정보가 있어야함 => 유저 정보를 담는 오브젝트의 타입은 UserDetails 객체이다.
 * **/
@Getter @Setter // 세션 내부의 Authentication 내부에서 UserDetails(PrincipalDetails)를 쓸지 OAuth2User를 쓸지 애매함 => OAuth2User도 상속받기!
public class PrincipalDetails implements UserDetails, OAuth2User { // UserDetails에 접근하는 방법 => 일단 UserDetails를 상속받기, PrincipalDetails는 UserDetails의 성격을 가짐 이제 PrincipalDetails를 Authentication안에 넣을 수 있다.

    private User user; // User를 품기(User에 접근 가능해짐)
    private Map<String, Object> attributes; // 아래 getAttributes()와 getName() 함수에서 쓰임임

    public PrincipalDetails(User user) { // 일반 로그인 할 때 사용
        this.user = user;
    }

    public PrincipalDetails(User user, Map<String, Object> attributes) { // OAuth 로그인 할 때 사용
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 해당 User의 권한을 리턴하는 곳
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 현재 시간 - 로그인 시간 => 1년을 초과하면 return false;
        return true;
    }

    /**
     * attributes는
     * {
     *     sub=109742856182916427686,
     *     name=구현서,
     *     given_name=현서,
     *     family_name=구,
     *     picture=https://lh3.googleusercontent.com/-cP67SVp6dA0,
     *     email_verified=true,
     *     locale=ko
     * }
     * 와 같은 데이터를 담고있다.
     **/
    @Override
    public Map<String, Object> getAttributes() { // OAuth2User의 함수
        return attributes;
    }

    @Override
    public String getName() { // OAuth2User의 함수
        return null;
    }

}
