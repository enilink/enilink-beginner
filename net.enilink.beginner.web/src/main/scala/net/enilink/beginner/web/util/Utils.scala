package net.enilink.beginner.web.util

import net.enilink.komma.core.URIs

object DCTERMS {
  val NAMESPACE_URI = URIs.createURI("http://purl.org/dc/terms/")

  val PROPERTY_DATE = NAMESPACE_URI.appendLocalPart("date")
}