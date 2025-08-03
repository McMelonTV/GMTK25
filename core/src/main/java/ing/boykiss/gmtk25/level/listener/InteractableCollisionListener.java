package ing.boykiss.gmtk25.level.listener;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.actor.level.object.Button;
import ing.boykiss.gmtk25.actor.level.object.Replicator;
import ing.boykiss.gmtk25.actor.level.object.Switch;
import ing.boykiss.gmtk25.actor.level.object.WinFlag;
import ing.boykiss.gmtk25.utils.Tuple;

public class InteractableCollisionListener implements ContactListener {
    private boolean noData(Contact contact) {
        return !isEitherString(getFixtureData(contact));
    }

    private Tuple<Object, Object> getObjects(Contact contact) {
        return new Tuple<>(contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getBody().getUserData());
    }

    private Tuple<Object, Object> getFixtureData(Contact contact) {
        return new Tuple<>(contact.getFixtureA().getUserData(), contact.getFixtureB().getUserData());
    }

    private boolean isEitherString(Tuple<Object, Object> tuple) {
        return tuple.a() instanceof String
            || tuple.b() instanceof String;
    }

    private Tuple<String, String> getFixtureStringData(Tuple<Object, Object> tuple) {
        return new Tuple<>(string(tuple.a()), string(tuple.b()));
    }

    private String string(Object object) {
        return object instanceof String ? (String) object : "";
    }

    /**
     * Tries to get an object of type T from the contact, where either fixture's user data string contains the given key.
     */
    private <T> T tryGet(Class<T> clazz, String key, Contact contact) {
        Tuple<String, String> fixtureData = getFixtureStringData(getFixtureData(contact));
        Tuple<Object, Object> objects = getObjects(contact);
        if (fixtureData.a().contains(key) && clazz.isInstance(objects.a())) {
            return clazz.cast(objects.a());
        } else if (fixtureData.b().contains(key) && clazz.isInstance(objects.b())) {
            return clazz.cast(objects.b());
        }
        return null;
    }

    /**
     * Tries to get an object of type T from the contact, where one fixture's user data string contains key1 and the other's contains key2.
     */
    private <T> T tryGet(Class<T> clazz, String key1, String key2, Contact contact) {
        Tuple<String, String> fixtureData = getFixtureStringData(getFixtureData(contact));
        Tuple<Object, Object> objects = getObjects(contact);
        if (fixtureData.a().contains(key1) && fixtureData.b().contains(key2) && clazz.isInstance(objects.a())) {
            return clazz.cast(objects.a());
        } else if (fixtureData.b().contains(key1) && fixtureData.a().contains(key2) && clazz.isInstance(objects.b())) {
            return clazz.cast(objects.b());
        }
        return null;
    }

    @Override
    public void beginContact(Contact contact) {
        if (noData(contact)) return;

        Button buttonObject = tryGet(Button.class, "button", contact);
        if (buttonObject != null) buttonObject.addCollision();
        WinFlag winFlagObject = tryGet(WinFlag.class, "win_flag", contact);
        if (winFlagObject != null) winFlagObject.addCollision();

        Switch switchObject = tryGet(Switch.class, "switch", "player_sensor", contact);
        if (switchObject != null) GMTK25.getPlayer().setNearestInteractable(switchObject);
        Replicator replicatorObject = tryGet(Replicator.class, "replicator", "player_sensor", contact);
        if (replicatorObject != null) GMTK25.getPlayer().setNearestInteractable(replicatorObject);
    }

    @Override
    public void endContact(Contact contact) {
        if (noData(contact)) return;

        Button buttonObject = tryGet(Button.class, "button", contact);
        if (buttonObject != null) buttonObject.removeCollision();
        WinFlag winFlagObject = tryGet(WinFlag.class, "win_flag", contact);
        if (winFlagObject != null) winFlagObject.removeCollision();

        Switch switchObject = tryGet(Switch.class, "switch", "player_sensor", contact);
        if (switchObject != null) GMTK25.getPlayer().setNearestInteractable(null);
        Replicator replicatorObject = tryGet(Replicator.class, "replicator", "player_sensor", contact);
        if (replicatorObject != null) GMTK25.getPlayer().setNearestInteractable(null);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
