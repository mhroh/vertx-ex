package com.devroh.vertx.streamer;

import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.datagram.DatagramSocket;

public class UdpStreamer extends AbstractVerticle {
    private static final Integer DEF_BIND_PORT = 514;
    private static final String DEF_COMPRESSOR_ADDRESS = "compressor";
    private static final String KEY_BIND_ADDRESS = "datagram.bind.address";
    private static final String KEY_BIND_PORT = "datagram.bind.port";

    private DatagramSocket udpServer;
    private String bindAddress;
    private Integer bindPort;

    public void start() {
        this.bindAddress =  this.config().getString(KEY_BIND_ADDRESS);
        this.bindPort = this.config().getInteger(KEY_BIND_PORT, DEF_BIND_PORT);

        this.udpServer = this.vertx.createDatagramSocket(new DatagramSocketOptions()
                .setBroadcast(false).setReuseAddress(true).setReusePort(true));

        this.udpServer.rxListen(this.bindPort, this.bindAddress)
                .subscribe(datagramSocket ->
                    datagramSocket.toObservable().subscribe( datagram -> {
                        this.vertx.eventBus().send(DEF_COMPRESSOR_ADDRESS, datagram.getDelegate().data());
                    })
                );

        System.out.println("UdpStreamer : Start " + this.deploymentID());
    }

    public void stop() {

    }

}
