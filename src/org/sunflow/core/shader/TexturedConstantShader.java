package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.ShadingState;
import org.sunflow.core.Texture;
import org.sunflow.core.TextureCache;
import org.sunflow.image.Color;

public class TexturedConstantShader extends ConstantShader {
    private Texture tex;

	public TexturedConstantShader() {
		tex = null;
	}

    @Override
    public boolean update(ParameterList pl, SunflowAPI api) {
        String filename = pl.getString("texture", null);
        if (filename != null)
            tex = TextureCache.getTexture(api.resolveTextureFilename(filename), false);
        return tex != null && super.update(pl, api);
    }

    @Override
    public Color getRadiance(ShadingState state) {
    	Color c = tex.getPixel(state.getUV().x, state.getUV().y);
//    	float rgb[] = c.getRGB();
//    	c.set(rgb[0] * 2.0f, rgb[1] * 2.0f, rgb[2] * 2.0f);
        return c;
    }

}
