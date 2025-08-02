package ing.boykiss.gmtk25.input;

import com.badlogic.gdx.Gdx;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.event.input.InputEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class InputKeyStack extends HashSet<InputKeys> {
    private final EventHandler<InputEvent> inputEventHandler = Input.getEventHandler(InputEvent.class);

    public void update() {
        //handle escape key separately so that pause works
        Input.PAUSE_KEYS.forEach(this::handleKeyUpdate);

        if (GMTK25.isPaused || Input.isLocked()) {
            this.releaseAllExceptPause();
            return;
        }

        for (InputKeys key : InputKeys.values()) {
            if (Input.PAUSE_KEYS.contains(key)) continue;
            handleKeyUpdate(key);
        }
    }

    private void handleKeyUpdate(InputKeys key) {
        if (Gdx.input.isKeyPressed(key.getGdxKey())) {
            if (!this.contains(key)) {
                inputEventHandler.call(new InputEvent(key, false));
                this.add(key);
            }
        } else {
            if (this.contains(key)) {
                inputEventHandler.call(new InputEvent(key, true));
                this.remove(key);
            }
        }
    }

    private Set<InputKeys> filterExceptPause() {
        return Input.keyStack.stream().filter(k -> !Input.PAUSE_KEYS.contains(k)).collect(Collectors.toSet());
    }

    private boolean isEmptyExceptPause() {
        return filterExceptPause().isEmpty();
    }

    private void clearExceptPause() {
        filterExceptPause().forEach(Input.keyStack::remove);
    }

    private void releaseAllExceptPause() {
        if (this.isEmptyExceptPause()) return;

        this.filterExceptPause().forEach(key -> inputEventHandler.call(new InputEvent(key, true)));
        this.clearExceptPause();
    }
}
