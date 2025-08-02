package ing.boykiss.gmtk25;

public class Constants {
    public static final int TPS = 60;
    public static final float UNIT_SCALE = 1.0f / 8.0f;
    public static final float VIEWPORT_WIDTH = 320.0f * UNIT_SCALE;
    public static final float VIEWPORT_HEIGHT = 180.0f * UNIT_SCALE;

    public static final float CAMERA_SPEED = 500.0f * UNIT_SCALE;
    public static final float CAMERA_PLAYER_DISTANCE = 100.0f * UNIT_SCALE; // Distance from the player to the camera before it starts moving

    public static final int VELOCITY_ITERATIONS = 10;
    public static final int POSITION_ITERATIONS = 10;

    public static final float GRAVITY = 125;

    public static final float VOLUME = 0.2f;
}
