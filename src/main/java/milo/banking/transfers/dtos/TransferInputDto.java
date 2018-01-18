package milo.banking.transfers.dtos;

import milo.utils.validators.Iban;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class TransferInputDto {

	@NotNull @Iban
	private String senderIban;
	@NotNull @Iban
	private String receiverIban;
	@NotNull @Min(0)
	private BigDecimal amount;
	@Size(max = 255)
	private String reference;

	public TransferInputDto() {
	}

	public TransferInputDto(String senderIban, String receiverIban, BigDecimal amount, String reference) {
		this.senderIban = senderIban;
		this.receiverIban = receiverIban;
		this.amount = amount;
		this.reference = reference;
	}

	public String getReceiverIban() {
		return receiverIban;
	}

	public void setReceiverIban(String receiverIban) {
		this.receiverIban = receiverIban;
	}

	public String getSenderIban() {
		return senderIban;
	}

	public void setSenderIban(String senderIban) {
		this.senderIban = senderIban;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}