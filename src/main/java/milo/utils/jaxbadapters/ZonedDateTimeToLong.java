package milo.utils.jaxbadapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeToLong extends XmlAdapter<Long, ZonedDateTime> {

	@Override
	public Long marshal(ZonedDateTime timestamp) {
		return timestamp == null ? null : timestamp.toInstant().toEpochMilli() / 1000;
	}

	@Override
	public ZonedDateTime unmarshal(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp * 1000), ZoneId.systemDefault());
	}

}