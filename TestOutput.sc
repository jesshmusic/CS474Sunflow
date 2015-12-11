image {
  resolution 400 300
  aa 0 2
  samples 1
  filter mitchell
}

camera {
  type pinhole
  eye    0 20 25
  target -7 0 20
  up     0 0 1
  fov    30
  aspect 1.333333
}

trace-depths {
  diff 1
  refl 4
  refr 4
}


gi {
  type path
  samples 64
}

/*
light {
   type point
   color { "sRGB nonlinear" 1.000 1.000 1.000 }
   power 5000.0
   p -19 -42 15
}
*/


include Room.sc
include WindowBackground.sc
/*include Tardis.sc*/
include Cassandra.sc
include Cassandra_Rack.sc




