
/**
 * Converts activity data from backend in snake_case to camelCase
 */


function activityResponseToCamelCase(activity) {
  return {
    activityId: activity.activity_id,
    creatorId: activity.creator_id,
    creatorName: activity.creator_name,
    description: activity.description,
    activityName: activity.activity_name,
    activityType: activity.activity_type,
    continuous: activity.continous,
    startTime: activity.start_time,
    endTime: activity.end_time,
    location: activity.location,
    hashtags: activity.hashtags,
    role: activity.role,
    visibility: activity.visibility,
  }
}

export default {
  activityResponseToCamelCase
};
