import ResourceList from './ResourceList.js';

/**
 * ActivityList component
 *
 * @param {*} activities activities to display
 * @returns An "ul" element containing a list of "li" elements for each Activity
 */
export default function ActivityList(activities) {
    return ResourceList(
        activities,
        activity => `#sports/${activity.sport}/activities/${activity.id}`, // Link to details
        activity => `on ${activity.date}` // display supplier
    )
}

