package milo.banking.helpers

import spock.lang.Specification

class BaseSpec extends Specification {

    protected client = new ApiClient()

    def setup() {
        client.setup("localhost", 8080)
    }

}
