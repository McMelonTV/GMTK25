package ing.boykiss.gmtk25.actor.level.object;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import ing.boykiss.gmtk25.utils.Nullean;
import lombok.Getter;

import java.util.function.Supplier;

public class Door extends Targetable {
    private final static Animation<TextureRegion> TEXTURES = AnimationUtils.createAnimationSheet(AssetRegistry.DOOR_TEXTURE, 2, 1, new int[]{0, 1}, 0.2f);

    @Getter
    private boolean isOpen = false;

    private Nullean initialState = new Nullean(true, false);
    private Supplier<Boolean> stateGetter;

    @Getter
    private Fixture doorFixture;

    public Door(Vector2 position, String label) {
        super(position, label);
    }

    public Door(Vector2 position, String label, boolean isOpen) {
        this(position, label);
        this.initialState = new Nullean(false, isOpen);
        this.isOpen = isOpen;
    }

    public Door(Vector2 position, String label, Supplier<Boolean> stateGetter) {
        this(position, label);
        this.stateGetter = stateGetter;
        this.isOpen = stateGetter.get();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = TEXTURES.getKeyFrame(isOpen ? 1 : 0, false);
        batch.draw(currentFrame,
            getBody().getPosition().x - currentFrame.getRegionWidth() * Constants.UNIT_SCALE / 2,
            getBody().getPosition().y - currentFrame.getRegionHeight() * Constants.UNIT_SCALE / 2,
            currentFrame.getRegionWidth() * Constants.UNIT_SCALE,
            currentFrame.getRegionHeight() * Constants.UNIT_SCALE);
    }

    @Override
    public void handleInteraction(Interactable interactable) {
        setIsOpen(!isOpen);
    }

    @Override
    float height() {
        return 8f * Constants.UNIT_SCALE;
    }

    @Override
    Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2.0f * Constants.UNIT_SCALE, 12.0f * Constants.UNIT_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.restitution = 0.0f;

        doorFixture = body.createFixture(fixtureDef);

        return body;
    }

    public void setIsOpen(boolean state) {
        isOpen = state;
        if (doorFixture != null) {
            doorFixture.setSensor(state);
        }
    }

    @Override
    public void resetState() {
        if (!initialState.isNull()) {
            setIsOpen(initialState.state());
        } else if (stateGetter != null) {
            setIsOpen(stateGetter.get());
        } else {
            setIsOpen(false);
        }
    }
}
