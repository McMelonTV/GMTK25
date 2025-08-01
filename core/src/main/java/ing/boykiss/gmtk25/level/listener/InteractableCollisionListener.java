package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.actor.interactable.InteractableButton;
import ing.boykiss.gmtk25.event.Event;

import java.util.Arrays;
import java.util.List;

public class InteractableCollisionListener implements ContactListener {
    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            if (fixtureA.contains("button")) {
                ((InteractableButton)contact.getFixtureA().getBody().getUserData()).getOnExit().call(new Event() {});
            }  else if (fixtureB.contains("button")) {
                ((InteractableButton)contact.getFixtureB().getBody().getUserData()).getOnExit().call(new Event() {});
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            if (fixtureA.contains("button")) {
                ((InteractableButton)contact.getFixtureA().getBody().getUserData()).getOnEnter().call(new Event() {});
            } else if (fixtureB.contains("button")) {
                ((InteractableButton)contact.getFixtureB().getBody().getUserData()).getOnEnter().call(new Event() {});
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
