/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.mango.util

import java.nio.file.Files

import com.google.common.io.Resources
import org.bdgenomics.utils.misc.SparkFunSuite
import org.scalatest.FunSuiteLike

import scala.io.Source

trait MangoFunSuite extends SparkFunSuite with FunSuiteLike {

  override val appName: String = "mango"
  override val properties: Map[String, String] = Map(
    ("spark.serializer", "org.apache.spark.serializer.KryoSerializer"),
    ("spark.kryo.registrator", "org.bdgenomics.mango.serialization.MangoKryoRegistrator"),
    ("spark.kryoserializer.buffer.max", "200m"),
    ("spark.kryo.referenceTracking", "true"))

  def resourcePath(path: String) = ClassLoader.getSystemClassLoader.getResource(path).getFile

  def globPath(path: String, suffix: String) = {
    val newPath = path.split("/").dropRight(1).mkString("/")
    newPath + s"/*${suffix}"
  }
}

