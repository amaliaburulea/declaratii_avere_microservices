package com.declaratiiavere.demnitarservice;

import java.math.BigDecimal;

/**
 * Created by razvan.dani on 10.05.2017.
 */
public class X {
    public static void main(String[] args) {
        System.out.println("new BigDecimal(\"3,245\") = " + new BigDecimal("3,245.12".replaceAll(",", "")));
    }
}
