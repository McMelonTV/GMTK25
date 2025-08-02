package ing.boykiss.gmtk25.actor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.registry.AssetRegistry;

public class PauseScreen extends Actor {
    @Override
    public void draw(Batch batch, float parentOpacity) {
        if (GMTK25.isPaused) {
            Color color = batch.getColor();

            // i have no clue why but i couldnt get this to work any other way so fuck it
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(AssetRegistry.FILL_TEXTURE, -Gdx.graphics.getWidth(), -Gdx.graphics.getHeight(), Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);

            Texture pausedTextTexture = AssetRegistry.PAUSED_TEXT_TEXTURE;
            batch.setColor(1, 1, 1, 1);
            batch.draw(pausedTextTexture, -pausedTextTexture.getWidth() * 6, -pausedTextTexture.getHeight() * 6, pausedTextTexture.getWidth() * 6, pausedTextTexture.getHeight() * 6);

            batch.setColor(color);
        }
    }
}
