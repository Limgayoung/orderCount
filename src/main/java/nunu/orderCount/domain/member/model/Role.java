package nunu.orderCount.domain.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.member.exception.RoleIsNotExistException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Role {
    OWNER("ROLE_OWNER"),
    ADMIN("ROLE_ADMIN");

    private final String role;
    private static final Map<String, Role> roleMap = Stream.of(values()).collect(Collectors.toMap(Role::getRole, role -> role));

    public static Role of(String role) {
        if (roleMap.get(role) == null) {
            throw new RoleIsNotExistException();
        }
        return roleMap.get(role);
    }
}
