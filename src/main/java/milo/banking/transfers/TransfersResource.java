package milo.banking.transfers;

import milo.banking.Mappers;
import milo.banking.accounts_transfers.AccountsTransferService;
import milo.banking.transfers.dtos.TransferDto;
import milo.banking.transfers.dtos.TransferInputDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;

@Path("/transfers") @Stateless
public class TransfersResource {

	@Inject
	private AccountsTransferService accountsTransferService;
	@Inject
	private TransfersService transfersService;
	@Inject
	private Mappers.TransferMapper transferMapper;

	@POST
	public TransferDto create(@Valid TransferInputDto transferInputDto) {
		Transfer transfer = accountsTransferService.makeTransfer(transferInputDto.getSenderIban(),
				transferInputDto.getReceiverIban(), transferInputDto.getAmount(), transferInputDto.getReference());
		return transferMapper.toTransferDto(transfer);
	}

	@GET @Path("{id}")
	public TransferDto read(@PathParam("id") Long id) {
		Transfer transfer = transfersService.find(id);
		if (transfer == null) {
			throw new NotFoundException();
		}
		return transferMapper.toTransferDto(transfer);
	}

}
