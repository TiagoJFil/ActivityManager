package pt.isel.ls.db

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.api.getApiRoutes
import pt.isel.ls.api.getAppRoutes
import pt.isel.ls.api.utils.TEST_ENV
import pt.isel.ls.repository.database.utils.transaction
import kotlin.test.assertEquals

/*

class DbAccessTest{
    val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        ?: error("Please specify JDBC_DATABASE_URL environment variable")
    val dataSource = PGSimpleDataSource().apply {
        setURL(jdbcDatabaseURL)
    }
    private var testClient = getApiRoutes(getAppRoutes(TEST_ENV))

    @After
    fun tearDown() {
        testClient = getApiRoutes(getAppRoutes(TEST_ENV))
    }

    @BeforeClass
    fun createDummyTables(){

        val userTable = "user$suffix"
        val emailTable = "email$suffix"
        val tokenTable = "tokens$suffix"
        val routeTable = "route$suffix"
        val sportTable = "sport$suffix"
        val activityTable = "activity$suffix"
        dataSource.connection.transaction {
            val stmt = createStatement()

            stmt.executeUpdate("""
        create table "$userTable" (
            id serial primary key,
            name varchar(35) not null
        );
        create table "$emailTable"(
            "user" int,
            email varchar(100) constraint email_invalid check(email ~* '^[A-Z0-9._%-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}${'$'}') primary key,
        foreign key ("user") references "$userTable"(id)
        );

        create table "$tokenTable"(
            token char(36) primary key,
            "user" int not null,
            foreign key ("user") references "$userTable"(id)
        );

        create table "$routeTable" (
            id serial primary key,
            startLocation varchar(200) not null,
            endLocation varchar(200) not null,
            distance real not null check(distance > 0),
            "user" int not null,
            foreign key ("user") references "$userTable"(id)
        );

        create table "$sportTable"(
            id serial primary key,
            name varchar(50) not null,
            description varchar(200) DEFAULT null,
            "user" int not null,
            foreign key ("user") references "$userTable"(id)
        );

        create table "$activityTable" (
            id serial primary key,
            date date not null,
            duration bigint not null check ( duration  > 0),
            sport int not null,
            route int DEFAULT null,
            "user" int not null,
            foreign key ("user") references "$userTable"(id),
            foreign key (sport) references "$sportTable"(id),
            foreign key (route) references "$routeTable"(id)
        );""")



        }



    }



}
*/