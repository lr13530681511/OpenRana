package com.LR.openRana.utils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JpaUtils {

    /**
     * 使用反射获取对象中的非空字段及其值。
     * @param entity 对象实例。
     * @return 包含非空字段和其值的映射。
     */
    public static Map<String, Object> getNonEmptyFieldsByReflection(Object entity) {
        Map<String, Object> nonEmptyFields = new LinkedHashMap<>();

        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true); // 允许访问私有字段
                Object value = field.get(entity);
                if (value != null) {
                    if (value instanceof String && ((String) value).isEmpty()) continue; // 跳过空字符串
                    nonEmptyFields.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return nonEmptyFields;
    }

    /**
     * 构建根据实体对象的非空字段进行模糊搜索的Specification。
     *
     * @param entity 实体对象。
     * @param <T>    实体类型。
     * @return Specification<T> 对象。
     * <p>
     * 需要使用请在repository里面增加继承JpaSpecificationExecutor<Entity>：
     */
    public static <T> Specification<T> buildSpecificationForFuzzySearch(T entity) {
        Map<String, Object> requestFields = getNonEmptyFieldsByReflection(entity);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, Object> entry : requestFields.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                if (fieldValue != null) {
                    if (fieldValue instanceof String) {
                        Predicate predicate = cb.like(root.get(fieldName).as(String.class), "%" + fieldValue + "%");
                        predicates.add(predicate);
                    } else if (fieldValue instanceof Boolean) {
                        Predicate predicate = cb.equal(root.get(fieldName), fieldValue);
                        predicates.add(predicate);
                    } else {
                        // 对于非字符串和非布尔类型的字段，可以继续使用cb.equal()进行精确匹配
                        Predicate predicate = cb.equal(root.get(fieldName), fieldValue);
                        predicates.add(predicate);
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
