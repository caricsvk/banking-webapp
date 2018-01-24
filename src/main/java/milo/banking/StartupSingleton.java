package milo.banking;

import milo.banking.accounts.AccountsService;
import milo.banking.accounts.entities.Account;
import milo.banking.accounts.entities.AccountHolder;
import milo.banking.accounts_transfers.AccountsTransferService;
import milo.banking.transfers.Transfer;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.logging.Logger;

@Startup @Singleton
public class StartupSingleton {

	private static final Logger LOGGER = Logger.getLogger(StartupSingleton.class.getName());

	@Inject
	private AccountsTransferService accountsTransferService;
	@Inject
	private AccountsService accountsService;

	@PostConstruct
	private void postConstruct() {
		this.generateData();
	}

	private void generateData() {
		LOGGER.info("StartupSingleton.postConstruct -----------------------------");
		Account duisAccount = accountsService.create(new AccountHolder("Dui", null));
		Account jozefsAccount = accountsService.create(new AccountHolder("Jozef Viskupic", "jozko@viskupic.sk"));
		Account sarkasAccount = accountsService.create(new AccountHolder("Sarka Mala", null));

		LOGGER.info("StartupSingleton.postConstruct + " + jozefsAccount);
		LOGGER.info("StartupSingleton.postConstruct + " + duisAccount);
		LOGGER.info("StartupSingleton.postConstruct + " + sarkasAccount);

		Transfer transferOne = accountsTransferService.makeTransfer("RD63ABNA0443279338",
				duisAccount.getIban(), new BigDecimal(4409), null);
		Transfer tranfserTwo = accountsTransferService.makeTransfer("RD63ABNA0443279338",
				sarkasAccount.getIban(), new BigDecimal(200), null);
		Transfer transferThree = accountsTransferService.makeTransfer(duisAccount.getIban(), sarkasAccount.getIban(),
				new BigDecimal(350), "890427");
		Transfer tranfserFour = accountsTransferService.makeTransfer(sarkasAccount.getIban(),
				"GB63ABNA0479338999", new BigDecimal(97.88), null);

		LOGGER.info("StartupSingleton.postConstruct + " + transferOne);
		LOGGER.info("StartupSingleton.postConstruct + " + tranfserTwo);
		LOGGER.info("StartupSingleton.postConstruct + " + transferThree);
		LOGGER.info("StartupSingleton.postConstruct + " + tranfserFour);
	}
}
