package milo.banking.transfers;

import milo.banking.accounts.entities.Account;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Stateless
public class TransfersService {

	@PersistenceContext
	private EntityManager entityManager;

	public Transfer send(Account senderAccount, Account receiverAccount, BigDecimal amount, String reference) {
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

}
