package ing.boykiss.gmtk25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import lombok.Getter;

public class Player extends Actor {
    @Getter
    private final Body body;

    @Getter
    private final Vector2 velocity;

    @Getter
    private boolean isOnFloor;

    public int collisionCount = 0;

    private final World world;

    public Player(World world, Vector2 spawnPos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnPos);

        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(1f);

        // Body fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);

        // Sensor fixture for floor detection
        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(0.99f);
        sensorCircle.setPosition(new Vector2(0, -0.1f)); // Position it below the player

        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorCircle;
        sensorFixtureDef.isSensor = true; // Make it a sensor
        sensorFixtureDef.density = 0f; // No density for sensor
        sensorFixtureDef.friction = 0f;
        sensorFixtureDef.restitution = 0f;

        Fixture sensor = body.createFixture(sensorFixtureDef);
        sensor.setUserData("player_sensor"); // Set user data for identification

        velocity = new Vector2();

        this.world = world;

        circle.dispose();
    }

    @Override
    public void act(float deltaTime) {
        isOnFloor = collisionCount > 0; // update isOnFloor based on collision count

        velocity.y = body.getLinearVelocity().y;

        if (Gdx.input.isKeyPressed(Input.Keys.C) && isOnFloor) {
            velocity.y = 7500 * deltaTime; // Jump force
        }

        velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -5000 * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = 5000 * deltaTime;
        }

        body.setLinearVelocity(velocity);
    }
}
