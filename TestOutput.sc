image {
  resolution 640 480
  aa 0 1
  samples 4
  filter mitchell
}

camera {
  type pinhole
  eye    15 -20 10
  target 0 0 5
  up     0 0 1
  fov    60
  aspect 1.333333
}

light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 1000.0
   p 1 5 10
}

light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 1000.0
   p 1 5 3
}

trace-depths {
  diff 2
  refl 4
  refr 4
}

shader {
  name default-shader
  type diffuse
  diff 0.25 0.25 0.25
}

object {
  shader default-shader
  type plane  p 0 0 0
  n 0 0 1
}


shader {
	name Material.shader
	type shiny
	diff {"sRGB linear" 1.000000 0.000000 0.000000 }
	refl 1.0
}

shader {
	name Material.001.shader
	type diffuse
	diff {"sRGB linear" 0.000000 1.000000 0.000000 }
}

shader {
	name Material.002.shader
	type constant
	color {"sRGB linear" 0.000000 0.000000 1.000000 }
}

object {
	shaders 3
	  Material.shader
	  Material.001.shader
	  Material.002.shader
	transform {
		translate 0.0 2.0 2.0
	}
	type generic-mesh
	name Cube
	points 8
		1.0 -1.0 -1.0
		1.0 -1.0 1.0
		-1.0 -1.0 1.0
		-1.0 -1.0 -1.0
		1.0 1.0 -0.999999
		0.999999 1.0 1.000001
		-1.0 1.0 1.0
		-1.0 1.0 -1.0
	triangles 12
		 4 5 1
		 2 6 7
		 0 4 1
		 3 2 7
		 5 6 2
		 0 3 7
		 1 5 2
		 4 0 7
		 1 2 3
		 7 6 5
		 0 1 3
		 4 7 5
	normals facevarying
		 1 0 0 1 0 0 1 0 0
		 -1 0 0 -1 0 0 -1 0 0
		 1 0 0 1 0 0 1 0 0
		 -1 0 0 -1 0 0 -1 0 0
		 0 0 1 0 0 1 0 0 1
		 0 0 -1 0 0 -1 0 0 -1
		 0 0 1 0 0 1 0 0 1
		 0 0 -1 0 0 -1 0 0 -1
		 0 -1 0 0 -1 0 0 -1 0
		 0 1 0 0 1 0 0 1 0
		 0 -1 0 0 -1 0 0 -1 0
		 0 1 0 0 1 0 0 1 0
	uvs facevarying
		 1 0 1 1 0 1
		 0 0 1 0 1 1
		 0 0 1 0 0 1
		 0 1 0 0 1 1
		 1 0 1 1 0 1
		 1 0 1 1 0 1
		 0 0 1 0 0 1
		 0 0 1 0 0 1
		 1 0 1 1 0 1
		 1 0 1 1 0 1
		 0 0 1 0 0 1
		 0 0 1 0 0 1
	face_shaders
		 0
		 0
		 0
		 0
		 1
		 1
		 1
		 1
		 2
		 2
		 2
		 2
}
