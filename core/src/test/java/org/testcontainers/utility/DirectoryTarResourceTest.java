package org.testcontainers.utility;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.File;
import java.util.concurrent.TimeoutException;

public class DirectoryTarResourceTest {

    @Rule
    public GenericContainer container = new GenericContainer(
            new ImageFromDockerfile()
                    .withDockerfileFromBuilder(builder ->
                            builder.from("alpine:3.3")
                                    .copy("/foo", "/tmp/foo")
                                    .cmd("ls -alr /tmp/foo")
                                    .build()
                    ).withFileFromFile("/foo", new File(".")))
            .withStartupCheckStrategy(new OneShotStartupCheckStrategy());


    @Test
    public void simpleTest() throws TimeoutException {
        final WaitingConsumer wait = new WaitingConsumer();
        wait.waitUntilEnd();
        final ToStringConsumer toString = new ToStringConsumer();
        container.followOutput(wait.andThen(toString));
    }
}
