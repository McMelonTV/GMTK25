package ing.boykiss.gmtk25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PauseScreen extends Actor {
    @Override
    public void draw(Batch batch, float parentOpacity) {
        if (GMTK25.isPaused) {
            Color color = batch.getColor();
            batch.setColor(0, 0, 0, 0.5f);
            // i have no clue why but i couldnt get this to work any other way so fuck it
            batch.draw(new Texture("textures/fill.png"), -Gdx.graphics.getWidth(), -Gdx.graphics.getHeight(), Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            batch.setColor(color);
        }
    }
}
