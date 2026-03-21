package com.Lamicons.CurriculumService.Annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for userId header parameter.
 * This is provided by API Gateway after JWT validation.
 * Makes Swagger documentation consistent across all endpoints.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "X-USER-ID",
    description = "User ID extracted from JWT token by API Gateway",
    required = true,
    in = ParameterIn.HEADER,
    schema = @Schema(type = "string", format = "uuid", example = "123e4567-e89b-12d3-a456-426614174000")
)
public @interface UserIdHeader {
}
