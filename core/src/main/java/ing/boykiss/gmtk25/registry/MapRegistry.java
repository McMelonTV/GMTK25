package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class MapRegistry {
    private static final TmxMapLoader mapLoader = new TmxMapLoader();

    public static final TiledMap DEV_MAP = mapLoader.load(AssetRegistry.DEV_MAP_PATH);
    public static final TiledMap EMPTY_MAP = mapLoader.load(AssetRegistry.EMPTY_MAP_PATH);
    public static final TiledMap MENU_MAP = mapLoader.load(AssetRegistry.MENU_MAP_PATH);

    public static final TiledMap LEVEL_1_MAP = mapLoader.load(AssetRegistry.LEVEL_1_MAP_PATH);
}
