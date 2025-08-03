package ing.boykiss.gmtk25.actor.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.registry.ShaderRegistry;

import java.util.ArrayList;
import java.util.List;

public class PlayerDummyRenderer extends Actor {
    private final List<PlayerDummy> renderableDummies = new ArrayList<>();

    @Override
    public void draw(Batch batch, float parentOpacity) {
        batch.end();
        batch.flush();
        batch.begin();
        batch.setShader(ShaderRegistry.PLAYER_DUMMY);
        for (PlayerDummy dummy : renderableDummies) {
            dummy.draw(batch);
        }
        batch.setShader(null);
    }

    public void addRenderableDummy(PlayerDummy dummy) {
        renderableDummies.add(dummy);
    }

    public void removeRenderableDummy(PlayerDummy dummy) {
        renderableDummies.remove(dummy);
    }
}
