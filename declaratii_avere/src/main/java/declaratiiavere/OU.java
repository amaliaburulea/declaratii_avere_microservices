package declaratiiavere;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Array;


/**
 * The name of this class is short because it contains static utility methods that
 * can be called from anywhere in the system, and the calls should be easy and
 * quick to write in the code. OU stands for Object Utils, as among other things,
 * there are methods to convert wrapper objects to primitive types that take
 * care of the case when the object passed in is null.<p>
 * <p/>
 * This can be used as a place for other such very generic methods that are
 * needed every now and then almost anywhere in the system.<p>
 *
 * @author jmattsso
 */
public class OU {
    static final Integer ZERO = 0;
    static final Integer ONE = 1;
    static final String ONE_STRING = "1";
    private static final int DEFAULT_BIG_DECIMAL_SCALE = 4;
    private static final String END_OF_LINE_CHARS = "\n";
    private static final String HTML_TAG_BR = "<br>";
    static final String TRUE_STRING = "true";
    static String DUPLICATE_WORD_FIND_PATTERN = "(\\b\\w+(?:[\\s\\p{Punct}]+\\w+)*)[\\s\\p{Punct}]+\\1";
    static String DUPLICATE_WORD_REPLACE_PATTERN = "$1";

    private static ConcurrentHashMap<String, Field> fieldByClassAndFieldNameMap = new ConcurrentHashMap<String, Field>();


    /**
     * Converts an Integer to a boolean. Only the Integer value of 1 means true.
     *
     * @param value Integer to convert
     * @return True, if the Integer had a value of 1
     */
    public static boolean booleanValue(Integer value) {
        return ONE.equals(value);
    }

    /**
     * Converts an String to a boolean. Only the String value of "1" means true.
     *
     * @param value String to convert
     * @return True, if the String had a value of "1"
     */
    public static boolean booleanValue(String value) {
        return ONE_STRING.equals(value);
    }

    /**
     * Converts an Integer to an int. If the Integer is null, -1 is returned.
     *
     * @param iInt Integer to convert
     * @return The value as int
     */
    public static int intValue(Integer iInt) {
        return intValue(iInt, -1);
    }

    /**
     * Converts an Integer to an int. If the Integer is null, the default value
     * specified as def is returned.
     *
     * @param iInt Integer to convert
     * @param def  Default value to return when iInt is null
     * @return The value as int
     */
    public static int intValue(Integer iInt, int def) {
        if (iInt == null) {
            return def;
        }
        return iInt;
    }

    /**
     * Converts a boolean to an int.
     *
     * @param b  The boolean to be converted
     * @return   The value as int
     */
    public static int intValue(boolean b) {
        return b ? 1 : 0;
    }

    /**
     * Converts a String to an int. If the String is empty or cannot be converted -1 is returned.
     *
     * @param s  The String to be converted
     * @return   The value as int
     */
    public static int intValue(String s) {
        return intValue(s, -1);
    }

    /**
     * Converts a String to an int. If the String is empty or cannot be converted the default is returned.
     *
     * @param s    The String to be converted
     * @param def  The default value to be used
     * @return     The value as int
     */
    public static int intValue(String s, int def) {
        int returnedValue = def;
        if (s != null){
            try {
                returnedValue =  Integer.parseInt(s);
            } catch (NumberFormatException ignore) {
                returnedValue = def;
            }
        }

        return returnedValue;
    }

    /**
     * Converts a Double to a double. If the Double is null, Double.NaN
     * (Not-a-Number) is returned.
     *
     * @param dDbl Double to convert
     * @return The value as double
     */
    public static double doubleValue(Double dDbl) {
        return doubleValue(dDbl, Double.NaN);
    }

    /**
     * Converts a Double to a double. If the Double is null, the default value
     * specified as def is returned.
     *
     * @param dDbl Double to convert
     * @param def  The default value to return when dDbl is null
     * @return The value as double
     */
    public static double doubleValue(Double dDbl, double def) {
        if (dDbl == null) {
            return def;
        }
        return dDbl;
    }

    /**
     * Converts a String to a double. If the String is empty or cannot be converted -1.0 is returned.
     *
     * @param s The String to be converted
     * @return The value as double
     */
    public static double doubleValue(String s) {
        return doubleValue(s, -1.0);
    }

    /**
     * Converts a String to a double. If the String is empty or cannot be converted the default is returned.
     *
     * @param s   The String to be converted
     * @param def The default value to be used
     * @return The value as double
     */
    public static double doubleValue(String s, double def) {
        double returnedValue = def;
        if (s != null) {
            try {
                returnedValue = Double.parseDouble(s);
            } catch (NumberFormatException ignore) {
                returnedValue = def;
            }
        }

        return returnedValue;
    }

    /**
     * Converts a String to an long. If the String is empty or cannot be converted -1 is returned.
     *
     * @param s The String to be converted
     * @return The value as long
     */
    public static long longValue(String s) {
        return longValue(s, -1);
    }

    /**
     * Converts a String to an long. If the String is empty or cannot be converted the default is returned.
     *
     * @param s   The String to be converted
     * @param def The default value to be used
     * @return The value as long
     */
    public static long longValue(String s, long def) {
        long returnedValue = def;
        if (s != null) {
            try {
                returnedValue = Long.parseLong(s);
            } catch (NumberFormatException ignore) {
                returnedValue = def;
            }
        }

        return returnedValue;
    }

    /**
     * Returns the string itself if non-null, and an empty string ("") when the
     * String passed in is null.
     *
     * @param str The string passed in
     * @return The string passed in if it's non-null, or "" otherwise
     */
    public static String null2e(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * Returns the String itself if non-null or not equal to string "null", and an empty string ("") when the
     * String passed in is null or equal to String "null".
     *
     * @param str The string passed in
     * @return The string passed in if it's non-null and not equal to "null", or "" otherwise
     */
    public static String nullString2e(String str) {
        return nullString2e(str, "");
    }

    /**
     * Returns the String itself if non-null or not equal to string "null", and the default value when the
     * String passed in is null or equal to String "null".
     *
     * @param str The string passed in
     * @return The string passed in if it's non-null and not equal to "null", or default value otherwise
     */
    public static String nullString2e(String str, String def) {
        if ((str == null) || (str.equals("null"))) {
            return def;
        }
        return str;
    }

    /**
     * Returns the string itself if non-null and not empty, or null if the
     * passed in String is null or empty.
     *
     * @param str The string passed in
     * @return The string passed in if it's non-null and not empty,
     *         or null otherwise
     */
    public static String e2null(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        return str;
    }

    /**
     * Returns the collection itself if non-null and not empty, or null if the
     * passed in collection is null or empty.
     *
     * @param collection The collection passed in
     * @return The collection itself if non-null and not empty, or null otherwise.
     */
    public static <T extends Collection<?>> T e2null(T collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return collection;
    }

    /**
     * Returns the map itself if non-null and not empty, or null if the
     * passed in map is null or empty.
     *
     * @param map The map passed in
     * @return The map itself if non-null and not empty, or null otherwise.
     */
    public static <T extends Map<?, ?>> T e2null(T map) {

        if (map == null || map.isEmpty()) {
            return null;
        }

        return map;
    }

    /**
     * Converts an integer to string, padding with zeros in front to make a
     * string of at least requested numDigits characters.
     *
     * @param value     Integer value to convert
     * @param numDigits Number of characters the resulting String should have
     * @return Zero-padded String representation of value
     */
    public static String zeroPadInt(int value, int numDigits) {
        String strInt = Integer.toString(value);
        return padString(strInt, numDigits, '0');
    }

    /**
     * Pads a String by adding one or more of the specified padding character
     * to the LEFT side of the string so that the length of the string is
     * at least numChars characters.
     *
     * @param str       String to pad
     * @param padChar   The character to use for padding
     * @return Padded String
     */
    public static String padString(String str, int numChars, char padChar) {
        if (str.length() >= numChars) {
            return str;
        }
        StringBuffer buf = new StringBuffer(numChars);
        for (int i = 0; i < numChars - str.length(); i++) {
            buf.append(padChar);
        }
        buf.append(str);
        return buf.toString();
    }

    /**
     * Pads a String by adding one or more of the specified padding character
     * to the RIGHT side of the string so that the length of the string is
     * at least numChars characters.
     *
     * @param str       String to pad
     * @param padChar   The character to use for padding
     * @return Padded String
     */
    public static String rightPadString(String str, int numChars, char padChar) {
        if (str.length() >= numChars) {
            return str;
        }
        StringBuffer buf = new StringBuffer(numChars);
        buf.append(str);
        for (int i = 0; i < numChars - str.length(); i++) {
            buf.append(padChar);
        }
        return buf.toString();
    }

    /**
     * <p>Replaces all occurences of a substring with another substring.</p>
     * <p>Returns the original string if any of the parameters is null.</p>
     *
     * @param s  The original string
     * @param s1 The substring to be replaced
     * @param s2 The new substring which is to be inserted
     * @return A new string, with all occurences of s1 replaced with s2
     */
    public static String strReplace(String s, String s1, String s2) {
        if (s == null || s1 == null || s2 == null) return s;
        int pos;
        int searchFrom = 0;
        while ((pos = s.indexOf(s1, searchFrom)) >= 0) {
            s = s.substring(0, pos) + s2 + s.substring(pos + s1.length());
            searchFrom = pos + s2.length();
        }
        return s;
    }

    /**
     * Converts the end of line characters from a given text to &lt;br&gt;.<p/>
     *
     * @param textToFormat  The text to format
     * @return  The text conatining the end of line chars replaced with &lt;br&gt;
     */
    public static String formatHtmlText(String textToFormat) {

        String formattedText = null;

        if (textToFormat != null) {
            formattedText = textToFormat.replaceAll(END_OF_LINE_CHARS, HTML_TAG_BR);
        }

        return formattedText;
    }

    /**
     * <p>Divides a string into substrings, separated by a given separator string.</p>
     *
     * @param s         The string to be divided
     * @param delimiter The separator string
     * @return A string array containing the substrings
     */
    public static String[] strSplit(String s, String delimiter) {
        return strSplit(s, delimiter, -1);
    }

    /**
     * <p>Divides a string into substrings, separated by a given delimiter string. Delimiter can contain multiple characters.</p>
     * <p>The maximum number of substrings can be specified, in which case the last element of the
     * returned array may contain further separator instances. Empty Strings are included in the results,
     * so for example if s = "a;;b;" amd delimiter=";", the the returned elements with be "a", "", "b", "".</p>
     *
     * @param s         The string to be divided
     * @param delimiter The separator string
     * @param maxCount  Maximum number of elements returned, negative number means no limitation
     * @return A string array, containing 0..maxCount elements (if maxCount is not negative)
     */
    public static String[] strSplit(String s, String delimiter, int maxCount) {
        java.util.List<String> list = new java.util.ArrayList<String>();
        if (s != null && delimiter != null && maxCount != 0) {
            while (s.length() > 0 && maxCount != 0) {
                int pos = s.indexOf(delimiter);
                if (maxCount == 1) {
                    list.add(s);
                } else {
                    if (pos < 0) {
                        list.add(s);
                        s = "";
                    } else {
                        list.add(s.substring(0, pos));
                        s = s.substring(pos + delimiter.length());

                        if (s.equals("")) {
                            list.add("");
                        }
                    }
                }

                if (maxCount > 0) {
                    maxCount--;
                }
            }
        } else {
            list.add(s);
        }
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * <p>Divides a string into substrings, separated by a given delimiter string. Delimiter can contain multiple characters.</p>
     * <p>The maximum number of substrings can be specified, in which case the last element of the
     * returned array may contain further separator instances. Empty Strings are included in the results,
     * so for example if s = "a;;b;" amd delimiter=";", the the returned elements with be "a", "", "b", "".</p>
     *
     * @param s               The string to be divided
     * @param delimiter       The separator string
     * @param maxCount        Maximum number of elements returned, negative number means no limitation
     * @param maxSubStrLenght Maximum number of the substring length
     * @return A string array, containing 0..maxCount elements (if maxCount is not negative)
     */
    public static String[] strSplit(String s, String delimiter, int maxCount, int maxSubStrLenght) {
        java.util.List<String> list = new java.util.ArrayList<String>();
        if (s != null && delimiter != null && maxCount != 0) {
            while (s.length() > 0 && maxCount != 0) {
                if (maxCount == 1) {
                    s = OU.truncate(s, maxSubStrLenght);
                    list.add(trim(s));
                } else {
                    String subStr = OU.truncate(s, maxSubStrLenght);
                    boolean isLastSubStr = s.substring(Math.min(subStr.length(), maxSubStrLenght)).length() == 0;
                    int lastIndexOfDelimiter = subStr.lastIndexOf(delimiter) + 1;

                    if (lastIndexOfDelimiter < 0 || isLastSubStr) {
                        list.add(subStr);
                        s = s.substring(Math.min(subStr.length(), maxSubStrLenght));
                    } else {
                        list.add(trim(s.substring(0, lastIndexOfDelimiter)));
                        s = s.substring(lastIndexOfDelimiter);
                    }

                }

                if (maxCount > 0) {
                    maxCount--;
                }
            }
        } else {
            s = OU.truncate(s, maxSubStrLenght);
            list.add(s);
        }

        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * Compares two Objects using the equals method, but also handles the case when
     * one or both of the objects are null. If both are null, true is returned (i.e. two
     * nulls are considered equal), if only one of them is null, false is returned.
     *
     * @param o1 First object to compare
     * @param o2 Second object to compare
     * @return True if objects are equal or both null, false otherwise
     */
    public static boolean nullSafeEquals(Object o1, Object o2) {
        return o1 == null && o2 == null || o1 != null && o1.equals(o2);
    }


    /**
     * Compares two Comparable objects using the compareTo method, but also handles the case when
     * one or both of the objects are null. The second object provided MUST be comparable with the first object.
     * If both are null, true is returned (i.e. two
     * nulls are considered equal -> 0), if only one of them is null, -1 is returned.
     *
     * @param comparable1 First comparable object to compare
     * @param comparable2 First comparable object object to compare. The second Object MUST be comparable with the first object
     * @return 0 if objects are considered equal by the compareTo method or both null, -1 otherwise
     */
    public static <S extends Comparable, T extends Comparable<S>> int nullSafeCompare(T comparable1, S comparable2) {
        if (comparable1 == null && comparable2 == null) {
            return 0;
        }
        if (comparable1 == null) {
            return 1;
        }
        if (comparable2 == null) {
            return -1;
        }

        try {
            return comparable1.compareTo(comparable2);
        } catch (ClassCastException e) {
            return -1;
        }
    }

    /**
     * Truncates a String to a specified length. If the input string is shorter or equal
     * to the specified length, it is returned unchanged. If input string is null, null
     * is returned.
     *
     * @param str Input string
     * @param len Length to truncate to
     * @return Truncated string
     */
    public static String truncate(String str, int len) {
        if (str == null) {
            return null;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * Truncates a String to a specified length, cutting from the beginning of the
     * string if necessary. If the input string is shorter or equal to the specified
     * length, it is returned unchanged. If input string is null, null is returned.
     *
     * @param str Input string
     * @param len Length to truncate to
     * @return Truncated string
     */
    public static String truncateLeft(String str, int len) {
        if (str == null) {
            return null;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * Converts a collection of Integers to an int array (int[]).
     *
     * @param coll Collection of Integers
     * @return int array
     */
    public static int[] toIntArray(Collection<Integer> coll) {
        if (coll == null) {
            return null;
        }
        if (coll.size() == 0) {
            return new int[0];
        }
        int[] ints = new int[coll.size()];
        int ndx = 0;
        for (Integer in : coll) {
            ints[ndx++] = in;
        }
        return ints;
    }

    /**
     * Converts a collection of Integers to an Integer array (Integer[]).
     *
     * @param coll Collection of Integers
     * @return Integer array
     */
    public static Integer[] toIntegerArray(Collection<Integer> coll) {
        if (coll == null) {
            return null;
        }
        if (coll.size() == 0) {
            return new Integer[0];
        }
        Integer[] integers = new Integer[coll.size()];
        int ndx = 0;
        for (Integer in : coll) {
            integers[ndx++] = in;
        }
        return integers;
    }

    /**
     * Converts an int array into a List of Integers.
     * In case of null array, an empty list is returned
     *
     * @return a List of Integers
     */
    public static List<Integer> toIntegerList(int[] intArray) {
        List<Integer> integerList = new ArrayList<Integer>();
        if (intArray != null) {
            for (int intVal : intArray) {
                integerList.add(intVal);
            }
        }
        return integerList;
    }

    /**
     * Converts a String array into a List of Integers (converts the first numberOfElements items).
     * In case of null array, an empty list is returned.
     *
     * @param stringArray      an array of String (actually containing int values)
     * @return                 a List of Integers
     */
    public static List<Integer> toIntegerListFromStringArray(String[] stringArray) {
        return toIntegerListFromStringArray(stringArray, null);
    }

    /**
     * Converts a String array into a List of Integers (converts the first numberOfElements items).
     * In case of null array, an empty list is returned.
     * In case of a null numberOfElements, all the array will be converted to a list.
     *
     * @param stringArray      - an array of String (actually containing int values)
     * @param numberOfElements - the number of elements that should be convert into a List
     * @return a List of Integers
     */
    public static List<Integer> toIntegerListFromStringArray(String[] stringArray, Integer numberOfElements) {
        List<Integer> integerList = null;
        Integer numberOfElementsToCopy;
        if (stringArray != null && stringArray.length > 0) {
            numberOfElementsToCopy = (numberOfElements == null) ? stringArray.length : numberOfElements;
            integerList = new ArrayList<Integer>();
            for (int i = 0; i < numberOfElementsToCopy; i++) {
                integerList.add(OU.intValue(stringArray[i]));
            }
        }
        return integerList;
    }

    /**
     * Converts an Integer list to a String array
     * @param integerList
     * @return
     */
    public static String[] toStringArrayFromIntegerList(List<Integer> integerList) {
        String[] stringArray = null;

        if (e2null(integerList) != null ){
            stringArray = new String[integerList.size()];
            int index = 0;
            for (Integer integerValue : integerList) {
                stringArray[index++] = Integer.toString(OU.intValue(integerValue));
            }
        }
        return stringArray;
    }

    /**
     * Null safe min of two BigDecimals
     */
    public static BigDecimal nullSafeMin(BigDecimal d1, BigDecimal d2) {
        if (d1 == null) {
            return d2;
        }
        if (d2 == null) {
            return d1;
        }
        return d1.min(d2);
    }

    /**
     * Null safe max of two BigDecimals
     */
    public static BigDecimal nullSafeMax(BigDecimal d1, BigDecimal d2) {
        if (d1 == null) {
            return d2;
        }
        if (d2 == null) {
            return d1;
        }
        return d1.max(d2);
    }

    /**
     * Null safe min of two Comparable objects of same type, if any of the objects are null then the other one is returned. If both
     * are null then null is returned.
     *
     * @param comparable1   The first object to compare
     * @param comparable2   The second object to compare
     * @return              The min value
     */
    public static <T extends Comparable<T>> T nullSafeMin(T comparable1, T comparable2) {
        T compareResult;

        if (comparable1 == null) {
            compareResult = comparable2;
        } else if (comparable2 == null) {
            compareResult = comparable1;
        } else {
            if (comparable1.compareTo(comparable2) < 0) {
                compareResult = comparable1;
            } else {
                compareResult = comparable2;
            }
        }

        return compareResult;
    }

    /**
     * Null safe max of two Comparable objects of same type, if any of the objects are null then the other one is returned. If both
     * are null then null is returned.
     *
     * @param comparable1   The first object to compare
     * @param comparable2   The second object to compare
     * @return              The min value

     */
    public static <T extends Comparable<T>> T nullSafeMax(T comparable1, T comparable2) {
        T compareResult;

        if (comparable1 == null) {
            compareResult = comparable2;
        } else if (comparable2 == null) {
            compareResult = comparable1;
        } else {
            if (comparable1.compareTo(comparable2) > 0) {
                compareResult = comparable1;
            } else {
                compareResult = comparable2;
            }
        }

        return compareResult;
    }

    /**
     * Null safe scale
     */
    public static BigDecimal setNullSafeScale(BigDecimal d, int scale, int roundingMode) {

        if (d == null) return null;

        return d.setScale(scale, roundingMode);
    }

    /**
     * Merges two int arrays whose elements are ordered in ascending order.
     * If the same int value is encountered in both lists, it is inserted only once
     * in the merged array - so this is like a union of two sets in int arrays.
     *
     * @param mergeTo
     * @param mergeFrom
     * @return
     */
    public static int[] mergeOrderedIntArrays(int[] mergeTo, int[] mergeFrom) {
        if (mergeFrom == null) {
            return mergeTo;
        }
        if (mergeTo == null) {
            return mergeFrom;
        }
        int additions = 0;
        int i = 0;
        int j = 0;
        while (i < mergeTo.length || j < mergeFrom.length) {
            if (i < mergeTo.length && j < mergeFrom.length && mergeTo[i] == mergeFrom[j]) {
                i++;
                j++;
            } else if (j >= mergeFrom.length || (i < mergeTo.length && mergeTo[i] < mergeFrom[j])) {
                i++;
            } else {
                j++;
                additions++;
            }
        }
        if (additions == 0) {
            return mergeTo;
        }

        int[] merged = new int[mergeTo.length + additions];
        int k = 0;
        for (i = 0, j = 0; i < mergeTo.length || j < mergeFrom.length;) {
            if (i < mergeTo.length && j < mergeFrom.length && mergeTo[i] == mergeFrom[j]) {
                merged[k++] = mergeTo[i++];
                j++;
            } else if (j >= mergeFrom.length || (i < mergeTo.length && mergeTo[i] < mergeFrom[j])) {
                merged[k++] = mergeTo[i++];
            } else {
                merged[k++] = mergeFrom[j++];
            }
        }
        return merged;
    }


    /**
     * Wraps text to lines.
     *
     * @param text       The text to be wrapped to multiple lines
     * @param lineLenght The lenght of one line
     * @return List of Strings representing the text wrapped to lines
     */
    public static List<String> wrapTextToLines(String text, int lineLenght) {
        return wrapTextToLines(text, lineLenght, -1);
    }
    /**
     * Wraps text to lines.
     *
     * @param text       The text to be wrapped to multiple lines
     * @param lineLenght The lenght of one line
     * @param maxLines   The maximum number of lines, if not limit is needed, use version of method that does
     *                   not have this param
     * @return List of Strings representing the text wrapped to lines
     */
    public static List<String> wrapTextToLines(String text, int lineLenght, int maxLines) {
        if (text == null) {
            return null;
        }

        List<String> linesList = new ArrayList<String>();

        String remainingText = text;
        remainingText = remainingText.replaceAll("\\s+\r\n","\r\n");  // replaces spaces followed by carriage return with just carriage return
        int spaceOffset = 0;
        int prevSpaceOffset;
        int lineBreakOffset;

        while ((remainingText.length() > lineLenght || remainingText.indexOf("\r\n") > 0)
                && (maxLines <= 0 || linesList.size() < maxLines)) {
            prevSpaceOffset = spaceOffset;
            spaceOffset = remainingText.indexOf(' ', prevSpaceOffset + 1);

            if (spaceOffset == -1) {
                spaceOffset = remainingText.length();
            }

            lineBreakOffset = remainingText.indexOf("\r\n", prevSpaceOffset + 1);

            if (lineBreakOffset > -1 && lineBreakOffset < spaceOffset && lineBreakOffset < lineLenght) {
                linesList.add(remainingText.substring(0, lineBreakOffset));
                remainingText = remainingText.substring(lineBreakOffset + 2);
                spaceOffset = 0;
            } else if (spaceOffset  > lineLenght) {
                if (prevSpaceOffset > 0) {
                    linesList.add(remainingText.substring(0, prevSpaceOffset));
                    remainingText = remainingText.substring(prevSpaceOffset + 1);
                } else {
                    linesList.add(remainingText.substring(0, lineLenght));
                    remainingText = remainingText.substring(lineLenght);
                }

                spaceOffset = 0;
            }
        }

        if (maxLines <= 0 || linesList.size() < maxLines) {
            linesList.add(remainingText);
        }

        return linesList;
    }

    public static String stripPhoneNumber(String phoneNumber) {
        String newPhone = null;
        if (phoneNumber != null) {
            newPhone = phoneNumber.replaceAll("[\\-\\(\\)\\+\\s]", "");
        }
        return newPhone;
    }

    /**
     * Returns a String representing the values from the List separated by be specified delimiter. The Objects
     * in the List can can be of any type and the toString() will be used when construction the reult.
     *  This method is nullSafe - any null elements of this list are treated as empty strings
     * <p>Example:
     * <blockquote><pre>
     * considering a list with the following elemnts
     * ["Mono", null, "Beethoven", null, "Rammstein", "Andain"]
     * and a delimitter ";"
     * the result will be the following String:
     * "Mono;;Beethoven;;Rammstein;Andain"
     * </pre></blockquote>
     * Also if the delimiter if is null, will be ignored.
     *
     * @param list      the list containing the list that must be separated with the specified delimiter
     * @param delimiter the delimiter that should be used in order to separate the values
     * @return          the delimiter that will be used to separtate the list's elements
     */
    public static String listToDelimiterSeparatedValues(Collection<?> list, String delimiter) {
        String elementSeparator = null;

        return listToDelimiterSeparatedValues(list, delimiter, elementSeparator);
    }

    /**
     * Has the same behaviour as the other listToDelimiterSeparatedValues method except that each element in eclosed using
     * the specified elementSeparator, e.g. a returned String would look like 'a','b' if the list contains a and b, the delimiter
     * is comma and the element separator is '.
     *
     * @param list              the list containing the list that must be separated with the specified delimiter
     * @param delimiter         the delimiter that should be used in order to separate the values
     * @param elementSeparator  the element separator user for enclosing the elements, null or empty means no enclosing is used
     * @return                  the delimiter that will be used to separtate the list's elements
     */
    public static String listToDelimiterSeparatedValues(Collection<?> list, String delimiter, String elementSeparator) {
        String tokenizedString = null;

        if (list != null) {
            StringBuffer tokenizedStringBuffer = new StringBuffer();

            for (Object currentObject : list) {
                if (tokenizedStringBuffer.length() > 0 && delimiter != null) {
                    tokenizedStringBuffer.append(delimiter);
                }

                tokenizedStringBuffer.append(OU.null2e(elementSeparator));
                tokenizedStringBuffer.append(currentObject == null ? "" : currentObject.toString());
                tokenizedStringBuffer.append(OU.null2e(elementSeparator));
            }

            tokenizedString = tokenizedStringBuffer.toString();
        }

        return tokenizedString;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String null2zero(String str) {
        if (str == null) {
            return "0";
        }
        return str;
    }

    /**
     * Returns an Integer equal to zero if the integer parameter is null, otherwise returns the integer.
     *
     * @param integer   The Integer object
     * @return          An integer equal to zero if the integer parameter is null, otherwise the integer
     */
    public static Integer nullToZero(Integer integer) {
        return (integer == null) ? 0 : integer;
    }

    /**
     * Returns an Double equal to zero if the double parameter is null, otherwise returns the double.
     *
     * @param doubleNo   The Double object
     * @return          An double equal to zero if the double parameter is null, otherwise the double
     */
    public static Double nullToZero(Double doubleNo) {
        return (doubleNo == null) ? 0 : doubleNo;
    }

    /**
     * Returns an BigDecimal equal to zero if the BigDecimal parameter is null, otherwise returns the BigDecimal.
     *
     * @param bigDecimal   The BigDecimal object
     * @return          A bigDecimal equal to zero if the bigDecimal parameter is null, otherwise the bigDecimal
     */
    public static BigDecimal nullToZeroBD(BigDecimal bigDecimal) {
        return (bigDecimal == null) ? new BigDecimal(0) : bigDecimal;
    }

    /**
     * Returns an BigDecimal equal to zero if the string parameter is null, otherwise returns the BigDecimal.
     *
     * @param str The String object that need to be converted to BigDecimal
     * @return A bigDecimal equal to zero if the str parameter is null, otherwise the bigDecimal
     */
    public static BigDecimal nullToZeroBD(String str) {
        return (OU.e2null(str) == null) ? new BigDecimal(0) : new BigDecimal(str);
    }

    /**
     * Returns an Boolean equal to false if the Boolean parameter is null, otherwise returns the boolean.
     *
     * @param booleanValue   The Boolean object
     * @return          An Boolean equal to zero if the integer parameter is null, otherwise the integer
     */
    public static Boolean nullToFalse(Boolean booleanValue) {
        return (booleanValue == null) ? false : booleanValue;
    }

    /**
     *
     * @param str - the String from which will remove the substring str and all the characters after it
     * @param subStr - the substring after will look for in the initial String
     * @return - if the substring argument occurs as a substring within the String
     * returns a substring of the initial String. The substrings begins with the first character and
     * extends to the  index of the rightmost occurrence of the specified argument subStr
     * If the substring argument doesn't occur as a substring within the String str
     * returns the original String
     */

    public static String removeSubStringFromEnd(String str, String subStr) {
        if (str == null) {
            return str;
        }

        int index = str.lastIndexOf(subStr);
        if (index != -1) {
            str = str.substring(0, index);
        }
        return str;
    }

    /**
     * <p>Converts an array into the format of {a; b; c}, where a,b,c are the String representations
     * of the array's elements.</p>
     *
     * @param array   Array of Objects
     * @return        String representation of the array's elements
     */
    public static String arrayToStr(Object[] array) {
        String s = "{";
        if (array != null) {
            for (int i=0; i<array.length; i++) {
                s += (i == 0 ? "" : "; ") + array[i];
            }
        }
        s += "}";
        return s;
    }

    /**
     * <p>Converts an int array into the format of {1; 2; 3}, where 1,2,3 are the String representations
     * of the array's elements.</p>
     *
     * @param array   Array of Objects
     * @return        String representation of the array's elements
     */
    public static String intArrayToStr(int[] array) {
        String s = "{";
        if (array != null) {
            for (int i=0; i<array.length; i++) {
                s += (i == 0 ? "" : "; ") + array[i];
            }
        }
        s += "}";
        return s;
    }

    /**
     * <p>Converts an int array into the format of 1,2,3  using as delimiter the param no acollades in this one!
     * of the array's elements.</p>
     * @param array
     * @param delimiter
     * @return
     */
    public static String intArrayToStr(int[] array, String delimiter) {
        String s = "";
        if (array != null) {
            for (int i=0; i<array.length; i++) {
                s += (i == 0 ? "" : delimiter) + array[i];
            }
        }
        return s;
    }

    /**
     * converts a string that contains number deleimited by delimiter to an array of ints
     * @param str
     * @param delimiter
     * @return int[]
     */
    public static int[] strToIntArray(String str, String delimiter) {
        int[] array ;
        if (e2null(str) == null) {
            array = new int[0];
        }else{
            String[] arrayStr = str.split(delimiter);
            array = new int[arrayStr.length];
            for (int i=0; i<arrayStr.length; i++) {
                array[i] = Integer.parseInt(arrayStr[i]);
            }
        }
        return array;
    }

    /**
     * converts a string that contains number deleimited by delimiter to an array of doubles
     * @param str
     * @param delimiter
     * @return int[]
     */
    public static double[] strToDoubleArray(String str, String delimiter) {
        double[] array ;
        if (e2null(str) == null) {
            array = new double[0];
        }else{
            String[] arrayStr = str.split(delimiter);
            array = new double[arrayStr.length];
            for (int i=0; i<arrayStr.length; i++) {
                array[i] = Double.parseDouble(arrayStr[i]);
            }
        }
        return array;
    }


    /**
     * compares two int arrays if they contain the same set of ints
     * @param a1
     * @param a2
     * @return
     */
    public static boolean sameSetOfInts(int[] a1, int[] a2){
        Set s1 = new HashSet();
        s1.addAll(OU.toIntegerList(a1));
        Set s2 = new HashSet();
        s2.addAll(OU.toIntegerList(a2));
        return s1.equals(s2);
    }
    /**
     * <p>Converts a boolean array into the format of {true; false; fasee}, where true,false,false are
     * the String representations of the array's elements.</p>
     *
     * @param array   Array of Objects
     * @return        String representation of the array's elements
     */
    public static String booleanArrayToStr(boolean[] array) {
        String s = "{";
        if (array != null) {
            for (int i=0; i<array.length; i++) {
                s += (i == 0 ? "" : "; ") + array[i];
            }
        }
        s += "}";
        return s;
    }



    /**
     * Returns trimmed string or empty string if the original string is null.
     *
     * @param str   The original string
     * @return      The trimmed string
     */
    public static String null2eTrim(String str) {
        return null2e(str).trim();
    }

    /**
     * Returns trimmed string or null if the original string is null.
     *
     * @param str   The original string
     * @return      The trimmed string
     */
    public static String trim(String str) {
        return (str != null) ? str.trim() : null;
    }

    /**
     * If the Integer is zero returns null, otherwise returns the original Integer.
     *
     * @param integer   The Integer object
     * @return          null if the integer is zero, otherwise the original integer.
     */
    public static Integer zeroToNull(Integer integer) {
        return (integer != null && integer.equals(ZERO)) ? null : integer;
    }

    /**
     * If the bigDecimal is zero returns null, otherwise returns the original bigDecimal.
     *
     * @param bigDecimal   The BigDecimal object
     * @return          null if the bigDecimal is zero, otherwise the original bigDecimal.
     */
    public static BigDecimal zeroToNull(BigDecimal bigDecimal) {
        return (bigDecimal != null && bigDecimal.compareTo(BigDecimal.ZERO) == 0) ? null : bigDecimal;
    }

    /**
     * Compares the 2 BigDecimal numbers after scaling them to the DEFAULT_BIG_DECIMAL_SCALE.
     * If a different scale is needed, the overloaded method can be used.
     *
     * The method is also null safe.
     *
     * @param firstDecimalNumber      The first BigDecimal object
     * @param secondDecimalNumber     The second BigDecimal object
     * @return                        the result of the comparison
     */
    public static int scaleSafeCompare(BigDecimal firstDecimalNumber, BigDecimal secondDecimalNumber) {
        return scaleSafeCompare(firstDecimalNumber, secondDecimalNumber, DEFAULT_BIG_DECIMAL_SCALE);
    }

    /**
     * Compares the 2 BigDecimal numbers after scaling them to the specified scale.
     *
     * The method is also null safe.
     *
     * @param firstDecimalNumber      The first BigDecimal object
     * @param secondDecimalNumber     The second BigDecimal object
     * @param scale                   The scale
     * @return                        the result of the comparison
     */
    public static int scaleSafeCompare(BigDecimal firstDecimalNumber, BigDecimal secondDecimalNumber, int scale) {
        int comparisonResult;

        if (firstDecimalNumber == null && secondDecimalNumber == null) {
            comparisonResult = 0;
        } else if (firstDecimalNumber == null) {
            comparisonResult = -1;
        } else if (secondDecimalNumber == null) {
            comparisonResult = 1;
        } else {
            comparisonResult = firstDecimalNumber.setScale(scale, BigDecimal.ROUND_HALF_EVEN).compareTo(secondDecimalNumber.setScale(scale));
        }

        return comparisonResult;
    }

    /**
     * Compares the 2 BigDecimal numbers.
     *
     * The method is also null safe.
     *
     * @param firstDecimalNumber      The first BigDecimal object
     * @param secondDecimalNumber     The second BigDecimal object
     * @return                        True if both BigDecimal are null or have the same value (even if the scale is different)
     */
    public static boolean safeEqual(BigDecimal firstDecimalNumber, BigDecimal secondDecimalNumber) {
        boolean areEqual;

        areEqual = (firstDecimalNumber == null && secondDecimalNumber == null)
                || (firstDecimalNumber != null && secondDecimalNumber != null && (firstDecimalNumber.compareTo(secondDecimalNumber) == 0));

        return areEqual;
    }

    /**
     * Compares the 2 Strings as BigDecimal numbers.
     *
     * The method is also null safe.
     *
     * @param firstDecimalNumber      The first BigDecimal object as String
     * @param secondDecimalNumber     The second BigDecimal object as String
     * @return                        True if both BigDecimal are null or have the same value (even if the scale is different)
     */
    public static boolean safeEqualFromDecimalStrings(String firstDecimalNumber, String secondDecimalNumber) {
        boolean areEqual;

        areEqual = (OU.e2null(firstDecimalNumber) == null && OU.e2null(secondDecimalNumber) == null)
                || (OU.e2null(firstDecimalNumber) != null && OU.e2null(secondDecimalNumber) != null &&
                (new BigDecimal(firstDecimalNumber).compareTo(new BigDecimal(secondDecimalNumber)) == 0));

        return areEqual;
    }

    public static String strNullUnicode(String str)
    {
        if (str != null) {
            return "N'" + str.replaceAll("\'", "''") + "'";
        } else {
            return "null";
        }
    }

    /**
     * This method replaces every occurence of ' with two '. This is useful before giving as parameter to a sql , a field
     * which contains '
     *
     * @param str input string
     * @return modified string
     */
    public static String strNullReplaceApostrophe(String str) {
        String modifiedString;

        if (str != null && str.trim().length() > 0) {
            modifiedString = str.replaceAll("\'", "''");
        } else {
            modifiedString = null;
        }

        return modifiedString;
    }

    public static boolean nullSafeContains(Object[] objects, Object searchedObj){
        if(objects == null){
            return false;
        }
        List list = Arrays.asList(objects);
        return list.contains(searchedObj);
    }

    /**
     * Converts a string array into string using as delimiter, delimited by delimiter param
     * @param in - array of String
     * @param delimiter - the delimiter String
     * @return String
     */
    public static String listToStringDelimited(String[] in, String delimiter) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; in != null && i < in.length; i++) {
            if (i > 0) {
                out.append(delimiter);
            }
            out.append(in[i] == null ? "" : in[i]);
        }
        return out.toString();
    }

    /**
     * Returns the enum value coresponding to the given enum string for the specified enum class.
     * If the sepcified enum class does not contain any enum with the name matching the enum string
     * null is returned.
     *
     * @param enumString the enum string name
     * @param enumClass the enum class
     * @return the enum value coresponding to the given enum string for the specified enum class, or null if none exists
     */
    public static <T extends Enum<T>> T enumValue(String enumString, Class<T> enumClass){
        T enumValue = null;

        try{
            if (enumClass != null && OU.e2null(enumString) != null){
                enumValue = Enum.valueOf(enumClass, enumString);
            }
        } catch(IllegalArgumentException ex){
            //the provided value is not a valid value for the enum class so null will be returned
        }

        return enumValue;
    }

    /**
     * Converts a list of string's into a list of integer's
     * @param stringList - list containing String values
     * @return a list of Integer
     */
    public static List<Integer> stringListToIntegerList(List<String> stringList) {
        List<Integer> integerList = new ArrayList<Integer>();

        for (String stringElement : stringList) {
            integerList.add(intValue(stringElement));
        }

        return integerList;
    }

    /**
     * Converts a list of integer's into a list of string's
     *
     * @param integerList - list containing Integer values
     * @return a list of Strings
     */
    public static List<String> integerListToStringList(List<Integer> integerList) {
        List<String> stringList = new ArrayList<String>();

        if (e2null(integerList) != null) {
            for (Integer integerElement : integerList) {
                if (integerElement != null) {
                    stringList.add(integerElement.toString());
                }
            }
        }

        return stringList;
    }

    /**
     * Extracts all the digits from a String.
     *
     * @param str The string containing the digits and also other characters
     * @return    A string containing only digits
     */
    public static String getDigitsFromString(String str) {

        if (str == null || str.length() == 0) {
            return null;
        }

        String emailRegEx = "[^\\d]";           //any character not digit
        Pattern pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(str);

        return matcher.replaceAll("");   //removes all the non-digit chars
    }

    /**
     * Parses the creditcard number supplied as parameter and takes out the spaces, the "." and "-" characters.
     *
     * @param creditCardNumber the creditcard number to be parsed
     * @return the new creditcard number without the spaces, the "." and "-" characters
     */
    public static String stripCreditCardNumber(String creditCardNumber) {
        String newCreditCardNumber = null;

        if (creditCardNumber != null) {
            newCreditCardNumber = creditCardNumber.replaceAll("[\\s.-]", "");
        }

        return newCreditCardNumber;
    }

    /**
     * concat 2 ByteArrays
     *
     * @param b1
     * @param b2
     * @return the concat of the two byte arrays
     */
    public static byte[] concatByteArrays(byte[] b1,byte[] b2){
        byte[] b = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, b, 0, b1.length);
        System.arraycopy(b2, 0, b, b1.length, b2.length);
        return  b;
    }

    /**
     * Return an encoded credit card: the last digits are displayed and first digits are  replaced with *
     * @param creditCardNumber            the credit card number
     * @param numberOfDigists             the number of digits displayed from the credit card,
     * @return    the encoded credit card.
     */
    public static String encodeCreditCardNumberDigits(String creditCardNumber, int numberOfDigists) {
        String creditCardNumberEncoded = creditCardNumber;

        if (creditCardNumber != null && numberOfDigists < creditCardNumber.length()) {
            creditCardNumberEncoded = creditCardNumber.substring(0, creditCardNumber.length() - numberOfDigists).replaceAll("\\d", "*")
                    + creditCardNumber.substring(creditCardNumber.length() - numberOfDigists);
        }

        return creditCardNumberEncoded;
    }

    /**
     * Remove all zero decimals after . from end of big decimal number, if such exist
     *
     * @param bigDecimalValue the big decimal number
     * @return the new big decimal number without unusefull 0 decimals from end
     */
    public static BigDecimal removeZeroDecimalsFromEndOfBD(BigDecimal bigDecimalValue) {
        String bigDecimalString = OU.nullToZeroBD(bigDecimalValue).toString();

        while (bigDecimalString.endsWith("0") && bigDecimalString.indexOf(".") > 0) {
            bigDecimalString = bigDecimalString.substring(0, bigDecimalString.length() - 1);
        }

        if (bigDecimalString.endsWith(".")) {
            bigDecimalString = bigDecimalString.substring(0, bigDecimalString.length() - 1);
        }

        return new BigDecimal(bigDecimalString);
    }

    /**
     * make parent folders for a file
     * @param filePath
     */
    public static void makeDirsForFile(String filePath){
        String parentFolder = filePath.substring(0, filePath.lastIndexOf("\\"));
        makeDirs(parentFolder);
    }

    /**
     * create missing parentfolders for a path
     * @param path
     */
    public static void makeDirs(String path){
        File parentFile = new File(path);

        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

    /**
     * Method creates a LinkedHashSet containing elements from the array of string
     * @param arrayString - array to be tranfored in set
     * @return - a LinkedHashSet
     */
    public static Set<Integer> toSetIntegerFromStringArray(String[] arrayString) {
        Set<Integer> setInteger = new LinkedHashSet<Integer>();
        if (arrayString != null) {
            for (String stringElement : arrayString) {
                setInteger.add(OU.intValue(stringElement));
            }
        }
        return setInteger;
    }

    /**
     * Method creates a LinkedHashSet containing elements from the array of string
     * @param arrayString - array to be tranfored in set
     * @return - a LinkedHashSet
     */
    public static Set<String> toSetStringFromStringArray(String[] arrayString) {
        Set<String> setInteger = new LinkedHashSet<String>();
        if (arrayString != null) {
            for (String stringElement : arrayString) {
                setInteger.add(stringElement);
            }
        }
        return setInteger;
    }

    public static String getFirstNotNull(String str1, String str2){
        return (str1==null?str2:str1);
    }
    public static String getFirstNotNull(String str1, String str2, String str3){
        return getFirstNotNull(getFirstNotNull(str1,str2),str3);
    }
    public static String getFirstNotNull(String str1, String str2, String str3, String str4){
        return getFirstNotNull(getFirstNotNull(str1,str2,str3),str4);
    }

    /**
     * Method returns true if the price is between (minPrice, maxPrice).
     *
     * @param price - the price that is compared; if (price = null) will be considered that it is in the min/max limit
     * @param minPrice - min price limit, can be null = means there is no min price limit
     * @param maxPrice - max price limit, can be null = means there is no max price limit
     * @return - true or false wheter the price is bettwen limits or not
     */
    public static boolean priceInInterval(BigDecimal price, BigDecimal minPrice, BigDecimal maxPrice) {
        boolean isInInterval = false;

        if ((price == null) || (((minPrice == null) || minPrice.compareTo(price) <= 0)
                && (maxPrice == null || price.compareTo(maxPrice)<=0))) {
            isInInterval = true;
        }
        return isInInterval;
    }

    /**
     * This method splits the given List of Objects into the given <code>subListSize</code> number of elements.
     * Returns null if the number of sublists is not a positive number or if the list is null<p/>
     *
     * @param elementList       The set of hotel sourcekeys
     * @param numberOfSubLists  The number of sublist
     * @return                  A set containig all the sublists of hotels
     */
    public static <T> Set<List<T>> splitListInNumberOfSublists(List<T> elementList, int numberOfSubLists) {

        Set<List<T>> subListSet = new HashSet<List<T>>();

        if (elementList != null) {
            int sizeOfSmallSublist = elementList.size() / numberOfSubLists;
            int sizeOfLargeSublist = sizeOfSmallSublist + 1;
            int numberOfLargeSublists = elementList.size() % numberOfSubLists;
            int numberOfSmallSublists = numberOfSubLists - numberOfLargeSublists;

            int numberOfElementsHandled = 0;

            for (int i = 0; i < numberOfSubLists; i++) {
                int size = i < numberOfSmallSublists ? sizeOfSmallSublist : sizeOfLargeSublist;
                List<T> sublist = elementList.subList(numberOfElementsHandled, numberOfElementsHandled + size);

                if (!sublist.isEmpty()) {
                    subListSet.add(sublist);
                }

                numberOfElementsHandled += size;
            }
        }

        return subListSet;
    }

    /**
     * This method splits the input list of elements in multiple lists which will be added in a set.<p/>
     *
     * @param list                 The list of objects that need to be splited
     * @param maxElementsInSubList This indicates how much elements the resulted lists will have
     * @return A <code>Set</code> containig lists of elements
     */
    public static Set<List> splitListInSublistsWithMaxNumberOfElements(List list, int maxElementsInSubList) {

        Set<List> resultSet = new HashSet<List>();

        if (OU.e2null(list) != null) {
            if (list.size() > maxElementsInSubList) {

                Integer sublistNumber = list.size() / maxElementsInSubList;

                for (int i = 0; i < sublistNumber; i++) {
                    resultSet.add(list.subList(i * maxElementsInSubList, (i + 1) * maxElementsInSubList));
                }

                if (sublistNumber * maxElementsInSubList != list.size()) {
                    resultSet.add(list.subList(sublistNumber * maxElementsInSubList, list.size()));
                }
            } else {
                resultSet.add(list);
            }
        }

        return resultSet;
    }

    /**
     * this method checks with case insensitive if a string ends with the specified suffix
     * @param str - string to check
     * @param suffix - suffix to check
     * @return - boolean
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {

        if (str == null || suffix == null) {
            return false;
        }

        if (str.endsWith(suffix)) {
            return true;
        }

        if (str.length() < suffix.length()) {
            return false;
        } else {
            return str.toLowerCase().endsWith(suffix.toLowerCase());
        }
    }

    /**
     * this method checks with case insensitive if a string starts with the specified prefix
     * @param str - string to check
     * @param prefix - prefix to check
     * @return - boolean
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {

        if (str == null || prefix == null) {
            return false;
        }

        if (str.startsWith(prefix)) {
            return true;
        }

        if (str.length() < prefix.length()) {
            return false;
        } else {
            return str.toLowerCase().startsWith(prefix.toLowerCase());
        }
    }

    /**
     * this method removes all the non-alphanumeric characters
     * @param str - string to be processed
     * @return - string containing only alphanumeric characters
     */
    public static String removeNonAlphanumericCharacters(String str){
        if (str != null){
            str.replaceAll("[^A-Za-z0-9 ]", "");
        }
        return str;
    }

    /**
     * Converts an String to a boolean. Only the String value of "true" means true.
     *
     * @param value String to convert
     * @return True, if the String had a value of "true"
     */
    public static boolean booleanValueFromString (String value) {
        boolean result = false;
        if (e2null(value) != null){
            result = TRUE_STRING.equalsIgnoreCase(value);
        }
        return result;
    }

    /**
     * Returns the Field object associted with the specified field name of an object.
     *
     * @param object    The object
     * @param fieldName The field name
     * @return          The Field object
     */
    public static Field getField(Object object, String fieldName) {
        Field field;
        boolean fieldFound = false;
        Class objectClass = object.getClass();

        String cacheKey = objectClass.getName() + "_" + fieldName;
        field = fieldByClassAndFieldNameMap.get(cacheKey);

        if (field == null) {
            while (!fieldFound && objectClass.getSuperclass() != null) {
                try {
                    field = objectClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    fieldFound = true;
                } catch (NoSuchFieldException nfe) {
                    objectClass = objectClass.getSuperclass();
                }
            }

            if (field != null) {
                fieldByClassAndFieldNameMap.put(cacheKey, field);
            }
        }

        return field;
    }

    /**
     * Returns the value for the specified field of an object. The field name can be nested name. If any but the last
     * of the names that are part of a chain is a collection, the the first element of the collection is used.
     *
     * @param object    The object
     * @param fieldName The field name
     * @return The field value
     */
    public static Object getFieldValue(Object object, String fieldName) {
        Object fieldValue;

        try {
            if (object == null) {
                return null;
            }

            if (object instanceof Collection || object instanceof Object[]) {
                Iterator objectCollectionIterator = null;

                if (object instanceof Collection) {
                    objectCollectionIterator= ((Collection) object).iterator();
                } else if (object instanceof Object[]){
                    objectCollectionIterator = Arrays.asList((Object[]) object).iterator();
                }

                if (objectCollectionIterator != null && objectCollectionIterator.hasNext()) {
                    Object firstCollectionObject = objectCollectionIterator.next();
                    return getFieldValue(firstCollectionObject, fieldName);
                } else {
                    return null;
                }
            }

            String currentFieldName;

            if (fieldName.indexOf(".") > -1) {
                currentFieldName = fieldName.substring(0, fieldName.indexOf("."));
            } else {
                currentFieldName = fieldName;
            }

            Object currentFieldValue = null;
            Field currentField = getField(object, currentFieldName);

            if (currentField != null) {
                if (currentField.get(object) instanceof Collection && !currentFieldName.equals(fieldName)) {
                    Iterator collectionIterator = ((Collection) currentField.get(object)).iterator();

                    if (collectionIterator.hasNext()) {
                        currentFieldValue = collectionIterator.next();
                    }
                } else {
                    currentFieldValue = currentField.get(object);
                }
            }

            if (currentFieldValue != null) {
                if (fieldName.equals(currentFieldName)) {
                    fieldValue = currentFieldValue;
                } else {
                    String remainingFieldName = fieldName.substring(currentFieldName.length() + 1);
                    fieldValue = getFieldValue(currentFieldValue, remainingFieldName);
                }
            } else {
                fieldValue = null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fieldValue;
    }

    /**
     * Removes duplicate consecutive words or group of words from the given input String.<p/>
     * e.g.: STANDARD SINGLE ROOM.RACK RATE RACK RATE. becomes --> STANDARD SINGLE ROOM.RACK RATE.
     *
     * @param inputString   String to process
     * @return              The input string that hav the duplicate words or group of words removed
     */
    public static String removeDuplicateConsecutiveWords(String inputString) {

        String outputString = inputString;
        Boolean continueReplace = Boolean.TRUE;

        while (OU.e2null(outputString) != null && continueReplace) {
            String currentResult = outputString.replaceAll(DUPLICATE_WORD_FIND_PATTERN, DUPLICATE_WORD_REPLACE_PATTERN);

            if (currentResult.equals(outputString)) {
                continueReplace = Boolean.FALSE;
            }

            outputString = currentResult;
        }

        return outputString;
    }
}

