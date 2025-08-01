package ing.boykiss.gmtk25.actor.interactable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.level.listener.InteractableCollisionListener;
import lombok.Getter;

public class InteractableButton extends Interactable {
    @Getter
    private Body body;
    @Getter
    private Fixture sensor;

    @Getter
    private Vector2 position;

    @Getter
    private final EventHandler<Event> onEnter = new EventHandler<>();
    @Getter
    private final EventHandler<Event> onExit = new EventHandler<>();

    private int collisions = 0;

    public InteractableButton(World world, Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5.0f * Constants.UNIT_SCALE, 1.0f * Constants.UNIT_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);

        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(4.0f * Constants.UNIT_SCALE, 3.0f * Constants.UNIT_SCALE, new Vector2(0, 3 * Constants.UNIT_SCALE), 0);

        FixtureDef sensorDef = new FixtureDef();
        sensorDef.shape = sensorShape;
        sensorDef.density = 0f;
        sensorDef.friction = 0f;
        sensorDef.restitution = 0f;
        sensorDef.isSensor = true;

        sensor = body.createFixture(sensorDef);
        sensor.setUserData("button");

        onEnter.addListener(event -> {
            System.out.println("pressed");
            collisions++;
        });
        onExit.addListener(event -> {
            System.out.println("unpressed");
            collisions--;
        });

        this.position = position;
        body.setTransform(position.x, position.y, 0);
    }

    @Override
    public void act(float deltaTime) {
    }

    public boolean isPressed() {
        return collisions <= 0;
    }
}
