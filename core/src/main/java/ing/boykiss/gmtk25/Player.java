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

    private final World world;

    public Player(World world, Vector2 spawnPos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnPos);

        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        velocity = new Vector2();

        this.world = world;

        circle.dispose();
    }

    @Override
    public void act(float deltaTime) {
        velocity.y = body.getLinearVelocity().y;

        if (Gdx.input.isKeyPressed(Input.Keys.C) && velocity.y == 0.0f) {
            velocity.y = 150;
        }

        velocity.x = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -10000 * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = 10000 * deltaTime;
        }

        body.setLinearVelocity(velocity);
    }
}
