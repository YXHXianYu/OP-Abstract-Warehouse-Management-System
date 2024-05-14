package op.warehouse.backend.security;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;

public class RequiresRoleTypeAuthorizingAnnotationMethodInterceptor  extends AuthorizingAnnotationMethodInterceptor {
    public RequiresRoleTypeAuthorizingAnnotationMethodInterceptor() {
        super(new RoleAnnotationHandler());
    }

    public RequiresRoleTypeAuthorizingAnnotationMethodInterceptor (AnnotationResolver annotationResolver) {
        super(new RoleAnnotationHandler(), annotationResolver);
    }
}
