package ing.boykiss.gmtk25;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import lombok.Getter;

import java.util.*;

public class Level extends Actor {
    @Getter
    protected final TiledMap map;
    @Getter
    protected final OrthogonalTiledMapRenderer renderer;
    @Getter
    protected final OrthographicCamera camera;
    @Getter
    protected final List<Body> bodies;

    private final Map<PolygonShape, Vector2> shapes = new HashMap<>();

    public Level(World world, TiledMap map, OrthographicCamera camera) {
        this.map = map;
        this.camera = camera;
        this.renderer = new OrthogonalTiledMapRenderer(map);

        for (MapLayer layer : this.map.getLayers()) {
            for (MapObject o : layer.getObjects()) {
                if (o instanceof PolygonMapObject po) {
                    PolygonShape shape = new PolygonShape();
                    shape.set(po.getPolygon().getVertices());
                    float x = (float)po.getProperties().get("x");
                    float y = (float)po.getProperties().get("y");
                    this.shapes.put(shape, new Vector2(x, y));
                }
            }
        }

        List<Body> bodies = new ArrayList<>();
        for (PolygonShape shape : this.shapes.keySet()) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Vector2 pos = this.shapes.get(shape);
            bodyDef.position.x = pos.x;
            bodyDef.position.y = pos.y;

            Body body = world.createBody(bodyDef);
            body.createFixture(shape, 0.0f);
            bodies.add(body);
        }

        this.bodies = Collections.unmodifiableList(bodies);
    }

    @Override
    public void draw(Batch batch, float parentOpacity) {
        renderer.setView(camera);
        renderer.render();
    }
}
