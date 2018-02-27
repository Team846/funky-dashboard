package com.lynbrookrobotics

import slinky.core.StatelessComponent
import slinky.core.annotations.react
import slinky.core.facade.{React, ReactElement}
import slinky.web.ReactDOM
import org.scalajs.dom

import scala.scalajs.js

package object mdl {
  private def upgrade(top: dom.Element) = {
    js.Dynamic.global.componentHandler.upgradeElement(top)
  }

  @react class MaterialComponent extends StatelessComponent {
    type Props = ReactElement

    override def componentDidMount(): Unit = {
      upgrade(ReactDOM.findDOMNode(this))
    }

    override def componentDidUpdate(prevProps: ReactElement, prevState: Unit): Unit = {
      upgrade(ReactDOM.findDOMNode(this))
    }

    override def render(): ReactElement = {
      props
    }
  }

  implicit class MaterialAble[T <% ReactElement](val elem: T) {
    def material: ReactElement = {
      MaterialComponent(elem)
    }
  }
}
