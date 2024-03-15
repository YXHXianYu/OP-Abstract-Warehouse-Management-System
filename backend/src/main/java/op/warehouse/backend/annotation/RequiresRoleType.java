package op.warehouse.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import op.warehouse.backend.entity.RoleType;
import org.apache.shiro.authz.annotation.Logical;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRoleType {
    RoleType[] value();
    Logical logical() default Logical.AND;

    // 描述
    String[] description() default {};
}
