package mars.kotlin.impl

import org.opendaylight.controller.md.sal.binding.api.DataBroker
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.rev150105.NetconfOdlSampleService
import spock.lang.Specification
import spock.lang.Timeout

@Timeout(5)
class NetconfSampleServiceSpec extends Specification {

    def "rpc sample service gets registered when creating NetconfOdlSampleService"() {

        given: "dataBroker mock"
        def dataBroker = Mock DataBroker
        and: "providerContext mock"
        def providerContext = Mock BindingAwareBroker.ProviderContext
        def rpcRegistration = Mock BindingAwareBroker.RpcRegistration

        when: "instantiating NetconfSampleService"
        new NetconfSampleService(providerContext, dataBroker)

        then: "rpc implementation is added to provider context"
        // Stubbing and verification on the same line
        1 * providerContext.addRpcImplementation(NetconfOdlSampleService.class, _ as NetconfOdlSampleService) >> {
            return rpcRegistration
        }
    }

}