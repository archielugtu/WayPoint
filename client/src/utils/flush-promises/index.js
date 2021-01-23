/**
 * Waits for all the promises to resolve/reject
 *
 * Useful for tests
 */
var scheduler = typeof setImmediate === 'function' ? setImmediate : setTimeout;

function flushPromises() {
  return new Promise(function(resolve) {
    scheduler(resolve);
  });
}

module.exports = flushPromises;
