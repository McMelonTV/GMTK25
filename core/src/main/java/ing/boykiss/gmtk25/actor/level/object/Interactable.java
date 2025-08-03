package ing.boykiss.gmtk25.actor.level.object;


import com.badlogic.gdx.math.Vector2;

import java.util.function.Consumer;

/**
 * A LevelObject that the Player can interact with, either through the interaction keybind or in-world
 */
public abstract class Interactable extends LevelObject {
    private final InteractionTarget target;

    public Interactable(Vector2 position, String label, InteractionTarget target) {
        super(position, label);
        this.target = target;
    }

    public void interact() {
        target.call(this::handleTargetableInteraction, this::handleCallableInteraction);
    }

    private void handleTargetableInteraction(Targetable t) {
        t.handleInteraction(this);
    }

    private void handleCallableInteraction(Consumer<Interactable> c) {
        c.accept(this);
    }
}
