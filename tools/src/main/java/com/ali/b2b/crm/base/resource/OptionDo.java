package com.ali.b2b.crm.base.resource;

/**
 * 值对:key/value.
 * 一般用于页面下拉框资源
 * @author jinbo.yu
 *
 */
public class OptionDo  {
    String key;
    String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OptionDo() {
    }

    public OptionDo(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
