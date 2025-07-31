package ing.boykiss.gmtk25.event.player;

import com.badlogic.gdx.physics.box2d.Fixture;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.event.Event;

public record PlayerHitHazardEvent(Fixture fixtureA, Fixture fixtureB, Player player) implements Event {
}
