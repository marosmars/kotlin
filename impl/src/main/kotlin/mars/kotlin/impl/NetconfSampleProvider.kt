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

    companion object {
        val NETCONF_TOPO_IID: InstanceIdentifier<Topology> = InstanceIdentifier.create(NetworkTopology::class.java)
                .child(Topology::class.java, TopologyKey(TopologyId(TopologyNetconf.QNAME.localName)));
    }

    private val LOG = LoggerFactory.getLogger(NetconfSampleProvider::class.java)

    private var mountPointService: MountPointService? = null;

    override fun onSessionInitiated(ctx: BindingAwareBroker.ProviderContext?) {
        LOG.info("Kotlin works!")
        mountPointService = ctx!!.getSALService(MountPointService::class.java)
        val dataBroker = ctx.getSALService(DataBroker::class.java)
        dataBroker.registerDataTreeChangeListener(DataTreeIdentifier(
                LogicalDatastoreType.OPERATIONAL,
                NetconfSampleProvider.NETCONF_TOPO_IID.child(Node::class.java)), this)
    }

    override fun close() {
        LOG.info("Kotlin out!")
    }

    override fun onDataTreeChanged(changes: MutableCollection<DataTreeModification<Node>>) {
        for (i in changes.iterator()) {
            LOG.info("Kotlin detected data change: {}", i.rootPath)
            val mountPoint = mountPointService!!.getMountPoint(i.rootPath.rootIdentifier);
        }
    }

}
