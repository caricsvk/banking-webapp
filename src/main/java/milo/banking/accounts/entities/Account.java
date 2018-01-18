package milo.banking.accounts.entities;

import milo.utils.validators.Iban;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@NamedQueries({
		@NamedQuery(name = Account.GET_OPENED, query = "SELECT account FROM Account account " +
				" WHERE account.archived = false AND account.remote = false ORDER BY account.createdOn DESC"),
		@NamedQuery(name = Account.GET_INTERNAL_IBANS, query = "SELECT account.iban FROM Account account " +
				" WHERE account.remote = false ORDER BY account.createdOn DESC")
})
public class Account {

	public static final String GET_OPENED = "Account.getOpened";
	public static final String GET_INTERNAL_IBANS = "Account.getIbans";

	@Id @Iban
	private String iban;
	@NotNull @Embedded
	private AccountHolder accountHolder;
	private BigDecimal balance;
	protected Timestamp createdOn;
	private boolean archived = false;
	private boolean remote = false;

	public Account() {
	}

	public Account(String iban, AccountHolder accountHolder) {
		this.setIban(iban);
		this.setAccountHolder(accountHolder);
	}

	@PrePersist
	private void prePersist() {
		this.balance = BigDecimal.ZERO;
		this.createdOn = new Timestamp(System.currentTimeMillis());
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public AccountHolder getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(AccountHolder accountHolder) {
		this.accountHolder = accountHolder;
	}

	public ZonedDateTime getCreatedOn() {
		return createdOn == null ? null :
				ZonedDateTime.ofInstant(createdOn.toInstant(), ZoneId.systemDefault());
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	public boolean isUpdatable() {
		return !isArchived() && !isRemote();
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Account{" +
				"iban='" + iban + '\'' +
				", accountHolder=" + accountHolder +
				", createdOn=" + createdOn +
				", archived=" + archived +
				'}';
	}
}
