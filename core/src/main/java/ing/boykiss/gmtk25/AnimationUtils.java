package ing.boykiss.gmtk25;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationUtils {
    public static Animation<TextureRegion> createAnimationSheet(Texture texture, int frameCols, int frameRows, float frameDuration) {
        int tileWidth = texture.getWidth() / frameCols;
        int tileHeight = texture.getHeight() / frameRows;
        TextureRegion[][] tmp = TextureRegion.split(TextureRegistry.PLAYER_SHEET, tileWidth, tileHeight);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return new Animation<>(frameDuration, frames);
    }
}
