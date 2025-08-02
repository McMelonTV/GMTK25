package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.player.PlayerDummy;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.player.PlayerHitHazardEvent;
import ing.boykiss.gmtk25.event.player.PlayerJumpOnDummyEvent;

import java.util.Arrays;
import java.util.List;

public class PlayerCollisionListener implements ContactListener {
    private final Player player;

    public PlayerCollisionListener(Player player) {
        this.player = player;
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            if (fixtureA.contains("player_sensor") || fixtureB.contains("player_sensor")) {
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
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            if (fixtureA.contains("player_sensor") || fixtureB.contains("player_sensor")) {
                player.collisionCount++;
                if (fixtureA.contains("dummy_player") || fixtureB.contains("dummy_player")) {
                    Fixture dummyFixture = fixtureA.contains("dummy_player") ? contact.getFixtureA() : contact.getFixtureB();
                    if (dummyFixture.getBody().getUserData() instanceof PlayerDummy dummyPlayer) {
                        player.getOnJump().addListener(event -> EventBus.call(PlayerJumpOnDummyEvent.class, new PlayerJumpOnDummyEvent(player, dummyPlayer)));
                    }
                }
            }
            if (fixtureA.contains("player_hurtbox") || fixtureB.contains("player_hurtbox")) {
                if (fixtureA.contains("hazard") || fixtureB.contains("hazard")) {
                    EventBus.call(PlayerHitHazardEvent.class, new PlayerHitHazardEvent(player));
                }
            }
        }
    }
};
