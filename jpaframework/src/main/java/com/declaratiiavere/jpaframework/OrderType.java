package com.declaratiiavere.jpaframework;

/**
 * Enum for order type.
 *
 * @author Razvan Dani
 */
public enum OrderType {
    ASCENDING("ASC"),    //order type ascending
    DESCENDING("DESC");    //order type descending

    private String text;

    OrderType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
