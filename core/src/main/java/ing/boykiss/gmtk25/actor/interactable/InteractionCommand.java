package ing.boykiss.gmtk25.actor.interactable;

import java.util.Queue;

public class InteractionCommand implements IInteractionTarget {
    private final Runnable runnable;
    private final Queue<Runnable> renderStack;

    public InteractionCommand(Runnable runnable, Queue<Runnable> renderStack) {
        this.runnable = runnable;
        this.renderStack = renderStack;
    }

    @Override
    public void interact() {
        this.renderStack.add(this.runnable);
    }
}
