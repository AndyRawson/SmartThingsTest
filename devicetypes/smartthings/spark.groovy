metadata {
	// Automatically generated. Make future change here.
	definition (name: "Spark", namespace: "smartthings", author: "SmartThings") {
		capability "Switch"
	}


	// tile definitions
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}

		main "switch"
		details "switch"
	}
}

def parse(String description) {
	log.error "This device does not support incoming events"
	return null
}

def on() {
	put 'turnOn'
}

def off() {
	put 'turnOff'
}

private put(action) {
	// TODO: will this be configurable by user?
	def apiKey = "fb91rfPFS84wmzH3"

	rest(
			method: 'PUT',
			endpoint: "http://sprk.io",
			path: "/device/${device.deviceNetworkId}/${action}",
			query: [api_key: apiKey]
	)
}
