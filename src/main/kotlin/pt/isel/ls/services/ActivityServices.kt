package pt.isel.ls.services

import kotlinx.datetime.toLocalDate
import pt.isel.ls.entities.Activity
import pt.isel.ls.repository.ActivityRepository
import pt.isel.ls.utils.ActivityID
import pt.isel.ls.utils.UserID
import java.time.DateTimeException
import java.time.format.DateTimeFormatter

const val SID_REQUIRED = "SportsID is required."
const val SID_NOT_FOUND = "SportsID not found."
const val DURATION_REQUIRED = "Duration is required."
const val DURATION_NO_VALUE = "Duration field has no value"
const val DATE_REQUIRED = "Date is required."
const val DATE_INVALID = "Date is invalid."
const val DATE_NO_VALUE = "Date field has no value."
const val RID_NO_VALUE = "RID field has no value."


class ActivityServices(val repository: ActivityRepository){
    /**
     * Creates a new activity.
     * @param sportID The sports id of the activity.
     * @param duration The duration of the activity.
     * @param date The date of the activity.
     * @param rid The id of the route where the activity will take place.
     * @param userId The id of the user that created the activity.
     */
    fun createActivity(userId: UserID, sportID: String?, duration: String?, date: String?, rid: String?): ActivityID {
        try {
            requireNotNull(sportID) { SID_REQUIRED }
            requireNotNull(duration) { DURATION_REQUIRED }
            requireNotNull(date) { DATE_REQUIRED }
            require(duration.isNotBlank()) { DURATION_NO_VALUE }
            require(date.isNotBlank()) { DATE_NO_VALUE }
            if (rid != null) require(rid.isNotBlank()) { RID_NO_VALUE }
            val format = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

            val formatVerification = format.parse(duration)
            println(duration)

            val activityId = generateRandomId()
            val activity = Activity(activityId, date.toLocalDate(), duration, sportID, rid, userId)
            //TODO("SID_NOT_FOUND ERROR) and DATE_INVALID THROW

            repository.addActivity(activity)
            return activityId
        }catch (e: DateTimeException){
            throw IllegalArgumentException("Wrong duration format")
        }
    }

    fun getSportActivities(sportID: String?): List<Activity> {
        if(sportID == null) throw MissingParameter("sportID")
        if(sportID.isBlank()) throw InvalidParameter("sportID")
        return repository.getActivitiesBySport(sportID)
    }

}