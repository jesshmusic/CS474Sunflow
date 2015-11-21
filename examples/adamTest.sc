image {
  resolution 640 480
  aa 0 1
  filter mitchell
}

camera {
  type pinhole
  eye    0 -30 10
  target 0 0 5
  up     0 0 1
  fov    60
  aspect 1.333333
}

/*
light {
  type ibl
  image sky_small.hdr
  center 0 -1 0
  up 0 0 1
  lock true
  samples 200
}
*/

light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 5000.0
   p 1 4 3
}

shader {
  name default-shader
  type diffuse
  diff 0.25 0.25 0.25
}

object {
  shader default-shader
  type plane
  p 0 0 0
  n 0 0 1
}

shader {
  name SSShader
  type sss
}

object {
	shader SSShader
	transform {
		scale 5 0.1 5
		translate 0 0 2.5
	}
	type generic-mesh
	name cube
	sssampledensity 100.0
	points 8
	  -1 -1 1
	  -1 1 1
	  -1 -1 -1
	  -1 1 -1
	  1 -1 1
	  1 1 1
	  1 -1 -1
	  1 1 -1
	triangles 12
	  3 2 0
	  7 6 2
	  5 4 6
	  1 0 4
	  2 6 4
	  7 3 1
	  1 3 0
	  3 7 2
	  7 5 6
	  5 1 4
	  0 2 4
	  5 7 1
	normals facevarying
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 0 -1 0 0 -1 0 0 -1
	  	1 0 0 1 0 0 1 0 0
	  	0 0 1 0 0 1 0 0 1
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 0 -1 0 0 -1 0 0 -1
	  	1 0 0 1 0 0 1 0 0
	  	0 0 1 0 0 1 0 0 1
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	uvs none
}
