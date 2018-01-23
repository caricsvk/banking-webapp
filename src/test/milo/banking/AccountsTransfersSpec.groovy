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
        def account1Balance = 0
        def account2Balance = 0
        when:
        def amount = 989.10
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, amount, "welcome"))
        account1Balance += amount
        amount = 8700
        client.transferPost(new TransferInputDto(foreignIban, account2.iban, amount, null))
        account2Balance += amount
        amount = 440
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, amount, null))
        account1Balance -= amount
        account2Balance += amount
        amount = 100.44
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, amount, "loan from mom"))
        account1Balance -= amount
        account2Balance += amount
        amount = 100
        client.transferPost(new TransferInputDto(account2.iban, account1.iban, amount, "returning"))
        account1Balance += amount
        account2Balance -= amount
        def account1Response = client.accountGetWithTransfers(account1.iban)
        def account2Response = client.accountGetWithTransfers(account2.iban)
        then:
        account1Response.data.transfers.size() == 4
        account2Response.data.transfers.size() == 4
        account1Response.data.balance == account1Balance
        account2Response.data.balance == account2Balance
    }

    def "When searching transfers, test limit and offset"() {
        given:
        def account1 = client.accountPost(new AccountHolder("Dan Smith", null)).data
        def account2 = client.accountPost(new AccountHolder("Jane Doe", "jane@doe.com")).data
        def account1LastTransactionAmount = 100
        def account1LastByOneTransactionAmount = 440
        def transactionResultsLimit = 2
        def transactionResultsLimit2 = 1
        client.transferPost(new TransferInputDto(foreignIban, account2.iban, 8700, null))
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, 989.10, "welcome"))
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, account1LastByOneTransactionAmount, null))
        client.transferPost(new TransferInputDto(account2.iban, account1.iban, account1LastTransactionAmount, "returning"))
        when:
        def response = client.accountTransferSearch(account1.iban, null, transactionResultsLimit, null)
        def responseWithOffset = client.accountTransferSearch(account1.iban, null, transactionResultsLimit2, 1)
        then:
        response.data.size() == transactionResultsLimit
        response.data[0].amount == account1LastTransactionAmount
        response.data[1].amount == account1LastByOneTransactionAmount
        responseWithOffset.data.size() == transactionResultsLimit2
        responseWithOffset.data[0].amount == account1LastByOneTransactionAmount
    }

    def "When searching transfers, test filter for reference and holder name"() {
        given:
        def account1 = client.accountPost(new AccountHolder("Dan Smith", null)).data
        def account2 = client.accountPost(new AccountHolder("Jane Doe", "jane@doe.com")).data
        def janFirstTransactionAmount = 989.10
        def transactionsResultsLimit = 1
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, janFirstTransactionAmount, "welcome jan"))
        client.transferPost(new TransferInputDto(foreignIban, account1.iban, 440, "lending"))
        client.transferPost(new TransferInputDto(account1.iban, foreignIban, 440, "returning"))
        client.transferPost(new TransferInputDto(account1.iban, account2.iban, 100, null))
        when:
        def response = client.accountTransferSearch(account1.iban, "jan", null, null)
        def responseWithLimit = client.accountTransferSearch(account1.iban, "jan", transactionsResultsLimit, 1)
        then:
        response.data.size() == 2
        responseWithLimit.data.size() == transactionsResultsLimit
        responseWithLimit.data[0].amount == janFirstTransactionAmount
    }
}