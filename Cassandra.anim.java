import org.sunflow.SunflowAPI;
import org.sunflow.core.*;
import org.sunflow.core.camera.*;
import org.sunflow.core.primitive.*;
import org.sunflow.core.shader.*;
import org.sunflow.image.Color;
import org.sunflow.math.*;
import java.io.*;

public void build() {
	int currentFrame = getCurrentFrame();
	try {
		FileOutputStream fos = new FileOutputStream("Cassandra.setting.sc");
		PrintWriter out = new PrintWriter(fos);
		Vector3 cameraLoc = new Vector3(10.0f, (80.0f - currentFrame / 2.0f ), 25.0f);
		Vector3 eyeLoc = new Vector3(-10f, -25f, 15f);
		float fDist = Math.sqrt((cameraLoc.x * eyeLoc.x) + (cameraLoc.y * eyeLoc.y) + (cameraLoc.z * eyeLoc.z));
		out.println("camera {");
		out.println("type thinlens");
		out.println("eye " + cameraLoc.x + " " + cameraLoc.y + " " + cameraLoc.z);
		out.println("target " + eyeLoc.x + " " + eyeLoc.y + " " + eyeLoc.z);
		out.println("up     0 0 1");
		out.println("fov 35");
		out.println("aspect 1.7777778");
		out.println("fdist " + fDist);
		out.println("lensr  0.75");
		out.println("}");
		out.flush();
		out.close();
	} catch(Exception e) {
	}

	include("Cassandra.setting.sc");
	include("AnimationScene.sc");
}