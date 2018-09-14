package eng.db.integrations.objects

import java.sql.ResultSet

import eng.db.dataaccess.base.{Query, StringField}

import scala.concurrent.{ExecutionContext, Future}

object CategoriaUso {
    def getAllQuery()(implicit ex: ExecutionContext) = Query[CategoriaUso]()
        .selectLiteral(
            "Code Id",
            "Description Description"
        ) withParser { (s: ResultSet) =>
            CategoriaUso(s.getString("Id"), s.getString("Description"))
        } from "TABLE({function-name-and-parameters})" where "1=1"
}
case class CategoriaUso(
                       id: String,
                       description: String
                       )