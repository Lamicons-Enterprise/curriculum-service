package com.Lamicons.CurriculumService.Annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for user role header parameter.
 * This is provided by API Gateway after JWT validation.
 * Makes Swagger documentation consistent across all endpoints.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "X-USER-ROLE",
    description = "User role extracted from JWT token by API Gateway",
    required = true,
    in = ParameterIn.HEADER,
    schema = @Schema(type = "string", example = "SUPER_ADMIN", allowableValues = {"SUPER_ADMIN", "ADMIN", "INSTRUCTOR", "COORDINATOR", "STUDENT"})
)
public @interface UserRoleHeader {
}
