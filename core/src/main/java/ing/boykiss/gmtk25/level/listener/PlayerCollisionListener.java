package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.actor.player.Player;
import ing.boykiss.gmtk25.actor.player.PlayerDummy;
import ing.boykiss.gmtk25.event.EventBus;
import ing.boykiss.gmtk25.event.player.PlayerHitHazardEvent;

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
                if (fixtureA.contains("dummy_player") || fixtureB.contains("dummy_player")) {
                    Body playerBody = fixtureA.contains("player_sensor") ? contact.getFixtureA().getBody() : contact.getFixtureB().getBody();
                    Body dummyBody = fixtureA.contains("dummy_player") ? contact.getFixtureA().getBody() : contact.getFixtureB().getBody();
                    Fixture playerFixture = fixtureA.contains("player_sensor") ? contact.getFixtureA() : contact.getFixtureB();

                    if (playerBody.getPosition().y > dummyBody.getPosition().y - player.getHeight() / 2f) {
                        if (dummyBody.getUserData() instanceof PlayerDummy dummyPlayer) {
                            System.out.println("Player jumped on dummy player: " + dummyPlayer);
                            playerBody.setLinearVelocity(new Vector2(0, Player.getJUMP_FORCE()));
                        }
                    }
                }
                player.collisionCount++;

            }
            if (fixtureA.contains("player_hurtbox") || fixtureB.contains("player_hurtbox")) {
                if (fixtureA.contains("hazard") || fixtureB.contains("hazard")) {
                    EventBus.call(PlayerHitHazardEvent.class, new PlayerHitHazardEvent(player));
                }
            }
        }
    }
}
