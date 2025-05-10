package com.user.user_service.shared.infrastructure.constant;

import java.util.List;

import lombok.Getter;

@Getter
public enum FilterTypeAllowed {

    EQUALS("eq","equals", new String[]
        {"string", "double", "boolean", "date", "datetime", "long", "int", "byte", "short", "float", "char", "enum"}),
    NOT_EQUALS("ne", "notEquals", new String[]
        {"string", "double", "boolean", "date", "datetime", "long", "int", "byte", "short", "float", "char", "enum"}),
    GREATER_THAN("gt", "greaterThan", new String[]
        {"double", "date", "datetime", "long", "int", "byte", "short", "float", "char"}),
    GREATER_THAN_OR_EQUALS("ge","greaterThanOrEquals", new String[]
        {"double", "date", "datetime", "long", "int", "byte", "short", "float", "char"}),
    LESS_THAN("lt","lessThan", new String[]
        {"double", "date", "datetime", "long", "int", "byte", "short", "float", "char"}),
    LESS_THAN_OR_EQUALS("le","lessThanOrEquals", new String[]
        {"double", "date", "datetime", "long", "int", "byte", "short", "float", "char"}),
    LIKE("like", "like", new String[]
        {"string", "double", "boolean", "date", "datetime", "long", "int", "byte", "short", "float", "char", "enum"});

    private final String shortValue;
    private final String description;
    private final String[] allowedField;

    private FilterTypeAllowed(String shortValue, String description, String[] allowedField) {
        this.shortValue = shortValue;
        this.description = description;
        this.allowedField = allowedField;
    }

    public List<String> getAllShortValues(){
        return List.of(values()).stream()
                .map(FilterTypeAllowed::getShortValue)
                .toList();
    }
}
