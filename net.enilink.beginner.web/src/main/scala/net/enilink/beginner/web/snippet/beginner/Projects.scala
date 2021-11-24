package net.enilink.beginner.web.snippet.beginner

import net.enilink.beginner.web.util.DCTERMS
import net.enilink.beginner.web.util.SnippetHelpers._
import net.enilink.komma.core.{IEntity, Statement}
import net.enilink.komma.model.IModel
import net.enilink.platform.lift.util.{AjaxHelpers, CurrentContext, Globals}
import net.enilink.vocab.foaf.FOAF
import net.enilink.vocab.rdf.RDF
import net.liftweb.common.{Empty, Full}
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{Noop, Run}
import net.liftweb.util.ClearNodes
import net.liftweb.util.Helpers._

import java.util.GregorianCalendar
import javax.xml.datatype.DatatypeFactory
import scala.collection.immutable.Nil

class Projects {
  /**
   * Creates a canonical project URI in the form of [model name]/projects/[name].
   */
  def projectUri(model: IModel, name: String) = {
    var ns = model.getManager.getNamespace("")
    if (ns.lastSegment == "") ns = ns.trimSegments(1)
    ns.appendSegments(Array("projects", "")).appendLocalPart(name)
  }

  /**
   *
   */
  def create = SHtml.hidden(() => {
    val result = for {
      model <- Globals.contextModel.vend
      name <- doAlert(paramNotEmpty("name", "Bitte einen Namen eingeben."))
    } yield {
      val project = model.resolve(projectUri(model, name))
      val projectType = FOAF.TYPE_PROJECT
      (if (project.getRdfTypes.contains(projectType)) {
        S.error("alert-name", "Ein Projekt mit diesem Namen existiert bereits.")
        Empty
      } else {
        project.addProperty(RDF.PROPERTY_TYPE, projectType)
        project.setRdfsLabel(name)
        S.param("description").filter(!_.isEmpty) foreach {
          desc => project.setRdfsComment(desc)
        }
        project.set(DCTERMS.PROPERTY_DATE, DatatypeFactory.newInstance.newXMLGregorianCalendar(new GregorianCalendar))

        val projectModel = project.getModel.getModelSet.createModel(project.getURI)
        projectModel.setLoaded(true)
        // add at least one statement to the model
        projectModel.getManager.add(new Statement(project, RDF.PROPERTY_TYPE, projectType))
        // set up the project with imports etc.
        // projectModel.addImport(someModel, null);
        Full(Run(s"$$(document).trigger('project-created', ['${project.getURI}']);"))
      }) openOr Noop
    }
    result openOr Noop
  }: JsCmd)

  /**
   * Delete the complete model that was created for a project.
   * The project is determined by using the current RDFa subject of the HTML node.
   */
  def delete = CurrentContext.value.map { c =>
    AjaxHelpers.onEvents("onclick" :: Nil, _ => {
      val project = c.subject.asInstanceOf[IEntity]
      val t = project.getEntityManager.getTransaction
      val result =
        try {
          for {
            m <- Globals.contextModel.vend
            modelSet = m.getModelSet
            projectModel <- Option(modelSet.getModel(project.getURI, false))
          } {
            projectModel.getManager.clear
            modelSet.getModels.remove(projectModel)
            modelSet.getMetaDataManager.remove(projectModel)
          }
          t.begin
          project.getEntityManager.removeRecursive(project, true)
          t.commit
          Full(Run(s"""$$('[about="${project.getURI}"]').remove()"""))
        } catch {
          case _: Throwable => if (t.isActive) t.rollback; Empty
        }
      result openOr Noop
    }) andThen "a [href]" #> "javascript://"
  } openOr ClearNodes
}