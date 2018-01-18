package milo.banking.accounts.dtos;

import milo.utils.jaxbadapters.ZonedDateTimeToLong;
import milo.banking.accounts.entities.Account;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@XmlRootElement
public class AccountBasicDto extends Account {

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

}