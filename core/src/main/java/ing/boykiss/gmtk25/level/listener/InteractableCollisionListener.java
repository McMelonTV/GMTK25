package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.level.object.Button;
import ing.boykiss.gmtk25.actor.level.object.Switch;

import java.util.Arrays;
import java.util.List;

public class InteractableCollisionListener implements ContactListener {
    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            Button buttonObject = null;
            if (fixtureA.contains("button")) {
                buttonObject = (Button) contact.getFixtureA().getBody().getUserData();
            } else if (fixtureB.contains("button")) {
                buttonObject = (Button) contact.getFixtureB().getBody().getUserData();
            }
            if (buttonObject != null) buttonObject.removeCollision();

            Switch switchObject = null;
            if (fixtureA.contains("switch") && fixtureB.contains("player_sensor")) {
                switchObject = (Switch) contact.getFixtureA().getBody().getUserData();
            } else if (fixtureB.contains("switch") && fixtureA.contains("player_sensor")) {
                switchObject = (Switch) contact.getFixtureB().getBody().getUserData();
            }
            if (switchObject != null) GMTK25.getPlayer().setInteractableSwitch(null);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            Button buttonObject = null;
            if (fixtureA.contains("button")) {
                buttonObject = (Button) contact.getFixtureA().getBody().getUserData();
            } else if (fixtureB.contains("button")) {
                buttonObject = (Button) contact.getFixtureB().getBody().getUserData();
            }
            if (buttonObject != null) buttonObject.addCollision();

            Switch switchObject = null;
            if (fixtureA.contains("switch") && fixtureB.contains("player_sensor")) {
                switchObject = (Switch) contact.getFixtureA().getBody().getUserData();
            } else if (fixtureB.contains("switch") && fixtureA.contains("player_sensor")) {
                switchObject = (Switch) contact.getFixtureB().getBody().getUserData();
            }
            if (switchObject != null) GMTK25.getPlayer().setInteractableSwitch(switchObject);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
