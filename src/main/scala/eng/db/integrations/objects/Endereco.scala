package eng.db.integrations.objects

import eng.db.dataaccess.base.{Query, StringField}
import eng.db.shared.base.QueryKey

import scala.concurrent.{ExecutionContext, Future}

object Endereco {
    def getFromDataQuery(endereco: Endereco)(implicit ex: ExecutionContext) = Query[QueryKey](() => new QueryKey())
        .select(
            StringField[QueryKey]("Id", "Id", (c, id) => c.id = id),
            StringField[QueryKey]("Code", "Code", (c, code) => {
                c.code = {
                    if (code.isEmpty) None
                    else Some(code)
                }
            })
        ).from (
            new StringBuilder()
                .append("TABLE({function-name}(")
                .append(s"'${endereco.municipio.getOrElse("")}',")
                .append(s"'${endereco.logradouro.getOrElse("")}',")
                .append(s"'${endereco.numeroImovel.getOrElse("")}',")
                .append(s"'${endereco.complemento.getOrElse("")}',")
                .append(s"'${endereco.pontoReferencia.getOrElse("")}'")
                .append("))")
                .toString()
        )
        .where (
            "1=1"
        )
}
case class Endereco(
                   municipio: Option[String],
                   logradouro: Option[String],
                   numeroImovel: Option[String],
                   complemento: Option[String],
                   pontoReferencia: Option[String]
                   )
