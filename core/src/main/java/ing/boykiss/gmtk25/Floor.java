package ing.boykiss.gmtk25;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import lombok.Getter;

public class Floor {
    @Getter
    private final Body body;

    public Floor(World world) {
        BodyDef floorBodyDef = new BodyDef();
        floorBodyDef.type = BodyDef.BodyType.StaticBody;
        floorBodyDef.position.set(new Vector2(0, 0));

        body = WorldManager.world.createBody(floorBodyDef);

        EdgeShape floorShape = new EdgeShape();
        floorShape.set(new Vector2(0, 0), new Vector2(Constants.VIEWPORT_WIDTH, 0));

        FixtureDef floorFixtureDef = new FixtureDef();
        floorFixtureDef.shape = floorShape;
        body.createFixture(floorFixtureDef);

        floorShape.dispose();
    }
}
