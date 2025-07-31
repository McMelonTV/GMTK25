package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.input.event.InputEvent;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

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
    private Viewport uiViewport;
    private Stage uiStage;
    private Level level;

    SpriteBatch spriteBatch;

    @Getter
    private Player player;

    private boolean fullscreen = false;

    private int windowedWidth = 1280;
    private int windowedHeight = 720;

    public static final Queue<Runnable> renderStack = new LinkedList<>();

    private boolean isPaused = false;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        backViewport = new ScreenViewport();
        backStage = new Stage();
        backStage.setViewport(backViewport);

        Input.getEventHandler(InputEvent.class).addListener(event -> {
            if (event.released() && event.key().equals(Input.Keys.F11)) {
                synchronized (renderStack) {
                    renderStack.add(fullscreen ?
                        () -> {
                            Gdx.graphics.setUndecorated(false);
                            Gdx.graphics.setWindowedMode(windowedWidth, windowedHeight);
                            fullscreen = false;
                        } :
                        () -> {
                            windowedWidth = Gdx.graphics.getWidth();
                            windowedHeight = Gdx.graphics.getHeight();

                            Gdx.graphics.setUndecorated(true);
                            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();
                            Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
                            fullscreen = true;
                        }
                    );
                }
            }
            if (event.released() && (event.key().equals(Input.Keys.ESCAPE) || event.key().equals(Input.Keys.P))) {
                isPaused = !isPaused;
            }
        });

        background = new Image(new Texture("textures/fill.png")) {
            private final ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal("shaders/background.vsh"),
                Gdx.files.internal("shaders/background.fsh")
            );

            private float time;

            @Override
            public void draw(Batch batch, float parentAlpha) {
                time += Gdx.graphics.getDeltaTime();

                batch.end();
                batch.flush();

                ShaderProgram.pedantic = false;

                batch.begin();
                batch.setShader(shader);

                shader.setUniformf("u_time", time);
                shader.setUniformf("u_viewportRes", viewport.getWorldWidth() / Constants.UNIT_SCALE, viewport.getWorldHeight() / Constants.UNIT_SCALE);

                this.getDrawable().draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            }
        };
        background.setColor(Color.DARK_GRAY);
        backStage.addActor(background);

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);
        stage = new Stage();
        stage.setViewport(viewport);

        camera.translate(Constants.VIEWPORT_WIDTH / 2.0f, Constants.VIEWPORT_HEIGHT / 2.0f);

        uiViewport = new ScreenViewport();
        uiStage = new Stage();
        uiStage.setViewport(uiViewport);

        level = new Level(WorldManager.world, new TmxMapLoader().load("tiledmaps/dev_map.tmx"), camera);
        player = new Player(WorldManager.world, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE));

        stage.addActor(level);
        stage.addActor(player);

        WorldManager.world.setContactListener(new ListenerClass(player)); // Set the contact listener for onFloor detection

        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        backViewport.apply();
        backStage.draw();
        viewport.apply();
        stage.draw();

        //WorldManager.debugRenderer.render(WorldManager.world, camera.combined);

        uiViewport.apply();
        uiStage.draw();

        spriteBatch.setProjectionMatrix(uiViewport.getCamera().combined);

        spriteBatch.begin();
        if (isPaused) {
            spriteBatch.setColor(0, 0, 0, 0.5f);
            spriteBatch.draw(new Texture("textures/fill.png"), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            spriteBatch.setColor(255, 255, 255, 1);
        }
        spriteBatch.end();

        synchronized (renderStack) {
            while (!renderStack.isEmpty()) {
                Runnable r = renderStack.poll();
                if (r != null) r.run();
            }
        }

        Input.update();
    }

    public void tick() {
        if (isPaused) return;
        WorldManager.world.step(Gdx.graphics.getDeltaTime(), Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);

        backStage.act();
        stage.act();
        uiStage.act();
    }

    @Override
    public void resize(int width, int height) {
        backViewport.update(width, height);
        viewport.update(width, height);
        uiViewport.update(width, height);
        background.setSize(viewport.getScreenWidth(), viewport.getScreenHeight());
        background.setPosition(-viewport.getScreenWidth() / 2.0f, -viewport.getScreenHeight() / 2.0f);
    }

    @Override
    public void dispose() {
        tickThread.interrupt();

        backStage.dispose();
        stage.dispose();
        uiStage.dispose();
    }
}
