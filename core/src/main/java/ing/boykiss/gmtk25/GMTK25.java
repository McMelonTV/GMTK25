package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GMTK25 extends ApplicationAdapter {
    private final Thread tickThread = new Thread(() -> {
        long eSleepTime = (long) (1_000f / Constants.TPS);
        // declare variables in advance because perf
        long startTime;
        long elapsedTime;
        long sleepTime;
        while (true) {
            try {
                startTime = System.currentTimeMillis();

                tick();

                elapsedTime = System.currentTimeMillis() - startTime;

                sleepTime = eSleepTime - elapsedTime;
                if (sleepTime >= 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                // If the thread is interrupted, we stop ticking
                return;
            }
        }
    });

    private Viewport backViewport;
    private Stage backStage;
    private Image background;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private Level level;

    @Getter
    private Player player;

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

        camera.translate(Constants.VIEWPORT_WIDTH / 2.0f, Constants.VIEWPORT_HEIGHT / 2.0f);

        level = new Level(WorldManager.world, new TmxMapLoader().load("tiledmaps/dev_map.tmx"), camera);
        player = new Player(WorldManager.world, new Vector2(30, 50));

        stage.addActor(level);
        stage.addActor(player);

        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backStage.draw();
        stage.draw();

        WorldManager.debugRenderer.render(WorldManager.world, camera.combined);
    }

    public void tick() {
        WorldManager.world.step(Gdx.graphics.getDeltaTime(), Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);

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

        backStage.dispose();
        stage.dispose();
    }
}
