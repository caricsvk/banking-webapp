package milo.banking.accounts;

import milo.banking.Mappers;
import milo.banking.accounts.dtos.AccountBasicDto;
import milo.banking.accounts.dtos.AccountDto;
import milo.banking.accounts.entities.Account;
import milo.banking.accounts.entities.AccountHolder;
import milo.banking.transfers.TransfersService;
import milo.banking.transfers.dtos.TransferBasicDto;
import milo.utils.validators.Iban;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/accounts") @Stateless @TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountsResource {

	@Inject
	private AccountsService accountsService;
	@Inject
	private TransfersService transfersService;
	@Inject
	private Mappers.AccountMapper accountMapper;
	@Inject
	private Mappers.TransferMapper transferMapper;

	@POST
	public AccountBasicDto create(@Valid AccountHolder accountHolder) {
		Account newAccount = accountsService.create(accountHolder);
		return accountMapper.toAccountBasicDto(newAccount);
	}

	@PUT @Path("{iban}")
	public AccountBasicDto update(@PathParam("iban") @Iban String iban, @Valid AccountHolder accountHolder) {
		Account updatedAccount = accountsService.update(iban, accountHolder);
		return accountMapper.toAccountBasicDto(updatedAccount);
	}


	@DELETE @Path("{iban}")
	public Response delete(@PathParam("iban") @Iban String iban) {
		accountsService.archive(iban);
		return Response.noContent().build();
	}

	@GET @Path("{iban}") @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Response read(@PathParam("iban") @Iban String iban,
						 @QueryParam("withTransfers") @DefaultValue("false") boolean withTransfers) {
		Account account = accountsService.find(iban, LockModeType.NONE);
		if (account == null || account.isRemote()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else if (account.isArchived()) {
			return Response.status(Response.Status.GONE).build();
		}
		if (withTransfers) {
			AccountDto accountDto = accountMapper.toAccountDto(account);
			accountDto.setTransfers(
					transfersService.searchByIbanAndFilter(iban, 50, null, null).stream()
							.map(transferMapper::toTransferBasicDto).collect(Collectors.toList())
			);
			return Response.ok(accountDto).build();
		} else {
			return Response.ok(accountMapper.toAccountBasicDto(account)).build();
		}
	}

	@GET @Path("{iban}/transfers") @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Response read(@PathParam("iban") @Iban String iban,
						 @QueryParam("filter") String filter,
						 @QueryParam("offset") @DefaultValue("0") int offset,
						 @QueryParam("limit") @DefaultValue("50") @Min(1) @Max(500) int limit) {
		Account account = accountsService.find(iban, LockModeType.NONE);
		if (account == null || account.isRemote()) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} else if (account.isArchived()) {
			return Response.status(Response.Status.GONE).build();
		}
		List<TransferBasicDto> transfers = transfersService.searchByIbanAndFilter(iban, limit, offset, filter).stream()
				.map(transferMapper::toTransferBasicDto).collect(Collectors.toList());
		return Response.ok(transfers).build();
	}

	@GET
	public List<AccountBasicDto> readAll(@QueryParam("offset") @DefaultValue("0") int offset,
										 @QueryParam("limit") @DefaultValue("50") @Min(1) @Max(500) int limit) {
		return accountsService.getAll(offset, limit).stream().map(account -> accountMapper.toAccountBasicDto(account))
				.collect(Collectors.toList());
	}

}