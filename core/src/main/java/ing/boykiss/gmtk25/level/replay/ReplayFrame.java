package ing.boykiss.gmtk25.level.replay;

import com.badlogic.gdx.math.Vector2;

public class ReplayFrame {
    public Vector2 playerPosition;
    public Vector2 playerVelocity;

    public ReplayFrame(Vector2 playerPosition, Vector2 playerVelocity) {
        this.playerPosition = playerPosition;
        this.playerVelocity = playerVelocity;
    }
}
