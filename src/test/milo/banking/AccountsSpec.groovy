package milo.banking

import groovyx.net.http.ContentType
import milo.banking.accounts.entities.AccountHolder
import milo.banking.helpers.BaseSpec

class AccountsSpec extends BaseSpec {

    def "When creating account, code 200 will be returned and new iban generated"() {
        given:
        def account = new AccountHolder("holder name", "holder@email.org")
        when:
        def response = client.accountPost(account)
        then:
        response.status == 200
        response.data.iban != null
    }

    def "When creating account with invalid inputs, code 400 will be returned"() {
        given:
        def account = new AccountHolder(null, "wrong.email")
        when:
        def response = client.accountPost(account)
        then:
        response.status == 400
    }

    def "When getting account after creation, same data and new iban will be returned"() {
        given:
        def email = "holder@email.org"
        def name = "holder name"
        def account = new AccountHolder(name, email)
        when:
        def postResponse = client.accountPost(account)
        def response = client.accountGet(postResponse.data.iban)
        then:
        response.status == 200
        response.data.iban != null
        response.data.accountHolder.email == email
        response.data.accountHolder.name == name
    }

    def "When updating account, new data and same iban will be returned"() {
        given:
        def createdAccountResponse = client.accountPost(new AccountHolder("holder name", "holder@email.org"))
        def updatedAccount = new AccountHolder("NEW NAME", "NEW@email.org")
        when:
        def updatedAccountResponse = client.accountPut(createdAccountResponse.data.iban, updatedAccount)
        then:
        updatedAccountResponse.status == 200
        updatedAccountResponse.data.iban != null
        updatedAccountResponse.data.iban == createdAccountResponse.data.iban
        updatedAccountResponse.data.accountHolder.email == updatedAccount.email
        updatedAccountResponse.data.accountHolder.name == updatedAccount.name
    }

    def "When updating account with wrong input, 400 will be returned"() {
        given:
        def createdAccountResponse = client.accountPost(new AccountHolder("holder name", "holder@email.org"))
        def updatedAccount = new AccountHolder("NEW NAME", "wrongemail")
        when:
        def updatedAccountResponse = client.accountPut(createdAccountResponse.data.iban, updatedAccount)
        then:
        updatedAccountResponse.status == 400
    }

    def "When updating account after deletion, 410 will be returned"() {
        given:
        def createdAccountResponse = client.accountPost(new AccountHolder("holder name", "holder@email.org"))
        def updatedAccount = new AccountHolder("NEW NAME", "NEW@email.org")
        when:
        client.accountDelete(createdAccountResponse.data.iban)
        def updatedAccountResponse = client.accountPut(createdAccountResponse.data.iban, updatedAccount)
        then:
        updatedAccountResponse.status == 410
    }

    def "When deleting account, 204 for delete and 410 for following get will be returned"() {
        given:
        def createdAccount = client.accountPost(new AccountHolder("holder name", "holder@email.org")).data
        when:
        def response = client.accountDelete(createdAccount.iban)
        def responseGet = client.accountGet(createdAccount.iban)
        then:
        response.status == 204
        responseGet.status == 410
    }

    def "When getting accounts count after creation, count will be incremented"() {
        given:
        def allAccountsResponseBefore = client.accountsGet(null, null)
        when:
        client.accountPost(new AccountHolder("holder name", "holder@email.org"))
        def allAccountsAfterResponse = client.accountsGet(null, null)
        then:
        allAccountsAfterResponse.status == 200
        allAccountsAfterResponse.data.size() == allAccountsResponseBefore.data.size() + 1
    }

    def "When getting accounts and limiting results to 2, only 2 will be returned"() {
        given:
        client.accountPost(new AccountHolder("name 1", "holder3@email.org"))
        client.accountPost(new AccountHolder("holder name", "holder2@email.org"))
        client.accountPost(new AccountHolder("XxxX", "holder1@email.org"))
        when:
        def response = client.accountsGet(2, null)
        then:
        response.status == 200
        response.data.size() == 2
    }

    def "When getting accounts, 50 results by default, max 500 results by request, and 400 status if more requested"() {
        given:
        for (int i = 0; i < 510; i ++) {
            client.accountPost(new AccountHolder("name 1", "holder3@email.org"))
        }
        when:
        def defaultResponse = client.accountsGet(null, null)
        def maxResponse = client.accountsGet(500, null)
        def moreResponse = client.accountsGet(501, null)
        then:
        defaultResponse.data.size() == 50
        maxResponse.data.size() == 500
        moreResponse.status == 400
    }

    def "When using wrong media type, 415 will be returned"() {
        given:
        def account = new AccountHolder("holder name", "holder@email.org")
        when:
        def response = client.accountPost(account, ContentType.HTML)
        then:
        response.status == 415
    }

}