package milo.banking.accounts.dtos;

import milo.banking.accounts.entities.Account;
import milo.banking.transfers.Transfer;
import milo.utils.jaxbadapters.ZonedDateTimeToLong;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@XmlRootElement
public class AccountDto extends Account {

	private Set<Transfer> transfers;

	@Override @XmlTransient
	public boolean isArchived() {
		return super.isArchived();
	}

	@Override @XmlTransient
	public boolean isRemote() {
		return super.isRemote();
	}

	@Override @XmlTransient
	public boolean isUpdatable() {
		return super.isUpdatable();
	}

	@Override @XmlJavaTypeAdapter(ZonedDateTimeToLong.class)
	public ZonedDateTime getCreatedOn() {
		return super.getCreatedOn();
	}

	public void setCreatedOn(ZonedDateTime zonedDateTime) {
		this.createdOn = Timestamp.from(zonedDateTime.toInstant());
	}

	public Set<Transfer> getTransfers() {
		return transfers;
	}

	public void setTransfers(Collection<Transfer> transfers) {
		this.transfers = new TreeSet<>((o, t1) -> {
			long secondsDifference = o.getCreatedOn().toEpochSecond() - t1.getCreatedOn().toEpochSecond();
			return secondsDifference != 0L ? (int) secondsDifference : o.getCreatedOn().getNano() - t1.getCreatedOn().getNano();
		});
		this.transfers.addAll(transfers);
	}

}