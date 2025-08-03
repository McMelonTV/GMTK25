package ing.boykiss.gmtk25.level.replay;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.player.PlayerDummy;
import lombok.Getter;

import java.util.List;

public class ReplayManager {
    /**
     * Prepares the replay for a player.
     *
     * @param player
     */
    public void replay(Player player) {
        if (replayFrames.isEmpty()) {
            return;
        }

        ReplayData data = new ReplayData(new PlayerDummy(player), replayFrames);
        replayData.add(data);

        data.player.getBody().setTransform(data.frames.getFirst().playerPosition, 0);
        data.player.setVelocity(data.frames.getFirst().playerVelocity);
        data.player.setSpriteScale(data.frames.getFirst().playerScale);
        data.player.setAnimation(data.frames.getFirst().animation);
        data.player.setAnimationLooping(data.frames.getFirst().animationLooping);
    }

    public static final ReplayManager INSTANCE = new ReplayManager();

    private ReplayManager() {
        // Private constructor to prevent instantiation
    }

    private boolean isRecording = false;

    // Return the recorded replay data
    @Getter
    private final List<ReplayFrame> replayFrames = new java.util.ArrayList<>();
    private final List<ReplayData> replayData = new java.util.ArrayList<>();

    public void startRecording() {
        // Logic to start recording the replay
        isRecording = true;
        replayFrames.clear(); // Clear previous data if any
    }

    public void recordFrame(Vector2 playerPosition, Vector2 playerVelocity, Vector2 playerScale, Animation<TextureRegion> animation, boolean animationLooping) {
        // Logic to record the player's position for the current frame
        if (!isRecording) return;

        replayFrames.add(new ReplayFrame(new Vector2(playerPosition), new Vector2(playerVelocity), new Vector2(playerScale), animation, animationLooping));
    }

    public void stopRecording() {
        // Logic to stop recording the replay
        isRecording = false;
    }

    public static class ReplayData {
        public PlayerDummy player;
        public List<ReplayFrame> frames;
        public int currentFrame = 0;
        public boolean isReplaying = true;

        public ReplayData(PlayerDummy player, List<ReplayFrame> frames) {
            this.player = player;
            this.frames = frames;
        }
    }

    /**
     * Advances to the next frame in the replay.
     *
     * @return true if there are more frames to replay, false if the end of the replay has been reached.
     */
    public boolean nextFrame(ReplayData data) {
        if (!data.isReplaying) {
            return false; // If not currently replaying, do nothing
        }
        if (data.player.isDestroyed()) {
            return false;
        }
        if (data.frames.size() < data.currentFrame) {
            return false;
        }
        data.currentFrame++;
        data.player.getBody().setTransform(data.frames.get(data.currentFrame).playerPosition, 0);
        data.player.setVelocity(data.frames.get(data.currentFrame).playerVelocity); // do animation based on this | nah i decided to just store animations :p -Whale
        data.player.setSpriteScale(data.frames.get(data.currentFrame).playerScale);
        if (!data.player.getAnimation().equals(data.frames.get(data.currentFrame).animation)) {
            data.player.resetStateTime();
        }
        data.player.setAnimation(data.frames.get(data.currentFrame).animation);
        data.player.setAnimationLooping(data.frames.get(data.currentFrame).animationLooping);

        // Check if there are more frames to replay
        if (data.currentFrame >= replayFrames.size() - 1) {
            data.currentFrame--;
            //data.currentFrame = 0;
            return true;
        }
        return true;
    }

    public void update() {
        for (ReplayData data : replayData) {
            if (!nextFrame(data)) {
                data.isReplaying = false;
            }
        }
    }
}
