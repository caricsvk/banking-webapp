package milo.banking;

import milo.banking.accounts.dtos.AccountBasicDto;
import milo.banking.accounts.dtos.AccountDto;
import milo.banking.accounts.entities.Account;
import milo.banking.transfers.Transfer;
import milo.banking.transfers.dtos.TransferBasicDto;
import milo.banking.transfers.dtos.TransferDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

public interface Mappers {

	AccountMapper ACCOUNT = org.mapstruct.factory.Mappers.getMapper(AccountMapper.class);
	TransferMapper TRANSFER = org.mapstruct.factory.Mappers.getMapper(TransferMapper.class);

	@Mapper
	interface AccountMapper {
		AccountDto toAccountDto(Account account);
		AccountBasicDto toAccountBasicDto(Account account);
	}

	@Mapper
	interface TransferMapper {
		@Mappings({
				@Mapping(source = "senderAccount.iban", target = "senderIban"),
				@Mapping(source = "receiverAccount.iban", target = "receiverIban")
		})
		TransferBasicDto toTransferBasicDto(Transfer transfer);
		TransferDto toTransferDto(Transfer transfer);
	}
}

