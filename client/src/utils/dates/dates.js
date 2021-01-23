/**
 * Defines functions related to formatting or parsing date obj
 */

/**
 * Get time offset between client's browser and GMT
 * @return String of timezone offset in format of (+|-)HHMM
 *          e.g. +1200
 */
function timezoneOffset() {
  return new Date()
    .toString()
    .split(" ")[5]
    .split("GMT")[1];
}

export default {
  // Formats Date object to YYYY-MM-DD
  dateFormatterToYYYYMMDD: function(date) {
    const day = ("0" + date.getDate()).slice(-2);
    const month = ("0" + (1 + date.getMonth())).slice(-2);
    return date.getFullYear() + "-" + month + "-" + day;
  },
  // Formats Date object to DD/MM/YYYY
  dateFormatter: function(date) {
    const day = ("0" + date.getDate()).slice(-2);
    const month = ("0" + (1 + date.getMonth())).slice(-2);
    return day + "/" + month + "/" + date.getFullYear();
  },
  // Formats Date object to pretty English
  dateFormatterToEnglish: function(date) {
    const options = {
      weekday: "long",
      day: "2-digit",
      month: "long",
      year: "numeric",
      hour12: true,
      hour: "2-digit",
      minute: "2-digit",
    };
    return date.toLocaleTimeString("en-US", options);
  },
  // Formats Date object to HH:MM
  dateFormatterToHourMinuteString: function(date) {
    const options = {
      hour12: false,
      hour: "2-digit",
      minute: "2-digit",
    };
    return date.toLocaleTimeString("en-US", options);
  },
  // Parses DD/MM/YYYY to Date object
  dateParser: function(date) {
    return new Date(
      date
        .split("/")
        .reverse()
        .join("/")
    );
  },
  /**
   * Generates date string in YYYY-MM-DD'T'HH:MM:SSZ format
   * e.g. 2020-02-20T08:00:00+1300
   */
  generateTimeStr: function(date) {
    let time = new Date(date);

    const timezone = timezoneOffset();
    const offset = Number(timezone.substring(0, 3));
    time.setHours(time.getHours() + offset);

    return time.toISOString().slice(0, -1) + timezone;
  },
};
