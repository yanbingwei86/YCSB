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

import com.mogujie.mogucache.MoguCache;
import com.mogujie.mogucache.MogucacheImpl;
import com.mogujie.mogucache.MogucacheManager;
import com.mogujie.mogucache.packet.Result;
import com.yahoo.ycsb.*;

import java.util.*;

/**
 * YCSB binding for Mogucache.
 *
 * See {@code mogucache/README.md} for details.
 */
public class MogucacheAsyncClient extends DB {
  private int maxValueLength = 4096;
  public static final String NAMESPACE = "mogucache.namespace";

  private MogucacheImpl pool = null;
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

    namespace = props.getProperty(NAMESPACE);
    if (namespace == null) {
      throw new DBException("must specify namespace info");
    }

    MogucacheManager manager = new MogucacheManager();
    manager.init(this.namespace, 1000, 100000);
      try {
          pool = manager.getValidCacheObject();
      } catch (Exception e) {
          e.printStackTrace();
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
    MoguCache cache = null;
    try {
      cache = pool;
      Result<String> rslt = cache.get(key);
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
    MoguCache cache = null;
    try {
      cache = pool;
      Result<String> rslt = cache.set(key, getValueStr(values));
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
