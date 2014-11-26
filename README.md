# Overview

Simizer is a distributed application simulator.  This document will explain the general structure of the framework.

## Properties

  - Simizer is written in Java.
  - It is designed to enable the comparison protocols performance.

# Project Description

## Why a new simulator?

Trade offs in large distributed systems:
  - Consistency VS Availability: Partition and fault tolerance
  - Consistency VS Latency: System performance ->  QoS and SLAs
  - Cloud infrastructure issues:  Unpredictable latency variations (shared physical architectures)

Existing simulators:
 - CloudSim [http://www.cloudbus.org/cloudsim/]
 - SimGRID [http://simgrid.gforge.inria.fr/]
 - OptorSim [http://sourceforge.net/projects/optorsim/]

$→$ mainly focus on physical infrastructure simulation and study.

## The Simizer Project

1. Started as a small load balancing simulator in 2012
2. Derived from the need to simulate Amazon EC2 deployments
3. Extended to support diverse protocols and geographic distribution of datacenters and clients

$→$ Objective to integrate directly with load balancer

## Project Goals

- Provide means to simulate consistency policies at large scale
- Provide a simple API to implement policies
- Provide a simple JSON based system model specification
- Simulation at virtual machine level rather than hardware level

# Quickstart

Replay a simulation:
- to launch lbsim you need:
+ A load balancing policy
+ A requests sequence file (.csv)
+ A node description file (.json)
example:
here is the command line to run simulation:
java -cp ./dist/lbsim.jar lbsim.Lbsim run ./5nodes.json ./reqDescription_v1.csv ./workload_gene_v1.csv ./test lbsim.policies.RoundRobin

./5nodes.json -> node description file
./reqDescription_v1.csv -> request description file
./workload_gene_v1.csv -> workload description file
./test -> ressource description file (not implemented yet...)
lbsim.policies.RoundRobin -> load balancing policy



You can generate a new trace:
java -cp ./dist/lbsim.jar lbsim.Lbsim generate wl.conf ./reqDescription_v1.csv 10 200 30 > workload_gene_v1.csv

4 The Task API: Writing protocols and simulating apps in Simizer 
=================================================================

4.1 A Simple API for designing protocols 
-----------------------------------------

4.1.1 Optimistic policy example 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


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


4.2 Application example: 
-------------------------

4.2.1 Modelling a weather map interpolation application 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



  public class InterpolationApp extends Application {
        private static int KILO = 1024;
  
      public InterpolationApp() {
          super(0,128*KILO*KILO);
  
      }
      public InterpolationApp(int port) {
          super(port,128*KILO*KILO);
      }
      @Override
      public void init() {
          Request registerRequest = new Request("register");
          registerRequest.setAppId(0);
          vm.send(registerRequest, 
                      vm.getNetwork().getNode(Integer.parseInt(config.getProperty("frontend"))));
      }
      /**
       * Adds the frontend id to application configuration
       * @param ftdId 
       */
      public void setFrontend(int ftdId) {
          this.config.setProperty("frontend", Integer.toString(ftdId));
      }
      @Override
      public void handle(Node orig, Request req) {
  
          List<Resource> rList =new ArrayList<>();
  
          for(Integer rId: req.getResources()) {
              Resource r = vm.read(rId, 15 *KILO);
              if(r!=null)
                  rList.add(r);
          }
          // If some files don't exist we send an error back to the
          // client.
          if(rList.size() == req.getResources().size()) {
              long nbInst = 24*15*1500*rList.size();
               vm.execute(nbInst, 15*KILO*rList.size(), rList);
          }
          else {
              req.setError(req.getResources().size() - rList.size());
          }
          vm.sendResponse(req, orig);
    }   
  }

5 Developments 
===============

5.1 Task API 
-------------

5.1.1 TODO Generic Request / Response / send One Way system 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
- In order to this we need by application two lists: pendingResponses and pendingClients.

5.1.2 TODO More complex protocols will require DSL or byte code manipulation 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

5.2 Network Simulation 
-----------------------

5.2.1 TODO For accuracy: integration with Cloud Sim (network model) 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

5.3 Misc 
---------

5.3.1 TODO Cleanup code before pushing to github 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+ Code available @  [http://forge.isep.fr/projects/simizer]


5.3.2 TODO GUI -> realtime graphing 
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
