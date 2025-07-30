package ing.boykiss.gmtk25;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class WorldManager {
    public static World world = new World(new Vector2(0, -10), true);
    public static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
}
