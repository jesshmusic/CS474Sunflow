package org.OBJImporter;

public class ImportedModelShader {
	String fileName;
	String name;
	String type;
	String color;
	String texture;

	public ImportedModelShader(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
		this.type = "";
		this.color = "";
		this.texture = "";
	}

	public String toString() {
		String returnString = "\nshader {\n";
		if (this.type.equals("constant")) {
			returnString = returnString 
					+ "\tname " + this.name + "\n"
					+ "\ttype " + this.type + "\n";
			returnString = returnString + "\tcolor " + this.color + "\n";
			returnString = returnString + "}\n";
		} else if (this.type.equals("shiny")) {
			returnString = returnString 
					+ "\tname " + this.name + "\n"
					+ "\ttype " + this.type + "\n";

			if (!this.texture.equals("")) {
				returnString = returnString + "\ttexture \"" + this.texture + "\"\n";
			} else {
				returnString = returnString + "\tdiff " + this.color + "\n";
			}
			returnString = returnString + "\trefl 1.0\n";
			returnString = returnString + "}\n";
		}  else {
			returnString = returnString 
					+ "\tname " + this.name + "\n"
					+ "\ttype " + this.type + "\n";

			if (!this.texture.equals("")) {
				returnString = returnString + "\ttexture \"" + this.texture + "\"\n";
			} else {
				returnString = returnString + "\tdiff " + this.color + "\n";
			}
			returnString = returnString + "}\n";
		}
		return returnString;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImportedModelShader)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		ImportedModelShader rhs = (ImportedModelShader) obj;
		return (this.fileName.equals(rhs.fileName) && this.name.equals(rhs.name));
	}
}
