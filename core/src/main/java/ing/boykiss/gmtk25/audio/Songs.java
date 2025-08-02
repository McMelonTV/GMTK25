package ing.boykiss.gmtk25.audio;

import java.util.List;
import java.util.Map;

public class Songs {
    public List<SongPart> initialBucket;
    public Map<SongPart, Integer> parts;

    // Integer here is for amount of times it shows up in the bucket
    public Songs(List<SongPart> initialBucket, Map<SongPart, Integer> parts) {
        this.initialBucket = initialBucket;
        this.parts = parts;
    }
}
