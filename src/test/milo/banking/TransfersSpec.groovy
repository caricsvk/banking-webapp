package milo.banking

import milo.banking.accounts.entities.AccountHolder
import milo.banking.helpers.BaseSpec
import milo.banking.transfers.dtos.TransferInputDto

class TransfersSpec extends BaseSpec {

    def foreignIban = "NL46ABNA0808389875"
    def foreignIban2 = "NL46ABNA0808389876"

    def "When making transfer, code 200 will be returned and new id generated and corresponding data"() {
        given:
        def iban = client.accountPost(new AccountHolder("holder name", "holder@email.org")).data.iban
        def amount = 122.50
        def reference = "richard"
        when:
        def response = client.transferPost(new TransferInputDto(foreignIban, iban, amount, reference))
        then:
        response.status == 200
        response.data.id != null
        response.data.amount == amount
        response.data.reference == reference
        response.data.senderAccount.iban == foreignIban
        response.data.receiverAccount.iban == iban
    }

    def "When getting transfer, code 200 will be returned with corresponding data"() {
        given:
        def iban = client.accountPost(new AccountHolder("holder name", "holder@email.org")).data.iban
        def amount = 122.50
        def reference = "richard"
        when:
        def id = client.transferPost(new TransferInputDto(foreignIban, iban, amount, reference)).data.id
        println "TransfersSpec.When getting transfer, code 200 will be returned with corresponding data -=-=- " + id
        def response = client.transferGet(id)
        then:
        response.status == 200
        response.data.id == id
        response.data.amount == amount
        response.data.reference == reference
        response.data.senderAccount.iban == foreignIban
        response.data.receiverAccount.iban == iban
    }

    def "When getting non-existing transfer, code 404 will be returned"() {
        given:
        when:
        def response = client.transferGet(-1)
        then:
        response.status == 404
    }

    def "When making transfer from account with insufficient funds, code 406 will be returned"() {
        given:
        def iban = client.accountPost(new AccountHolder("holder name", "holder@email.org")).data.iban
        when:
        def response = client.transferPost(new TransferInputDto(iban, foreignIban, 10, null))
        then:
        response.status == 406
    }

    def "When making transfer to deleted account, code 410 will be returned"() {
        given:
        def iban = client.accountPost(new AccountHolder("holder name", "holder@email.org")).data.iban
        when:
        client.accountDelete(iban)
        def response = client.transferPost(new TransferInputDto(iban, foreignIban, 10, null))
        then:
        response.status == 410
    }

    def "When making transfer without internal account, code 404 will be returned"() {
        given:
        when:
        def response = client.transferPost(new TransferInputDto(foreignIban2, foreignIban, 10, null))
        then:
        response.status == 404
    }

}