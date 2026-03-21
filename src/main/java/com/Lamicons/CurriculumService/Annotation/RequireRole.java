package com.Lamicons.CurriculumService.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required roles for controller methods.
 * The role check is performed using the X-USER-ROLE header.
 * 
 * Example:
 * @RequireRole("SUPER_ADMIN")
 * @RequireRole({"ADMIN", "SUPER_ADMIN"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * Array of allowed roles. User must have at least one of these roles.
     */
    String[] value();
}
