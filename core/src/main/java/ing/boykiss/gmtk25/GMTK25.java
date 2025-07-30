package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GMTK25 extends ApplicationAdapter {
    private final Thread tickThread = new Thread(() -> {
        long prevTime = System.nanoTime();
        while (true) {
            long currTime = System.nanoTime();
            float time = (currTime - prevTime) / 1_000_000_000f;
            if (time < 1.0f / Constants.TPS) {
                continue;
            }
            prevTime = currTime;

            tick();
        }
    });

    @Override
    public void create() {
        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void tick() {

    }

    @Override
    public void dispose() {
        tickThread.interrupt();
    }
}
