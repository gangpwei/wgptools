package com.ali.martini.common;
/*
 * 用于存储resource表中的值,给app_clot_info中取缓存信息用
 */
public class ResourceDo {
    private String type;
    private String name;
    private String value;
    private String ordering;
    private String parent;
    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String value5;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOrdering() {
		return ordering;
	}
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public String getValue3() {
		return value3;
	}
	public void setValue3(String value3) {
		this.value3 = value3;
	}
	public String getValue4() {
		return value4;
	}
	public void setValue4(String value4) {
		this.value4 = value4;
	}
	public String getValue5() {
		return value5;
	}
	public void setValue5(String value5) {
		this.value5 = value5;
	}

	public ResourceDo() {
	}

	public ResourceDo(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public ResourceDo(String name, String value, String value1) {
		this.name = name;
		this.value = value;
		this.value1 = value1;
	}

	public ResourceDo(String name, String value, String value1, String value2) {
		this.name = name;
		this.value = value;
		this.value1 = value1;
		this.value2 = value2;
	}

	public ResourceDo(String name, String value, String value1, String value2, String value3, String value4, String value5) {
		this.name = name;
		this.value = value;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.value5 = value5;
	}
}
