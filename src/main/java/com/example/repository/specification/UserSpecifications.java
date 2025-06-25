package com.example.repository.specification;

import com.example.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {
    
    public static Specification<User> hasName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                "%" + name.toLowerCase() + "%"
            );
    }

    public static Specification<User> hasEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("email")), 
                "%" + email.toLowerCase() + "%"
            );
    }

    public static Specification<User> hasRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("role"), role);
    }

    public static Specification<User> withFilters(String name, String email, String role) {
        return Specification.where(hasName(name))
                .and(hasEmail(email))
                .and(hasRole(role));
    }
}