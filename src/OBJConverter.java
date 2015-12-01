import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.sunflow.system.UI;
import org.sunflow.system.UI.Module;
import org.sunflow.util.FloatArray;
import org.sunflow.util.IntArray;

public class OBJConverter {


	public static void main(String[] args) {
		if (args.length > 0) {
			parseOBJ(args[0], args[1]);
		}

	}

	//	NOTE: All objects MUST be triangles!!!!
	//			ALSO: The object should not have normals mapped.
	//			In other words the faces should be of the form: "f v1/t1 v2/t2 v3/t3"
	private static void parseOBJ(String inputFilename, String outputFilename) {

		String objectString = "";
        int lineNumber = 1;
        try {
        	File sceneFile = new File(outputFilename);
        	if (!sceneFile.exists()) {
        		sceneFile.createNewFile();
        	}
        	FileWriter fileWriter = new FileWriter(sceneFile.getAbsoluteFile());
        	BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
        	objectString = objectString + "image {\n";
        	objectString = objectString + "  resolution 600 600\n";
        	objectString = objectString + "  aa 0 1\n";
        	objectString = objectString + "  samples 2\n";
        	objectString = objectString + "  filter gaussian\n";
        	objectString = objectString + "}\n\n";
        	
        	objectString = objectString + "trace-depths {\n";
        	objectString = objectString + "  diff 1\n";
        	objectString = objectString + "  refl 4\n";
        	objectString = objectString + "  refr 4\n";
        	objectString = objectString + "}\n\n";
        	
        	objectString = objectString + "gi {\n";
        	objectString = objectString + "  type path\n";
        	objectString = objectString + "  samples 4\n";
        	objectString = objectString + "}\n\n";

        	objectString = objectString + "camera {\n";
        	objectString = objectString + "  type pinhole\n";
        	objectString = objectString + "  eye    -20 -5 10\n";
        	objectString = objectString + "  target 0 0 5\n";
        	objectString = objectString + "  up     0 0 1\n";
        	objectString = objectString + "  fov    60\n";
        	objectString = objectString + "  aspect 1.333333\n";
        	objectString = objectString + "}\n\n";


        	objectString = objectString + "light {\n";
        	objectString = objectString + "   type point\n";
        	objectString = objectString + "   color { \"sRGB nonlinear\" 1.000 1.000 1.000 }\n";
        	objectString = objectString + "   power 5000.0\n";
        	objectString = objectString + "   p 1 4 3\n";
        	objectString = objectString + "}\n\n";
        	
        	objectString = objectString + "\n\nobject {\n";
        	
        	UI.printInfo(Module.GEOM, "OBJ - Reading geometry: \"%s\" ...", inputFilename);
        	FloatArray verts = new FloatArray();
        	FloatArray vertTexs = new FloatArray();
        	IntArray vnXs = new IntArray();
        	IntArray vnYs = new IntArray();
        	IntArray vnZs = new IntArray();
        	IntArray tris = new IntArray();
        	ArrayList<BlendNormal> normals = new ArrayList<BlendNormal>();
        	int normalsIndex = 0;
        	IntArray uvUs = new IntArray();
        	IntArray uvVs = new IntArray();
        	ArrayList<BlendUV> uvs = new ArrayList<BlendUV>();
        	
        	//	Counts
        	int numPoints = 0;
        	int numTriangles = 0;
        	
        	//	Object part strings
        	String currentObjectName = "";
        	
        	//	Parse the obj file. Can't get UVs or normals yet.
        	FileReader file = new FileReader(inputFilename);
        	BufferedReader bf = new BufferedReader(file);
        	String line;
        	while ((line = bf.readLine()) != null) {
        		if (line.startsWith("mtllib")) {
        			String[] mat = line.split("\\s+");
        			objectString = objectString + "\tshader \"" + mat[1] + ".shader\"\n";
        		} else if (line.startsWith("o ")) {
        			String[] name = line.split("\\s+");
        			currentObjectName = name[1];
        		} else if (line.startsWith("v ")) {
        			String[] v = line.split("\\s+");
        			verts.add(Float.parseFloat(v[1]));
        			verts.add(Float.parseFloat(v[2]));
        			verts.add(Float.parseFloat(v[3]));
        			numPoints++;
        		} else if (line.startsWith("vt ")) {
        			String[] vt = line.split("\\s+");
        			uvUs.add((int)Float.parseFloat(vt[1]));
        			uvVs.add((int)Float.parseFloat(vt[2]));
        		}  else if (line.startsWith("vn ")) {
        			String[] vn = line.split("\\s+");
        			vnXs.add((int)Float.parseFloat(vn[1]));
        			vnYs.add((int)Float.parseFloat(vn[2]));
        			vnZs.add((int)Float.parseFloat(vn[3]));
        		} else if (line.startsWith("f ")) {
        			String[] f = line.split("\\s+");
    				numTriangles++;
        			if (f[1].contains("/")) {
        				String[] f1 = f[1].split("/");
        				String[] f2 = f[2].split("/");
        				String[] f3 = f[3].split("/");
        				tris.add(Integer.parseInt(f1[0]) - 1);
        				tris.add(Integer.parseInt(f2[0]) - 1);
        				tris.add(Integer.parseInt(f3[0]) - 1);
        				BlendUV newUV = new BlendUV(Integer.parseInt(f1[1]) - 1, Integer.parseInt(f2[1]) - 1, Integer.parseInt(f3[1]) - 1);
        				BlendNormal newNormal = new BlendNormal(Integer.parseInt(f1[2]) - 1, Integer.parseInt(f2[2]) - 1, Integer.parseInt(f3[2]) - 1);
        				normals.add(newNormal);
        				uvs.add(newUV);
        				
        			} else {
        				tris.add(Integer.parseInt(f[1]) - 1);
        				tris.add(Integer.parseInt(f[2]) - 1);
        				tris.add(Integer.parseInt(f[3]) - 1);
        			}
        		}
        		if (lineNumber % 100000 == 0)
        			UI.printInfo(Module.GEOM, "OBJ -   * Parsed %7d lines ...", lineNumber);
        		lineNumber++;
        	}
        	
        	objectString = objectString + "\ttype generic-mesh\n\tname " + currentObjectName + "\n";
        	
        	//	Add points to the object
        	objectString = objectString + "\tpoints " + numPoints + "\n";
        	for (int i = 0; i < verts.getSize(); i = i + 3) {
				objectString = objectString + "\t  " + verts.get(i) + " " + verts.get(i + 1) + " " + verts.get(i + 2) + "\n";
			}
        	
        	//	Add faces (triangles) to the object
        	objectString = objectString + "\ttriangles " + numTriangles + "\n";
        	for (int i = 0; i < tris.getSize(); i = i + 3) {
				objectString = objectString + "\t  " + tris.get(i) + " " + tris.get(i + 1) + " " + tris.get(i + 2) + "\n";
			}

        	//	Add normals to the object
        	objectString = objectString + "\tnormals facevarying\n";
        	for (BlendNormal blendNormal : normals) {
        		objectString = objectString + "\t  " + blendNormal.getNormalString(vnXs, vnYs, vnZs) + "\n";
			}

        	//	Add normals to the object
        	objectString = objectString + "\tuvs facevarying\n";
        	for (BlendUV blendUV : uvs) {
        		objectString = objectString + "\t  " + blendUV.getUVString(uvUs, uvVs) + "\n";
			}
//        	
        	objectString = objectString + "}";

            bufferWriter.write(objectString);
            bufferWriter.close();
        	System.out.println(objectString);
        	file.close();
            UI.printInfo(Module.GEOM, "OBJ -   * Creating mesh ...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            UI.printError(Module.GEOM, "Unable to read mesh file \"%s\" - file not found", inputFilename);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            UI.printError(Module.GEOM, "Unable to read mesh file \"%s\" - syntax error at line %d", lineNumber);
        } catch (IOException e) {
            e.printStackTrace();
            UI.printError(Module.GEOM, "Unable to read mesh file \"%s\" - I/O error occured", inputFilename);
        }
	}
}

class BlendNormal {
	int x, y, z;
	
	BlendNormal(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String getNormalString(IntArray vnXs, IntArray vnYs, IntArray vnZs) {
		return "\t" + vnXs.get(x) + " " + vnYs.get(x) + " " + vnZs.get(x) + " " +
				 vnXs.get(y) + " " + vnYs.get(y) + " " + vnZs.get(y) + " " +
				 vnXs.get(z) + " " + vnYs.get(z) + " " + vnZs.get(z);
	}
}

class BlendUV {
	int a, b, c;
	
	BlendUV(int a, int b, int c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public String getUVString(IntArray uvUs, IntArray uvVs) {
		return "\t" + uvUs.get(a) + " " + uvVs.get(a) + " " +
				uvUs.get(b) + " " + uvVs.get(b) + " " +
				uvUs.get(c) + " " + uvVs.get(c);
	}
}
