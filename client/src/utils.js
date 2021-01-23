import crypto from "crypto";

/**
 * Gets the hash of the provided password.
 * Throws an error if the password is undefined.
 *
 * @param password   The password to hash
 * @returns {string} The hash of the password, as a hex string
 */
function getPasswordHash(password) {
  if (password === undefined) {
    throw "Password is undefined, cannot generate hash.";
  }
  // Synchronously create a hash of a password (with blank salt), iterating 1000 times, for a length of 64 (8 bytes),
  // with digest sha512
  return crypto.pbkdf2Sync(password, "superSalty", 1000, 8, `sha512`).toString(`hex`).toUpperCase();
}

export {getPasswordHash};

/**
 * Returns a pretty string representation of a JavaScript Date object
 *
 * @param date: JavaScript Date object
 * @returns {string} Date formatted in "(Day) (Month name) (Year)"
 */
function toPrettyDate(date) {
  const day   = date.getDate();
  const year  = date.getFullYear();
  const month = date.toLocaleString('default', { month: 'long' });
  return `${month} ${day}, ${year}`;
}

export {toPrettyDate}

/**
 * Checks if a date text in DD/MM/YYYY format is before current date
 *
 * @param dateText: date string in DD/MM/YYYY format
 * @returns {Boolean} True if before, False otherwise
 */
function isBirthdateWithinCommonSense(dateText) {
    let inputDate = new Date(dateText);
    const today = new Date();
    const hundredYearsAgo = new Date().setFullYear(today.getFullYear() - 100);
    const tenYearsAgo = new Date().setFullYear(today.getFullYear() - 10);
    return inputDate >= hundredYearsAgo && inputDate <= tenYearsAgo;
}

export {isBirthdateWithinCommonSense};

/**
 * Alerts the user with a confirmation box with custom message
 *
 * @param input: Message to show to user
 * @returns {Boolean} True if OK is selected, False otherwise
 */
function confirmationAlertBox(input) {
  return confirm(input + ", are you sure?");
}

export {confirmationAlertBox};
