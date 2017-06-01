package com.declaratiiavere.common.utils;

/**
 * Utilities class.
 *
 * @author Razvan Dani
 */
public class Utilities {

    public static boolean nullSafeEquals(Object object1, Object object2) {
        return (object1 != null && object1.equals(object2)) || (object1 == null && object2 == null);
    }

    public static boolean isEmptyOrNull(String str) {
        return str == null || str.equals("");
    }

}
