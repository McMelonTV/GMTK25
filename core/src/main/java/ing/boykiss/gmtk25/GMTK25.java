package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ing.boykiss.gmtk25.actor.level.LevelBackground;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.ui.PauseScreen;
import ing.boykiss.gmtk25.audio.MusicPlayer;
import ing.boykiss.gmtk25.audio.Song;
import ing.boykiss.gmtk25.event.input.InputEvent;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.level.listener.CollisionListener;
import ing.boykiss.gmtk25.level.listener.InteractableCollisionListener;
import ing.boykiss.gmtk25.level.listener.PlayerCollisionListener;
import ing.boykiss.gmtk25.level.replay.ReplayManager;
import ing.boykiss.gmtk25.registry.LevelRegistry;
import ing.boykiss.gmtk25.registry.SoundRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GMTK25 extends ApplicationAdapter {
    @Getter
    private static GMTK25 instance;

    @Getter
    private static Player player;

    @Getter
    private static Box2DDebugRenderer debugRenderer;

    @Getter
    private static OrthographicCamera camera;
    @Getter
    private static Viewport viewport;

    private final Thread tickThread = new Thread(() -> {
        final float target = 1.0f / Constants.TPS;
        long prevTime = System.nanoTime();
        while (!Thread.currentThread().isInterrupted()) {
            long currTime = System.nanoTime();
            float delta = (currTime - prevTime) / 1_000_000_000f;
            if (delta < target) {
                long sleepMillis = (long) ((target - delta) * 1_000);
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }
            prevTime = currTime;
            tick(delta);
        }
    });

    private Viewport backViewport;
    private Stage backStage;
    private Image background;
    private Viewport uiViewport;
    private Stage uiStage;
    private MusicPlayer musicPlayer;
    private Song song;

    SpriteBatch spriteBatch;

    private boolean fullscreen = false;

    private int windowedWidth = 1280;
    private int windowedHeight = 720;

    public static final Queue<Runnable> renderStack = new LinkedList<>();

    public static boolean isPaused = false;

    @Override
    public void create() {
        instance = this;
        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);

        player = new Player(LevelRegistry.menu);

        musicPlayer = new MusicPlayer();
        song = new Song(
            List.of(
                SoundRegistry.MAIN_SONG_PART_C,
                SoundRegistry.MAIN_SONG_PART_A,
                SoundRegistry.MAIN_SONG_PART_B
            ),
            Map.of(
                SoundRegistry.MAIN_SONG_PART_A, 2,
                SoundRegistry.MAIN_SONG_PART_D, 2,
                SoundRegistry.MAIN_SONG_PART_B, 2,
                SoundRegistry.MAIN_SONG_PART_C, 1
            )
        );
        musicPlayer.playSong(song);

        spriteBatch = new SpriteBatch();

        backViewport = new ScreenViewport();
        backStage = new Stage();
        backStage.setViewport(backViewport);

        background = new LevelBackground(backViewport);
        backStage.addActor(background);

        Input.getEventHandler(InputEvent.class).addListener(event -> {
            if (event.released() && event.key().equals(Keys.F11)) {
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
            if (event.released() && (Input.PAUSE_KEYS.contains(event.key()))) {
                isPaused = !isPaused;
            }
            if (event.released() && event.key().equals(Keys.R)) {
                if (isPaused) return;
                renderStack.add(player::startLoop);
            }
            if (event.released() && event.key().equals(Keys.B)) {
                if (isPaused) return;
                player.levelTransition(player.getLevel() == LevelRegistry.level0 ? LevelRegistry.level1 : LevelRegistry.level0);
            }
            if (event.released() && event.key().equals(Keys.K)) {
                if (isPaused) return;
                player.kill();
            }
            if (event.released() && event.key().equals(Keys.M)) {
                if (isPaused) return;
                player.levelTransition(LevelRegistry.menu);
            }
        });

        camera.translate(Constants.VIEWPORT_WIDTH / 2.0f, Constants.VIEWPORT_HEIGHT / 2.0f);

        uiViewport = new ScreenViewport();
        uiStage = new Stage();
        uiStage.setViewport(uiViewport);
        uiStage.addActor(new PauseScreen());

        CollisionListener.INSTANCE.getListeners().add(new PlayerCollisionListener(player));
        CollisionListener.INSTANCE.getListeners().add(new InteractableCollisionListener());

        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCameraPosition();

        musicPlayer.update(Gdx.graphics.getDeltaTime());

        backViewport.apply();
        backStage.draw();

        viewport.apply();
        player.getLevel().getStage().draw();

        debugRenderer.render(player.getLevel().getWorld(), camera.combined);

        uiViewport.apply();
        uiStage.draw();

        synchronized (renderStack) {
            while (!renderStack.isEmpty()) {
                Runnable r = renderStack.poll();
                if (r != null) r.run();
            }
        }

        Input.keyStack.update();

        AnimationUtils.playTransitionAnimation(spriteBatch);
    }

    public void tick(float deltaTime) {
        if (isPaused) return;

        AnimationUtils.tickAnimation(deltaTime);

        player.getLevel().getWorld().step(deltaTime, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);

        ReplayManager.INSTANCE.update();

        backStage.act(deltaTime);
        player.getLevel().getStage().act(deltaTime);
        uiStage.act(deltaTime);
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
        player.getLevel().dispose();
        uiStage.dispose();

        Gdx.app.exit();
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
        if (finalCameraPosition.x < player.getLevel().getCameraLeft()) {
            finalCameraPosition.x = player.getLevel().getCameraLeft();
        } else if (finalCameraPosition.x > player.getLevel().getCameraRight()) {
            finalCameraPosition.x = player.getLevel().getCameraRight();
        }
        if (finalCameraPosition.y < player.getLevel().getCameraBottom()) {
            finalCameraPosition.y = player.getLevel().getCameraBottom();
        } else if (finalCameraPosition.y > player.getLevel().getCameraTop()) {
            finalCameraPosition.y = player.getLevel().getCameraTop();
        }

        camera.position.set(finalCameraPosition, 0);
        camera.update();
    }

    public void transitionToLevel(String levelName) {
        player.levelTransition(LevelRegistry.LEVELS.get(levelName));
    }
}
