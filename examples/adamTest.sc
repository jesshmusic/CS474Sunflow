image {
  resolution 640 480
  aa 0 1
  filter mitchell
}

camera {
  type pinhole
  eye    -10.5945 -30.0581 10.967
  target 0.0554193 0.00521195 5.38209
  up     0 0 1
  fov    60
  aspect 1.333333
}

light {
  type ibl
  image sky_small.hdr
  center 0 -1 0
  up 0 0 1
  lock true
  samples 200
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
  name Glass
  type glass
  eta 1.6
  color 1 1 1
}

shader {
  name Mirror
  type mirror
  refl 0.7 0.7 0.7
}

shader {
  name SSShader
  type sss
}


object {
  shader Mirror
  type sphere
  c -13 0 5
  r 3
}

object {
	shader SSShader
	transform {
		scale 5 5 5
		translate 6 -6 -3
	}
	type generic-mesh
	name cube
	sssampledensity 10.0
	points 8
	  -1.991474 5.65753 2.91525
	  -1.991474 7.077255 2.91525
	  -1.991474 5.65753 1.495525
	  -1.991474 7.077255 1.495525
	  -0.571748 5.65753 2.91525
	  -0.571748 7.077255 2.91525
	  -0.571748 5.65753 1.495525
	  -0.571748 7.077255 1.495525
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
