package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ing.boykiss.gmtk25.utils.AnimationUtils;

public class AnimationRegistry {
    public static final Animation<TextureRegion> PLAYER_IDLE = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_IDLE_TEXTURE, 2, 2, new int[]{
        0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 3,
    }, 0.1f);;
    public static final Animation<TextureRegion> PLAYER_RUN = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_RUN_TEXTURE, 3, 3, new int[]{
        0, 1, 2, 3, 4, 5, 6, 7,
    }, 0.05f);
    public static final Animation<TextureRegion> PLAYER_JUMP = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_JUMP_TEXTURE, 2, 2, new int[]{
        0, 1, 2,
    }, 0.07f);
    public static final Animation<TextureRegion> PLAYER_FALL = AnimationUtils.createAnimationSheet(AssetRegistry.PLAYER_FALL_TEXTURE, 1, 1, new int[]{
        0,
    }, 0.1f);
}
