package com.mbesida.synchronizer.cake

import com.typesafe.config.ConfigFactory


trait ConfigProvider {
  implicit def conf = ConfigProvider.config
}

object ConfigProvider {
  lazy val config = ConfigFactory.load()
}
