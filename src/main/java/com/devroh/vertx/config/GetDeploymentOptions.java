package com.devroh.vertx.config;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.buffer.Buffer;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class GetDeploymentOptions implements Configs<DeploymentOptions> {
	
	@Override
	public Optional<DeploymentOptions> getConfig(String path) {
		Optional<Buffer> optBuffer= getConfigBuffer(path);
		WeakReference<Optional<Buffer>> ref = new WeakReference<Optional<Buffer>>(optBuffer);

		try {
			return optBuffer.isPresent() ?
					Optional.of(new DeploymentOptions().setConfig(ref.get().get().toJsonObject())) :
					Optional.empty();
		}
		finally {
			optBuffer = null;
		}
	}
	
	public static void main(String ... strings) {
		Optional<DeploymentOptions> conf = new GetDeploymentOptions().getConfig(strings[1]);

		if (conf.isPresent())  {
			conf.get().getConfig().getMap().forEach((str, val)->{ System.out.println("Key: " + str + " / Value: " +String.valueOf(val)); });
		}
		else {
			System.out.println("Nothing");
		}
	}
}
