package ing.boykiss.gmtk25.level.replay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ReplayFrame {
    public Vector2 playerPosition;
    public Vector2 playerVelocity;
    public Vector2 playerScale;
    public Animation<TextureRegion> animation;
    public boolean animationLooping;

    public ReplayFrame(Vector2 playerPosition, Vector2 playerVelocity, Vector2 playerScale, Animation<TextureRegion> animation, boolean animationLooping) {
        this.playerPosition = playerPosition;
        this.playerVelocity = playerVelocity;
        this.playerScale = playerScale;
        this.animation = animation;
        this.animationLooping = animationLooping;
    }
}
