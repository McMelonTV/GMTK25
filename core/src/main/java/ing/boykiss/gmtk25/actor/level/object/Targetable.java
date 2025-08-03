package ing.boykiss.gmtk25.actor.level.object;

import com.badlogic.gdx.math.Vector2;

/**
 * A LevelObject that can be targeted by an Interactable (LevelObject)
 */
public abstract class Targetable extends LevelObject {
    public Targetable(Vector2 position, String label) {
        super(position, label);
    }

    public abstract void handleInteraction(Interactable interactable);
}
