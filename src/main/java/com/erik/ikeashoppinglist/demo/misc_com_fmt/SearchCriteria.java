package com.erik.ikeashoppinglist.demo.misc_com_fmt;

public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
    private String match_type;  // and, or, not

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getMatch_type() {
        return match_type;
    }

    public void setMatch_type(String match_type) {
        this.match_type = match_type;
    }

    public SearchCriteria(String key, String operation, Object value, String match_type) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.match_type = match_type;
    }

    public SearchCriteria() {
    }
}
