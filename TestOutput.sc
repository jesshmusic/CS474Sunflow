image {
  resolution 640 360
  aa 0 2
  samples 4
  filter gaussian
}

camera {
  type thinlens
  eye    10 20 25
  target -10 -25 15
  up     0 0 1
  fov    55
  aspect 1.7777778
  fdist  60
  lensr  0.4
  sides 6
  rotation 36.0
}
/*
camera {
  type pinhole
  eye    0 20 5
  target -13 -40 15
  up     0 0 1
  fov    35
  aspect 1.333333
}
*/

trace-depths {
  diff 1
  refl 12
  refr 12
}

gi {
	type irr-cache
	samples 512
	tolerance 0.01
	spacing 0.05 5.0
}

/*
gi {
  type path
  samples 16
}

light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 5000.0
   p -19 -42 15
}
*/


include Room.sc
include WindowBackground.sc
include Tardis.sc
include Cassandra_FULL_new.sc




