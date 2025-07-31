package ing.boykiss.gmtk25.event;

import java.util.HashMap;
import java.util.Map;

/*
 This is the hub for all global events that we want to access from anywhere
 */
public class EventBus {
    private static final Map<Class<? extends Event>, EventHandler<? extends Event>> eventHandlers = new HashMap<>();

    // This generates a new EventHandler if one does not already exist
    // This SHOULD be the only way an EventHandler gets added to the Map
    // Even though this gives that unchecked warning that we're suppressing, we know that it's going be the same type as clazz
    @SuppressWarnings({"unchecked"})
    public static <T extends Event> EventHandler<T> getHandler(Class<T> clazz) {
        eventHandlers.computeIfAbsent(clazz, k -> new EventHandler<T>());
        return (EventHandler<T>) eventHandlers.get(clazz);
    }

    public static <T extends Event> void addListener(Class<T> clazz, EventListener<T> listener) {
        getHandler(clazz).addListener(listener);
    }

    public static <T extends Event> void call(Class<T> clazz, T event) {
        // Check if EventHandler exists here so that we don't make a new one for an Event that no one is listening to
        if (!eventHandlers.containsKey(clazz)) {
            return;
        }
        getHandler(clazz).call(event);
    }
}
