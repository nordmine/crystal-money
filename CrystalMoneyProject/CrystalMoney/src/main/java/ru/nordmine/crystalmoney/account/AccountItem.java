package ru.nordmine.crystalmoney.account;

import java.math.BigDecimal;

public class AccountItem {
	
	private int id;
	private String name;
	private BigDecimal amount;
	private int iconId;
	private boolean isCard;
	private String comment;
	
	public AccountItem(int id, String name, int icon, BigDecimal amount) {
		super();
		this.id = id;
		this.name = name;
		this.iconId = icon;
		this.amount = amount;
	}
	
	public AccountItem(int id, String name, int iconId, BigDecimal amount,
			boolean isCard, String comment) {
		this(id, name, iconId, amount);
		this.isCard = isCard;
		this.comment = comment;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIconId() {
		return iconId;
	}
	public void setIconId(int icon) {
		this.iconId = icon;
	}

	public boolean isCard() {
		return isCard;
	}

	public void setCard(boolean isCard) {
		this.isCard = isCard;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
