package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.impl.rev141210;

import mars.kotlin.impl.NetconfSampleProvider;

public class NetconfSampleModule extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.impl.rev141210.AbstractNetconfSampleModule {
    public NetconfSampleModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
                               org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public NetconfSampleModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier,
                               org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
                               org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.netconf.odl.sample.impl.rev141210.NetconfSampleModule oldModule,
                               java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final NetconfSampleProvider netconfSampleProvider = new NetconfSampleProvider();
        getBrokerDependency().registerProvider(netconfSampleProvider);
        return netconfSampleProvider;
    }

}
