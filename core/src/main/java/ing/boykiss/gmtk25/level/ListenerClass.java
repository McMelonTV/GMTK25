package ing.boykiss.gmtk25.level;

import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.actor.player.DummyPlayer;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.event.EventListener;
import ing.boykiss.gmtk25.event.player.PlayerHitHazardEvent;
import ing.boykiss.gmtk25.event.player.PlayerJumpOnDummyEvent;

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
                if (fixtureA.equals("dummy_player") || fixtureB.equals("dummy_player")) {
                    Fixture dummyFixture = fixtureA.equals("dummy_player") ? contact.getFixtureA() : contact.getFixtureB();
                    if (dummyFixture.getBody().getUserData() instanceof DummyPlayer dummyPlayer) {
                        player.getOnJump().addListener(event -> EventBus.call(PlayerJumpOnDummyEvent.class, new PlayerJumpOnDummyEvent(player, dummyPlayer)));
                    }
                }
            }
            if (fixtureA.equals("player_hurtbox") || fixtureB.equals("player_hurtbox")) {
                if (fixtureA.equals("hazard") || fixtureB.equals("hazard")) {
                    EventBus.call(PlayerHitHazardEvent.class, new PlayerHitHazardEvent(player));
                }
            }
        }
    }
};
