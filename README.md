SoffHeap
===
A simple library to store large set of objects out of the Java heap space using sun.misc.Unsafe to minimize GC overhead. 

## Usage (Scala)
### SBT settings 

Add the following sbt dependency to your project settings:

```scala
libraryDependencies += "com.despegar" % "soffheap" % "0.1.3"
```
### Example:

```scala
val soffHeapMap = SoffHeapMapBuilder[String,SomeObject]().build()

soffHeapMap.put("key1", SomeObject()) //the object is moved out of the heap

val someObjectFromOffheap = soffHeapMap.get("key1")
```

### Usage (Java)

Add the following dependency to your pom.xml (Maven):

```xml
<dependency>
	<group>com.despegar</group>
	<artifactId>soffheap</artifactId>
	<version>0.1.2</version>
</dependency>
```
```java
SoffHeapMap<String,SomeObject> soffHeapMap = new SoffHeapMapBuilder<String,SomeObject>().buildJ(); //Use buildJ for plan Java access

soffHeapMap.put("key1", new SomeObject()); //the object is moved out of the heap

SomeObject someObjectFromOffheap = soffHeapMap.get("key1"); 
```
## Usage (Spring)

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans 
       
       http://www.springframework.org/schema/beans/spring-beans.xsd 
       http://www.springframework.org/schema/util 
       http://www.springframework.org/schema/util/spring-util.xsd">

    <bean name="someSnapshot" class="com.despegar.soffheap.spring.SnapshotFactoryBean">
        <property name="name" value="SnapshotTest"/>
        <property name="cronExpression" value="0/2 * * ? * *"/>
        <property name="path" value="/tmp"/>
        <property name="dataSource" ref="snapshotBuilderDs"/>
        <property name="hintedClasses" ref="hintedClasses"/>
    </bean>

    <bean name="snapshotBuilderDs" class="com.despegar.soffheap.SomeDataSource"/>

    <util:set id="hintedClasses">
        <value type="java.lang.Class">com.despegar.soffheap.PojoValue</value>
    </util:set>

</beans>
```
## Configuration

By default SoffHeap sets a limit of 2GB in the maximum amount of memory that can be allocated to store objects. This limit can be modified by passing a VM parameter.

### Example: 
```java
java -DmaximumSoffHeapMemoryInGB=4
```
## Tunings
  * Hinted classes: The serializers used by SoffHeap can be tuned if necessary passing the types involved in serialization/deserialization of your objects.
    ```scala
    SoffHeapMapBuilder[String,SomeComplexObject]().withHintedClass(classOf[SomeComplexObject])
						  .withHintedClass(classOf[SomeOtherClass])...
    ```
  * Heap cache: Frequent accesses to objects can be optimized by enabling an LRU cache in the heap. Maximum heap elements can be configured.
    ```scala
    SoffHeapMapBuilder[String,SomeComplexObject]().withMaximumHeapElements(10)
    ```

## Implementation details

  * Built in Scala.
  * Reference Counting to free unused objects.
  * LRU Heap Cache.
  * Kryo and FST serialization. 
  * Metrics statistics.
  * Disk persistence.
  * Scheduled reloads.
  * Basic Spring support.

## Monitoring

SoffHeap exposes a set of metrics that can be used to monitor its behavior. By default a JMX exporter is used. Other monitoring solutions can be used by setting Codahale Metrics reporters to the SoffHeap MetricsRegistry.

### Example:
```scala
val registry =  SoffHeapMetricsRegistryHolder.getMetricsRegistry()
val reporter =  GangliaReporter.forRegistry(registry)
                               .convertRatesTo(TimeUnit.SECONDS)
                               .convertDurationsTo(TimeUnit.MILLISECONDS)
                               .build(ganglia)
  
reporter.start(1, TimeUnit.MINUTES)
```
## Limitations

  * The first implementation only supports a keyvalue store.
  * Only support sun.misc.Unsafe. Fallback to DirectBuffer usage is pending.
  * No TTL support.

## History
  * October 29, 2013   Released version 0.1.3

