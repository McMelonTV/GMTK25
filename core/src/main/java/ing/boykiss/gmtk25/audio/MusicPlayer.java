package ing.boykiss.gmtk25.audio;

import games.rednblack.miniaudio.MiniAudio;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    @Getter
    private Songs currentSong;
    private SongPart currentPart;

    private final List<SongPart> partBucket = new ArrayList<>();

    private final MiniAudio ma;
    private int repeat = 0;

    public MusicPlayer(MiniAudio ma) {
        this.ma = ma;
    }

    float time = 0;

    public void update(float deltaTime) {
        if (currentSong == null) {
            return;
        }
        if (currentPart == null) {
            updatePart();
            return;
        }
        if (time <= 0) {
            if (repeat-- <= 0) {
                updatePart();
            }
            playPart();
        }
        time -= deltaTime;
    }

    public void playSong(Songs song) {
        currentSong = song;
        repeat = 0;
        time = 0;
        if (song.initialBucket == null) {
            repopulateBucket();
            return;
        }
        partBucket.clear();
        partBucket.addAll(song.initialBucket);
    }

    private void repopulateBucket() {
        partBucket.clear();
        for (SongPart part : currentSong.parts.keySet()) {
            int amount = currentSong.parts.get(part);
            for (int i = 0; i < amount; i++) {
                partBucket.add(part);
            }
        }
        Collections.shuffle(partBucket);
    }

    private void updatePart() {
        if (partBucket.isEmpty()) {
            repopulateBucket();
        }
        currentPart = partBucket.getFirst();
        partBucket.removeFirst();
        if (currentPart == null) {
            return;
        }
        System.out.println(currentPart.sound);
        repeat = currentPart.repeat;
    }

    private void playPart() {
        time = currentPart.length;
        ma.playSound(currentPart.sound);
    }
}
