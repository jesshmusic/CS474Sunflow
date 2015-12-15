image {
  resolution 1280 720
  aa 0 2
  samples 8
  filter mitchell
}

camera {
  type thinlens
  eye    25 25 15
  target -12 -25 10
  up     0 0 1
  fov    60
  aspect 1.7777778
  fdist  66
  lensr  0.2
}

trace-depths {
  diff 4
  refl 4
  refr 4
}

gi {
	type irr-cache 
	samples 512 
	tolerance 0.01 
	spacing 0.05 5.0
}

/* Cassandra Shaders */

shader {
	name Skin.shader
	type sss
    diff {"sRGB linear" 0.800000 0.25 0.25 }
	text CassandraUV_Contrasted.png
	alphaMap Cassandra_Alpha.png
	samp 16
	refl 0.0
	attn 0.25
	tpow 0.01
	tfoc 0.1
	percentDiffuse 0
}

shader {
	name BrainMat.shader
	type diffuse
	diff {"sRGB linear" 0.8 0.65 0.7 }
}

/* Rack Shaders */

shader {
	name Rack_Metal.shader
	type ward
	diff {"sRGB nonlinear" 0.2 0.2 0.2 }
	spec {"sRGB nonlinear" 0.8 0.8 0.8 }
	rough .07 .1
	samples 4
}

shader {
	name Rack_Metal_Black.shader
	type diffuse
	diff {"sRGB nonlinear" 0.000000 0.000000 0.000000 }
}



shader {
	name Brain_Jar.shader
	type glass
	eta 1.1
	color 0.5 0.5 2.0
	absorbtion.distance 50.0
	absorbtion.color { "sRGB nonlinear" 1.0 1.0 1.0 }
}


shader {
	name BrainJarLight.shader
	type constant
	color {"sRGB linear" 0.6 0.6 5.0 }
}


/* Room Light Shaders */

shader {
	name Ceiling_Lights.shader
	type constant
	texture RoomTexture.jpg
	brightness 0.2
}

shader {
	name Front_Lights.shader
	type constant
	texture RoomTexture.jpg
	brightness 100.0
}

shader {
	name Left_Lights.shader
	type constant
	texture RoomTexture.jpg
	brightness 15.0
}

shader {
	name Right_Lights.shader
	type constant
	texture RoomTexture.jpg
	brightness 15.0
}


/* Window background shader */ 

shader {
	name Starry_Sky.shader
	type constant
	texture PlanetAndStars.jpg
	brightness 3.0
}




/* Tardis Shaders */

shader {
	name Light.shader
	type constant
	texture "web_TARDIS_DIFF.jpg"
	brightness 10.0
}

shader {
	name TardisBlue.shader
	type diffuse
	texture "web_TARDIS_DIFF.jpg"
}

shader {
	name TardisBlue_web_TARDIS_DIFF.jpg.shader
	type diffuse
	texture "web_TARDIS_DIFF.jpg"
}

shader {
	name TardisWindows.shader
	type constant
	texture "web_TARDIS_DIFF.jpg"
	brightness 5.0
}

shader {
	name TardisPolicBoxSign.shader
	type constant
	texture "web_TARDIS_DIFF.jpg"
	brightness 3.0
}


/* Objects */
include Room.sc
include WindowBackground.sc
include Tardis_FINAL.sc
include Cassandra_FINAL.sc