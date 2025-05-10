package com.user.user_service.shared.infrastructure.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FilterValueParse {

    public static Object parse(String value, String field, Class<?> classObject) {
        if (value == null || value.isEmpty()) return null;

        String type = findFieldByClass(classObject, field);

        return switch (type.toLowerCase()) {
            case "string"     -> value;
            case "int"        -> Integer.parseInt(value);
            case "long"       -> Long.parseLong(value);
            case "double"     -> Double.parseDouble(value);
            case "bigdecimal" -> new BigDecimal(value);
            case "char"       -> value.charAt(0);
            case "date"       -> LocalDate.parse(value);
            case "datetime"   -> LocalDateTime.parse(value);
            case "enum"       -> parseEnum(value, field, classObject); // You’ll need enum-specific logic here.
            case "boolean"    -> Boolean.parseBoolean(value);
            case "byte"       -> Byte.parseByte(value);
            case "short"      -> Short.parseShort(value);
            case "float"      -> Float.parseFloat(value);
            default -> throw new IllegalArgumentException("Unsupported filter value type: " + type);
        };
    }

    @SuppressWarnings("unchecked")
    public static Object parseEnum(String value, String field, Class<?> classObject) {
        try {
            Class<?> enumClass = classObject.getDeclaredField(field).getType();
            return Enum.valueOf((Class<Enum>) enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for enum field: " + field + ", value: " + value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String findFieldByClass(Class<?> classObject, String field) {
        try{
            if(classObject.getDeclaredField(field).getType().isEnum()){
                return "enum";
            } else {
                return classObject.getDeclaredField(field).getType().getSimpleName();
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
