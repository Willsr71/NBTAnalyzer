package sr.will.nbtanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sr.will.nbtanalyzer.nbt.CompoundMap;
import sr.will.nbtanalyzer.nbt.CompoundTag;
import sr.will.nbtanalyzer.nbt.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class NBTAnalyzer {
    private static NBTAnalyzer instance;

    private static final Logger logger = LoggerFactory.getLogger("NBTAnalyzer");
    public static final long startTime = System.currentTimeMillis();
    public static final String VERSION = "@version@";

    public static HashMap<Integer, Integer> chunkSizes = new HashMap<>();

    public NBTAnalyzer(String file) {
        instance = this;

        logger.info(file);

        List<Tag<?>> region = RegionReader.readFile(new File(file));

        int largestChunkIndex = 0;
        for (int x : chunkSizes.keySet()) {
            if (chunkSizes.get(x) > chunkSizes.get(largestChunkIndex)) {
                largestChunkIndex = x;
            }
        }

        logger.info("Largest chunk is {} of size {}", largestChunkIndex, chunkSizes.get(largestChunkIndex));

        CompoundMap chunkTags = (CompoundMap) region.get(largestChunkIndex).getValue();

        CompoundMap levelTags = (CompoundMap) chunkTags.get("Level").getValue();

        logger.info("Chunk is at position x={}, z={}", levelTags.get("xPos").getValue(), levelTags.get("zPos").getValue());

        ArrayList<CompoundTag> tileEntities = new ArrayList((Collection) levelTags.get("TileEntities").getValue());

        logger.info("There are {} Tile Entities in this chunk", tileEntities.size());

        for (CompoundTag tile : tileEntities) {
            //logger.info("Value: {}", tile);
        }
    }
}
