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

light {
  type ibl
  image "checker.hdr"
  center 0 -1 0
  up 0 0 1
  lock true
  samples 200
}

camera {
  type thinlens
  eye    -15 16 -50
  target -2 6 0
  up     0 1 0
  fov 15
  aspect 1
  fdist 55
  lensr 0.2
}

shader {
  name ground.shader
  type diffuse
  texture "grid.png"
}

shader {
	name signText.shader
	type diffuse
	texture "Tardis-Police-Public-Call-Box-Text.jpg"
}

object {
  shader ground.shader
  type generic-mesh
  points 4
	-30 0 -30
	 30 0 -30
	 30 0  30
	-30 0  30
  triangles 2
	0 2 1
	0 3 2
  normals vertex
	0 1 0
	0 1 0
	0 1 0
	0 1 0
  uvs vertex
	0 0
	-5 0
	-5 5
	0 5
}

object {
	shader signText.shader
	type generic-mesh
	name Police_Box_Black_Sign_Cube.004
	points 24
	  -2.925255 10.525215 3.42467
	  -2.925255 11.12014 3.42467
	  -2.925255 10.525215 -3.421554
	  -2.925255 11.12014 -3.421554
	  2.890151 10.525215 3.42467
	  2.890151 11.12014 3.42467
	  2.890151 10.525215 -3.421554
	  2.890151 11.12014 -3.421554
	  -2.925255 11.12014 2.927019
	  -2.925255 10.525215 2.92702
	  2.890151 11.12014 2.92702
	  2.890151 10.525215 2.927019
	  -2.925255 11.12014 -2.884392
	  2.890151 10.525215 -2.898556
	  -2.925255 10.525215 -2.898556
	  2.890151 11.12014 -2.884392
	  -3.389545 10.525215 -2.898556
	  -3.389545 10.525215 2.92702
	  3.326092 11.12014 -2.884392
	  3.326092 11.12014 2.92702
	  3.326092 10.525215 2.927019
	  -3.389545 11.12014 2.927019
	  -3.389545 11.12014 -2.884392
	  3.326092 10.525215 -2.898556
	triangles 44
	  3 2 14
	  7 6 2
	  10 5 4
	  1 0 4
	  2 6 13
	  7 3 12
	  14 16 22
	  9 11 4
	  8 9 0
	  10 8 1
	  14 13 11
	  7 15 13
	  13 23 20
	  10 15 12
	  18 19 20
	  22 16 17
	  9 17 16
	  12 22 21
	  8 21 17
	  11 20 19
	  15 18 23
	  10 19 18
	  12 3 14
	  3 7 2
	  11 10 4
	  5 1 4
	  14 2 13
	  15 7 12
	  12 14 22
	  0 9 4
	  1 8 0
	  5 10 1
	  9 14 11
	  6 7 13
	  11 13 20
	  8 10 12
	  23 18 20
	  21 22 17
	  14 9 16
	  8 12 21
	  9 8 17
	  10 11 19
	  13 15 23
	  15 10 18
	normals facevarying
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 0 -1 0 0 -1 0 0 -1
	  	1 0 0 1 0 0 1 0 0
	  	0 0 1 0 0 1 0 0 1
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	0 0 0 0 0 0 0 0 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 1 0 0 1 0 0 1 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	1 0 0 1 0 0 1 0 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	1 0 0 1 0 0 1 0 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	0 0 1 0 0 1 0 0 1
	  	0 0 1 0 0 1 0 0 1
	  	0 0 0 0 0 0 0 0 0
	  	0 1 0 0 1 0 0 1 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 0 -1 0 0 -1 0 0 -1
	  	1 0 0 1 0 0 1 0 0
	  	0 0 1 0 0 1 0 0 1
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	0 0 0 0 0 0 0 0 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 1 0 0 1 0 0 1 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	1 0 0 1 0 0 1 0 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	1 0 0 1 0 0 1 0 0
	  	-1 0 0 -1 0 0 -1 0 0
	  	0 -1 0 0 -1 0 0 -1 0
	  	0 1 0 0 1 0 0 1 0
	  	0 0 1 0 0 1 0 0 1
	  	0 0 1 0 0 1 0 0 1
	  	0 0 0 0 0 0 0 0 0
	  	0 1 0 0 1 0 0 1 0
	uvs facevarying
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	1 1 0 1 0 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	1 1 0 1 0 0
	  	0 1 0 0 1 0
	  	1 1 0 1 0 0
	  	1 1 0 1 0 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	0 1 0 0 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 0 1 1 0 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 0 1 1 0 0
	  	1 1 0 1 1 0
	  	1 0 1 1 0 0
	  	1 0 1 1 0 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
	  	1 1 0 1 1 0
}

shader {
	name TARDISWindow.shader
	type constant
	color {"sRGB nonlinear" 2.0 1.0 0.5 }
}

shader {
	name TARDISLight.shader
	type constant
	color {"sRGB nonlinear" 1.0 1.0 2.0 }
}

shader {
	name TARDISBlue.shader
	type diffuse
	diff 0.015 0.075 0.25
}

shader {
	name TARDISWhiteSign.shader
	type diffuse
	texture "WhiteSign.jpg"
}

object {
	shader TARDISWhiteSign.shader
	transform {
		translate 0.0 -12.75 -4.4
		rotatez 180.0
	}
	type generic-mesh
	name Police_Box_White_Sign_Cube.003
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
	uvs facevarying
	  	1 0 1 1 0 1
	  	1 0 1 1 0 1
	  	1 0 1 1 0 1
	  	0 0 0 0 1 0
	  	1 0 1 1 0 1
	  	1 0 1 1 0 1
	  	0 0 1 0 0 1
	  	0 0 1 0 0 1
	  	0 0 1 0 0 1
	  	1 0 0 0 1 0
	  	0 0 1 0 0 1
	  	0 0 1 0 0 1
}

object {
	shader TARDISBlue.shader
	transform {
		rotatex -90
		rotatey 180
	}
	type file-mesh
	name TARDIS
	filename TARDIS.obj
	smooth_normals false
}

object {
	shader TARDISBlue.shader
	transform {
		rotatex -90
		rotatey 180
	}
	type file-mesh
	name TARDISDoors
	filename TARDISDoors.obj
	smooth_normals false
}

object {
	shader TARDISBlue.shader
	transform {
		rotatex -90
		rotatey 180
	}
	type file-mesh
	name TARDISTopper
	filename TARDIS-topper.obj
	smooth_normals true
}

object {
	shader TARDISWindow.shader
	transform {
		translate 0.11 0.0 0.0
		rotatex -90
		rotatey 180
	}
	type file-mesh
	name TARDIS_window_plane
	filename TARDIS_window_plane.obj
	smooth_normals false
}

object {
	shader TARDISLight.shader
	transform {
		rotatex -90
		rotatey 180
	}
	type file-mesh
	name TARDIS_Light
	filename TARDIS-light.obj
	smooth_normals true
}