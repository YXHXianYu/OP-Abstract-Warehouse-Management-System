package op.warehouse.backend.security;

import org.apache.shiro.spring.aop.SpringAnnotationResolver;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;

public class RoleTypeAopAllianceAnnotationsAuthorizingMethodInterceptor extends AopAllianceAnnotationsAuthorizingMethodInterceptor {
    public RoleTypeAopAllianceAnnotationsAuthorizingMethodInterceptor() {
        super();
        this.methodInterceptors.add(
                new RequiresRoleTypeAuthorizingAnnotationMethodInterceptor(new SpringAnnotationResolver())
        );
    }
}
