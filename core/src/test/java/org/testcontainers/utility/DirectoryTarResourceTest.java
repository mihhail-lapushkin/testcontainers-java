package org.testcontainers.utility;

import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;

//@Slf4j
public class DirectoryTarResourceTest {

    @Test
    public void simpleTest() throws TimeoutException {

        WaitingConsumer wait = new WaitingConsumer();

        final ToStringConsumer toString = new ToStringConsumer();

        GenericContainer container = new GenericContainer(
                new ImageFromDockerfile()
                        .withDockerfileFromBuilder(builder ->
                                builder.from("alpine:3.3")
                                        .copy("/tmp/foo", "/foo")
                                        .cmd("cat /foo/src/test/resources/test-recursive-file.txt")
                                        .build()
                        ).withFileFromFile("/tmp/foo", new File(".")))
                .withStartupCheckStrategy(new OneShotStartupCheckStrategy())
                .withLogConsumer(wait.andThen(toString));

        container.start();
        wait.waitUntilEnd(60, TimeUnit.SECONDS);

        final String results = toString.toUtf8String();
//        log.info(results);

        assertTrue("The container has a file that was copied in via a recursive copy", results.contains("Used for DirectoryTarResourceTest"));
    }
}
