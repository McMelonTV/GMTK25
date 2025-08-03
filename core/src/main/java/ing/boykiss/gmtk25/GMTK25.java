package ing.boykiss.gmtk25;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.audio.MusicPlayer;
import ing.boykiss.gmtk25.event.input.InputEvent;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.input.InputEventListener;
import ing.boykiss.gmtk25.level.listener.CollisionListener;
import ing.boykiss.gmtk25.level.listener.InteractableCollisionListener;
import ing.boykiss.gmtk25.level.listener.PlayerCollisionListener;
import ing.boykiss.gmtk25.level.replay.ReplayManager;
import ing.boykiss.gmtk25.level.stage.BackStage;
import ing.boykiss.gmtk25.level.stage.UIStage;
import ing.boykiss.gmtk25.registry.LevelRegistry;
import ing.boykiss.gmtk25.registry.SongRegistry;
import ing.boykiss.gmtk25.utils.AnimationUtils;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GMTK25 extends ApplicationAdapter {
    @Getter
    private static GMTK25 instance;
    @Getter
    private static Box2DDebugRenderer debugRenderer;
    @Getter
    public static boolean isPaused = false;

    @Getter
    private static OrthographicCamera camera;
    @Getter
    private static Viewport levelViewport;
    @Getter
    private static Player player;
    private static boolean fullscreen = false;
    private static int windowedWidth = 1280;
    private static int windowedHeight = 720;

    @Getter
    private static MusicPlayer musicPlayer;
    @Getter
    private SpriteBatch spriteBatch;
    @Getter
    private Viewport screenViewport;
    @Getter
    private BackStage backStage;

    public static final Queue<Runnable> renderStack = new LinkedList<>();
    @Getter
    private UIStage uiStage;
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

    public static void toggleFullscreen() {
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

    public static void togglePaused() {
        isPaused = !isPaused;
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
    public void create() {
        instance = this;
        debugRenderer = new Box2DDebugRenderer();
        spriteBatch = new SpriteBatch();

        screenViewport = new ScreenViewport();
        backStage = new BackStage();
        backStage.setViewport(screenViewport);

        camera = new OrthographicCamera();
        camera.translate(Constants.VIEWPORT_WIDTH / 2.0f, Constants.VIEWPORT_HEIGHT / 2.0f);
        levelViewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);
        player = new Player(LevelRegistry.menu);

        uiStage = new UIStage();
        uiStage.setViewport(screenViewport);

        musicPlayer = new MusicPlayer(SongRegistry.MAIN_SONG);

        CollisionListener.INSTANCE.getListeners().add(new PlayerCollisionListener(player));
        CollisionListener.INSTANCE.getListeners().add(new InteractableCollisionListener());

        Input.getEventHandler(InputEvent.class).addListener(new InputEventListener());

        tickThread.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.updateCameraPosition();

        musicPlayer.update(Gdx.graphics.getDeltaTime());

        screenViewport.apply();
        backStage.draw();

        levelViewport.apply();
        player.getLevel().getStage().draw();
        debugRenderer.render(player.getLevel().getWorld(), camera.combined);

        screenViewport.apply();
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

    @Override
    public void resize(int width, int height) {
        screenViewport.update(width, height);
        levelViewport.update(width, height);

        backStage.getBackground().setSize(levelViewport.getScreenWidth(), levelViewport.getScreenHeight());
        backStage.getBackground().setPosition(-levelViewport.getScreenWidth() / 2.0f, -levelViewport.getScreenHeight() / 2.0f);
    }

    @Override
    public void dispose() {
        tickThread.interrupt();

        backStage.dispose();
        player.getLevel().dispose();
        uiStage.dispose();

        spriteBatch.dispose();

        Gdx.app.exit();
    }
}
