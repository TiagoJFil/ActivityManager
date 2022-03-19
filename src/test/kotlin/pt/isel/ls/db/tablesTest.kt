package pt.isel.ls.db

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.assertEquals

private val jdbcDatabaseURL: String = System.getenv("JDBC_DATABASE_URL")

class TableTest{

    private val dataSource = PGSimpleDataSource()
    private val studentTable = "studentstest"
    private val coursesTable = "coursestest"

    @Before
    fun fillMockTables(){
        dataSource.setURL(jdbcDatabaseURL)
        dataSource.connection.use {
            it.createStatement().use { statement ->
                statement.apply{
                    connection.autoCommit = false
                    executeUpdate(
                        """create table if not exists $coursesTable(cid serial primary key,name varchar(80));"""
                    )
                    executeUpdate(
                        "create table if not exists $studentTable (" +
                                "number int primary key," +
                                "name varchar(80), " +
                                "course int references courses(cid));"
                    )
                    executeUpdate("insert into $coursesTable(cid, name)values (2, 'TEST')")
                    executeUpdate("insert into $studentTable(course, number, name)values (2, 9999, 'Miguel')")
                    connection.commit()
                    connection.autoCommit = true
                }
            }
        }
    }

    @Test
    fun test_SELECT_operation(){
        dataSource.connection.use {
            it.createStatement().use { statement ->
                val rs = statement.executeQuery(
                    """select name from $studentTable where course = 2 AND number = 9999"""
                )
                rs.apply {
                    next()
                    val next = getString(1)
                    assertEquals(next, "Miguel" )
                }
            }
        }
    }

    @Test
    fun test_UPDATE_operation(){
        dataSource.connection.use {
           it.createStatement().use{ statement ->
               val updt = statement.executeUpdate(
                       "update $studentTable set name = 'Joao' where course = 2 and number = 9999"
               )
               assertEquals(1, updt)
           }
        }
    }


    @After
    fun deleteMockTables(){

        dataSource.connection.use {
            it.createStatement().use { statement ->
               statement.apply {
                   executeUpdate("drop table if exists $studentTable;")
                   executeUpdate("drop table if exists $coursesTable;")
               }
            }
        }

    }
}