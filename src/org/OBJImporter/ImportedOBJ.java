package org.OBJImporter;

import java.util.ArrayList;

import org.sunflow.util.FloatArray;

public class ImportedOBJ {
	ArrayList<String> shaders = new ArrayList<>();
	String name;
	ArrayList<String> points = new ArrayList<>();
	ArrayList<String> triangles = new ArrayList<>();
	FloatArray vnXs = new FloatArray();
	FloatArray vnYs = new FloatArray();
	FloatArray vnZs = new FloatArray();
	ArrayList<BlendNormal> normals = new ArrayList<BlendNormal>();
	FloatArray uvUs = new FloatArray();
	FloatArray uvVs = new FloatArray();
	ArrayList<BlendUV> uvs = new ArrayList<BlendUV>();
	ArrayList<Integer> faceShaders = new ArrayList<>();
	
	public ImportedOBJ(String name) {
		this.name = name;
	}

	public String toString() {
		String returnString = "\nobject {\n";
		if (shaders.size() > 1) {
			returnString = returnString + "\tshaders " + shaders.size() + "\n";
//			for (int i = shaders.size() - 1; i > -1; i--) {
//				returnString = returnString + "\t  " + shaders.get(i).name + "\n";
//			}
			for (String nextShader: shaders) {
				returnString = returnString + "\t  " + nextShader + "\n";
			}
		} else if (!shaders.isEmpty()) {
			returnString = returnString + "\tshader " + shaders.get(0) + "\n";
		}
		returnString = returnString + "\ttype generic-mesh\n";
		returnString = returnString + "\tname " + this.name + "\n";
		returnString = returnString + "\tpoints " + this.points.size() + "\n";
		for (String point: points) {
			returnString = returnString + "\t\t" + point + "\n";
		}
		returnString = returnString + "\ttriangles " + this.triangles.size() + "\n";
		for (String triangle: triangles) {
			returnString = returnString + "\t\t " + triangle + "\n";
		}
		returnString = returnString + "\tnormals none\n";		// Let Sunflow calculate the normals
//		for (BlendNormal blendNormal: normals) {
//			returnString = returnString + "\t\t " + blendNormal.getNormalString(vnXs, vnYs, vnZs) + "\n";
//		}
		returnString = returnString + "\tuvs facevarying\n";
		for (BlendUV blendUV: uvs) {
			returnString = returnString + "\t\t " + blendUV.getUVString(uvUs, uvVs) + "\n";
		}
		if (shaders.size() > 1) {
			returnString = returnString + "\tface_shaders\n";
			for (Integer faceShader: faceShaders) {
				returnString = returnString + "\t\t " + faceShader.intValue() + "\n";
			}
		}
		returnString = returnString + "}\n";
		return returnString;
	}
}
