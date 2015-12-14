image {
  resolution 854 480
  aa 0 2
  samples 4
  filter gaussian
}

camera {
  type thinlens
  eye    10 35 15
  target -10 -35 12
  up     0 0 1
  fov    30
  aspect 1.77777
  fdist  60
  lensr  0.2
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


light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 5000.0
   p -20 -38 15
}


light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 5000.0
   p -15 -25 10
}

/*
light {
  type ibl
  image RoomHDR.hdr
  center 0 -1 0
  up 0 0 1
  lock false
  samples 16
}
*/


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

include Tardis.sc
include Cassandra_FULL_new.sc





