package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class AssetRegistry {
    public static final Texture PLAYER_TEXTURE = new Texture("textures/player/player.png");

    public static final Texture PLAYER_IDLE_TEXTURE = new Texture("textures/player/idle.png");
    public static final Texture PLAYER_RUN_TEXTURE = new Texture("textures/player/run.png");
    public static final Texture PLAYER_JUMP_TEXTURE = new Texture("textures/player/jump.png");
    public static final Texture PLAYER_FALL_TEXTURE = new Texture("textures/player/fall.png");

    public static final Texture FILL_TEXTURE = new Texture("textures/fill.png");

    public static final Texture PAUSED_TEXT_TEXTURE = new Texture("textures/paused.png");
    public static final Texture CONTROLS_TEXTURE = new Texture("textures/controls.png");

    public static final Texture BUTTON_TEXTURE = new Texture("textures/button.png");
    public static final Texture DOOR_TEXTURE = new Texture("textures/door.png");
    public static final Texture SWITCH_TEXTURE = new Texture("textures/lever.png");

    public static final Texture WIN_FLAG_TEXTURE = new Texture("textures/winflag.png");
    public static final Texture REPLICATOR_TEXTURE = new Texture("textures/replicator.png");

    public static final String MENU_MAP_PATH = "tiledmaps/maps/main_menu_map.tmx";

    public static final String TUTORIAL_MAP_PATH = "tiledmaps/maps/tutorial/tutorial1_map.tmx";
    public static final String TUTORIAL2_MAP_PATH = "tiledmaps/maps/tutorial/tutorial2_map.tmx";
    public static final String TUTORIAL3_MAP_PATH = "tiledmaps/maps/tutorial/tutorial3_map.tmx";

    public static final String LEVEL_1_MAP_PATH = "tiledmaps/maps/level_1.tmx";

    public static final Texture KEYBOARD_TEXTURE = new Texture("textures/tilesets/tilemap_white_packed.png");

    public static final BitmapFont FONT = new BitmapFont(Gdx.files.internal("fonts/SegoeUI.fnt"));
    public static final BitmapFont FONT_LARGE = new BitmapFont(Gdx.files.internal("fonts/SegoeUIlarge.fnt"));

    static {
        FONT.setUseIntegerPositions(false);
        FONT_LARGE.setUseIntegerPositions(false);
    }
}
