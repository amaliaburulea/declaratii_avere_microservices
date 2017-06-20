package com.declaratiiavere.common.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Object comparator.
 *
 * @author Razvan Dani
 */
public class ObjectComparator<T> implements Comparator<T> {
    private List<Method> parameterGetterMethodList;

    /**
     * Constructor for ObjectComparator. Should be used by the getInstance method only.
     *
     * @param classObject       the class of the objects that will be compared
     * @param parameterNameList the parameters which will be used to compare the objects
     */
    public ObjectComparator(Class classObject, List<String> parameterNameList) {
        try {
            parameterGetterMethodList = new ArrayList<>();

            for (String parameterName : parameterNameList) {
                Method parameterGetterMethod = classObject.getMethod("get" + parameterName.substring(0, 1).toUpperCase() + parameterName.substring(1));
                parameterGetterMethodList.add(parameterGetterMethod);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * <p>Compares two objects, as required by the superclass.</p>
     * <p>An object with null value is considered equal to another null object, and less than anything else.</p>
     *
     * @param object1 The first object to be compared
     * @param object2 The second object to be compared
     * @return A negative, zero or positive value if the first item was less, equal or greater than the second
     * @throws ClassCastException If the two objects cannot be compared or the type is not recognized
     */
    public int compare(T object1, T object2) {
        int compareValue = 0;

        try {
            Iterator<Method> iterator = parameterGetterMethodList.iterator();

            while (iterator.hasNext() && compareValue == 0) {
                Method parameterGetterMethod = iterator.next();

                Comparable parameterValue1 = (Comparable) parameterGetterMethod.invoke(object1);
                Comparable parameterValue2 = (Comparable) parameterGetterMethod.invoke(object2);

                if (parameterValue1 == null) {
                    compareValue = parameterValue2 == null ? 0 : -1;
                } else if (parameterValue2 == null) {
                    compareValue = 1;
                } else {
                    compareValue = parameterValue1.compareTo(parameterValue2);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return compareValue;
    }
}
