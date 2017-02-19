package org.testcontainers.images.builder.traits;

import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * BuildContextBuilder's trait for NIO-based (Files and Paths) manipulations.
 *
 */
public interface FilesTrait<SELF extends FilesTrait<SELF> & BuildContextBuilderTrait<SELF>> {

    default SELF withFileFromFile(String path, File file) {
        return withFileFromPath(path, file.toPath());
    }

    default SELF withFileFromPath(String path, Path filePath) {
        final MountableFile mountableFile = MountableFile.forHostPath(filePath.toAbsolutePath().toString());
        return ((SELF) this).withFileFromTransferable(path, new Transferable() {

            @Override
            public long getSize() {
                try {
                    return mountableFile.size();
                } catch (IOException e) {
                    throw new RuntimeException("Can't get size from " + filePath, e);
                }
            }

            @Override
            public int getFileMode() {
                try {
                    return mountableFile.fileMode();
                } catch (IOException e) {
                    throw new RuntimeException("Can't get file mode from " + filePath, e);
                }
            }

            @Override
            public void transferTo(TarArchiveOutputStream tarArchiveOutputStream, final String destination) {
                try {
                    mountableFile.archiveTo(destination, tarArchiveOutputStream);
                } catch (IOException e) {
                    throw new RuntimeException("Can't transfer file " + filePath, e);
                }
            }
            @Override
            public String getDescription() {
                return "File from: " + filePath;
            }
        });
    }
}
