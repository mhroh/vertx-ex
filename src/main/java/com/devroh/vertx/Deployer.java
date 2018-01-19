package com.devroh.vertx;

import com.devroh.vertx.compressor.GzipCompressor;
import com.devroh.vertx.streamer.UdpStreamer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import rx.functions.Action2;

import com.devroh.vertx.config.GetDeploymentOptions;
import com.devroh.vertx.runner.Runner;

public class Deployer extends AbstractVerticle{
    public static Action2<Vertx, String> undeploy = (vertx, id) -> vertx.runOnContext(Void->vertx.undeploy(id));

    public void start() {
        this.config().stream().forEachOrdered( entry ->
           System.out.println( entry.getKey() + " : " + entry.getValue())
        );

        this.vertx.rxDeployVerticle(UdpStreamer.class.getName(),
                new DeploymentOptions().setConfig(this.config().copy())
                        .setWorker(false).setWorkerPoolName("UDP-Streamer").setWorkerPoolSize(4))
                .subscribe();

        this.vertx.rxDeployVerticle(GzipCompressor.class.getName(),
                new DeploymentOptions().setConfig(this.config().copy())
                        .setWorker(true).setWorkerPoolName("Gzip-Compressor").setWorkerPoolSize(4))
                .subscribe();
    }

    public void stop() {
        System.out.println("Stop Deployer : " + this.deploymentID());
    }

    public static void main(String ... strings) {
        VertxOptions opt = new VertxOptions().setClustered(false);
        Runner.runExample(Runner.EXAMPLES_DIR, Deployer.class, opt,
                new GetDeploymentOptions().getConfig(strings[3]).get().setWorker(true));
    }
}
