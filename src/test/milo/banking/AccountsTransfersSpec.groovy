package milo.banking

import milo.banking.accounts.entities.AccountHolder
import milo.banking.helpers.BaseSpec
import milo.banking.transfers.dtos.TransferInputDto

class AccountsTransfersSpec extends BaseSpec {

    def foreignIban = "NL46ABNA0808389875"

    def "When creating transfers, all of them appear in both accounts and balance will be correct"() {
        given:
        def account1 = client.accountPost(new AccountHolder("Dan Smith", null)).data
        def account2 = client.accountPost(new AccountHolder("Jane Doe", "jane@doe.com")).data
        when:
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, 989.10, "welcome"))
        client.transferPost(new TransferInputDto(foreignIban, account2.iban, 8700, null))
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, 440, null))
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, 100.44, "loan from mom"))
        client.transferPost(new TransferInputDto(account2.iban, account1.iban, 100, "returning"))
        def account1Response = client.accountGetWithTransfers(account1.iban)
        def account2Response = client.accountGetWithTransfers(account2.iban)
        then:
        account1Response.data.transfers.size() == 4
        account2Response.data.transfers.size() == 4
        account1Response.data.balance == 989.10 - 440 - 100.44 + 100
        account2Response.data.balance == 8700 + 440 + 100.44 - 100
    }

    def "When searching transfers, test limit and offset"() {
        given:
        def account1 = client.accountPost(new AccountHolder("Dan Smith", null)).data
        def account2 = client.accountPost(new AccountHolder("Jane Doe", "jane@doe.com")).data
        client.transferPost(new TransferInputDto(foreignIban, account2.iban, 8700, null))
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, 989.10, "welcome"))
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, 440, null))
        client.transferPost(new TransferInputDto(account2.iban, account1.iban, 100, "returning"))
        when:
        def response = client.accountTransferSearch(account1.iban, null, 2, null)
        def responseWithOffset = client.accountTransferSearch(account1.iban, null, 1, 1)
        then:
        response.data.size() == 2
        response.data[0].amount == 100
        response.data[1].amount == 440
        responseWithOffset.data.size() == 1
        responseWithOffset.data[0].amount == 440
    }

    def "When searching transfers, test filter for reference and holder name"() {
        given:
        def account1 = client.accountPost(new AccountHolder("Dan Smith", null)).data
        def account2 = client.accountPost(new AccountHolder("Jane Doe", "jane@doe.com")).data
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, 989.10, "welcome jan"))
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, 440, "lending"))
        client.transferPost(new TransferInputDto(account1.iban, foreignIban, 440, "returning"))
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, 100, null))
        when:
        def response = client.accountTransferSearch(account1.iban, "jan", null, null)
        def responseWithLimit = client.accountTransferSearch(account1.iban, "jan", 1, 1)
        then:
        response.data.size() == 2
        responseWithLimit.data.size() == 1
        responseWithLimit.data[0].amount == 989.10
    }
}