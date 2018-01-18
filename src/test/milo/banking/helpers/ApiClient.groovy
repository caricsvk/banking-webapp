package milo.banking.helpers

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Specification


class ApiClient extends Specification {

    def client

    def setup(host, port) {
        client = new RESTClient("http://$host:$port/api/")
        client.handler.failure = {resp, data ->
            resp.data = data
            return resp
        }
    }

    def accountPost(account) {
        return accountPost(account, ContentType.JSON)
    }

    def accountPost(account, mediaType) {
        return client.post(path: "accounts", requestContentType: mediaType,
                headers: ['accept': mediaType], body: account)
    }

    def accountPut(iban, account) {
        return client.put(path: "accounts/$iban", requestContentType: ContentType.JSON,
                headers: ['accept': ContentType.JSON], body: account)
    }

    def accountDelete(iban) {
        return client.delete(path: "accounts/$iban")
    }

    def accountGet(iban) {
        return client.get(path: "accounts/$iban", headers: ['accept': ContentType.JSON])
    }

    def accountGetWithTransfers(iban) {
        return client.get(path: "accounts/$iban", headers: ['accept': ContentType.JSON], query: [withTransfers: 'true'])
    }

    def accountsGet(limit, offset) {
        def query = [:]
        if (limit != null) {
            query.put("limit", limit)
        }
        if (offset != null) {
            query.put("offset", offset)
        }
        return client.get(path: "accounts", headers: ['accept': ContentType.JSON], query: query)
    }

    def accountTransferSearch(iban, filter, limit, offset) {
        def query = [:]
        if (filter != null) {
            query.put('filter', filter)
        }
        if (limit != null) {
            query.put('limit', limit)
        }
        if (offset != null) {
            query.put('offset', limit)
        }
        return client.get(path: "accounts/$iban/transfers", headers: ['accept': ContentType.JSON], query: query)
    }

    def transferPost(transfer) {
        return client.post(path: "transfers", requestContentType: ContentType.JSON,
                headers: ['accept': ContentType.JSON], body: transfer)
    }

    def transferGet(transferId) {
        return client.get(path: "transfers/$transferId", headers: ['accept': ContentType.JSON])
    }
}
