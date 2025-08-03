package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.object.Button;
import ing.boykiss.gmtk25.actor.level.object.Door;
import ing.boykiss.gmtk25.actor.level.object.InteractionTarget;
import ing.boykiss.gmtk25.actor.level.object.Replicator;
import ing.boykiss.gmtk25.actor.level.object.Switch;
import ing.boykiss.gmtk25.actor.level.object.WinFlag;
import lombok.Getter;

import java.util.Set;

public class LevelRegistry {
    public static final Level menu;
    public static final Level tutorial1;
    public static final Level tutorial2;
    public static final Level tutorial3;
    public static final Level level1;
    public static final Level level2;

    static {
        Switch musicSwitch = new Switch(new Vector2(32, 8), "Music", new InteractionTarget(null, (s) -> GMTK25.getMusicPlayer().toggleMusic()), GMTK25.getMusicPlayer()::isEnabled);
        Switch level0Switch = new Switch(new Vector2(8, 3), "Tutorial", new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.TUTORIAL1.getLevel())));
        Switch level1Switch = new Switch(new Vector2(14, 3), "Level 1", new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.LEVEL1.getLevel())));

        menu = new Level(
                MapRegistry.MENU_MAP,
                new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
                Set.of(musicSwitch, level0Switch, level1Switch)
        );
    }

    static {
        WinFlag winFlag = new WinFlag(new Vector2(53, 3), null, new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.TUTORIAL2.getLevel(), "Nice!")));

        tutorial1 = new Level(
                MapRegistry.TUTORIAL_MAP,
                new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
                Set.of(winFlag)
        );
    }

    static {
        Door door = new Door(new Vector2(27.5f, 5), null);
        Switch doorSwitch = new Switch(new Vector2(22, 3), null, new InteractionTarget(door, null));

        WinFlag winFlag = new WinFlag(new Vector2(50, 17), null, new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.TUTORIAL3.getLevel(), "Just one more!")));

        tutorial2 = new Level(
                MapRegistry.TUTORIAL2_MAP,
                new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
                Set.of(door, doorSwitch, winFlag)
        );
    }

    static {
        Door door = new Door(new Vector2(27.5f, 5), null);
        Button doorButton = new Button(new Vector2(22, 3), null, new InteractionTarget(door, null));

        Replicator replicator = new Replicator(new Vector2(18.5f, 3), null, new InteractionTarget(null, (r) -> {
            GMTK25.renderStack.add(GMTK25.getPlayer()::startLoop);
        }));

        WinFlag winFlag = new WinFlag(new Vector2(50, 17), null, new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.MENU.getLevel(), "Congrats!")));

        tutorial3 = new Level(
                MapRegistry.TUTORIAL3_MAP,
                new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
                Set.of(door, doorButton, winFlag, replicator)
        );
    }

    static {
        WinFlag winFlag = new WinFlag(new Vector2(35, 14), null, new InteractionTarget(null, (b) -> GMTK25.getPlayer().levelTransition(LevelAccessor.LEVEL2.getLevel(), "Bounce on dat clone!")));

        Door door = new Door(new Vector2(25.5f, 16), null);
        Button doorButton = new Button(new Vector2(16, 7), null, new InteractionTarget(door, null));

        Replicator replicator = new Replicator(new Vector2(22.5f, 7), null, new InteractionTarget(null, (r) -> {
            GMTK25.renderStack.add(GMTK25.getPlayer()::startLoop);
        }));

        level1 = new Level(
            MapRegistry.LEVEL_1_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Set.of(winFlag, door, doorButton, replicator)
        );
    }

    static {
        WinFlag winFlag = new WinFlag(new Vector2(35, 12), null, new InteractionTarget(null, (b) -> GMTK25.getPlayer().levelTransition(LevelAccessor.MENU.getLevel(), "Bounce on dat clone!")));

        Replicator replicator = new Replicator(new Vector2(13.5f, 3), null, new InteractionTarget(null, (r) -> {
            GMTK25.renderStack.add(GMTK25.getPlayer()::startLoop);
        }));

        level2 = new Level(
            MapRegistry.LEVEL_2_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 100 * Constants.UNIT_SCALE),
            Set.of(winFlag, replicator)
        );
    }

    private enum LevelAccessor {
        MENU(menu),
        TUTORIAL1(tutorial1),
        TUTORIAL2(tutorial2),
        TUTORIAL3(tutorial3),
        LEVEL1(level1),
        LEVEL2(level2);

        @Getter
        private final Level level;

        LevelAccessor(Level level) {
            this.level = level;
        }
    }
}
