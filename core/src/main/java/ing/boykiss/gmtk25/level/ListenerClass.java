package ing.boykiss.gmtk25.level;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.event.EventListener;
import ing.boykiss.gmtk25.event.player.PlayerHitHazardEvent;

public class ListenerClass implements ContactListener {
    Player player;
    public ListenerClass(Player player) {
        this.player = player;
    }

    public static final EventHandler<PlayerHitHazardEvent> playerHitHazardHandler = new EventHandler<>();

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            String fixtureA = contact.getFixtureA().getUserData() instanceof String ? (String) contact.getFixtureA().getUserData() : "unknown";
            String fixtureB = contact.getFixtureB().getUserData() instanceof String ? (String) contact.getFixtureB().getUserData() : "unknown";
            if (fixtureA.equals("player_sensor") || fixtureB.equals("player_sensor")) {
                player.collisionCount--;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            String fixtureA = contact.getFixtureA().getUserData() instanceof String ? (String) contact.getFixtureA().getUserData() : "unknown";
            String fixtureB = contact.getFixtureB().getUserData() instanceof String ? (String) contact.getFixtureB().getUserData() : "unknown";
            if (fixtureA.equals("player_sensor") || fixtureB.equals("player_sensor")) {
                player.collisionCount++;
            }
            if (fixtureA.equals("player_hurtbox") || fixtureB.equals("player_hurtbox")) {
                if (fixtureA.equals("hazard") || fixtureB.equals("hazard")) {
                    playerHitHazardHandler.call(new PlayerHitHazardEvent(contact.getFixtureA(), contact.getFixtureB(), player));
                }
            }
        }
    }
};
