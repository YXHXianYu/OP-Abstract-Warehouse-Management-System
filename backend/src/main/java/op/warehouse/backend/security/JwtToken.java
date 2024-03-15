package op.warehouse.backend.security;

import op.warehouse.backend.util.JwtUtil;
import org.apache.shiro.authc.AuthenticationToken;

public class JwtToken implements AuthenticationToken {
    private String username;
    private String token;

    public JwtToken(String token){
        this.token = token;
        JwtUtil jwtUtil = new JwtUtil();
        this.username = jwtUtil.getClaimFiled(token, "username");
    }

    /**
     * 类似用户名
     * @return
     */
    @Override
    public Object getPrincipal() {
        return username;
    }

    /**
     * 类似密码
     * @return
     */
    @Override
    public Object getCredentials() {
        return token;
    }
}
