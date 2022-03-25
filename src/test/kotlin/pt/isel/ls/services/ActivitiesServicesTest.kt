package pt.isel.ls.services

import org.junit.Test
import pt.isel.ls.repository.memory.ActivityDataMemRepository
import kotlin.test.assertFailsWith

class ActivitiesServicesTest {
    val activitiesServices = ActivityServices(ActivityDataMemRepository())


    @Test
    fun `try to create an activity with a blank duration`(){
        assertFailsWith<IllegalArgumentException> { activitiesServices.createActivity("123","123"," ","2002-12-31","123") }
    }
    @Test
    fun `try to create an activity with a blank date`(){
        assertFailsWith<IllegalArgumentException> { activitiesServices.createActivity("123","123","02:16:32.993"," ","123") }
    }
    @Test
    fun `try to create an activity with the wrong date format`(){
        assertFailsWith<IllegalArgumentException> { activitiesServices.createActivity("123","123","02:16:32.993","20-12-31","123") }
    }

    @Test
    fun `try to create an activity with the wrong duration format`(){
        assertFailsWith<IllegalArgumentException> { activitiesServices.createActivity("123","123","52:16:32.993","2002-12-31","123") }
    }

    @Test
    fun `try to create an activity with a blank rid doesnt work`(){
        assertFailsWith<IllegalArgumentException> { activitiesServices.createActivity("123","123","02:16:32.993","2002-12-31"," ") }
    }

}