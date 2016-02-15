package mars.kotlin.impl

import org.opendaylight.controller.md.sal.binding.api.DataBroker
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification.ModificationType
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification
import org.opendaylight.controller.md.sal.binding.api.MountPointService
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeConnectionStatus.ConnectionStatus
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
        changes.filter { it.rootNode.modificationType == ModificationType.WRITE }
                .forEach { LOG.info("NETCONF node ${it.rootNode.dataAfter!!.nodeId} was created") }

        // THen log deleted nodes
        changes.filter { it.rootNode.modificationType == ModificationType.DELETE }
                .forEach { LOG.info("NETCONF node ${it.rootNode.dataBefore!!.nodeId} was removed") }

        // Then log connection updates
        changes.filter { it.rootNode.modificationType == ModificationType.SUBTREE_MODIFIED }
                .forEach {
                    when (it.rootNode.dataAfter!!.getAugmentation(NetconfNode::class.java).connectionStatus) {
                        ConnectionStatus.Connected -> {
                            // TODO for controller-config this is not emitted
                            LOG.info("NETCONF node ${it.rootNode.dataBefore!!.nodeId} was connected")
                        }
                        ConnectionStatus.UnableToConnect -> {
                            LOG.info("NETCONF node ${it.rootNode.dataBefore!!.nodeId} was failed to connect")
                        }
                    }
                }
    }

    override fun toString(): String {
        return "$javaClass : $this";
    }
}

/**
 * Generic extension function to add easy-to-use try-with-resources
 */
fun <T : AutoCloseable, R> T.use(body: (it: T) -> R): R {
    try {
        return body.invoke(this)
    } finally {
        try {
            close();
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
