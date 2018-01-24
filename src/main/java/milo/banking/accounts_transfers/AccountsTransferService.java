package milo.banking.accounts_transfers;

import milo.banking.accounts.AccountsService;
import milo.banking.accounts.entities.Account;
import milo.banking.transfers.Transfer;
import milo.banking.transfers.TransfersService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import java.math.BigDecimal;

@Stateless
public class AccountsTransferService {

	@Inject
	private TransfersService transfersService;
	@Inject
	private AccountsService accountsService;

	public Transfer makeTransfer(String senderIban, String receiverIban, BigDecimal amount, String reference) {

		Account senderAccount = this.accountsService.find(senderIban, LockModeType.PESSIMISTIC_WRITE);
		if (senderAccount == null) {
			senderAccount = accountsService.createRemoteAccount(senderIban);
		}
		Account receiverAccount = this.accountsService.find(receiverIban, LockModeType.PESSIMISTIC_WRITE);
		if (receiverAccount == null) {
			receiverAccount = accountsService.createRemoteAccount(receiverIban);
		}

		return transfersService.send(senderAccount, receiverAccount, amount, reference);
	}

}
