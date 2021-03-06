/*
 * Licensed to Intel Corporation under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Intel Corporation licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intel.analytics.bigdl.torch

import com.intel.analytics.bigdl.nn.Euclidean
import com.intel.analytics.bigdl.tensor.Tensor
import com.intel.analytics.bigdl.utils.RandomGenerator._
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.util.Random

@com.intel.analytics.bigdl.tags.Serial
class EuclideanSpec extends FlatSpec with BeforeAndAfter with Matchers{
  before {
    if (!TH.hasTorch()) {
      cancel("Torch is not installed")
    }
  }

  "A Euclidean " should "generate correct output and grad with input one dimension" in {
    val seed = 100
    RNG.setSeed(seed)

    val input = Tensor[Double](7).apply1(e => Random.nextDouble())
    val gradOutput = Tensor[Double](7).apply1(e => Random.nextDouble())

    val code = "torch.manualSeed(" + seed + ")\n" +
      "module = nn.Euclidean(7, 7)\n" +
      "weight = module.weight\n" +
      "output = module:forward(input)\n" +
      "module:zeroGradParameters()\n" +
      "gradInput = module:backward(input,gradOutput)\n" +
      "gradWeight = module.gradWeight\n" +
      "_repeat2 = module._repeat2\n"

    val (luaTime, torchResult) = TH.run(code, Map("input" -> input, "gradOutput" -> gradOutput),
      Array("output", "gradInput", "weight", "gradWeight", "_repeat2"))

    val luaOutput1 = torchResult("output").asInstanceOf[Tensor[Double]]
    val luaOutput2 = torchResult("gradInput").asInstanceOf[Tensor[Double]]
    val luaWeight = torchResult("weight").asInstanceOf[Tensor[Double]]
    val luaGradWeight = torchResult("gradWeight").asInstanceOf[Tensor[Double]]

    val module = new Euclidean[Double](7, 7)
    val start = System.nanoTime()
    val output = module.forward(input)
    val gradInput = module.backward(input, gradOutput)
    val weight = module.weight
    val gradWeight = module.gradWeight
    val end = System.nanoTime()
    val scalaTime = end - start

    weight should be(luaWeight)
    output should be(luaOutput1)
    gradInput should be(luaOutput2)
    gradWeight should be(luaGradWeight)

    println("Test case : Euclidean, Torch : " + luaTime +
      " s, Scala : " + scalaTime / 1e9 + " s")
  }

  "A Euclidean " should "generate correct output and grad with input two dimensions" in {
    val seed = 100
    RNG.setSeed(seed)

    val input = Tensor[Double](8, 7).apply1(e => Random.nextDouble())
    val gradOutput = Tensor[Double](8, 7).apply1(e => Random.nextDouble())

    val code = "torch.manualSeed(" + seed + ")\n" +
      "module = nn.Euclidean(7, 7)\n" +
      "weight = module.weight\n" +
      "output = module:forward(input)\n" +
      "module:zeroGradParameters()\n" +
      "gradInput = module:backward(input,gradOutput)\n" +
      "gradWeight = module.gradWeight\n" +
      "_repeat2 = module._repeat2\n"

    val (luaTime, torchResult) = TH.run(code, Map("input" -> input, "gradOutput" -> gradOutput),
      Array("output", "gradInput", "weight", "gradWeight", "_repeat2"))

    val luaOutput1 = torchResult("output").asInstanceOf[Tensor[Double]]
    val luaOutput2 = torchResult("gradInput").asInstanceOf[Tensor[Double]]
    val luaWeight = torchResult("weight").asInstanceOf[Tensor[Double]]
    val luaGradWeight = torchResult("gradWeight").asInstanceOf[Tensor[Double]]

    val module = new Euclidean[Double](7, 7)
    val start = System.nanoTime()
    val output = module.forward(input)
    val gradInput = module.backward(input, gradOutput)
    val weight = module.weight
    val gradWeight = module.gradWeight
    val end = System.nanoTime()
    val scalaTime = end - start

    weight should be(luaWeight)
    output should be(luaOutput1)
    gradInput should be(luaOutput2)
    gradWeight should be(luaGradWeight)


    println("Test case : Euclidean, Torch : " + luaTime +
      " s, Scala : " + scalaTime / 1e9 + " s")
  }
}
