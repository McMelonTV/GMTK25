package ing.boykiss.gmtk25.actor.level;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;


public class LevelObject {
    @Getter
    private final LevelObjectType type;

    @Getter
    private final Vector2 position;

    @Getter
    private final Runnable command;

    @Getter
    private String label;

    @Getter
    private boolean rotated;

    public LevelObject(LevelObjectType type, Vector2 position) {
        this.type = type;
        this.position = position;
        this.command = null;
    }

    public LevelObject(LevelObjectType type, Vector2 position, String label) {
        this.type = type;
        this.position = position;
        this.command = null;
        this.label = label;
    }

    public LevelObject(LevelObjectType type, Runnable command) {
        this.type = type;
        this.position = null;
        this.command = command;
    }

}

