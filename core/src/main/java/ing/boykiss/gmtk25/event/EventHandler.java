package ing.boykiss.gmtk25.event;

import java.util.ArrayList;
import java.util.List;

public class EventHandler<T extends Event> {
    private final List<EventListener<T>> listeners;

    public EventHandler() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(EventListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener<T> listener) {
        listeners.remove(listener);
    }

    public void call(T event) {
        for (EventListener<T> listener : listeners) {
            listener.invoke(event);
        }
    }
}

