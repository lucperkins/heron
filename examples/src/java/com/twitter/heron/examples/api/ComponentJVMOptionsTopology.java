//  Copyright 2017 Twitter. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.twitter.heron.examples.api;

import java.util.Map;

import com.twitter.heron.api.Config;
import com.twitter.heron.api.HeronSubmitter;
import com.twitter.heron.api.bolt.BaseRichBolt;
import com.twitter.heron.api.bolt.OutputCollector;
import com.twitter.heron.api.metric.GlobalMetrics;
import com.twitter.heron.api.topology.OutputFieldsDeclarer;
import com.twitter.heron.api.topology.TopologyBuilder;
import com.twitter.heron.api.topology.TopologyContext;
import com.twitter.heron.api.tuple.Tuple;
import com.twitter.heron.api.utils.Utils;
import com.twitter.heron.common.basics.ByteAmount;
import com.twitter.heron.examples.api.spout.TestWordSpout;
import com.twitter.heron.simulator.Simulator;


/**
 * This is a basic example of a Storm topology.
 */
public final class ComponentJVMOptionsTopology {

  private ComponentJVMOptionsTopology() {
  }

  public static void main(String[] args) throws Exception {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("word", new TestWordSpout(), 2);
    builder.setBolt("exclaim1", new ExclamationBolt(), 2)
        .shuffleGrouping("word")
        .addConfiguration("test-config", "test-key"); // Sample adding component-specific config

    Config conf = new Config();
    conf.setDebug(true);
    conf.setMaxSpoutPending(10);

    // TOPOLOGY_WORKER_CHILDOPTS will be a global one
    conf.put(Config.TOPOLOGY_WORKER_CHILDOPTS, "-XX:+HeapDumpOnOutOfMemoryError");

    // For each component, both the global and if any the component one will be appended.
    // And the component one will take precedence
    conf.setComponentJvmOptions("word", "-XX:NewSize=300m");
    conf.setComponentJvmOptions("exclaim1", "-XX:NewSize=300m");

    // component resource configuration
    conf.setComponentCpu("word", 0.5);
    conf.setComponentRam("word", ByteAmount.fromMegabytes(512));
    conf.setComponentDisk("word", ByteAmount.fromMegabytes(512));
    conf.setComponentCpu("exclaim1", 0.5);
    conf.setComponentRam("exclaim1", ByteAmount.fromMegabytes(512));
    conf.setComponentDisk("exclaim1", ByteAmount.fromMegabytes(512));

    // container resource configuration
    conf.setContainerDiskRequested(ByteAmount.fromGigabytes(2));
    conf.setContainerRamRequested(ByteAmount.fromGigabytes(2));
    conf.setContainerCpuRequested(2);

    // Specify the size of ram padding to per container.
    // Notice, this config will be considered as a hint,
    // and it's up to the packing algorithm to determine whether to apply this hint
    conf.setContainerRamPadding(ByteAmount.fromGigabytes(2));

    if (args != null && args.length > 0) {
      conf.setNumStmgrs(2);
      HeronSubmitter.submitTopology(args[0], conf, builder.createTopology());
    } else {
      Simulator simulator = new Simulator();
      simulator.submitTopology("test", conf, builder.createTopology());
      Utils.sleep(10000);
      simulator.killTopology("test");
      simulator.shutdown();
    }


  }

  public static class ExclamationBolt extends BaseRichBolt {
    private static final long serialVersionUID = 2165326630789117557L;
    private long nItems;
    private long startTime;

    @Override
    @SuppressWarnings("rawtypes")
    public void prepare(
        Map conf,
        TopologyContext context,
        OutputCollector collector) {
      nItems = 0;
      startTime = System.currentTimeMillis();
    }

    @Override
    public void execute(Tuple tuple) {
      if (++nItems % 100000 == 0) {
        long latency = System.currentTimeMillis() - startTime;
        System.out.println("Bolt processed " + nItems + " tuples in " + latency + " ms");
        GlobalMetrics.incr("selected_items");
      }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
  }
}
