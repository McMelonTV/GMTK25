package ing.boykiss.gmtk25.actor.level.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import lombok.Getter;


public abstract class LevelObject extends Actor {
    @Getter
    private final String label;

    @Getter
    private final Vector2 position;
    @Getter
    private Body body;
    @Getter
    private Label labelWidget;

    public LevelObject(Vector2 position, String label) {
        position.y -= height();
        this.position = position;
        this.label = label;
    }

    abstract float height();

    abstract Body createBody(World world);

    public void initBody(World world) {
        if (this.body != null) return;

        this.body = createBody(world);
        body.setTransform(position, 0);

        if (label != null && !label.isEmpty()) {
            // create a new label actor
            Label.LabelStyle style = new Label.LabelStyle();
            style.font = AssetRegistry.FONT;
            style.fontColor = Color.WHITE;
            Label labelWidget = new Label(label, style);
            labelWidget.setFontScale(0.05f);
            labelWidget.setPosition(position.x, position.y + 1);
            labelWidget.setSize(1f, 1f);
            labelWidget.setAlignment(Align.center);

            this.labelWidget = labelWidget;
        }
    }

    public void resetState() {
    }
}
