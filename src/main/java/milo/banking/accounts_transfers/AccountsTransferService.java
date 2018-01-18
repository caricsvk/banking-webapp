package milo.banking.accounts_transfers;

import milo.banking.accounts.AccountsService;
import milo.banking.accounts.entities.Account;
import milo.banking.transfers.Transfer;
import milo.banking.transfers.TransfersService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Stateless
public class AccountsTransferService {

	@Inject
	private TransfersService transfersService;
	@Inject
	private AccountsService accountsService;

	public Transfer makeTransfer(String senderIban, String receiverIban, BigDecimal amount, String reference) {

		Account senderAccount = this.accountsService.find(senderIban);
		if (senderAccount == null) {
			senderAccount = accountsService.createRemoteAccount(senderIban);
		}
		updateAccount(senderAccount, senderAccount.getBalance().subtract(amount));

		Account receiverAccount = this.accountsService.find(receiverIban);
		if (receiverAccount == null) {
			receiverAccount = accountsService.createRemoteAccount(receiverIban);
		}
		updateAccount(receiverAccount, receiverAccount.getBalance().add(amount));

		if (senderAccount.isRemote() && receiverAccount.isRemote()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return transfersService.send(senderAccount, receiverAccount, amount, reference);
	}

	private void updateAccount(Account account, BigDecimal newBalance) {
		if (account.isArchived()) {
			throw new WebApplicationException(
					Response.status(Response.Status.GONE).entity("GONE " + account.getIban()).build()
			);
		}
		if (!account.isRemote() && newBalance.compareTo(BigDecimal.ZERO) < 0) {
			throw new WebApplicationException(
					"AccountsTransferService.makeTransfer failed with low balance " + account.getIban(),
					Response.status(Response.Status.NOT_ACCEPTABLE).entity("LOW_BALANCE").build()
			);
		}
		account.setBalance(newBalance);
	}

}
