image {
  resolution 1200 1200
  aa 0 2
  samples 4
  filter gaussian
}

camera {
  type spherical
  eye 0 0 5
  target 0 -10 5
  up 0 0 1
}

gi {
  type path
  samples 16
}

include Room.sc
include WindowBackground.sc




