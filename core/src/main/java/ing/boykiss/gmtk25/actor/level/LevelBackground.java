package ing.boykiss.gmtk25.actor.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import ing.boykiss.gmtk25.Constants;
import ing.boykiss.gmtk25.registry.AssetRegistry;

public class LevelBackground extends Image {
    private final Viewport viewport;
    private final ShaderProgram shader;
    private float time;

    public LevelBackground(Viewport viewport) {
        super(AssetRegistry.FILL_TEXTURE);
        this.setColor(Color.DARK_GRAY);
        this.viewport = viewport;

        ShaderProgram.pedantic = false; // less strict on uniforms
        shader = new ShaderProgram(
            Gdx.files.internal(AssetRegistry.BACKGROUND_VERTEX_SHADER_PATH),
            Gdx.files.internal(AssetRegistry.BACKGROUND_FRAGMENT_SHADER_PATH)
        );
        if (!shader.isCompiled()) {
            Gdx.app.error("ShaderError", shader.getLog());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        time += Gdx.graphics.getDeltaTime();

        batch.end();
        batch.flush();

        ShaderProgram.pedantic = false;

        batch.begin();
        batch.setShader(shader);

        shader.setUniformf("u_time", time);
        shader.setUniformf("u_viewportRes", viewport.getWorldWidth() * Constants.UNIT_SCALE, viewport.getWorldHeight() * Constants.UNIT_SCALE);

        this.getDrawable().draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
}
