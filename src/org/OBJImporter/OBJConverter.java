package org.OBJImporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

		ArrayList<ImportedOBJ> objects = new ArrayList<>();
		ArrayList<ImportedModelShader> shaders = new ArrayList<>();
		ImportedOBJ currentObject = new ImportedOBJ("");
		String sceneFileContents= "";
		int lineNumber = 1;
		try {
			File sceneFile = new File(outputFilename);
			if (!sceneFile.exists()) {
				sceneFile.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(sceneFile.getAbsoluteFile());
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

//			//	Set up the default scene elements
//			sceneFileContents = sceneFileContents + "image {\n";
//			sceneFileContents = sceneFileContents + "  resolution 640 480\n";
//			sceneFileContents = sceneFileContents + "  aa 0 1\n";
//			sceneFileContents = sceneFileContents + "  samples 4\n";
//			sceneFileContents = sceneFileContents + "  filter mitchell\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//
//			sceneFileContents = sceneFileContents + "camera {\n";
//			sceneFileContents = sceneFileContents + "  type pinhole\n";
//			sceneFileContents = sceneFileContents + "  eye    15 40 15\n";
//			sceneFileContents = sceneFileContents + "  target 0 0 8\n";
//			sceneFileContents = sceneFileContents + "  up     0 0 1\n";
//			sceneFileContents = sceneFileContents + "  fov    60\n";
//			sceneFileContents = sceneFileContents + "  aspect 1.333333\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//
//			sceneFileContents = sceneFileContents + "light {\n";
//			sceneFileContents = sceneFileContents + "  type sunsky\n";
//			sceneFileContents = sceneFileContents + "  up 0 0 1\n";
//			sceneFileContents = sceneFileContents + "  east 0 1 0\n";
//			sceneFileContents = sceneFileContents + "  sundir 1 -1 0.31\n";
//			sceneFileContents = sceneFileContents + "  turbidity 2\n";
//			sceneFileContents = sceneFileContents + "  samples 16\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//			
//			sceneFileContents = sceneFileContents + "light {\n";
//			sceneFileContents = sceneFileContents + "   type point\n";
//			sceneFileContents = sceneFileContents + "   color { \"sRGB nonlinear\" 1.000 1.000 1.000 }\n";
//			sceneFileContents = sceneFileContents + "   power 1000.0\n";
//			sceneFileContents = sceneFileContents + "   p 1 5 10\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//
//
//			sceneFileContents = sceneFileContents + "light {\n";
//			sceneFileContents = sceneFileContents + "   type point\n";
//			sceneFileContents = sceneFileContents + "   color { \"sRGB nonlinear\" 1.000 1.000 1.000 }\n";
//			sceneFileContents = sceneFileContents + "   power 1000.0\n";
//			sceneFileContents = sceneFileContents + "   p 1 5 3\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//
//			sceneFileContents = sceneFileContents + "trace-depths {\n";
//			sceneFileContents = sceneFileContents + "  diff 2\n";
//			sceneFileContents = sceneFileContents + "  refl 4\n";
//			sceneFileContents = sceneFileContents + "  refr 4\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//
//			sceneFileContents = sceneFileContents + "shader {\n";
//			sceneFileContents = sceneFileContents + "  name default-shader\n";
//			sceneFileContents = sceneFileContents + "  type diffuse\n";
//			sceneFileContents = sceneFileContents + "  diff 0.25 0.25 0.25\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//			
//			sceneFileContents = sceneFileContents + "object {\n";
//			sceneFileContents = sceneFileContents + "  shader default-shader\n";
//			sceneFileContents = sceneFileContents + "  type plane";
//			sceneFileContents = sceneFileContents + "  p 0 0 0\n";
//			sceneFileContents = sceneFileContents + "  n 0 0 1\n";
//			sceneFileContents = sceneFileContents + "}\n\n";
//
//			sceneFileContents = sceneFileContents + "gi {\n";
//			sceneFileContents = sceneFileContents + "  type path\n";
//			sceneFileContents = sceneFileContents + "  samples 4\n";
//			sceneFileContents = sceneFileContents + "}\n\n";


			UI.printInfo(Module.GEOM, "OBJ - Reading geometry: \"%s\" ...", inputFilename);


			//	Parse the obj file. 
			FileReader file = new FileReader(inputFilename);
			BufferedReader bf = new BufferedReader(file);
			String line;
			int currentMaterialNumber = -1;
			String currentMaterialFileName = "";
			while ((line = bf.readLine()) != null) {
				if (line.startsWith("mtllib")) {
					String[] matFileName = line.split("\\s+");
					currentMaterialFileName = matFileName[1];
				} else if (line.startsWith("o ")) {
					String[] name = line.split("\\s+");
					if (!currentObject.name.equals("")) {
						objects.add(currentObject);
						currentObject = new ImportedOBJ(name[1]);
						currentMaterialNumber = -1;
					} else {
						currentObject = new ImportedOBJ(name[1]);
						currentObject.shaders = parseMaterialFile(currentMaterialFileName);
						currentMaterialNumber = -1;
					}
				} else if (line.startsWith("v ")) {
					String[] v = line.split("\\s+");
					String pointString = Float.parseFloat(v[1]) + " " + Float.parseFloat(v[2]) + " " + Float.parseFloat(v[3]);
					currentObject.points.add(pointString);
				} else if (line.startsWith("vt ")) {
					String[] vt = line.split("\\s+");
					currentObject.uvUs.add(Float.parseFloat(vt[1]));
					currentObject.uvVs.add(Float.parseFloat(vt[2]));
				}  else if (line.startsWith("vn ")) {
					String[] vn = line.split("\\s+");
					currentObject.vnXs.add((int)Float.parseFloat(vn[1]));
					currentObject.vnYs.add((int)Float.parseFloat(vn[2]));
					currentObject.vnZs.add((int)Float.parseFloat(vn[3]));
				} else if (line.startsWith("usemtl ")) {
					currentMaterialNumber++;
				} else if (line.startsWith("f ")) {
					String[] f = line.split("\\s+");
					currentObject.faceShaders.add(currentMaterialNumber);
					
					//	NOTE: All OBJ files MUST have UVs AND Normals!!!
					
					if (f[1].contains("/")) {
						String[] f1 = f[1].split("/");
						String[] f2 = f[2].split("/");
						String[] f3 = f[3].split("/");
						String triangleString = (Integer.parseInt(f1[0]) - 1) + " " +
								(Integer.parseInt(f2[0]) - 1) + " " +
								(Integer.parseInt(f3[0]) - 1);
						currentObject.triangles.add(triangleString);
						BlendUV newUV = new BlendUV(Integer.parseInt(f1[1]) - 1, Integer.parseInt(f2[1]) - 1, Integer.parseInt(f3[1]) - 1);
						BlendNormal newNormal = new BlendNormal(Integer.parseInt(f1[2]) - 1, Integer.parseInt(f2[2]) - 1, Integer.parseInt(f3[2]) - 1);
						currentObject.normals.add(newNormal);
						currentObject.uvs.add(newUV);

					} else {
						String triangleString = (Integer.parseInt(f[1]) - 1) + " " +
								(Integer.parseInt(f[2]) - 1) + " " +
								(Integer.parseInt(f[3]) - 1);
						currentObject.triangles.add(triangleString);
					}
				}
				if (lineNumber % 100000 == 0)
					UI.printInfo(Module.GEOM, "OBJ -   * Parsed %7d lines ...", lineNumber);
				lineNumber++;
			}

			objects.add(currentObject);
			UI.printInfo(Module.GEOM, "OBJ - Finished reading file, %d objects found ...", objects.size());
			int numShaders = 0;
			for (ImportedOBJ nextObject: objects) {
				for (ImportedModelShader nextShader: nextObject.shaders) {
					sceneFileContents = sceneFileContents + nextShader.toString();
					numShaders++;
				}
			} 
			UI.printInfo(Module.GEOM, "OBJ - %d shaders written to file ...", numShaders);
			
			for (ImportedOBJ nextObject: objects) {
				UI.printInfo(Module.GEOM, "OBJ - Writing \"%s\" to scene ...", nextObject.name);
				sceneFileContents = sceneFileContents + nextObject.toString();
				UI.printInfo(Module.GEOM, "OBJ - Finished writing \"%s\" to scene ...", nextObject.name);
			}UI.printInfo(Module.GEOM, "OBJ - Finished writing to scene ...");
			bufferWriter.write(sceneFileContents);
			bufferWriter.close();
			System.out.println(sceneFileContents);
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

	//	Parses a material file into shaders.
	private static ArrayList<ImportedModelShader> parseMaterialFile(String fileName) {

		//	In the end, these must ALL have the same size.
		ArrayList<ImportedModelShader> modelShaders = new ArrayList<>();
		ImportedModelShader currentShader = new ImportedModelShader("", "");
		String ambientColor = "";
		String diffuseColor = ""; 
		//		boolean isFirstMaterial = true;
		try {
			FileReader file = new FileReader(fileName);
			BufferedReader bf = new BufferedReader(file);
			String line;
			while ((line = bf.readLine()) != null) {
				if (line.startsWith("newmtl")) {
					String[] shaderName = line.split("\\s+");
					String newShaderName = shaderName[1] + ".shader";
					if (!currentShader.name.equals("")) {
						modelShaders.add(currentShader);
						currentShader = new ImportedModelShader(newShaderName, fileName);
					} else {
						currentShader = new ImportedModelShader(newShaderName, fileName);
					}
				} else if (line.startsWith("Ka")) {
					String[] ambientCols = line.split("\\s+");
					ambientColor = "{\"sRGB linear\" " + ambientCols[1] + " " + ambientCols[2] + " " + ambientCols[3] + " }";
				} else if (line.startsWith("Kd")) {
					String[] diffuse = line.split("\\s+");
					diffuseColor = "{\"sRGB linear\" " + diffuse[1] + " " + diffuse[2] + " " + diffuse[3] + " }";
				} else if (line.startsWith("map_Kd")) {
					String[] diffuseTextureName = line.split("\\s+");
					currentShader.texture = diffuseTextureName[1];
				} else if (line.startsWith("illum")) {
					String[] illumType = line.split("\\s+");
					int illum = (int)Integer.parseInt(illumType[1]);
					if (illum == 2) {
						currentShader.color = ambientColor;
						currentShader.type = "constant";
					} else if (illum == 3) {
						currentShader.color = diffuseColor;
						currentShader.type = "shiny";
					} else {
						currentShader.color = diffuseColor;
						currentShader.type = "diffuse";
					}
				}

				//	Make sure ALL ArrayLists have the same number of elements by putting in empty elements.
				//	This will help with

			}
			modelShaders.add(currentShader);
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			UI.printError(Module.GEOM, "Unable to read mesh file \"%s\" - file not found", fileName);
		} catch (IOException e) {
			e.printStackTrace();
			UI.printError(Module.GEOM, "Unable to read mesh file \"%s\" - I/O error occured", fileName);
		}
		return modelShaders;
	}
}

class BlendNormal {
	int x, y, z;

	BlendNormal(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String getNormalString(FloatArray vnXs, FloatArray vnYs, FloatArray vnZs) {
		return vnXs.get(x) + " " + vnYs.get(x) + " " + vnZs.get(x) + " " +
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

	public String getUVString(FloatArray uvUs, FloatArray uvVs) {
		return uvUs.get(a) + " " + uvVs.get(a) + " " +
				uvUs.get(b) + " " + uvVs.get(b) + " " +
				uvUs.get(c) + " " + uvVs.get(c);
	}
}
