package ing.boykiss.gmtk25.input;

import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.event.input.InputEvent;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class Input {
    public static final List<InputKeys> PAUSE_KEYS = List.of(InputKeys.ESCAPE);

    private static final Map<Class<? extends Event>, EventHandler<?>> eventHandlers = Map.of(
        InputEvent.class, new EventHandler<InputEvent>()
    );

    public static final InputKeyStack keyStack = new InputKeyStack();

    @SuppressWarnings({"unchecked"})
    public static <T extends Event> EventHandler<T> getEventHandler(Class<T> event) {
        return (EventHandler<T>) eventHandlers.get(event);
    }

    @Getter
    private static boolean isLocked = false;

    public static void lock() {
        isLocked = true;
    }

    public static void unlock() {
        isLocked = false;
    }
}
