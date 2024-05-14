package op.warehouse.backend.security;

import op.warehouse.backend.annotation.RequiresRoleType;
import op.warehouse.backend.entity.RoleType;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class RoleAnnotationHandler extends AuthorizingAnnotationHandler {

    public RoleAnnotationHandler() {
        super(RequiresRoleType.class);
    }

    protected RoleType[] getAnnotationValue (Annotation annotation) {
        RequiresRoleType srpAnnotation = (RequiresRoleType) annotation;
        return srpAnnotation.value();
    }

    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequiresRoleType roleAnnotation)) {
            return;
        }
        RoleType[] roleTypes = roleAnnotation.value();
        ArrayList<String> strRoleTypes = new ArrayList<>();
        for(RoleType role : roleTypes) {
            strRoleTypes.add(role.toString());
        }
        RequiresRoleType srpAnnotation = (RequiresRoleType) a;

        Subject subject = getSubject();
        if(roleTypes.length == 1) {
            subject.checkRole(strRoleTypes.get(0));
        } else if (Logical.AND.equals(srpAnnotation.logical())) {

        } else {
            if(Logical.OR.equals(srpAnnotation.logical())) {
                boolean  hasAtLeastOneRole = false;
                for (String role : strRoleTypes) {
                     if (this.getSubject().hasRole(role)) {
                         hasAtLeastOneRole = true;
                         }
                     }
                if (!hasAtLeastOneRole) {
                    this.getSubject().checkRole(strRoleTypes.get(0));
                }
            }
        }
    }
}
