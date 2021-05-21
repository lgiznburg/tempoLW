// For a zone (zoneElementId), periodically issues an AJAX request (eventURL) to update it.

define(["jquery", "t5/core/zone"], function($, zoneManager) {

    return function(zoneElementId, eventURL, frequencySecs) {

        var frequencyMillis = frequencySecs * 1000;
        var updatesCount = 0;

        var interval = setInterval(updateZone, frequencyMillis);

        function updateZone() {
                // Update the zone.
                zoneManager.deferredZoneUpdate(zoneElementId, eventURL);
        }
    }

});