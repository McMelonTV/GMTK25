package ing.boykiss.gmtk25.actor.level.object;

import ing.boykiss.gmtk25.GMTK25;

import java.util.function.Consumer;

public record InteractionTarget(Targetable targetable, Consumer<Interactable> callable) {
    public InteractionTarget {
        if (targetable == null && callable == null || targetable != null && callable != null)
            throw new IllegalArgumentException("A Target can only have either a Targetable or a Callable.");
    }

    public void call(Consumer<Targetable> targetableConsumer, Consumer<Consumer<Interactable>> callableConsumer) {
        // im not sure if we need to add it to the renderStack
        if (targetable != null) {
            GMTK25.renderStack.add(() -> targetableConsumer.accept(targetable));
        } else if (callable != null) {
            GMTK25.renderStack.add(() -> callableConsumer.accept(callable));
        }
    }
}
