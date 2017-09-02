package com.lynbrookrobotics

import me.shadaj.slinky.core.{Component, TagComponent}
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.core.facade.{React, ReactElement}
import me.shadaj.slinky.web.ReactDOM
import org.scalajs.dom

import scala.scalajs.js

package object mdl {
  private def upgrade(top: dom.Element) = {
    js.Dynamic.global.componentHandler.upgradeElement(top)
  }

  @react
  class MaterialComponent extends Component {
    type Props = ReactElement
    type State = Unit

    override def componentDidMount(): Unit = {
      upgrade(ReactDOM.findDOMNode(this.asInstanceOf[React.Component]))
    }

    override def componentDidUpdate(prevProps: ReactElement, prevState: Unit): Unit = {
      upgrade(ReactDOM.findDOMNode(this.asInstanceOf[React.Component]))
    }

    override def initialState: Unit = ()

    override def render(): ReactElement = {
      props
    }
  }

  implicit class MaterialAble(val elem: ReactElement) extends AnyVal {
    def material: ReactElement = {
      MaterialComponent(elem)
    }
  }

  implicit class MaterialAbleTag(val elem: TagComponent[_]) extends AnyVal {
    def material: ReactElement = {
      MaterialComponent(elem)
    }
  }
}
