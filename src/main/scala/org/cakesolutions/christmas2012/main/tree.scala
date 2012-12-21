package org.cakesolutions.christmas2012.main

sealed abstract class Tree(val left: Tree) {
  def o = new Ball(this)
  def x = new Spike(this)
  def * = new Candle(this)
  def oxo = new BigBall(this)
  def oo = new DoubleBall(this)
  def *** = new ElectricCandle(this)

  def / = new LeftNeedle(this)
  def \ = new RightNeedle(this)
  def | = new Trunk(this)
}
class Top(star: Star) extends Tree(star)
abstract class Needle(left: Tree) extends Tree(left)
class LeftNeedle(left: Tree) extends Needle(left)
class RightNeedle(left: Tree) extends Needle(left)
class Trunk(parent: Tree) extends Tree(parent)

abstract class Decoration(left: Tree) extends Tree(left)
class Star extends Decoration(null)
class Candle(left: Tree) extends Decoration(left)
class Ball(left: Tree) extends Decoration(left)
class Spike(left: Tree) extends Decoration(left)
class BigBall(left: Tree) extends Decoration(left)
class DoubleBall(left: Tree) extends Decoration(left)
class ElectricCandle(left: Tree) extends Decoration(left)

trait DecorationBuilder {
  def \-/ = new PartialDecoration

  class PartialDecoration {
    def -->*<-- = new Star
  }
}
