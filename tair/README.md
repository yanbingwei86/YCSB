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

This section describes how to run YCSB on Tair. 

### 1. Start Tair

### 2. Install Java and Maven

### 3. Set Up YCSB

Git clone YCSB and compile:

    git clone http://github.com/brianfrankcooper/YCSB.git
    cd YCSB
    mvn -pl com.yahoo.ycsb:tair-binding -am clean package

### 4. Provide Tair Connection Parameters
    
Set the host, port, and password (do not tair auth is not turned on) in the 
workload you plan to run.

- `tair.master`
- `tair.slave`
- `tair.groupname`

Or, you can set configs with the shell command, EG:

    ./bin/ycsb load tair -s -P workloads/workloada -p "tair.master=127.0.0.1:5198" -p "tair.groupname=group_1" > outputLoad.txt

### 5. Load data and run tests

Load the data:

    ./bin/ycsb load tair -s -P workloads/workloada > outputLoad.txt

Run the workload test:

    ./bin/ycsb run tair -s -P workloads/workloada > outputRun.txt

