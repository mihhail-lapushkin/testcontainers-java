package org.testcontainers.utility;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.File;
import java.util.concurrent.TimeoutException;

import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;

public class DirectoryTarResourceTest {

    @Rule
    public GenericContainer container = new GenericContainer(
            new ImageFromDockerfile()
                    .withDockerfileFromBuilder(builder ->
                            builder.from("alpine:3.3")
                                    .copy("/tmp/foo", "/foo")
                                    .cmd("cat /foo/src/test/resources/test-recursive-file.txt")
                                    .build()
                    ).withFileFromFile("/tmp/foo", new File(".")))
            .withStartupCheckStrategy(new OneShotStartupCheckStrategy());


    @Test
    public void simpleTest() throws TimeoutException {

        final ToStringConsumer toString = new ToStringConsumer();
        container.followOutput(toString);

        final String results = toString.toUtf8String();

        assertTrue("The container has a file that was copied in via a recursive copy", results.contains("Used for DirectoryTarResourceTest"));
    }
}
