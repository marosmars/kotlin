# ODL sample application using NETCONF and kotlin JVM language

This is a small SDN sample app, using:
* ODL
* NETCONF
* Kotling

Final app can be found on [github](https://github.com/marosmars/kotlin). Each step in this guide equals to 1 commit in that repo.

## Preparations

### Getting familiar with kotlin
<https://kotlinlang.org/docs/reference/basic-syntax.html>

### Dependencies
* Java 8 - Oracle or Openjdk
* Maven - version 3.2.5 +

### Empty ODL app

Generate app with [ODL archetype](https://wiki.opendaylight.org/view/OpenDaylight_Controller:MD-SAL:Startup_Project_Archetype).

Using 1.1.0-SNAPSHOT version which in translation is Beryllium snapshot version. However this close to formal ODL 
Beryllium release, that version is pretty stable.

Fill in the blanks and build with:

  `mvn clean install`

to build app stub. You can read more at [ODL's wiki](https://wiki.opendaylight.org/view/Main_Page) or you can just take 
a look at [first commit](https://github.com/marosmars/kotlin/commit/b3f58dca29f267a353fc7f2471c99d1e61349a98) in this sample

## Enable Kotlin

Next step is adding kotlin to the project.

Using RC version of kotlin: 1.0.0-rc-1036 since formal release is not yet out. Pretty straightforward with maven: [enable kotlin commit](https://github.com/marosmars/kotlin/commit/c59c0f71aaacb402dbc341e2f39cbd7593a2dd76)

Now just build:

`mvn clean install`

After build succeeds, start ODL with sample application:

`./karaf/target/assembly/bin/karaf to start and check our app with kotlin`

In ODL logs(log:tail), following output should be visible after a few moments, indicating successful kotlin setup:

`2016-02-06 14:29:26,941 | INFO  | config-pusher    | NetconfSampleProvider            | 142 - org.mars.kotlin.netconf-odl-sample-impl - 1.0.0.SNAPSHOT | Kotlin works!`

## Add Netconf features

Now just add ODL NETCONF features. This sample app will use NETCONF to perform SDN stuff and NETCONF [features need to be added](https://github.com/marosmars/kotlin/commit/cf7993fd5fb62a233c23f99307f8f50d6e3b8b81).

More information can be found in [official ODL NETCONF sample app](https://wiki.opendaylight.org/view/Controller_Core_Functionality_Tutorials:Tutorials:Netconf_Mount).

After another build, start app and in the logs look for:


`2016-02-06 14:59:37,175 | INFO  | ult-dispatcher-2 | NetconfSampleProvider            | 234 - org.mars.kotlin.netconf-odl-sample-impl - 1.0.0.SNAPSHOT | Kotlin detected data change: org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier@15afd047`

`2016-02-06 14:59:48,776 | INFO  | sing-executor-11 | NetconfDevice                    | 157 - org.opendaylight.netconf.sal-netconf-connector - 1.3.0.SNAPSHOT | RemoteDevice{controller-config}: Netconf connector initialized successfully`

Now kotlin is working as well as ODL NETCONF features.

## Trying out kotlin's features

Now, kotlin can be utilized to [full extent](https://github.com/marosmars/kotlin/commit/e84329e671d7c7c5568ffb53a9ea1fe0a43f404d).

## Adding list-nodes RPC

[Simple RPC called list-nodes](https://github.com/marosmars/kotlin/commit/9735bfcbb111fa39721752c50ddc4fb3e9c84ee2) to allow external and internal apps to list mounted netconf devices.
It's a copy from [official ODL NETCONF sample app](https://wiki.opendaylight.org/view/Controller_Core_Functionality_Tutorials:Tutorials:Netconf_Mount).

After performing build and run the app, list-nodes can be invoked with:

`curl -u "admin:admin" http://localhost:8181/restconf/operations/netconf-odl-sample:list-nodes -X POST`

## Rewrite Module and ModuleFactory to Kotlin

// TODO

## Unit tests
// TODO [Spock](https://code.google.com/archive/p/spock/) tests