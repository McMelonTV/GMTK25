package ing.boykiss.gmtk25.actor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import ing.boykiss.gmtk25.registry.AssetRegistry;
import ing.boykiss.gmtk25.utils.Constants;

public class Background extends Image {
    private final Viewport viewport;
    private final ShaderProgram shader = new ShaderProgram(
            Gdx.files.internal(AssetRegistry.BACKGROUND_VERTEX_SHADER_PATH),
            Gdx.files.internal(AssetRegistry.BACKGROUND_FRAGMENT_SHADER_PATH)
    );
    private float time;

    public Background(Viewport viewport) {
        super(AssetRegistry.FILL_TEXTURE);
        this.setColor(Color.DARK_GRAY);
        this.viewport = viewport;
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
