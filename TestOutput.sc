image {
  resolution 600 600
  aa 0 1
  samples 2
  filter gaussian
}

trace-depths {
  diff 1
  refl 4
  refr 4
}

gi {
  type path
  samples 4
}

camera {
  type pinhole
  eye    -5 -5 5
  target 0 0 0
  up     0 0 1
  fov    60
  aspect 1.333333
}

light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 5000.0
   p 1 4 3
}



object {
	shader default
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
	triangles 6
	  0 4 5
	  2 6 7
	  1 5 6
	  4 0 3
	  0 1 2
	  4 7 6
	normals facevarying
	  	1 0 0 1 0 0 1 0 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 0 1 0 0 1 0 0 1
	  	0 0 -1 0 0 -1 0 0 -1
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	uvs facevarying
	  	0 0 1 0 1 1
	  	0 0 1 0 1 1
	  	0 0 1 0 1 1
	  	0 0 1 0 1 1
	  	0 0 1 0 1 1
	  	0 0 1 0 1 1
}