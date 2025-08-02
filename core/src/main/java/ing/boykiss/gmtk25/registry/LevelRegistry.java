package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.actor.level.Level;

public class LevelRegistry {
    public static final Level level0 = new Level(MapRegistry.DEV_MAP, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE));
    public static final Level level1 = new Level(MapRegistry.EMPTY_MAP, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE));
}
