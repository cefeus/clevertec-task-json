package service.deserialize;

import com.github.drapostolos.typeparser.TypeParser;
import org.apache.commons.lang3.StringUtils;
import util.DateAndTimeFormatter;
import util.ObjectTypizationUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

public class FieldSetter {

    private static final TypeParser parser = TypeParser.newBuilder().build();
    private static final DateAndTimeFormatter formatter = new DateAndTimeFormatter();

    public  static <T> T deserialize(Map<String, Object> parsedMap, Class<T> clazz) {
        return deserializeRec(parsedMap, clazz);
    }

    private static  <T> T deserializeRec(Map<String, Object> parsedMap, Class<T> clazz){
        T root = null;
        try {
            root = clazz.getDeclaredConstructor().newInstance();

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            Class<?> type = field.getType();
            String fieldName = field.getName();
            Type genericType = field.getGenericType();
            Object value = parseField(parsedMap, fieldName, genericType, type);
            field.setAccessible(true);
            setField(value, field, root, clazz);
        }
        return root;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T parseField(Map<String, Object> parsedMap, String fieldName, Type genericType, Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return (T) parseAsCollection(parsedMap, fieldName, genericType, type);
        } else if(Map.class.isAssignableFrom(type))
            return (T) parseAsMap(parsedMap, fieldName, genericType, type);
        else if(isPrimitiveOrWrapper(type) || type.equals(String.class))
            return (T) parseAsPrimitive(parsedMap, fieldName, type);
        else if (ObjectTypizationUtil.isDate(type))
            return (T) parseAsDate(parsedMap, fieldName, type);
        else if(type.equals(UUID.class))
            return parseAsUUID(parsedMap, fieldName, type);
        else if (type.equals(BigInteger.class)) {
            return parseAsBigInt(parsedMap, fieldName, type);
        }else if (type.equals(BigDecimal.class)) {
            return parseAsBigDecimal(parsedMap, fieldName, type);
        }
        else return (T)deserializeRec(parsedMap, type);
    }

    private static <T> T parseAsDate(Map<String, Object> parsedMap, String fieldName, Class<?> type) {
        Object value = getValueFromMap(parsedMap, fieldName);
        return (T) formatter.getFormattedDateTime(value, type);
    }

    private static <T> T parseAsPrimitive(Map<String, Object> parsedMap, String fieldName, Class<?> type) {
        Object value = getValueFromMap(parsedMap, fieldName);
        return (T) convert(type, value);
    }

    private static <T> T parseAsUUID(Map<String, Object> parsedMap, String fieldName, Class<?> type) {
        Object value = getValueFromMap(parsedMap, fieldName);
        return (T) UUID.fromString(value.toString());
    }

    private static <T> T parseAsBigInt(Map<String, Object> parsedMap, String fieldName, Class<?> type) {
        Object value = getValueFromMap(parsedMap, fieldName);
        return (T) new BigInteger(value.toString());
    }

    private static <T> T parseAsBigDecimal(Map<String, Object> parsedMap, String fieldName, Class<?> type) {
        Object value = getValueFromMap(parsedMap, fieldName);
        return (T) new BigDecimal(value.toString());
    }


    private static <T> T parseAsCollection(Map<String, Object> parsedMap, String fieldName, Type genericType, Class<?> type) {
        Collection collection = null;
        try {
            collection = getCollection(type);
            Class<T> collectionType = (Class<T>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
            List<Object> reserve = getValueFromMap(parsedMap, fieldName);
            //Map<String, Object> processed = Map.of(fieldName, reserve);
            if(List.class.isAssignableFrom(collectionType) || Map.class.isAssignableFrom(collectionType))
              reserve = reserve.stream().map(el -> parseField(parsedMap, fieldName, collectionType, collectionType)).collect(Collectors.toList());
            if(isPrimitiveOrWrapper(type) || type.equals(String.class)) {
                for (int i = 0; i < reserve.size(); i++) {
                    Object value = convert(collectionType, reserve.get(i));
                    collection.add(value);
                }
            }
            else
                for (Object object : reserve) {
                    Object value = deserializeRec((Map<String, Object>) object, collectionType);
                    collection.add(value);
                }

            return (T) collection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static <T> T parseAsMap(Map<String, Object> parsedMap, String fieldName, Type genericType, Class<?> type)  {
        Map map = null;
        try {
            map = getMap(type);
        Type actualKeyType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        Class<T> actualValueType = (Class<T>)((ParameterizedType) genericType).getActualTypeArguments()[1];
        Map<String, Object> reserve = getValueFromMap(parsedMap, fieldName);

        for (Map.Entry<String, Object> entry:reserve.entrySet()) {
            Object key = convert(actualKeyType, entry.getKey());
            Object value = isPrimitiveOrWrapper(actualValueType)?
                    convert(actualValueType, entry.getKey())
                    : parseField(reserve, fieldName, genericType, type);
            map.put(key,value);
        }
        return (T) map;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object convert(Type type, Object value) {
        return parser.parseType(value.toString(), type);
    }

    private static Collection getCollection(Class<?> type) throws Exception {
        if (type.isInterface()) {
            if (type.isAssignableFrom(List.class)) {
                return new ArrayList();
            }
            if (type.isAssignableFrom(Set.class)) {
                return new HashSet();
            }
            if (type.isAssignableFrom(Queue.class)) {
                return new ArrayDeque();
            }
           throw new Exception();
        } else {
            return (Collection) type.getDeclaredConstructor().newInstance();
        }
    }

    private static Map getMap(Class<?> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type.isInterface()) {
            return new HashMap();
        } else {
            return (Map) type.getDeclaredConstructor().newInstance();
        }
    }

    private static <T> void setField(Object fieldValue, Field field, T instance, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = field.getName();
        String setter = "set" + StringUtils.capitalize(name);
        Method setMethod = clazz.getDeclaredMethod(setter, field.getType());
        setMethod.invoke(instance, fieldValue);
    }

    private static <T> T getValueFromMap(Map<String, Object> parsedMap, String key) {
        return (T) parsedMap.get(key);
    }

}
