package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ing.boykiss.gmtk25.actor.interactable.InteractableButton;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.LevelBackground;
import ing.boykiss.gmtk25.actor.player.DummyPlayer;
import ing.boykiss.gmtk25.actor.player.DummyPlayerRenderer;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.ui.PauseScreen;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.input.InputEvent;
import ing.boykiss.gmtk25.event.player.PlayerJumpOnDummyEvent;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.level.WorldManager;
import ing.boykiss.gmtk25.level.listener.CollisionListener;
import ing.boykiss.gmtk25.level.listener.InteractableCollisionListener;
import ing.boykiss.gmtk25.level.listener.PlayerCollisionListener;
import ing.boykiss.gmtk25.level.replay.ReplayManager;
import ing.boykiss.gmtk25.registry.MapRegistry;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GMTK25 extends ApplicationAdapter {
    @Getter
    private static GMTK25 instance;

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

    public DummyPlayerRenderer dummyPlayerRenderer;

    private boolean fullscreen = false;

    private int windowedWidth = 1280;
    private int windowedHeight = 720;

    public static final Queue<Runnable> renderStack = new LinkedList<>();

    public static boolean isPaused = false;

    @Override
    public void create() {
        instance = this;

        spriteBatch = new SpriteBatch();

        backViewport = new ScreenViewport();
        backStage = new Stage();
        backStage.setViewport(backViewport);

        background = new LevelBackground(backViewport);
        backStage.addActor(background);

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
            if (event.released() && event.key().equals(Input.Keys.R)) {
                renderStack.add(() -> {
                    ReplayManager.INSTANCE.stopRecording();
                    DummyPlayer dummyPlayer = new DummyPlayer(WorldManager.world, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE));
                    dummyPlayerRenderer.addRenderableDummy(dummyPlayer);
                    ReplayManager.INSTANCE.replay(dummyPlayer);

                    EventBus.addListener(PlayerJumpOnDummyEvent.class, e -> {
                        if (e.dummyPlayer() != dummyPlayer) {
                            return;
                        }
                        renderStack.add(() -> {
                            dummyPlayer.destroy();
                            dummyPlayerRenderer.removeRenderableDummy(dummyPlayer);
                        });
                    });
                });
            }
        });

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);

        stage = new Stage();
        stage.setViewport(viewport);

        camera.translate(Constants.VIEWPORT_WIDTH / 2.0f, Constants.VIEWPORT_HEIGHT / 2.0f);

        uiViewport = new ScreenViewport();
        uiStage = new Stage();
        uiStage.setViewport(uiViewport);

        level = new Level(WorldManager.world, MapRegistry.DEV_MAP, camera);
        player = new Player(WorldManager.world, new Vector2(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE));
        dummyPlayerRenderer = new DummyPlayerRenderer();

        stage.addActor(level);
        stage.addActor(dummyPlayerRenderer);
        stage.addActor(new InteractableButton(WorldManager.world, new Vector2(8, 3)));
        stage.addActor(player);
        uiStage.addActor(new PauseScreen());

        WorldManager.world.setContactListener(CollisionListener.INSTANCE); // Set the contact listener for onFloor detection

        CollisionListener.INSTANCE.getListeners().add(new PlayerCollisionListener(player));
        CollisionListener.INSTANCE.getListeners().add(new InteractableCollisionListener());

        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCameraPosition();

        backViewport.apply();
        backStage.draw();

        viewport.apply();
        stage.draw();

        uiViewport.apply();
        uiStage.draw();

        WorldManager.debugRenderer.render(WorldManager.world, camera.combined);

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

        ReplayManager.INSTANCE.update();

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

    private void updateCameraPosition() {
        Vector2 cameraMoveVector = player.getBody().getPosition().cpy().sub(camera.position.x, camera.position.y);
        // if camera near enough, don't move
        if (cameraMoveVector.len() < Constants.CAMERA_PLAYER_DISTANCE * Constants.UNIT_SCALE) {
            cameraMoveVector.setZero();
        }

        cameraMoveVector = cameraMoveVector.lerp(cameraMoveVector, 0.1f);

        // normalize the vector and scale it by camera speed
        // also multiply by delta time to make it frame rate independent
        if (cameraMoveVector.len() > Constants.CAMERA_SPEED * Gdx.graphics.getDeltaTime()) {
            cameraMoveVector.nor().scl(Constants.CAMERA_SPEED * Gdx.graphics.getDeltaTime());
        }
        // if the vector is too small, set it to zero
        if (cameraMoveVector.len() < 0.1f) {
            cameraMoveVector.setZero();
        }

        Vector2 finalCameraPosition = new Vector2(camera.position.cpy().x + cameraMoveVector.x, camera.position.cpy().y + cameraMoveVector.y);

        // snap camera to camera limits
        if (finalCameraPosition.x < level.getCameraLeft()) {
            finalCameraPosition.x = level.getCameraLeft();
        } else if (finalCameraPosition.x > level.getCameraRight()) {
            finalCameraPosition.x = level.getCameraRight();
        }
        if (finalCameraPosition.y < level.getCameraBottom()) {
            finalCameraPosition.y = level.getCameraBottom();
        } else if (finalCameraPosition.y > level.getCameraTop()) {
            finalCameraPosition.y = level.getCameraTop();
        }

        camera.position.set(finalCameraPosition, 0);
        camera.update();

    }
}
