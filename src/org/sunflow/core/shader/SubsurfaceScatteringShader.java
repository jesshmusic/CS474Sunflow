package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.image.Color;

public class SubsurfaceScatteringShader implements Shader
{

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
		
	}

	@Override
	public Color getRadiance(ShadingState state)
	{
		return null;
	}
	
	@Override
	public void scatterPhoton(ShadingState state, Color power) { }
}
