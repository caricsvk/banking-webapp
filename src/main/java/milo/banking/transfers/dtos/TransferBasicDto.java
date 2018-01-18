package milo.banking.transfers.dtos;

import milo.utils.jaxbadapters.ZonedDateTimeToLong;
import milo.banking.accounts.entities.Account;
import milo.banking.transfers.Transfer;

import javax.validation.constraints.Null;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@XmlRootElement
public class TransferBasicDto extends Transfer {

	private String senderIban;
	private String receiverIban;
	private Account senderAccount;

	@Override @XmlTransient
	public Account getSenderAccount() {
		return senderAccount;
	}

	@Override @XmlTransient
	public Account getReceiverAccount() {
		return null;
	}

	@Override @XmlTransient
	public void setSenderAccount(Account fromAccount) {
		this.senderAccount = fromAccount;
	}

	@Override @XmlTransient
	public void setReceiverAccount(Account toAccount) {
		super.setReceiverAccount(toAccount);
	}

	@Override @XmlJavaTypeAdapter(ZonedDateTimeToLong.class)
	public ZonedDateTime getCreatedOn() {
		return super.getCreatedOn();
	}

	public void setCreatedOn(ZonedDateTime zonedDateTime) {
		this.createdOn = Timestamp.from(zonedDateTime.toInstant());
	}

	public String getReceiverIban() {
		return receiverIban;
	}

	public void setReceiverIban(String receiverIban) {
		this.receiverIban = receiverIban;
	}

	public String getSenderIban() {
		return senderIban;
	}

	public void setSenderIban(String senderIban) {
		this.senderIban = senderIban;
	}
}