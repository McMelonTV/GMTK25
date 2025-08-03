package ing.boykiss.gmtk25.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderRegistry {
    public static final String BACKGROUND_VERTEX_PATH = "shaders/background.vsh";
    public static final String BACKGROUND_FRAGMENT_PATH = "shaders/background.fsh";
    public static final ShaderProgram BACKGROUND;

    public static final String PLAYER_DUMMY_VERTEX_PATH = "shaders/player_dummy.vsh";
    public static final String PLAYER_DUMMY_FRAGMENT_PATH = "shaders/player_dummy.fsh";
    public static final ShaderProgram PLAYER_DUMMY;

    static {
        BACKGROUND = createShader(BACKGROUND_VERTEX_PATH, BACKGROUND_FRAGMENT_PATH);
        PLAYER_DUMMY = createShader(PLAYER_DUMMY_VERTEX_PATH, PLAYER_DUMMY_FRAGMENT_PATH);
    }

    private static ShaderProgram createShader(String vertexPath, String fragmentPath) {
        ShaderProgram.pedantic = false; // less strict on uniforms
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal(vertexPath), Gdx.files.internal(fragmentPath));
        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderError", shader.getLog());
        }
        return shader;
    }
}
