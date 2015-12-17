import org.sunflow.SunflowAPI;
import org.sunflow.core.*;
import org.sunflow.core.camera.*;
import org.sunflow.core.primitive.*;
import org.sunflow.core.shader.*;
import org.sunflow.image.Color;
import org.sunflow.math.*;
import java.io.*;

public void build() {
	int currentFrame = currentFrame();
	try {
		FileOutputStream fos = new FileOutputStream("Cassandra.setting.sc");
		PrintWriter out = new PrintWriter(fos);
		Vector3 cameraLoc = new Vector3((20.0f - (currentFrame / 8.0f)), (60.0f - (currentFrame / 6.0f)), (5.0f + (currentFrame / 8.0f)));
		Vector3 eyeLoc = new Vector3(-10f, -25f, 15f);
		double fDist = Math.sqrt(Math.pow((cameraLoc.x - eyeLoc.x), 2) + Math.pow((cameraLoc.y - eyeLoc.y), 2) + Math.pow((cameraLoc.z - eyeLoc.z), 2));
		out.println("camera {");
		out.println("type thinlens");
		out.println("eye " + cameraLoc.x + " " + cameraLoc.y + " " + cameraLoc.z);
		out.println("target " + eyeLoc.x + " " + eyeLoc.y + " " + eyeLoc.z);
		out.println("up     0 0 1");
		out.println("fov " + (60.0f - (currentFrame / 4.0)));
		out.println("aspect 1.7777778");
		out.println("fdist " + fDist);
		out.println("lensr  0.75");
		out.println("}");

		out.println("shader {");
		out.println("name Ceiling_Lights.shader");
		out.println("type constant");
		out.println("texture RoomTexture.jpg");
		out.println("brightness " + ((float)currentFrame / 2.0f));
		out.println("}");
		
		out.flush();
		out.close();

		System.out.println("camera {");
		System.out.println("type thinlens");
		System.out.println("eye " + cameraLoc.x + " " + cameraLoc.y + " " + cameraLoc.z);
		System.out.println("target " + eyeLoc.x + " " + eyeLoc.y + " " + eyeLoc.z);
		System.out.println("up     0 0 1");
		System.out.println("fov " + (60.0f - (currentFrame / 4.0)));
		System.out.println("aspect 1.7777778");
		System.out.println("fdist " + fDist);
		System.out.println("lensr  0.75");
		System.out.println("}");

		System.out.println("shader {");
		System.out.println("name Ceiling_Lights.shader");
		System.out.println("type constant");
		System.out.println("texture RoomTexture.jpg");
		System.out.println("brightness " + ((float)currentFrame / 2.0f));
		System.out.println("}");
	} catch(Exception e) {
	}

	include("Cassandra.setting.sc");
	include("AnimationScene.sc");
}