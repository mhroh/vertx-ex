package com.devroh.vertx.compressor;

import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.Message;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor extends AbstractVerticle {
    private static final String DEF_COMPRESSOR_ADDRESS = "compressor";
    private final String GZIPCOMPRESSOR_TARGE_FILENAME = "gzippedfile.gz";
    private final String GZIPCOMPRESSOR_TARGET_PATH = "/DATA1";

    private GZIPOutputStream gzipOutputStream;
    private File targetFile;

    private Func1<File, GZIPOutputStream> openGzipOutputStream = file -> {
        GZIPOutputStream out = null;

        try {
            out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file,true)));
        }
        catch (IOException e) {
            System.out.println("Open File Error : " + e.getMessage());
            out = null;
        } finally {
            return out;
        }
    };

    Action1<? super List<Message<Object>>> handleMsgDataList = msgList -> {
        if (msgList.size() > 0) {
            try {
                msgList.stream().map(msg -> ((Buffer) msg.getDelegate().body()))
                        .forEach( buffer ->{
                            try {
                                this.gzipOutputStream.write(buffer.getBytes());
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        });
            }
            catch (Exception e) {
                System.out.println("HandleMsgDataList : " + e.getMessage());
            }
        }
    };

    public void start() {
        this.targetFile = Paths.get(GZIPCOMPRESSOR_TARGET_PATH, GZIPCOMPRESSOR_TARGE_FILENAME).toFile();
        this.gzipOutputStream = openGzipOutputStream.call(this.targetFile);

        this.vertx.eventBus().consumer(DEF_COMPRESSOR_ADDRESS)
                .toObservable().buffer(1000,TimeUnit.MILLISECONDS, 1000)
                .subscribe(handleMsgDataList);
        System.out.println("GzipCompressor Start : " + this.deploymentID());
    }

    public void stop() {
        try {
            if (this.gzipOutputStream != null) {
                this.gzipOutputStream.flush();
                this.gzipOutputStream.close();
            }
        }
        catch (Exception e) {}

        System.out.println("Stop GzipCompressor " + this.deploymentID());
    }
}
