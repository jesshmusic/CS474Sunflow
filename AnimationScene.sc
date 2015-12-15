image {
  resolution 854 480
  aa 0 0
  samples 4
  filter gaussian
  jitter true
}

camera {
  type thinlens
  eye    10 20 25
  target -10 -25 15
  up     0 0 1
  fov    35
  aspect 1.7777778
  fdist  60
  lensr  0.75
  sides 6
  rotation 36.0
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

include Room.sc
include WindowBackground.sc
include Tardis.sc
include Cassandra_FULL_new.sc




