<!--
Copyright (c) 2014 - 2015 YCSB contributors. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. See accompanying
LICENSE file.
-->

## Quick Start

This section describes how to run YCSB on Mogucache. 

### 1. Start Mogucache

### 2. Install Java and Maven

### 3. Set Up YCSB

Git clone YCSB and compile:

    git clone http://github.com/sel-fish/YCSB.git
    cd YCSB
    mvn -pl com.yahoo.ycsb:mogucache-async-binding -am clean package

### 4. Provide Mogucache Connection Parameters
    
Set the host, port, and password (do not mogucache auth is not turned on) in the 
workload you plan to run.

- `mogucache.mastercs`
- `mogucache.slavecs`
- `mogucache.namespace`

Or, you can set configs with the shell command, EG:

    ./bin/ycsb load mogucache-async -s -P workloads/workloada -p "mogucache.mastercs=127.0.0.1:5198" -p "mogucache.namespace=bizname_1" > outputLoad.txt

### 5. Load data and run tests

Load the data:

    ./bin/ycsb load mogucache-async -s -P workloads/workloada > outputLoad.txt

Run the workload test:

    ./bin/ycsb run mogucache-async -s -P workloads/workloada > outputRun.txt

