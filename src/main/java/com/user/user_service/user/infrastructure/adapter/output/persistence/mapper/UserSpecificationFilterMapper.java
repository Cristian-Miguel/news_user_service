package com.user.user_service.user.infrastructure.adapter.output.persistence.mapper;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.user.user_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public class UserSpecificationFilterMapper {

    //eq: equals, ne: not equals, gt: greater than, lt: less than, ge: greater than or equal to, le: less than or equal to
    //filter types: like, eq, ne, gt, lt, ge, le
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Specification<UserEntity> buildFilter(String field, String type, String value) {
        return (root, query, cb) -> {
            Path<?> path = root.get(field);
            switch (type) {
                case "eq": return cb.equal(path, castValue(path, value));
                case "ne": return cb.notEqual(path, castValue(path, value));
                case "like":
                    String escaped = value
                            .replace("\\", "\\\\")
                            .replace("_", "\\_")
                            .replace("%", "\\%");

                    String pattern = "%" + escaped + "%";

                    System.out.println("Generated LIKE pattern: " + pattern);

                    return cb.like(cb.lower((Path<String>) path), cb.literal(pattern));
                case "gt": return cb.greaterThan((Path<Comparable>) path, (Comparable) castValue(path, value));
                case "lt": return cb.lessThan((Path<Comparable>) path, (Comparable) castValue(path, value));
                case "ge": return cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) castValue(path, value));
                case "le": return cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) castValue(path, value));
                default: return null;
            }
        };
    }

    private static Object castValue(Path<?> path, String value) {
        Class<?> type = path.getJavaType();
        if (type == String.class) return value;
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(value);
        if (type == LocalDate.class) return LocalDate.parse(value);
        if (type == Date.class) return Date.valueOf(value);
        if (type == Integer.class) return Integer.valueOf(value);
        if (type == Long.class) return Long.valueOf(value);
        if (type == Double.class) return Double.valueOf(value);
        if (type == Float.class) return Float.valueOf(value);
        if (type == Short.class) return Short.valueOf(value);
        if (type == Byte.class) return Byte.valueOf(value);
        if (type == Character.class) return value.charAt(0);
        if (type.isEnum()) return Enum.valueOf((Class<Enum>) type, value.toUpperCase());
        
        return value;
    }

    public static Specification<UserEntity> getUserByRole(String role, Long enterpriseId) {
        return (Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if ("admin".equalsIgnoreCase(role)) {
                return cb.conjunction(); // no filter, admin can see all
            } else if ("new_enterprise".equalsIgnoreCase(role)) {
                // assume you have a field in UserEntity like "enterpriseId"
                return cb.equal(root.get("adminUser"), enterpriseId);
            }
            return cb.disjunction(); // in case role doesn't match, return nothing
        };
    }
}
