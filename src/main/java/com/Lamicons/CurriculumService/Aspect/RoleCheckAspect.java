package com.Lamicons.CurriculumService.Aspect;

import com.Lamicons.CurriculumService.Annotation.RequireRole;
import com.Lamicons.CurriculumService.Exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Aspect to enforce role-based access control using @RequireRole annotation.
 * Validates user roles from X-USER-ROLE header before method execution.
 */
@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    private static final String USER_ROLE_HEADER = "X-USER-ROLE";

    @Before("@annotation(com.Lamicons.CurriculumService.Annotation.RequireRole)")
    public void checkRole(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequireRole requireRole = signature.getMethod().getAnnotation(RequireRole.class);
        
        if (requireRole == null) {
            return;
        }

        // Get current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.error("RoleCheckAspect :: No request attributes found");
            throw new UnauthorizedException("Unable to verify user role");
        }

        HttpServletRequest request = attributes.getRequest();
        String userRole = request.getHeader(USER_ROLE_HEADER);

        if (userRole == null || userRole.isEmpty()) {
            log.warn("RoleCheckAspect :: Missing X-USER-ROLE header for {}", signature.getMethod().getName());
            throw new UnauthorizedException("User role not found");
        }

        // Check if user has any of the required roles
        String[] allowedRoles = requireRole.value();
        boolean hasRole = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equalsIgnoreCase(userRole));

        if (!hasRole) {
            log.warn("RoleCheckAspect :: Access denied. User role: {}, Required roles: {}, Method: {}", 
                    userRole, Arrays.toString(allowedRoles), signature.getMethod().getName());
            throw new UnauthorizedException(
                    "Access denied. Required role: " + Arrays.toString(allowedRoles) + ", Your role: " + userRole);
        }

        log.debug("RoleCheckAspect :: Access granted. User role: {}, Method: {}", 
                userRole, signature.getMethod().getName());
    }
}
