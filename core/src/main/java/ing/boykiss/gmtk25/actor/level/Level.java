package ing.boykiss.gmtk25.actor.level;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.Constants;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Level extends Actor {
    @Getter
    protected final TiledMap map;
    @Getter
    protected final OrthogonalTiledMapRenderer renderer;
    @Getter
    protected final OrthographicCamera camera;
    @Getter
    protected final Body body;

    private final List<PolygonShape> shapes = new ArrayList<>();

    public Level(World world, TiledMap map, OrthographicCamera camera) {
        this.map = map;
        this.camera = camera;
        this.renderer = new OrthogonalTiledMapRenderer(map, Constants.UNIT_SCALE);

        for (MapLayer layer : this.map.getLayers()) {
            for (MapObject o : layer.getObjects()) {
                if (o instanceof PolygonMapObject po) {
                    PolygonShape shape = new PolygonShape();
                    float[] vertices = po.getPolygon().getTransformedVertices();
                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i] = vertices[i] * Constants.UNIT_SCALE;
                    }
                    shape.set(vertices);
                    this.shapes.add(shape);
                }
            }
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        this.body = world.createBody(bodyDef);
        for (PolygonShape shape : this.shapes) {
            body.createFixture(shape, 0.0f);
        }
    }

    @Override
    public void draw(Batch batch, float parentOpacity) {
        renderer.setView(camera);
        renderer.render();
    }
}
