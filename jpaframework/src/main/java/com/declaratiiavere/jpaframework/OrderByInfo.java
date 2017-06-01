package com.declaratiiavere.jpaframework;

/**
 * Java bean that encapsulates the field order by information.
 *
 * @author Razvan Dani
 */
public class OrderByInfo {
    private String fieldName;
    private OrderType orderType;

    public OrderByInfo() {
    }

    public OrderByInfo(String fieldName, OrderType orderType) {
        this.fieldName = fieldName;
        this.orderType = orderType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OrderByInfo");
        sb.append("{fieldName='").append(fieldName).append('\'');
        sb.append(", orderType=").append(orderType);
        sb.append('}');
        return sb.toString();
    }
}
