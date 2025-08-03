package ing.boykiss.gmtk25.actor.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.object.Interactable;
import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.event.input.InputEvent;
import ing.boykiss.gmtk25.event.player.PlayerHitHazardEvent;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.input.InputKeys;
import ing.boykiss.gmtk25.level.replay.ReplayManager;
import ing.boykiss.gmtk25.registry.AnimationRegistry;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Player extends Actor {
    @Getter
    private final Vector2 velocity = new Vector2();

    private Animation<TextureRegion> currentAnimation = AnimationRegistry.PLAYER_IDLE;
    private boolean currentAnimationLooping = true;

    private final Animation<TextureRegion> keyboardKeymap = AnimationUtils.createAnimationSheet(AssetRegistry.KEYBOARD_TEXTURE, 34, 24, new int[]{359}, 0.2f);

    @Getter
    private boolean isOnFloor;

    @Getter
    private final EventHandler<Event> onJump = new EventHandler<>();

    private static final float SPEED = 1250; // Speed of the player
    @Getter
    private static final float JUMP_FORCE = 40; // Jump force of the player
    private static final float MIN_JUMP_FORCE = 10; // Jump force of the player when jump is released

    private static final int COYOTE_TIME_DURATION = 6; // Duration of coyote time in ticks
    @Getter
    private Level level;
    private int coyoteTimeCounter = 0;

    private boolean jumping = false;

    private int jumpBuffer = 0;
    private static final int JUMP_BUFFER_DURATION = 8; // Duration of jump buffer in ticks

    private final Sprite sprite = new Sprite(AssetRegistry.PLAYER_TEXTURE);

    private float stateTime = 0f;

    public int collisionCount = 0;
    @Getter
    private Body body;
    private boolean coyoteTimeActive = false;

    public final List<PlayerDummy> dummies = new ArrayList<>();

    @Getter
    @Setter
    private Interactable nearestInteractable;

    public Player(Level level) {
        respawn(level);

        Input.getEventHandler(InputEvent.class).addListener(this::onInputEvent);
        EventBus.addListener(PlayerHitHazardEvent.class, this::onPlayerHitHazard);
    }

    private void reloadBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(level.getStartPos());

        body = level.getWorld().createBody(bodyDef);

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

        FixtureDef hurtBoxFixtureDef = new FixtureDef();
        hurtBoxFixtureDef.shape = shape;
        hurtBoxFixtureDef.isSensor = true; // Make it a sensor
        hurtBoxFixtureDef.density = 0f; // No density for sensor
        hurtBoxFixtureDef.friction = 0f;
        hurtBoxFixtureDef.restitution = 0f;

        Fixture hurtBox = body.createFixture(hurtBoxFixtureDef);
        hurtBox.setUserData("player_hurtbox"); // Set user data for identification

        shape.dispose();
        sensorShape.dispose();
    }

    private void reload() {
        reloadBody();
        coyoteTimeActive = false;
        coyoteTimeCounter = 0;
        jumping = false;
        jumpBuffer = 0;
        stateTime = 0f;
        collisionCount = 0;
        velocity.set(new Vector2());
        currentAnimation = AnimationRegistry.PLAYER_IDLE;
        currentAnimationLooping = true;
        ReplayManager.INSTANCE.startRecording();
        this.level.getStage().addActor(this);
    }

    public void teleport(Vector2 pos, float angle) {
        body.setTransform(pos, angle);
    }

    private void respawn() {
        respawnInternal(null);
    }

    private void respawn(Level newLevel) {
        Level oldLevel = this.level;
        respawnInternal(newLevel);
        if (oldLevel != null) oldLevel.resetInteractables();
    }

    private void respawnInternal(Level newLevel) {
        if (body != null) this.level.getWorld().destroyBody(body);
        destroyDummies();
        if (newLevel != null) this.level = newLevel;
        reload();
        teleportToLevelStart();
    }

    private void destroyDummies() {
        // we have to create a copy to prevent ConcurrentModificationException since PlayerDummy::destroy removes the dummy from the dummies list
        List<PlayerDummy> dummiesCopy = new ArrayList<>(dummies);
        dummiesCopy.forEach(PlayerDummy::destroy);
        dummiesCopy.clear(); // might help with memory, not sure
    }

    public void levelTransition(Level level) {
        AnimationUtils.startTransitionAnimation(() -> respawn(level), "Loading Level...");
    }

    public void levelTransition(Level level, String transitionText) {
        AnimationUtils.startTransitionAnimation(() -> respawn(level), transitionText);
    }

    public void kill() {
        AnimationUtils.startTransitionAnimation(this::respawn, "You died! Respawning...");
    }

    public void teleportToLevelStart() {
        teleport(level.getStartPos(), 0);
    }

    public void startLoop() {
        ReplayManager.INSTANCE.stopRecording();
        //teleportToLevelStart();
        ReplayManager.INSTANCE.replay(this);
    }

    private final float spriteHeightOffset = (sprite.getHeight() * Constants.UNIT_SCALE) * 0.25f;
    private final float spriteWidthOffset = (sprite.getWidth() * Constants.UNIT_SCALE) * 0.5f;
    private final float spriteHeightScaled = sprite.getHeight() * Constants.UNIT_SCALE;
    private final float spriteWidthScaled = sprite.getWidth() * Constants.UNIT_SCALE;

    private final Vector2 spriteScale = new Vector2(1, 1);

    @Override
    public void draw(Batch batch, float parentOpacity) {
        batch.end();
        batch.flush();
        batch.begin();

        currentAnimation = AnimationRegistry.PLAYER_IDLE;

        if (jumping) {
            currentAnimation = AnimationRegistry.PLAYER_JUMP;
            currentAnimationLooping = false;
            if (AnimationRegistry.PLAYER_JUMP.isAnimationFinished(stateTime)) {
                jumping = false;
            }
        }
        if (!isOnFloor && !jumping) {
            currentAnimation = AnimationRegistry.PLAYER_FALL;
            currentAnimationLooping = true;
        }
        if (currentAnimation == AnimationRegistry.PLAYER_IDLE && velocity.x != 0) {
            currentAnimation = AnimationRegistry.PLAYER_RUN;
        }

        if (!GMTK25.isPaused) stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, currentAnimationLooping);
        batch.draw(currentFrame,
            body.getPosition().x - spriteWidthOffset * spriteScale.x,
            body.getPosition().y - spriteHeightOffset * spriteScale.y,
            spriteWidthScaled * spriteScale.x,
            spriteHeightScaled * spriteScale.y
        );

        // if switch available, draw help
        if (nearestInteractable != null) {
            batch.flush();
            batch.draw(keyboardKeymap.getKeyFrame(0f, true),
                body.getPosition().x - keyboardKeymap.getKeyFrame(0f, true).getRegionWidth() * Constants.UNIT_SCALE * 0.35f,
                body.getPosition().y + keyboardKeymap.getKeyFrame(0f, true).getRegionHeight() * Constants.UNIT_SCALE * 0.75f,
                keyboardKeymap.getKeyFrame(0f, true).getRegionWidth() * Constants.UNIT_SCALE * 0.75f,
                keyboardKeymap.getKeyFrame(0f, true).getRegionHeight() * Constants.UNIT_SCALE * 0.75f
            );
        }
    }

    @Override
    public void act(float deltaTime) { // aka tick
        ReplayManager.INSTANCE.recordFrame(body.getPosition(), velocity, spriteScale, currentAnimation, currentAnimationLooping);
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
            coyoteTimeActive = true; // Reset coyote time when on the floor
        } else {
            if (coyoteTimeActive) {
                coyoteTimeCounter++;
                if (coyoteTimeCounter >= COYOTE_TIME_DURATION) {
                    coyoteTimeActive = false; // Deactivate coyote time after duration
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

        if (jumpBuffer > 0 && (isOnFloor || coyoteTimeActive)) { // jump from buffer
            velocity.y = JUMP_FORCE;
            jumpBuffer = 0; // Reset jump buffer after applying jump
            stateTime = 0;
            jumping = true;
            onJump.call(new Event() {
            });
        }

        if (Input.keyStack.contains(InputKeys.RIGHT) || Input.keyStack.contains(InputKeys.D)) {
            velocity.x += SPEED * deltaTime;
            spriteScale.x = 1; // Face right
        }
        if (Input.keyStack.contains(InputKeys.LEFT) || Input.keyStack.contains(InputKeys.A)) {
            velocity.x += -SPEED * deltaTime;
            spriteScale.x = -1; // Face left
        }
    }

    private void onInputEvent(InputEvent event) {
        if (event.key().equals(InputKeys.C) || event.key().equals(InputKeys.UP) || event.key().equals(InputKeys.W) || event.key().equals(InputKeys.SPACE)) {
            if (event.released()) {
                jumpBuffer = 0;
                if (velocity.y > MIN_JUMP_FORCE) {
                    body.setLinearVelocity(body.getLinearVelocity().x, MIN_JUMP_FORCE);
                }
                return;
            }
            jumpBuffer = JUMP_BUFFER_DURATION;
        }
        if (event.key().equals(InputKeys.E) && event.released()) {
            if (nearestInteractable != null) nearestInteractable.interact();
        }
    }

    private void onPlayerHitHazard(PlayerHitHazardEvent event) {
        if (event.player() != this) return;
        kill();
    }

    public void updateCameraPosition() {
        Camera camera = GMTK25.getCamera();
        Vector2 cameraMoveVector = body.getPosition().cpy().sub(camera.position.x, camera.position.y);
        // if camera near enough, don't move
        if (cameraMoveVector.len() < Constants.CAMERA_PLAYER_DISTANCE * Constants.UNIT_SCALE) {
            cameraMoveVector.setZero();
        }

        cameraMoveVector = cameraMoveVector.lerp(cameraMoveVector, 0.1f);

        // normalize the vector and scale it by camera speed
        // also multiply by delta time to make it frame rate independent
        if (cameraMoveVector.len() > Constants.CAMERA_SPEED * Gdx.graphics.getDeltaTime()) {
            cameraMoveVector.nor().scl(Constants.CAMERA_SPEED * Gdx.graphics.getDeltaTime());
        }
        // if the vector is too small, set it to zero
        if (cameraMoveVector.len() < 0.1f) {
            cameraMoveVector.setZero();
        }

        Vector2 finalCameraPosition = new Vector2(camera.position.cpy().x + cameraMoveVector.x, camera.position.cpy().y + cameraMoveVector.y);

        // snap camera to camera limits
        if (finalCameraPosition.x < level.getCameraLeft()) {
            finalCameraPosition.x = level.getCameraLeft();
        } else if (finalCameraPosition.x > level.getCameraRight()) {
            finalCameraPosition.x = level.getCameraRight();
        }
        if (finalCameraPosition.y < level.getCameraBottom()) {
            finalCameraPosition.y = level.getCameraBottom();
        } else if (finalCameraPosition.y > level.getCameraTop()) {
            finalCameraPosition.y = level.getCameraTop();
        }

        camera.position.set(finalCameraPosition, 0);
        camera.update();
    }
}
