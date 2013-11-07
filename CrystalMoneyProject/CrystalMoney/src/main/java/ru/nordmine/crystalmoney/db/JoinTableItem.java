package ru.nordmine.crystalmoney.db;

public class JoinTableItem {
	
	private String foreignKeyName;
	private String joinTableName;
    private String alias;

    public JoinTableItem(String foreignKeyName, String joinTableName, String alias) {
        this.foreignKeyName = foreignKeyName;
        this.joinTableName = joinTableName;
        this.alias = alias;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
