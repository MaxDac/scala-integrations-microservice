package eng.db.integrations.objects

import java.sql.ResultSet
import java.util.Date

import eng.db.dataaccess.base.DataAccessComponent.EquivalentTable
import eng.db.dataaccess.base.{DataAccessComponent, Query, StringField}

import scala.concurrent.{ExecutionContext, Future}

object Fornecimento {
    private def defaultSqlParser = (s: ResultSet) => Fornecimento(s.getString("IdFORN"), s.getString("CodFORN"))

    def getByCodeQuery(code: String)(implicit ex: ExecutionContext) = Query[Fornecimento]()
        .selectLiteral(
            "{column-name}_ID IdFORN",
            "{column-name}_COD CodFORN"
        ) withParser defaultSqlParser from "{table-name}" where s"{column-name}_COD = '$code'"

    def getByCode(code: String)(implicit ex: ExecutionContext): Future[Fornecimento] = getByCodeQuery(code) perform() map (fs => fs.head)

    def getFromAddressQuery(endereco: Endereco)(implicit ex: ExecutionContext) = Query[Fornecimento]()
        .selectLiteral(
            "Id IdFORN",
            "Code CodFORN"
        ) withParser defaultSqlParser from (
            new StringBuilder()
                .append("TABLE({function-name}(")
                .append(s"'${endereco.municipio.getOrElse("")}',")
                .append(s"'${endereco.logradouro.getOrElse("")}',")
                .append(s"'${endereco.numeroImovel.getOrElse("")}',")
                .append(s"'${endereco.complemento.getOrElse("")}',")
                .append(s"'${endereco.pontoReferencia.getOrElse("")}'")
                .append("))")
                .toString()
        ) where (
            "1=1"
        )

    def getManouverOnItemQuery(dac: DataAccessComponent, item: Fornecimento)(implicit ex: ExecutionContext) = {
        dac.queryEquivalentTable(EquivalentTable(
            "{function-name}",
            item.id
        ), queryResult => Manouver(
            queryResult.getString(1),
            Some(queryResult.getTimestamp(2))
        ))
    }
}
case class Fornecimento(
                        id: String,
                        code: String = ""
                       )

case class Manouver(
                   description: String = "",
                   date: Option[Date] = None
                   )
