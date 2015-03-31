metadata {
	// Automatically generated. Make future change here.
	definition (name: "Simulated Water Valve", namespace: "smartthings/testing", author: "SmartThings") {
		capability "Actuator"
		capability "Valve"
		capability "Sensor"
	}

	// tile definitions
	tiles {
		standardTile("contact", "device.contact", width: 2, height: 2, canChangeIcon: true) {
			state "closed", label: '${name}', action: "valve.open", icon: "st.valves.water.closed", backgroundColor: "#79b821"
			state "open", label: '${name}', action: "valve.close", icon: "st.valves.water.open", backgroundColor: "#53a7c0"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main "contact"
		details(["contact","refresh"])
	}
}

def installed() {
	sendEvent(name: "contact", value: "closed")
}

def open() {
	sendEvent(name: "contact", value: "open")
}

def close() {
	sendEvent(name: "contact", value: "closed")
}
