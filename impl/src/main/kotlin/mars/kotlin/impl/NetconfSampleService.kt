package mars.kotlin.impl

import com.google.common.util.concurrent.Futures
import org.opendaylight.controller.md.sal.binding.api.DataBroker
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.rev150105.ListNodesOutput
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.rev150105.ListNodesOutputBuilder
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.rev150105.NetconfOdlSampleService
import org.opendaylight.yangtools.yang.common.RpcError
import org.opendaylight.yangtools.yang.common.RpcResult
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.concurrent.Future
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType as DsType

/**
 * RPCs implementation for netconf-odl-sample
 */
class NetconfSampleService(private val ctx: BindingAwareBroker.ProviderContext, private val dataBroker: DataBroker) :
        NetconfOdlSampleService, AutoCloseable {

    companion object {
        val LOG = LoggerFactory.getLogger(NetconfSampleService::class.java)
    }

    private val registration: BindingAwareBroker.RpcRegistration<NetconfOdlSampleService> =
            ctx.addRpcImplementation(NetconfOdlSampleService::class.java, this)

    override fun close() = registration.close()

    /**
     * List NETCONF topology nodes
     */
    override fun listNodes(): Future<RpcResult<ListNodesOutput>>? {

        return dataBroker.newReadOnlyTransaction().use { tx ->
            try {
                val outBld = ListNodesOutputBuilder().init {

                    tx.read(DsType.CONFIGURATION, NetconfSampleProvider.NETCONF_TOPO_IID).checkedGet().orNull()?.let {
                        topology ->
                        ncConfigNodes = topology.node.map { node -> node.nodeId.value }
                    }

                    tx.read(DsType.OPERATIONAL, NetconfSampleProvider.NETCONF_TOPO_IID).checkedGet().orNull()?.let {
                        topology ->
                        ncOperNodes = topology.node.map { node ->
                            // Log netconf capabilities if present
                            node.getAugmentation(NetconfNode::class.java)?.let { netconfNode ->
                                LOG.info("Capabilities of ${node.nodeId}: ${netconfNode.availableCapabilities.availableCapability}")
                            }

                            node.nodeId.value
                        }
                    }
                }

                Futures.immediateFuture(object : RpcResult<ListNodesOutput> {
                    override fun isSuccessful(): Boolean = true
                    override fun getResult(): ListNodesOutput? = outBld.build()
                    override fun getErrors(): MutableCollection<RpcError>? = Collections.emptyList()
                })

            } catch(e: ReadFailedException) {
                Futures.immediateFailedFuture<RpcResult<ListNodesOutput>>(e)
            }
        }
    }
}