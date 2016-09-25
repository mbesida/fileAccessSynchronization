package com.mbesida.synchronizer

import javax.xml.bind.annotation.{XmlAccessType, XmlAccessorType, XmlRootElement}

package object rest {
  @XmlRootElement(name = "input")
  @XmlAccessorType(XmlAccessType.FIELD)
  case class InputData(v2: Double, v3: Int, v4: Int) {
    private def this() = this(0,0,0)
  }

  @XmlRootElement(name ="getResult")
  @XmlAccessorType(XmlAccessType.FIELD)
  case class GetResult(value: Double) {
    private def this() = this(0) //jaxb requires no-arg contructor
  }

  @XmlRootElement(name ="getResult")
  @XmlAccessorType(XmlAccessType.FIELD)
  case class PostResult(value: Int) {
    private def this() = this(0) //jaxb requires no-arg contructor
  }
}
