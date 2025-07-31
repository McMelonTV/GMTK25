package ing.boykiss.gmtk25.level.replay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.actor.player.DummyPlayer;
import lombok.Getter;

import java.util.List;

public class ReplayManager {
    public static final ReplayManager INSTANCE = new ReplayManager();

    private ReplayManager() {
        // Private constructor to prevent instantiation
    }

    private boolean isRecording = false;

    // Return the recorded replay data
    @Getter
    private final List<ReplayFrame> replayData = new java.util.ArrayList<>();

    public void startRecording() {
        // Logic to start recording the replay
        isRecording = true;
        replayData.clear(); // Clear previous data if any
    }

    public void recordFrame(Vector2 playerPosition, Vector2 playerVelocity, Vector2 playerScale, Animation<TextureRegion> animation, boolean animationLooping) {
        // Logic to record the player's position for the current frame
        if (!isRecording) return;

        replayData.add(new ReplayFrame(new Vector2(playerPosition), new Vector2(playerVelocity), new Vector2(playerScale), animation, animationLooping));
    }

    public void stopRecording() {
        // Logic to stop recording the replay
        isRecording = false;
    }

    @Getter
    private boolean isReplaying = false;
    private int currentFrame = 0;
    private DummyPlayer player;

    /**
     * Prepares the replay for a player.
     *
     * @param player
     */
    public void replay(DummyPlayer player) {
        if (replayData.isEmpty()) {
            return;
        }
        currentFrame = 0;
        this.player = player;
        this.player.getBody().setTransform(replayData.get(currentFrame).playerPosition, 0);
        this.player.setVelocity(replayData.get(currentFrame).playerVelocity); // do animation based on this | nah i decided to just store animations :p -Whale
        this.player.setSpriteScale(replayData.get(currentFrame).playerScale);
        this.player.setAnimation(replayData.get(currentFrame).animation);
        this.player.setAnimationLooping(replayData.get(currentFrame).animationLooping);
        isReplaying = true;
    }

    /**
     * Advances to the next frame in the replay.
     *
     * @return true if there are more frames to replay, false if the end of the replay has been reached.
     */
    public boolean nextFrame() {
        if (this.player.isDestroyed()) {
            return false;
        }
        currentFrame++;
        this.player.getBody().setTransform(replayData.get(currentFrame).playerPosition, 0);
        this.player.setVelocity(replayData.get(currentFrame).playerVelocity); // do animation based on this | nah i decided to just store animations :p -Whale
        this.player.setSpriteScale(replayData.get(currentFrame).playerScale);
        if (!this.player.getAnimation().equals(replayData.get(currentFrame).animation)) {
            this.player.resetStateTime();
        }
        this.player.setAnimation(replayData.get(currentFrame).animation);
        this.player.setAnimationLooping(replayData.get(currentFrame).animationLooping);

        // Check if there are more frames to replay
        if (currentFrame >= replayData.size() - 1) {
            isReplaying = false; // Stop replaying if we reached the end
            this.player.destroy();
            return false;
        }
        return true;
    }
}
