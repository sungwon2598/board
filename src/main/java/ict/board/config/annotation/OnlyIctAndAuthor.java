package ict.board.config.annotation;

import ict.board.domain.member.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyIctAndAuthor {
    Role[] roles() default {Role.ADMIN, Role.MANAGER, Role.STAFF};
}