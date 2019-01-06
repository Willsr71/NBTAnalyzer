package sr.will.nbtanalyzer;

import sr.will.nbtanalyzer.nbt.Tag;
import sr.will.nbtanalyzer.nbt.stream.NBTInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionReader {
    private static final int SECTOR_BYTES = 4096;

    public static List<Tag<?>> readFile(File f) {
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            // If there isn't a header and at least one chunk sector, the region is empty
            if (raf.length() < SECTOR_BYTES * 3) {
                return Collections.emptyList();
            }
            // Each chunk can use 1 or more sectors, and the first two sectors
            // are the header, so this is the maximum number of chunks
            int maxChunks = ((int) raf.length() / SECTOR_BYTES) - 2;
            int[] chunkLocation = new int[maxChunks];
            int entries = 0;
            for (int i = 0; i < (SECTOR_BYTES / 4); i++) {
                int offset = raf.readInt();
                if (offset != 0) {
                    // The rest of the offset is the number of sectors that the chunk
                    // occupies.  We don't care about that as each chunk stores its length
                    chunkLocation[entries++] = (offset >> 8) * SECTOR_BYTES;
                }
            }
            List<Tag<?>> list = new ArrayList<>();

            for (int i = 0; i < entries; i++) {
                list.add(readChunk(raf, chunkLocation[i]));
            }

            return list;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    private static Tag<?> readChunk(RandomAccessFile raf, int location) throws IOException {
        raf.seek(location);
        int length = raf.readInt();
        byte compressionType = raf.readByte();
        byte[] data = new byte[length - 1];
        raf.readFully(data);
        return readTag(decompress(compressionType, data));
    }

    private static InputStream decompress(int type, byte[] data) throws IOException {
        switch (type) {
            case 1:
                return new GZIPInputStream(new ByteArrayInputStream(data));
            case 2:
                return new InflaterInputStream(new ByteArrayInputStream(data));
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }

    private static Tag<?> readTag(InputStream in) throws IOException {
        return new NBTInputStream(in, false).readTag();
    }
}