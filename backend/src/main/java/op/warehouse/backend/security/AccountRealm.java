package op.warehouse.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.entity.RoleType;
import op.warehouse.backend.entity.User;
import op.warehouse.backend.exception.BaseException;
import op.warehouse.backend.service.UserService;
import op.warehouse.backend.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class AccountRealm extends AuthorizingRealm {
    @Resource
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    /**
     * 多重写一个support
     * 标识这个Realm是专门用来验证JwtToken
     * 不负责验证其他的token（UsernamePasswordToken）
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String jwt = (String) authenticationToken.getCredentials();
        // 获取jwt中关于用户名
        String username = (String) jwtUtil.getClaimsByToken(jwt).get("username");
        RoleType roleType = RoleType.valueOf((String)  jwtUtil.getClaimsByToken(jwt).get("roletype"));
        // 查询用户
        User user = userService.getUserByUsername(roleType, username);
        if (user == null) {
            throw new BaseException(ResponseCodeEnum.BAD_REQUEST, "用户不存在");
        }

        Claims claims = jwtUtil.getClaimsByToken(jwt);
        if (jwtUtil.isTokenExpired(claims.getExpiration())) {
            throw new BaseException(ResponseCodeEnum.BAD_REQUEST, "token过期，请重新登录");
        }
        return new SimpleAuthenticationInfo(user, jwt, getName());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("获取角色权限");
        User user = (User) principalCollection.iterator().next();
        Set<String> roles = new HashSet<>();
        roles.add(user.getRoleType().toString());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        log.info(roles.toString());
        info.setRoles(roles);
        return info;
    }
}
