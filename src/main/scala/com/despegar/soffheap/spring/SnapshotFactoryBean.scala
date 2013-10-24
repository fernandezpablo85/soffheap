package com.despegar.soffheap.spring

import org.springframework.beans.factory.FactoryBean
import com.despegar.soffheap.snapshot.{SnapshotBuilder, DataSource, Snapshot}
import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
 * Convenient factory for construct a SoffHeapSnapshot
 */
class SnapshotFactoryBean extends FactoryBean[Snapshot[_,_]] {

  @BeanProperty
  var name: String = _

  @BeanProperty
  var dataSource: DataSource[Any,Any]  = _

  @BeanProperty
  var cronExpression: String = _

  @BeanProperty
  var diskPersistence: Boolean = false

  @BeanProperty
  var hintedClasses:java.util.Set[Class[_]] = _

  @BeanProperty
  var maxHeapElementsInCache:Int = 0

  var path: String = _

  def getObject = {
    val snapshotBuilder = SnapshotBuilder[Any,Any]().withDataSource(dataSource)
                                                    .withReloadsAt(cronExpression)
                                                    .withName(name)

    if (diskPersistence) {
      if (path == null) snapshotBuilder.withDiskPersistence(path)
      else snapshotBuilder.withDiskPersistence()
    }

    if(!hintedClasses.isEmpty){
      hintedClasses.asScala.foreach(snapshotBuilder.withHintedClass(_))
    }

    if(maxHeapElementsInCache > 0) {
      snapshotBuilder.withMaximumHeapElements(maxHeapElementsInCache)
    }
    snapshotBuilder.build()
  }


  def getObjectType: Class[_] = {
    classOf[Snapshot[_,_]]
  }

  def isSingleton: Boolean = true

  def setPath(path:String):Unit = {
    this.path = path
    this.diskPersistence = true
  }
}


