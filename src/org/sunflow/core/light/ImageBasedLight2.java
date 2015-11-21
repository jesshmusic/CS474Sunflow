package org.sunflow.core.light;

import java.util.Random;

import org.sunflow.SunflowAPI;
import org.sunflow.core.IntersectionState;
import org.sunflow.core.LightSample;
import org.sunflow.core.LightSource;
import org.sunflow.core.ParameterList;
import org.sunflow.core.PrimitiveList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.Texture;
import org.sunflow.core.TextureCache;
import org.sunflow.image.Bitmap;
import org.sunflow.image.Color;
import org.sunflow.math.BoundingBox;
import org.sunflow.math.Matrix4;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

public class ImageBasedLight2 implements PrimitiveList, LightSource, Shader {
	private Texture envmap;	// HDR environment map
	private OrthoNormalBasis basis;
	private int numSamples;	// number of light samples
	private float jacobian;
	private float[] CDF;	// array storing CDF
	private String method;	// possible choices are: random, stratified, imp
	private Random rand1, rand2, rand3;		// two random number generators
	private int mapwidth, mapheight;	// width and height of the environment map
	private int pdfwidth, pdfheight;	// width and height of the PDF/CDF

	public ImageBasedLight2() {
		envmap = null;
		method = null;
		updateBasis(new Vector3(0, 0, -1), new Vector3(0, 1, 0));
		numSamples = 64;
		mapwidth = mapheight = 0;
		pdfwidth = 64;		// default pdfwidth
		pdfheight = 32;		// default pdfheight
		rand1 = new Random(1234567);	// random seeds
		rand2 = new Random(7654321);
		rand3 = new Random(59384738);
		CDF = new float[pdfwidth*pdfheight];        
	}

	private void updateBasis(Vector3 center, Vector3 up) {
		if (center != null && up != null) {
			basis = OrthoNormalBasis.makeFromWV(center, up);
			basis.swapWU();
			basis.flipV();
		}
	}

	@Override
	// This function parses parameters defined for ImageBasedLight2
	// and update class internal variables accordingly
	public boolean update(ParameterList pl, SunflowAPI api) {
		// TODO Auto-generated method stub
		updateBasis(pl.getVector("center", null), pl.getVector("up", null));
		numSamples = pl.getInt("samples", numSamples);
		if (method == null)
			method = pl.getString("method", null);
		String filename = pl.getString("texture", null);
		if (filename != null)
			envmap = TextureCache.getTexture(api.resolveTextureFilename(filename), true);

		// no texture provided
		if (envmap == null)
			return false;
		Bitmap b = envmap.getBitmap();
		if (b == null)
			return false;

		mapwidth = b.getWidth();
		mapheight = b.getHeight();
		jacobian = (float) (2 * Math.PI * Math.PI);


		if (method.equals("imp")) {
			// If using importance sampling, you need to process
			// the environment map and build the PDF/CDF
			// To do so, imagine partitioning the environment map (stored in Bitmap b)
			// into pdfwidth x pdfheight blocks, then compute the total 
			// luminance value of each block. Each pixel of the map
			// stores the RGB color, and you can use the Color class's getLuminance() function
			// to convert it to a luminance value. The sum of the luminance values of 
			// all pixels in a block gives the total luminance, and this will be the importance
			// value of the block. Finally, you can normalize the importance values to create a
			// PDF, from which you then compute the CDF. If the PDF is normalized and CDF is
			// computed correctly, the last element of the CDF should be equal to 1.0.

			// ----- WRITE YOUR IMPLEMENTATION BELOW ---------
			
			//	Get total luminance (for generating the PDF)
			float[] PDF = new float[CDF.length];
			float totLum = 0.0f;
			for (int x = 0; x < b.getWidth(); x++) {
				for (int y = 0; y < b.getHeight(); y++) {
					totLum = totLum + b.getPixel(x, y).getLuminance();
				}
			}

			int xStart = 0;
			int yStart = 0;
			int pdfIndex = 0;
			for (int i = 0; i < pdfwidth; i++) {
				yStart = 0;
				for (int j = 0; j < pdfheight; j++) {
					float lum = 0.0f;
					for (int x = xStart; x < ((b.getWidth() / pdfwidth) + xStart); x++) {
						for (int y = yStart; y < ((b.getHeight() / pdfheight)+ yStart); y++) {
							lum = lum + b.getPixel(x, y).getLuminance();
						}
					}
					yStart = yStart + (b.getHeight() / pdfheight);
					PDF[pdfIndex] = lum;
					pdfIndex++;
				}
				xStart = xStart + (b.getWidth() / pdfwidth);
			}
			
			//	Generate CDF
			CDF[0] = (PDF[0]);
			for (int i = 1; i < CDF.length; i++) {
				CDF[i] = CDF[i-1] + (PDF[i]);
			}
			for (int i = 0; i < CDF.length; i++) {
				CDF[i] = CDF[i] / totLum;
			}
			//	Something is going on with float precision, so just to make sure:
			CDF[CDF.length - 1] = 1.0f;
		}
		return true;
	}

	public void init(String name, SunflowAPI api) {
		// register this object with the api properly
		api.geometry(name, this);
		if (api.lookupGeometry(name) == null) {
			// quit if we don't see our geometry in here (error message
			// will have already been printed)
			return;
		}
		api.shader(name + ".shader", this);
		api.parameter("shaders", name + ".shader");
		api.instance(name + ".instance", name);
		api.light(name + ".light", this);
	}

	@Override
	public int getNumSamples() {
		// TODO Auto-generated method stub
		return numSamples;	// return the number of lights amples
	}

	public void prepareShadingState(ShadingState state) {
		if (state.includeLights())
			state.setShader(this);
	}

	public void intersectPrimitive(Ray r, int primID, IntersectionState state) {
		if (r.getMax() == Float.POSITIVE_INFINITY)
			state.setIntersection(0, 0, 0);
	}

	public int getNumPrimitives() {
		return 1;
	}

	public float getPrimitiveBound(int primID, int i) {
		return 0;
	}

	public BoundingBox getWorldBounds(Matrix4 o2w) {
		return null;
	}

	public PrimitiveList getBakingPrimitives() {
		return null;
	}

	@Override
	// This function computes a set of samples on the light source
	// and weighs each sample according to the sampling probability
	public void getSamples(ShadingState state) {
		// During path tracing, if this is the first intersection
		// compute all samples; if this is beyond the first intersection
		// compute just one sample, to avoid computation from growing exponentially
		int n = state.getDiffuseDepth() > 0 ? 1 : numSamples;
		if (method.equals("random")) {
			// This is the example of uniform random sampling
			// It computes n samples, by randomly sampling the environment map
			for (int i=0; i<n; i++) {
				// generate two random numbers
				float xi_1 = rand1.nextFloat();
				float xi_2 = rand2.nextFloat();

				double phi = xi_1 * Math.PI * 2;
				double theta=xi_2 * Math.PI;

				// invP is the inverse of the PDF
				// it is proportional to the delta solid angle represented by each sample
				float invP = (float) Math.sin(theta) * jacobian/n;

				// The code below accesses the radiance of the environment map
				// at the selected sample, and add it to the sample set.
				// You can duplicate the code for your stratified and imp implementations.
				Vector3 dir = getDirection(theta, phi);
				basis.transform(dir);
				if (Vector3.dot(dir, state.getGeoNormal()) > 0) {
					LightSample dest = new LightSample();
					dest.setShadowRay(new Ray(state.getPoint(), dir));
					dest.getShadowRay().setMax(Float.MAX_VALUE);
					Color radiance = envmap.getPixel(xi_1, xi_2);
					dest.setRadiance(radiance, radiance);
					dest.getDiffuseRadiance().mul(invP);
					dest.getSpecularRadiance().mul(invP);
					dest.traceShadow(state);
					state.addSample(dest);
				}					
			}
		} else if (method.equals("stratified")) {
			int dim = (int)Math.sqrt(n);
//			System.out.printf("DIM = %d\n", dim);
			// Generate dim x dim samples using stratified sampling.
			// Here we assume the total number of samples n is a square number,
			// so dim x dim = n

			// ----- WRITE YOUR IMPLEMENTATION BELOW ---------
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					float partitionX = (float)i / (float)dim;
					float partitionY = (float)j / (float)dim;
					float xi_1 = (rand1.nextFloat() / (float)dim) + partitionX;
					float xi_2 = (rand2.nextFloat() / (float)dim) + partitionY;

					double phi = xi_1 * Math.PI * 2;
					double theta=xi_2 * Math.PI;

					// invP is the inverse of the PDF
					// it is proportional to the delta solid angle represented by each sample
					float invP = (float) Math.sin(theta) * jacobian/n;

					// The code below accesses the radiance of the environment map
					// at the selected sample, and add it to the sample set.
					// You can duplicate the code for your stratified and imp implementations.
					Vector3 dir = getDirection(theta, phi);
					basis.transform(dir);
					if (Vector3.dot(dir, state.getGeoNormal()) > 0) {
						LightSample dest = new LightSample();
						dest.setShadowRay(new Ray(state.getPoint(), dir));
						dest.getShadowRay().setMax(Float.MAX_VALUE);
						Color radiance = envmap.getPixel(xi_1, xi_2);
						dest.setRadiance(radiance, radiance);
						dest.getDiffuseRadiance().mul(invP);
						dest.getSpecularRadiance().mul(invP);
						dest.traceShadow(state);
						state.addSample(dest);
					}
				}
			}


		} else if (method.equals("imp")) {
			// Generate n samples using importance sampling
			// First make use of the CDF to pick a block (with probability proportional to
			// the block's importance value). Then generate a random sample within the selected block

			// ----- WRITE YOUR IMPLEMENTATION BELOW ---------
			for (int i=0; i<n; i++) {
				// generate two random numbers
				float ksi = rand3.nextFloat();
				int x = 0;
				
				int min = 0;
				int max = CDF.length - 1;
				while (min <= max) {
					int mid = (min + max) / 2;
					if (CDF[mid] == ksi) {
						min = mid;
						break;
					} else if (CDF[mid] < ksi) {
						min = mid + 1;
					} else {
						max = mid - 1;
					}
				}
				x = min;
				if (x < 0) {
					x = 0;
				} else if (x > CDF.length - 1) {
					x = CDF.length - 1;
				}
				
				float a = x / pdfheight;
				float b = x % pdfheight;
				
				float xBlock = a / (float)pdfwidth;
				float yBlock = b / (float)pdfheight;
				
				float xBlockSize = 1.0f / (float)pdfwidth;
				float yBlockSize = 1.0f / (float)pdfheight;
				
				float xi_1 = xBlock + (rand1.nextFloat() * xBlockSize);
				float xi_2 = yBlock + (rand2.nextFloat() * yBlockSize);

				double phi = xi_1 * Math.PI * 2;
				double theta = xi_2 * Math.PI;
				
				// invP is the inverse of the PDF
				// it is proportional to the delta solid angle represented by each sample
				float pdfValue = 0.0f;
				if (x == 0) {
					pdfValue = CDF[0];
				} else {
					
					//	I noticed that in the 'checker.hdr' PDF, many values would end up being 0.0, 
					//	so I iterate backwards until it finds a pdfValue > 0
					int x2 = x;
					while (pdfValue == 0.0f) {
						pdfValue = CDF[x2] - CDF[x2-1];
						x2--;
						if(x2 == 0) {
							pdfValue = CDF[0];
							break;
						}
					}
				}
				float invP = (float) Math.sin(theta) * jacobian/n;
				invP = invP / (pdfValue * pdfheight * pdfwidth);

				
				// The code below accesses the radiance of the environment map
				// at the selected sample, and add it to the sample set.
				// You can duplicate the code for your stratified and imp implementations.
				Vector3 dir = getDirection(theta, phi);
				basis.transform(dir);
				if (Vector3.dot(dir, state.getGeoNormal()) > 0) {
					LightSample dest = new LightSample();
					dest.setShadowRay(new Ray(state.getPoint(), dir));
					dest.getShadowRay().setMax(Float.MAX_VALUE);
					Color radiance = envmap.getPixel(xi_1, xi_2);
					dest.setRadiance(radiance, radiance);
					dest.getDiffuseRadiance().mul(invP);
					dest.getSpecularRadiance().mul(invP);
					dest.traceShadow(state);
					if (dest.getDiffuseRadiance().isNan()) {
						System.out.printf("\n\n---------NaN color found! x=%d\n\tinvP = %f, denominator: %f\n\tPDF value: %f\n\n", x, invP, (pdfValue * pdfheight * pdfwidth), pdfValue);
					}
					state.addSample(dest);
				}
			}
		}
	}

	@Override
	public void getPhoton(double randX1, double randY1, double randX2,
			double randY2, Point3 p, Vector3 dir, Color power) {
		// TODO Auto-generated method stub

	}

	public void scatterPhoton(ShadingState state, Color power) {
	}

	public Color getRadiance(ShadingState state) {
		// lookup texture based on ray direction
		return state.includeLights() ? getColor(basis.untransform(state.getRay().getDirection(), new Vector3())) : Color.BLACK;
	}	

	private Color getColor(Vector3 dir) {
		float u, v;
		// assume lon/lat format
		double phi, theta;
		theta = Math.acos(dir.y);
		phi = Math.atan2(dir.z, dir.x);
		u = (float) (0.5 - 0.5 * phi / Math.PI);
		v = (float) (theta / Math.PI);
		return envmap.getPixel(u, v);
	}

	// Returns direction (in terms of x, y, z) given spherical
	// angles theta and phi
	private Vector3 getDirection(double theta, double phi) {
		Vector3 dest = new Vector3();
		double sin_theta = Math.sin(theta);
		dest.x = (float) (-sin_theta * Math.cos(phi));
		dest.y = (float) Math.cos(theta);
		dest.z = (float) (sin_theta * Math.sin(phi));
		return dest;
	}

	@Override
	public float getPower() {
		// TODO Auto-generated method stub
		return 0;
	}

}

