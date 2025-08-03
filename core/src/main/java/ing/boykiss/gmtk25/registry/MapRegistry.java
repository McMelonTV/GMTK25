package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class MapRegistry {
    private static final TmxMapLoader mapLoader = new TmxMapLoader();

    public static final TiledMap MENU_MAP = mapLoader.load(AssetRegistry.MENU_MAP_PATH);

    public static final TiledMap TUTORIAL_MAP = mapLoader.load(AssetRegistry.TUTORIAL_MAP_PATH);
    public static final TiledMap TUTORIAL2_MAP = mapLoader.load(AssetRegistry.TUTORIAL2_MAP_PATH);
    public static final TiledMap TUTORIAL3_MAP = mapLoader.load(AssetRegistry.TUTORIAL3_MAP_PATH);

    public static final TiledMap LEVEL_1_MAP = mapLoader.load(AssetRegistry.LEVEL_1_MAP_PATH);
}
