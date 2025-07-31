package ing.boykiss.gmtk25.actor.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class DummyPlayerRenderer extends Actor {
    private final List<DummyPlayer> renderableDummies = new ArrayList<>();

    @Override
    public void draw(Batch batch, float parentOpacity) {
        batch.end();
        batch.flush();
        batch.begin();
        for (DummyPlayer dummy : renderableDummies) {
            dummy.draw(batch);
        }
    }

    public void addRenderableDummy(DummyPlayer dummy) {
        renderableDummies.add(dummy);
    }

    public void removeRenderableDummy(DummyPlayer dummy) {
        renderableDummies.remove(dummy);
    }
}
