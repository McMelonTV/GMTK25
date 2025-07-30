package ing.boykiss.gmtk25.event;

public interface EventListener<T extends Event>{
    void invoke(T event);
}
