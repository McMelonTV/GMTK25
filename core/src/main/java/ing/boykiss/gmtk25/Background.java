package ing.boykiss.gmtk25;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Background extends Image {
    private final Viewport viewport;
    private final ShaderProgram shader = new ShaderProgram(
        Gdx.files.internal("shaders/background.vsh"),
        Gdx.files.internal("shaders/background.fsh")
    );
    private float time;

    public Background(Viewport viewport) {
        super(new Texture("textures/fill.png"));
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
