package ing.boykiss.gmtk25.actor.interactable;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;

public class InteractableButton extends Interactable {
    @Getter
    private final Body body;
    @Getter
    private final Fixture sensor;

    @Getter
    private final Vector2 position;

    @Getter
    private final EventHandler<Event> onEnter = new EventHandler<>();
    @Getter
    private final EventHandler<Event> onExit = new EventHandler<>();

    private final Animation<TextureRegion> textures = AnimationUtils.createAnimationSheet(AssetRegistry.BUTTON_TEXTURE, 2, 1, new int[]{0, 1}, 0.2f);

    private int collisions = 0;

    private final IInteractionTarget interactionTarget;

    public InteractableButton(World world, Vector2 position, IInteractionTarget interactionTarget) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = world.createBody(bodyDef);
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

        this.interactionTarget = interactionTarget;

        sensor = body.createFixture(sensorDef);
        sensor.setUserData("button");

        onEnter.addListener(event -> {
            if (collisions == 0) {
                // Only interact if this is the first collision
                System.out.println("Button pressed");
                if (interactionTarget != null) {
                    interactionTarget.interact();
                }
            }
            collisions++;
        });
        onExit.addListener(event -> {
            if (collisions == 1) {
                // Only interact if this is the last collision
                System.out.println("Button released");
                if (interactionTarget != null) {
                    interactionTarget.interact();
                }
            }
            System.out.println("unpressed");

            collisions--;
        });

        position.y -= 0.45f;// * Constants.UNIT_SCALE; // Adjust position to center the button

        this.position = position;
        body.setTransform(position.x, position.y, 0);
    }

    @Override
    public void act(float deltaTime) {
    }

    @Override
    public void draw(Batch batch, float parentOpacity) {
        TextureRegion currentFrame = textures.getKeyFrame(collisions > 0 ? 1 : 0, true);
        batch.draw(currentFrame,
            body.getPosition().x - currentFrame.getRegionWidth() * Constants.UNIT_SCALE / 2,
            body.getPosition().y - 1 * Constants.UNIT_SCALE / 2,
            currentFrame.getRegionWidth() * Constants.UNIT_SCALE,
            currentFrame.getRegionHeight() * Constants.UNIT_SCALE);
    }

    public boolean isPressed() {
        return collisions <= 0;
    }
}
