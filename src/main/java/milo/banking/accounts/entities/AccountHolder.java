package milo.banking.accounts.entities;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class AccountHolder {

	public AccountHolder() {
	}

	public AccountHolder(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@NotNull @NotEmpty
	private String name;
	@Email
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "AccountHolder{" +
				"name='" + name + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}
