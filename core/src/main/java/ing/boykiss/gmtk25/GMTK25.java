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
import ing.boykiss.gmtk25.actor.interactable.Door;
import ing.boykiss.gmtk25.actor.interactable.InteractableButton;
import ing.boykiss.gmtk25.actor.level.Level;
import ing.boykiss.gmtk25.actor.level.LevelBackground;
import ing.boykiss.gmtk25.actor.player.DummyPlayer;
import ing.boykiss.gmtk25.actor.player.DummyPlayerRenderer;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.ui.PauseScreen;
import ing.boykiss.gmtk25.audio.MusicPlayer;
import ing.boykiss.gmtk25.audio.Song;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.input.InputEvent;
import ing.boykiss.gmtk25.event.player.PlayerHitHazardEvent;
import ing.boykiss.gmtk25.event.player.PlayerJumpOnDummyEvent;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.level.WorldManager;
import ing.boykiss.gmtk25.level.listener.CollisionListener;
import ing.boykiss.gmtk25.level.listener.InteractableCollisionListener;
import ing.boykiss.gmtk25.level.listener.PlayerCollisionListener;
import ing.boykiss.gmtk25.level.replay.ReplayManager;
import ing.boykiss.gmtk25.registry.MapRegistry;
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

    private final Thread tickThread = new Thread(() -> {
        long prevTime = System.nanoTime();
        while (true) {
            long currTime = System.nanoTime();
            float time = (currTime - prevTime) / 1_000_000_000f;
            if (time < 1.0f / Constants.TPS) {
                continue;
            }
            prevTime = currTime;
            tick(time);
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
    private MusicPlayer musicPlayer;
    private Song song;

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
        Door door = new Door(WorldManager.world, new Vector2(12, 5));
        stage.addActor(door);
        stage.addActor(new InteractableButton(WorldManager.world, new Vector2(8, 3), door));
        stage.addActor(player);
        uiStage.addActor(new PauseScreen());

        WorldManager.world.setContactListener(CollisionListener.INSTANCE); // Set the contact listener for onFloor detection

        CollisionListener.INSTANCE.getListeners().add(new PlayerCollisionListener(player));
        CollisionListener.INSTANCE.getListeners().add(new InteractableCollisionListener());

        EventBus.addListener(PlayerHitHazardEvent.class, this::onPlayerDeath);

        tickThread.start();
    }

    private void resetLevel() {
        player.getBody().setTransform(30 * Constants.UNIT_SCALE, 50 * Constants.UNIT_SCALE, 0);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCameraPosition();

        musicPlayer.update(Gdx.graphics.getDeltaTime());

        backViewport.apply();
        backStage.draw();

        viewport.apply();
        stage.draw();

        WorldManager.debugRenderer.render(WorldManager.world, camera.combined);

        uiViewport.apply();
        uiStage.draw();

        synchronized (renderStack) {
            while (!renderStack.isEmpty()) {
                Runnable r = renderStack.poll();
                if (r != null) r.run();
            }
        }

        AnimationUtils.playTransitionAnimation(spriteBatch);

        Input.update();
    }

    public void tick(float deltaTime) {
        //tick animation
        AnimationUtils.tickAnimation(deltaTime);

        if (isPaused) return;
        WorldManager.world.step(deltaTime, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS);

        ReplayManager.INSTANCE.update();

        backStage.act(deltaTime);
        stage.act(deltaTime);
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

    private void onPlayerDeath(PlayerHitHazardEvent event) {
        System.out.println("Player hit hazard, resetting level...");
        AnimationUtils.startTransitionAnimation(this::resetLevel);
    }
}
