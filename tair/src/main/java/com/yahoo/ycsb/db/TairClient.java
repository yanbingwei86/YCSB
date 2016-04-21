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
 * Tair client binding for YCSB.
 *
 * All YCSB records are mapped to a Tair *hash field*.  For scanning
 * operations, all keys are saved (by an arbitrary hash) in a sorted set.
 */

package com.yahoo.ycsb.db;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.impl.DefaultTairManager;
import com.yahoo.ycsb.*;

import java.util.*;

/**
 * YCSB binding for <a href="http://tair.taobao.org/">Tair</a>.
 *
 * See {@code tair/README.md} for details.
 */
public class TairClient extends DB {

  private DefaultTairManager tairManager = null;
  private int defaultNamespace = 961;
  private int maxValueLength = 4096;

  public static final String MASTERCS = "tair.mastercs";
  public static final String SLAVECS = "tair.slavecs";
  public static final String GROUPNAME = "tair.groupname";

  public void init() throws DBException {
    Properties props = getProperties();
    List<String> configserverList = new ArrayList<String>();

    String masterString = props.getProperty(MASTERCS);
    if (masterString != null) {
      configserverList.add(masterString);
    } else {
      throw new DBException("must specify master configserver info");
    }

    String slaveString = props.getProperty(SLAVECS);
    if (slaveString != null) {
      configserverList.add(slaveString);
    } else {
      configserverList.add(masterString);
    }

    String groupname = props.getProperty(GROUPNAME);
    if (groupname == null) {
      throw new DBException("must specify groupname info");
    }

    tairManager = new DefaultTairManager();
    tairManager.setConfigServerList(configserverList);
    tairManager.setGroupName(groupname);
    tairManager.init();
  }

  public void cleanup() throws DBException {
    tairManager.close();
  }

  /*
   * Calculate a hash for a key to store it in an index. The actual return value
   * of this function is not interesting -- it primarily needs to be fast and
   * scattered along the whole space of doubles. In a real world scenario one
   * would probably use the ASCII values of the keys.
   */
  private double hash(String key) {
    return key.hashCode();
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
    Result<DataEntry> rde = tairManager.get(defaultNamespace, key);
    if (rde.getRc().equals(ResultCode.SUCCESS)) {
      return Status.OK;
    } else if (rde.getRc().equals(ResultCode.DATANOTEXSITS)) {
      return Status.NOT_FOUND;
    }
    return Status.ERROR;
  }

  @Override
  public Status insert(String table, String key,
                       HashMap<String, ByteIterator> values) {
    String value = getValueStr(values);
//    System.out.println("insert..., key: " + key + ", value: " + value);
    ResultCode code = tairManager.put(defaultNamespace, key, value);
    if (code.equals(ResultCode.SUCCESS)) {
      return Status.OK;
    } else {
      System.out.println(code);
      return Status.ERROR;
    }
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
