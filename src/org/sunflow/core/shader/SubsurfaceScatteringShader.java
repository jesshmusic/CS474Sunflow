package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.Texture;
import org.sunflow.core.TextureCache;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.image.Color;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

/**
 * (1) https://graphics.stanford.edu/papers/bssrdf/bssrdf.pdf
 * (2) https://graphics.stanford.edu/papers/fast_bssrdf/fast_bssrdf.pdf
 * (3) https://graphics.cg.uni-saarland.de/fileadmin/cguds/courses/ws1011/cg1/rc/Garrido_web/papers/Hery_ImplementingASkinBSSRDF.pdf
 */
public class SubsurfaceScatteringShader implements Shader
{
	private TriangleMesh mesh;
	private Color diffuseColor = Color.white();
	private Texture tex;
	private int samples = 16;
	private float refl = 0.1f;
	
	// A value which affects how far light travels in the substance: bigger means it goes further
	private float attenuation = 10f;
	
	// Values needed for BSSRDF
	private float eta = 1.3f;
	
	// These values are taken from the table in (1) and converted to m^-1
	private float sigmaSprime = new Color(0.74f, 0.88f, 1.01f).getAverage() * 1000;
	private float sigmaA = new Color(0.032f, 0.17f, 0.48f).getAverage() * 1000;

		
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
		return true;
	}
	
	/**
	 * Set the mesh to be used in subsurface shading
	 * @param mesh
	 */
	public void setMesh(TriangleMesh mesh)
	{
		this.mesh = mesh;
	}

	
	@Override
	public Color getRadiance(ShadingState state)
	{
		// Base case, return diffuse 
		if(state.getDepth() > 0)
		{
			// This check prevents samples taken from behind from ending up as black
			if(!state.checkBehind())
			{
				state.faceforward();
			}
			
			state.initLightSamples();
			return state.diffuse(getDiffuse(state));
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
					
					Point3 p = new Point3();
					Color c = state.traceRefraction(new Ray(originPoint, direction), i, p);
					Vector3 diff = Point3.sub(p, state.getPoint(), new Vector3());
					float r = diff.length();
					
					// float scale = getScaleBSSRDF(r);
					float scale = getScaleSimple(r);
					rad.madd(scale, c);
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

			return rad;
		}
	}

	private Color getDiffuse(ShadingState state)
	{
		return tex != null ? tex.getPixel(state.getUV().x, state.getUV().y) : diffuseColor;
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
