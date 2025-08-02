package ing.boykiss.gmtk25.actor.level;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;


public class LevelObject {
    @Getter
    private final LevelObjectType type;

    @Getter
    private final Vector2 position;

    public LevelObject(LevelObjectType type, Vector2 position) {
        this.type = type;
        this.position = position;
    }

}

