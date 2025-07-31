package ing.boykiss.gmtk25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.input.Input;
import lombok.Getter;

public class Player extends Actor {
    @Getter
    private final Body body;

    @Getter
    private final Vector2 velocity;

    @Getter
    private boolean isOnFloor;

    private final Sprite sprite = new Sprite(TextureRegistry.PLAYER_TEXTURE);

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> runAnimation;
    private float stateTime = 0f;

    public int collisionCount = 0;

    private final World world;

    public Player(World world, Vector2 spawnPos) {
        idleAnimation = AnimationUtils.createAnimationSheet(TextureRegistry.PLAYER_IDLE, 2, 2, new int[] {
            0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 3
        }, 0.1f);
        runAnimation = AnimationUtils.createAnimationSheet(TextureRegistry.PLAYER_RUN, 3, 3, new int[] {
            0, 1, 2, 3, 4, 5, 6, 7
        }, 0.05f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnPos);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6.0f * Constants.UNIT_SCALE, 8.0f * Constants.UNIT_SCALE);

        // Body fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        body.setFixedRotation(true);

        // Sensor fixture for floor detection
        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(5.94f * Constants.UNIT_SCALE, 7.94f * Constants.UNIT_SCALE, new Vector2(0, -0.1f * Constants.UNIT_SCALE), 0);

        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorShape;
        sensorFixtureDef.isSensor = true; // Make it a sensor
        sensorFixtureDef.density = 0f; // No density for sensor
        sensorFixtureDef.friction = 0f;
        sensorFixtureDef.restitution = 0f;

        Fixture sensor = body.createFixture(sensorFixtureDef);
        sensor.setUserData("player_sensor"); // Set user data for identification

        velocity = new Vector2();

        this.world = world;

        shape.dispose();
    }

    private final float spriteHeightOffset = (sprite.getHeight() * Constants.UNIT_SCALE) * 0.25f;
    private final float spriteWidthOffset = (sprite.getWidth() * Constants.UNIT_SCALE) * 0.5f;
    private final float spriteHeightScaled = sprite.getHeight() * Constants.UNIT_SCALE;
    private final float spriteWidthScaled = sprite.getWidth() * Constants.UNIT_SCALE;

    private final Vector2 spriteScale = new Vector2(1, 1);

    public void draw(SpriteBatch spriteBatch) {
        Animation<TextureRegion> currentAnimation = velocity.x == 0 ? idleAnimation : runAnimation;

        if (currentAnimation == null) {
            return;
        }

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        spriteBatch.draw(currentFrame,
            body.getPosition().x - spriteWidthOffset * spriteScale.x,
            body.getPosition().y - spriteHeightOffset * spriteScale.y,
            spriteWidthScaled * spriteScale.x,
            spriteHeightScaled * spriteScale.y
        );
    }

    @Override
    public void act(float deltaTime) {
        isOnFloor = collisionCount > 0; // update isOnFloor based on collision count

        velocity.y = body.getLinearVelocity().y;

        if (Input.keyPressed(Input.Keys.C) && isOnFloor) {
            velocity.y = 7500 * deltaTime; // Jump force
        }

        velocity.x = 0;
        if (Input.keyPressed(Input.Keys.RIGHT)) {
            velocity.x += 5000 * deltaTime;
            spriteScale.x = 1;
        }
        if (Input.keyPressed(Input.Keys.LEFT)) {
            velocity.x += -5000 * deltaTime;
            spriteScale.x = -1;
        }

        body.setLinearVelocity(velocity);
    }
}
