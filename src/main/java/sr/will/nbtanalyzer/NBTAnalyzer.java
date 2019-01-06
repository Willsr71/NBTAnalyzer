package sr.will.nbtanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sr.will.nbtanalyzer.nbt.CompoundMap;
import sr.will.nbtanalyzer.nbt.CompoundTag;
import sr.will.nbtanalyzer.nbt.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTAnalyzer {
    private static NBTAnalyzer instance;

    private static final Logger logger = LoggerFactory.getLogger("NBTAnalyzer");
    public static final long startTime = System.currentTimeMillis();
    public static final String VERSION = "@version@";

    public NBTAnalyzer(String file) {
        instance = this;

        logger.info(file);
        List<Tag<?>> region = RegionReader.readFile(new File(file));

        Tag<?> chunk = getLargestTag(region);
        CompoundMap chunkTags = (CompoundMap) chunk.getValue();
        CompoundMap level = (CompoundMap) chunkTags.get("Level").getValue();
        ArrayList<CompoundTag> tileEntities = new ArrayList<>((Collection) level.get("TileEntities").getValue());
        logger.info("Largest chunk is ({}, {}) of size {} with {} TileEntities",
                level.get("xPos").getValue(),
                level.get("zPos").getValue(),
                chunk.getSize(),
                tileEntities.size()
        );

        CompoundTag tile = getLargestCompoundTag(tileEntities);
        CompoundMap tileTags = tile.getValue();
        logger.info("Largest Tile Entity is {} ({}, {}, {}) of size {}",
                tileTags.get("id").getValue(),
                tileTags.get("x").getValue(),
                tileTags.get("y").getValue(),
                tileTags.get("z").getValue(),
                tile.getSize()
        );

        logger.info("Tile Data:");
        for (String key : tileTags.keySet()) {
            logger.info("{} (Type: {}, Size: {})",
                    key,
                    tileTags.get(key).getType(),
                    tileTags.get(key).getSize());
        }
    }

    public Tag<?> getLargestTag(List<Tag<?>> list) {
        int largestIndex = 0;
        for (int x = 0; x < list.size(); x += 1) {
            if (list.get(x).getSize() > list.get(largestIndex).getSize()) {
                largestIndex = x;
            }
        }

        return list.get(largestIndex);
    }

    public CompoundTag getLargestCompoundTag(List<CompoundTag> list) {
        int largestIndex = 0;
        for (int x = 0; x < list.size(); x += 1) {
            if (list.get(x).getSize() > list.get(largestIndex).getSize()) {
                largestIndex = x;
            }
        }

        return list.get(largestIndex);
    }

    public static Logger getLogger() {
        return logger;
    }
}
