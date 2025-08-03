package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class AssetRegistry {
    public static final Texture PLAYER_TEXTURE = new Texture("textures/player.png");

    public static final Texture PLAYER_IDLE_TEXTURE = new Texture("textures/player_idle.png");
    public static final Texture PLAYER_RUN_TEXTURE = new Texture("textures/player_run.png");
    public static final Texture PLAYER_JUMP_TEXTURE = new Texture("textures/player_jump.png");
    public static final Texture PLAYER_FALL_TEXTURE = new Texture("textures/player_fall.png");

    public static final Texture FILL_TEXTURE = new Texture("textures/fill.png");

    public static final Texture PAUSED_TEXT_TEXTURE = new Texture("textures/paused.png");

    public static final Texture BUTTON_TEXTURE = new Texture("textures/button.png");
    public static final Texture DOOR_TEXTURE = new Texture("textures/door.png");
    public static final Texture SWITCH_TEXTURE = new Texture("textures/lever.png");

    public static final String BACKGROUND_VERTEX_SHADER_PATH = "shaders/background.vsh";
    public static final String BACKGROUND_FRAGMENT_SHADER_PATH = "shaders/background.fsh";

    public static final String DEV_MAP_PATH = "tiledmaps/dev_map.tmx";
    public static final String EMPTY_MAP_PATH = "tiledmaps/empty_map.tmx";
    public static final String MENU_MAP_PATH = "tiledmaps/main_menu_map.tmx";

    public static final String FONT_PATH = "fonts/SegoeUI.ttf";
    public static final BitmapFont FONT;

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
}
