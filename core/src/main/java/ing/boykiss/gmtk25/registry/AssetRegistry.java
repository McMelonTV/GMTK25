package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.graphics.Texture;

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

    public static final String BACKGROUND_VERTEX_SHADER_PATH = "shaders/background.vsh";
    public static final String BACKGROUND_FRAGMENT_SHADER_PATH = "shaders/background.fsh";

    public static final String DEV_MAP_PATH = "tiledmaps/dev_map.tmx";
    public static final String EMPTY_MAP_PATH = "tiledmaps/empty_map.tmx";
    public static final String MENU_MAP_PATH = "tiledmaps/main_menu_map.tmx";
}
