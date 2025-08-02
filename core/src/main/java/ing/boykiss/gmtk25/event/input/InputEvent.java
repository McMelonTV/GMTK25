package ing.boykiss.gmtk25.event.input;

import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.input.InputKeys;

public record InputEvent(InputKeys key, boolean released) implements Event {
}
