package io.github.mvillafuertem.scalcite.server.infrastructure

import java.util.UUID

import scalikejdbc.{ NoExtractor, SQL, _ }

package object repository {

  private[repository] def queryFindById(table: TableDefSQLSyntax, id: Long): SQL[Nothing, NoExtractor] =
    sql"SELECT * FROM ${table} WHERE ID = ${id}"

  private[repository] def queryFindByUUID(table: TableDefSQLSyntax, uuid: UUID): SQL[Nothing, NoExtractor] =
    sql"SELECT * FROM ${table} WHERE UUID = ${uuid}"

  private[repository] def queryFindAll(table: TableDefSQLSyntax): SQL[Nothing, NoExtractor] =
    sql"SELECT * FROM ${table}"

  private[repository] def queryDeleteByUUID(table: TableDefSQLSyntax, uuid: UUID): SQL[Nothing, NoExtractor] =
    sql"DELETE FROM ${table} WHERE UUID = ${uuid}"

}
