package com.example.repository.specification;

import com.example.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

public final class UserSpecification {
    
    private UserSpecification() {
        // Private constructor to prevent instantiation
    }
    
    public static Specification<User> nameContains(String name) {
        return StringUtils.hasText(name) ? 
            (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    "%" + name.toLowerCase() + "%"
                )
            : null;
    }
    
    public static Specification<User> emailContains(String email) {
        return StringUtils.hasText(email) ? 
            (root, query, criteriaBuilder) -> 
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), 
                    "%" + email.toLowerCase() + "%"
                )
            : null;
    }
    
    public static Specification<User> hasRole(String role) {
        return StringUtils.hasText(role) ? 
            (root, query, criteriaBuilder) -> 
                criteriaBuilder.equal(root.get("role"), role)
            : null;
    }
    
    public static Specification<User> filterBy(String name, String email, String role) {
        return Specification.where(nameContains(name))
            .and(emailContains(email))
            .and(hasRole(role));
    }
}