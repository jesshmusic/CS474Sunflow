package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.ShadingState;
import org.sunflow.core.Texture;
import org.sunflow.core.TextureCache;
import org.sunflow.image.Color;

public class TexturedConstantShader extends ConstantShader {
    private Texture tex;
    private float brightness = 2.0f;

	public TexturedConstantShader() {
		tex = null;
	}

    @Override
    public boolean update(ParameterList pl, SunflowAPI api) {
    	brightness = pl.getFloat("brightness", 2.0f);
        String filename = pl.getString("texture", null);
        if (filename != null)
            tex = TextureCache.getTexture(api.resolveTextureFilename(filename), false);
        return tex != null && super.update(pl, api);
    }

    @Override
    public Color getRadiance(ShadingState state) {
    	Color c = tex.getPixel(state.getUV().x, state.getUV().y);
    	float rgb[] = c.getRGB();
    	c.set(rgb[0] * brightness, rgb[1] * brightness, rgb[2] * brightness);
        return c;
    }

}
