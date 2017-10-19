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

package com.twitter.heron.streamlet;

import com.twitter.heron.streamlet.impl.BuilderImpl;

/**
 * A Builder is used to register all sources. Builders thus keep track
 * of all starting points for the computation DAG and use this
 * information to build the topology.
 */
public interface Builder {
  static Builder createBuilder() {
    return new BuilderImpl();
  }

  /**
   * All sources of the computation need to be registered using addSource,
   * which can be called as many times as necessary on a single Builder.
   * @param supplier The supplier function that is used to create the streamlet
   */
  <R> Streamlet<R> newSource(SerializableSupplier<R> supplier);

  /**
   * Same as newSource except returns a KVStreamlet rather than a Streamlet
   * @param supplier
   * @param <K> The type of the KVStreamlet's key
   * @param <V> The type of the KVStreamlet's value
   * @return
   */
  <K, V> KVStreamlet<K, V> newKVSource(SerializableSupplier<KeyValue<K, V>> supplier);

  /**
   * Creates a new Streamlet using the underlying generator (Builder).
   * @param generator The generator that provides the elements of the streamlet
   * @param <R> The Streamlet's data type
   * @return
   */
  <R> Streamlet<R> newSource(Source<R> generator);

  /**
   * Same as newSource except returns a KVStreamlet.
   * @param generator The generator that provides the elements of the streamlet
   * @param <K> The type of the KVStreamlet's key
   * @param <V> The type of the KVStreamlet's value
   * @return
   */
  <K, V> KVStreamlet<K, V> newKVSource(Source<KeyValue<K, V>> generator);
}
