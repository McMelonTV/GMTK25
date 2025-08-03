package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.interactable.InteractableButton;
import ing.boykiss.gmtk25.actor.interactable.InteractableSwitch;
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
                ((InteractableButton) contact.getFixtureA().getBody().getUserData()).getOnExit().call(new Event() {
                });
            } else if (fixtureB.contains("button")) {
                ((InteractableButton) contact.getFixtureB().getBody().getUserData()).getOnExit().call(new Event() {
                });
            }

            // check for switch
            if (fixtureA.contains("switch")) {
                if (fixtureB.contains("player_sensor")) {
                    GMTK25.getPlayer().setInteractableSwitch(null);

                } else if (fixtureB.contains("switch")) {
                    if (fixtureA.contains("player_sensor")) {
                        GMTK25.getPlayer().setInteractableSwitch(null);
                    }
                }
            }
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() instanceof String || contact.getFixtureB().getUserData() instanceof String) {
            List<String> fixtureA = contact.getFixtureA().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureA().getUserData()).split(" ")).toList() : List.of();
            List<String> fixtureB = contact.getFixtureB().getUserData() instanceof String ? Arrays.stream(((String) contact.getFixtureB().getUserData()).split(" ")).toList() : List.of();

            if (fixtureA.contains("button")) {
                ((InteractableButton) contact.getFixtureA().getBody().getUserData()).getOnEnter().call(new Event() {
                });
            } else if (fixtureB.contains("button")) {
                ((InteractableButton) contact.getFixtureB().getBody().getUserData()).getOnEnter().call(new Event() {
                });
            }

            // check for switch
            if (fixtureA.contains("switch")) {
                if (fixtureB.contains("player_sensor")) {
                    GMTK25.getPlayer().setInteractableSwitch(((InteractableSwitch) contact.getFixtureA().getBody().getUserData()));
                }
            } else if (fixtureB.contains("switch")) {
                if (fixtureA.contains("player_sensor")) {
                    GMTK25.getPlayer().setInteractableSwitch(((InteractableSwitch) contact.getFixtureB().getBody().getUserData()));
                }
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
