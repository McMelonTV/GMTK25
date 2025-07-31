package ing.boykiss.gmtk25.actor.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.player.PlayerJumpOnDummyEvent;
import ing.boykiss.gmtk25.registry.AnimationRegistry;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;
import lombok.Setter;

public class DummyPlayer {

    @Getter
    private final Body body;
    @Getter
    private boolean destroyed = false;

    @Getter
    @Setter
    private Vector2 velocity;
    @Getter
    @Setter
    private Vector2 spriteScale = new Vector2(1, 1);

    @Getter
    @Setter
    private Animation<TextureRegion> animation;

    @Getter
    @Setter
    private boolean animationLooping;

    private final Sprite sprite = new Sprite(AssetRegistry.PLAYER_TEXTURE);

    private float stateTime = 0f;


    public DummyPlayer(World world, Vector2 spawnPos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(spawnPos);

        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4.0f * Constants.UNIT_SCALE, 8.0f * Constants.UNIT_SCALE);

        // Body fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("dummy_player");
        body.setFixedRotation(true);

        velocity = new Vector2();

        shape.dispose();
    }


    private final float spriteHeightOffset = (sprite.getHeight() * Constants.UNIT_SCALE) * 0.25f;
    private final float spriteWidthOffset = (sprite.getWidth() * Constants.UNIT_SCALE) * 0.5f;
    private final float spriteHeightScaled = sprite.getHeight() * Constants.UNIT_SCALE;
    private final float spriteWidthScaled = sprite.getWidth() * Constants.UNIT_SCALE;

    public void resetStateTime() {
        stateTime = 0;
    }

    public void draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, animationLooping);
        batch.draw(currentFrame,
            body.getPosition().x - spriteWidthOffset * spriteScale.x,
            body.getPosition().y - spriteHeightOffset * spriteScale.y,
            spriteWidthScaled * spriteScale.x,
            spriteHeightScaled * spriteScale.y
        );
    }

    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        GMTK25.renderStack.add(() -> {
            GMTK25.getInstance().getRenderableDummies().remove(this);
            getBody().getWorld().destroyBody(getBody());
        });
    }
}
