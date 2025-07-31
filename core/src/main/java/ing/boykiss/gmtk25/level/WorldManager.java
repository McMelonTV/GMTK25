package ing.boykiss.gmtk25.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import ing.boykiss.gmtk25.utils.Constants;

public class WorldManager {
    public static World world = new World(new Vector2(0, -Constants.GRAVITY), true);
    public static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
}
