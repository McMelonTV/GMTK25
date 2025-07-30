package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    private Viewport backViewport;
    private Stage backStage;
    private Image background;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    @Override
    public void create() {
        backViewport = new ScreenViewport();
        backStage = new Stage();
        backStage.setViewport(backViewport);

        background = new Image(new Texture("textures/fill.png"));
        background.setColor(Color.DARK_GRAY);
        backStage.addActor(background);

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);
        stage = new Stage();
        stage.setViewport(viewport);

        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backStage.draw();
        stage.draw();
    }

    public void tick() {
        backStage.act();
        stage.act();
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        viewport.update(width, height);
        background.setSize(width, height);
        background.setPosition(-width / 2.0f, -height / 2.0f);
    }

    @Override
    public void dispose() {
        tickThread.interrupt();
    }
}
