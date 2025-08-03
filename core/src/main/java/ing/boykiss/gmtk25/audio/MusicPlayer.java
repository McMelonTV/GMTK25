package ing.boykiss.gmtk25.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Timer;
import ing.boykiss.gmtk25.Constants;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    @Getter
    private boolean isEnabled = true;

    public MusicPlayer(Song initialSong) {
        playSong(initialSong);
    }

    @Getter
    private Song currentSong;
    private SongPart currentPart;

    @Getter
    private float volume = Constants.VOLUME;

    public void setVolume(float value) {
        volume = value;
        for (Music music : activeMusics) {
            music.setVolume(volume);
        }
    }

    public void toggleMusic() {
        isEnabled = !isEnabled;

        if (isEnabled) {
            setVolume(Constants.VOLUME);
        } else {
            setVolume(0f);
        }
    }

    public MusicPlayer() {
    }

    private final List<SongPart> partBucket = new ArrayList<>();
    private final List<Music> activeMusics = new ArrayList<>();

    private int repeat = 0;

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

    public void playSong(Song song) {
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
//        System.out.println(currentPart.sound);
        repeat = currentPart.repeat;
    }

    private void playPart() {
        time = currentPart.length;
        Music music = Gdx.audio.newMusic(Gdx.files.internal(currentPart.sound));
        activeMusics.add(music);
        music.play();
        music.setVolume(volume);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                music.dispose();
                activeMusics.remove(music);
            }
        }, currentPart.length + 2);
    }
}
