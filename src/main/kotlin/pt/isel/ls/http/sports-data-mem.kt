package pt.isel.ls

import kotlinx.serialization.Serializable

@Serializable
data class Email(val value: String){
    init {
       // require(Regex("").matches(value))
    }
}



@Serializable
data class Location(val value : String){

}

//data class Date()

@Serializable
data class User(val id: String,val name : String,val email : Email){

}

@Serializable
data class Sport(val id : String, val name: String, val description : String? = null, val user: User)

@Serializable
data class Route(val id : String, val StartLocation : Location, val EndLocation : Location, val distance : Int, val user : User)


@Serializable
data class Activity(val id : String, val date : String, val duration : Int, val sport : Sport, val route : Route? = null, val user : User)