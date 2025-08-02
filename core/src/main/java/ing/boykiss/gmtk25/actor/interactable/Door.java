package ing.boykiss.gmtk25.actor.interactable;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;

public class Door extends Actor implements IInteractionTarget {
    @Getter
    private final Body body;

    @Getter
    private final Fixture doorFixture;

    @Getter
    private final Vector2 position;

    private boolean isOpen = false;

    private final Animation<TextureRegion> textures = AnimationUtils.createAnimationSheet(AssetRegistry.DOOR_TEXTURE, 2, 1, new int[]{0, 1}, 0.2f);

    public Door(World world, Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2.0f * Constants.UNIT_SCALE, 12.0f * Constants.UNIT_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.restitution = 0.0f;

        doorFixture = body.createFixture(fixtureDef);

        position.y -= 8.0f * Constants.UNIT_SCALE; // Adjust position to center the door vertically
        this.position = position;
        body.setTransform(position.x, position.y, 0);
    }

    @Override
    public void interact() {
        isOpen = !isOpen;
        doorFixture.setSensor(isOpen);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = textures.getKeyFrame(isOpen ? 1 : 0, true);
        batch.draw(currentFrame,
            body.getPosition().x - currentFrame.getRegionWidth() * Constants.UNIT_SCALE / 2,
            body.getPosition().y - currentFrame.getRegionHeight() * Constants.UNIT_SCALE / 2,
            currentFrame.getRegionWidth() * Constants.UNIT_SCALE,
            currentFrame.getRegionHeight() * Constants.UNIT_SCALE);
    }
}
