package ing.boykiss.gmtk25.input;

import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.event.EventListener;
import ing.boykiss.gmtk25.event.input.InputEvent;
import ing.boykiss.gmtk25.registry.LevelRegistry;

public class InputEventListener implements EventListener<InputEvent> {
    @Override
    public void invoke(InputEvent event) {
        Player player = GMTK25.getPlayer();
        if (event.released() && event.key().equals(InputKeys.F11)) {
            GMTK25.toggleFullscreen();
        }
        if (event.released() && event.key().equals(InputKeys.ESCAPE)) {
            GMTK25.togglePaused();
        }
        if (event.released() && event.key().equals(InputKeys.R)) {
            if (GMTK25.isPaused()) return;
            GMTK25.renderStack.add(player::startLoop);
        }
        if (event.released() && event.key().equals(InputKeys.B)) {
            if (GMTK25.isPaused()) return;
            player.levelTransition(player.getLevel() == LevelRegistry.level0 ? LevelRegistry.level1 : LevelRegistry.level0);
        }
        if (event.released() && event.key().equals(InputKeys.K)) {
            if (GMTK25.isPaused()) return;
            player.kill();
        }
        if (event.released() && event.key().equals(InputKeys.M)) {
            if (GMTK25.isPaused()) return;
            player.levelTransition(LevelRegistry.menu);
        }
    }
}
