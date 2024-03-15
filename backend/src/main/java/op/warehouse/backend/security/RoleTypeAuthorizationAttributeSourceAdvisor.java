package op.warehouse.backend.security;

import op.warehouse.backend.annotation.RequiresRoleType;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RoleTypeAuthorizationAttributeSourceAdvisor extends AuthorizationAttributeSourceAdvisor {
    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
            new Class[]{RequiresRoleType.class, RequiresPermissions.class, RequiresRoles.class,
                    RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class};

    public RoleTypeAuthorizationAttributeSourceAdvisor() {
        setAdvice(new RoleTypeAopAllianceAnnotationsAuthorizingMethodInterceptor());
    }

    @Override
    public boolean matches(Method method, Class targetClass) {
        Method m = method;
        if (this.isAuthzAnnotationPresent(method)) {
            return true;
        } else {
            if (targetClass != null) {
                try {
                    m = targetClass.getMethod(m.getName(), m.getParameterTypes());
                    return this.isAuthzAnnotationPresent(m) || this.isAuthzAnnotationPresent(targetClass);
                } catch (NoSuchMethodException var5) {
                }
            }

            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    private boolean isAuthzAnnotationPresent(Class<?> targetClazz) {
        Class[] authzAnnotationClasses = AUTHZ_ANNOTATION_CLASSES;

        for (int i = 0; i < authzAnnotationClasses.length; ++i) {
            Class<? extends Annotation> annClass = authzAnnotationClasses[i];
            Annotation a = AnnotationUtils.findAnnotation(targetClazz, annClass);
            if (a != null) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("Duplicates")
    private boolean isAuthzAnnotationPresent(Method method) {
        Class[] authzAnnotationClasses = AUTHZ_ANNOTATION_CLASSES;

        for (int i = 0; i < authzAnnotationClasses.length; ++i) {
            Class<? extends Annotation> annClass = authzAnnotationClasses[i];
            Annotation a = AnnotationUtils.findAnnotation(method, annClass);
            if (a != null) {
                return true;
            }
        }

        return false;
    }
}
