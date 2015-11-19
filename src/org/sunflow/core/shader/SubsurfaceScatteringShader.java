package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.core.primitive.TriangleMesh.MeshSampler;
import org.sunflow.image.Color;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

public class SubsurfaceScatteringShader implements Shader
{
	private TriangleMesh mesh;
	private Color diffuseColor = Color.BLUE;
	
	
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
		
		
		final float c = 1.3f;
		final ShadingState fstate = state; 
		final Point3 hitPoint = new Point3(fstate.getPoint());
		final Color color = Color.black();
		
		mesh.sampleMesh(new MeshSampler()
		{
			@Override
			public void sample(Point3 position, Vector3 normal, float area)
			{
				position = fstate.transformObjectToWorld(position);
				normal = fstate.transformNormalObjectToWorld(normal);
				normal.normalize();
				
				float r = 0;
				{
					float dx = position.x - hitPoint.x;
					float dy = position.y - hitPoint.y;
					float dz = position.z - hitPoint.z;
					r = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
				}
				
				float scale = (float)Math.pow(Math.E, -r*r/c) * area;
				fstate.getPoint().set(position);
				fstate.getNormal().set(normal);
				fstate.faceforward();
				fstate.initLightSamples();
				Color dipoleContribution = fstate.diffuse(diffuseColor);
				Color.mul(scale, dipoleContribution, dipoleContribution);
				color.add(dipoleContribution);
			}
		});
		
		return color;
	}
	
	@Override
	public void scatterPhoton(ShadingState state, Color power) { }
}
