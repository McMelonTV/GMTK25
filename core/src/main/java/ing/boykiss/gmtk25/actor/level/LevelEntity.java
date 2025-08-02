package ing.boykiss.gmtk25.actor.level;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;


public class LevelEntity {
    @Getter
    private final EntityType type;

    @Getter
    private final Vector2 position;

    public LevelEntity(EntityType type, Vector2 position) {
        this.type = type;
        this.position = position;
    }

}

