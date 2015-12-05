package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.Texture;
import org.sunflow.core.TextureCache;
import org.sunflow.image.Color;
import org.sunflow.math.MathUtils;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

/**
 * (1) https://graphics.stanford.edu/papers/bssrdf/bssrdf.pdf
 * (2) https://graphics.stanford.edu/papers/fast_bssrdf/fast_bssrdf.pdf
 * (3) https://graphics.cg.uni-saarland.de/fileadmin/cguds/courses/ws1011/cg1/rc/Garrido_web/papers/Hery_ImplementingASkinBSSRDF.pdf
 */
public class SubsurfaceScatteringShader implements Shader
{
	private Color diffuseColor = Color.white();
	private Texture tex;
	private int samples = 16;
	
	// How reflective the surface is
	private float refl = 0.1f;
	
	// A value which affects how far light travels in the substance: bigger means it goes further
	private float attenuation = 10f;
	
	// This is a 0-1 value controlling how much of the final color is plain diffuse
	float percentDiffuse = 0f;
	
	// Controls how much light comes through as transparency 
	private float transparencyPower = 0f;
	
	// Controls how focused the transparency looks
	private float transparencyFocus = 0f;
	
	// Values needed for BSSRDF
	private float eta = 1.3f;
	// These values are taken from the table in (1) and converted to m^-1
	private float sigmaSprime = new Color(0.74f, 0.88f, 1.01f).getAverage() * 1000;
	private float sigmaA = new Color(0.032f, 0.17f, 0.48f).getAverage() * 1000;
	
	// A thread value that controls whether recursive raytracing is for diffuse color or transparency
	private static ThreadLocal<Boolean> transparency = new ThreadLocal<>();
	
	public SubsurfaceScatteringShader()
	{
		transparency.set(false);
	}
		
	@Override
	public boolean update(ParameterList pl, SunflowAPI api)
	{
		diffuseColor = pl.getColor("color", diffuseColor);
		String filename = pl.getString("texture", null);
        if (filename != null)
            tex = TextureCache.getTexture(api.resolveTextureFilename(filename), false);
        samples = pl.getInt("samples", samples);
        refl = pl.getFloat("reflectance", refl);
        attenuation = pl.getFloat("attenuation", attenuation);
        transparencyPower = pl.getFloat("tpow", transparencyPower);
        transparencyFocus = pl.getFloat("tfoc", transparencyFocus);
		return true;
	}
	
	@Override
	public Color getRadiance(ShadingState state)
	{
		// Base case, return diffuse or transparency
		if(state.getDepth() > 0)
		{
			// Diffuse case
			if(transparency.get() == null || !transparency.get())
			{
				// This check prevents samples taken from behind from ending up as black
				if(!state.checkBehind())
				{
					state.faceforward();
				}
				
				state.initLightSamples();
				return state.diffuse(getDiffuse(state));
			}
			// Transparent case
			else
			{				
				// This check prevents samples taken from behind from ending up as black
				if(!state.checkBehind())
				{
					state.faceforward();
				}
				
				Ray r = new Ray(state.getPoint(), state.getRay().getDirection());
				Color c = state.traceReflection(r, 0); 
				return c;
			}
		}
		// Do SSS
		else
		{
			// Embed point in material
			float sigmaTprime = sigmaSprime + sigmaA;
			float depth = (1/sigmaTprime);
			Point3 originPoint = new Point3(state.getPoint());
			originPoint.x -= state.getNormal().x * depth;
			originPoint.y -= state.getNormal().y * depth;
			originPoint.z -= state.getNormal().z * depth;
			
			// Sample all directions and weight returned color by distance
			Color rad = Color.black();
			int n = (int)Math.ceil(Math.sqrt(samples));
			for(int i=0; i<n; i++)
			{
				for(int j=0; j<n; j++)
				{
					// Stratified sampling
					double r1 = state.getRandom(i*n+j, 0) * (1f/n) + (float)i/n;
					double r2 = state.getRandom(i*n+j, 1) * (1f/n) + (float)j/n;
					
					float theta = (float)Math.acos(1 - 2 * r1);
					float phi = (float) (Math.PI * 2 * r2);
					
					Vector3 direction = new Vector3();
					direction.x = (float)(Math.sin(theta) * Math.cos(phi));
					direction.y = (float)(Math.sin(theta) * Math.sin(phi));
					direction.z = (float)Math.cos(theta);
					
					transparency.set(false);
					Point3 p = new Point3();
					Ray sampleRay = new Ray(originPoint, direction);
					Color c = state.traceRefraction(sampleRay, i, p);
					Vector3 diff = Point3.sub(p, state.getPoint(), new Vector3());
					float r = diff.length();
					float scale = getScale(r);
					
					// Add in transparency term
					if(transparencyPower > 0)
					{
						transparency.set(true);
						Vector3 rayDir = state.getRay().getDirection();
						float dot = Vector3.dot(direction, rayDir) / (rayDir.length()*direction.length());
						float percentTransparency = MathUtils.clamp(dot, 0, 1);
						percentTransparency = (float)Math.pow(percentTransparency, transparencyFocus) * transparencyPower;
						
						// Give transparency a double helping of scale so it does not dominate scattering
						percentTransparency *= scale;
						
						if(percentTransparency > 0)
						{
							Color t = state.traceRefraction(sampleRay, i);
							t.mul(percentTransparency);
							c.add(t);
						}
					}
					
					c.mul(scale);
					rad.add(c);
				}
			}			
			
			// Average out sample contributions
			rad.mul(1f/(n*n));
			
			// Compute Fresnel term
			if (state.includeSpecular())
			{
				state.faceforward();
		        float cos = state.getCosND();
		        float dn = 2 * cos;
		        Vector3 refDir = new Vector3();
		        refDir.x = (dn * state.getNormal().x) + state.getRay().getDirection().x;
		        refDir.y = (dn * state.getNormal().y) + state.getRay().getDirection().y;
		        refDir.z = (dn * state.getNormal().z) + state.getRay().getDirection().z;
		        Ray refRay = new Ray(state.getPoint(), refDir);
		        // compute Fresnel term
		        cos = 1 - cos;
		        float cos2 = cos * cos;
		        float cos5 = cos2 * cos2 * cos;
	
		        Color ret = Color.white();
		        Color r = getDiffuse(state).copy().mul(refl);
		        ret.sub(r);
		        ret.mul(cos5);
		        ret.add(r);
		        
		        rad.add(ret.mul(state.traceReflection(refRay, 0)));
			}
			
			// Take a ratio of the normal diffuse to the SSS color. This helps prevent overall darkening.
			{
				state.initLightSamples();
				Color c = state.diffuse(getDiffuse(state));
				c.mul(percentDiffuse);
				Color d = rad.copy();
				d.mul(1 - percentDiffuse);
				d.add(c);
				
				// Take the SSS if it's brighter to get it showing in dark patches
				rad = Color.max(rad, d);
			}
			
			return rad;
		}
	}

	private Color getDiffuse(ShadingState state)
	{
		return tex != null ? tex.getPixel(state.getUV().x, state.getUV().y) : diffuseColor;
	}
	
	// Put the desired scale algorithm here
	private float getScale(float r)
	{
		return getScaleSimple(r);
	}
	
	private float getScaleSimple(float r)
	{
		return (float)Math.exp(-r*r/attenuation);
	}
	
	private float getScaleBSSRDF(float r)
	{
		// Compute all the factors in the crazy BSSRDF equation (eq 4 in paper 1)
		float fdr = -1.44f / (eta * eta) + 0.71f / eta + 0.688f + 0.0636f * eta;
		float sigmaTprime = sigmaSprime + sigmaA;
		float A = (1 + fdr)/(1 - fdr);
		float D = 1/(3*sigmaTprime);
		float zr = 1/sigmaTprime;
		float zv = zr + 4 * A * D;
		float sigmaTR = (float)Math.sqrt(3 * sigmaA * sigmaTprime);
		float alphaPrime = sigmaSprime / sigmaTprime;
		float dv = (float)Math.sqrt(r * r + zv * zv);
		float dr = (float)Math.sqrt(r * r + zr * zr);
		
		float scale = 0;
		scale += (sigmaTR * dr + 1) * (float)Math.pow(Math.E, -sigmaTR * dr) / (sigmaTprime * dr * dr * dr);
		scale += zv * (sigmaTR * dv + 1) * (float)Math.pow(Math.E, -sigmaTR * dv) / (sigmaTprime * dv * dv * dv);
		scale *= alphaPrime / (4 * (float)Math.PI);
		scale /= (float)Math.PI;
		return scale;
	}
	
	@Override
	public void scatterPhoton(ShadingState state, Color power) { }
}
