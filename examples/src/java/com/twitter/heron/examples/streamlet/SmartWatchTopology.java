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

package com.twitter.heron.examples.streamlet;

import com.twitter.heron.api.utils.Utils;
import com.twitter.heron.examples.streamlet.utils.StreamletUtils;
import com.twitter.heron.streamlet.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class SmartWatchTopology {
    private static final Logger LOG = Logger.getLogger(SmartWatchTopology.class.getName());

    private static final List<String> JOGGERS = Arrays.asList(
            "bill",
            "ted"
    );

    private static class SmartWatchReading implements Serializable {
        private static final long serialVersionUID = -6555650939020508026L;
        private final String userId;
        private final float distanceRun;

        SmartWatchReading() {
            Utils.sleep(5);
            this.userId = StreamletUtils.randomFromList(JOGGERS);
            this.distanceRun = (float) ThreadLocalRandom.current().nextInt(10);
            LOG.info(String.format("Emitted smart watch reading: %s", this));
        }

        KeyValue<String, Float> toKV() {
            return new KeyValue<>(userId, distanceRun);
        }

        @Override
        public String toString() {
            return String.format("(user: %s, distance: %f)", userId, distanceRun);
        }
    }

    /**
     * All Heron topologies require a main function that defines the topology's behavior
     * at runtime
     */
    public static void main(String[] args) throws Exception {
        int jogLength = 20;

        Builder processingGraphBuilder = Builder.createBuilder();

        processingGraphBuilder.newSource(SmartWatchReading::new)
                .setName("smart-watch-readings-source")
                .mapToKV(SmartWatchReading::toKV)
                .setName("map-smart-watch-readings-to-kv")
                .reduceByKeyAndWindow(WindowConfig.TumblingCountWindow(jogLength), (x, y) -> (x + y) / jogLength)
                .setName("emit-average-speed-by-runner")
                .log();

        Config config = new Config();

        /**
         * Fetches the topology name from the first command-line argument
         */
        String topologyName = StreamletUtils.getTopologyName(args);

        /**
         * Finally, the processing graph and configuration are passed to the Runner,
         * which converts the graph into a Heron topology that can be run in a Heron
         * cluster.
         */
        new Runner().run(topologyName, config, processingGraphBuilder);
    }
}
