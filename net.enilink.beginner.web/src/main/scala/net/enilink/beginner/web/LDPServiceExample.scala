package net.enilink.beginner.web

import net.enilink.komma.core.{Statement, URI, URIs}
import net.enilink.platform.ldp.{LDP, LDPHelper}
import net.enilink.platform.lift.util.Globals
import net.enilink.vocab.rdf.RDF
import net.enilink.vocab.rdfs.RDFS
import net.liftweb.common.Full

import scala.jdk.CollectionConverters._

object LDPServiceExample extends LDPHelper {
// // use Handler to configure LDP service
//  val handler = new BasicContainerHandler("ldp")
//  val rootUri = URIs.createURI(s"""http://localhost:8080/${ handler.getPath }/""")

  // use default configuration
  val rootUri = URIs.createURI("http://localhost:8080/ldp/")
  // initialize the root container
  init(rootUri)

  // register LDP endpoint for resources
  register("ldp", rootUri, null)

  protected def init(uri: URI) = Globals.contextModelSet.vend.map { ms =>
    ms.getUnitOfWork.begin
    try {
      // create the resource model if it does not yet exist
      val m = ms.createModel(uri)
      m.setLoaded(true)
      m.getManager.add(List(
        new Statement(uri, RDF.PROPERTY_TYPE, LDP.TYPE_RESOURCE),
        //new Statement(uri, RDF.PROPERTY_TYPE, LDP.TYPE_RDFSOURCE),
        //new Statement(uri, RDF.PROPERTY_TYPE, LDP.TYPE_CONTAINER),
        new Statement(uri, RDF.PROPERTY_TYPE, LDP.TYPE_BASICCONTAINER),
        new Statement(uri, RDFS.PROPERTY_LABEL, "LDP Basic container"),
        new Statement(uri, RDFS.PROPERTY_COMMENT, "root container for various RDF and none-RDF resources including containers ")).asJava)

      // side effect for rdfa templates: set Globals.contextModel
      Globals.contextModel.default.set(() => Full(m))
    } catch {
      case t: Throwable => t.printStackTrace
    } finally {
      ms.getUnitOfWork.end
    }
  }

}
