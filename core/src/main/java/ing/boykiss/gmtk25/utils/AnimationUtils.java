package ing.boykiss.gmtk25.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ing.boykiss.gmtk25.input.Input;
import ing.boykiss.gmtk25.registry.AssetRegistry;

public class AnimationUtils {
    public static Animation<TextureRegion> createAnimationSheet(Texture texture, int sheetCols, int sheetRows, int[] frames, float frameLength) {
        int tileWidth = texture.getWidth() / sheetCols;
        int tileHeight = texture.getHeight() / sheetRows;
        TextureRegion[][] tmp = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] foundFrames = new TextureRegion[sheetCols * sheetRows];
        int index = 0;
        for (int i = 0; i < sheetRows; i++) {
            for (int j = 0; j < sheetCols; j++) {
                foundFrames[index++] = tmp[i][j];
            }
        }
        TextureRegion[] frameData = new TextureRegion[frames.length];
        for (int i = 0; i < frames.length; i++) {
            frameData[i] = foundFrames[frames[i] % foundFrames.length]; // Modulo here wraps the index so it never goes out of bounds
        }
        return new Animation<>(frameLength, frameData);
    }


    static float transitionStateTime = 0f;
    static boolean transitionStarted = false;

    static Runnable callback = null;
    static boolean callbackCalled = false;

    static String transitionTextt = "";

    public static void startTransitionAnimation(Runnable midTransitionCallback, String transitionText) {
        if (!transitionStarted) {
            Input.lock();
            callback = midTransitionCallback;
            callbackCalled = false;
            transitionStarted = true;
            transitionStateTime = 0f; // Reset state time for the new transition
            transitionTextt = transitionText != null ? transitionText : "Transitioning..."; // Default text if none provided
        }
    }

    public static void playTransitionAnimation(SpriteBatch batch) {
        if (!transitionStarted) {
            return; // No transition to play
        }
        float posX = (Gdx.graphics.getWidth()) - Gdx.graphics.getWidth() * ((float) Math.cos((transitionStateTime * Math.PI) - Math.PI) + 1);
        batch.begin();
        // Draw a black rectangle over the entire screen
        batch.setColor(0, 0, 0, 1f);
        batch.draw(
            AssetRegistry.FILL_TEXTURE,
            posX, 0,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        );

        GlyphLayout layout = new GlyphLayout();
        layout.setText(AssetRegistry.FONT_LARGE, transitionTextt);
        posX -= (layout.width / 2f);
        posX += Gdx.graphics.getWidth() / 2f; // Center the text horizontally
        AssetRegistry.FONT_LARGE.draw(batch, transitionTextt, posX, Gdx.graphics.getHeight() / 4f);

        batch.end();
        if (transitionStateTime > 0.5f && !callbackCalled) {
            if (callback != null) {
                callback.run();
                callbackCalled = true; // Ensure the callback is only called once
            }
        }
    }

    public static void tickAnimation(float delta) {
        transitionStateTime += delta / 2;

        if (transitionStateTime > 1) {
            transitionStateTime = 0;
            transitionStarted = false; // Reset the transition state
            Input.unlock();
        }
    }
}
