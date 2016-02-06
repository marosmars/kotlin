package mars.kotlin.impl

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider
import org.slf4j.LoggerFactory

class NetconfSampleProvider : BindingAwareProvider, AutoCloseable {

    private val LOG = LoggerFactory.getLogger(NetconfSampleProvider::class.java)

    override fun onSessionInitiated(ctx: BindingAwareBroker.ProviderContext?) {
        LOG.info("Kotlin works!")
    }

    override fun close() {
        LOG.info("Kotlin out!")
    }

}
