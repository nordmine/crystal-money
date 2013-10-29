package ru.nordmine.crystalmoney.trx;


public class TransactionItem {

	private int id;
	private String comment;
	private Double amount;
	private int accountId;
	private long created;

	// read-only fields
	private String categoryName;
	private int accountIconId;

	// for save operation in dao
	private int transactionType;
	private int categoryId;

	public TransactionItem(int id, String comment, int accountId,
			Double amount, long created) {
		super();
		this.id = id;
		this.comment = comment;
		this.amount = amount;
		this.accountId = accountId;
		this.created = created;
	}

	public TransactionItem(int id, String comment, int accountId,
			Double amount, long created, int accountIconId,
			int transactionType, int categoryId, String categoryName) {
		this(id, comment, accountId, amount, created);
		this.categoryName = categoryName;
		this.accountIconId = accountIconId;
		this.transactionType = transactionType;
		this.categoryId = categoryId;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String name) {
		this.comment = name;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int iconId) {
		this.accountId = iconId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getAccountIconId() {
		return accountIconId;
	}

	public void setAccountIconId(int accountIconId) {
		this.accountIconId = accountIconId;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

}
