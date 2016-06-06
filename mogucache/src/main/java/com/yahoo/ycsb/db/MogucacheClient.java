/**
 * Copyright (c) 2012 YCSB contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

/**
 * Mogucache client binding for YCSB.
 *
 * All YCSB records are mapped to a Mogucache *hash field*.  For scanning
 * operations, all keys are saved (by an arbitrary hash) in a sorted set.
 */

package com.yahoo.ycsb.db;

import com.mogujie.mogucache.config.CachePoolConfig;
import com.mogujie.mogucache.node.Result;
import com.mogujie.mogucache.pool.MoguCacheFactory;
import com.yahoo.ycsb.*;

import java.util.*;

/**
 * YCSB binding for Mogucache.
 *
 * See {@code mogucache/README.md} for details.
 */
public class MogucacheClient extends DB {
  private int maxValueLength = 4096;
  public static final String MASTERCS = "mogucache.mastercs";
  public static final String SLAVECS = "mogucache.slavecs";
  public static final String NAMESPACE = "mogucache.namespace";

  private MoguCacheFactory pool = null;
  private String masterIP = "";
  private int masterPort = 0;
  private String slaveIP = "";
  private int slavePort = 0;
  private String namespace = "";

  public static String getHost(String address) {
    String host = null;
    if (address != null) {
      String[] a = address.split(":");
      if (a.length >= 2) {
        host = a[0].trim();
      }
    }
    return host;
  }

  public static int getPort(String address) {
    int port = 0;
    if (address != null) {
      String[] a = address.split(":");
      if (a.length >= 2) {
        port = Integer.parseInt(a[1].trim());
      }
    }
    return port;
  }

  public void init() throws DBException {
    Properties props = getProperties();

    String masterString = props.getProperty(MASTERCS);
    if (masterString != null) {
      this.masterIP = getHost(masterString);
      this.masterPort = getPort(masterString);
    } else {
      throw new DBException("must specify master configserver info");
    }

    String slaveString = props.getProperty(SLAVECS);
    if (slaveString != null) {
      this.slaveIP = getHost(slaveString);
      this.slavePort = getPort(slaveString);
    } else {
      throw new DBException("must specify slave configserver info");
    }

    namespace = props.getProperty(NAMESPACE);
    if (namespace == null) {
      throw new DBException("must specify namespace info");
    }

    CachePoolConfig config = new CachePoolConfig();
    config.setMaxActive(30);
    config.setMaxIdle(30);
    config.setMaxWait(512);
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);

    pool = new MoguCacheFactory(config, this.namespace, this.masterIP, this.masterPort,
            this.slaveIP, this.slavePort, 20000, 100);
    try {
      pool.init();
    } catch (Exception e) {
      throw new DBException("mogucache init failed");
    }
  }

  public void cleanup() throws DBException {
    pool.close();
  }

  private String getValueStr(HashMap<String, ByteIterator> values) {
    HashMap<String, String> stringMap = StringByteIterator.getStringMap(values);
    StringBuffer rslt = new StringBuffer();
    for (Map.Entry<String, String> pair : stringMap.entrySet()) {
      rslt.append(pair.getKey());
      rslt.append(pair.getValue());
      if (rslt.length() > maxValueLength) {
        return rslt.substring(0, maxValueLength);
      }
    }
    return rslt.toString();
  }

  @Override
  public Status read(String table, String key, Set<String> fields,
                     HashMap<String, ByteIterator> result) {
    try {
      Result<String> rslt = pool.get(key);
      if (rslt.isSuccess()) {
        if (null != rslt.getValue()) {
          return Status.OK;
        } else {
          return Status.NOT_FOUND;
        }
      } else {
        return Status.ERROR;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Status.ERROR;
  }

  @Override
  public Status insert(String table, String key,
                       HashMap<String, ByteIterator> values) {
    try {
      Result<String> rslt = pool.set(key, getValueStr(values));
      if (rslt.isSuccess()) {
        return Status.OK;
      } else {
        return Status.ERROR;
      }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return Status.ERROR;
  }

  @Override
  public Status delete(String table, String key) {
    System.out.println("delete...");
    return Status.NOT_IMPLEMENTED;
  }

  @Override
  public Status update(String table, String key,
                       HashMap<String, ByteIterator> values) {
    return insert(table, key, values);
  }

  @Override
  public Status scan(String table, String startkey, int recordcount,
                     Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
    System.out.println("scan...");
    return Status.NOT_IMPLEMENTED;
  }

}
