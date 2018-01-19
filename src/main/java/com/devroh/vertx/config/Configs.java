package com.devroh.vertx.config;

import io.vertx.core.buffer.Buffer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Configs<T> {
	Predicate<Path> validPath = path -> path.toFile().exists();
	Predicate<Path> isFile= path -> path.toFile().isFile();
	Predicate<Path> isReadable = path -> path.toFile().canRead();

	Function<Path, Optional<Buffer>> readConfig = path -> {
		Buffer buffer = Buffer.buffer();
		try (Stream<String> stream = Files.lines(path)) {
			stream.forEach(buffer::appendString);
			return Optional.of(buffer);
		}
		catch (Exception e) {
			return Optional.empty();
		}
	};

	default Optional<Buffer> getConfigBuffer(String path) {
		final Path confPath = Paths.get(path);
		return validPath.and(isFile)
				.and(isReadable)
				.test(confPath) ? readConfig.apply(confPath) : Optional.empty();
	}

	Optional<T> getConfig(String path);
}
