package ing.boykiss.gmtk25.audio;

public class SongPart {
    public String sound;
    public float length;
    public int repeat;

    public SongPart(String sound, float length, int repeat) {
        this.sound = sound;
        this.length = length;
        this.repeat = repeat;
    }
}
