package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.object.*;
import lombok.Getter;

import java.util.Set;

public class LevelRegistry {
    public static final Level menu;
    public static final Level level0;
    public static final Level level1;
    public static final Level tutorial1;
    public static final Level tutorial2;
    public static final Level tutorial3;

    static {
        Switch musicSwitch = new Switch(new Vector2(31, 8), "Music", new InteractionTarget(null, (s) -> GMTK25.getMusicPlayer().toggleMusic()), GMTK25.getMusicPlayer()::isEnabled);
        Switch level0Switch = new Switch(new Vector2(8, 3), "Tutorial", new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.TUTORIAL.getLevel())));
        Switch level1Switch = new Switch(new Vector2(14, 3), "Level 1", new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.LEVEL1.getLevel())));

        menu = new Level(
            MapRegistry.MENU_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Set.of(musicSwitch, level0Switch, level1Switch)
        );
    }

    static {
        Door door = new Door(new Vector2(13, 5), null);
        Button doorButton = new Button(new Vector2(8, 3), null, new InteractionTarget(door, null));

        WinFlag winFlag = new WinFlag(new Vector2(35, 8), null, new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.MENU.getLevel(), "You win!")));

        Replicator replicator = new Replicator(new Vector2(6, 3), null, new InteractionTarget(null, (r) -> {
            GMTK25.renderStack.add(GMTK25.getPlayer()::startLoop);
        }), false);

        level0 = new Level(
            MapRegistry.DEV_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Set.of(door, doorButton, winFlag, replicator)
        );
    }

    static {
        WinFlag winFlag = new WinFlag(new Vector2(45, 7), null, new InteractionTarget(null, (b) -> GMTK25.getPlayer().levelTransition(LevelAccessor.MENU.getLevel(), "Winner, winner I lost my chicken dinner!")));

        Door door = new Door(new Vector2(31.5f, 9), null);
        Button doorButton = new Button(new Vector2(21, 7), null, new InteractionTarget(door, null));

        level1 = new Level(
            MapRegistry.LEVEL_1_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Set.of(winFlag, door, doorButton)
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

        //50 17

        WinFlag winFlag = new WinFlag(new Vector2(50, 17), null, new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.TUTORIAL3.getLevel(), "You win!")));

        tutorial2 = new Level(
            MapRegistry.TUTORIAL2_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Set.of(door, doorSwitch, winFlag)
        );
    }

    static {
        Door door = new Door(new Vector2(27.5f, 5), null);
        Button doorButton = new Button(new Vector2(22, 3), null, new InteractionTarget(door, null));


        Replicator replicator = new Replicator(new Vector2(18, 3), null, new InteractionTarget(null, (r) -> {
            GMTK25.renderStack.add(GMTK25.getPlayer()::startLoop);
        }), false);

        //50 17

        WinFlag winFlag = new WinFlag(new Vector2(50, 17), null, new InteractionTarget(null, (s) -> GMTK25.getPlayer().levelTransition(LevelAccessor.MENU.getLevel(), "You win!")));

        tutorial3 = new Level(
            MapRegistry.TUTORIAL3_MAP,
            new Vector2(32 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE),
            Set.of(door, doorButton, winFlag, replicator)
        );
    }

    private enum LevelAccessor {
        MENU(menu),
        TUTORIAL(tutorial1),
        TUTORIAL2(tutorial2),
        TUTORIAL3(tutorial3),
        LEVEL0(level0),
        LEVEL1(level1);

        @Getter
        private final Level level;

        LevelAccessor(Level level) {
            this.level = level;
        }
    }
}
