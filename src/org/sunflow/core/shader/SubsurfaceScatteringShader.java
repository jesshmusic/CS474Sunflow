package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
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
	private Color diffuseColor = Color.BLUE;
	
	// Values needed for BSSRDF
	private float eta = 1.3f;
	
	// These values are taken from the table in (1) and converted to m^-1
	private float sigmaSprime = new Color(0.74f, 0.88f, 1.01f).getAverage() / 1000 * 0.1f;
	private float sigmaA = new Color(0.032f, 0.17f, 0.48f).getAverage() / 1000 * 0.1f;
	
		
	@Override
	public boolean update(ParameterList pl, SunflowAPI api)
	{
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
			Color c = state.diffuse(diffuseColor); 
			return c;
		}
		// Do SSS
		else
		{
			// Embed point in material
			float sigmaTprime = sigmaSprime + sigmaA;
			float depth = (1/sigmaTprime) / 1000;
			Point3 originPoint = new Point3(state.getPoint());
			originPoint.x -= state.getNormal().x * depth;
			originPoint.y -= state.getNormal().y * depth;
			originPoint.z -= state.getNormal().z * depth;
			
			// Sample all directions and weight returned color by distance
			Color rad = Color.black();
			int numberOfSamples = 16;
			for(int i=0; i<numberOfSamples; i++)
			{
				double r1 = state.getRandom(0, i);
				double r2 = state.getRandom(1, i);
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
				
				float scale = getScaleBSSRDF(r);
				rad.madd(scale, c);
			}
			
			// Average out sample contributions
			rad.mul(1f/numberOfSamples);
			
			// Add in diffuse color as well
			state.initLightSamples();
			Color c = state.diffuse(diffuseColor);
			rad.add(c);
			return rad;
		}
	}
	
	private float getScaleSimple(float r)
	{
		return (float)Math.exp(-r*r);
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
		return scale;
	}
	@Override
	public void scatterPhoton(ShadingState state, Color power) { }
}
