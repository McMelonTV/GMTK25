package ing.boykiss.gmtk25.level.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import ing.boykiss.gmtk25.actor.level.LevelBackground;
import lombok.Getter;

public class BackStage extends Stage {
    @Getter
    private final LevelBackground background;

    public BackStage() {
        background = new LevelBackground();
        addActor(background);
    }
}
