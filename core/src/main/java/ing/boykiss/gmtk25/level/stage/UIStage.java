package ing.boykiss.gmtk25.level.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import ing.boykiss.gmtk25.actor.ui.PauseScreen;
import lombok.Getter;

public class UIStage extends Stage {
    @Getter
    private final PauseScreen pauseScreen;

    public UIStage() {
        pauseScreen = new PauseScreen();
        addActor(pauseScreen);
    }
}
