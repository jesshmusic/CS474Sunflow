image {
  resolution 640 480
  aa 0 1
  samples 8
  filter gaussian
  jitter true
}

camera {
  type thinlens
  eye    10 20 25
  target -10 -25 15
  up     0 0 1
  fov    45
  aspect 1.3333333
  fdist  60
  lensr  0.5
  sides 6
  rotation 36.0
}

trace-depths {
  diff 2
  refl 4
  refr 4
}

gi {
  type path
  samples 32
}

include Room.sc
include WindowBackground.sc
include Tardis_FINAL.sc
include Cassandra_FINAL.sc




