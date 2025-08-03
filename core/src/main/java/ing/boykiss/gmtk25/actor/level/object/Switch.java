package ing.boykiss.gmtk25.actor.level.object;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import ing.boykiss.gmtk25.utils.Nullean;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

public class Switch extends Interactable {
    private final static Animation<TextureRegion> TEXTURES = AnimationUtils.createAnimationSheet(AssetRegistry.SWITCH_TEXTURE, 2, 1, new int[]{0, 1}, 0.2f);

    @Getter
    @Setter
    private boolean isActive = false;

    private Nullean initialState = new Nullean(true, false);
    private Supplier<Boolean> stateGetter;

    public Switch(Vector2 position, String label, InteractionTarget target) {
        super(position, label, target);
    }

    public Switch(Vector2 position, String label, InteractionTarget target, boolean isActive) {
        this(position, label, target);
        this.initialState = new Nullean(false, isActive);
        this.isActive = isActive;
    }

    public Switch(Vector2 position, String label, InteractionTarget target, Supplier<Boolean> stateGetter) {
        this(position, label, target);
        this.stateGetter = stateGetter;
        this.isActive = stateGetter.get();
    }

    @Override
    public void draw(Batch batch, float parentOpacity) {
        TextureRegion currentFrame = TEXTURES.getKeyFrame(isActive ? 1 : 0, false);
        batch.draw(currentFrame,
            getBody().getPosition().x - currentFrame.getRegionWidth() * Constants.UNIT_SCALE / 2,
            getBody().getPosition().y - 4 * Constants.UNIT_SCALE,
            currentFrame.getRegionWidth() * Constants.UNIT_SCALE,
            currentFrame.getRegionHeight() * Constants.UNIT_SCALE);
    }

    @Override
    public void interact() {
        super.interact();
        setActive(!isActive);
    }

    @Override
    float height() {
        return 0f * Constants.UNIT_SCALE;
    }

    @Override
    Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6.0f * Constants.UNIT_SCALE, 4.0f * Constants.UNIT_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = true;

        Fixture sensor = body.createFixture(fixtureDef);
        sensor.setUserData("switch");

        return body;
    }

    @Override
    public void resetState() {
        if (!initialState.isNull()) {
            setActive(initialState.state());
        } else if (stateGetter != null) {
            setActive(stateGetter.get());
        } else {
            setActive(false);
        }
    }
}
