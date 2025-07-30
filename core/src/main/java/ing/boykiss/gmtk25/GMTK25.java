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

    private TiledMap map;
    private TiledMapRenderer mapRenderer;

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

        map = new TmxMapLoader().load("tiledmaps/dev_map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        tickThread.start();

        Floor floor = new Floor(WorldManager.world);

        player = new Player(WorldManager.world, new Vector2(10, 50));
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backStage.draw();
        mapRenderer.setView(camera);
        mapRenderer.render();
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
        map.dispose();
    }
}
