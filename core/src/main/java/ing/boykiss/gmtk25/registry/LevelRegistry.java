package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.LevelObject;
import ing.boykiss.gmtk25.actor.level.LevelObjectType;
import lombok.Getter;

import java.util.Map;

public class LevelRegistry {
    public static final Level menu = new Level(
            MapRegistry.MENU_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Map.of(
                    new LevelObject(LevelObjectType.SWITCH, new Vector2(31, 8), "Music"),
                    new LevelObject(LevelObjectType.COMMAND, () -> GMTK25.getMusicPlayer().toggleMusic()),
                    new LevelObject(LevelObjectType.SWITCH, new Vector2(8, 3), "Level 0"),
                    new LevelObject(LevelObjectType.COMMAND, () -> GMTK25.getPlayer().levelTransition(LevelAccessor.LEVEL0.getLevel())),
                    new LevelObject(LevelObjectType.SWITCH, new Vector2(14, 3), "Level 1"),
                    new LevelObject(LevelObjectType.COMMAND, () -> GMTK25.getPlayer().levelTransition(LevelAccessor.LEVEL1.getLevel()))
            )
    );

    public static final Level level0 = new Level(
            MapRegistry.DEV_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Map.of(
                    new LevelObject(LevelObjectType.BUTTON, new Vector2(8, 3)),
                    new LevelObject(LevelObjectType.DOOR, new Vector2(13, 5))
            )
    );
    public static final Level level1 = new Level(
            MapRegistry.EMPTY_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Map.of(
                    new LevelObject(LevelObjectType.BUTTON, new Vector2(8, 3)),
                    new LevelObject(LevelObjectType.COMMAND, () -> GMTK25.getPlayer().levelTransition(LevelAccessor.LEVEL0.getLevel()))
            )
    );

    private enum LevelAccessor {
        MENU(menu),
        LEVEL0(level0),
        LEVEL1(level1);

        @Getter
        private final Level level;

        LevelAccessor(Level level) {
            this.level = level;
        }
    }
}
