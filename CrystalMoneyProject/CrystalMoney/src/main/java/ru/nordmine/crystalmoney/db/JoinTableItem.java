package ru.nordmine.crystalmoney.db;

public class JoinTableItem {
	
	private String foreignKeyName;
	private String joinTableName;
	
	public JoinTableItem(String foreignKeyName, String joinTableName) {	
		this.foreignKeyName = foreignKeyName;
		this.joinTableName = joinTableName;
	}
	public String getForeignKeyName() {
		return foreignKeyName;
	}
	public void setForeignKeyName(String foreignKeyName) {
		this.foreignKeyName = foreignKeyName;
	}
	public String getJoinTableName() {
		return joinTableName;
	}
	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}
	
}
