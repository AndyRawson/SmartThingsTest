metadata {
	// Automatically generated. Make future change here.
	definition (name: "Arduino ThingShield", namespace: "smartthings", author: "SmartThings") {

		fingerprint profileId: "0104", deviceId: "0138", inClusters: "0000"
	}

	// Simulator metadata
	simulator {
		// status messages
		status "ping": "catchall: 0104 0000 01 01 0040 00 6A67 00 00 0000 0A 00 0A70696E67"
		status "hello": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A48656c6c6f20576f726c6421"
	}

	// UI tile definitions
	tiles {
		standardTile("shield", "device.shield", width: 2, height: 2) {
			state "default", icon:"st.shields.shields.arduino", backgroundColor:"#ffffff"
		}

		main "shield"
		details "shield"
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	def value = zigbee.parse(description)?.text
	def name = value && value != "ping" ? "response" : null
	def result = createEvent(name: name, value: value)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

// Commands to device
// TBD
