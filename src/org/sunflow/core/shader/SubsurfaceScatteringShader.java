package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.core.primitive.TriangleMesh.MeshSampler;
import org.sunflow.image.Color;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;
import org.sunflow.system.UI;
import org.sunflow.system.UI.Module;
import org.sunflow.system.UI.PrintLevel;

public class SubsurfaceScatteringShader implements Shader
{
	private TriangleMesh mesh;
	private Color diffuseColor = Color.BLUE;
	
	// Values needed for BSSRDF
	private float eta = 1.3f;
	private float sigmaSprime = new Color(0.74f, 0.88f, 1.01f).getAverage(); // 1000;
	private float sigmaA = new Color(0.032f, 0.17f, 0.48f).getAverage(); // 1000;
		
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

	/**
	 * https://graphics.stanford.edu/papers/bssrdf/bssrdf.pdf
	 */
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
			float depth = 0.05f;
			Point3 originPoint = new Point3(state.getPoint());
			originPoint.x -= state.getNormal().x * depth;
			originPoint.y -= state.getNormal().y * depth;
			originPoint.z -= state.getNormal().z * depth;
			
			// Sample all directions and weight returned color by distance
			Color rad = Color.black();
			for(int i=0; i<16; i++)
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
				
				float scale = (float)Math.exp(-r*r);
				rad.madd(scale, c);
			}
			
			// Add in diffuse color as well
			state.initLightSamples();
			Color c = state.diffuse(diffuseColor);
			rad.add(c);
			return rad;
		}
	}
	
	@Override
	public void scatterPhoton(ShadingState state, Color power) { }
}
