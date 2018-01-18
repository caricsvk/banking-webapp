package milo.banking.accounts;

import milo.banking.accounts.entities.Account;
import milo.banking.accounts.entities.AccountHolder;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class AccountsService {

	@PersistenceContext
	private EntityManager entityManager;

	public Account create(AccountHolder accountHolder) {
		Account account = new Account(generateNewIban(), accountHolder);
		entityManager.persist(account);
		return account;
	}

	public Account update(String iban, AccountHolder accountHolder) {
		Account account = find(iban);
		if (account == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else if (!account.isUpdatable()) {
			throw new WebApplicationException(Response.Status.GONE);
		}
		account.setAccountHolder(accountHolder);
		entityManager.merge(account);
		return account;
	}

	public Account find(String iban) {
		return entityManager.find(Account.class, iban);
	}

	public List<Account> getAll(int offset, int limit) {
		return entityManager.createNamedQuery(Account.GET_OPENED, Account.class)
				.setFirstResult(offset).setMaxResults(limit).getResultList();
	}

	public void archive(String iban) {
		Account account = find(iban);
		if (account == null || account.isRemote()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		account.setArchived(true);
		entityManager.merge(account);
	}

	public Account createRemoteAccount(String iban) {
		Account account = new Account(iban, new AccountHolder(iban, null));
		account.setRemote(true);
		entityManager.persist(account);
		return account;
	}

	/**
	 * simplified iban generation
	 * @return new iban
	 */
	private String generateNewIban() {
		try {
			String lastIban = entityManager.createNamedQuery(Account.GET_INTERNAL_IBANS, String.class).setMaxResults(1)
					.getSingleResult();
			Integer lastIbanPart = Integer.valueOf(lastIban.substring(lastIban.length() - 6)) + 1;
			String postPart = "" + lastIbanPart;
			while (postPart.length() < 6) {
				postPart = "0" + postPart;
			}
			return lastIban.substring(0, lastIban.length() - 6) + postPart;
		} catch (NoResultException ex) {
			return "SK36REVO00997035000001";
		}
	}
}
