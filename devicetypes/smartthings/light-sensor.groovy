metadata {
	// Automatically generated. Make future change here.
	definition (name: "Light Sensor", namespace: "smartthings", author: "SmartThings") {
		capability "Illuminance Measurement"
		capability "Sensor"

		fingerprint profileId: "0104", deviceId: "0106", inClusters: "0000,0001,0003,0009,0400"
	}

	// simulator metadata
	simulator {
		status "dark": "illuminance: 8"
		status "light": "illuminance: 300"
		status "bright": "illuminance: 1000"
	}

	// UI tile definitions
	tiles {
		valueTile("illuminance", "device.illuminance", width: 2, height: 2) {
			state("illuminance", label:'${currentValue}', unit:"lux",
				backgroundColors:[
					[value: 9, color: "#767676"],
					[value: 315, color: "#ffa81e"],
					[value: 1000, color: "#fbd41b"]
				]
			)
		}

		main "illuminance"
		details "illuminance"
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	def result
	if (description?.startsWith("illuminance: ")) {
		def raw = description - "illuminance: "
		if (raw.isNumber()) {
			result = createEvent(
				name:  "illuminance",
				value: Math.round(zigbee.lux(raw as Integer)).toString(),
				unit:  "lux"
			)
		}
	}
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}
