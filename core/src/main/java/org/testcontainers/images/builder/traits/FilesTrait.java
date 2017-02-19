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
        return ((SELF) this).withFileFromTransferable(path, new Transferable() {

            @Override
            public long getSize() {
                try {
                    return MountableFile.forHostPath(filePath.toAbsolutePath().toString()).size();
                } catch (IOException e) {
                    throw new RuntimeException("Can't get size from " + filePath, e);
                }
            }

            @Override
            public int getFileMode() {
                try {
                    return MountableFile.forHostPath(filePath.toAbsolutePath().toString()).fileMode();
                } catch (IOException e) {
                    throw new RuntimeException("Can't get file mode from " + filePath, e);
                }
            }

            @Override
            public void transferTo(TarArchiveOutputStream tarArchiveOutputStream, final String name) {
                try {
                    MountableFile.forHostPath(filePath.toAbsolutePath().toString()).archiveTo(tarArchiveOutputStream, name);
                } catch (IOException e) {
                    throw new RuntimeException("Can't transfer file " + filePath, e);
                }
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public String getDescription() {
                return null;
            }

        });
    }
}
