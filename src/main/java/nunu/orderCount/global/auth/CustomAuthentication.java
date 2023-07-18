package nunu.orderCount.global.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.member.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CustomAuthentication implements Authentication {

    private final Long memberId; //todo: key 변경 시 변경 필요
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(()->role.getRole());
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return memberId;
    }

    @Override
    public Object getDetails() {
        return memberId;
    }

    @Override
    public Object getPrincipal() {
        return memberId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}
