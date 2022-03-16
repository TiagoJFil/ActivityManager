import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.assertEquals


fun createMockStudentTable(){
    val dataSource = PGSimpleDataSource()
    dataSource.getConnection().use {
        val stm = it.createStatement().use {
            val rs = it.executeQuery("insert into students(course, number, name)values" +
                    "(9999, 9999, 'Miguel')")

        }

    }
}

class TableTest{

    @Test
    fun test_SELECT_operation(){
        val dataSource = PGSimpleDataSource()
        createMockStudentTable()
        dataSource.getConnection().use {
            val stm = it.createStatement().use {
                val rs = it.executeQuery("select name from students " +
                        "where course = 9999")
                rs.next()
                val next = rs.getString(1)
                assertEquals(next, "Miguel" )
            }

        }
    }

    fun read() {

    }
}