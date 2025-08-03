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
import lombok.Getter;

public class Button extends Interactable {
    private final static Animation<TextureRegion> TEXTURES = AnimationUtils.createAnimationSheet(AssetRegistry.BUTTON_TEXTURE, 2, 1, new int[]{0, 1}, 0.2f);

    @Getter
    private int collisions = 0;

    public Button(Vector2 position, String label, InteractionTarget target) {
        super(position, label, target);
    }

    public void addCollision() {
        collisions = Math.clamp(collisions + 1, 0, Integer.MAX_VALUE);
        if (collisions == 1) interact();
    }

    public void removeCollision() {
        collisions = Math.clamp(collisions - 1, 0, Integer.MAX_VALUE);
        if (collisions == 0) interact();
    }

    @Override
    public void draw(Batch batch, float parentOpacity) {
        TextureRegion currentFrame = TEXTURES.getKeyFrame(collisions > 0 ? 1 : 0, true);
        batch.draw(currentFrame,
            getBody().getPosition().x - currentFrame.getRegionWidth() * Constants.UNIT_SCALE / 2,
            getBody().getPosition().y - 1 * Constants.UNIT_SCALE / 2,
            currentFrame.getRegionWidth() * Constants.UNIT_SCALE,
            currentFrame.getRegionHeight() * Constants.UNIT_SCALE);
    }

    @Override
    float height() {
        return 3.6f * Constants.UNIT_SCALE;
    }

    @Override
    Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5.0f * Constants.UNIT_SCALE, Constants.UNIT_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(4.0f * Constants.UNIT_SCALE, 2.0f * Constants.UNIT_SCALE, new Vector2(0, 3 * Constants.UNIT_SCALE), 0);

        FixtureDef sensorDef = new FixtureDef();
        sensorDef.shape = sensorShape;
        sensorDef.density = 0f;
        sensorDef.friction = 0f;
        sensorDef.restitution = 0f;
        sensorDef.isSensor = true;

        Fixture sensor = body.createFixture(sensorDef);
        sensor.setUserData("button");

        return body;
    }
}
