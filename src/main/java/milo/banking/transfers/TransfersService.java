package milo.banking.transfers;

import milo.banking.accounts.entities.Account;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class TransfersService {

	@PersistenceContext
	private EntityManager entityManager;

	public Transfer send(Account senderAccount, Account receiverAccount, BigDecimal amount, String reference) {
		this.accountsChecks(senderAccount, receiverAccount, amount);
		senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
		receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
		Transfer transfer = new Transfer(senderAccount, receiverAccount, amount, reference);
		entityManager.persist(transfer);
		return transfer;
	}

	public Transfer find(Long id) {
		return entityManager.find(Transfer.class, id);
	}

	public List<Transfer> searchByIbanAndFilter(String iban, int limit, Integer offset, String filter) {
		if (offset == null) {
			offset = 0;
		}
		TypedQuery<Transfer> namedQuery;
		if (filter != null && !filter.isEmpty()) {
			namedQuery = entityManager.createNamedQuery(Transfer.SEARCH_FILTERED, Transfer.class)
					.setParameter("filter", "%" + filter.toLowerCase() + "%");
		} else {
			namedQuery = entityManager.createNamedQuery(Transfer.SEARCH, Transfer.class);
		}
		return namedQuery.setParameter("iban", iban).setMaxResults(limit).setFirstResult(offset).getResultList();
	}

	private void accountsChecks(Account senderAccount, Account receiverAccount, BigDecimal amount) {
		if (senderAccount.isArchived() || receiverAccount.isArchived()) {
			throw new WebApplicationException(Response.Status.GONE);
		}
		if (senderAccount.isRemote() && receiverAccount.isRemote()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		BigDecimal newSenderBalance = senderAccount.getBalance().subtract(amount);
		if (!senderAccount.isRemote() && newSenderBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new WebApplicationException(
					"AccountsTransferService.makeTransfer failed with low balance " + senderAccount.getIban(),
					Response.status(Response.Status.NOT_ACCEPTABLE).entity("LOW_BALANCE").build()
			);
		}
	}

}
