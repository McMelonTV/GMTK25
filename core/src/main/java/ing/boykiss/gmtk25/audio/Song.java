package ing.boykiss.gmtk25.audio;

import java.util.Map;

public class Song {
    public Map<SongPart, Float> parts;

    public Song(Map<SongPart, Float> parts) {
        this.parts = parts;
    }
}
