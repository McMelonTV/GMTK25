package ing.boykiss.gmtk25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;
import lombok.Setter;

public class DummyPlayer {

    @Getter
    private final Body body;

    @Getter
    @Setter
    private Vector2 velocity;

    private final Sprite sprite = new Sprite(AssetRegistry.PLAYER_TEXTURE);

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> runAnimation;
    private float stateTime = 0f;


    public DummyPlayer(World world, Vector2 spawnPos) {
        idleAnimation = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_IDLE_TEXTURE, 2, 2, new int[]{
            0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 3
        }, 0.1f);
        runAnimation = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_RUN_TEXTURE, 3, 3, new int[]{
            0, 1, 2, 3, 4, 5, 6, 7
        }, 0.03f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(spawnPos);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4.0f * Constants.UNIT_SCALE, 8.0f * Constants.UNIT_SCALE);

        // Body fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef).setSensor(true);
        body.setFixedRotation(true);

        velocity = new Vector2();

        shape.dispose();
    }


    private final float spriteHeightOffset = (sprite.getHeight() * Constants.UNIT_SCALE) * 0.25f;
    private final float spriteWidthOffset = (sprite.getWidth() * Constants.UNIT_SCALE) * 0.5f;
    private final float spriteHeightScaled = sprite.getHeight() * Constants.UNIT_SCALE;
    private final float spriteWidthScaled = sprite.getWidth() * Constants.UNIT_SCALE;

    private final Vector2 spriteScale = new Vector2(1, 1);

    public void draw(Batch batch) {
        Animation<TextureRegion> currentAnimation = velocity.x == 0 ? idleAnimation : runAnimation;

        if (currentAnimation == null) {
            return;
        }

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame,
            body.getPosition().x - spriteWidthOffset * spriteScale.x,
            body.getPosition().y - spriteHeightOffset * spriteScale.y,
            spriteWidthScaled * spriteScale.x,
            spriteHeightScaled * spriteScale.y
        );
    }
}
