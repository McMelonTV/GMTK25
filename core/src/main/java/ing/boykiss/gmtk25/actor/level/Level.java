package ing.boykiss.gmtk25.actor.level;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.interactable.Door;
import ing.boykiss.gmtk25.actor.interactable.InteractableButton;
import ing.boykiss.gmtk25.actor.player.PlayerDummyRenderer;
import ing.boykiss.gmtk25.level.listener.CollisionListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Level extends Actor {
    @Getter
    protected final World world;
    @Getter
    protected final Stage stage;
    @Getter
    protected final Vector2 startPos;
    @Getter
    protected final TiledMap map;
    @Getter
    protected final OrthogonalTiledMapRenderer renderer;
    @Getter
    protected final Body body;

    @Getter
    private final PlayerDummyRenderer dummyPlayerRenderer;

    @Getter
    protected final List<Fixture> hazardSensors = new ArrayList<>();

    // camera bounding box
    @Getter
    private final float cameraLeft;
    @Getter
    private final float cameraRight;
    @Getter
    private final float cameraTop;
    @Getter
    private final float cameraBottom;

    private final List<PolygonShape> shapes = new ArrayList<>();
    private final List<PolygonShape> hazardShapes = new ArrayList<>();

    // interactables must be first button, second door
    public Level(TiledMap map, Vector2 startPos, Map<LevelObject, LevelObject> interactables) {
        this.world = new World(new Vector2(0, -Constants.GRAVITY), true);
        this.stage = new Stage();
        stage.setViewport(GMTK25.getViewport());

        this.map = map;
        this.renderer = new OrthogonalTiledMapRenderer(map, Constants.UNIT_SCALE);
        this.startPos = startPos;

        // Set the camera limits to the map size
        int width = map.getProperties().get("width", Integer.class);
        int height = map.getProperties().get("height", Integer.class);
        this.cameraLeft = Constants.VIEWPORT_WIDTH / 2;
        this.cameraRight = width - Constants.VIEWPORT_WIDTH / 2;
        this.cameraTop = height - Constants.VIEWPORT_HEIGHT / 2 - 0.75f;
        this.cameraBottom = Constants.VIEWPORT_HEIGHT / 2 + 0.75f;

        for (MapLayer layer : this.map.getLayers()) {
            for (MapObject o : layer.getObjects()) {
                if (o instanceof PolygonMapObject po) {
                    PolygonShape shape = new PolygonShape();
                    float[] vertices = po.getPolygon().getTransformedVertices();
                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i] = vertices[i] * Constants.UNIT_SCALE;
                    }
                    shape.set(vertices);
                    if (layer.getName().equals("HazardCollision")) {
                        hazardShapes.add(shape);
                        continue;
                    }
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

        for (PolygonShape shape : this.hazardShapes) {
            FixtureDef def = new FixtureDef();
            def.shape = shape;
            def.isSensor = true; // Make it a sensor
            def.density = 0f; // No density for sensor
            def.friction = 0f;
            def.restitution = 0f;

            Fixture fixture = body.createFixture(def);
            fixture.setUserData("hazard");

            this.hazardSensors.add(fixture);
        }

        stage.addActor(this);

        dummyPlayerRenderer = new PlayerDummyRenderer();
        stage.addActor(dummyPlayerRenderer);

        for (Map.Entry<LevelObject, LevelObject> entry : interactables.entrySet()) {
            if (entry.getKey().getType() == LevelObjectType.BUTTON && entry.getValue().getType() == LevelObjectType.DOOR) {
                Door door = new Door(world, entry.getValue().getPosition());
                stage.addActor(door);

                InteractableButton button = new InteractableButton(world, entry.getKey().getPosition(), door);
                stage.addActor(button);
            }
        }

        //Door door = new Door(world, new Vector2(12, 5));
        //stage.addActor(door);

        //stage.addActor(new InteractableButton(world, new Vector2(8, 3), door));

        world.setContactListener(CollisionListener.INSTANCE); // Set the contact listener for onFloor detection
    }

    @Override
    public void draw(Batch batch, float parentOpacity) {
        renderer.setView(GMTK25.getCamera());
        renderer.render();
    }

    public void dispose() {
        stage.dispose();
        world.dispose();
        renderer.dispose();
    }
}
