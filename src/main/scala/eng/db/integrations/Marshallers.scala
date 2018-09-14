package eng.db.integrations

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import eng.db.integrations.objects._
import eng.db.shared.base.BaseTypesMarshallers
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait Marshallers extends SprayJsonSupport with DefaultJsonProtocol with BaseTypesMarshallers {
    implicit val documentFormat: RootJsonFormat[CategoriaUso] = jsonFormat2(CategoriaUso.apply)
    implicit val fornecimentoFormat: RootJsonFormat[Fornecimento] = jsonFormat2(Fornecimento.apply)
    implicit val enderecoFormat: RootJsonFormat[Endereco] = jsonFormat5(Endereco.apply)
    implicit val manouverFormat: RootJsonFormat[Manouver] = jsonFormat2(Manouver.apply)
}
