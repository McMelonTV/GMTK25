package ing.boykiss.gmtk25.event.input;

import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.input.Input;

public record InputEvent(Input.Keys key, boolean released) implements Event {
}
