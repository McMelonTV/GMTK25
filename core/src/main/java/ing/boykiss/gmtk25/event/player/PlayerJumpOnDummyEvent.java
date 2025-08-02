package ing.boykiss.gmtk25.event.player;

import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.player.PlayerDummy;
import ing.boykiss.gmtk25.event.Event;

public record PlayerJumpOnDummyEvent(Player player, PlayerDummy dummyPlayer) implements Event {
}
