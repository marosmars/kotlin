package mars.kotlin.impl

import org.opendaylight.controller.md.sal.binding.api.*
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier
import org.slf4j.LoggerFactory

class NetconfSampleProvider : BindingAwareProvider, AutoCloseable, DataTreeChangeListener<Node> {
    init {
        LOG.info("Netconf + kotlin sample app initialized")
    }

    companion object {
        val LOG = LoggerFactory.getLogger(NetconfSampleProvider::class.java)
        val NETCONF_TOPO_IID: InstanceIdentifier<Topology> = InstanceIdentifier.create(NetworkTopology::class.java)
                .child(Topology::class.java, TopologyKey(TopologyId(TopologyNetconf.QNAME.localName)));
    }

    private var mountPointService: MountPointService? = null;
    private var rpcs: NetconfSampleService? = null;

    override fun onSessionInitiated(ctx: BindingAwareBroker.ProviderContext?) {
        LOG.info("Kotlin works!")
        mountPointService = ctx!!.getSALService(MountPointService::class.java)
        ctx.getSALService(DataBroker::class.java).let { dataBroker ->
            dataBroker.registerDataTreeChangeListener(DataTreeIdentifier(LogicalDatastoreType.OPERATIONAL,
                    NETCONF_TOPO_IID.child(Node::class.java)), this)
            rpcs = NetconfSampleService(ctx, dataBroker)
        }
    }

    override fun close() {
        with(LOG) {
            info("Kotlin out!")
            mountPointService = null
            info("Un-setting mountpoint service")
            rpcs?.let { it.close() }
        }
    }

    override fun onDataTreeChanged(changes: MutableCollection<DataTreeModification<Node>>) {
        // First just log new nodes
        changes.filter { it.rootNode.modificationType == DataObjectModification.ModificationType.WRITE }
                .forEach { LOG.info("NETCONF connector ${it.rootNode.dataAfter!!.nodeId} was created") }

        // THen log deleted nodes
        changes.filter { it.rootNode.modificationType == DataObjectModification.ModificationType.DELETE }
                .forEach { LOG.info("NETCONF connector ${it.rootNode.dataAfter!!.nodeId} was removed") }

        // TODO process rest
    }

    override fun toString(): String {
        return "$javaClass : $this";
    }
}

/**
 * Generic extension function to add easy-to-use try-with-resources
 */
fun <T, R> T.use(body: (it: T) -> R): R {
    try {
        return body.invoke(this)
    } finally {
        try {
            if(this is AutoCloseable) {
                close();
            }
        } catch(e: Exception) {
            NetconfSampleProvider.LOG.warn("Unable to close resource properly. Ignoring", e)
        }
    }
}

/**
 * Generic extension function to add easy-to-use init. Much like kotlin's "with"
 */
fun <T> T.init(body: T.() -> Unit): T {
    body.invoke(this)
    return this;
}
