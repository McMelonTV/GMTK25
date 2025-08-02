package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.actor.level.EntityType;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.LevelEntity;

import java.util.Map;

public class LevelRegistry {
    public static final Level level0 = new Level(MapRegistry.DEV_MAP, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
        Map.of(
            new LevelEntity(EntityType.BUTTON, new Vector2(8, 3)),
            new LevelEntity(EntityType.DOOR, new Vector2(13, 5))
        )
    );
    public static final Level level1 = new Level(MapRegistry.EMPTY_MAP, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
        Map.of()
    );
}
