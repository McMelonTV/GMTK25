package ing.boykiss.gmtk25.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.AssetRegistry;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.AssetRegistry;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.input.event.InputEvent;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import ing.boykiss.gmtk25.world.ReplayManager;
import lombok.Getter;

public class Player extends Actor {
    @Getter
    private final Body body;

    @Getter
    private final Vector2 velocity;

    @Getter
    private boolean isOnFloor;

    private static final int SPEED = 5000; // Speed of the player
    private static final int JUMP_FORCE = 80; // Jump force of the player
    private static final int MIN_JUMP_FORCE = 25; // Jump force of the player when jump is released

    private static final int COYOTE_TIME_DURATION = 6; // Duration of coyote time in ticks
    private boolean CoyoteTimeActive = false;
    private int coyoteTimeCounter = 0;

    private boolean jumping = false;

    private int jumpBuffer = 0;
    private static final int JUMP_BUFFER_DURATION = 8; // Duration of jump buffer in ticks

    private final Sprite sprite = new Sprite(AssetRegistry.PLAYER_TEXTURE);

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> runAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> fallAnimation;
    private float stateTime = 0f;

    public int collisionCount = 0;

    public Player(World world, Vector2 spawnPos) {
        idleAnimation = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_IDLE_TEXTURE, 2, 2, new int[]{
            0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 3,
        }, 0.1f);
        runAnimation = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_RUN_TEXTURE, 3, 3, new int[]{
            0, 1, 2, 3, 4, 5, 6, 7,
        }, 0.05f);
        jumpAnimation = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_JUMP_TEXTURE, 2, 2, new int[]{
            0, 1, 2,
        }, 0.07f);
        fallAnimation = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_FALL_TEXTURE, 1, 1, new int[]{
            0,
        }, 0.1f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnPos);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(3.95f * Constants.UNIT_SCALE, 8.0f * Constants.UNIT_SCALE);

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
        sensorShape.setAsBox(3.8f * Constants.UNIT_SCALE, 7.94f * Constants.UNIT_SCALE, new Vector2(0, -0.02f * Constants.UNIT_SCALE), 0);

        FixtureDef sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.shape = sensorShape;
        sensorFixtureDef.isSensor = true; // Make it a sensor
        sensorFixtureDef.density = 0f; // No density for sensor
        sensorFixtureDef.friction = 0f;
        sensorFixtureDef.restitution = 0f;

        Fixture sensor = body.createFixture(sensorFixtureDef);
        sensor.setUserData("player_sensor"); // Set user data for identification

        velocity = new Vector2();

        Input.getEventHandler(InputEvent.class).addListener(this::onInputEvent);

        shape.dispose();

        ReplayManager.INSTANCE.startRecording();
    }

    private final float spriteHeightOffset = (sprite.getHeight() * Constants.UNIT_SCALE) * 0.25f;
    private final float spriteWidthOffset = (sprite.getWidth() * Constants.UNIT_SCALE) * 0.5f;
    private final float spriteHeightScaled = sprite.getHeight() * Constants.UNIT_SCALE;
    private final float spriteWidthScaled = sprite.getWidth() * Constants.UNIT_SCALE;

    private final Vector2 spriteScale = new Vector2(1, 1);

    @Override
    public void draw(Batch batch, float parentOpacity) {
        Animation<TextureRegion> currentAnimation = idleAnimation;
        boolean looping = true;

        if (jumping) {
            currentAnimation = jumpAnimation;
            looping = false;
            if (jumpAnimation.isAnimationFinished(stateTime)) {
                jumping = false;
            }
        }
        if (!isOnFloor && !jumping) {
            currentAnimation = fallAnimation;
            looping = true;
        }
        if (currentAnimation == idleAnimation && velocity.x != 0) {
            currentAnimation = runAnimation;
            looping = true;
        }

        if (currentAnimation == null) {
            return;
        }

        if (!GMTK25.isPaused) stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, looping);
        batch.draw(currentFrame,
            body.getPosition().x - spriteWidthOffset * spriteScale.x,
            body.getPosition().y - spriteHeightOffset * spriteScale.y,
            spriteWidthScaled * spriteScale.x,
            spriteHeightScaled * spriteScale.y
        );
    }

    @Override
    public void act(float deltaTime) { // aka tick
        ReplayManager.INSTANCE.recordFrame(body.getPosition(), velocity);
        isOnFloor = collisionCount > 0; // update isOnFloor based on collision count

        tickCoyoteTime();

        velocity.y = body.getLinearVelocity().y;
        velocity.x = 0; // Reset horizontal velocity before applying new input

        if (velocity.y < 0 && jumping) {
            jumping = false;
        }

        handleInput(deltaTime);

        body.setLinearVelocity(velocity);

        // Snapping x and y to pixels when their respective velocity is 0
        float x = Math.round(body.getPosition().x / Constants.UNIT_SCALE) * Constants.UNIT_SCALE;
        float y = Math.round(body.getPosition().y / Constants.UNIT_SCALE) * Constants.UNIT_SCALE;

        body.setTransform(
            body.getLinearVelocity().x == 0.0f ? x : body.getPosition().x,
            body.getLinearVelocity().y == 0.0f ? y : body.getPosition().y,
            body.getAngle()
        );
    }

    /**
     * Ticks the coyote time counter.
     */
    private void tickCoyoteTime() {
        if (isOnFloor) {
            coyoteTimeCounter = 0;
            CoyoteTimeActive = true; // Reset coyote time when on the floor
        } else {
            if (CoyoteTimeActive) {
                coyoteTimeCounter++;
                if (coyoteTimeCounter >= COYOTE_TIME_DURATION) {
                    CoyoteTimeActive = false; // Deactivate coyote time after duration
                }
            } else {
                coyoteTimeCounter = 0; // Reset counter if not active
            }
        }
    }


    /**
     * Handles the input buffer and applies the actions.
     * This method is called on the main thread.
     */
    private void handleInput(float deltaTime) {
        if (jumpBuffer > 0) {
            jumpBuffer--;
        }

        if (jumpBuffer > 0 && (isOnFloor || CoyoteTimeActive)) { // jump from buffer
            velocity.y = JUMP_FORCE;
            jumpBuffer = 0; // Reset jump buffer after applying jump
            stateTime = 0;
            jumping = true;
        }

        if (Input.keyPressed(Input.Keys.RIGHT) || Input.keyPressed(Input.Keys.D)) {
            velocity.x += SPEED * deltaTime;
            spriteScale.x = 1; // Face right
        }
        if (Input.keyPressed(Input.Keys.LEFT) || Input.keyPressed(Input.Keys.A)) {
            velocity.x += -SPEED * deltaTime;
            spriteScale.x = -1; // Face left
        }
    }

    private void onInputEvent(InputEvent event) {
        if (event.key().equals(Input.Keys.C) || event.key().equals(Input.Keys.UP) || event.key().equals(Input.Keys.W) || event.key().equals(Input.Keys.SPACE)) {
            if (event.released()) {
                jumpBuffer = 0;
                if (velocity.y > MIN_JUMP_FORCE) {
                    body.setLinearVelocity(body.getLinearVelocity().x, MIN_JUMP_FORCE);
                }
                return;
            }
            jumpBuffer = JUMP_BUFFER_DURATION;
        }
    }
}
