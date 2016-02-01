**Latest news (9 Feb 10):** a multi-threaded motif finder (`nz.ac.massey.cs.gql4jung.jmpl.MultiThreadedGQLImpl`) is now ready to use. All test cases pass, the engine can be instantiated passing the number of threads as parameter. Setting this number to the number of cores / processors works best. Here are some results from searching for CD instances in xalan on a Xeon E5440 8-core box running RHL: 1 thread - 154s, 2 threads - 82s, 4 threads - 48s, 8 threads - 37s. However, on a MacBook Pro the same took only 15s with 1 thread and 9s with 2 threads.

The aim of this project is to provide a graph query language that can be used to query graphs represented in [jung](http://jung.sourceforge.net/). There are numerous applications for this, including the declarative definition of architectural antipatterns and smells in systems. The project is inspired by the use of XPath queries (on the AST) to describe code level antipatterns in [PMD](http://pmd.sourceforge.net/).

The current version of the software can analyse dependency graphs saved in [graphml](http://graphml.graphdrawing.org/) and [odem](http://www.dependency-analyzer.org/) format, and can extract graphs from byte code (multiple jar files and class file folders) using [http://depfind.sourceforge.net/ depfind](.md). There are several on-board queries - abstraction without decoupling, circular dependencies between name spaces and containers, multiple clusters in name spaces, db to ui illegal layer dependencies, degenerated inheritance and subtype knowledge.

![http://www-ist.massey.ac.nz/jbdietrich/gql4jung/screenshot1.jpg](http://www-ist.massey.ac.nz/jbdietrich/gql4jung/screenshot1.jpg)

Queries consist of path constraints and constraints on vertices (representing classes) that can be written using a java style expression language (currently, [MVEL2](http://mvel.codehaus.org/) is supported). An example query is given below:

```
<motif name="abstraction without decoupling">
    <select role="client">
    	<constraint>!client.abstract</constraint>
    </select>
    <select role="service">
   	<constraint>service.abstract</constraint>
    </select>
    <select role="service_impl">
    	<constraint>!service_impl.abstract</constraint>
    </select>
    <connectedBy role="inherits" from="service_impl" to="service">
    	<constraint>inherits.type=='extends' || inherits.type=='implements'</constraint>
    </connectedBy>
    <connectedBy role="service_invocation" from="client" to="service" minLength="1" maxLength="1">
    	<constraint>service_invocation.type=='uses'</constraint>    
    </connectedBy>
    <connectedBy role="implementation_dependency" from="client" to="service_impl">
	<constraint>implementation_dependency.type=='uses'</constraint>
    </connectedBy>
    <groupBy>
        <element>client</element>
        <element>service</element>
    </groupBy>
</motif>
```

The tool can find results for those queries in complex graphs. Resolving these constraints is a difficult problem (it is NP complete). However, we have implemented a number of heuristics and as a result we have been able to analyse large graphs extracted from real world projects with upto 50,000 edges within <= minutes. In most cases, analysis only takes a few seconds. The full results have been submitted for publications. The results of this study also show that these definitions are useful as (1) there are some real world programs with instances of these patterns, (2) the number of significantly different instances in a program is relatively small.



The software contains the algorithms and a simple query browser application that displays the motifs found as graphs. It also comes with some sample graphs.