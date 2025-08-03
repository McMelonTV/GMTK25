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

            int left = -(Gdx.graphics.getWidth() / 2);
            int bottom = -(Gdx.graphics.getHeight() / 2);
            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();

            // i have no clue why but i couldnt get this to work any other way so fuck it
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(AssetRegistry.FILL_TEXTURE, left, bottom, width, height);

            Texture pausedTextTexture = AssetRegistry.PAUSED_TEXT_TEXTURE;
            batch.setColor(1, 1, 1, 1f);
            batch.draw(pausedTextTexture, -pausedTextTexture.getWidth() * 3, -pausedTextTexture.getHeight() * 3, pausedTextTexture.getWidth() * 6, pausedTextTexture.getHeight() * 6);

            Texture controlsTexture = AssetRegistry.CONTROLS_TEXTURE;
            batch.setColor(1, 1, 1, 1f);
            batch.draw(controlsTexture, left, bottom, controlsTexture.getWidth() * 3, controlsTexture.getHeight() * 3);

            batch.setColor(color);
        }
    }
}
