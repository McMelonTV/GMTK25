package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class AssetRegistry {
    public static final Texture PLAYER_TEXTURE = new Texture("textures/player/player.png");

    public static final Texture PLAYER_IDLE_TEXTURE = new Texture("textures/player/idle.png");
    public static final Texture PLAYER_RUN_TEXTURE = new Texture("textures/player/run.png");
    public static final Texture PLAYER_JUMP_TEXTURE = new Texture("textures/player/jump.png");
    public static final Texture PLAYER_FALL_TEXTURE = new Texture("textures/player/fall.png");

    public static final Texture FILL_TEXTURE = new Texture("textures/fill.png");

    public static final Texture PAUSED_TEXT_TEXTURE = new Texture("textures/paused.png");

    public static final Texture BUTTON_TEXTURE = new Texture("textures/button.png");
    public static final Texture DOOR_TEXTURE = new Texture("textures/door.png");
    public static final Texture SWITCH_TEXTURE = new Texture("textures/lever.png");

    public static final Texture WIN_FLAG_TEXTURE = new Texture("textures/winflag.png");
    public static final Texture REPLICATOR_TEXTURE = new Texture("textures/replicator.png");

    public static final String TUTORIAL_MAP_PATH = "tiledmaps/tutorial_map.tmx";
    public static final String TUTORIAL2_MAP_PATH = "tiledmaps/tutorial2_map.tmx";
    public static final String TUTORIAL3_MAP_PATH = "tiledmaps/tutorial3_map.tmx";
    public static final String DEV_MAP_PATH = "tiledmaps/dev_map.tmx";
    public static final String EMPTY_MAP_PATH = "tiledmaps/empty_map.tmx";
    public static final String MENU_MAP_PATH = "tiledmaps/main_menu_map.tmx";
    //24x34
    public static final Texture KEYBOARD_TEXTURE = new Texture("textures/tilesets/tilemap_white_packed.png");

    public static final String LEVEL_1_MAP_PATH = "tiledmaps/maps/level_1.tmx";

    public static final String FONT_PATH = "fonts/SegoeUI.ttf";
    public static final BitmapFont FONT;
    public static final BitmapFont FONT_LARGE;

    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.borderWidth = 1;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0.1f, 0.1f, 0.1f, 0.75f);
        BitmapFont font = generator.generateFont(parameter); // font size 24 pixels
        generator.dispose();
        font.setUseIntegerPositions(false);
        FONT = font;
    }

    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        parameter.borderWidth = 1;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0.1f, 0.1f, 0.1f, 0.75f);
        BitmapFont font = generator.generateFont(parameter); // font size 24 pixels
        generator.dispose();
        font.setUseIntegerPositions(false);
        FONT_LARGE = font;
    }
}
