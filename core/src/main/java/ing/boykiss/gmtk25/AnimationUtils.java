package ing.boykiss.gmtk25;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
}
