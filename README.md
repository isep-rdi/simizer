# The Simizer Project

Simizer is a distributed application simulator.  It can be used to simulate the
behavior of cloud platforms without the need to deploy an application on an
actual cloud infrastructure (which takes both time and money).  Instead, the
framework can be used to quickly run a simulation, modify the parameters, and
then run the simulation again.

## Overview

  - Simizer is written in Java.
  - It is designed to compare various implementations of algorithms and
    protocols in a controlled environment.

## Goals

  - To operate at the virtual machine level rather than the hardware level.
  - To provide a simple API to implement polcies:
    + for load balancing
    + for data consistency (at a large scale)
  - To be easy to learn and use.

## Motivation

Why do we need a new simulator?  Aren't there already simulators on the market
that accomplish what we are trying to do?  The existing frameworks focus on the
study of physical infrastructure rather than the algorithms.

When using and developing these simulators, it is necessary to consider the
trade-offs present in these large distributed systems:

  - Consistency vs. Availability: Partition and fault tolerance
  - Consistency vs. Latency: System performance -> QoS and SLAs
  - Cloud infrastructure issues: Unpredictable latency variations (shared
    physical architectures)

Here is a short list of existing simulators:

  - [CloudSim](http://www.cloudbus.org/cloudsim/)
  - [SimGRID](http://simgrid.gforge.inria.fr/)
  - [OptorSim](http://sourceforge.net/projects/optorsim/)

## History

1. _Simizer_ started as a small load balancing simulator in 2012.
2. The motivation for its development was the need to simulate Amazon EC2
   deployments.
3. Over time, it has been extended to support diverse protocols and geographic 
   distribution of data centers and clients.


# Quickstart

The included examples are a good place to start learning how to use the
framework.  There is an included tutorial that starts at square one and works
its way up to some rather complex examples.

Also included in the examples framework are the tools needed to start building
applications that take advantages of the included load balancing applications
and code.

To get started, visit the releases page and download the latest release.
Extract the file, then navigate to the tutorials directory to get started.


# Uses

## Load Balancing Analysis

The _Simizer_ framework comes with everything needed to simulate the
characteristics of various load balancing algorithms.

Several implementations of common load balancing algorithms, such as Round
Robin, Cost-Aware, and others are included with the examples add-on to the
framework.

## Consistency Analysis


# API Features

## Tasks

```java
public class OptimisticPolicy implements StoreApplication {
  public Request write(Request r) {
    // local write
    Integer id= r.getResourceId();
    Resource res= disk.write(id);
    // Replication to other nodes
    for(Node n: HashRing.getNodes(id))
      sendRequest("replicate",res,n, r.getArrivalTime());
    return r;
  }

  public Request read(Request r) {
    Integer id= r.getResourceId();
    Resource res= disk.read(id);
    // Resource checking
    if(res == null) r.setError(r.getError()+1);
    return sendResponse(r, res);
  }
  
  public Request replicate(Request r) {
    Resource res = r.getResource();
    // Check version
    if(res.getWritetime() > disk.read(res.getId()).getWriteTime())
      disk.write(res);
    return r;
  }
}
```

## Applications

Consider an application that interpolates weather maps.  Using the _Simizer_
framework, we can easily write an application that simulates the basic behavior.

The general idea of this application is as follows:

  - the application receives a request (containing application-specific metadata)
  - the server reads the required resources from the disk (by using the framework,
    these coud come from an HDD or a cache)
  - the server performs some processing, proportional to the number of resources
  - the servers sends a response back to the client

```java
public class InterpolationApp extends Application {

  public InterpolationApp(int port) {
    super(port, 128 * StorageElement.MEGABYTE);
  }

  @Override
  public void init(TaskScheduler scheduler) {
    Request registerRequest = new Request(0, "", "register");

    Node destination = vm.getNetwork().getNode(
            Integer.parseInt(config.getProperty("frontend")));
    scheduler.sendRequest(destination, registerRequest);
  }

  @Override
  public void handle(TaskScheduler scheduler, Node orig, Request req) {
    List<Resource> rList = new ArrayList<>();

    // STEP ONE: Read each of the required resources.
    // By using the "scheduler", we utilize the simulation capabilities of
    // simizer.  We'll read the resources one after another.
    for (Integer rId : req.getResources()) {
      Resource r = scheduler.read(rId, (int) (15 * StorageElement.KILOBYTE));
      if (r != null) {
        rList.add(r);
      }
    }

    // STEP TWO: Process the files.
    // If there are files missing, we mark this as an error.
    if (rList.size() == req.getResources().size()) {
      long nbInst = 24 * 15 * 1500 * rList.size();
      int memSize = 15 * (int) StorageElement.KILOBYTE * rList.size();
      scheduler.execute(nbInst, memSize, rList);
    } else {
      req.reportErrors(req.getResources().size() - rList.size());
    }

    // STEP THREE: Send a response back to the client.
    // This will only happen after the simulation of the above "read" and
    // "execute" operations.
    scheduler.sendResponse(req, orig);
  }

}
```


# Future Developments

## Network Simulation

For accuracy in network transmissions, consider integrating with the _CloudSim_
framework.

## GUI Analysis

Currently, the output from the application is not particuarly "pretty."  A
separate application (or an addition to the _Simizer_ framework) could be added
that allows the results of a simulation to be viewed in a more graphical,
easier-to-understand manner.

This GUI could even include some sort of realtime graphing output during the
simulation.  (This may require running the simulation in realtime as opposed to
"as fast as we possibly can.")
