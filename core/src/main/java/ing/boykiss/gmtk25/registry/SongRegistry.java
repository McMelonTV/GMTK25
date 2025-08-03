package ing.boykiss.gmtk25.registry;

import ing.boykiss.gmtk25.audio.Song;

import java.util.List;
import java.util.Map;

public class SongRegistry {
    public static final Song MAIN_SONG = new Song(
        List.of(
            SoundRegistry.MAIN_SONG_PART_C,
            SoundRegistry.MAIN_SONG_PART_A,
            SoundRegistry.MAIN_SONG_PART_B
        ),
        Map.of(
            SoundRegistry.MAIN_SONG_PART_A, 2,
            SoundRegistry.MAIN_SONG_PART_D, 2,
            SoundRegistry.MAIN_SONG_PART_B, 2,
            SoundRegistry.MAIN_SONG_PART_C, 1
        )
    );
}
