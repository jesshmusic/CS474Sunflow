package org.sunflow.core.shader;

import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.core.primitive.TriangleMesh.MeshSampler;
import org.sunflow.image.Color;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

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
		final ShadingState fstate = state; 
		final Point3 hitPoint = new Point3(fstate.getPoint());
		final Color color = Color.black();
		
		final float eta = this.eta;
		final float sigmaSprime = this.sigmaSprime;
		final float sigmaA = this.sigmaA;
		
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
					r = distance(hitPoint, position);
				}
				
				// Compute all the factors in the crazy BSSRDF equation (eq 4 in paper)
				float fdr = 1.44f / (eta * eta) + 0.71f / eta + 0.688f + 0.0636f * eta;
				float sigmaTprime = sigmaSprime + sigmaA;
				float A = (1 + fdr)/(1 - fdr);
				float D = 1/(3*sigmaTprime);
				float zr = 1/sigmaTprime;
				float zv = zr + 4 * A * D;
				float sigmaTR = (float)Math.sqrt(3 * sigmaA * sigmaTprime);
				float alphaPrime = sigmaSprime / sigmaTprime;
				float dv = 0;
				{
					zv /= 1000; // Convert to m
					Point3 v = new Point3(position);
					v.x += normal.x * zv;
					v.y += normal.y * zv;
					v.z += normal.z * zv;
					dv = (float)Math.sqrt(r * r + zv * zv);
					zv *= 1000; // Revert to mm
					dv /= 1000; // Convert to mm
				}
				float dr = 0;
				{
					zr /= 1000; // Convert to mm
					Point3 rp = new Point3(position);
					rp.x -= normal.x * zr ;
					rp.y -= normal.y * zr;
					rp.z -= normal.z * zr;
					dr = (float)Math.sqrt(r * r + zr * zr);
					zr *= 1000; // Revert to mm
					dr /= 1000; // Convert to mm
				}
				
				float scale = 0;
				scale += zr * (sigmaTR * dr + 1) * (float)Math.pow(Math.E, -sigmaTR * dr) / (sigmaTprime * dr * dr * dr);
				scale += zv * (sigmaTR * dv + 1) * (float)Math.pow(Math.E, -sigmaTR * dv) / (sigmaTprime * dv * dv * dv);
				scale *= alphaPrime / (4 * (float)Math.PI);
				scale *= area;
				scale *= 1 / (float) Math.PI;
				
				//float scale = (float)Math.pow(Math.E, -r*r/10) * area;
				
				fstate.getPoint().set(position);
				fstate.getNormal().set(normal);
				fstate.getGeoNormal().set(normal);
				fstate.setBasis(OrthoNormalBasis.makeFromW(fstate.getNormal()));
				// fstate.faceforward();
				fstate.initLightSamples();
				
				Color dipoleContribution = fstate.diffuse(diffuseColor);
				dipoleContribution.mul(scale);
				color.add(dipoleContribution);
			}
			
			float distance(Point3 a, Point3 b)
			{
				float dx = a.x - b.x;
				float dy = a.y - b.y;
				float dz = a.z - b.z;
				return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
			}
		});
		
		/*
		state.getRay().dx = -1;
		state.getRay().dy = 0;
		state.getRay().dz = 0;
		
		state.faceforward();
		state.initLightSamples();
		return state.diffuse(diffuseColor);
		*/
		return color;
	}
	
	@Override
	public void scatterPhoton(ShadingState state, Color power) { }
}
