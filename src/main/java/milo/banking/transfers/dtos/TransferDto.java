package milo.banking.transfers.dtos;

import milo.banking.Mappers;
import milo.utils.jaxbadapters.ZonedDateTimeToLong;
import milo.banking.accounts.entities.Account;
import milo.banking.transfers.Transfer;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@XmlRootElement
public class TransferDto extends Transfer {

	@Override @XmlJavaTypeAdapter(ZonedDateTimeToLong.class)
	public ZonedDateTime getCreatedOn() {
		return super.getCreatedOn();
	}

	public void setCreatedOn(ZonedDateTime zonedDateTime) {
		this.createdOn = Timestamp.from(zonedDateTime.toInstant());
	}

	@Override
	public void setSenderAccount(Account fromAccount) {
		super.setSenderAccount(Mappers.ACCOUNT.toAccountBasicDto(fromAccount));
	}

	@Override
	public void setReceiverAccount(Account toAccount) {
		super.setReceiverAccount(Mappers.ACCOUNT.toAccountBasicDto(toAccount));
	}
}