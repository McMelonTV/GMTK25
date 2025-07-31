package ing.boykiss.gmtk25;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import ing.boykiss.gmtk25.actor.Player;

public class ListenerClass implements ContactListener {
    Player player;
    public ListenerClass(Player player) {
        this.player = player;
    }

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
        }
    }
};
