/**
 *  Wattvision
 *
 *  Author: steve
 *  Date: 2014-02-13
 */
// for the UI
metadata {

	definition(name: "Wattvision", namespace: "smartthings", author: "Steve Vlaminck") {
		capability "Power Meter"
		capability "Refresh"
		attribute "powerContent", "string"
	}

	simulator {
		// define status and reply messages here
	}

	tiles {

		valueTile("power", "device.power") {
			state "power", label: '${currentValue} W'
		}

		tile(name: "powerChart", attribute: "powerContent", type: "HTML", url: '${currentValue}', width: 3, height: 2) { }

		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat") {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main "power"
		details(["powerChart", "power", "refresh"])

	}
}

def refresh() {
	parent.getDataFromWattvision()
	setGraphUrl(parent.getGraphUrl(device.deviceNetworkId))
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

public setGraphUrl(graphUrl) {

	log.trace "setting url for Wattvision graph"

	sendEvent([
		date           : new Date(),
		value          : graphUrl,
		name           : "powerContent",
		displayed      : false,
		isStateChange  : true,
		description    : "Graph updated",
		descriptionText: "Graph updated"
	])
}

public addWattvisionData(json) {

	log.trace "Adding data from Wattvision"

	def data = json.data
	def units = json.units ?: "watts"

	if (data) {
		def latestData = data[-1]
		data.each {
			sendPowerEvent(it.t, it.v, units, (latestData == it))
		}
	}

}

private sendPowerEvent(time, value, units, isLatest = false) {
	def wattvisionDateFormat = parent.wattvisionDateFormat()

	def eventData = [
		date           : new Date().parse(wattvisionDateFormat, time),
		value          : value,
		name           : "power",
		displayed      : isLatest,
		isStateChange  : isLatest,
		description    : "${value} ${units}",
		descriptionText: "${value} ${units}"
	]

	log.debug "sending event: ${eventData}"
	sendEvent(eventData)

}
