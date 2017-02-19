package org.testcontainers.images.builder;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public interface Transferable {

    int DEFAULT_FILE_MODE = 0100644;

    /**
     * Get file mode. Default is 0100644.
     * @see Transferable#DEFAULT_FILE_MODE
     *
     * @return file mode
     */
    default int getFileMode() {
        return DEFAULT_FILE_MODE;
    }

    /**
     * Size of an object.
     *
     * @return size in bytes
     */
    long getSize();

    /**
     * transfer content of this Transferable to the output stream. <b>Must not</b> close the stream.
     *
     * @param tarArchiveOutputStream stream to output
     * @param name
     */
    default void transferTo(TarArchiveOutputStream tarArchiveOutputStream, final String name) {
        TarArchiveEntry tarEntry = new TarArchiveEntry(name);
        tarEntry.setSize(getSize());
        tarEntry.setMode(getFileMode());

        try {
            tarArchiveOutputStream.putArchiveEntry(tarEntry);
            IOUtils.write(getBytes(), tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            throw new RuntimeException("Can't transfer " + getDescription(), e);
        }
    }

    byte[] getBytes();

    String getDescription();
}
