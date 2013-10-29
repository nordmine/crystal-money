package ru.nordmine.crystalmoney.db;

public class WhereClauseItem {
	
	private String fieldName;
	private String value;
	private String sign;
	
	public WhereClauseItem(String fieldName, String sign, String value) {	
		this.fieldName = fieldName;
		this.value = value;
		this.sign = sign;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return fieldName + " " + sign + " ?";
	}
}
