package op.warehouse.backend.util;

import op.warehouse.backend.dto.ResponseCodeEnum;
import op.warehouse.backend.dto.ResultDTO;
import op.warehouse.backend.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class SecurityUtilities {
    public static ResultDTO getReadOthersResultDTO(Long id) {
        // 获取当前 Subject
        Subject subject = SecurityUtils.getSubject();
        // 获取已认证的 Principal（用户对象）
        User authUser = (User) subject.getPrincipal();
        // 判断id是否合法
        if(!id.equals(authUser.getId())) {
            return ResultDTO.error(ResponseCodeEnum.UNAUTHORIZED, "Not permitted to check other's information");
        }
        return null;
    }

    public static User getAuthUser() {
        // 获取当前 Subject
        Subject subject = SecurityUtils.getSubject();
        // 获取已认证的 Principal（用户对象）
        return (User) subject.getPrincipal();
    }
}
