package milo.banking.transfers;

import milo.banking.accounts.entities.Account;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@NamedQueries({
		@NamedQuery(name = Transfer.SEARCH, query = "SELECT transfer FROM Transfer transfer" +
				" WHERE transfer.receiverAccount.iban = :iban" +
				" OR transfer.senderAccount.iban = :iban" +
				" ORDER BY transfer.createdOn DESC"),
		@NamedQuery(name = Transfer.SEARCH_FILTERED, query = "SELECT transfer FROM Transfer transfer" +
				" WHERE (transfer.receiverAccount.iban = :iban" +
				" 		OR transfer.senderAccount.iban = :iban)" +
				" AND (LOWER(transfer.reference) LIKE :filter" +
				" 	  OR LOWER(transfer.senderAccount.accountHolder.name) LIKE :filter" +
				"	  OR LOWER(transfer.receiverAccount.accountHolder.name) LIKE :filter)" +
				" ORDER BY transfer.createdOn DESC"),
})
public class Transfer {

	public static final String SEARCH = "Transfer.search";
	public static final String SEARCH_FILTERED = "Transfer.searchFiltered";

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull @Min(0)
	private BigDecimal amount;
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY) @NotNull
	private Account senderAccount;
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY) @NotNull
	private Account receiverAccount;
	protected Timestamp createdOn;
	@Size(max = 255)
	private String reference;

	public Transfer() {
	}

	public Transfer(Account senderAccount, Account receiverAccount, BigDecimal amount, String reference) {
		this.amount = amount;
		this.senderAccount = senderAccount;
		this.receiverAccount = receiverAccount;
		this.reference = reference;
	}

	@PrePersist
	private void prePersist() {
		this.createdOn = new Timestamp(System.currentTimeMillis());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getSenderAccount() {
		return senderAccount;
	}

	public void setSenderAccount(Account fromAccount) {
		this.senderAccount = fromAccount;
	}

	public Account getReceiverAccount() {
		return receiverAccount;
	}

	public void setReceiverAccount(Account toAccount) {
		this.receiverAccount = toAccount;
	}

	public ZonedDateTime getCreatedOn() {
		return createdOn == null ? null :
				ZonedDateTime.ofInstant(createdOn.toInstant(), ZoneId.systemDefault());
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "Transfer{" +
				"id=" + id +
				", amount=" + amount +
				", senderAccount=" + (senderAccount != null ? senderAccount.getIban() : null) +
				", receiverAccount=" + (receiverAccount != null ? receiverAccount.getIban() : null) +
				", createdOn=" + createdOn +
				'}';
	}
}
