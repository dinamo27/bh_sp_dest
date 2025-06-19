package com.example.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;

import com.example.model.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Component
public class UserSpecifications {
    
    public static Specification<User> hasName(String name) {
        return (StringUtils.hasText(name)) 
            ? (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
            : null;
    }
    
    public static Specification<User> hasEmail(String email) {
        return (StringUtils.hasText(email)) 
            ? (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%")
            : null;
    }
    
    public static Specification<User> hasExactEmail(String email) {
        return (email == null) 
            ? (root, query, cb) -> cb.conjunction()
            : (root, query, cb) -> cb.equal(cb.lower(root.get("email")), email.toLowerCase());
    }
    
    public static Specification<User> hasRole(String role) {
        return (StringUtils.hasText(role)) 
            ? (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
                cb.equal(root.get("role"), role)
            : null;
    }
    
    public static Specification<User> hasId(Long id) {
        return (id == null)
            ? (root, query, cb) -> cb.conjunction()
            : (root, query, cb) -> cb.equal(root.get("id"), id);
    }
    
    public static Specification<User> isNotDeleted() {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.or(
                cb.equal(root.get("isDeleted"), false),
                cb.isNull(root.get("isDeleted"))
            );
        };
    }
    
    public static Specification<User> hasAdminRole() {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.equal(root.get("role"), "ADMIN");
        };
    }
    
    public static Specification<User> isActiveAdmin() {
        return Specification.where(isNotDeleted()).and(hasAdminRole());
    }
    
    public static Specification<User> isNotUser(Long userId) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.notEqual(root.get("id"), userId);
        };
    }
    
    public static Specification<User> excludeSensitiveInfo() {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            return cb.conjunction();
        };
    }
    
    public static Specification<User> withSecurityConstraints(Long currentUserId, boolean hasAdminPermission) {
        return (root, query, criteriaBuilder) -> {
            if (hasAdminPermission) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.equal(root.get("id"), currentUserId);
        };
    }
    
    public static Specification<User> filterBy(String name, String email, String role) {
        return Specification.where(hasName(name))
                .and(hasEmail(email))
                .and(hasRole(role));
    }
    
    @SafeVarargs
    public static Specification<User> and(Specification<User>... specifications) {
        Specification<User> result = Specification.where(null);
        for (Specification<User> spec : specifications) {
            result = result.and(spec);
        }
        return result;
    }
}