package koo.securityv1.config.auth;

import koo.securityv1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 * 로그인 진행이 완료가 되면 session을 만들어준다. (Security ContextHolder에 세션을 저장함)
 * 세션에 들어갈 수 있는 정보(오브젝트)는 Authentication 객체
 * Authentication 안에는 User 정보가 있어야함 => 유저 정보를 담는 오브젝트의 타입은 UserDetails 객체이다.
 * **/
public class PrincipalDetails implements UserDetails { // UserDetails에 접근하는 방법 => 일단 UserDetails를 상속받기, PrincipalDetails는 UserDetails의 성격을 가짐 이제 PrincipalDetails를 Authentication안에 넣을 수 있다.

    @Autowired
    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
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

}
