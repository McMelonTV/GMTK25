package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.physics.box2d.*;
import ing.boykiss.gmtk25.actor.interactable.InteractableButton;
import ing.boykiss.gmtk25.event.Event;

public class InteractableCollisionListener implements ContactListener {
    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            String fixtureA = contact.getFixtureA().getUserData() instanceof String ? (String) contact.getFixtureA().getUserData() : "unknown";
            String fixtureB = contact.getFixtureB().getUserData() instanceof String ? (String) contact.getFixtureB().getUserData() : "unknown";

            if (fixtureA.equals("button")) {
                ((InteractableButton)contact.getFixtureA().getBody().getUserData()).getOnExit().call(new Event() {});
            }  else if (fixtureB.equals("button")) {
                ((InteractableButton)contact.getFixtureB().getBody().getUserData()).getOnExit().call(new Event() {});
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            String fixtureA = contact.getFixtureA().getUserData() instanceof String ? (String) contact.getFixtureA().getUserData() : "unknown";
            String fixtureB = contact.getFixtureB().getUserData() instanceof String ? (String) contact.getFixtureB().getUserData() : "unknown";

            if (fixtureA.equals("button")) {
                ((InteractableButton)contact.getFixtureA().getBody().getUserData()).getOnEnter().call(new Event() {});
            } else if (fixtureB.equals("button")) {
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
